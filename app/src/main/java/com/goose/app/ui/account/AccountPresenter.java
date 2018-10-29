package com.goose.app.ui.account;


import com.goose.app.data.DataProvider;
import com.goose.app.model.sign.LastSignInfo;
import com.taoyr.app.base.BasePresenter;
import com.taoyr.app.ifs.UiCallback;

import javax.inject.Inject;

/**
 * Created by taoyr on 2018/1/5.
 */

public final class AccountPresenter extends BasePresenter<AccountContract.View>
        implements AccountContract.Presenter {

    @Inject
    DataProvider mDataProvider;

    @Inject
    public AccountPresenter() {
    }

    @Override
    public void getLastSignInfo() {
        doRequest(mDataProvider.provideObservable(DataProvider.OperationType.GET_LAST_SIGN_INFO),
                SHOW_CANCELABLE_DIALOG, new UiCallback<LastSignInfo>() {
                    @Override
                    public void onSuccess(LastSignInfo info) {
                        mView.getLastSignInfoOnUi(info);
                    }
                    @Override
                    public void onNoAuthenticated() {
                        mView.goLogin();
                    }
                    @Override
                    public void onFailure(String msg) {
                        mView.showToast("签到获取失败");
                    }
                });
    }

    @Override
    public void sign() {
        doRequest(mDataProvider.provideObservable(DataProvider.OperationType.SIGN),
                SHOW_CANCELABLE_DIALOG, new UiCallback<LastSignInfo>() {
                    @Override
                    public void onSuccess(LastSignInfo info) {
                        mView.signOnUi();
                    }

                    @Override
                    public void onFailure(String msg) {
                        mView.showToast("签到失败");
                    }

                    @Override
                    public void onNoAuthenticated() {
                        mView.goLogin();
                    }
                });
    }
}
