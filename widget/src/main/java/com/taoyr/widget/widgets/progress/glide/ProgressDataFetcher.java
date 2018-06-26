package com.taoyr.widget.widgets.progress.glide;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.taoyr.widget.utils.LogMan;
import com.taoyr.widget.widgets.progress.ProgressListener;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by taoyr on 2018/3/26.
 */

public class ProgressDataFetcher implements DataFetcher<InputStream> {

    private String mUrl;
    private Call mCall;
    private InputStream mStream;
    private ProgressListener mProgressListener;
    private boolean mIsCancelled;
    private OkHttpClient mClient;

    public ProgressDataFetcher(String url, ProgressListener progressListener) {
        mUrl = url;
        mProgressListener = progressListener;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        Request request = new Request.Builder().url(mUrl).build();
        /*OkHttpClient client = OkHttpUtils.createOkHttpClient();
        client.interceptors().add(new ProgressInterceptor(mProgressListener));*/

        if (mClient == null) {
            mClient = OkHttpUtils.createProgressOkHttpClient(mProgressListener);
        }

        /*OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier(OkHttpUtils.createHostnameVerifier())
                // javax.net.ssl.SSLHandshakeException: java.security.cert.CertPathValidatorException:
                // Trust anchor for certification path not found.
                .sslSocketFactory(OkHttpUtils.createSSLSocketFactory())
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                                    *//*Request.Builder builder = chain.request().newBuilder();
                                    String token = BaseApplication.getInstance().getToken();
                                    if (!TextUtils.isEmpty(token)) {
                                        builder.addHeader("Ticket", token);
                                    }
                                    Response originalResponse = chain.proceed(builder.build());*//*
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new ProgressResponseBody(originalResponse.body(), mProgressListener))
                                .build();
                    }
                })
                .readTimeout(30, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(30,TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(30,TimeUnit.SECONDS)//设置连接超时时间
                .build();*/

        try {
            mCall = mClient.newCall(request);
            Response response = mCall.execute();
            if (mIsCancelled) {
                return null;
            }
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            mStream = response.body().byteStream();
        } catch (Exception e) {
            LogMan.logError(e);
            return null;
        }
        return mStream;
    }

    @Override
    public void cleanup() {
        if (mStream != null) {
            try {
                mStream.close();
                mStream = null;
            } catch (IOException e) {
                mStream = null;
            }
        }
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    public String getId() {
        return mUrl;
    }

    @Override
    public void cancel() {
        mIsCancelled = true;
    }
}
