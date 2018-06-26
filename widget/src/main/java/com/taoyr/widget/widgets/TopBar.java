package com.taoyr.widget.widgets;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taoyr.widget.R;
import com.taoyr.widget.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by taoyiran on 2018/1/13.
 *
 * TopBar仅为Activity所使用。
 * 标题栏的左按钮应该恒为返回功能（结束当前Activity），因此应该统一封装到BaseActivity中。
 * 外部Activity仅需要设置TopBar的title，以及右边的功能按钮（可以为文字或者图标），目前设计中最多
 * 只有一个，考虑到扩展，可以把按钮的添加做成动态的，但是目前进度紧先写死。
 */

public class TopBar extends FrameLayout {

    @BindView(R2.id.ll_left_area)
    LinearLayout ll_left_area;

    @BindView(R2.id.img_back)
    ImageView img_back;

    @BindView(R2.id.title)
    TextView title;

    @BindView(R2.id.ll_right_area)
    LinearLayout ll_right_area;

    @BindView(R2.id.txt_function)
    TextView txt_function;

    @BindView(R2.id.img_function)
    ImageView img_function;

    private Context mContext;
    private View mRootView;

    public TopBar(@NonNull Context context) {
        super(context);
    }

    public TopBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.title, this);
        setId(R.id.top_bar);
        ButterKnife.bind(this, mRootView);
    }

    /**
     * Deprecated
     * 旧方法，应该使用BaseActivity封装的方法
     */
/*    public void initialize(String title, TopBarCallback callback) {
        mCallback = callback;
        setTopBarTitle(title);
    }*/

    // 这个类本身叫TopBar，下面属于命名多余
    // public void setTopBarTitle(String title) {
    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setBackFunction(OnClickListener listener) {
        if (img_back != null)
        img_back.setOnClickListener(listener);
    }

    public void setRightFunction(String function, OnClickListener listener) {
        this.txt_function.setText(function);
        ll_right_area.setOnClickListener(listener);
    }

    public void setRightFunction(int resId, OnClickListener listener) {
        this.img_function.setImageResource(resId);
        ll_right_area.setOnClickListener(listener);
    }

    public void hideBackBtn(boolean hide) {
        img_back.setVisibility(hide ? GONE : VISIBLE);
    }

    /*@OnClick({R.id.ll_left_area, R.id.ll_right_area})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_left_area:
                if (mCallback != null) mCallback.onBackBtnPressed();
                break;
            case R.id.ll_right_area:
                if (mCallback != null) mCallback.onRightBtnPressed();
                break;
        }
    }*/
}
