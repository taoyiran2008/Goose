package com.goose.app.ui.picture;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.goose.app.data.DataProvider;
import com.goose.app.model.PictureDetailInfo;
import com.goose.app.ui.login.LoginActivity;
import com.goose.app.widgets.controller.SimplePictureListController;
import com.taoyr.app.base.BaseActivity;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerView;
import com.taoyr.widget.widgets.commonrv.base.BaseRecyclerViewGlue;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by taoyr on 2018/1/6.
 */

public class PictureDetailActivity extends BaseActivity<PictureDetailContract.Presenter> implements PictureDetailContract.View  {


    @BindView(R.id.base_recycler_view)
    BaseRecyclerViewGlue base_recycler_view;

    @BindView(R.id.img_star)
    ImageView img_star;
    @BindView(R.id.img_share)
    ImageView img_share;

    @BindView(R.id.img_like)
    ImageView img_like;

    @BindView(R.id.img_download)
    ImageView img_download;

    String mProductId;
    PictureDetailInfo mDetailInfo;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_picture_detail;
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        mProductId = intent.getStringExtra(Configs.EXTRA_ID);
    }

    @Override
    protected void initView() {
        mPresenter.getProductDetail(mProductId);
        mPresenter.operateProduct(mProductId, DataProvider.OPERATION_TYPE_VIEW);
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

        /**
         * 如果链接含有空格，则recycler view中加载图片会有问题，但是我们将url通过intent传到图片浏览
         * 页面，却可以正常加载（intent传递过程中，有trim的操作?）
         */
        String urls[] = new String[0];
        if (!TextUtils.isEmpty(info.url)) {
            urls = info.url.split(",\\s*");
        }

        base_recycler_view.initialize(new SimplePictureListController(mContext), BaseRecyclerView.ORIENTATION_VERTICAL, 1, 20);

        base_recycler_view.refresh(Arrays.asList(urls));
    }

    @OnClick({R.id.img_star, R.id.img_like, R.id.img_download,R.id.img_share})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_star:
                mPresenter.operateProduct(mProductId, DataProvider.OPERATION_TYPE_VIEW);
                break;
            case R.id.img_download:
                mPresenter.operateProduct(mProductId, DataProvider.OPERATION_TYPE_DOWNLOAD);
                break;
            case R.id.img_like:
                mPresenter.operateProduct(mProductId, DataProvider.OPERATION_TYPE_FAVOR);
                break;
            case R.id.img_share:
                //share
                break;
        }
    }
    @Override
    public void goLogin() {
        startActivity(new Intent(mContext, LoginActivity.class));
    }
    @Override
    public void operateProductOnUi(String type) {
    }
}
