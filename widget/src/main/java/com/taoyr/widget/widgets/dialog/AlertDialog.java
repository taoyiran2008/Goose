package com.taoyr.widget.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.taoyr.widget.R;

public class AlertDialog extends Dialog {

    private TextView msg, ok;
    private ImageView close;

    public void initialize(String msg) {
        this.msg.setText(msg);
    }

    public AlertDialog(Context context) {
        super(context, R.style.dialog_alert);
        setContentView(R.layout.dialog_alert_view);

        initView();
    }

    private void initView() {
        msg = (TextView) findViewById(R.id.msg);
        ok = (TextView) findViewById(R.id.ok);
        close = (ImageView) findViewById(R.id.close);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public AlertDialog(Context context, int themeResId) {
        super(context, themeResId);
    }
}
