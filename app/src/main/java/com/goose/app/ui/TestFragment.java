package com.goose.app.ui;

/**
 * Created by taoyr on 2017/10/11.
 */

/*public class TestFragment extends BaseFragment<IBasePresenter<IBaseView>>  {

    private View mRoot;

    @BindView(R.id.base_recycler_view)
    BaseRecyclerViewGlue base_recycler_view;

    @BindView(R.id.pull_to_refresh)
    PullToRefreshViewGroup pull_to_refresh;

    @BindView(R.id.carousel_view_pager)
    CarouselViewPager carousel_view_pager;

    @BindView(R.id.coverflow)
    CoverFlowView coverflow;

    private List<PictureInfo> mList = new ArrayList<>();

    @Inject
    public TestFragment() {
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_picture;
    }

    @Override
    protected void initView() {
        initPullToRefreshWidget();
        base_recycler_view.initialize(new PictureListController(), BaseRecyclerView.ORIENTATION_VERTICAL, 1, 20);
        for (int i = 0; i < 60; i++) {
            mList.add(new PictureInfo());
        }
        base_recycler_view.refresh(mList);

        initCarouselViewPager();

        coverflow.setAdapter(new MyCoverFlowAdapter(mContext));
        coverflow.setImageClickListener(new CoverFlowView.ImageClickListener() {
            @Override
            public void onClick(CoverFlowView coverFlowView, int position) {
                showToast("image postion: " + position);
                coverflow.setSelection(position);
            }
        });
    }

    private void initPullToRefreshWidget() {
        pull_to_refresh.setTheme(PullToRefreshViewGroup.THEME_BLUE_TONE);
        pull_to_refresh.setFooterEnable(true);
        //pull_to_refresh.setAlwaysEnablePullUp(true);
        pull_to_refresh.setOnRefreshListener(new PullToRefreshListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
            }
        }, hashCode());
    }

    private void initCarouselViewPager() {
       // MyCarouselPagerAdatper adapter = new MyCarouselPagerAdatper(mContext, carousel_view_pager);
        //carousel_view_pager.setAdapter(adapter);
        //carousel_view_pager.startTimer();

        MyLoopPagerAdapter adapter1 = new MyLoopPagerAdapter(mContext, carousel_view_pager);
        ArrayList<Integer> mList = new ArrayList<>();
        mList.add(R.drawable.default_pic_02);
        *//*mList.add(R.drawable.default_pic_02);
        mList.add(R.drawable.default_pic_02);*//*
        adapter1.refresh(mList);
    }

    *//*private static class MyCarouselPagerAdatper extends CarouselPagerAdapter {

        // 测试数量，1、2、3、4、8
        int[] imgRes = {
                R.drawable.default_pic_02,
                R.drawable.default_pic_02,
                R.drawable.default_pic_02,
        };

        public MyCarouselPagerAdatper(Context context, CarouselViewPager viewPager) {
            super(context, viewPager);
        }

        @Override
        public int getRealDataCount() {
            return imgRes != null ? imgRes.length : 0;
        }

        @Override
        public Object instantiateRealItem(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            // 坑1
            //view.setAdjustViewBounds(true);
            view.setImageResource(imgRes[position]);
            //view.setLayoutParams(new LinearLayout.LayoutParams(900, 400));
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            container.addView(view);
            return view;
        }
    }*//*

    private static class MyLoopPagerAdapter extends LoopPagerAdapter<Integer>  {

        public MyLoopPagerAdapter(Context context, CarouselViewPager viewPager) {
            super(context, viewPager);
        }

        @Override
        protected View getItemView(Integer data) {
            ImageView view = new ImageView(mContext);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setImageResource(data);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }
    }

    public class MyCoverFlowAdapter extends CoverFlowAdapter {

        private boolean dataChanged;

        public MyCoverFlowAdapter(Context context) {

            image1 = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.footprint_header_bg1);

            image2 = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.default_pic_02);
        }

        public void changeBitmap() {
            dataChanged = true;

            notifyDataSetChanged();
        }

        private Bitmap image1 = null;

        private Bitmap image2 = null;

        @Override
        public int getCount() {
            return dataChanged ? 3 : 8;
        }

        @Override
        public Bitmap getImage(final int position) {
            return (dataChanged && position == 0) ? image2 : image1;
        }
    }

}*/
