package com.goose.app.ui.zhibo;

/**
 * Created by taoyr on 2018/1/5.
 */


import com.taoyr.app.di.scope.ActivityScoped;

import dagger.Binds;
import dagger.Module;

@Module
abstract public class ZhiboDetailModule {

    @ActivityScoped
    @Binds
    abstract ZhiboDetailContract.Presenter ZhiboDetailPresenter(ZhiboDetailPresenter presenter);
}
