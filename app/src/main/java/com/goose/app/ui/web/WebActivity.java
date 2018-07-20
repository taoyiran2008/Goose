package com.goose.app.ui.web;

import android.content.Intent;

import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.taoyr.app.base.SimpleActivity;
import com.taoyr.widget.widgets.MyWebView;

import butterknife.BindView;

/**
 * Created by taoyiran on 2018/2/8.
 */

public class WebActivity extends SimpleActivity {

    @BindView(R.id.webview)
    MyWebView webview;

    String mUrl;
    String mTitle;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_web;
    }

    @Override
    public void handleIntent(Intent intent) {
        mUrl = intent.getStringExtra(Configs.EXTRA_URL);
        mTitle = intent.getStringExtra(Configs.EXTRA_TITLE);
    }

    @Override
    protected void initView() {
        setTopBarTitle(mTitle == null ? "浏览网页" : mTitle);
        webview.loadUrl(mUrl);
    }
}
