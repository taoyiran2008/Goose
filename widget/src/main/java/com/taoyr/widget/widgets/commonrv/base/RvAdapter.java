package com.taoyr.widget.widgets.commonrv.base;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.taoyr.widget.widgets.commonrv.controller.NoContentController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taoyr on 2017/9/15.
 */

public class RvAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<T> mList; // List中的元素可能显示不同的内容，比如VideoCell，ImageCell。type可以在T和UiController中做文章
    private BaseRvController<T> mUiController;
    // 本来想让Adapter和recycler view完全解耦，但是如果adapter内容为空，我们会显示No content view，一旦外部recycler view
    // 的layout manager为两行，会导致无法居中显示
    private RecyclerView mBaseRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private CellType mState = CellType.NORMAL;

    public RvAdapter(BaseRvController<T> controller, RecyclerView baseRecyclerView) {
        this(new ArrayList<T>(), controller, baseRecyclerView);
    }

    public enum CellType {
        EMPTY,
        NORMAL,
        ERROR
    }

    public RvAdapter(List<T> list, BaseRvController<T> controller, RecyclerView baseRecyclerView) {
        mList = list;
        mUiController = controller;
        mBaseRecyclerView = baseRecyclerView;
        mLayoutManager = mBaseRecyclerView.getLayoutManager();
        mBaseRecyclerView.setLayoutManager(new LinearLayoutManager(mBaseRecyclerView.getContext()));
    }

    public void refresh(List<T> list) {
        // TODO Recycler view支持区间范围内更新，这也是它在显示和效率上优于list view的原有之一
        // Great ambition: 工程内所有的list view替换为recycler view
        mState = CellType.NORMAL;
        mList = list;
        if (checkEmpty()) {
            showNoContent();
        } else {
            mBaseRecyclerView.setLayoutManager(mLayoutManager);
            notifyDataSetChanged();
        }
    }

    public BaseRvController<T> getController() {
        return mUiController;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*if (mList == null || mList.size() < 1) {
            return new NoContentController().create(parent);
        }*/
        // 这里不能使用List来判断，必须覆写getType方法，因为只有检查到数据源的Type变化的时候，这个
        // 方法才会触发。否则，会出现复用的viewholder不匹配导致bind出错（比如，刚开始数据源为空，
        // 那么我们使用的是NoContent的viewholder。但是当我们给list赋值后，进行refresh，仍然会
        // 使用NoContent的viewholder）
        if (viewType == CellType.EMPTY.ordinal()) {
            return new NoContentController().create(parent);
        } else {
            return mUiController.create(parent);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (mUiController != null && !checkEmpty()) {
            boolean isConsumed = mUiController.bind(holder, mList.get(position));
            if (!isConsumed) {
                mUiController.bind(holder, this, mList, position);
            }

            // 设置点击事件。在adapter中进行统一封装，不用下发到每个controller进行单独的处理(bind中)
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUiController.onSelected(mList.get(position), holder.getLayoutPosition(),
                            RvAdapter.this);
                }
            });
        }
        // EMPTY的item并不需要绑定数据。
    }

    @Override
    public int getItemCount() {
        // 当mList为空应该显示1条空数据的视图。这里如果返回0，notifyDataSetChanged后onCreateViewHolder
        // 或者onBindViewHolder将不会被调用。
        return checkEmpty()  ? 1 : mList.size();
    }

    public void showNoContent() {
        // mList.clear(); // 这里不能将源数据清空了
        mState = CellType.EMPTY;
        //mBaseRecyclerView.setLayoutManager(null);
        mBaseRecyclerView.setLayoutManager(new GridLayoutManager(mBaseRecyclerView.getContext(), 1));
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (checkEmpty()) {
            return CellType.EMPTY.ordinal();
        }
        return CellType.NORMAL.ordinal();
    }

    private boolean checkEmpty() {
        return mList == null || mList.size() == 0 || mState == CellType.EMPTY;
    }
}
