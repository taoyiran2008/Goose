package com.goose.app.di;

import com.google.gson.Gson;
import com.goose.app.data.DataProvider;
import com.goose.app.ifs.IApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by taoyr on 2018/7/10.
 */

@Module
public class DataProviderModule {

    @Provides
    @Singleton
    IApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(IApiService.class);
    }

    @Provides
    @Singleton
    DataProvider provideDataProvider(IApiService service, Gson gson) {
        return new DataProvider(service, gson);
    }
}
