package com.goose.app.ui.main;

import com.goose.app.data.DataProvider;
import com.goose.app.model.CategoryInfo;
import com.taoyr.app.base.BasePresenter;
import com.taoyr.app.ifs.UiCallback;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by taoyr on 2018/1/5.
 */

public final class MainPresenter extends BasePresenter<MainContract.View>
        implements MainContract.Presenter {

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

    /**
     * BasePresenter封装了基本的功能，比如takeView, dropView，网络请求的处理，请求的保存（CompositeDisposable），
     * 以及取消。以及Http请求结果的统一处理（错误处理，消息提示，暴露最基本的onSuccess, onFailure）。我们
     * 集成BasePresenter，就可以把这些共通的方法拿来己用了。但是同样的，我们需要为LoginPresenter的constructor加
     *
     * @ Inject注解，因为实际使用的时候，创建并提供给LoginActivity的是LoginPresenter实体
     */
    @Inject
    public MainPresenter() {
    }

    @Override
    public void getCategoryList(final String type) {
        doRequest(mDataProvider.provideObservable(DataProvider.OperationType.GET_CATEGORY_LIST, type),
                SHOW_CANCELABLE_DIALOG, new UiCallback<List<CategoryInfo>>() {
                    @Override
                    public void onSuccess(List<CategoryInfo> list) {
                        mView.getCategoryListOnUi(list, type);
                    }

                    @Override
                    public void onFailure(String msg) {
                        mView.showToast("分类获取失败");
                    }

                    @Override
                    public void onNoAuthenticated() {

                    }
                });
    }
}
