
package com.taoyr.app.base;

import android.content.Context;
import android.content.DialogInterface;

import com.taoyr.widget.widgets.dialog.ConfirmDialog;

/**
 * 定义一些基本的ui操作。退出activity时，cancel 所有request这样的操作，属于业务逻辑，不应该封装到这里。
 */
public interface IBaseView<T> {

    void showToast(String msg);

    //void setDisableBackKey(boolean disable);

    void showLoadingDialog(boolean cancelable);
    void showLoadingDialog(boolean cancelable, DialogInterface.OnCancelListener listener);

    void showAlertDialog(String msg);

    void showConfirmDialog(String msg, ConfirmDialog.Callback callback);

    void dismissLoadingDialog();

    void dismissAlertDialog();

    void dismissConfirmDialog();

    Context getBaseViewContext();
}
