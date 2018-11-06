package com.goose.app.ui.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.goose.app.ui.login.LoginActivity;
import com.goose.app.ui.personal.SignUpPersonalActivity;
import com.taoyr.app.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class SignUpTelActivity extends BaseActivity<SignUpTelContract.Presenter>
        implements SignUpTelContract.View, TextWatcher {

    @BindView(R.id.edt_tel)
    EditText edt_tel;
    @BindView(R.id.base_ll)
    LinearLayout base_ll;
    @BindView(R.id.edt_pw)
    EditText edt_pw;
    @BindView(R.id.edt_invite)
    EditText edt_invite;
    @BindView(R.id.txt_next)
    TextView txt_next;

    String mMobile = "";
    String mPassword;
    String mShareCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_sign_up_tel;
    }

    @Override
    protected void initView() {
        setTopBarTitle("注册");
        /*setTopBarFunction("首页", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, LoginActivity.class));
            }
        });*/

        edt_tel.addTextChangedListener(this);
        edt_pw.addTextChangedListener(this);
       // base_ll.getBackground().setAlpha(100);//0~255透明度值
        smartCheckFields();
    }

    @OnClick({R.id.txt_next})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_next:
                mMobile = edt_tel.getText().toString();
                /*if (!AccountValidateUtil.isMobileLoose(mMobile)) {
                    showToast("手机号格式不正确");
                    return;
                }*/

                mPassword = edt_pw.getText().toString();
                /*if (mPassword.length() < 6 || !AccountValidateUtil.isPassword(mPassword)) {
                    showToast("密码设置不合规，请重新输入");
                    return;
                }*/
                mShareCode = edt_invite.getText().toString();

                mPresenter.register(mMobile, mPassword,mShareCode);
                break;
        }
    }

    private void smartCheckFields() {
        if (!TextUtils.isEmpty(edt_tel.getText())) {
            txt_next.setEnabled(true);
            return;
        }
        txt_next.setEnabled(false);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        smartCheckFields();
    }

    @Override
    public void sendCodeOnUi() {

    }

    @Override
    public void registerOnUi() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(Configs.EXTRA_MOBILE_NUMBER, mMobile);
        startActivity(intent);
        finish();
    }
}
