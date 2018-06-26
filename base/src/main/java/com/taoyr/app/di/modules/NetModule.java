package com.taoyr.app.di.modules;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taoyr.app.configs.UrlConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.taoyr.app.utility.OkHttpUtils.createOkHttpClient;

/**
 * Created by taoyr on 2018/1/4.
 */

@Module
public class NetModule {

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return createOkHttpClient();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlConfig.BASE_URL) // 服务器地址
                .addConverterFactory(GsonConverterFactory.create()) // 使用默认的gson转换器，当然也可以自己构造gson对象传进来
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 添加rxjava转换器
                .client(okHttpClient) // okhttp对象
                .build();
        return retrofit;
    }

    private static Gson buildGson() {
        return new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                //.registerTypeAdapter(UserInfo.class, new UserInfoTypeAdapter())
                .create();
    }

    /*@Provides
    @Singleton
    IApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(IApiService.class);
    }*/
}
