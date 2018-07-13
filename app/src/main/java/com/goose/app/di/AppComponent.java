package com.goose.app.di;

import com.taoyr.app.base.BaseApplication;
import com.taoyr.app.di.modules.AppModule;
import com.taoyr.app.di.modules.ApplicationModule;
import com.taoyr.app.di.modules.NetModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by taoyr on 2018/1/4.
 *
 * dagger中Component为Inject对象和Module之间的桥梁。
 * 这个Component提供的注入对象，都是单例的存在。
 *
 * AndroidInjector为dagger android中的特性，使得Android组件比如Activity，Fragment等不用去关注注入的
 * 过程，即component.inject(this)
 */

@Singleton
@Component(modules={ApplicationModule.class, NetModule.class, AppModule.class,
        ActivityBindingModule.class, DataProviderModule.class,
        AndroidSupportInjectionModule.class})
public interface AppComponent extends AndroidInjector<BaseApplication> {

    // Gives us syntactic sugar. we can then do DaggerAppComponent.builder().application(this).build().inject(this);
    // never having to instantiate any modules or say which module we are passing the application to.
    // Application will just be provided into our app graph now.
    @Component.Builder
    interface Builder {

        @BindsInstance
        AppComponent.Builder application(BaseApplication application);

        AppComponent build();
    }
}
