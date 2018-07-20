package com.goose.app.ui.main;

import com.goose.app.model.CategoryInfo;
import com.taoyr.app.base.IBasePresenter;
import com.taoyr.app.base.IBaseView;

import java.util.List;


/**
 * Created by taoyr on 2018/1/5.
 */

public interface MainContract {

    interface View extends IBaseView<Presenter> {
        void getCategoryListOnUi(List<CategoryInfo> list, String type);
    }

    interface Presenter extends IBasePresenter<View> {
        void getCategoryList(String type);
    }
}
