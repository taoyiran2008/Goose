package com.goose.app.ui.main;

/**
 * Created by taoyr on 2018/1/5.
 */


import com.taoyr.app.base.BasePresenter;
import com.taoyr.app.base.IBasePresenter;
import com.taoyr.app.base.IBaseView;
import com.taoyr.app.di.scope.ActivityScoped;
import com.taoyr.app.di.scope.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract public class MainModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract TextViewFragment textViewFragment();

    @ActivityScoped
    @Binds
    abstract IBasePresenter<IBaseView> textViewPresenter(BasePresenter<IBaseView> presenter);

    @FragmentScoped
    @ContributesAndroidInjector
    abstract PictureFragment pictureFragment();

    /*@ActivityScoped
    @Binds
    abstract IBasePresenter<IBaseView> picturePresenter(BasePresenter<IBaseView> presenter);*/
}
