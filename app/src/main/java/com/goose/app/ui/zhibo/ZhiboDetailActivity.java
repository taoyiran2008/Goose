package com.goose.app.ui.zhibo;

import android.content.Intent;

import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.goose.app.data.DataProvider;
import com.goose.app.model.PictureDetailInfo;
import com.goose.app.model.PictureInfo;
import com.goose.app.ui.login.LoginActivity;
import com.taoyr.app.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by taoyr on 2018/1/6.
 */

public class ZhiboDetailActivity extends BaseActivity<ZhiboDetailContract.Presenter> implements ZhiboDetailContract.View {

    @BindView(R.id.nice_video_player)
    IjkVideoView mNiceVideoPlayer;
    String mProductId;
    String mProductCategory;
    PictureDetailInfo mDetailInfo;
    private List<PictureInfo> mList = new ArrayList<>();

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_zhibo_detail;
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
    }

    @Override
    public void getProductDetailOnUi(PictureDetailInfo info) {
        mDetailInfo = info;

        setTopBarTitle(info.title);


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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
