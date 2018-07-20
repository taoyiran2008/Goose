package com.goose.app.ui.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.goose.app.R;
import com.goose.app.data.DataProvider;
import com.goose.app.model.CategoryInfo;
import com.goose.app.rxbus.RefreshProductEvent;
import com.goose.app.ui.account.AccountFragment;
import com.goose.app.ui.picture.PictureFragment;
import com.goose.app.ui.search.SearchActivity;
import com.goose.app.widgets.TopBarGoose;
import com.taoyr.app.base.BaseActivity;
import com.taoyr.app.utility.LogMan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by taoyr on 2018/1/6.
 */

public class MainActivity extends BaseActivity<MainContract.Presenter> implements MainContract.View  {

    SectionsPagerAdapter mAdapter;

    @BindView(R.id.top_bar_goose)
    TopBarGoose top_bar_goose;

    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.tab)
    TabLayout tab;

    @Inject
    PictureFragment mPicture;
    @Inject
    TextViewFragment mMy;
    @Inject
    TextViewFragment mCategory;
    @Inject
    AccountFragment mAccount;

    /*侧滑菜单相关*/
    //@BindView(R.id.ll_drawer_head)
    View ll_drawer_head;

    @BindView(R.id.nav)
    NavigationView nav;

    @BindView(R.id.drawer)
    DrawerLayout drawer;


    ArrayList<Fragment> mFragments = new ArrayList<>();
    String[] mTabTitles = {"美图", "视频", "小说", "我的"};
    int[] mIcons = {R.drawable.selector_tab_icon_index, R.drawable.selector_tab_icon_my,
            R.drawable.selector_tab_icon_category, R.drawable.selector_tab_icon_account};

    HashMap<String, List<CategoryInfo>> mCategoryMap = new HashMap<>();
    int mCurrentPosition = 0;
    String mProductType = DataProvider.DATA_TYPE_PICTURE;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        //asvp.initialize(new MyViewPagerAdapter(this));
        mFragments.add(mPicture);
        LogMan.logDebug("mPresenter == null : " + (mPresenter == null));
        /*mFragments.add(new Fragment());
        mFragments.add(new Fragment());
        mFragments.add(new Fragment());*/
        mFragments.add(mMy);
        //mFragments.add(new TextViewFragment());
        mFragments.add(mCategory);
        mFragments.add(mAccount);
        mAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);
        // 防止IndexFragment重建，导致onCreateView被繁复调用
        vp.setOffscreenPageLimit(mTabTitles.length);

        /*txt_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) startActivity(new Intent(mContext, SearchActivity.class));
            }
        });*/

        setExitOnDoubleBack(true);

        initTab();

        mPresenter.getCategoryList(mProductType);

        initSideBar();
        initGooseTitleBar();
    }

    private void initGooseTitleBar() {
        top_bar_goose.initialize(new ArrayList<CategoryInfo>(), new TopBarGoose.Callback() {
            @Override
            public void onCategorySelect(CategoryInfo category) {
                sendEvent(new RefreshProductEvent(mProductType, category.code));
            }

            @Override
            public void onMoreClick() {
                if (drawer.isDrawerOpen(nav)){
                    drawer.closeDrawer(nav);
                }else{
                    drawer.openDrawer(nav);
                }
            }

            @Override
            public void onSearchClick() {
                Intent intent = new Intent(mContext, SearchActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    private void initSideBar() {
        ll_drawer_head = nav.getHeaderView(0);

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.personal:
                        break;
                }
                showToast((String) item.getTitle());
                return false;
            }
        });

        Glide.with(this).load(R.drawable.profile_bg).asBitmap().transform(new BlurTransformation(mContext, 25))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(mContext.getResources(), resource);
                        ll_drawer_head.setBackground(drawable);
                    }
                });
    }

    private void initTab() {
        tab.setTabMode(TabLayout.MODE_FIXED);
        tab.setTabTextColors(ContextCompat.getColor(this, R.color.tf_grey),
                ContextCompat.getColor(this, R.color.orange));
        tab.setSelectedTabIndicatorHeight(0);
        //tab.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white));
        //ViewCompat.setElevation(tab, 10);
        tab.setupWithViewPager(vp);

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() != mCurrentPosition) {
                    mCurrentPosition = tab.getPosition();

                    top_bar_goose.setVisibility(View.VISIBLE);
                    if (mCurrentPosition == 0) { // 图片
                        mProductType = DataProvider.DATA_TYPE_PICTURE;

                    } else if (mCurrentPosition == 1) { // 视频

                    } else if (mCurrentPosition == 2) { // 小说

                    } else if (mCurrentPosition == 3) { // 我的
                        top_bar_goose.setVisibility(View.GONE);
                    }

                    if (mCategoryMap.get(mProductType) == null) {
                        mPresenter.getCategoryList(mProductType);
                    } else {
                        top_bar_goose.refresh(mCategoryMap.get(mProductType));
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

        for (int i = 0; i < mTabTitles.length; i++) {
            // tab layout绑定viewpager后，tabitem会自动创建
            //TabLayout.Tab tabItem = tab.newTab();
            //tab.addTab(tabItem);
            TabLayout.Tab tabItem = tab.getTabAt(i);
            if (tabItem != null) {
                tabItem.setCustomView(getTabView(i));
                if (i == 0) {
                    tabItem.select();
                }
            }
        }
    }

    private View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item_main, null);
        //ImageView img_icon = ButterKnife.findById(view, R.id.img_icon);
        ImageView img_icon = view.findViewById(R.id.img_icon);
        TextView txt_title = view.findViewById(R.id.txt_title);

        // Android 4.4 setImageResource Resources$NotFoundException
        // 不是vector导致的，也不是模块化依赖导致的，是我把图片仅放到了drawable-v24文件夹下面
        img_icon.setImageResource(mIcons[position]);
        //img_icon.setImageDrawable(ContextCompat.getDrawable(this, mIcons[position]));
        txt_title.setText(mTabTitles[position]);
        return view;
    }

    @Override
    public void getCategoryListOnUi(List<CategoryInfo> list, String type) {
        mCategoryMap.put(type, list);
        top_bar_goose.refresh(list);
        sendEvent(new RefreshProductEvent(type, list.get(0).code));
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // 显示在tab上的名字
            if (position < mTabTitles.length)
                return mTabTitles[position];
            else
                return "";
        }
    }
}
