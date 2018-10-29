package com.goose.app.ui.account;


import com.goose.app.model.sign.LastSignInfo;
import com.taoyr.app.base.IBasePresenter;
import com.taoyr.app.base.IBaseView;

/**
 * Created by taoyr on 2018/1/5.
 */

public interface AccountContract {

    interface View extends IBaseView<Presenter> {
        void getLastSignInfoOnUi(LastSignInfo info);
        void signOnUi();
        void  goLogin();
    }

    interface Presenter extends IBasePresenter<View> {
        void getLastSignInfo();
        void sign();
    }
}
