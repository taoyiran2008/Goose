package com.taoyr.app.utility;

import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.taoyr.app.thirdparty.ifs.IRouterAppService;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpUtils {

    /**
     * 建立HTTP连接超时，一般来说很快
     */
    private final static int CONNECT_TIMEOUT = 30;

    /**
     * 读取内容超时，发起请求Request到收到Response响应
     */
    private final static int READ_TIMEOUT = 30;

    /**
     * 写超时前等待，一般是通过HTTP请求向服务端发送数据，比如将json放到http request body，或者向
     * 服务器传文件（Profile image）
     */
    private final static int WRITE_TIMEOUT = 30;

    public static OkHttpClient createOkHttpClient() {
        //OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier(createHostnameVerifier())
                // javax.net.ssl.SSLHandshakeException: java.security.cert.CertPathValidatorException:
                // Trust anchor for certification path not found.
                .sslSocketFactory(createSSLSocketFactory())
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) // 设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS) // 设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS) // 设置连接超时时间
                .addInterceptor(createHeaderInterceptor()) // 添加HTTP请求头拦截器
                .addInterceptor(createLogInterceptor()) // 添加日志拦截器
                .build();
        return client;
    }

    public static OkHttpClient createProgressOkHttpClient(ProgressListener listener) {
        //OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier(createHostnameVerifier())
                // javax.net.ssl.SSLHandshakeException: java.security.cert.CertPathValidatorException:
                // Trust anchor for certification path not found.
                .sslSocketFactory(createSSLSocketFactory())
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) // 设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS) // 设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS) // 设置连接超时时间
                .addInterceptor(new ProgressInterceptor(listener)) // 添加进度支持
                .addInterceptor(createLogInterceptor()) // 添加日志拦截器
                .build();
        return client;
    }

    // 日志拦截器。因为okhttp处理请求相关失误全权交给了Retrofit框架，当出现一些请求异常，只能通过
    // log来调试解决问题。
    public static HttpLoggingInterceptor createLogInterceptor() {
        return new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogMan.logError(message);
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY /*BASIC*/); // 设置打印HTTP请求的级别
    }

    OkHttpUtils() {
        ARouter.getInstance().inject(this);
    }

    // Header拦截器
    public static Interceptor createHeaderInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                /*Request request = chain.request();
                return chain.proceed(request);*/

                //在这里你可以做一些想做的事,比如token失效时,重新获取token
                //或者添加header等等

                Request request = chain.request();
                Request.Builder builder = request.newBuilder();
                //String token = BaseApplication.getInstance().getToken();
                // 使用arouter获取app的数据
                //IRouterService service = ARouter.getInstance().navigation(IRouterService.class);

                IRouterAppService service = ARouter.getInstance().navigation(IRouterAppService.class);
                //IRouterAppService service2 = (IRouterAppService) ARouter.getInstance().build(IRouterAppService.SERVICE_PATH).navigation();
                if (service != null) {
                    String token = service.getToken();
                    if (!TextUtils.isEmpty(token)) {
                        builder.addHeader("token", token);
                    }
                }

                Response response = null;
                try {
                    // 尽量避免返回null的response对象
                    response = new Response.Builder().
                            request(request).
                            protocol(Protocol.HTTP_1_1).
                            code(404).
                            build();

                    response = chain.proceed(builder.build());
                } catch (Exception e) {
                    // 在假网环境下（连着没有Internet访问的wifi），会报IOException。
                    // 如果Manifest没有加权限，访问会出现404，retrofit端会报NPE。
                    LogMan.logError(e);
                }

                if (response != null && response.header("SSO-Login-Url") != null) {
                    /*在调用接口时，App端需要校验响应请求头。当响应头携带了SSO-Login-Url时，表示用户的
                    会话信息过期或者未登录，需要重新登录校验。当前测试阶段，令牌有效期为15分钟。*/
                    // showToast导致Cannot create handler inside thread without Looper.prepare()
                    //BaseApplication.getInstance().relogin();

                    //BaseApplication.getInstance().showLoginDialog();
                    // 使用Arouter调用app画面
                }

                return response;
                //return chain.proceed(builder.build());
            }
        };
    }

    public static X509TrustManager createTrustAllCerts() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[0];
            }
        };
    }

    // Prevent Memory Leak
    // 不使用匿名内部类的方式创建，静态的方法保证不持有外部类的引用
    public static HostnameVerifier createHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    public static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{createTrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }
}
