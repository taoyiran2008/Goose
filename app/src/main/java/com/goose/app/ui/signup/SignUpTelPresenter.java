package com.goose.app.ui.signup;

import com.goose.app.GooseApplication;
import com.goose.app.data.DataProvider;
import com.taoyr.app.base.BasePresenter;
import com.taoyr.app.ifs.UiCallback;
import com.taoyr.app.model.UserDetailInfo;

import javax.inject.Inject;

/**
 * Created by taoyr on 2018/1/5.
 */

/*public final class LoginPresenter
        implements LoginContract.Presenter {*/
public final class SignUpTelPresenter extends BasePresenter<SignUpTelContract.View>
        implements SignUpTelContract.Presenter {

    @Inject
    DataProvider mDataProvider;

    @Inject
    public SignUpTelPresenter() {
    }

    @Override
    public void sendCode(final String mobile) {
    }

    @Override
    public void register(String username, String password,String shareCode) {
        doRequest(mDataProvider.provideObservable(DataProvider.OperationType.REGISTER, username, password,shareCode),
                SHOW_CANCELABLE_DIALOG, new UiCallback<UserDetailInfo>() {
                    @Override
                    public void onSuccess(final UserDetailInfo info) {
                        /*if (info != null) {
                            // 保存用户信息（包括用户令牌token）
                            GooseApplication.getInstance().setUserInfo(info);
                            mView.showToast("注册成功");
                            mView.registerOnUi();
                        }*/
                        // 保存用户信息（包括用户令牌token）
                        GooseApplication.getInstance().setUserInfo(info);
                        mView.showToast("注册成功");
                        mView.registerOnUi();
                    }

                    @Override
                    public void onFailure(String msg) {
                    }

                    @Override
                    public void onNoAuthenticated() {

                    }
                });
    }
}
