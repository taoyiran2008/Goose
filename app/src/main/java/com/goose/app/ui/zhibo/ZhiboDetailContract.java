package com.goose.app.ui.zhibo;

import com.goose.app.model.PictureDetailInfo;
import com.goose.app.model.PictureInfo;
import com.taoyr.app.base.IBasePresenter;
import com.taoyr.app.base.IBaseView;

import java.util.List;


/**
 * Created by taoyr on 2018/1/5.
 */

public interface ZhiboDetailContract {

    interface View extends IBaseView<Presenter> {
        void getProductDetailOnUi(PictureDetailInfo list);

        void operateProductOnUi(String type);

        void goLogin();
    }

    interface Presenter extends IBasePresenter<View> {
        void getProductDetail(String id);


        void operateProduct(String id, String type);

    }
}
