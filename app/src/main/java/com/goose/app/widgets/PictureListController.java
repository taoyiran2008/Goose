package com.goose.app.widgets;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.goose.app.R;
import com.goose.app.model.PictureInfo;
import com.taoyr.widget.widgets.commonrv.base.BaseRvController;
import com.taoyr.widget.widgets.commonrv.base.RvAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by taoyr on 2017/9/22.
 */

public class PictureListController extends BaseRvController<PictureInfo> {

    @Override
    public RecyclerView.ViewHolder create(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_view_item_picture, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public boolean bind(RecyclerView.ViewHolder viewHolder, PictureInfo data) {
        return false;
    }

    @Override
    public boolean bind(RecyclerView.ViewHolder viewHolder, final RvAdapter<PictureInfo> adapter,
                        final List<PictureInfo> list, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final PictureInfo struct = list.get(position);

        return true;
    }

    @Override
    public void onSelected(PictureInfo info, int position, RvAdapter<PictureInfo> adapter) {
        super.onSelected(info, position, adapter);
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img)
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
