package com.taoyr.widget.widgets.commonrv.base;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by taoyr on 2017/9/15.
 * MVC中的C，既实现视图控制，也实现了模块化，将不同数据及其对应的数据展示样式分为单独的模块来做。
 *
 * 该类为视图控制器的基类，声明为abstract的class，复写了releaseResource的接口方法，后面集成它的类，
 * 可以不必实现这个方法（因为少用）。因此这个类也起到了适配器的作用。
 *
 * UIController
 */

public abstract class BaseRvController<T> implements IController<T> {

    //@Inject
    //protected RxBus mBus = BaseApplication.getInstance().mRxbus;
    /**
     * UiController的主要工作是负责指定显示的画面，以及将数据绑定的画面上。但无法将逻辑完全剥离，比如
     * 点击展示的元素，可能会要求View进行一些画面更新（比如弹出一个dialog），也有可能去要求Presenter
     * 做一些事情，比如根据当前的元素内容，去请求数据。
     *
     * 我们可以去持有mActivity的软引用，这样我们通过强转，不仅可以访问View的方法，也可以访问
     * Presenter的方法。
     *
     * 我们也可以通过单独设定回调函数来做。
     *
     * 最佳实践是通过EventBus(otto)来将事件发送出去，并进一步的使用Rxbus实现响应式编程。
     */
    //private WeakReference<BaseActivity> mActivity;

/*    public BaseRvController() { // Avoid: There is no default constructor in BaseRvCell
    }

    public BaseRvController(BaseActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    public void setActivity(BaseActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    public BaseActivity getActivity() {
        if (mActivity != null) {
            return mActivity.get();
        }
        return null;
    }*/

    /*非必须实现的方法*/

    @Override
    public void releaseResource(RecyclerView.ViewHolder viewHolder) {
        /*no-op*/
    }

    @Override
    public void onSelected(T t, int position, RvAdapter<T> adapter) {
        /*no-op*/
    }

    @Override
    public int getSectionPosition(List<T> list, String initial) {
       /*no-op*/
        return -1;
    }

    @Override
    public boolean bind(RecyclerView.ViewHolder viewHolder, T data) {
        return true;
    }

    @Override
    public boolean bind(RecyclerView.ViewHolder viewHolder, RvAdapter<T> adapter, List<T> list, int position) {
        return true;
    }
}
