package com.taoyr.app.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.taoyr.app.R;
import com.taoyr.app.rxbus.RxBus;
import com.taoyr.widget.widgets.TopBar;
import com.taoyr.widget.widgets.dialog.AlertDialog;
import com.taoyr.widget.widgets.dialog.ConfirmDialog;
import com.taoyr.widget.widgets.dialog.LoadingDialog;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Created by taoyr on 2018/1/5.
 *
 * 如果页面非常简单，不需要使用MVP和依赖注入框架，则继承这个Activity。该类除了没有注入功能外，与
 * BaseActivity的功用一致。
 */

public abstract class SimpleActivity extends AppCompatActivity implements IBaseView {

    protected Handler mHandler;
    protected Context mContext;

    private LoadingDialog mLoadingDialog;
    private AlertDialog mAlertDialog;
    private ConfirmDialog mConfirmDialog;

    /**
     * 注意这个CompositeDisposable与Presenter中的区别开。这里是用于RxBus消息传递用的。而Presenter中
     * 的是用于HTTP请求。
     */
    private CompositeDisposable mDisposables;
    private Unbinder mUnbinder;
    private RxBus mBus;
    protected BaseApplication mApplication;
    protected SharedPreferences mSp;
    protected TopBar mTopBar;
    private ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全局去掉默认的ActionBar（我们会使用自定义的）
        // supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        if (getLayoutResID() > 0) {
            setContentView(getLayoutResID());
        }
        // 使用butter knife注解（Fragment需要单独用？）
        mUnbinder = ButterKnife.bind(this);
        mBus = BaseApplication.getInstance().mRxbus;
        mApplication = BaseApplication.getInstance();
        mSp = PreferenceManager.getDefaultSharedPreferences(mApplication);
        mHandler = new Handler();
        mContext = this;
        mDisposables = new CompositeDisposable();
        initTopBar();

        // 沉浸式状态栏
        mImmersionBar = ImmersionBar.with(this);
        if (mTopBar != null) {
            // 注意：没有title bar的activity(MainActivity)，需要添加一个高度为0的top bar
            mImmersionBar.titleBarMarginTop(mTopBar); // 解决状态栏和布局顶部重叠问题，任选其一。最新增加的方法，更推荐使用
            //mImmersionBar.statusBarView(mTopBar); // 解决状态栏和布局顶部重叠问题，任选其一。需要在top bar上方加上一个height=0dp的View
        }
        mImmersionBar
                //.fitsSystemWindows(true) // 解决状态栏和布局重叠问题，任选其一，默认为false，当为true时一定要指定statusBarColor()，不然状态栏为透明色，还有一些重载方法
                .statusBarDarkFont(true, 0.2f) // 原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                .keyboardEnable(true)  // 解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)  // 单独指定软键盘模式
                .init();

        if (getIntent() != null) {
            handleIntent(getIntent());
        }
        initView();
    }

    /**
     * 一部分Activity并不需要处理intent，因此不将其声明为abstract
     */
    public void handleIntent(Intent intent) {};

    /**
     * Top bar只有Activity有。而且使用频率非常高，加个统一处理非常有必要。
     */
    @SuppressLint("WrongViewCast")
    private void initTopBar() {
        //mTopBar = findViewById(R.id.top_bar);
        mTopBar = getWindow().getDecorView().findViewById(R.id.top_bar);
        if (mTopBar != null) {
            mTopBar.setBackFunction(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            /*mTopBar.initialize("", new TopBarCallback() {
                @Override
                public void onBackBtnPressed() {
                    finish();
                }

                @Override
                public void onRightBtnPressed() {
                    // no-op
                }
            });*/
        }
    }

    /**
     * 外部不应直接操作mTopBar。封装的方法，进行了一些判空等safe操作。
     */
    public void setTopBarTitle(String title) {
        if (mTopBar != null) {
            if (!TextUtils.isEmpty(title)) {
                mTopBar.setTitle(title);
            }
            mTopBar.hideBackBtn(false);
        }
    }

    public void setTopBarFunction(String name, View.OnClickListener listener) {
        if (mTopBar != null) {
            mTopBar.setRightFunction(name, listener);
        }
    }

    public void setTopBarFunction(int resId, View.OnClickListener listener) {
        if (mTopBar != null) {
            mTopBar.setRightFunction(resId, listener);
        }
    }

    public void hideTopBarBack(boolean hideBack) {
        if (mTopBar != null) {
            mTopBar.hideBackBtn(hideBack);
        }
    }

    protected abstract int getLayoutResID();

    protected abstract void initView();

    @Override
    public void showToast(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void showLoadingDialog(boolean cancelable) {
        showLoadingDialog(cancelable, null);
    }

    public void showLoadingDialog(final boolean cancelable,
                                  final DialogInterface.OnCancelListener listener) {
        // Precaution: 当在activity中开启异步线程时，当工作进行完之前销毁activity，会抛出异常：
        // unable to add window, token not valid
        if (isFinishing()) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog == null) {
                    mLoadingDialog = new LoadingDialog(mContext);
                }
                mLoadingDialog.setCancelable(cancelable);
                if (cancelable && listener != null) {
                    mLoadingDialog.setOnCancelListener(listener);
                }
                mLoadingDialog.show();
            }
        });
    }

    @Override
    public void showAlertDialog(final String msg) {
        if (isFinishing()) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAlertDialog == null) {
                    mAlertDialog = new AlertDialog(mContext);
                }
                mAlertDialog.initialize(msg);
                mAlertDialog.show();
            }
        });
    }

    @Override
    public void showConfirmDialog(final String msg, final ConfirmDialog.Callback callback) {
        if (isFinishing()) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mConfirmDialog == null) {
                    mConfirmDialog = new ConfirmDialog(mContext);
                }
                mConfirmDialog.initialize(msg, callback);
                mConfirmDialog.show();
            }
        });
    }

    @Override
    public void dismissLoadingDialog() {
        // if (isFinishing()) return; // has leaked window decorview
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void dismissAlertDialog() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAlertDialog != null) {
                    mAlertDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void dismissConfirmDialog() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mConfirmDialog != null) {
                    mConfirmDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 避免忘记手动关闭dialog出现leaked window
        dismissLoadingDialog();
        dismissAlertDialog();
        dismissConfirmDialog();

        // 清除所有注册过的subscriber，避免内存溢出
        mDisposables.clear();

        mUnbinder.unbind();
        // 避免内存泄漏
        mImmersionBar.destroy();
    }

    public RxBus getRxBus() {
        return mBus;
    }

    /*public void sendEvent(Object o) {
        mBus.send(o);
    }*/

    public void subscribeEvent(Consumer consumer) {
        mDisposables.add(mBus.asFlowable()
                //.subscribeOn(AndroidSchedulers.mainThread()) // 事件消费者
                //.observeOn(AndroidSchedulers.mainThread()) // 事件生产者
                .subscribe(consumer));
    }

    /**
     * 双击返回键退出APP
     */
    private boolean mExitOnDoubleBack = false;
    private boolean isExitTriggeredOnce = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mExitOnDoubleBack) {
                exitBy2Click();//双击退出函数
                return false;
            }
        }
        //return false; // back key不响应
        return super.onKeyDown(keyCode, event);
    }

    public void setExitOnDoubleBack(boolean enable) {
        mExitOnDoubleBack = enable;
    }

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExitTriggeredOnce == false) {
            isExitTriggeredOnce = true;
            Toast.makeText(this, "双击退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExitTriggeredOnce = false;
                }
            }, 2000);
        } else {
            //BaseApplication.isGuest = true;
            BaseApplication.getInstance().exit();
        }
    }

    @Override
    public Context getBaseViewContext() {
        return mContext;
    }
}
