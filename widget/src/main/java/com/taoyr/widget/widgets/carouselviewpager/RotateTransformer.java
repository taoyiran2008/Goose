package com.taoyr.widget.widgets.carouselviewpager;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by taoyr on 2018/6/28.
 */

public class RotateTransformer implements ViewPager.PageTransformer {

    public static final float MAX_SCALE = 1.2f;
    public static final float MIN_SCALE = 0.6f;

    @Override
    public void transformPage(View page, float position) {
        page.setRotationY(position * -45);

        if (position < -1) {
            position = -1;
        } else if (position > 1) {
            position = 1;
        }

        float tempScale = position < 0 ? 1 + position : 1 - position;

        float slope = (MAX_SCALE - MIN_SCALE) / 1;
        //一个公式
        float scaleValue = MIN_SCALE + tempScale * slope;
        page.setScaleX(scaleValue);
        page.setScaleY(scaleValue);

        page.setAlpha(scaleValue);
        page.setPivotX(page.getWidth() * (1 - position - (position > 0 ? 1 : -1) * 0.75f) * 0.5f);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            page.getParent().requestLayout();
        }
    }
}
