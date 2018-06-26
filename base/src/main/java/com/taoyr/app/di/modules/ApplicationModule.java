package com.taoyr.app.di.modules;

import android.content.Context;

import com.taoyr.app.base.BaseApplication;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ApplicationModule {
    //expose Application as an injectable context
    @Binds
    abstract Context bindContext(BaseApplication application);
}

