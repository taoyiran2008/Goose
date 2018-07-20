package com.goose.app.widgets.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.goose.app.ui.picture.PictureViewerActivity;
import com.taoyr.app.utility.PictureLoader;
import com.taoyr.widget.widgets.commonrv.base.BaseRvController;
import com.taoyr.widget.widgets.commonrv.base.RvAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by taoyr on 2017/9/22.
 */

public class SimplePictureListController extends BaseRvController<String> {

    private Context mContext;

    public SimplePictureListController(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder create(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_view_item_picture_simple, parent, false);
        //CommonUtils.tuneHeightRatio(mContext, itemView, 199, 375);
        return new ViewHolder(itemView);
    }

    @Override
    public boolean bind(RecyclerView.ViewHolder viewHolder, final String url) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        PictureLoader.simpleLoad(holder.img, url);

        /*holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<RolloutInfo> data = new ArrayList<>();
                RolloutBDInfo bdInfo;

                bdInfo = new RolloutBDInfo();
                RolloutInfo imageInfo = new RolloutInfo();
                //图片的宽高可以自己去设定,也可以计算图片宽高
                imageInfo.width = 1280;
                imageInfo.height = 720;
                imageInfo.url = url;
                data.add(imageInfo);

                bdInfo.x = holder.img.getLeft();
                bdInfo.y = holder.img.getTop();
                //视图布局的宽高
                bdInfo.width = holder.img.getLayoutParams().width;
                bdInfo.height = holder.img.getLayoutParams().height;
                //跳转和传数据都必须要
                Intent intent = new Intent(mContext, RolloutPreviewActivity.class);
                intent.putExtra("data", (Serializable) data);
                intent.putExtra("bdinfo",bdInfo);
                intent.putExtra("type", 0);//单图传0
                intent.putExtra("index",0);
                mContext.startActivity(intent);
            }
        });*/
        return true;
    }

    @Override
    public boolean bind(RecyclerView.ViewHolder viewHolder, final RvAdapter<String> adapter,
                        final List<String> urls, final int position) {
        return false;
    }

    @Override
    public void onSelected(String url, int position, RvAdapter<String> adapter) {
        Intent intent = new Intent(mContext, PictureViewerActivity.class);
        intent.putExtra(Configs.EXTRA_URL, url);
        mContext.startActivity(intent);
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
