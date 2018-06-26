package com.taoyr.widget.widgets.indexrecyclerview;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.taoyr.widget.R;
import com.taoyr.widget.widgets.commonrv.base.RvAdapter;
import com.taoyr.widget.widgets.commonrv.decorator.RecyclerViewDivider;

import java.util.List;

/**
 * 数据排序，过滤，确定数据section分组的位置皆在外部完成。
 */
public class IndexRecyclerView<T> extends FrameLayout {

    /*数据和UI绑定相关*/
    private List<T> mList;
    private RvAdapter<T> mAdapter;

    /*控件View*/
    private View mRootView;
    private SideBar side_bar;
    private RecyclerView recycler_view;
    private TextView hint_text;

    private Context mContext;
    private LayoutInflater mInflater;
    LinearLayoutManager mManager;
    private boolean mMoveFlag;
    private int mPosition = -1;

    public IndexRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public void setAdapter(RvAdapter<T> adapter) {
        mAdapter = adapter;
        if (recycler_view != null && adapter != null) {
            recycler_view.setAdapter(adapter);
        }
    }

    public void setData(List<T> list) {
        mList = list;
        // Workaround solution: index recycler view的布局特殊性，Sidebar是通过onDraw画的，
        // 导致no content view的match parent属性失效。只有点击sidebar，触发invalidate才能全屏
        // side_bar.invalidate();
        // recycler_view.smoothScrollToPosition(0);
        // 忽略前面注释，显示不全是因为父容器的height属性为wrap_content
        mAdapter.refresh(list);
    }

    public void setSideBarIndexTable(String[] table) {
        side_bar.setIndxTable(table);
    }

    private void initView() {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = mInflater.inflate(R.layout.index_recycler_view, this);
        side_bar = (SideBar) mRootView.findViewById(R.id.side_bar);
        recycler_view = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        hint_text = (TextView) mRootView.findViewById(R.id.hint_text);

        // 关联TextView
        side_bar.setTextView(hint_text);
        //设置右侧触摸监听
        side_bar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                if (mAdapter != null && mAdapter.getController() != null) {
                    // 获得该字母首次出现的位置
                    int position = mAdapter.getController().getSectionPosition(mList, s);
                    if (position != -1) {
                        // 这个方法只是让该项目出现在list中，但是具体的位置很随意，并不会将它显示到当前可见的第一项
                        //recycler_view.smoothScrollToPosition(position);
                        smoothMoveToPosition(position);
                    }
                }
            }
        });

        initRecyclerView();
    }

    /**
     * 从前往后翻，当定位的位置位于list末尾，并不会将定位项显示到顶部。
     */
/*    private void smoothMoveToPosition(int n) {
        if (mManager == null || recycler_view == null) return;

        int firstItem = mManager.findFirstVisibleItemPosition();
        int lastItem = mManager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            recycler_view.scrollToPosition(n);
        } else if (n <= lastItem) {
            int top = recycler_view.getChildAt(n - firstItem).getTop();
            recycler_view.scrollBy(0, top);
        } else {
            recycler_view.scrollToPosition(n);
        }
    }*/
    private void initRecyclerView() {
        mManager = new LinearLayoutManager(mContext);
        RecyclerViewDivider divider = new RecyclerViewDivider();
        divider.setOrientation(RecyclerViewDivider.ORIENTATION_VERTICAL);
        divider.setColor(getResources().getColor(R.color.transparent));
        divider.setDividerWidth(dip2px(mContext, 10));

        recycler_view.setLayoutManager(mManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.addItemDecoration(divider);

        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mMoveFlag && mPosition != -1) {
                        smoothMoveToPosition(mPosition);
                        mMoveFlag = false;
                        mPosition = -1;
                    }
                }
            }
        });
    }

    private void smoothMoveToPosition(int position) {
        int firstItem = mManager.findFirstVisibleItemPosition();
        int lastItem = mManager.findLastVisibleItemPosition();

        if (position <= firstItem) { // 当要置顶的项在当前显示的第一个项的前面时
            // smooth scroll 滑动速度太慢，而且没法设置速度
            //recycler_view.smoothScrollToPosition(position);
            recycler_view.scrollToPosition(position);
        } else if (position <= lastItem) { // 当要置顶的项已经在屏幕上显示时
            int top = recycler_view.getChildAt(position - firstItem).getTop();
            recycler_view.scrollBy(0, top);
        } else { // 当要置顶的项在当前显示的最后一项的后面时
            // 先调用smoothScrollToPosition将该项目移动的屏幕显示区域中，然后通过监听scroll state
            // change(STOPPED)完成最后1km的距离微调。
            // 如果使用scrollToPosition(n)，onScrollStateChanged将不会响应
            //recycler_view.smoothScrollToPosition(position);
            recycler_view.scrollToPosition(position);
            mPosition = position;
            //mMoveFlag = true;

            // 使用于scrollToPosition，因为scrollToPosition出发不了onScroll事件，导致当我们需要跳到
            // 的索引在lastItem之后，无法置顶的问题。加延迟是等待scroll完毕，立即调用getChildAt会
            // 得到NULL
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    int firstItem = mManager.findFirstVisibleItemPosition();
                    View child = recycler_view.getChildAt(mPosition - firstItem);
                    if (child != null) {
                        int top = child.getTop();
                        recycler_view.scrollBy(0, top);
                    }
                }
            }, 100);
        }
    }

    int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
