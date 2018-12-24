package com.goose.app.ui.zhibo;

import com.goose.app.data.DataProvider;
import com.goose.app.model.PictureDetailInfo;
import com.goose.app.model.PictureInfo;
import com.taoyr.app.base.BasePresenter;
import com.taoyr.app.ifs.UiCallback;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by taoyr on 2018/1/5.
 */

public final class ZhiboDetailPresenter extends BasePresenter<ZhiboDetailContract.View>
        implements ZhiboDetailContract.Presenter {

    /**
     * 使用泛型, 父类的mView会自动的被转成LoginContract.View。使用起来会很方便
     */
    // Dagger does not support injection into private fields
    //@Inject
    //LoginContract.View mView;

    /*@Inject
    IApiService mService;*/

    @Inject
    DataProvider mDataProvider;

    @Inject
    public ZhiboDetailPresenter() {
    }


    @Override
    public void operateProduct(String id, final String type) {
        doRequest(mDataProvider.provideObservable(DataProvider.OperationType.OPERATE_PRODUCT, id, type),
                SHOW_CANCELABLE_DIALOG, new UiCallback<PictureDetailInfo>() {
                    @Override
                    public void onSuccess(PictureDetailInfo info) {
                        mView.operateProductOnUi(type);
                    }

                    @Override
                    public void onFailure(String msg) {
                        mView.showToast("操作失败");
                    }

                    @Override
                    public void onNoAuthenticated() {
                        mView.goLogin();
                    }
                });
    }

}
