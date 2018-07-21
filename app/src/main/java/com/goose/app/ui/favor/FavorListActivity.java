package com.goose.app.ui.favor;

import com.goose.app.R;
import com.goose.app.model.PictureInfo;
import com.goose.app.widgets.controller.PictureListController;
import com.taoyr.app.base.BaseActivity;
import com.taoyr.app.utility.PageManager;
import com.taoyr.pulltorefresh.PullToRefreshListener;
import com.taoyr.pulltorefresh.PullToRefreshViewGroup;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerView;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerViewGlue;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by taoyr on 2018/1/6.
 */

public class FavorListActivity extends BaseActivity<FavorListContract.Presenter>
        implements FavorListContract.View {

    @BindView(R.id.base_recycler_view)
    BaseRecyclerViewGlue base_recycler_view;

    @BindView(R.id.pull_to_refresh)
    PullToRefreshViewGroup pull_to_refresh;

    private List<PictureInfo> mList = new ArrayList<>();

    PageManager mPageManager = new PageManager() {
        @Override
        public void loadPage(int pageSize, int pageIndex) {
            mPresenter.getFavorList(pageIndex, pageSize);
        }
    };

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_favor_list;
    }

    @Override
    protected void initView() {
        setTopBarTitle("我的收藏");

        base_recycler_view.initialize(new PictureListController(mContext), BaseRecyclerView.ORIENTATION_VERTICAL, 1);

        initPullToRefreshWidget();

        mPageManager.refreshPage();
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
    public void refreshList(List list) {
        pull_to_refresh.finishLoading();
        pull_to_refresh.finishRefreshing(true);
        //if (!loadMore) mCurrentPage = 1;

        if (list == null || list.isEmpty()) {
            // 注意这里应该是mList，不然当上拉刷新没有数据的时候，原本有数据的页面会被冲掉
            // 当loadMore=false，不考虑上面这种情况
            //base_recycler_view.refresh(list);
            //base_recycler_view.refresh(mList);
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
}
