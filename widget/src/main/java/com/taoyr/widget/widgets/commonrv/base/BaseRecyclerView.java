package com.taoyr.widget.widgets.commonrv.base;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.taoyr.widget.widgets.commonrv.decorator.GridSpacingItemDecoration;
import com.taoyr.widget.widgets.commonrv.decorator.SpaceItemDecorator;

import java.util.List;

/**
 * Created by taoyiran on 2018/1/13.
 */

public class BaseRecyclerView<T> extends RecyclerView {

    public static final int ORIENTATION_HORIZONTAL = 1;
    public static final int ORIENTATION_VERTICAL = 2;
    public static final int ITEM_SPACE = 20; // dp

    Context mContext;
    RecyclerView.LayoutManager mManager;
    RvAdapter<T> mAdapter;
    View mRootView;
    ItemDecoration mDecorator;

    public BaseRecyclerView(Context context) {
        super(context);
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
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

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public void refresh(List<T> data) {
        mAdapter.refresh(data);
        /*postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollToPosition(mAdapter.getItemCount() - 1);
            }
        }, 200);*/
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
