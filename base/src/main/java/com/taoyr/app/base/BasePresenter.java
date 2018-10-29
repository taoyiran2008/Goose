package com.taoyr.app.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.taoyr.app.ifs.UiCallback;
import com.taoyr.app.model.HttpResultInfo;
import com.taoyr.app.utility.LogMan;
import com.taoyr.app.utility.NetUtils;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * Created by taoyr on 2018/1/5.
 * <p>
 * 一个空壳。一些继承自BaseActivity的Activity，本身没有业务逻辑需要放到Presenter中，则使用这个壳子，
 * 避免注入报错。
 * <p>
 * 设计原则之一：Activity中不包含任何逻辑相关的代码，因此把设置网络请求时请求（生产者）和监听（消费者）
 * 的thread的逻辑封装到Presenter中。这里不能直接使用IBasePresenter<IBaseView>，这样会导致的问题是，比如
 * 在LoginPresenter中，因为LoginContract.Presenter extends IBasePresenter<LoginContract.View>，有个
 * 隐形的泛型声明，导致extends和implement两个继承关系中用到的泛型不一致。
 * <p>
 * 使用该类的Activity，必须把泛型指定清楚，比如MainActivity extends BaseActivity<IBasePresenter<IBaseView>>
 * com.tianfeng.app.base.BasePresenter cannot be provided without an @Provides-annotated method. This
 * type supports members injection but cannot be implicitly provided
 */

public class BasePresenter<T extends IBaseView> implements IBasePresenter<T> {

    @Inject
    //protected IApiService mService;
    protected Retrofit mRetrofit;

    // 用限定protected，使子类可以无障碍访问
    protected T mView;

    //private Disposable mDisposable; // 仅作为一个临时变量，因为每发起一次新的请求，就会改变
    private CompositeDisposable mDisposables;

    private AtomicInteger mRequestCounter = new AtomicInteger(0);

    @Inject
    public BasePresenter() {
        mDisposables = new CompositeDisposable();
    }

    @Override
    public void takeView(T view) {
        mView = view;
    }

    @Override
    public void dropView() {
        mDisposables.clear();
    }

    /**
     * 发送一个GET/POST请求。
     * 这里只考虑了一种最简单的场景Case，也就是发起一次http请求，将原始数据转换为我们需要的类型，并
     * 对结果进行统一处理。
     * TODO: 对于有返回原始数据ResponseBody的，对请求数据filter，flatMap，zip等特殊处理的，另外的增加统一处理的方法。
     */
    protected void doRequest(final Observable<HttpResultInfo> request, final int cancelable, final UiCallback callback) {
        // 检查网络连接
        if (!NetUtils.isConnected(BaseApplication.getContext())) {
            mView.showToast("网络未连接，请检查您的网络设置");
            return;
        }

        if (request == null) {
            String errorMessage = "request is null";
            mView.showToast(errorMessage);
            if (callback != null) {
                callback.onFailure(errorMessage);
            }

            return;
        }

        request.compose(this.<HttpResultInfo>setThread()).subscribe(new Observer<HttpResultInfo>() {
            @Override
            public void onSubscribe(final Disposable d) {
                //mDisposable = d;
                mRequestCounter.incrementAndGet();
                mDisposables.add(d);
                /**
                 * 这里使用Dialog会发现，设计有些问题。因为考虑到Dialog生命周期的管理，我们在BaseActivity中
                 * 只保存了一个LoadingDialog实例，方便在onDestroy中进行销毁。然而可能会有，一个画面中，
                 * 同时发起了两次请求，一次cancelable=false，一次cancelable=true。然而它们都是使用的同一个
                 * Dialog，因此当我们取消Dialog后，之前cancelable=false启动的Dialog也没了。
                 *
                 * 实际上，发起的请求90%是可取消/静默的的，因此我们假定不会出现前面描述的这种混合模式。
                 * 并且设定为，取消dialog的动作会cancel前面所有的request。
                 */
                if (cancelable != SHOW_NO_DIALOG) {
                    mView.showLoadingDialog(cancelable == SHOW_CANCELABLE_DIALOG,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    // 取消单个请求
                                    //mDisposable.dispose();
                                    //mDisposables.remove(d);
                                    mDisposables.clear();
                                }
                            });
                }
            }

            @Override
            public void onNext(HttpResultInfo httpResultInfo) {
                if (true) {
                    if (callback != null && httpResultInfo != null) {
                        // UiCallback的泛型类型如果与httpResultInfo.datas的实际类型不匹配，会报错
                        // java.lang.ClassCastException: java.lang.String cannot be cast to com.goose.app.model.sign.LastSignInfo
                        try {
                            callback.onSuccess(httpResultInfo.datas);
                        } catch (Exception e) {
                            LogMan.logError(e);
                            callback.onSuccess(null);
                        }
                    }
                } else if ("401".equals(httpResultInfo.code)) {
                    //跳转到登录页
                    callback.onNoAuthenticated();

                } else {
                    /*mView.showToast("errorCode: " + httpResultInfo.resultCode
                            + ", errorMsg: " + httpResultInfo.resultDesc);*/
                    String errorMessage = "网络请求异常";
                    if (!TextUtils.isEmpty(httpResultInfo.message)) {
                        errorMessage = httpResultInfo.message;
                    }
                    mView.showToast(errorMessage);
                    callback.onFailure(errorMessage);
                }

                dismissLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                String errorMessage = "未知的错误";
                mView.showToast(errorMessage);
                if (callback != null) {
                    callback.onFailure(errorMessage);
                }

                dismissLoadingDialog();
            }

            @Override
            public void onComplete() {
                dismissLoadingDialog();
            }
        });
    }

    private void dismissLoadingDialog() {
        int requestCount = mRequestCounter.decrementAndGet();
        if (requestCount <= 0) {
            // 重置计数器
            mRequestCounter = new AtomicInteger(0);
            mView.dismissLoadingDialog();
        }
    }

    private static <T> ObservableTransformer<T, T> setThread() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    protected <T> T getApiService(Class<T> clz) {
        return mRetrofit.create(clz);
    }
}
