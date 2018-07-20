package com.goose.app.widgets;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.goose.app.R;
import com.goose.app.model.CategoryInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TopBarGoose extends RelativeLayout {

    @BindView(R.id.tab_sub)
    TabLayout tab;

    @BindView(R.id.img_search)
    ImageView img_search;

    @BindView(R.id.img_slide)
    ImageView img_slide;

    Context mContext;
    View mRootView;
    int mCurrentPosition = 0;
    Callback mCallback;
    List<CategoryInfo> mCategoryList = new ArrayList<>();

    public interface Callback {
        void onCategorySelect(CategoryInfo category);
        void onMoreClick(); // side bar
        void onSearchClick();
    }

    public TopBarGoose(@NonNull Context context) {
        super(context);
    }

    public TopBarGoose(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopBarGoose(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.top_bar_goose, this);
        ButterKnife.bind(this, mRootView);
    }

    public void initialize(List<CategoryInfo> list, Callback callback) {
        mCallback = callback;
        refresh(list);
    }

    public void refresh(List<CategoryInfo> list) {
        mCategoryList = list;
        initTab();
    }

    @OnClick({R.id.img_search, R.id.img_slide})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_search:
                if (mCallback != null) {
                    mCallback.onSearchClick();
                }
                break;
            case R.id.img_slide:
                if (mCallback != null) {
                    mCallback.onMoreClick();
                }
                break;
        }
    }

    private void initTab() {
        tab.removeAllTabs();

        tab.setVisibility(View.VISIBLE);
        tab.setTabMode(TabLayout.MODE_SCROLLABLE);
        tab.setTabTextColors(ContextCompat.getColor(mContext, R.color.tf_grey),
                ContextCompat.getColor(mContext, R.color.orange));
        tab.setSelectedTabIndicatorColor(ContextCompat.getColor(mContext, R.color.orange));
        // 这会在tab layout底部产生一个阴影
        //ViewCompat.setElevation(tab, 10);

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() != mCurrentPosition) {
                    mCurrentPosition = tab.getPosition();

                    if (mCallback != null) {
                        CategoryInfo info = mCategoryList.get(mCurrentPosition);
                        mCallback.onCategorySelect(info);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        for (CategoryInfo categoryInfo : mCategoryList) {
            TabLayout.Tab tabItem = tab.newTab();
            tab.addTab(tabItem);
            tabItem.setText(categoryInfo.name);
        }

        // 根据tab layout的内容动态调整显示模式
        // TODO 这里有个bug，研报的tab会出现中间的tab item丢失text appearance，从而显示默认的小字体
        //CommonUtils.tuneIndicatorMode(tab);
    }
}
