package com.taoyr.widget.widgets.commonrv.decorator;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by taoyiran on 2018/1/13.
 * 专用于横向排列的recycler view布局
 */

public class SpaceItemDecorator extends RecyclerView.ItemDecoration {

    private int space;
    private int spanCount; // 每行（纵向）/ 每列（横向）排列的item个数

    public SpaceItemDecorator(int space, int spanCount) {
        this.space = space;
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        //outRect.bottom = space;

        // 没一行起头的那个元素，不设置左边距
        /**
         * 0 2 4
         * 1 3 5
         *
         * 0 1
         * 2 3
         * 4 5
         */
        /*if (parent.getChildLayoutPosition(view) % spanCount == 0) {
            outRect.left = 0;
        }*/
        if (parent.getChildLayoutPosition(view) < spanCount) {
            outRect.left = 0;
        }
    }
}
