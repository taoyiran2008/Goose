package com.goose.app.di;

import com.goose.app.ui.favor.FavorListActivity;
import com.goose.app.ui.favor.FavorListModule;
import com.goose.app.ui.login.LoginActivity;
import com.goose.app.ui.login.LoginModule;
import com.goose.app.ui.main.MainActivity;
import com.goose.app.ui.main.MainModule;
import com.goose.app.ui.personal.SignUpPersonalActivity;
import com.goose.app.ui.personal.SignUpPersonalModule;
import com.goose.app.ui.picture.PictureDetailActivity;
import com.goose.app.ui.picture.PictureDetailModule;
import com.goose.app.ui.search.SearchActivity;
import com.goose.app.ui.search.SearchModule;
import com.goose.app.ui.signup.SignUpTelActivity;
import com.goose.app.ui.signup.SignUpTelModule;
import com.goose.app.ui.video.VideoDetailActivity;
import com.goose.app.ui.video.VideoDetailModule;
import com.goose.app.ui.zhibo.ZhiboDetailActivity;
import com.goose.app.ui.zhibo.ZhiboDetailModule;
import com.taoyr.app.di.scope.ActivityScoped;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever module
 * ActivityBindingModule is on, in our case that will be AppComponent. The beautiful part about this
 * setup is that you never need to tell AppComponent that it is going to have all these subcomponents
 * nor do you need to tell these subcomponents that AppComponent exists.
 *
 * We are also telling Dagger.Android that this generated SubComponent needs to include the specified
 * modules and be aware of a scope annotation @ActivityScoped When Dagger.Android annotation
 * processor runs it will create 4 subcomponents for us.
 */
@Module
public abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = MainModule.class)
    abstract MainActivity mainActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = PictureDetailModule.class)
    abstract PictureDetailActivity pictureDetailActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = VideoDetailModule.class)
    abstract VideoDetailActivity videoDetailActivity();
    @ActivityScoped
    @ContributesAndroidInjector(modules = ZhiboDetailModule.class)
    abstract ZhiboDetailActivity zhiboDetailActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = LoginModule.class)
    abstract LoginActivity loginActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = SignUpTelModule.class)
    abstract SignUpTelActivity signUpTelActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = SignUpPersonalModule.class)
    abstract SignUpPersonalActivity signUpPersonalActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = SearchModule.class)
    abstract SearchActivity searchActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = FavorListModule.class)
    abstract FavorListActivity favorListActivity();
}
