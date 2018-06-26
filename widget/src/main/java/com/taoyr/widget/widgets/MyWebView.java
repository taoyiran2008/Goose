package com.taoyr.widget.widgets;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.taoyr.widget.ifs.WebViewCallback;
import com.taoyr.widget.widgets.dialog.LoadingDialog;

/**
 * Created by taoyiran on 2018/2/4.
 */

public class MyWebView extends WebView {

    private static final String MIME_TYPE_TEXT_HTML = "text/html";
    private static final String ENCODING = "utf-8";

    Context mContext;
    private LoadingDialog mLoadingDialog;
    WebViewCallback mCallback;

    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setCallback(WebViewCallback callback) {
        mCallback = callback;
    }

    private void init(Context context) {
        mContext = context;
        setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // 允许加载不可信任证书的所有HTTPS页面
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //return false;
                // 页面内的连接不允许点击
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoadingDialog();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                dismissLoadingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 404错误会进入这里面，如果页面没找到，服务器自动跳转到错误处理页面（404），
                // 因此不算是url异常，而进入onReceivedError回调
                super.onPageFinished(view, url);
                if (mCallback != null) {
                    mCallback.onFinishLoading();
                }
                dismissLoadingDialog();
            }
        });

        WebSettings settings = getSettings();
        // H5前端会做APP显示的适配，包括调整字体，行距，以及屏幕是否是wideViewPort。
        // 我们不强制使用wideViewPort，将H5的内容正好填充慢整个画面，因为这会导致一个问题，就是
        // 当H5只做了字体调整，但是没有设置wideViewPort时，一旦应用这个属性，设定的字体大小会无效
        /*settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);*/

        // 支持webview缩放
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(true);
        settings.setDisplayZoomControls(false); // 隐藏built-in 缩放按钮

        // 支持local storage
        settings.setJavaScriptEnabled(true);
        //settings.setLoadsImagesAutomatically(true);
        // 解决旅游度假页面，部分图片加载不出的问题
        settings.setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        settings.setDomStorageEnabled(true);// 打开本地缓存提供JS调用,至关重要
        //settings.setAppCacheMaxSize(1024 * 1024 * 8); // API建议使用默认的最大缓存
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        String appCachePath = mContext.getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setDatabaseEnabled(true);
        setBackgroundColor(0);
    }

    public void showLoadingDialog() {
        // Precaution: 当在activity中开启异步线程时，当工作进行完之前销毁activity，会抛出异常：
        // unable to add window, token not valid
        post(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog == null) {
                    mLoadingDialog = new LoadingDialog(mContext);
                }
                mLoadingDialog.setCancelable(true);
                mLoadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        stopLoading();
                    }
                });
                mLoadingDialog.show();
            }
        });
    }

    public void dismissLoadingDialog() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    public void loadH5Content(String content) {
        // 防止中文乱码
        loadDataWithBaseURL("", content, MIME_TYPE_TEXT_HTML, ENCODING, null);
    }
}
