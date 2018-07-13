package com.taoyr.widget.widgets.carouselviewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by taoyr on 2018/1/6.
 */

public class CarouselViewPager extends ViewPager {

    private static final int DEFAULT_TIMEOUT_IN_SECONDS = 2;
    private static final int DEFAULT_OFF_SCREEN_LIMIT = 3;

    /*@IntDef({RESUME, PAUSE, DESTROY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LifeCycle {
    }

    public static final int RESUME = 0;
    public static final int PAUSE = 1;
    public static final int DESTROY = 2;
    *//**
     * 生命周期状态，保证{@link #mCarouselTimer}在各生命周期选择执行策略
     *//*
    private int mLifeCycle = RESUME;*/
    /**
     * 是否正在触摸状态，用以防止触摸滑动和自动轮播冲突
     */
    private boolean mIsTouching = false;

    /**
     * 轮播间隔时间
     */
    private int mTimeOut = DEFAULT_TIMEOUT_IN_SECONDS;
    private Context mContext;

    private final static float DISTANCE = 10;
    private float downX;
    private float downY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            downX = ev.getX();
            downY = ev.getY();
        }else if (ev.getAction() == MotionEvent.ACTION_UP) {

            float upX = ev.getX();
            float upY = ev.getY();

            if(Math.abs(upX - downX) > DISTANCE || Math.abs(upY - downY) > DISTANCE){
                return super.dispatchTouchEvent(ev);
            }

            View view = viewOfClickOnScreen(ev);
            if (view != null) {
                int position = (Integer) view.getTag();
                setCurrentItem(position);
                //if (state != ViewPager.SCROLL_STATE_IDLE) return;
                //switchPosition(position, false);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void switchPosition(int position, boolean isOnScroll) {
        CarouselPagerAdapter adapter = (CarouselPagerAdapter) getAdapter();
        if (position == 0) {
            position = adapter.getRealDataCount();
            //setCurrentItem(position, false);
            setCurrentItem(position + 1, false);
            setCurrentItem(position);
        } else if (position == adapter.getCount() - 1) {
            position = adapter.getRealDataCount() - 1;
            //setCurrentItem(position, false);
            setCurrentItem(position - 1, false);
            setCurrentItem(position);
        } else if (getCurrentItem() != position && !isOnScroll) {
            setCurrentItem(position);
        }
    }

    /**
     * 轮播定时器
     */
    private ScheduledExecutorService mCarouselTimer;

    public CarouselViewPager(Context context) {
        super(context, null);
    }

    public CarouselViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    /*public void setLifeCycle(@LifeCycle int lifeCycle) {
        this.mLifeCycle = lifeCycle;
    }*/

    private View viewOfClickOnScreen(MotionEvent ev) {
        int childCount = getChildCount();
        int currentIndex = getCurrentItem();
        int[] location = new int[2];
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            int position = (Integer) v.getTag();
            v.getLocationOnScreen(location);
            int minX = location[0];
            int minY = location[1];

            int maxX = location[0] + v.getWidth();
            int maxY = location[1] + v.getHeight();

            if(position < currentIndex){
                maxX -= v.getWidth() * (1 - GalleryTransformer.MIN_SCALE) * 0.5 + v.getWidth() * (Math.abs(1 - GalleryTransformer.MAX_SCALE)) * 0.5;
                minX -= v.getWidth() * (1 - GalleryTransformer.MIN_SCALE) * 0.5 + v.getWidth() * (Math.abs(1 - GalleryTransformer.MAX_SCALE)) * 0.5;
            }else if(position == currentIndex){
                minX += v.getWidth() * (Math.abs(1 - GalleryTransformer.MAX_SCALE));
            }else if(position > currentIndex){
                maxX -= v.getWidth() * (Math.abs(1 - GalleryTransformer.MAX_SCALE)) * 0.5;
                minX -= v.getWidth() * (Math.abs(1 - GalleryTransformer.MAX_SCALE)) * 0.5;
            }
            float x = ev.getRawX();
            float y = ev.getRawY();

            if ((x > minX && x < maxX) && (y > minY && y < maxY)) {
                return v;
            }
        }
        return null;
    }

    void init() {
        setOffscreenPageLimit(DEFAULT_OFF_SCREEN_LIMIT);

        // 设置3d效果
        setPageTransformer(true, new GalleryTransformer());

        /*addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchPosition(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });*/

        // 坑2, getLayoutParam NPE
        post(new Runnable() {
            @Override
            public void run() {
                // 设置view pager的宽度为屏幕的1/2
                WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                int screenWidth = wm.getDefaultDisplay().getWidth();
                int width = screenWidth / 2 * 1; // 1/2
                ViewGroup.LayoutParams lp0 = getLayoutParams();
                lp0.width = width;
                setLayoutParams(lp0);

                // 调整宽高比例
                tuneHeightRatio(mContext, CarouselViewPager.this, 120, 240);

                // 设置view pager的layout_gravity为水平居中
                if (getLayoutParams() instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) getLayoutParams();
                    lp.gravity = Gravity.CENTER_HORIZONTAL;
                } else if (getLayoutParams() instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
                    lp.gravity = Gravity.CENTER_HORIZONTAL;
                } else if (getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
                    lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                }

                // 设置page之间的margin为负数
                int pageMargin = -1 *  getLayoutParams().width / 3;
                setPageMargin(20);

                // 设置clip children以达到允许左右两个page，超出view pager显示区域(restricted frame/area)的效果
                if (getParent() instanceof ViewGroup) {
                    ViewGroup parent = (ViewGroup) getParent();
                    parent.setClipChildren(false);
                    parent.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return CarouselViewPager.this.dispatchTouchEvent(event);
                        }
                    });
                }
                setClipChildren(false);

                // 坑3，有几个PageTransformer需要这样才能在初始化时表现正常
                //setCurrentItem(getCurrentItem() + 1);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mIsTouching = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsTouching = false;
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //startTimer();
    }

    public void startTimer() {
        startTimer(DEFAULT_TIMEOUT_IN_SECONDS);
    }

    public void startTimer(int timeOut) {
        mTimeOut = timeOut;
        shutdownTimer();
        mCarouselTimer = Executors.newSingleThreadScheduledExecutor();
        mCarouselTimer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!mIsTouching
                        && getAdapter() != null
                        && getAdapter().getCount() > 1) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            setCurrentItem(getCurrentItem() + 1);
                        }
                    });
                }
                /*switch (mLifeCycle) {
                    case RESUME:
                        if (!mIsTouching
                                && getAdapter() != null
                                && getAdapter().getCount() > 1) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    setCurrentItem(getCurrentItem() + 1);
                                }
                            });
                        }
                        break;
                    case PAUSE:
                        break;
                    case DESTROY:
                        shutdownTimer();
                        break;
                }*/
            }
        }, 0, 1000 * timeOut, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        shutdownTimer();
    }

    private void shutdownTimer() {
        if (mCarouselTimer != null && mCarouselTimer.isShutdown() == false) {
            mCarouselTimer.shutdown();
        }
        mCarouselTimer = null;
    }

    public static void tuneHeightRatio(final Context context, final View view, final int height, final int width){ // 300/750
        view.post(new Runnable() {
            @Override
            public void run() {
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                /*int measureSpec = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
                view.measure(measureSpec, measureSpec);
                int originWidth = view.getMeasuredWidth();
                int originHeight = view.getMeasuredHeight();*/
                //int originWidth = CommonUtils.getViewDimensionInPx(view)[0];
                int originWidth = view.getWidth();
                // 在OPP R9s手机上，通过view.getWidth()，有的recycler item view的宽度获取为0
                if (originWidth <= 0) {
                    originWidth = getViewDimensionInPx2(view)[0];
                }

                int screenWidth = wm.getDefaultDisplay().getWidth();

                // 进行等比缩放，宽度始终等于屏幕宽度的80%。不考虑高度大于宽度的情况（会员卡原图宽高比例是3:2）
                // 注意两个整形相除，不会保留小数，当除数大于被除数时，等于0
                double ratio = (double) height / width;
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                //lp.width = screenWidth;
                //lp.height = (int) (screenWidth * ratio);
                lp.height = (int) (originWidth * ratio);
                view.setLayoutParams(lp);
            }
        });
    }

    public static int[] getViewDimensionInPx2(View view) { // -1, 630(Carousel image)
        int[] dimension = new int[2];
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        dimension[0] = lp.width;
        dimension[1] = lp.height;
        return dimension;
    }
}