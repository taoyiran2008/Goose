package com.goose.app.ui.favor;

/**
 * Created by taoyr on 2018/1/5.
 */


import com.taoyr.app.di.scope.ActivityScoped;

import dagger.Binds;
import dagger.Module;

@Module
abstract public class FavorListModule {

    @ActivityScoped
    @Binds
    abstract FavorListContract.Presenter favorListPresenter(FavorListPresenter presenter);
}
