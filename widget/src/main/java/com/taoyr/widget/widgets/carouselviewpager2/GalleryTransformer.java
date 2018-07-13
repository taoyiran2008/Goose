package com.taoyr.widget.widgets.carouselviewpager2;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 实现3D效果需要用到的类
 */

public class GalleryTransformer implements ViewPager.PageTransformer {
    /*@Override
    public void transformPage(View view, float position) {
        float scale = 0.5f;
        float scaleValue = Math.abs(Math.abs(position) - 1) * scale + scale;
        scaleValue = position < -1 || position > 1 ? scale : scaleValue;
        view.setScaleX(scaleValue);
        view.setScaleY(scaleValue);
        view.setAlpha(scaleValue);

        *//**
         * position = -1: PivotX 为 width
         * position =  0: PivotX 为 width / 2
         * position =  1: PivotX 为 0
         * 以此为基准做调整
         *//*
        final float offset = Build.VERSION.SDK_INT > 19 ? (Math.abs(position) / position) * 0.5f : 0f;
        view.setPivotX(view.getWidth() * (1 - position - offset) * scale);
        view.setPivotY(view.getHeight()/2);
        ViewCompat.setElevation(view, position >= -0.25 && position <= 0.25 ? 1 : 0);
    }*/

    public static final float MAX_SCALE = 1.2f;
    public static final float MIN_SCALE = 0.6f;
    public void transformPage(View page, float position) {
        //LogMan.logDebug("position: " + position);
        if (position < -1) {
            position = -1;
        } else if (position > 1) {
            position = 1;
        }

        float tempScale = position < 0 ? 1 + position : 1 - position;

        float slope = (MAX_SCALE - MIN_SCALE) / 1;

        float scaleValue = MIN_SCALE + tempScale * slope;
        page.setScaleX(scaleValue);
        page.setScaleY(scaleValue);
        //LogMan.logDebug("scaleValue: " + scaleValue);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            page.getParent().requestLayout();
        }
    }

    /*@Override
    public void transformPage(View view, float position) {
        //view.setRotationY(position * -45);
        float scale = 0.5f;

        float scaleValue = (1 - Math.abs(position) * scale);
        view.setScaleX(scaleValue);
        view.setScaleY(scaleValue);
        view.setAlpha(scaleValue);
        view.setPivotX(view.getWidth() * (1 - position - (position > 0 ? 1 : -1) * 0.75f) * scale);
        view.setPivotY(view.getHeight()/2);
        //view.setRotationY(position * -45);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(position > -0.25 && position < 0.25 ? 1 : 0);
        }
    }*/

    /*private static final float MIN_SCALE = 0.75f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            view.setAlpha(1);
            view.setTranslationX(0);
            view.setScaleX(1);
            view.setScaleY(1);

        } else if (position <= 1) { // (0,1]
            // Fade the page out.
            view.setAlpha(1 - position);

            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);

            // Scale the page down (between MIN_SCALE and 1)
            float scaleFactor = MIN_SCALE
                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }*/
}
