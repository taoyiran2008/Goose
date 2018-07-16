package com.taoyr.widget.widgets.carouselviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taoyr on 2018/6/28.
 */

public abstract class LoopPagerAdapter<T> extends PagerAdapter implements ViewPager.OnPageChangeListener {

    protected Context mContext;
    protected List<T> mList;
    protected List<View> views = new ArrayList<>();
    CarouselViewPager mViewPager;
    int currentPosition = 0;

    public LoopPagerAdapter(Context context, CarouselViewPager viewPager) {
        mContext = context;
        mViewPager = viewPager;
    }

    public void refresh(List<T> list) {
        mList = list;
        prepareData();
        notifyDataSetChanged();
    }

    void prepareData() {
        if (mList != null && mList.size() > 0) {
            // 添加最后一页到第一页
            mList.add(0,mList.get(mList.size()-1));
            // 添加第一页(经过上行的添加已经是第二页了)到最后一页
            mList.add(mList.get(1));
        }

        views = new ArrayList<>();
        for (T data : mList) {
            views.add(getItemView(data));
        }

        mViewPager.setAdapter(this);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(1,false);
    }

    protected abstract View getItemView(T data);

    @Override
    public final int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public final boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public final Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        view.setTag(position);
        container.addView(view);
        return view;
    }

    @Override
    public final void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;

        if (currentPosition == 0 && !mInTransition) {
            mInTransition = true;
            // 使得切换更平滑
            mViewPager.setCurrentItem(getCount() - 1, false);
            mViewPager.setCurrentItem(getCount() - 2);

            //mViewPager.setCurrentItem(getCount() - 2, false);
        } else if (currentPosition == getCount() - 1 && !mInTransition) {
            mInTransition = true;
            // 若当前为倒数第一张，设置页面为第二张
            mViewPager.setCurrentItem(0, false);
            mViewPager.setCurrentItem(1);

            //mViewPager.setCurrentItem(1, false);
        }
    }

    boolean mInTransition = false;

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state != ViewPager.SCROLL_STATE_IDLE) {
            mInTransition = false;
        }

        /*if (state != ViewPager.SCROLL_STATE_IDLE) return;

        if (currentPosition == 0) {
            // 使得切换更平滑
            mViewPager.setCurrentItem(getCount() - 3, false);
            mViewPager.setCurrentItem(getCount() - 2);

            //mViewPager.setCurrentItem(getCount() - 2, false);
        } else if (currentPosition == getCount() - 1) {
            // 若当前为倒数第一张，设置页面为第二张
            mViewPager.setCurrentItem(2, false);
            mViewPager.setCurrentItem(1);

            //mViewPager.setCurrentItem(1, false);
        }*/
    }
}
