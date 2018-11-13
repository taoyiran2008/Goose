package com.goose.app.ui.search;


import com.goose.app.model.PictureInfo;
import com.taoyr.app.base.IBasePresenter;
import com.taoyr.app.base.IBaseView;

import java.util.List;

/**
 * Created by taoyr on 2018/1/5.
 */

public interface SearchContract {

    interface View extends IBaseView<Presenter> {
        void addHistoryTag(String keywords);
        void refreshList(List<PictureInfo> list, boolean loadMore);
    }

    interface Presenter extends IBasePresenter<View> {
        void search(int pageIndex, int pageSize, String keywords,String mCategory,String type, boolean loadMore);
    }
}
