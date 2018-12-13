package com.goose.app.ui.personal;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.gson.JsonObject;
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
public final class SignUpPersonalPresenter extends BasePresenter<SignUpPersonalContract.View>
        implements SignUpPersonalContract.Presenter {

    @Inject
    DataProvider mDataProvider;

    @Inject
    public SignUpPersonalPresenter() {
    }

    @Override
    public void updateProfile(final String id, final String displayName, final String avatar) {
        doRequest(mDataProvider.provideObservable(DataProvider.OperationType.UPDATE_USER, id, displayName, avatar),
                SHOW_CANCELABLE_DIALOG, new UiCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        mView.showToast("保存用户信息成功");
                        //UserDetailInfo info = GooseApplication.getInstance().getUserInfo();
//                        if (info != null) {
//                            // 更新本地缓存
//                            info.avatar = avatar;
//                            info.displayName = displayName;
//                        }

                        mView.updateProfileOnUi();
                    }

                    @Override
                    public void onFailure(String msg) {
                        mView.showToast("保存用户信息失败");
                    }

                    @Override
                    public void onNoAuthenticated() {
                        mView.goLogin();
                    }
                });
    }

    @Override
    public void login(String username, String password) {
        /*doRequest(mService.login(new LoginVo(username, password)), SHOW_CANCELABLE_DIALOG, new UiCallback<String>() {
            @Override
            public void onSuccess(final String token) {
                if (!TextUtils.isEmpty(token)) {
                    // 设置用户令牌
                    BaseApplication.getInstance().setToken(token);

                    // 获取用户详情
                    doRequest(mService.getUserDetail(), SHOW_CANCELABLE_DIALOG, new UiCallback<UserDetailInfo>() {
                        @Override
                        public void onSuccess(UserDetailInfo info) {
                            if (info != null) {
                                BaseApplication.getInstance().setUser(info);
                                mView.loginOnUi();
                            }
                        }
                    });
                }
            }
        });*/
    }

    @Override
    public void uploadHeadImage(Bitmap bitmap) {
        doRequest(mDataProvider.provideObservable(DataProvider.OperationType.UPLOAD_FILE, bitmap),
                SHOW_CANCELABLE_DIALOG, new UiCallback<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject jsonObject) {
                        if (jsonObject != null) {
                            String url = jsonObject.get("url").getAsString();
                            if (!TextUtils.isEmpty(url)) {
                                mView.uploadHeadImageOnUi(url);
                                mView.showToast("头像上传成功");
                                return;
                            }
                        }

                        mView.showToast("头像上传失败");
                    }

                    @Override
                    public void onFailure(String msg) {
                        mView.showToast("头像上传失败");
                    }

                    @Override
                    public void onNoAuthenticated() {
                        mView.goLogin();
                    }
                });
    }
}
