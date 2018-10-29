package com.goose.app.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.goose.app.GooseApplication;
import com.goose.app.R;
import com.goose.app.ui.main.MainActivity;
import com.goose.app.ui.signup.SignUpTelActivity;
import com.taoyr.app.base.BaseActivity;
import com.taoyr.app.utility.ValidUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 这里的泛型使用接口类，而非实体类，因为需要保持与LoginContract.View的泛型声明一致。java的多态性，
 * 保证被注入到LoginContract.Presenter的对象为LoginPresenter实体。
 */
public class LoginActivity extends BaseActivity<LoginContract.Presenter>
        implements LoginContract.View, TextWatcher {

    @BindView(R.id.edt_tel)
    EditText edt_tel;
    @BindView(R.id.edt_pw)
    EditText edt_pw;
    @BindView(R.id.txt_login)
    TextView txt_login;
    @BindView(R.id.txt_forget)
    TextView txt_forget;
    @BindView(R.id.txt_signup)
    TextView txt_signup;

    String mMobile;
    String mPassword;

    // 覆写了这个重载方法，Activity画面空白
/*    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       String token= GooseApplication.getInstance().getToken();
       if(!ValidUtil.isEmpty(token)){
           //loginOnUi();
       }
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        /*edt_tel = (EditText) findViewById(R.id.edt_tel);
        edt_pw = (EditText) findViewById(R.id.edt_pw);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_sign_up = (Button) findViewById(R.id.btn_sign_up);*/
        //mPresenter.takeView(this);
        setTopBarTitle("登陆");
        hideTopBarBack(true);

        edt_tel.addTextChangedListener(this);
        edt_pw.addTextChangedListener(this);
        smartCheckFields();

        //edt_tel.setText("tao");
        //edt_pw.setText("tao");
    }

    @OnClick({R.id.txt_login, R.id.txt_forget, R.id.txt_signup})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_login:
                mMobile = edt_tel.getText().toString();
                mPassword = edt_pw.getText().toString();
                if (validateFields()) {
                    mPresenter.login(mMobile, mPassword);
                }
                break;
            case R.id.txt_forget:
                //startActivity(new Intent(mContext, ForgetActivity.class));
                break;
            case R.id.txt_signup:
                startActivity(new Intent(mContext, SignUpTelActivity.class));
                // 验证令牌过期
                //startActivity(new Intent(mContext, SignUpPersonalActivity.class));
                break;
        }
    }

    private boolean validateFields() {
        /*if (!AccountValidateUtil.isMobileLoose(mMobile)) {
            showToast("手机号格式不正确");
            return false;
        } else if (!AccountValidateUtil.isPassword(mPassword)) {
            showToast("密码格式不正确");
            return false;
        }*/
        return true;
    }

    private void smartCheckFields() {
        if (!TextUtils.isEmpty(edt_tel.getText())
                && !TextUtils.isEmpty(edt_pw.getText())) {
            txt_login.setEnabled(true);
            return;
        }
        txt_login.setEnabled(false);
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
    public void loginOnUi() {
        super.onBackPressed();
    }
}
