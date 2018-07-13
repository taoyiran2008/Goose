package com.taoyr.app.ifs;

/**
 * Created by taoyr on 2018/1/10.
 */

public interface UiCallback<T> {
    void onSuccess(T t);
    // 底层BasePresenter中对错误做了统一处理，上层已没有处理的必要。非也，底层只封装了错误码处理
    // 等通用的逻辑，如果发送个请求加载评论列表，报404，这时候也需要一个回调告知UI取消下拉刷新转圈。
    // 为了减少对项目文件的修改，我们在onError的时候，同样调用onSuccess，只是将请求成功标记设置为false
    void onFailure(String msg);
}
