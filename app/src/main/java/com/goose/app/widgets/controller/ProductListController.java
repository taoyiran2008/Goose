package com.goose.app.widgets.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.goose.app.data.DataProvider;
import com.goose.app.model.PictureInfo;
import com.goose.app.ui.picture.PictureDetailActivity;
import com.goose.app.ui.video.VideoDetailActivity;
import com.taoyr.app.utility.CommonUtils;
import com.taoyr.app.utility.PictureLoader;
import com.taoyr.widget.widgets.commonrv.base.BaseRvController;
import com.taoyr.widget.widgets.commonrv.base.RvAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by taoyr on 2017/9/22.
 */

public class ProductListController extends BaseRvController<PictureInfo> {

    private Context mContext;

    public ProductListController(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder create(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_view_item_picture, parent, false);
        CommonUtils.tuneHeightRatio(mContext, itemView, 199, 375);
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

        String urls[] = new String[0];
        if (!TextUtils.isEmpty(struct.url)) {
            urls = struct.url.split(",");
        }

        PictureLoader.simpleLoad(holder.img, struct.cover);
        holder.txt_title.setText(struct.title);
        holder.txt_count.setText(String.valueOf(urls.length));
        holder.txt_views.setText(String.valueOf(struct.view));

        return true;
    }

    @Override
    public void onSelected(PictureInfo info, int position, RvAdapter<PictureInfo> adapter) {
        super.onSelected(info, position, adapter);

        Intent intent = null;
        if (info.type.equals(DataProvider.DATA_TYPE_PICTURE)) {
            intent = new Intent(mContext, PictureDetailActivity.class);
        } else if (info.type.equals(DataProvider.DATA_TYPE_VIDEO)) {
            intent = new Intent(mContext, VideoDetailActivity.class);
        }
        intent.putExtra(Configs.EXTRA_ID, info.id);
        intent.putExtra(Configs.EXTRA_CATEGORY, info.category);
        mContext.startActivity(intent);
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.txt_title)
        TextView txt_title;
        @BindView(R.id.txt_views)
        TextView txt_views;
        @BindView(R.id.txt_count)
        TextView txt_count;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
