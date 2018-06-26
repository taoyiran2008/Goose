package com.taoyr.widget.widgets.autosrollviewpager;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.taoyr.widget.R;

import java.util.List;

import static android.view.Gravity.CENTER;

/**
 * Created by taoyr on 2018/1/6.
 */

public class AutoScrollViewPager extends RelativeLayout implements ViewPager.OnPageChangeListener {

    private static final int CAROUSEL_INTERVAL_IN_MS = 3000; // 3s切换一下广告牌

    private ViewPager mViewPager;
    private BannerPagerAdapter mAdapter;

    private Context mContext;
    private Handler mHandler;

    private LinearLayout mPointLayout;
    private FrameLayout mMaskLayout;
    private ImageView mDefaultImage;

    private int mDefaultImageResId = -1;

    public AutoScrollViewPager(Context context) {
        super(context);
    }

    public AutoScrollViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScrollViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        //mHandler = getHandler(); // NPE
        mHandler = new Handler();
        mViewPager = new ViewPager(context);
        mViewPager.addOnPageChangeListener(this);
        addView(mViewPager);
        // 这行代码会导致运行在Android5.1（API 22）手机上Crash，报错 Error inflating class
        // 需要用mContext.getResource().getColor()
        //setBackgroundColor(mContext.getColor(R.color.lightgrey));
    }

    // TODO 考虑扩展，可以将BannerPagerAdapter抽象出一个接口
    public void initialize(BannerPagerAdapter adapter, int defaultImageResId) {
        if (mViewPager != null) {
            mAdapter = adapter;
            mViewPager.setAdapter(adapter);
            initPointView(adapter.getCount());
            resumeScrolling();
            addMask();
            setDefaultImageResId(defaultImageResId);
            showDefaultImage(true);
        }
    }

    public void refresh(List list) {
        if (mAdapter != null) {
            mAdapter.refresh(list);
            initPointView(mAdapter.getCount());
        }
        showDefaultImage(list == null || list.isEmpty());
        resumeScrolling();
    }

    private Runnable mShowNext = new Runnable() {
        @Override
        public void run() {
            if (mViewPager != null){
                int nextIndex = mViewPager.getCurrentItem() + 1;
                int maxIndex = mAdapter.getCount() - 1;
                if (nextIndex > maxIndex) nextIndex = 0;
                mViewPager.setCurrentItem(nextIndex);
                resumeScrolling();
            }
        }
    };

    public void resumeScrolling() {
        mHandler.removeCallbacks(mShowNext);
        mHandler.postDelayed(mShowNext, CAROUSEL_INTERVAL_IN_MS);
    }

    public void pauseScrolling() {
        mHandler.removeCallbacks(mShowNext);
    }

    public void initPointView(int size) {
        if (mPointLayout == null) {
            mPointLayout = new LinearLayout(mContext);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(ALIGN_PARENT_RIGHT);
            layoutParams.setMargins(12, 20, 12, 20);
            mPointLayout.setLayoutParams(layoutParams);
            addView(mPointLayout);
        }

        mPointLayout.removeAllViews();
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(mContext);
            //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 8;
            params.gravity = CENTER;
            imageView.setLayoutParams(params);
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.dot_long);
            } else {
                imageView.setBackgroundResource(R.drawable.dot_short);
            }

            mPointLayout.addView(imageView);
        }
    }

    /**
     * 在最上层覆盖上蒙层效果，解决显示浅色图片时，浅色的圆点显示不出来的问题。
     */
    private void addMask() {
        if (mMaskLayout == null) {
            mMaskLayout = new FrameLayout(mContext);
            mMaskLayout.setBackgroundColor(mContext.getResources().getColor(R.color.tf_mask_banner));
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(ALIGN_PARENT_TOP);
            layoutParams.addRule(ALIGN_PARENT_LEFT);
            mMaskLayout.setLayoutParams(layoutParams);
            addView(mMaskLayout);
        }
    }

    public void setDefaultImageResId(int defaultImageResId) {
        this.mDefaultImageResId = defaultImageResId;
    }

    private void showDefaultImage(boolean show) {
        if (mDefaultImage == null) {
            mDefaultImage = new ImageView(mContext);
            if (mDefaultImageResId == -1) {
                mDefaultImage.setBackgroundResource(R.drawable.default_banner_index);
            } else {
                mDefaultImage.setBackgroundResource(mDefaultImageResId);
            }

            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(ALIGN_PARENT_TOP);
            layoutParams.addRule(ALIGN_PARENT_LEFT);
            mDefaultImage.setLayoutParams(layoutParams);
            addView(mDefaultImage);
        }
        mDefaultImage.setVisibility(show ? VISIBLE : GONE);
    }

    private void updatePointView(int position) {
        int size = mPointLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            ImageView imageView = (ImageView) mPointLayout.getChildAt(i);
            if (i == position) {
                imageView.setBackgroundResource(R.drawable.dot_long);
            } else {
                imageView.setBackgroundResource(R.drawable.dot_short);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        updatePointView(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_DRAGGING:
                // 拖拽的时候清除轮播事件
                pauseScrolling();
                break;
            case ViewPager.SCROLL_STATE_IDLE:
                // 静止的时候开始自动轮播
                resumeScrolling();
                break;
            default:
                break;
        }
    }
}