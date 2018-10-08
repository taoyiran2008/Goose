package com.goose.app.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goose.app.R;
import com.goose.app.model.sign.LastSignInfo;
import com.goose.app.model.sign.SignInfo;
import com.goose.app.model.sign.SignRuleInfo;
import com.taoyr.app.utility.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClockInDialog extends Dialog {

    /**
     * Unable to parse bindings编译报错原因可能是：
     * 1. R文件引用错误（因为copy&paste，app主工程用到了子module的R文件）
     * 2. Dagger编译错误，导致中间文件没有生成
     */
    @BindView(R.id.ll_container)
    LinearLayout ll_container;

    @BindView(R.id.rl_inner)
    RelativeLayout rl_inner;

    TextView txt_bonus;

    TextView txt_clock_in;

    ImageView img_close;

    Context mContext;
    LastSignInfo mLastSignInfo;

    public void initialize(LastSignInfo lastSignInfo) {
        mLastSignInfo = lastSignInfo;
        setSignButtonDisabled(false);
        addDayViews();
    }

    public ClockInDialog(Context context) {
        super(context, R.style.dialog_confirm);
        mContext = context;
        setContentView(R.layout.dialog_clock_in_view);
        ButterKnife.bind(this);
        /*setContentView(R.layout.dialog_clock_in_view);
        mUnbinder = ButterKnife.bind(this);*/

        /*View view = View.inflate(context, R.layout.dialog_clock_in_view, null);
        setContentView(view);
        ButterKnife.bind(this, view);*/

        /*Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        dialogWindow.setGravity(Gravity.CENTER);

        // 如果layout指定了长宽，则这段代码不起作用，wrap_content才有效
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        lp.height = (int) (wm.getDefaultDisplay().getHeight() * 0.4 ); // 高度设置为屏幕的0.6
        lp.width = (int) (wm.getDefaultDisplay().getWidth() * 0.8); // 宽度设置为屏幕的0.85
        dialogWindow.setAttributes(lp);*/

        initView();
    }

    private void initView() {
        //ll_container = findViewById(R.id.ll_container);
        txt_bonus = findViewById(R.id.txt_bonus);
        txt_clock_in = findViewById(R.id.txt_clock_in);
        img_close = findViewById(R.id.img_close);

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    void addDayViews() {
        if (mLastSignInfo == null) return;

        ll_container.removeAllViews();

        SignInfo signInfo = mLastSignInfo.sign;
        Date today = new Date();
        Date signDay = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            signDay = sdf.parse(signInfo.lastSigntime);
        } catch (Exception e) {
        }

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

        /*Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(today);
        cal2.setTime(signDay);*/

        boolean isAlreadySigned = sdf2.format(today).equals(sdf2.format(signDay));

        for (SignRuleInfo rule : mLastSignInfo.signRules) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.block_clock_in_day, null);
            ImageView img_icon = view.findViewById(R.id.img_icon);
            TextView txt_day = view.findViewById(R.id.txt_day);
            TextView txt_coin = view.findViewById(R.id.txt_coin);

            if (rule.signDays == signInfo.signTimes && isAlreadySigned) {
                txt_day.setText("今天");
                setSignButtonDisabled(true);
                txt_bonus.setText("金币+" + rule.rewardCoins );
            } else if (rule.signDays == (signInfo.signTimes + 1) && !isAlreadySigned) {
                txt_day.setText("今天");
                txt_bonus.setText("金币+" + rule.rewardCoins );
            } else {
                txt_day.setText("第" + rule.signDays + "天");
            }
            txt_coin.setText("+" + rule.rewardCoins);

            ll_container.addView(view);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
            lp.setMargins(10, 0, 10, 0);
        }
    }

    public ClockInDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    /*@Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mUnbinder.unbind();
    }*/

    public void setSignButtonOnClickListener(View.OnClickListener listener) {
        txt_clock_in.setOnClickListener(listener);
    }

    public void setSignButtonDisabled(boolean disabled) {
        CommonUtils.disableView(txt_clock_in, disabled);
        if (disabled) {
            txt_clock_in.setText("已签到");
        } else {
            txt_clock_in.setText("立即签到");
        }
    }
}
