package com.goose.app.ui.login;

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
public final class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter {

    /**
     * 使用泛型, 父类的mView会自动的被转成LoginContract.View。使用起来会很方便
     */
    // Dagger does not support injection into private fields
    //@Inject
    //LoginContract.View mView;

    /*@Inject
    IApiService mService;*/

    @Inject
    DataProvider mDataProvider;

    /**
     * BasePresenter封装了基本的功能，比如takeView, dropView，网络请求的处理，请求的保存（CompositeDisposable），
     * 以及取消。以及Http请求结果的统一处理（错误处理，消息提示，暴露最基本的onSuccess, onFailure）。我们
     * 集成BasePresenter，就可以把这些共通的方法拿来己用了。但是同样的，我们需要为LoginPresenter的constructor加
     *
     * @ Inject注解，因为实际使用的时候，创建并提供给LoginActivity的是LoginPresenter实体
     */
    @Inject
    public LoginPresenter() {
    }

    @Override
    public void login(String username, String password) {
        // 天风项目上使用的复杂的泛型检查，实际上是没有用的。
        doRequest(mDataProvider.provideObservable(DataProvider.OperationType.LOGIN, username, password),
                SHOW_CANCELABLE_DIALOG, new UiCallback<UserDetailInfo>() {
                    @Override
                    public void onSuccess(final UserDetailInfo info) {
                        if (info != null) {
                            // 持久化保存token到本地SP中，实现下次免登陆
                            GooseApplication.getInstance().setToken(info.token);

                            // 保存用户信息（包括用户令牌token）
                            GooseApplication.getInstance().setUserInfo(info);
                            mView.showToast("登录成功");
                            mView.loginOnUi();
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                    }
                });
    }

/*    @Override
    public void takeView(LoginContract.View view) {
        super(view);
        mView = view;
    }

    @Override
    public void dropView() {
    }*/
}
