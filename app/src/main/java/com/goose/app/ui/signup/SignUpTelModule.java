package com.goose.app.ui.signup;

/**
 * Created by taoyr on 2018/1/5.
 */

import com.taoyr.app.di.scope.ActivityScoped;
import com.taoyr.app.di.scope.ApplicationScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract public class SignUpTelModule {

    @ApplicationScoped
    @ContributesAndroidInjector
    abstract SignUpTelActivity signUpTelActivity();

    @ActivityScoped
    @Binds
    abstract SignUpTelContract.Presenter signUpTelPresenter(SignUpTelPresenter presenter);
}
