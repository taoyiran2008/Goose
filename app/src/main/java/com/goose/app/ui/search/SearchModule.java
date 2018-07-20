package com.goose.app.ui.search;

/**
 * Created by taoyr on 2018/1/5.
 */


import com.taoyr.app.di.scope.ActivityScoped;

import dagger.Binds;
import dagger.Module;

@Module
abstract public class SearchModule {

    @ActivityScoped
    @Binds
    abstract SearchContract.Presenter searchPresenter(SearchPresenter presenter);
}
