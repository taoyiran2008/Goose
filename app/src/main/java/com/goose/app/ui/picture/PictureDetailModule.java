package com.goose.app.ui.picture;

/**
 * Created by taoyr on 2018/1/5.
 */


import com.taoyr.app.di.scope.ActivityScoped;

import dagger.Binds;
import dagger.Module;

@Module
abstract public class PictureDetailModule {

    @ActivityScoped
    @Binds
    abstract PictureDetailContract.Presenter pictureDetailPresenter(PictureDetailPresenter presenter);
}
