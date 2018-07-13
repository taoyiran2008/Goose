package com.goose.app.ui.login;

/**
 * Created by taoyr on 2018/1/5.
 */


import com.taoyr.app.di.scope.ActivityScoped;
import com.taoyr.app.di.scope.ApplicationScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link LoginPresenter}.
 */
@Module
abstract public class LoginModule {

    // 注意这里不能定义与ActivityScoped相同的scope
    @ApplicationScoped
    @ContributesAndroidInjector
    abstract LoginActivity loginActivity();

    // 注意这里的Scope需要与ApplicationModule中的一致，不然编译会报错：scope conflicts
    @ActivityScoped
    @Binds
    abstract LoginContract.Presenter loginPresenter(LoginPresenter presenter);

/*    @ActivityScoped
    @Binds
    abstract IBasePresenter loginBasePresenter(BasePresenter presenter);*/
}
