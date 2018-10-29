package com.goose.app.ui.favor;


import com.goose.app.model.PictureInfo;
import com.taoyr.app.base.IBasePresenter;
import com.taoyr.app.base.IBaseView;

import java.util.List;

/**
 * Created by taoyr on 2018/1/5.
 */

public interface FavorListContract {

    interface View extends IBaseView<Presenter> {
        void refreshList(List<PictureInfo> list);
        void goLogin();
    }

    interface Presenter extends IBasePresenter<View> {
        void getFavorList(int pageIndex, int pageSize);
    }
}
