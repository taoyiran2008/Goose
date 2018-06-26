
package com.taoyr.app.base;

public interface IBasePresenter<T> {

    /**
     * MVP中的P层，用于将原本Activity/Fragment(MVC中C层)的视图和业务逻辑隔离。它的主要工作是进行
     * 业务运算，然后会去唤起V层的画面。因此P层并不是纯业务的，他也能决定一些UI的表现形式。因此
     * 把Dialog的显示方式定义在这里，应该没毛病。
     *
     * 下面的Flag相互之间是互斥的关系，不用bit flag来表示（0, 1, 2, 4, 8...）。
     */
    public static final int SHOW_NO_DIALOG = 0;

    public static final int SHOW_CANCELABLE_DIALOG = 1;

    public static final int SHOW_DIALOG_AWAYS = 2;

    /**
     * Binds presenter with a view when resumed. The Presenter will perform initialization here.
     *
     * @param view the view associated with this presenter
     */
    void takeView(T view);

    /**
     * Drops the reference to the view when destroyed
     * 可以在这里进行请求取消的操作。
     */
    void dropView();
}
