package com.goose.app.ui.video;

import com.goose.app.data.DataProvider;
import com.goose.app.model.PictureDetailInfo;
import com.goose.app.model.PictureInfo;
import com.goose.app.ui.picture.PictureDetailContract;
import com.taoyr.app.base.BasePresenter;
import com.taoyr.app.ifs.UiCallback;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by taoyr on 2018/1/5.
 */

public final class VideoDetailPresenter extends BasePresenter<VideoDetailContract.View>
        implements VideoDetailContract.Presenter {

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
    public VideoDetailPresenter() {
    }

    @Override
    public void getProductDetail(String id) {
        doRequest(mDataProvider.provideObservable(DataProvider.OperationType.GET_PRODUCT_DETAIL, id),
                SHOW_CANCELABLE_DIALOG, new UiCallback<PictureDetailInfo>() {
                    @Override
                    public void onSuccess(PictureDetailInfo info) {
                        mView.getProductDetailOnUi(info);
                    }

                    @Override
                    public void onFailure(String msg) {
                        mView.showToast("图片详情获取失败");
                    }
                });
    }

    @Override
    public void getRecommendProductList(String category, int pageIndex, int pageSize) {
        doRequest(mDataProvider.provideObservable(DataProvider.OperationType.GET_PRODUCT_LIST,
                DataProvider.DATA_TYPE_VIDEO, category, "", pageIndex, pageSize),
                SHOW_CANCELABLE_DIALOG, new UiCallback<List<PictureInfo>>() {
                    @Override
                    public void onSuccess(List<PictureInfo> list) {
                        mView.getRecommendProductListOnUi(list);
                    }

                    @Override
                    public void onFailure(String msg) {
                        mView.showToast("列表信息获取失败");
                    }
                });
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
                });
    }
}
