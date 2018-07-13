package com.goose.app.ui.picture;

/**
 * Created by taoyr on 2018/1/5.
 */


import com.taoyr.app.di.scope.ApplicationScoped;
import com.taoyr.app.di.scope.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract public class PictureModule {

    @ApplicationScoped
    @ContributesAndroidInjector
    abstract PictureFragment pictureFragment();

    @FragmentScoped
    @Binds
    abstract PictureContract.Presenter picturePresenter(PictureContract presenter);
}
