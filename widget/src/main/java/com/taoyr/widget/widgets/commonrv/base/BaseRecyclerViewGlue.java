package com.taoyr.widget.widgets.commonrv.base;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.TranslateAnimation;

import com.taoyr.widget.widgets.commonrv.decorator.GridSpacingItemDecoration;
import com.taoyr.widget.widgets.commonrv.decorator.SpaceItemDecorator;

import java.util.List;

/**
 * Created by taoyiran on 2018/1/13.
 */

public class BaseRecyclerViewGlue<T> extends RecyclerView {

    public static final int ORIENTATION_HORIZONTAL = 1;
    public static final int ORIENTATION_VERTICAL = 2;
    public static final int ITEM_SPACE = 20; // dp

    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_DOWN = 2;
    private static final int DIRECTION_LEFT = 3;
    private static final int DIRECTION_RIGHT = 4;

    Context mContext;
    LayoutManager mManager;
    RvAdapter<T> mAdapter;
    View mRootView;
    ItemDecoration mDecorator;

    int mFirstVisiblePosition;
    int mLastVisiblePosition;
    // 平移动画相关
    int mGlueDistanceInDip = 60;
    int mGlueDurationInMs = 300;

    public BaseRecyclerViewGlue(Context context) {
        super(context);
    }

    public BaseRecyclerViewGlue(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRecyclerViewGlue(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mContext = context;
    }

    /*默认Item间距*/
    public void initialize(BaseRvController<T> controller,
                           int orientation, int columnNum) {
        initialize(controller, orientation, columnNum, ITEM_SPACE);
    }

    public void initialize(BaseRvController<T> controller,
                           int orientation, int columnNum, int span) {
        if (mDecorator != null) {
            //removeAllViews();
            removeItemDecoration(mDecorator);
        }
        if (orientation == ORIENTATION_HORIZONTAL) {
            if (columnNum == 1) {
                mManager = new LinearLayoutManager(mContext);
                LinearLayoutManager manager = (LinearLayoutManager) mManager;
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            } else {
                mManager = new GridLayoutManager(mContext, columnNum);
                LinearLayoutManager manager = (GridLayoutManager) mManager;
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            }
            if (span > 0) {
                //mDecorator = new SpaceItemDecorator(dip2px(span));
                mDecorator = new SpaceItemDecorator(dip2px(mContext, span), columnNum);
                addItemDecoration(mDecorator);
            }
        } else if (orientation == ORIENTATION_VERTICAL) {
            /*if (columnNum == 1) {
                mManager = new LinearLayoutManager(mContext);
                RecyclerViewDivider divider = new RecyclerViewDivider();
                divider.setOrientation(RecyclerViewDivider.ORIENTATION_VERTICAL);
                divider.setColor(getResources().getColor(R.color.transparent));
                divider.setDividerWidth(dip2px(ITEM_SPACE));
                addItemDecoration(divider);
            } else {
                mManager = new GridLayoutManager(mContext, columnNum);
                addItemDecoration(new SpaceItemDecorator(dip2px(ITEM_SPACE)));
            }*/
            mManager = new GridLayoutManager(mContext, columnNum);
            if (span > 0) {
                mDecorator = new GridSpacingItemDecoration(columnNum, dip2px(mContext, span), false);
                addItemDecoration(mDecorator);
            }
        }

        // mManager.setSpanSizeLookup（）// 动态更改每个item占用的列数
        setLayoutManager(mManager);
        setItemAnimator(new DefaultItemAnimator());
        mAdapter = new RvAdapter(controller, this);
        setAdapter(mAdapter);
        mAdapter.refresh(null);

        setOnScrollListener();
    }

    void setOnScrollListener() {
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisiblePosition = getFirstVisiblePosition();
                int lastVisiblePosition = getLastVisiblePosition();

                if (dy > 0 || dx > 0) { // 手势向上滑动，列表向下滚动，新出现的recycler view item，从屏幕下方一定距离往上粘滞
                    if (mLastVisiblePosition < lastVisiblePosition) {
                        int[] newPositions = getPositionInBetween(mLastVisiblePosition, lastVisiblePosition);
                        // 这里不能用recycler view一行的数量span count来遍历（比如初始化一行二列的列表，最后一列只有一个元素）
                        for (int position : newPositions) {
                            View view = mManager.findViewByPosition(position);
                            startGlueAnimation(view, dy > 0 ? DIRECTION_UP : DIRECTION_LEFT);
                        }
                    }
                } else if (dy < 0 || dx < 0) {
                    if (firstVisiblePosition < mFirstVisiblePosition) {
                        int[] newPositions = getPositionInBetween(mFirstVisiblePosition, firstVisiblePosition);
                        for (int position : newPositions) {
                            View view = mManager.findViewByPosition(position);
                            startGlueAnimation(view, dy < 0 ? DIRECTION_DOWN : DIRECTION_RIGHT);
                        }
                    }
                }
                mFirstVisiblePosition = firstVisiblePosition;
                mLastVisiblePosition = lastVisiblePosition;
            }
        });
    }

    /**
     * 适用于通过前后first/last visible position的差值，获取新进入显示区域的view position。
     * 只需要将先后两次的first/last visible position传入，便可以自动计算出结果。
     *
     * Input: 8, 10 （手势向上滑动，一行两列的情况）
     * Output: {9, 10}
     *
     *  Input: 2, 0（手势向下滑动，一行两列的情况）
     *  Output: {1, 0}
     */
    int[] getPositionInBetween(int startPosition, int endPosition) {
        int[] positions = null;
        if (startPosition < endPosition) {
            positions = new int[endPosition - startPosition];
            for (int i = startPosition, j = 0; i < endPosition; i++, j++) {
                positions[j] = i + 1;
            }
        } else if (startPosition >= endPosition) {
            positions = new int[startPosition - endPosition];
            for (int i = endPosition, j = 0; i < startPosition; i++, j++) {
                positions[j] = i;
            }
        }

        return positions;
    }

    void startGlueAnimation(View view, int direction) {
        if (view == null) {
            return;
        }

        TranslateAnimation animation;
        switch (direction) {
            case DIRECTION_UP:
                animation = new TranslateAnimation(0, 0,
                        dip2px(mContext, mGlueDistanceInDip), 0);
                break;
            case DIRECTION_DOWN:
                animation = new TranslateAnimation(0, 0,
                        -dip2px(mContext, mGlueDistanceInDip), 0);
                break;
            case DIRECTION_LEFT:
                animation = new TranslateAnimation(dip2px(mContext, mGlueDistanceInDip),
                        0, 0, 0);
                break;
            case DIRECTION_RIGHT:
                animation = new TranslateAnimation(-dip2px(mContext, mGlueDistanceInDip),
                        0, 0, 0);
                break;
            default:
                animation = new TranslateAnimation(0, 0,
                        dip2px(mContext, mGlueDistanceInDip), 0);
        }

        animation.setDuration(mGlueDurationInMs);
        view.startAnimation(animation);
    }

    private int getFirstVisiblePosition() {
        if (mManager instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) mManager;
            return manager.findFirstVisibleItemPosition();
        } else if (mManager instanceof GridLayoutManager) {
            GridLayoutManager manager = (GridLayoutManager) mManager;
            return manager.findFirstVisibleItemPosition();
        }
        return 0;
    }

    private int getLastVisiblePosition() {
        if (mManager instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) mManager;
            return manager.findLastVisibleItemPosition();
        } else if (mManager instanceof GridLayoutManager) {
            GridLayoutManager manager = (GridLayoutManager) mManager;
            return manager.findLastVisibleItemPosition();
        }
        return 0;
    }

    public void refresh(List<T> data) {
        mAdapter.refresh(data);
        post(new Runnable() {
            @Override
            public void run() {
                mFirstVisiblePosition = getFirstVisiblePosition();
                mLastVisiblePosition = getLastVisiblePosition();
            }
        });
    }

    public void gotoBottom() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollToPosition(mAdapter.getItemCount() - 1);
            }
        }, 200);
    }

    int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
