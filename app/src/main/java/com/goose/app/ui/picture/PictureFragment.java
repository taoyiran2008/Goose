package com.goose.app.ui.picture;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.goose.app.R;
import com.goose.app.model.BannerInfo;
import com.goose.app.model.CategoryInfo;
import com.goose.app.model.PictureInfo;
import com.goose.app.widgets.PictureListController;
import com.taoyr.app.base.BaseFragment;
import com.taoyr.pulltorefresh.PullToRefreshListener;
import com.taoyr.pulltorefresh.PullToRefreshViewGroup;
import com.taoyr.widget.widgets.carouselviewpager.CarouselViewPager;
import com.taoyr.widget.widgets.carouselviewpager.LoopPagerAdapter;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerView;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerViewGlue;
import com.taoyr.widget.widgets.coverflow.CoverFlowView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by taoyr on 2017/10/11.
 */

public class PictureFragment extends BaseFragment<PictureContract.Presenter> implements PictureContract.View {

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
    public PictureFragment() {
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
        /*mList.add(R.drawable.default_pic_02);
        mList.add(R.drawable.default_pic_02);*/
        adapter1.refresh(mList);
    }

    @Override
    public void getBannerListOnUi(List<BannerInfo> list) {

    }

    @Override
    public void getProductListOnUi(List<PictureInfo> list) {

    }

    @Override
    public void getCategoryListOnUi(List<CategoryInfo> list) {

    }

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
}
