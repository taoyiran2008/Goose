package com.taoyr.app.di.modules;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.taoyr.app.base.BaseApplication;
import com.taoyr.app.rxbus.RxBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by taoyr on 2018/1/4.
 *
 * Module为dagger2中的依赖类，负责为@Inject注解的对象提供实体
 */

@Module
public class AppModule {

    // 这里不需要了，因为ApplicationModule已经注入过一个application实例了
    // 不然会报错：multiple injection
/*    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application providesApplication(Application application) {
        return application;
    }*/

    // Dagger will only look for methods annotated with @Provides
    @Provides
    @Singleton
        // Application reference must come from AppModule.class
    SharedPreferences providesSharedPreferences(BaseApplication application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    RxBus providesRxBus() {
        return new RxBus();
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new Gson();
    }
}
