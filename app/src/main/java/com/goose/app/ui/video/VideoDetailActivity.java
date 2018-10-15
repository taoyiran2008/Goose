package com.goose.app.ui.video;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.goose.app.data.DataProvider;
import com.goose.app.model.PictureDetailInfo;
import com.taoyr.app.base.BaseActivity;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerViewGlue;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

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

    @BindView(R.id.img_star)
    ImageView img_star;

    @BindView(R.id.img_like)
    ImageView img_like;

    @BindView(R.id.img_download)
    ImageView img_download;

    String mProductId;
    PictureDetailInfo mDetailInfo;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_video_detail;
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        mProductId = intent.getStringExtra(Configs.EXTRA_ID);
    }

    @Override
    protected void initView() {
        mPresenter.getProductDetail(mProductId);
    }

    /*private void addImageView(final String path) {
        final ImageView imageView = new ImageView(mContext);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
        layoutParams.topMargin = PictureUtils.dip2px(mContext, 20);
        ll_container.addView(imageView);

        CommonUtils.tuneHeightRatio(mContext, imageView, 1, 2);
        // 需要加一个小延迟，等到图像比例调整完之后再加载图片，不然图片会在按照
        // image view的原始宽高进行加载，出现无法填充满的情况。ResearchDetailActivity的cover image
        // 设置也存在同样的问题，之前还以为是图像被center crop裁剪后，才只能显示很小的区域
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PictureLoader.simpleLoad(imageView, path);
            }
        }, 200);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PictureViewerActivity.class);
                intent.putExtra(Configs.EXTRA_URL, path);
                mContext.startActivity(intent);
            }
        });
    }*/

    @Override
    public void getProductDetailOnUi(PictureDetailInfo info) {
        mDetailInfo = info;
        setTopBarTitle(info.title);

        mNiceVideoPlayer.setPlayerType(NiceVideoPlayer.TYPE_IJK); // IjkPlayer or MediaPlayer
        mNiceVideoPlayer.setUp(info.url, null);
        TxVideoPlayerController controller = new TxVideoPlayerController(this);
        controller.setTitle(info.title);
        //controller.setLenght(98000);
        Glide.with(this)
                .load(info.cover)
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

    @OnClick({R.id.img_star, R.id.img_like, R.id.img_download})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_star:
                mPresenter.operateProduct(mProductId, DataProvider.OPERATION_TYPE_VIEW);
                break;
            case R.id.img_download:
                mPresenter.operateProduct(mProductId, DataProvider.OPERATION_TYPE_DOWNLOAD);
                break;
        }
    }

    @Override
    public void operateProductOnUi(String type) {
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
