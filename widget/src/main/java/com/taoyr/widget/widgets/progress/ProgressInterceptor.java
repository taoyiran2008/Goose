package com.taoyr.widget.widgets.progress;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by taoyr on 2018/3/26.
 */

public class ProgressInterceptor implements Interceptor {

    private ProgressListener mProgressListener;

    public ProgressInterceptor(ProgressListener progressListener) {
        this.mProgressListener = progressListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(),
                mProgressListener)).build();
    }
}
