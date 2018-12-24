package com.goose.app.ui.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.goose.app.GooseApplication;
import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.goose.app.model.sign.LastSignInfo;
import com.goose.app.ui.favor.FavorListActivity;
import com.goose.app.ui.invite.InviteActivity;
import com.goose.app.ui.login.LoginActivity;
import com.goose.app.widgets.dialog.ClockInDialog;
import com.taoyr.app.base.BaseActivity;
import com.taoyr.app.base.BaseFragment;
import com.taoyr.app.model.UserDetailInfo;
import com.taoyr.app.utility.CommonUtils;
import com.taoyr.app.utility.PictureLoader;
import com.taoyr.app.utility.ValidUtil;
import com.taoyr.widget.widgets.EntryView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import freemarker.template.utility.DateUtil;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by taoyr on 2017/10/11.
 * 暂时一级画面
 */

public class AccountFragment extends BaseFragment<AccountContract.Presenter> implements AccountContract.View {

    @BindView(R.id.ll_frame)
    LinearLayout ll_frame;

    @BindView(R.id.img_head)
    ImageView img_head;
    @BindView(R.id.img_gender)
    ImageView img_gender;
    @BindView(R.id.txt_nick_name)
    TextView txt_nick_name;
    @BindView(R.id.sign_tv)
    TextView sign_tv;
    @BindView(R.id.txt_coin)
    TextView txt_coin;
    @BindView(R.id.ll_clock_in)
    LinearLayout ll_clock_in;

    @BindView(R.id.ev_my_picture)
    EntryView ev_open_live;
    @BindView(R.id.ev_invite)
    EntryView ev_invite;
    @BindView(R.id.ev_exit)
    EntryView ev_exit;

    UserDetailInfo userDetailInfo;
    ClockInDialog mClockInDialog;

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
        //  final BaseActivity activity = (BaseActivity) getActivity();
//        if (isVisibleToUser) {
//            if (activity != null) {
//                activity.setStatusBarTransparent(true);
//            }
//        } else {
//            // 注意：第一次进入MainActivity即会执行，可能因为控件没有初始化完毕，而会导致NPE
//            if (activity != null) {
//                activity.setStatusBarTransparent(false);
//            }
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // 从当前Fragment导航离开，并不会执行到
        /*BaseActivity activity = (BaseActivity) getActivity();
        activity.setStatusBarTransparent(false);*/
    }

    @OnClick({R.id.ev_my_picture, R.id.img_head, R.id.ll_clock_in, R.id.ev_invite, R.id.ev_exit, R.id.txt_nick_name})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ev_my_picture:
                Intent intent = new Intent(mContext, FavorListActivity.class);
                startActivity(intent);
                break;
            case R.id.img_head:
                break;
            case R.id.ll_clock_in:
                mPresenter.getLastSignInfo();
                break;
            case R.id.ev_invite:
                if (GooseApplication.getInstance().getToken() == null) {
                    mPresenter.getUserInfo();
                } else {
                    Intent intent2 = new Intent(mContext, InviteActivity.class);
                    intent2.putExtra(Configs.EXTRA_USER_SHARE_CODE, userDetailInfo.shareCode);
                    startActivity(intent2);
                }
                break;
            case R.id.ev_exit:
                GooseApplication.getInstance().setToken(null);
                Intent intent3 = new Intent(mContext, LoginActivity.class);
                startActivity(intent3);
                // mApplication.getBaseActivityManager().finishAllActivity();
                break;
            case R.id.txt_nick_name:
                mPresenter.getUserInfo();
//                if("".equals(GooseApplication.getInstance().getToken())){
//                    Intent intent4 = new Intent(mContext, LoginActivity.class);
//                    startActivity(intent4);
//                }
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void updateUserUi(UserDetailInfo info) {
        if (info == null) return;

        if (TextUtils.isEmpty(info.avatar)) {
            PictureLoader.loadImageViaGlide(img_head, R.drawable.logo_clear, true, false, -1);
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
        if (!ValidUtil.isEmpty(GooseApplication.getInstance().getToken())) {
            mPresenter.getUserInfo();
        }
        //mPresenter.getUserInfo();
        Glide.with(this).load(R.drawable.profile).asBitmap().transform(new BlurTransformation(mContext, 25))
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

    void showClockInDialog(LastSignInfo info) {
        if (mClockInDialog == null) {
            mClockInDialog = new ClockInDialog(mContext);
            mClockInDialog.setSignButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.sign();
                }
            });
        }
        mClockInDialog.initialize(info);
        mClockInDialog.show();
    }

    @Override
    public void getLastSignInfoOnUi(LastSignInfo info) {
        showClockInDialog(info);
    }


    @Override
    public void getUserInfo(UserDetailInfo user) {
        userDetailInfo = user;
        updateUserUi(user);
        txt_coin.setText(user.coin + "个金币");
        try {
            if (ValidUtil.isEmpty(user.lastSignTime)) {
                sign_tv.setText("签到领积分");
                return;
            }
            String lastSignTime = user.lastSignTime;
            Calendar calendarNow = Calendar.getInstance();
            calendarNow.setTime(new Date());

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(lastSignTime);
            calendar.setTime(date);
            if (calendar.get(Calendar.DAY_OF_MONTH) == calendarNow.get(Calendar.DAY_OF_MONTH)) {
                sign_tv.setText("已签到");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            sign_tv.setText("签到领积分");
        }


    }

    @Override
    public void signOnUi() {
        if (mClockInDialog != null) {
            mClockInDialog.setSignButtonDisabled(true);
        }
        mPresenter.getUserInfo();
    }

    @Override
    public void goLogin() {
        startActivity(new Intent(mContext, LoginActivity.class));
    }

}
