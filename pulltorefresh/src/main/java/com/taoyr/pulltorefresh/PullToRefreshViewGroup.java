package com.taoyr.pulltorefresh;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 得益于ViewCompat.canScrollVertically，不需要直到我们包裹的子View是AbsListView（expandable listview, listview），
 * 或者是Recycler mInnerView，并且根据不同的layout manager去得到first visible position, last visible position 等
 * 变量去判断是否滚动到了顶部/底部
 *
 * 暂时支持ListView，RecyclerView，ExpandableListView
 * FIXED_TODO 嵌套NestedScrollView+RecyclerView，会出现RV数据刷新后自动定位到RV，以及下拉刷新异常。
 * 上面问题已经解决，导致其产生问题的是，散布在工程中的自动定位代码（夜空中最亮的星），如下：
 * nested_scroll_view.fullScroll(View.FOCUS_UP);
 */

public class PullToRefreshViewGroup extends RelativeLayout {

    // 下拉头/上拉头回滚的速度
    /*恒定匀速版本*/
    public static final int SCROLL_BACK_SPEED = 30;
    /*加速度递减版本*/
    public static final int SCROLL_BACK_SPEED_RATIO = 3; // 越小回滚越快
    public static final int SCROLL_BACK_TOLERATED_MIN_DELTA = 1; // 回滚还原允许的最小距离误差值
    public static final int SCROLL_BACK_TOLERATED_MIN_SPEED = 2; // 回滚还原允许的最小速度

    // 下拉/上拉头隐藏的高度（赋值中，取控件的高度）
    //private static final int OFFSET_MARGIN = -PictureUtils.dip2px(60); // mHeader.getHeight()
    private static final int OFFSET_MARGIN = 60;
    private int mOffsetMargin = 0;

    // 定义下拉头破隐一击的Margin值（举例来说，设置为0表示下拉矩形的左上角，刚好从frame显示出来，即控件完全显示出来（NOT 部分显示））
    private static final int SHOW_MARGIN = 0;

    public static final int STATUS_PULL_TO_REFRESH = 0; // 下拉状态

    public static final int STATUS_RELEASE_TO_REFRESH = 1; // 释放立即刷新状态

    public static final int STATUS_REFRESHING = 2; // 正在刷新状态

    public static final int STATUS_REFRESH_FINISHED = 3; // 刷新完成或未刷新状态

    /**
     * 是否已加载过一次layout，这里onLayout中的初始化只需加载一次
     */
    private boolean mLoadOnce;

    private Context mContext;

    private Handler mHandler;

    // 被包含到Refresh控件中的recycler mInnerView
    private View mInnerView;

    // 在被判定为滚动之前用户手指可以移动的最大值
    private int mTouchSlop;

    // 记录手指按下时的屏幕纵坐标，用于滑动手势时计算纵向拖动距离
    private float mStartYpos;

    // 记录手指按下时的屏幕横坐标，用于滑动手势时计算横向滑动距离
    private float mStartXpos;

    // 记录手指滑动的时候move前的纵坐标，便于计算滑动中是向上还是向下
    // 该做法已被更优的方法替代，相关代码已注释掉
    @Deprecated
    private float mLastYPos;

    // 当前处理什么状态
    // Footer和Header使用同一个状态，因为不存Header头在刷新，Footer在加载更多数据的情况
    private int mCurrentStatus = STATUS_REFRESH_FINISHED;

    // 记录上一次的状态是什么，避免进行重复操作
    private int mLastStatus = mCurrentStatus;

    /*下拉刷新相关Specific*/

    public static final long ONE_SECOND = 1000; // 1s = 1000ms

    // 一分钟的毫秒值，用于判断上次的更新时间
    public static final long ONE_MINUTE = 60 * ONE_SECOND;

    // 一小时的毫秒值，用于判断上次的更新时间
    public static final long ONE_HOUR = 60 * ONE_MINUTE;

    // 一天的毫秒值，用于判断上次的更新时间
    public static final long ONE_DAY = 24 * ONE_HOUR;

    // 一月的毫秒值，用于判断上次的更新时间
    public static final long ONE_MONTH = 30 * ONE_DAY;

    // 一年的毫秒值，用于判断上次的更新时间
    public static final long ONE_YEAR = 12 * ONE_MONTH;

    // 上次更新时间的字符串常量，用于作为SharedPreferences的键值
    private static final String LAST_UPDATE_TIME = "last_update_time";

    // 上次更新时间的毫秒值
    private long mLastUpdateTimeInMs;

    /**
     * 为了防止不同界面的下拉刷新在上次更新时间上互相有冲突，使用id来做区分
     */
    private int mId = -1;

    // 回调接口
    private PullToRefreshListener mListener;

    // 用于存储上次更新时间
    private SharedPreferences mPreferences;

    // 下拉头的View（下拉刷新）
    private View mHeader;

    // 刷新时显示的进度条
    private ProgressBar mProgressBar;

    // 指示下拉和释放的箭头
    private ImageView mArrow;

    // 指示下拉和释放的文字描述
    private TextView mDescription;

    // 上次更新时间的文字描述
    private TextView mLastUpdateTime;

    // 下拉头的布局参数
    private LayoutParams mLayoutParam;

    /*上拉刷新相关，基本上克隆了下拉的逻辑*/
    // 上拉头的View（上拉加载更多）
    private View mFooter;
    private ProgressBar mProgressBar2;
    private ImageView mArrow2;
    private TextView mDescription2;
    private LayoutParams mLayoutParam2;
    private LayoutParams mLayoutParam3;

