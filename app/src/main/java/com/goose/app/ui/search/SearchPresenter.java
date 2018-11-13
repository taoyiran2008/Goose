package com.goose.app.ui.search;


import com.goose.app.data.DataProvider;
import com.goose.app.model.PictureInfo;
import com.taoyr.app.base.BasePresenter;
import com.taoyr.app.ifs.UiCallback;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by taoyr on 2018/1/5.
 */

public final class SearchPresenter extends BasePresenter<SearchContract.View>
        implements SearchContract.Presenter {

    @Inject
    DataProvider mDataProvider;

    @Inject
    public SearchPresenter() {
    }

    @Override
    public void search(int pageIndex, int pageSize, final String keywords,String category,String type, final boolean loadMore) {
        doRequest(mDataProvider.provideObservable(DataProvider.OperationType.GET_PRODUCT_LIST, type,category, keywords, pageIndex, pageSize),
                SHOW_CANCELABLE_DIALOG, new UiCallback<List<PictureInfo>>() {
                    @Override
                    public void onSuccess(List<PictureInfo> list) {
                        mView.addHistoryTag(keywords);
                        mView.refreshList(list, loadMore);
                    }

                    @Override
                    public void onFailure(String msg) {
                        mView.showToast("信息获取失败");
                    }

                    @Override
                    public void onNoAuthenticated() {

                    }
                });
    }
}
