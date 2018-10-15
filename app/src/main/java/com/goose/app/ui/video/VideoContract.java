package com.goose.app.ui.video;

import com.goose.app.model.BannerInfo;
import com.goose.app.model.PictureInfo;
import com.taoyr.app.base.IBasePresenter;
import com.taoyr.app.base.IBaseView;

import java.util.List;


/**
 * Created by taoyr on 2018/1/5.
 */

public interface VideoContract {

    interface View extends IBaseView<Presenter> {
        void getBannerListOnUi(List<BannerInfo> list);
        void getProductListOnUi(List<PictureInfo> list);
    }

    interface Presenter extends IBasePresenter<View> {
        void getBannerList();
        void getProductList(String category, int pageIndex, int pageSize);
    }
}
