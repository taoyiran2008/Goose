package com.goose.app.ui.invite;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.taoyr.app.base.SimpleActivity;
import com.taoyr.app.model.UserDetailInfo;
import com.taoyr.widget.widgets.MyWebView;

import butterknife.BindView;

/**
 * Created by taoyiran on 2018/2/8.
 */

public class InviteActivity extends SimpleActivity {

    @BindView(R.id.tv_code)
    TextView tv_code;


    String code;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_invite;
    }

    @Override
    public void handleIntent(Intent intent) {
        code =  intent.getStringExtra(Configs.EXTRA_USER_SHARE_CODE);
        if(code!=null){
            tv_code.setText(code);
        }
    }

    @Override
    protected void initView() {
        setTopBarTitle("邀请");

    }

    public void copy(View view) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(code);
        Toast.makeText(this, "复制成功，可以发给朋友们了。", Toast.LENGTH_LONG).show();
    }
}
