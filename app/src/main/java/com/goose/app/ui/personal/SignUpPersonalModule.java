package com.goose.app.ui.personal;

/**
 * Created by taoyr on 2018/1/5.
 */


import com.taoyr.app.di.scope.ActivityScoped;
import com.taoyr.app.di.scope.ApplicationScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract public class SignUpPersonalModule {

    @ApplicationScoped
    @ContributesAndroidInjector
    abstract SignUpPersonalActivity signUpPersonalActivity();

    @ActivityScoped
    @Binds
    abstract SignUpPersonalContract.Presenter signUpPersonalPresenter(SignUpPersonalPresenter presenter);
}
