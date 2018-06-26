package com.taoyr.widget.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.taoyr.widget.R;

public class ConfirmDialog extends Dialog {

    private Callback mCallback;
    private TextView msg, ok, cancel;
    private ImageView close;

    public interface Callback {
        void onConfirm();
        void onCancel();
    }

    public void initialize(String msg, Callback callback) {
        initialize(msg, false, callback);
    }

    public void initialize(String msg, boolean isLong, Callback callback) {
        if (isLong) {
            this.msg.setTextSize(12);
            this.msg.setGravity(Gravity.LEFT| Gravity.CENTER_VERTICAL);
            this.msg.setMovementMethod(ScrollingMovementMethod.getInstance());
        } else {
            // 可能同一个Activity会繁复多次的调用默认的确认框，以及长文本确认框，因此这里需要还原为
            // 默认的样式
            this.msg.setTextSize(16);
            this.msg.setGravity(Gravity.CENTER);
            this.msg.setMovementMethod(null);
        }
        this.msg.setText(msg);
        mCallback = callback;
    }

    public ConfirmDialog(Context context) {
        super(context, R.style.dialog_confirm);
        setContentView(R.layout.dialog_comfirm_view);

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
        msg = (TextView) findViewById(R.id.msg);
        ok = (TextView) findViewById(R.id.ok);
        cancel = (TextView) findViewById(R.id.cancel);
        close = (ImageView) findViewById(R.id.close);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mCallback != null) {
                    mCallback.onConfirm();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 默认行为
                dismiss();
                // 执行Callback中注册的行为
                if (mCallback != null) {
                    mCallback.onCancel();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mCallback != null) {
                    mCallback.onCancel();
                }
            }
        });
    }

    public ConfirmDialog(Context context, int themeResId) {
        super(context, themeResId);
    }
}
