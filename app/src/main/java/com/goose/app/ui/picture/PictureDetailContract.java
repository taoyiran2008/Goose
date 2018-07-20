package com.goose.app.ui.picture;

import com.goose.app.model.PictureDetailInfo;
import com.taoyr.app.base.IBasePresenter;
import com.taoyr.app.base.IBaseView;


/**
 * Created by taoyr on 2018/1/5.
 */

public interface PictureDetailContract {

    interface View extends IBaseView<Presenter> {
        void getProductDetailOnUi(PictureDetailInfo list);
        void operateProductOnUi(String type);
    }

    interface Presenter extends IBasePresenter<View> {
        void getProductDetail(String id);
        void operateProduct(String id, String type);
    }
}
