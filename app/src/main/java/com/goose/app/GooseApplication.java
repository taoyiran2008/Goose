package com.goose.app;

import com.goose.app.di.DaggerAppComponent;
import com.taoyr.app.base.BaseApplication;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

/**
 * Created by taoyr on 2018/6/20.
 */

public class GooseApplication extends BaseApplication {
    @Override
    protected AndroidInjector<? extends DaggerApplication> getAndroidInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }
}
