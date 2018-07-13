package com.goose.app;

import com.goose.app.data.DataProvider;
import com.goose.app.di.DaggerAppComponent;
import com.taoyr.app.base.BaseApplication;
import com.taoyr.app.model.UserDetailInfo;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

/**
 * Created by taoyr on 2018/6/20.
 */

public class GooseApplication extends BaseApplication {

    private static GooseApplication sInstance;

    UserDetailInfo mUserInfo;

    @Inject
    DataProvider mDataProvider;

    @Override
    protected AndroidInjector<? extends DaggerApplication> getAndroidInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public DataProvider getDataProvider() {
        return mDataProvider;
    }

    public static GooseApplication getInstance() {
        return sInstance;
    }

    public void setUserInfo(UserDetailInfo info) {
        mUserInfo = info;
    }

    public UserDetailInfo getUserInfo() {
        return mUserInfo;
    }
}
