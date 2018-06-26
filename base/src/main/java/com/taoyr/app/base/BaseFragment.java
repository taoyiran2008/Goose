package com.taoyr.app.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taoyr.app.R;
import com.taoyr.app.rxbus.RxBus;
import com.taoyr.app.utility.LogMan;
import com.taoyr.widget.widgets.dialog.ConfirmDialog;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Created by taoyr on 2018/1/11.
 * 本想的是UI全部由BaseActivity去承载，这样就不用创建一个BaseFragment，再去重复的实现一遍通用的UI
 * 接口(showToast, showAlertDialog等)。而我们只需要在Fragment中通过BaseActivity)getActivity，并获得
 * BaseActivity的Presenter，并在Fragment生命周期开始时，使用mPresenter.takeView(getActivity())将
 * BaseActivity作为View层传进去。
 *
 * 但还是把问题想简单了。因为一个Fragment虽然依附在Activity上，相对于Activity，它有着自己独立的生命周期。
 * 而且一个Activity可以使用ViewPager等方式，加载多个不同的Fragment。而且每个Fragment都应该有自己独立
 * 业务逻辑，UI显示也因画面和控件的差异大相径庭。因此Fragment应该有自己独立的Presenter。在功能上，与
 * Activity是平级的关系。
 *
 * 当然要避免重复的实现共通的UI接口，我们可以在方法体中直接使用BaseActivity的实现。
 */

public abstract class BaseFragment<P extends IBasePresenter> extends Fragment implements IBaseView<P> {

    protected Handler mHandler;
    protected Context mContext;

    @Inject
    protected BaseApplication mApplication;

    @Inject
    protected P mPresenter;

    @Inject
    protected RxBus mBus;

    protected View mRoot;
    //protected BaseActivity mActivity;
    protected IBaseView mActivity;

    // 下面的成员无需暴露给其子类使用
    private CompositeDisposable mDisposables;
    private Unbinder mUnbinder;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getLayoutResID() > 0) {
            mRoot = LayoutInflater.from(getActivity()).inflate(getLayoutResID(), null);
        } else {
            // 默认Fragment布局
            mRoot = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_textview, null);
        }

        //  mUnbinder = ButterKnife.bind(mRoot); // view会找不到，报NPE
        mUnbinder = ButterKnife.bind(this, mRoot);
        LogMan.logDebug("onCreateView");
        mActivity = (IBaseView) getActivity();
        mHandler = new Handler();
        mContext = getContext();
        mDisposables = new CompositeDisposable();
        mPresenter.takeView(this);
        initView();
        return mRoot;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUnbinder.unbind();
        mPresenter.dropView();
        mDisposables.clear();
    }

    protected abstract void initView();

    protected abstract int getLayoutResID();

    @Override
    public void showToast(String msg) {
        mActivity.showToast(msg);
    }

    @Override
    public void showLoadingDialog(boolean cancelable) {
        mActivity.showLoadingDialog(cancelable);
    }

    @Override
    public void showLoadingDialog(boolean cancelable, DialogInterface.OnCancelListener listener) {
        mActivity.showLoadingDialog(cancelable, listener);
    }

    @Override
    public void showAlertDialog(String msg) {
        mActivity.showAlertDialog(msg);
    }

    @Override
    public void showConfirmDialog(String msg, ConfirmDialog.Callback callback) {
        mActivity.showConfirmDialog(msg, callback);
    }

    @Override
    public void dismissLoadingDialog() {
        mActivity.dismissLoadingDialog();
    }

    @Override
    public void dismissAlertDialog() {
        mActivity.dismissAlertDialog();
    }

    @Override
    public void dismissConfirmDialog() {
        mActivity.dismissConfirmDialog();
    }

    public RxBus getRxBus() {
        return mBus;
    }

    public void sendEvent(Object o) {
        mBus.send(o);
    }

    public void subscribeEvent(Consumer consumer) {
        mDisposables.add(mBus.asFlowable()
                //.subscribeOn(AndroidSchedulers.mainThread()) // 事件消费者
                //.observeOn(AndroidSchedulers.mainThread()) // 事件生产者
                .subscribe(consumer));
    }

    @Override
    public Context getBaseViewContext() {
        return getContext();
    }
}