    // 默认启用上拉刷新
    private boolean mIsHeaderEnabled = true;
    // 默认禁用上拉刷新
    private boolean mIsFooterEnabled = false;
    private boolean mIsFooterOperated = false;
    private boolean mAlwaysEnablePullUp = false;

    public static final int THEME_BLUE_TONE = 1;
    public static final int THEME_WHITE_TONE = 2;
    private int mTheme = THEME_BLUE_TONE;

    public void setTheme(int theme) {
        if (mTheme != theme) {
            mTheme = theme;

            switch (theme) {
                case THEME_BLUE_TONE:
                    if (mHeader != null) {
                        mArrow.setImageResource(R.drawable.pull);
                        mDescription.setTextColor(mContext.getResources().getColor(R.color.black));
                        mLastUpdateTime.setTextColor(mContext.getResources().getColor(R.color.black));
                    }
                    if (mFooter != null) {
                        mArrow2.setImageResource(R.drawable.pull);
                        mDescription2.setTextColor(mContext.getResources().getColor(R.color.black));
                    }
                    break;
                case THEME_WHITE_TONE:
                    if (mHeader != null) {
                        mArrow.setImageResource(R.drawable.arrow_plane_white);
                        mDescription.setTextColor(mContext.getResources().getColor(R.color.white));
                        mLastUpdateTime.setTextColor(mContext.getResources().getColor(R.color.white));
                    }
                    if (mFooter != null) {
                        mArrow2.setImageResource(R.drawable.arrow_plane_white);
                        mDescription2.setTextColor(mContext.getResources().getColor(R.color.white));
                    }
                    break;
            }
        }
    }

    public void setHeaderEnable(boolean enable) {
        mIsHeaderEnabled = enable;
    }

    public void setFooterEnable(boolean enable) {
        mIsFooterEnabled = enable;
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public PullToRefreshViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        LogMan.logDebug("new PullToRefreshViewGroup()");
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mHandler = new Handler();
        mContext = context;
        mOffsetMargin = -dip2px(mContext, OFFSET_MARGIN);
    }

    // 放动态增加header和footer的逻辑最合理的位置，因为放构造函数中太早，XML中的listview还没有被加到
    // View tree中，addView(mFooter, 2)会报IndexOutOfBounds。而放onLayout又太晚，因为onLayout是对
    // 已存在的控件进行测绘和布局（在fw中，View控件通过WindowManagerService，进行测量，并布局于设备
    // 屏幕上，onLayout作为更新layout的行为被调用（包括窗口初始化onCreate，窗口刷新onResume）），
    // 前提是所有控件已经就绪。
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LogMan.logDebug("onFinishInflate");
        mHeader = LayoutInflater.from(mContext).inflate(R.layout.pull_to_refresh_header, null, true);
        mProgressBar = (ProgressBar) mHeader.findViewById(R.id.progress_bar);
        mArrow = (ImageView) mHeader.findViewById(R.id.arrow);
        mDescription = (TextView) mHeader.findViewById(R.id.description);
        mLastUpdateTime = (TextView) mHeader.findViewById(R.id.updated_at);
        addView(mHeader, 0);

        mFooter = LayoutInflater.from(mContext).inflate(R.layout.pull_to_refresh_footer, null, true);
        mProgressBar2 = (ProgressBar) mFooter.findViewById(R.id.progress_bar);
        mArrow2 = (ImageView) mFooter.findViewById(R.id.arrow);
        mDescription2 = (TextView) mFooter.findViewById(R.id.description);

