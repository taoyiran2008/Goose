package com.goose.app.ui.signup;


import com.taoyr.app.base.IBasePresenter;
import com.taoyr.app.base.IBaseView;

/**
 * Created by taoyr on 2018/1/5.
 */

public interface SignUpTelContract {

    interface View extends IBaseView<Presenter> {
        void sendCodeOnUi();
        void registerOnUi();
    }

    interface Presenter extends IBasePresenter<View> {
        void sendCode(String mobile);
        void register(String username, String password,String shareCode);
    }
}
