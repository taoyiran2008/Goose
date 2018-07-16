package com.goose.app.ui.picture;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.goose.app.R;
import com.goose.app.model.BannerInfo;
import com.goose.app.model.CategoryInfo;
import com.goose.app.model.PictureInfo;
import com.goose.app.widgets.PictureListController;
import com.goose.app.widgets.TopBarGoose;
import com.taoyr.app.base.BaseFragment;
import com.taoyr.app.utility.PictureLoader;
import com.taoyr.pulltorefresh.PullToRefreshListener;
import com.taoyr.pulltorefresh.PullToRefreshViewGroup;
import com.taoyr.widget.widgets.carouselviewpager.CarouselViewPager;
import com.taoyr.widget.widgets.carouselviewpager.LoopPagerAdapter;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerView;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerViewGlue;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by taoyr on 2017/10/11.
 */

public class PictureFragment extends BaseFragment<PictureContract.Presenter> implements PictureContract.View {

    private View mRoot;

    @BindView(R.id.nested_scroll_view)
    NestedScrollView nested_scroll_view;

    @BindView(R.id.base_recycler_view)
    BaseRecyclerViewGlue base_recycler_view;

    @BindView(R.id.pull_to_refresh)
    PullToRefreshViewGroup pull_to_refresh;

    @BindView(R.id.carousel_view_pager)
    CarouselViewPager carousel_view_pager;

    @BindView(R.id.top_bar_goose)
    TopBarGoose top_bar_goose;

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
        base_recycler_view.initialize(new PictureListController(mContext), BaseRecyclerView.ORIENTATION_VERTICAL, 1, 20);

        mPresenter.getBannerList();
        mPresenter.getCategoryList();
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

    @Override
    public void getBannerListOnUi(List<BannerInfo> list) {
        MyLoopPagerAdapter adapter = new MyLoopPagerAdapter(mContext, carousel_view_pager);
        adapter.refresh(list);
    }

    @Override
    public void getProductListOnUi(List<PictureInfo> list) {
        base_recycler_view.refresh(list);
    }

    @Override
    public void getCategoryListOnUi(List<CategoryInfo> list) {
        top_bar_goose.initialize(list, new TopBarGoose.Callback() {
            @Override
            public void onCategorySelect(CategoryInfo category) {
                mPresenter.getProductList(category.code);
            }

            @Override
            public void onMoreClick() {

            }

            @Override
            public void onSearchClick() {

            }
        });

        mPresenter.getProductList(list.get(0).code);
    }

    private static class MyLoopPagerAdapter extends LoopPagerAdapter<BannerInfo>  {

        public MyLoopPagerAdapter(Context context, CarouselViewPager viewPager) {
            super(context, viewPager);
        }

        @Override
        protected View getItemView(BannerInfo info) {
            ImageView view = new ImageView(mContext);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            PictureLoader.simpleLoad(view, info.image);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }
    }
}