        // RelativeLayout，虽然不同雨LinearLayout，插入的位置对布局不会有影响，但同样的，子View
        // 在父容器的先后位置概念还是有的，这样通过getChildAt(1)保证取到的是ListView，而不是
        // Footer/Header
        addView(mFooter, 2);
    }

    /**
     * 构造出刷新页面的整体布局框架(header-content-footer)，将下拉头向上偏移，进行隐藏，
     * 并给ListView注册touch事件。
     *
     * 对布局的修改和初始化最好放在这里。因为onLayout中不仅可以直接调用getHeight等方法获取控件的
     * 数据，而且此之前，调用mHeader.getLayoutParams(), getChildAt(1)将返回null
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        LogMan.logDebug("onLayout");
        if (changed && !mLoadOnce) {
            // Header
            mLayoutParam = (LayoutParams) mHeader.getLayoutParams();
            // 调试了一早上布局有问题，原因在于写错了对象名
            // mLayoutParam = (RelativeLayout.LayoutParams) mInnerView.getLayoutParams();
            mLayoutParam.topMargin = mOffsetMargin;
            mLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mHeader.setLayoutParams(mLayoutParam);
            // refreshLastUpdateTimeValue();

            // Footer
            mLayoutParam2 = (LayoutParams) mFooter.getLayoutParams();
            // 这里如果仿照上拉头的做法，设定topMargin，正数会把底部空白区域顶开，负数会位移到
            // listview上面，重叠显示，因此不可行
            mLayoutParam2.bottomMargin = mOffsetMargin;
            mLayoutParam2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mFooter.setLayoutParams(mLayoutParam2);

            // 初始化ListView（content body）
            // 在onFinishInflate，getChildAt(1)返回空
            mInnerView = getChildAt(1);
            // 这里需要设定到mRecyclerView上，如果设定到外面父容器上，会因为焦点抢占问题导致外部
            // 事件无法响应。
            mLayoutParam3 = (LayoutParams) mInnerView.getLayoutParams();
            //mInnerView.setOnTouchListener(this);
            LayoutParams lp = (LayoutParams) mInnerView.getLayoutParams();
            //lp.width = LayoutParams.MATCH_PARENT;
            //lp.height = LayoutParams.MATCH_PARENT;
            lp.addRule(RelativeLayout.BELOW, mHeader.getId());
            lp.addRule(RelativeLayout.ABOVE, mFooter.getId());
            mInnerView.setLayoutParams(lp);
            mLoadOnce = true;
        }
    }

    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mInnerView instanceof ViewPager) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }*/

    /*boolean mIsVpDragger;
    float startX, startY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 记录手指按下的位置
                startY = ev.getY();
                startX = ev.getX();
                // 初始化标记
                mIsVpDragger = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                if(mIsVpDragger) {
                    return false;
                }

                // 获取当前手指位置
                float endY = ev.getY();
                float endX = ev.getX();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);
                // 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。
                if(distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsVpDragger = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 初始化标记
                mIsVpDragger = false;
                break;
        }
        // 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
        return super.onInterceptTouchEvent(ev);
    }*/

    boolean mIsVerticalDrag;
    float startX, startY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 记录手指按下的位置
                startY = ev.getY();
                startX = ev.getX();
                // 初始化标记
                mIsVerticalDrag = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 获取当前手指位置
                float endY = ev.getY();
                float endX = ev.getX();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);
                float distance = endY - startY;
                boolean isOperateOuter = (distance > 0 && isAbleToPullDown()) || (distance < 0 && isAbleToPullUp());

                if(distanceY > mTouchSlop && distanceY > distanceX && isOperateOuter) {
                    mIsVerticalDrag = true;
                }
                break;
            default:
                mIsVerticalDrag = false;
                break;
        }
        if (mIsVerticalDrag) {
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // AS调试牛逼了，代码航瞎JB跳（错行），不知道是不是gradle编译把debug的字节码弄乱。
        // 解决办法：用log调试了，wtf咒语可以解决一些灵异问题
        LogMan.logDebug("wtf 0");
        // 正在刷新过程中，不能进行上拉和下拉操作
        if (mCurrentStatus == STATUS_REFRESHING) return false;

        int distance = 0;
        // boolean dragDown = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 手指按下屏幕
                LogMan.logDebug("wtf 1");
                mStartYpos = event.getRawY();
                mStartXpos = event.getRawX();
                //mLastYPos = mStartYpos;
                break;
            case MotionEvent.ACTION_MOVE: // 手指在屏幕滑动
                LogMan.logDebug("wtf 2");
                float yMove = event.getRawY();
                float xMove = event.getRawX();
                // Workaround: 奇怪的事件，recycler view在第一次点击触摸的时候，无法收到ACTION_DOWN，
                // 只能收到ACTION_MOVE。
                if (mStartYpos <= 0) {
                    mStartYpos = yMove;
                }
                if (mStartXpos <= 0) {
                    mStartXpos = xMove;
                }
                /*if (yMove - mLastYPos > 0) {
                    dragDown = true;
                } else {
                    dragDown = false;
                }
                mLastYPos = yMove;*/
                distance = (int) (yMove - mStartYpos);
                int distanceAbs = Math.abs(distance);
                LogMan.logDebug("distance: " + distance);

                if (distanceAbs < mTouchSlop) { // 还没拉到位（拖动距离不够），需要继续下拉
                    return false;
                }

                int distanceAbsX = (int) Math.abs(xMove - mStartXpos);

                if (distanceAbsX >= distanceAbs) { // 如果横向滑动距离比纵向的还大，就不拖拽（解决横向list滑动与上下拖拽冲突，滑动不顺畅的问题）
                    return false;
                }

                if (distance > 0 && isAbleToPullDown()) { // 下拉逻辑
                    LogMan.logDebug("wtf 2.1");
                    mIsFooterOperated = false;
                    // 将刷新失败的图标还原，重新刷新
                    if (mTheme == THEME_WHITE_TONE) {
                        mArrow.setImageResource(R.drawable.arrow_plane_white);
                    } else if (mTheme == THEME_BLUE_TONE) {
                        mArrow.setImageResource(R.drawable.pull);
                    }

                    if (mCurrentStatus != STATUS_REFRESHING) { // 如果是正在刷新状态，则对下拉动作不作响应（屏蔽下拉事件）
                        if (mLayoutParam.topMargin >= SHOW_MARGIN) {
                            mCurrentStatus = STATUS_RELEASE_TO_REFRESH;
                        } else { // 上拉头还没有完全拖动出来（矩形块左上角仍隐藏）
                            mCurrentStatus = STATUS_PULL_TO_REFRESH;
                        }

                        // 将header显示出来
                        mLayoutParam.topMargin = (distanceAbs / 2) + mOffsetMargin;
                        mHeader.setLayoutParams(mLayoutParam);

                        // 压缩下部分
                        mLayoutParam2.bottomMargin = mOffsetMargin - (distanceAbs / 2);
                        mFooter.setLayoutParams(mLayoutParam2);
                    }
                } else if (distance < 0 && isAbleToPullUp()) { // 上滑逻辑
                    LogMan.logDebug("wtf 2.2");
                    mIsFooterOperated = true;
                    if (mCurrentStatus != STATUS_REFRESHING) {
                        if (mLayoutParam3.topMargin < mOffsetMargin) { // 全部从底部拉出来了
                            LogMan.logDebug("wtf 2.2.1");
                            mCurrentStatus = STATUS_RELEASE_TO_REFRESH;
                        } else {
                            LogMan.logDebug("wtf 2.2.2");
                            mCurrentStatus = STATUS_PULL_TO_REFRESH;
                        }

                        // 将footer显示出来
                        mLayoutParam2.bottomMargin = (distanceAbs / 2) + mOffsetMargin;
                        mFooter.setLayoutParams(mLayoutParam2);

                        // 压缩上部分
                        /*mLayoutParam.topMargin = mOffsetMargin - (distanceAbs / 2);
                        mHeader.setLayoutParams(mLayoutParam);*/
                        // Nexus5，使用上面的实现方式，将无法进行上拉
                        mLayoutParam3.topMargin = - (distanceAbs / 2);
                        mInnerView.setLayoutParams(mLayoutParam3);
                    }
                }
                break;
            case MotionEvent.ACTION_UP: // 手指从屏幕抬起
                // 不只是第一次触摸，无法接收到ACTION DOWN。
                mStartYpos = 0;
            default:
                LogMan.logDebug("wtf 3");
                if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
                    // 松手时如果是释放立即刷新状态，就去调用正在刷新的任务
                    LogMan.logDebug("wtf 4");
                    new RefreshingTask().execute();
                } else if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {
                    // 松手时如果是下拉状态，就去调用隐藏下拉头的任务
                    LogMan.logDebug("wtf 5");
                    new HideHeaderTask().execute();
                }
                break;
        }
        LogMan.logDebug("wtf 6");
        if (mCurrentStatus == STATUS_PULL_TO_REFRESH
                || mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            // 上拉下拉，或者释放过程中，随时更新下拉头中的信息
            updateHeaderView();
            // 当前正处于下拉或释放状态，要让ListView失去焦点，否则被点击的那一项会一直处于选中状态
            mInnerView.setPressed(false);
            mInnerView.setFocusable(false);
            mInnerView.setFocusableInTouchMode(false);
            mLastStatus = mCurrentStatus;
            LogMan.logDebug("wtf 7");
            // 这里有个小细节需要说明，比如下拉刷新，当我们开始拖拽后，手指不离开屏幕，慢慢的滑动还原
            // ，即便header被隐藏了，listview也不能滑动。必须松开手指后才行。
            return true; // ①
        }
        LogMan.logDebug("wtf 8");
        return true;
        /*if (mInnerView instanceof RecyclerView || mInnerView instanceof ListView) {
            return false; // ②
            // ① ②的处理至关重要，保证listview在上拉或者下拉状态下吃掉事件，使得listview的滚动不影响到上拉或者下拉
        } else { // 子空间为非滚动页面，比如一个普通的RelativeLayout
            return true;
            // 必须为true，不然只能收到DOWN，后续的MOVE等事件无法收到(如果不再父控件吃掉consume事件(return true)，
            // 后续事件将由子控件处理。可以看到父容器的onTouch中可以实现onInterceptEvent的一些效果）。
        }*/
    }

    /**
     * 要解决的问题很清除：下拉刷新控件中嵌套ViewPager，因为ViewPager吃掉了事件，导致父容器（下拉刷新控件）无法通过手势判断进行下拉。
     *
     * 解决思路：要么在父容器拦截事件，并将事件交给父容器处理。要么在子控件返回false，不要消耗掉事件，或者在子控件请求父容器的拦截机制。
     *
     * 从parent解决，onIntercept返回true将纵向滑动截获，但是onTouch之后无法收到后续的MOVE事件。
     * 失败原因：mInnerView.setOnTouchListener，之前的实现方式是把touch listener直接设置到innerView上，然而innerview是嵌套了两层的
     * linear layout，viewpager嵌套在最里面。一旦父容器拦截事件后，后续的事件将由父容器处理，而不会进入到innerView上。之前touch listener
     * 中处理下拉和listview滑动的逻辑也比较tricky和迷惑人，实际上touch listener中判断如果是listview返回false，是为了将event继续的交给listview的onTouchEvent
     * 处理，从而不妨碍listiview的滑动。而如果是普通的linear layout返回true，是为了让后续的MOVE事件继续由touch listener的逻辑处理。
     *
     * 从child解决
     *      复写CustomViewPager的dispatchTouchEvent或onTouchEvent，直接返回false，可以让VP交出事件处理权到父容器，
     *    但child将无法进行任何交互，包括按钮的点击事件。
     *      复写CustomViewPager的dispatchTouchEvent或onTouchEvent，使用requestDisallowInterceptTouchEvent，如果判断
     *    是纵向滑动，则使用requestDisallowInterceptTouchEvent(false)让父容器拦截事件并在父容器处理
     *失败原因：同样是忽略了子View也就是view pager可能进行了多层嵌套，而requestDisallowInterceptTouchEvent(false)没法直接作用到下拉控件上。
     */

    /*@Override
    public boolean onTouch(View v, MotionEvent event) {
        // AS调试牛逼了，代码航瞎JB跳（错行），不知道是不是gradle编译把debug的字节码弄乱。
        // 解决办法：用log调试了，wtf咒语可以解决一些灵异问题
        LogMan.logDebug("wtf 0");
        // 正在刷新过程中，不能进行上拉和下拉操作
        if (mCurrentStatus == STATUS_REFRESHING) return false;

        int distance = 0;
        // boolean dragDown = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 手指按下屏幕
                LogMan.logDebug("wtf 1");
                mStartYpos = event.getRawY();
                mLastYPos = mStartYpos;
                break;
            case MotionEvent.ACTION_MOVE: // 手指在屏幕滑动
                LogMan.logDebug("wtf 2");
                float yMove = event.getRawY();
                // Workaround: 奇怪的事件，recycler view在第一次点击触摸的时候，无法收到ACTION_DOWN，
                // 只能收到ACTION_MOVE。
                if (mStartYpos <= 0) {
                    mStartYpos = yMove;
                }
                *//*if (yMove - mLastYPos > 0) {
                    dragDown = true;
                } else {
                    dragDown = false;
                }
                mLastYPos = yMove;*//*
                distance = (int) (yMove - mStartYpos);
                int distanceAbs = Math.abs(distance);
                LogMan.logDebug("distance: " + distance);

                if (distanceAbs < mTouchSlop) { // 还没拉到位（拖动距离不够），需要继续下拉
                    return false;
                }

                if (distance > 0 && isAbleToPullDown()) { // 下拉逻辑
                    LogMan.logDebug("wtf 2.1");
                    mIsFooterOperated = false;
                    // 将刷新失败的图标还原，重新刷新
                    if (mTheme == THEME_WHITE_TONE) {
                        mArrow.setImageResource(R.drawable.arrow_plane_white);
                    } else if (mTheme == THEME_BLUE_TONE) {
                        mArrow.setImageResource(R.drawable.arrow_plane);
                    }

                    if (mCurrentStatus != STATUS_REFRESHING) { // 如果是正在刷新状态，则对下拉动作不作响应（屏蔽下拉事件）
                        if (mLayoutParam.topMargin >= SHOW_MARGIN) {
                            mCurrentStatus = STATUS_RELEASE_TO_REFRESH;
                        } else { // 上拉头还没有完全拖动出来（矩形块左上角仍隐藏）
                            mCurrentStatus = STATUS_PULL_TO_REFRESH;
                        }

                        // 将header显示出来
                        mLayoutParam.topMargin = (distanceAbs / 2) + OFFSET_MARGIN;
                        mHeader.setLayoutParams(mLayoutParam);

                        // 压缩下部分
                        mLayoutParam2.bottomMargin = OFFSET_MARGIN - (distanceAbs / 2);
                        mFooter.setLayoutParams(mLayoutParam2);
                    }
                } else if (distance < 0 && isAbleToPullUp()) { // 上滑逻辑
                    LogMan.logDebug("wtf 2.2");
                    mIsFooterOperated = true;
                    if (mCurrentStatus != STATUS_REFRESHING) {
                        if (mLayoutParam3.topMargin < OFFSET_MARGIN) { // 全部从底部拉出来了
                            LogMan.logDebug("wtf 2.2.1");
                            mCurrentStatus = STATUS_RELEASE_TO_REFRESH;
                        } else {
                            LogMan.logDebug("wtf 2.2.2");
                            mCurrentStatus = STATUS_PULL_TO_REFRESH;
                        }

                        // 将footer显示出来
                        mLayoutParam2.bottomMargin = (distanceAbs / 2) + OFFSET_MARGIN;
                        mFooter.setLayoutParams(mLayoutParam2);

                        // 压缩上部分
                        *//*mLayoutParam.topMargin = OFFSET_MARGIN - (distanceAbs / 2);
                        mHeader.setLayoutParams(mLayoutParam);*//*
                        // Nexus5，使用上面的实现方式，将无法进行上拉
                        mLayoutParam3.topMargin = - (distanceAbs / 2);
                        mInnerView.setLayoutParams(mLayoutParam3);
                    }
                }
                break;
            case MotionEvent.ACTION_UP: // 手指从屏幕抬起
                // 不只是第一次触摸，无法接收到ACTION DOWN。
                mStartYpos = 0;
            default:
                LogMan.logDebug("wtf 3");
                if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
                    // 松手时如果是释放立即刷新状态，就去调用正在刷新的任务
                    LogMan.logDebug("wtf 4");
                    new RefreshingTask().execute();
                } else if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {
                    // 松手时如果是下拉状态，就去调用隐藏下拉头的任务
                    LogMan.logDebug("wtf 5");
                    new HideHeaderTask().execute();
                }
                break;
        }
        LogMan.logDebug("wtf 6");
        if (mCurrentStatus == STATUS_PULL_TO_REFRESH
                || mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            // 上拉下拉，或者释放过程中，随时更新下拉头中的信息
            updateHeaderView();
            // 当前正处于下拉或释放状态，要让ListView失去焦点，否则被点击的那一项会一直处于选中状态
            mInnerView.setPressed(false);
            mInnerView.setFocusable(false);
            mInnerView.setFocusableInTouchMode(false);
            mLastStatus = mCurrentStatus;
            LogMan.logDebug("wtf 7");
            // 这里有个小细节需要说明，比如下拉刷新，当我们开始拖拽后，手指不离开屏幕，慢慢的滑动还原
            // ，即便header被隐藏了，listview也不能滑动。必须松开手指后才行。
            return true; // ①
        }
        LogMan.logDebug("wtf 8");
        if (mInnerView instanceof RecyclerView || mInnerView instanceof ListView) {
            return false; // ②
            // ① ②的处理至关重要，保证listview在上拉或者下拉状态下吃掉事件，使得listview的滚动不影响到上拉或者下拉
        } else { // 子空间为非滚动页面，比如一个普通的RelativeLayout
            return true;
            // 必须为true，不然只能收到DOWN，后续的MOVE等事件无法收到(如果不再父控件吃掉consume事件(return true)，
            // 后续事件将由子控件处理。可以看到父容器的onTouch中可以实现onInterceptEvent的一些效果）。
        }
        //return super.onTouchEvent(event);
    }*/

    /**
     * 给下拉刷新控件注册一个监听器。
     */
    public void setOnRefreshListener(PullToRefreshListener listener, int id) {
        mListener = listener;
        mId = id;
    }

    /**
     * 当所有的刷新逻辑完成后调用，否则你的ListView将一直处于正在刷新状态，并无法进行二次刷新。
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void finishRefreshing(boolean isSuccess) {
        if (isSuccess) {
            if (mCurrentStatus != STATUS_REFRESH_FINISHED) {
                mCurrentStatus = STATUS_REFRESH_FINISHED;
                mPreferences.edit().putLong(LAST_UPDATE_TIME + mId,
                        System.currentTimeMillis()).commit();
                new HideHeaderTask().execute();
            }
        } else {
            mCurrentStatus = STATUS_REFRESH_FINISHED;
            // 如果刷新出错，不自动回滚下拉刷新头，仅提示刷新错误
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                    mArrow.setVisibility(View.VISIBLE);
                    if (mTheme == THEME_WHITE_TONE) {
                        mArrow.setImageResource(R.drawable.arrow_error_white);
                    } else if (mTheme == THEME_BLUE_TONE) {
                        mArrow.setImageResource(R.drawable.arrow_error);
                    }
                    mDescription.setText("刷新失败");
                }
            });
        }
    }

    public void finishLoading() {
        //mCurrentStatus = STATUS_REFRESH_FINISHED;
        //new HideHeaderTask().execute();

        // 因为散步在各处的refreshList方法，不会判断load more标记，一次性调用finishLoading和finishRefresh
        // 两个方法，导致一个问题。如果在产品板块下，不停的切换tab类目，会发现底部的footer会慢慢的往上移动。
        // 因此在这里统一修改，当没有进行下拉或者上拉出发的刷新，不去调整上拉/下拉头
        if (mCurrentStatus != STATUS_REFRESH_FINISHED) {
            mCurrentStatus = STATUS_REFRESH_FINISHED;
            new HideHeaderTask().execute();
        }
    }

    /**
     * 根据当前ListView的滚动状态来设定，来判断当前是否可以下拉，只有ListView滚动到头的时候才允许
     * 下拉。
     */
    private boolean isAbleToPullDown() {
        if (!mIsHeaderEnabled) return false;
        /*View firstChild = mInnerView.getChildAt(0);
        if (firstChild != null) {
            int firstVisiblePos = mLayoutManager.findFirstVisibleItemPosition();
            // 如果首个元素的上边缘，距离父布局值为0，就说明ListView滚动到了最顶部，此时应该允许下拉刷新
            if (firstVisiblePos == 0 && firstChild.getTop() == 0) {
                // 注意这里如果给listview设定了paddingTop，滑动到顶部getTop()不为0。另外如果对
                // listview或者其父容器进行scale，滑动到顶部后，虽然显示不全，但其相对位置并没改变。
                // getTop任然为0
                return true;
            } else {
                return false;
            }
        } else {
            // 如果ListView中没有元素，也应该允许下拉刷新
            return true;
        }*/
        return !ViewCompat.canScrollVertically(mInnerView, -1);
        //return !mInnerView.canScrollVertically(-1);
    }

    /**
     * @param alwaysEnablePullUp 为true时支持listview内容未占满屏幕时上拉。
     */
    public void setAlwaysEnablePullUp(boolean alwaysEnablePullUp) {
        mAlwaysEnablePullUp = alwaysEnablePullUp;
    }

    /**
     * 根据当前ListView的滚动状态来设定，来判断当前是否可以上拉，只有ListView滚动到最底部时才允许
     * 上拉。
     */
    private boolean isAbleToPullUp() {
        if (!mIsFooterEnabled) return false;

        //return !mInnerView.canScrollVertically(1);
        return !ViewCompat.canScrollVertically(mInnerView, 1);
        /*int lastVisiblePosition = mLayoutManager.findLastVisibleItemPosition();
        int firstVisiblePosition = mLayoutManager.findFirstVisibleItemPosition();
        int relativePositioon = lastVisiblePosition - firstVisiblePosition;
        int totalCount = mLayoutManager.getItemCount();
        int childCount = mLayoutManager.getChildCount();
        LogMan.logDebug("mInnerView lastVisiblePosition: " + lastVisiblePosition);
        LogMan.logDebug("mInnerView totalCount: " + totalCount);
        LogMan.logDebug("mInnerView childCount: " + childCount);

        // 已经完整显示最后一项数据（比如内容不满一屏），不需要listview的滑动显示更多数据
        if (lastVisiblePosition == totalCount - 1) {
            View lastVisibleItemView = mInnerView.getChildAt(relativePositioon);
            if (lastVisibleItemView != null && lastVisibleItemView.getBottom() <= mInnerView.getHeight()) {
                if (mAlwaysEnablePullUp) return true;
            }
        }

        // 滚动到最底部，且不满一屏
        if (lastVisiblePosition == totalCount - 1 && totalCount > childCount) {
            View lastVisibleItemView = mInnerView.getChildAt(relativePositioon);
            LogMan.logDebug("lastVisibleItemView.getBottom()" + lastVisibleItemView.getBottom());
            LogMan.logDebug("mInnerView.getHeight()" + mInnerView.getHeight());
            // 因为加了footer的缘故，当footer的padding从负数增加到0，listview的位置随之被往上挤压，
            // 高度变小，而lastVisibleItemView.getBottom()不变（当前做法不存在这个问题，因为使用了
            // 手抄本模式，listview会自动置底）
            // 这个判断是保证listview的最后一个item已经滚动到了最底部，而不是犹抱琵琶半遮面。拖拽
            // 到半截就把上拉加载的footer显示出来
            if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == mInnerView.getHeight()) {
                return true;
            }
        }

        return false;*/
    }

    /**
     * 更新下拉头中的信息。
     */
    private void updateHeaderView() {
        if (mLastStatus != mCurrentStatus) {
            if (!mIsFooterOperated) {
                if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {
                    mDescription.setText(getResources().getString(R.string.common_pull_to_refresh));
                    mArrow.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    rotateArrow(mArrow);
                } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
                    mDescription.setText(getResources().getString(R.string.common_release_to_refresh));
                    mArrow.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    rotateArrow(mArrow);
                } else if (mCurrentStatus == STATUS_REFRESHING) {
                    mDescription.setText(getResources().getString(R.string.common_now_refreshing));
                    mProgressBar.setVisibility(View.VISIBLE);
                    mArrow.clearAnimation();
                    mArrow.setVisibility(View.GONE);
                }
                refreshLastUpdateTimeValue();
            } else {
                if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {
                    mDescription2.setText(getResources().getString(R.string.common_pull_up_to_load_more));
                    mArrow2.setVisibility(View.VISIBLE);
                    mProgressBar2.setVisibility(View.GONE);
                    //rotateArrow(mArrow2);
                } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
                    mDescription2.setText(getResources().getString(R.string.common_release_to_load_more));
                    mArrow2.setVisibility(View.VISIBLE);
                    mProgressBar2.setVisibility(View.GONE);
                    //rotateArrow(mArrow2);
                } else if (mCurrentStatus == STATUS_REFRESHING) {
                    mDescription2.setText(getResources().getString(R.string.common_now_loading_more));
                    mProgressBar2.setVisibility(View.VISIBLE);
                    mArrow2.clearAnimation();
                    mArrow2.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 根据当前的状态来旋转箭头。
     */
    private void rotateArrow(ImageView arrow) {
        // 这种办法，在Nexus5手机上，下拉刷新，按钮会旋转后又转回去。适配问题让人脑壳疼
        /*float pivotX = arrow.getWidth() / 2f;
        float pivotY = arrow.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;

        if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {
            // 下拉头被倒置后为180度，因此转到360度，还原
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            // 释放刷新的时候，将下拉头旋转180度，倒过来
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        arrow.startAnimation(animation);*/

        // 这种做法没有旋转的动画效果了，但是解决了设备兼容性问题。
        if (mCurrentStatus == STATUS_PULL_TO_REFRESH) {
            arrow.setRotation(mIsFooterOperated ? 0 : 180);
        } else if (mCurrentStatus == STATUS_RELEASE_TO_REFRESH) {
            arrow.setRotation(mIsFooterOperated ? 180 : 0);
        }
    }

    /**
     * 刷新下拉头中上次更新时间的文字描述。
     */
    private void refreshLastUpdateTimeValue() {
        mLastUpdateTimeInMs = mPreferences.getLong(LAST_UPDATE_TIME + mId, -1);
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - mLastUpdateTimeInMs;
        long timeIntoFormat;
        String updateAtValue;
        if (mLastUpdateTimeInMs == -1 || timePassed < 0) {
            updateAtValue = "";
        } else if (timePassed < ONE_MINUTE) {
            timeIntoFormat = timePassed / ONE_SECOND;
            updateAtValue = String.format(
                    getResources().getString(R.string.common_updated_seconds_ago),
                    String.valueOf(timeIntoFormat));
        } else if (timePassed < ONE_HOUR) {
            timeIntoFormat = timePassed / ONE_MINUTE;
            updateAtValue = String.format(
                    getResources().getString(R.string.common_updated_minutes_ago),
                    String.valueOf(timeIntoFormat));
        } else if (timePassed < ONE_DAY) {
            timeIntoFormat = timePassed / ONE_HOUR;
            updateAtValue = String.format(
                    getResources().getString(R.string.common_updated_hours_ago),
                    String.valueOf(timeIntoFormat));
        } else if (timePassed < ONE_MONTH) {
            timeIntoFormat = timePassed / ONE_DAY;
            updateAtValue = String.format(
                    getResources().getString(R.string.common_updated_day_ago),
                    String.valueOf(timeIntoFormat));
        } else if (timePassed < ONE_YEAR) {
            timeIntoFormat = timePassed / ONE_MONTH;
            updateAtValue = String.format(
                    getResources().getString(R.string.common_updated_month_ago),
                    String.valueOf(timeIntoFormat));
        } else {
            timeIntoFormat = timePassed / ONE_YEAR;
            updateAtValue = String.format(
                    getResources().getString(R.string.common_updated_year_ago),
                    String.valueOf(timeIntoFormat));
        }
        mLastUpdateTime.setText(updateAtValue);
    }

    /**
     * 正在刷新的任务，在此任务中会去回调注册进来的下拉刷新监听器。
     */
    class RefreshingTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (mLayoutParam == null) return null;

            //int marginHeader = mLayoutParam.topMargin;
            int marginHeader = mIsFooterOperated ? mLayoutParam3.topMargin : mLayoutParam.topMargin;
            int marginFooter = mLayoutParam2.bottomMargin;

            // 将下拉头多余的距离还原到破隐一击的位置
            // 因为统一了上拉头和下拉头的逻辑，相比于前一个LinearLayout的版本，简化合并了部分代码

            while (true) {
                if (mIsFooterOperated) {
                    // 跟恒定的速度SCROLL_BACK_SPEED一样取正值，而上拉加载时marginFooter更大
                    int speed = (marginFooter - SHOW_MARGIN) / SCROLL_BACK_SPEED_RATIO;
                    marginHeader += speed;
                    marginFooter -= speed;
                    if (/*marginFooter <= SHOW_MARGIN - SCROLL_BACK_TOLERATED_MIN_DELTA*/speed < SCROLL_BACK_TOLERATED_MIN_SPEED) {
                        publishProgress(mOffsetMargin, SHOW_MARGIN);
                        break;
                    }
                } else {
                    int speed = (marginHeader - SHOW_MARGIN) / SCROLL_BACK_SPEED_RATIO;
                    marginHeader -= speed;
                    marginFooter += speed;
                    if (speed < SCROLL_BACK_TOLERATED_MIN_SPEED) {
                        publishProgress(SHOW_MARGIN, mOffsetMargin);
                        break;
                    }
                }

                publishProgress(marginHeader, marginFooter);
                SystemClock.sleep(10);
            }

            mCurrentStatus = STATUS_REFRESHING;

            // 触发回调函数
            if (mListener != null) {
                if (!mIsFooterOperated)
                    mListener.onRefresh();
                else
                    mListener.onLoadMore();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... margin) {
            updateHeaderView();
            //mLayoutParam.topMargin = margin[0];
            //mHeader.setLayoutParams(mLayoutParam);
            if (mIsFooterOperated) {
                mLayoutParam3.topMargin = margin[0];
                mInnerView.setLayoutParams(mLayoutParam3);
            } else {
                mLayoutParam.topMargin = margin[0];
                mHeader.setLayoutParams(mLayoutParam);
            }


            mLayoutParam2.bottomMargin = margin[1];
            mFooter.setLayoutParams(mLayoutParam2);
        }
    }

    /**
     * 隐藏下拉头的任务，当未进行下拉刷新或下拉刷新完成后，此任务将会使下拉头重新隐藏。
     */
    class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            // 有一种情况是当该控件的parent设定为GONE，因为尚未布局，mLayoutParam为空
            // 这种情况多存在于同一个页面存在两个内容容器，一个包含PullToRefreshViewGroup + ListView
            // ，另一个布局用于list为空时显示。默认我们会隐藏该空间所在的layout
            if (mLayoutParam == null/*|| mLayoutParam2 == null*/) return null; // 这种情况只判断一个即可

            //int marginHeader = mLayoutParam.topMargin;
            int marginHeader = mIsFooterOperated ? mLayoutParam3.topMargin : mLayoutParam.topMargin;
            int marginFooter = mLayoutParam2.bottomMargin;

            while (true) {
                if (mIsFooterOperated) {
                    /*marginHeader = marginHeader + SCROLL_BACK_SPEED;
                    marginFooter = marginFooter - SCROLL_BACK_SPEED;
                    if (marginFooter <= mOffsetMargin) {
                        publishProgress(marginHeader, mOffsetMargin);
                        break;
                    }*/
                    int speed = (marginFooter - SHOW_MARGIN) / SCROLL_BACK_SPEED_RATIO;
                    marginHeader += speed;
                    marginFooter -= speed;
                    if (speed < SCROLL_BACK_TOLERATED_MIN_SPEED) {
                        publishProgress(SHOW_MARGIN, mOffsetMargin);
                        break;
                    }
                } else {
                    int speed = (marginHeader - SHOW_MARGIN) / SCROLL_BACK_SPEED_RATIO;
                    marginHeader -= speed;
                    marginFooter += speed;
                    if (speed < SCROLL_BACK_TOLERATED_MIN_SPEED) {
                        publishProgress(mOffsetMargin, mOffsetMargin);
                        break;
                    }
                }

                publishProgress(marginHeader, marginFooter);
                SystemClock.sleep(10);
            }

            mCurrentStatus = STATUS_REFRESH_FINISHED;
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... margin) {
            //mLayoutParam.topMargin = margin[0];
            //mHeader.setLayoutParams(mLayoutParam);
            if (mIsFooterOperated) {
                mLayoutParam3.topMargin = margin[0];
                mInnerView.setLayoutParams(mLayoutParam3);
            } else {
                mLayoutParam.topMargin = margin[0];
                mHeader.setLayoutParams(mLayoutParam);
            }

            mLayoutParam2.bottomMargin = margin[1];
            mFooter.setLayoutParams(mLayoutParam2);
        }
    }

    int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
