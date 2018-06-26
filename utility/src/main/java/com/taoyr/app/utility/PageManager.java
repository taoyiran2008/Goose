package com.taoyr.app.utility;

/**
 * Created by taoyr on 2018/3/9.
 * 控制翻页的行为，主要用于减少定义过多分散的常量（PAGE_SIZE），并管理分页信息
 */

public abstract class PageManager {

    public static final int DEFAULT_PAGE_SIZE = 10;

    int mPageSize = DEFAULT_PAGE_SIZE;
    int mCurrentPage = 1;

    public final void nextPage() {
        loadPage(mPageSize, mCurrentPage);
    }

    public final void refreshPage() {
        mCurrentPage = 1;
        loadPage(mPageSize, mCurrentPage);
    }

    /**
     * TODO 必须在数据异步获取完毕后手动调用一下。暂时没有更好的办法。
     */
    public final void nextPageConfirm() {
        mCurrentPage++;
    }

    public final void setPageSize(int size) {
        mPageSize = size;
    }

    public final boolean isLoadMore() {
        return mCurrentPage > 1;
    }

    // 设为protected，使得匿名子类可以复写，也屏蔽了外部对该接口的访问。
    protected abstract void loadPage(int pageSize, int pageIndex);
}
