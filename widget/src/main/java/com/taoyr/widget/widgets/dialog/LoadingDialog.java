package com.taoyr.widget.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.taoyr.widget.R;

/**
 * 模态加载对话框，用户无法通过返回键手动退出。
 * 用在页面加载数据时，防止用户退出后，去操作并没有准备好的UI界面。
 */
public class LoadingDialog extends Dialog {
    public LoadingDialog(Context context) {
        super(context, R.style.dialog_loading);
        // 仅在中心显示一个加载滚动条
        setContentView(R.layout.dialog_loading_view);

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }
}
