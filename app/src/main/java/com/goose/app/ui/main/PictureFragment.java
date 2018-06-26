package com.goose.app.ui.main;

import android.view.View;

import com.goose.app.R;
import com.goose.app.model.PictureInfo;
import com.goose.app.widgets.PictureListController;
import com.taoyr.app.base.BaseFragment;
import com.taoyr.app.base.IBasePresenter;
import com.taoyr.app.base.IBaseView;
import com.taoyr.pulltorefresh.PullToRefreshListener;
import com.taoyr.pulltorefresh.PullToRefreshViewGroup;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerView;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerViewGlue;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by taoyr on 2017/10/11.
 */

public class PictureFragment extends BaseFragment<IBasePresenter<IBaseView>>  {

    private View mRoot;

    @BindView(R.id.base_recycler_view)
    BaseRecyclerViewGlue base_recycler_view;

    @BindView(R.id.pull_to_refresh)
    PullToRefreshViewGroup pull_to_refresh;

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
}
