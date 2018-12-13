package com.goose.app.ui.zhibo;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.goose.app.data.DataProvider;
import com.goose.app.model.BannerInfo;
import com.goose.app.model.PictureInfo;
import com.goose.app.rxbus.RefreshProductEvent;
import com.goose.app.ui.web.WebActivity;
import com.goose.app.widgets.controller.ZhiboListController;
import com.taoyr.app.base.BaseActivity;
import com.taoyr.app.base.BaseFragment;
import com.taoyr.app.utility.PageManager;
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
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by taoyr on 2017/10/11.
 */

public class ZhiboFragment extends BaseFragment<ZhiboContract.Presenter> implements ZhiboContract.View {

    private View mRoot;

    @BindView(R.id.nested_scroll_view)
    NestedScrollView nested_scroll_view;

    @BindView(R.id.base_recycler_view)
    BaseRecyclerViewGlue base_recycler_view;

    @BindView(R.id.pull_to_refresh)
    PullToRefreshViewGroup pull_to_refresh;

    @BindView(R.id.carousel_view_pager)
    CarouselViewPager carousel_view_pager;

    private List<PictureInfo> mList = new ArrayList<>();
    String mCategoryCode="";

    PageManager mPageManager = new PageManager() {
        @Override
        public void loadPage(int pageSize, int pageIndex) {
            mPresenter.getProductList(mCategoryCode, pageIndex, pageSize);
        }
    };

    @Inject
    public ZhiboFragment() {
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_zhibo;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        final BaseActivity activity = (BaseActivity) getActivity();
//        if (isVisibleToUser) {
//            if (activity != null) {
//                activity.setStatusBarTransparent(false);
//            }
//        } else {
//            // 注意：第一次进入MainActivity即会执行，可能因为控件没有初始化完毕，而会导致NPE
//            if (activity != null) {
//                activity.setStatusBarTransparent(false);
//            }
//        }
    }
    @Override
    protected void initView() {
        initPullToRefreshWidget();
        base_recycler_view.initialize(new ZhiboListController(mContext), BaseRecyclerView.ORIENTATION_VERTICAL, 2, 1);

        mPresenter.getBannerList();

        subscribeEvent(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                if (o instanceof RefreshProductEvent) {
                    RefreshProductEvent event = (RefreshProductEvent) o;
                    if(!mCategoryCode.equals(event.categoryCode)){
                        mCategoryCode = event.categoryCode;
                        if (DataProvider.DATA_TYPE_STREAM.equals(event.productType)) {
                            mPageManager.refreshPage();
                        }
                    }
                }
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
                mPageManager.refreshPage();
            }

            @Override
            public void onLoadMore() {
                mPageManager.nextPage();
            }
        }, hashCode());
    }

    @Override
    public void getBannerListOnUi(List<BannerInfo> list) {
        if (list == null || list.isEmpty()) {
            carousel_view_pager.setVisibility(View.GONE);
        } else {
            MyLoopPagerAdapter adapter = new MyLoopPagerAdapter(mContext, carousel_view_pager);
            adapter.refresh(list);
        }
    }

    @Override
    public void getProductListOnUi(List<PictureInfo> list) {
        pull_to_refresh.finishLoading();
        pull_to_refresh.finishRefreshing(true);

        if (list == null || list.isEmpty()) {
            if (!mPageManager.isLoadMore()) {
                base_recycler_view.refresh(list);
            }
            return; // No more data
        }

        if (mPageManager.isLoadMore()) {
            //mCurrentPage++;
            mList.addAll(list);
            base_recycler_view.refresh(mList);
        } else {
            mList = list;
            base_recycler_view.refresh(mList);
        }

        mPageManager.nextPageConfirm();
    }

    private static class MyLoopPagerAdapter extends LoopPagerAdapter<BannerInfo>  {

        Context mContext;

        public MyLoopPagerAdapter(Context context, CarouselViewPager viewPager) {
            super(context, viewPager);
            mContext = context;
        }

        @Override
        protected View getItemView(final BannerInfo info) {
            /*ImageView view = new ImageView(mContext);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            PictureLoader.simpleLoad(view, info.image);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));*/
            View itemView = LayoutInflater.from(mContext).inflate(
                    R.layout.banner_item_view, null);
            ImageView img = itemView.findViewById(R.id.img);
            TextView txt_title = itemView.findViewById(R.id.txt_title);

            PictureLoader.simpleLoad(img, info.image);
            txt_title.setText(info.title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, WebActivity.class);
                    intent.putExtra(Configs.EXTRA_URL, info.url);
                    intent.putExtra(Configs.EXTRA_TITLE, info.title);
                    mContext.startActivity(intent);
                }
            });

            return itemView;
        }
    }
}
