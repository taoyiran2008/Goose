package com.goose.app.ui.favor;


import com.goose.app.data.DataProvider;
import com.goose.app.model.PictureInfo;
import com.taoyr.app.base.BasePresenter;
import com.taoyr.app.ifs.UiCallback;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by taoyr on 2018/1/5.
 */

public final class FavorListPresenter extends BasePresenter<FavorListContract.View>
        implements FavorListContract.Presenter {

    @Inject
    DataProvider mDataProvider;

    @Inject
    public FavorListPresenter() {
    }

    @Override
    public void getFavorList(int pageIndex, int pageSize) {
        doRequest(mDataProvider.provideObservable(DataProvider.OperationType.GET_FAVOR_LIST, DataProvider.DATA_TYPE_PICTURE, pageIndex, pageSize),
                SHOW_CANCELABLE_DIALOG, new UiCallback<List<PictureInfo>>() {
                    @Override
                    public void onSuccess(List<PictureInfo> list) {
                        mView.refreshList(list);
                    }

                    @Override
                    public void onFailure(String msg) {
                        mView.showToast("收藏列表获取失败");
                    }

                    @Override
                    public void onNoAuthenticated() {
                        mView.goLogin();
                    }
                });
    }
}
