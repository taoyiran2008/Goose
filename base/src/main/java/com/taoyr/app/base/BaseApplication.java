package com.taoyr.app.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.taoyr.app.rxbus.RxBus;
import com.taoyr.app.utility.LogMan;

import java.lang.ref.WeakReference;
import java.util.Stack;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

/**
 * We create a custom {@link Application} class that extends  {@link DaggerApplication}.
 * We then override applicationInjector() which tells Dagger how to make our @Singleton Component
 * We never have to call `component.inject(this)` as {@link DaggerApplication} will do that for us.
 */

public abstract class BaseApplication extends DaggerApplication {

    private BaseActivityManager mBaseActivityManager;
    private static BaseApplication sInstance;
    private static Context sContext;

    @Inject
    public RxBus mRxbus;

    @Inject
    public Gson mGson;

    @Inject
    public SharedPreferences mSp;

    /*全局数据*/
    String token;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mBaseActivityManager = new BaseActivityManager();

        // 集成ShareSDK后，apk体积从22M增加到23M（1M以内）
        //MobSDK.init(this);
        //ShareUtils.init(this);

        // Activity 生命周期管理
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                mBaseActivityManager.addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                // 正常情况下使用popActivity没毛病，但是遇到CLEAR TOP或者屏幕反转事件，导致
                // Activity重建，新Activity的onCreate会先被调用，旧Activity的onDestroy会在其之后，
                // 导致Stack中保留的仍然是旧的Activity实例的引用。因此需要指定activity删除
                // popActivity();
                mBaseActivityManager.removeActivity(activity);
            }
        });
    }

    public BaseActivityManager getBaseActivityManager() {
        return mBaseActivityManager;
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return getAndroidInjector();
    }

    protected abstract AndroidInjector<? extends DaggerApplication> getAndroidInjector();

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static BaseApplication getInstance() {
        return sInstance;
    }

    public static Context getContext() {
        if (sContext == null) {
            sContext = getInstance().getApplicationContext();
        }

        return sContext;
    }

    public void exit() {
        // System.exit(0);
        mBaseActivityManager.finishAllActivity();
        // 已经不存在Activity了，才可以调用这个系统接口，彻底终止JVM实例（不然会AMS会自动重建）
        // System.exit(0); // 调用这个方法，会导致SharedPreference中的historyList内容被清空
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * you can restart your process through service or broadcast
     * 调用该方法，应用程序终止，所有restartProcess后面的代码将不再执行
     */
    /*public void restartProcess() {
        // you can send service or broadcast intent to restart your process
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);

        exit();
    }

    public void relogin() {
        //Toast.makeText(getContext(), "用户未登录或者会话已经过期，请重新登陆！", Toast.LENGTH_LONG).show();
        mBaseActivityManager.finishAllActivity();
        setUser(null);
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void showLoginDialog() {
        Intent intent = new Intent(getApplicationContext(), LoginDialogActivity.class);
        // android.util.AndroidRuntimeException: Calling startActivity() from outside of an Activity
        // context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }

    public void showRestartDialog() {
        *//*ConfirmDialog dialog = new ConfirmDialog(context);

        dialog.initialize(context.getString(R.string.hotfix_patch_success), new ConfirmDialog.Callback() {
            @Override
            public void onConfirm() {
                restartProcess();
            }

            @Override
            public void onCancel() {
            }
        });
        dialog.show();*//*
        Intent intent = new Intent(getApplicationContext(), RestartDialogActivity.class);
        // android.util.AndroidRuntimeException: Calling startActivity() from outside of an Activity
        // context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }*/

    public static class BaseActivityManager {

        private static Stack<WeakReference<Activity>> mActivityStack;

        /*{
            mActivityStack = new Stack<>();
        }*/

        /**
         * 添加Activity到堆栈
         */
        public void addActivity(Activity activity) {
            LogMan.logDebug("addActivity");
            if (mActivityStack == null) {
                mActivityStack = new Stack<>();
            }
            // AMS中通过堆栈的数据结构管理Activity实例，当启动模式为standard或者singleTop，相同
            // Activity可能创建多个实例，因此不能在这里简单的认为一个Activity类仅需要管理一个实例（单例）
            // 除非它的启动方式声明为单例(singleInstance)
            // if (mActivityStack.get(i).getClassName().equals(activity.getClass())) {
            // if (mActivityStack.contains(activity)) {
            // 保证对于某个Activity，窗口管理栈中最多只保留其一个引用
            //    LogMan.logDebug(activity.getClass().getSimpleName()
            //            + "has already been in the stack");
            //    return;
            // }
            mActivityStack.push(new WeakReference<Activity>(activity));
        }

        /**
         * 移除并获取当前Activity（堆栈中最后一个压入的）
         */
        public Activity popActivity() {
            LogMan.logDebug("popActivity");
            if (!mActivityStack.isEmpty()) {
                Activity activity = mActivityStack.pop().get();
                return activity;
            }
            return null;
        }

        public void removeActivity(Activity activity) {
            LogMan.logDebug("removeActivity");
            if (!mActivityStack.isEmpty()) {
                mActivityStack.remove(activity);
            }
        }

        public void finishNavActivity() {
            /*for (WeakReference<Activity> activity : mActivityStack) {
                if (activity.get() instanceof NavActivity) {
                    LogMan.logDebug("finish nav activity");
                    mActivityStack.remove(activity);
                    activity.get().finish();
                    break;
                }
            }*/
        }

        /**
         * 结束当前Activity（堆栈中最后一个压入的）
         */
        public void finishCurrentActivity() {
            LogMan.logDebug("finishCurrentActivity");
            Activity activity = popActivity();
            if (activity != null) {
                activity.finish();
            }
        }

        /**
         * 结束所有Activity
         */
        public void finishAllActivity() {
            LogMan.logDebug("finishAllActivity");
            while (!mActivityStack.isEmpty()) {
                finishCurrentActivity();
            }
        }
    }

    public String getToken() {
        if (token == null) {
            token = mSp.getString("token", "");
        }
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        mSp.edit().putString("token", token).apply();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        ARouter.init(this);
    }
}
