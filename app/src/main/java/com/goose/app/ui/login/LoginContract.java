package com.goose.app.ui.login;

import com.taoyr.app.base.IBasePresenter;
import com.taoyr.app.base.IBaseView;

/**
 * Created by taoyr on 2018/1/5.
 */

public interface LoginContract {

    interface View extends IBaseView<Presenter> {
        //void shakeInputbox(); // 输入错误信息时，抖动输入框
        void loginOnUi();
    }

    interface Presenter extends IBasePresenter<View> {
        void login(String username, String password);
    }
}
