package com.goose.app.ui.video;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.donkingliang.labels.LabelsView;
import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.goose.app.data.DataProvider;
import com.goose.app.model.PictureDetailInfo;
import com.goose.app.model.PictureInfo;
import com.goose.app.ui.login.LoginActivity;
import com.goose.app.ui.search.SearchActivity;
import com.goose.app.widgets.controller.VideoListController;
import com.goose.app.widgets.controller.VideoRecommendListController;
import com.taoyr.app.ShareUtils;
import com.taoyr.app.base.BaseActivity;
import com.taoyr.app.utility.TimeUtils;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerView;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerViewGlue;
import com.taoyr.widget.widgets.dialog.ConfirmDialog;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

import static com.goose.app.configs.Configs.EXTRA_TAG;
import static com.goose.app.configs.Configs.EXTRA_TYPE;

/**
 * Created by taoyr on 2018/1/6.
 */

public class VideoDetailActivity extends BaseActivity<VideoDetailContract.Presenter> implements VideoDetailContract.View {


    @BindView(R.id.base_recycler_view)
    BaseRecyclerViewGlue base_recycler_view;

    @BindView(R.id.nice_video_player)
    NiceVideoPlayer mNiceVideoPlayer;

    @BindView(R.id.tv_view)
    TextView tv_view;
    @BindView(R.id.tv_download_price)
    TextView tv_download_price;

    @BindView(R.id.tv_date)
    TextView tv_date;

    @BindView(R.id.tv_view_price)
    TextView tv_view_price;

    @BindView(R.id.img_star)
    ImageView img_star;

    @BindView(R.id.img_like)
    ImageView img_like;

    @BindView(R.id.img_download)
    ImageView img_download;

    @BindView(R.id.labels)
    LabelsView labelsView;

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
        ShareUtils.init(mContext);
        mPresenter.getProductDetail(mProductId);
        mPresenter.operateProduct(mProductId, DataProvider.OPERATION_TYPE_VIEW);
        mPresenter.getRecommendProductList(mProductCategory, 1, 20);
        base_recycler_view.initialize(new VideoRecommendListController(mContext), BaseRecyclerView.ORIENTATION_VERTICAL, 1, 5);
    }

    @Override
    public void getProductDetailOnUi(PictureDetailInfo info) {
        mDetailInfo = info;

        setTopBarTitle(info.title);

        tv_view.setText("热度" + info.view);
        tv_view_price.setText("观看：" + info.viewPrice);
        tv_download_price.setText("下载：" + info.downloadPrice);

        tv_date.setText(TimeUtils.parseGmtInMs(info.createTime));

        mNiceVideoPlayer.setPlayerType(NiceVideoPlayer.TYPE_IJK); // IjkPlayer or MediaPlayer
        mNiceVideoPlayer.setUp(info.url, null);
        TxVideoPlayerController controller = new TxVideoPlayerController(this);
        controller.setTitle(info.title);
        //controller.setLenght(98000);
        Glide.with(this)
                .load(info.cover)
                .placeholder(R.drawable.logo)
                .crossFade()
                .into(controller.imageView());
        mNiceVideoPlayer.setController(controller);

        String[] tags = info.tag.split(",");

        //直接设置一个字符串数组就可以了。
        labelsView.setLabels(Arrays.asList(tags));

        //标签的点击监听
        labelsView.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                //label是被点击的标签，data是标签所对应的数据，position是标签的位置。
                String tag = data.toString();
                Intent intent = new Intent(VideoDetailActivity.this, SearchActivity.class);
                intent.putExtra(EXTRA_TAG, tag);
                intent.putExtra(EXTRA_TYPE, DataProvider.DATA_TYPE_VIDEO);
                startActivity(intent);
                finish();
            }
        });
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
                ShareUtils.shareLink(mContext, "QQ", "http://download.cash-ico.com", new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                    }
                });
                break;
        }
    }

    @Override
    public void operateProductOnUi(String type) {
    }

    @Override
    public void goLogin() {
        showConfirmDialog("您还不是会员，请先注册哦", new ConfirmDialog.Callback() {
            @Override
            public void onConfirm() {
                startActivity(new Intent(mContext, LoginActivity.class));
            }

            @Override
            public void onCancel() {
                VideoDetailActivity.this.finish();
            }
        });

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
