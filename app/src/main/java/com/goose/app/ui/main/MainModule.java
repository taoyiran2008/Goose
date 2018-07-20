package com.goose.app.ui.main;

/**
 * Created by taoyr on 2018/1/5.
 */


import com.goose.app.ui.account.AccountFragment;
import com.goose.app.ui.picture.PictureContract;
import com.goose.app.ui.picture.PictureFragment;
import com.goose.app.ui.picture.PicturePresenter;
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

    @FragmentScoped
    @ContributesAndroidInjector
    abstract AccountFragment accountFragment();

    @ActivityScoped
    @Binds
    abstract IBasePresenter<IBaseView> indexPresenter(BasePresenter<IBaseView> presenter);

    @ActivityScoped
    @Binds
    abstract MainContract.Presenter textViewPresenter(MainPresenter presenter);

    @FragmentScoped
    @ContributesAndroidInjector
    abstract PictureFragment pictureFragment();

    @ActivityScoped
    @Binds
    abstract PictureContract.Presenter activityPresenter(PicturePresenter presenter);
}
