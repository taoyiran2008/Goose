package com.goose.app.ui;

/**
 * Created by taoyr on 2018/1/6.
 */

/*public class TestActivity extends BaseActivity<IBasePresenter<IBaseView>> {

    SectionsPagerAdapter mAdapter;

    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.tab)
    TabLayout tab;

    @Inject
    PictureFragment mIndex;
    @Inject
    TextViewFragment mMy;
    @Inject
    TextViewFragment mCategory;
    @Inject
    TextViewFragment mAccount;

    ArrayList<Fragment> mFragments = new ArrayList<>();
    String[] mTabTitles = {"主页", "我的", "分类", "帐号"};
    int[] mIcons = {R.drawable.selector_tab_icon_image, R.drawable.selector_tab_icon_video,
            R.drawable.selector_tab_icon_book, R.drawable.selector_tab_icon_account};

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        //asvp.initialize(new MyViewPagerAdapter(this));
        mFragments.add(mIndex);
        LogMan.logDebug("mPresenter == null : " + (mPresenter == null));
        *//*mFragments.add(new Fragment());
        mFragments.add(new Fragment());
        mFragments.add(new Fragment());*//*
        mFragments.add(mMy);
        //mFragments.add(new TextViewFragment());
        mFragments.add(mCategory);
        mFragments.add(mAccount);
        mAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);
        // 防止IndexFragment重建，导致onCreateView被繁复调用
        vp.setOffscreenPageLimit(mTabTitles.length);

        *//*txt_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) startActivity(new Intent(mContext, SearchActivity.class));
            }
        });*//*

        setExitOnDoubleBack(true);

        initTab();
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
                if (tab.getPosition() == 0) { // 主页

                } else if (tab.getPosition() == 1) { // 我的

                } else if (tab.getPosition() == 2) { // 分类

                } else if (tab.getPosition() == 3) { // 账号

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

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
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
}*/
