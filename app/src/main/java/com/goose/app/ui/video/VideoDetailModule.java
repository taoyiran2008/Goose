package com.goose.app.ui.video;

/**
 * Created by taoyr on 2018/1/5.
 */


import com.taoyr.app.di.scope.ActivityScoped;

import dagger.Binds;
import dagger.Module;

@Module
abstract public class VideoDetailModule {

    @ActivityScoped
    @Binds
    abstract VideoDetailContract.Presenter videoDetailPresenter(VideoDetailPresenter presenter);
}
