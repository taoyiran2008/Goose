package com.goose.app.ui.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.goose.app.GooseApplication;
import com.goose.app.R;
import com.goose.app.ui.favor.FavorListActivity;
import com.taoyr.app.base.BaseActivity;
import com.taoyr.app.base.BaseFragment;
import com.taoyr.app.base.IBasePresenter;
import com.taoyr.app.base.IBaseView;
import com.taoyr.app.model.UserDetailInfo;
import com.taoyr.app.utility.CommonUtils;
import com.taoyr.app.utility.PictureLoader;
import com.taoyr.widget.widgets.EntryView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by taoyr on 2017/10/11.
 * 暂时一级画面
 */

public class AccountFragment extends BaseFragment<IBasePresenter<IBaseView>> {

    @BindView(R.id.ll_frame)
    LinearLayout ll_frame;

    @BindView(R.id.img_head)
    ImageView img_head;
    @BindView(R.id.img_gender)
    ImageView img_gender;
    @BindView(R.id.txt_nick_name)
    TextView txt_nick_name;

    @BindView(R.id.ev_my_picture)
    EntryView ev_open_live;

    @Inject
    public AccountFragment() {
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_account;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        final BaseActivity activity = (BaseActivity) getActivity();
        if (isVisibleToUser) {
            activity.setStatusBarTransparent(true);
        } else {
            // 注意：第一次进入MainActivity即会执行，可能因为控件没有初始化完毕，而会导致NPE
            if (activity != null) {
                activity.setStatusBarTransparent(false);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // 从当前Fragment导航离开，并不会执行到
        /*BaseActivity activity = (BaseActivity) getActivity();
        activity.setStatusBarTransparent(false);*/
    }

    @OnClick({R.id.ev_my_picture, R.id.img_head})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ev_my_picture:
                Intent intent = new Intent(mContext, FavorListActivity.class);
                startActivity(intent);
                break;
            case R.id.img_head:
                break;
        }
    }

    private void updateUserUi(UserDetailInfo info) {
        if (info == null) return;

        if (TextUtils.isEmpty(info.avatar)) {
            PictureLoader.loadImageViaGlide(img_head, R.drawable.logo, true, false, -1);
        } else {
            // 不能使用placeholder，不然圆形头像的上下部分会缺失一块
            PictureLoader.loadImageViaGlide(img_head, info.avatar,
                    CommonUtils.getImageTimeStamp(mContext), -1, true, false, ImageView.ScaleType.CENTER_CROP);
        }

        txt_nick_name.setText(info.displayName == null ? "" : info.displayName);

        String sex = info.sex;
        if ("MALE".equals(sex)) {
            img_gender.setImageResource(R.drawable.male_icon);
        } else if ("FEMALE".equals(sex)) {
            img_gender.setImageResource(R.drawable.female_icon);
        } else {
            img_gender.setImageResource(R.drawable.male_icon);
        }
    }

    @Override
    protected void initView() {
        if (GooseApplication.getInstance().getUserInfo() != null) {
            updateUserUi(GooseApplication.getInstance().getUserInfo());
        }

        Glide.with(this).load(R.drawable.profile_bg).asBitmap().transform(new BlurTransformation(mContext, 25))
                .into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Drawable drawable = new BitmapDrawable(mContext.getResources(), resource);
                ll_frame.setBackground(drawable);
            }
        });

        /*subscribeEvent(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                if (o instanceof RefreshUserEvent) {
                    updateUserUi(mApplication.getUser());
                }
            }
        });*/
    }
}
