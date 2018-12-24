package com.goose.app.widgets.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.goose.app.model.PictureInfo;
import com.goose.app.ui.zhibo.ZhiboDetailActivity;
import com.taoyr.app.utility.CommonUtils;
import com.taoyr.app.utility.PictureLoader;
import com.taoyr.widget.widgets.commonrv.base.BaseRvController;
import com.taoyr.widget.widgets.commonrv.base.RvAdapter;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by taoyr on 2017/9/22.
 */

public class ZhiboListController extends BaseRvController<PictureInfo> {

    private Context mContext;

    public ZhiboListController(Context context) {
        mContext = context;
    }

    private List<PictureInfo> mList;

    @Override
    public RecyclerView.ViewHolder create(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_view_item_zhibo, parent, false);
        //CommonUtils.tuneHeightRatio(mContext, itemView, 200, 200);
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
        mList = list;
        if(struct.cover.contains("aliyuncs")){

            PictureLoader.simpleLoad(holder.img, struct.cover+"?x-oss-process=image/resize,m_fill,h_200,w_200");
        }else {

            PictureLoader.simpleLoad(holder.img, struct.cover);
        }
        holder.txt_title.setText(struct.title);

        return true;
    }

    @Override
    public void onSelected(PictureInfo info, int position, RvAdapter<PictureInfo> adapter) {
        super.onSelected(info, position, adapter);
        Intent intent = new Intent(mContext, ZhiboDetailActivity.class);
        intent.putExtra(Configs.EXTRA_ID, info.id);
        intent.putExtra(Configs.EXTRA_URL, info.url);
        intent.putExtra(Configs.EXTRA_TITLE, info.title);
        intent.putExtra(Configs.EXTRA_POSITION, position);
        intent.putExtra(Configs.EXTRA_LIST, (Serializable) mList);
        mContext.startActivity(intent);
    }


    static final class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.txt_title)
        TextView txt_title;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
