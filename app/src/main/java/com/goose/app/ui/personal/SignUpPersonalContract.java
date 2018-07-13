package com.goose.app.ui.personal;

import android.graphics.Bitmap;

import com.taoyr.app.base.IBasePresenter;
import com.taoyr.app.base.IBaseView;


/**
 * Created by taoyr on 2018/1/5.
 */

public interface SignUpPersonalContract {

    interface View extends IBaseView<Presenter> {
        void updateProfileOnUi();
        void loginOnUi();
        void uploadHeadImageOnUi(String url);
    }

    interface Presenter extends IBasePresenter<View> {
        void updateProfile(String id, String displayName, String avatar);
        void uploadHeadImage(Bitmap bitmap);
        void login(String username, String password);
    }
}
