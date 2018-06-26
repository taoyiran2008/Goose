package com.taoyr.widget.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.taoyr.widget.R;

import java.io.ByteArrayOutputStream;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class EntryView extends LinearLayout {

    private Context mContext;
    private TextView title;
    private ImageView icon; // entry icon
    private View divider;

    // 这两个属性没有默认值，是动态设定的（比如从后台读取到状态值），因此attr里没有加属性用于在xml中设定默认值
    private TextView txt_value;
    private ImageView img_selected;

    private String mTitltText;
    private int mIconResId;
    private boolean mDisableDivider;
    private int textColor;

    private String mEntryValue;

    public EntryView(Context context) {
        super(context);
    }

    public EntryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EntryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EntryView, defStyle, 0);
        mTitltText = ta.getString(R.styleable.EntryView_ev_title);
        textColor = ta.getColor(R.styleable.EntryView_ev_color, 0);
        mIconResId = ta.getResourceId(R.styleable.EntryView_ev_icon, -1);
        mDisableDivider = ta.getBoolean(R.styleable.EntryView_ev_divider, true);

        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.entry_view, this);
        title = findViewById(R.id.title);
        icon = findViewById(R.id.icon);
        divider = findViewById(R.id.divider);
        txt_value = findViewById(R.id.txt_value);
        img_selected = findViewById(R.id.img_selected);

        if (!TextUtils.isEmpty(mTitltText)) setTitle(mTitltText);
        setIcon(mIconResId);

        setDivider(mDisableDivider);
        title.setTextColor(getResources().getColor(R.color.black));
    }

    public void setTitle(String str) {
        title.setText(str);
    }

    public void setIcon(int resId) {
        if (resId == -1) {
            icon.setVisibility(GONE);
        } else {
            try {
                icon.setImageResource(resId);
            } catch (Exception e) {
                icon.setVisibility(GONE);
            }
        }
    }

    public void setEntryText(String str) {
        txt_value.setVisibility(VISIBLE);
        txt_value.setText(str);
    }

    public String getEntryText() {
        return TextUtils.isEmpty(txt_value.getText()) ? "" : txt_value.getText().toString();
    }

    public String getEntryValue() {
        return mEntryValue;
    }

    public void setEntryValue(String entryValue) {
        this.mEntryValue = entryValue;
    }

    public void setEntryImage(final int resId) {
        img_selected.setVisibility(VISIBLE);
        // 这里有个灵异现象，不能立即设置图像，需要有一个小延迟，不然只有第一次进入画面图像被设置成圆角。
        // 退出画面再次进入，图像无法被设置成圆角。
        img_selected.postDelayed(new Runnable() {
            @Override
            public void run() {
                Glide.with(mContext).load(resId).centerCrop()
                        .dontAnimate()
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(img_selected);
            }
        }, 100);
        //img_selected.setImageResource(resId);
    }

    public void setEntryImage(final String url) {
        img_selected.setVisibility(VISIBLE);
        // 灵异现象2：如果使用了默认加载图片，则会出现第一次进来没有圆形加载，在后面进来才圆形加载的情况
        img_selected.postDelayed(new Runnable() {
            @Override
            public void run() {
                Glide.with(mContext).load(url).centerCrop()
                        .dontAnimate()
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(img_selected);
            }
        }, 100);
    }

    public void setEntryImage(final Bitmap bitmap) {
        img_selected.setVisibility(VISIBLE);
        img_selected.postDelayed(new Runnable() {
            @Override
            public void run() {
                Glide.with(mContext).load(bitmap2Bytes(bitmap)).centerCrop()
                        .dontAnimate()
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(img_selected);
            }
        }, 100);
    }

    public void setDivider(boolean hide) {
        divider.setVisibility(hide ? GONE : VISIBLE);
    }

    public void setTextColor(int color) {
        title.setTextColor(color);
    }

    private byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
