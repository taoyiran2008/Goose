package com.goose.app.ui.video;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.goose.app.data.DataProvider;
import com.goose.app.model.PictureDetailInfo;
import com.goose.app.model.PictureInfo;
import com.goose.app.ui.login.LoginActivity;
import com.goose.app.widgets.controller.VideoListController;
import com.taoyr.app.base.BaseActivity;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerView;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerViewGlue;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by taoyr on 2018/1/6.
 */

public class VideoDetailActivity extends BaseActivity<VideoDetailContract.Presenter> implements VideoDetailContract.View  {


    @BindView(R.id.base_recycler_view)
    BaseRecyclerViewGlue base_recycler_view;

    @BindView(R.id.nice_video_player)
    NiceVideoPlayer mNiceVideoPlayer;

    @BindView(R.id.tv_view)
    TextView tv_view;
    @BindView(R.id.tv_download_price)
    TextView tv_download_price;

    @BindView(R.id.tv_view_price)
    TextView tv_view_price;

    @BindView(R.id.img_star)
    ImageView img_star;

    @BindView(R.id.img_like)
    ImageView img_like;

    @BindView(R.id.img_download)
    ImageView img_download;

    String mProductId;
    String mProductCategory;
    PictureDetailInfo mDetailInfo;
    private List<PictureInfo> mList = new ArrayList<>();
    @Override
    protected int getLayoutResID() {
        return R.layout.activity_video_detail;
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        mProductId = intent.getStringExtra(Configs.EXTRA_ID);
        mProductCategory = intent.getStringExtra(Configs.EXTRA_CATEGORY);
    }

    @Override
    protected void initView() {
        mPresenter.getProductDetail(mProductId);
        mPresenter.operateProduct(mProductId, DataProvider.OPERATION_TYPE_VIEW);
        mPresenter.getRecommendProductList(mProductCategory,1,20);
        base_recycler_view.initialize(new VideoListController(mContext), BaseRecyclerView.ORIENTATION_VERTICAL, 1, 20);
    }

    @Override
    public void getProductDetailOnUi(PictureDetailInfo info) {
        mDetailInfo = info;

        setTopBarTitle(info.title);

        tv_view.setText("热度"+ info.view);
        tv_view_price.setText("观看："+ info.viewPrice);
        tv_download_price.setText("下载："+ info.downloadPrice);

//        mNiceVideoPlayer.setPlayerType(NiceVideoPlayer.TYPE_IJK); // IjkPlayer or MediaPlayer
//        mNiceVideoPlayer.setUp(info.url, null);
//        TxVideoPlayerController controller = new TxVideoPlayerController(this);
//        controller.setTitle(info.title);
//        //controller.setLenght(98000);
//        Glide.with(this)
//                .load(info.cover)
//                .placeholder(R.drawable.logo)
//                .crossFade()
//                .into(controller.imageView());
//        mNiceVideoPlayer.setController(controller);

        mNiceVideoPlayer.setPlayerType(NiceVideoPlayer.TYPE_IJK); // IjkPlayer or MediaPlayer
        String videoUrl = "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4";
//        videoUrl = Environment.getExternalStorageDirectory().getPath().concat("/办公室小野.mp4");
        mNiceVideoPlayer.setUp(videoUrl, null);
        TxVideoPlayerController controller = new TxVideoPlayerController(this);
        controller.setTitle("办公室小野开番外了，居然在办公室开澡堂！老板还点赞？");
        controller.setLenght(98000);
        Glide.with(this)
                .load("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-30-43.jpg")
                .placeholder(R.drawable.img_default)
                .crossFade()
                .into(controller.imageView());
        mNiceVideoPlayer.setController(controller);

        /**
         * 如果链接含有空格，则recycler view中加载图片会有问题，但是我们将url通过intent传到图片浏览
         * 页面，却可以正常加载（intent传递过程中，有trim的操作?）
         */
//        String urls[] = new String[0];
//        if (!TextUtils.isEmpty(info.url)) {
//            urls = info.url.split(",\\s*");
//        }

//        base_recycler_view.initialize(new SimplePictureListController(mContext), BaseRecyclerView.ORIENTATION_VERTICAL, 1, 20);
//        base_recycler_view.refresh(Arrays.asList(urls));
    }

    @Override
    public void getRecommendProductListOnUi(List<PictureInfo> list) {
        mList.addAll(list);
        base_recycler_view.refresh(mList);
    }

    @OnClick({R.id.img_star, R.id.img_like, R.id.img_download})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_star:
                mPresenter.operateProduct(mProductId, DataProvider.OPERATION_TYPE_FAVOR);
                break;
            case R.id.img_download:
                mPresenter.operateProduct(mProductId, DataProvider.OPERATION_TYPE_DOWNLOAD);
                break;
            case R.id.img_like:
                mPresenter.operateProduct(mProductId, DataProvider.OPERATION_TYPE_SUPPORT);
                break;
            case R.id.img_share:
                //分享
                break;
        }
    }

    @Override
    public void operateProductOnUi(String type) {
    }
    @Override
    public void goLogin() {
        startActivity(new Intent(mContext, LoginActivity.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Override
    public void onBackPressed() {
        if (NiceVideoPlayerManager.instance().onBackPressd()) return;
        super.onBackPressed();
    }
}
