package com.goose.app.ui.zhibo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demo.yilv.videodemo.media.IjkVideoView;
import com.demo.yilv.videodemo.media.PlayerManager;
import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.goose.app.model.PictureDetailInfo;
import com.goose.app.model.PictureInfo;
import com.goose.app.ui.login.LoginActivity;
import com.goose.app.ui.video.VideoDetailActivity;
import com.taoyr.app.base.BaseActivity;
import com.taoyr.app.utility.ValidUtil;
import com.taoyr.widget.widgets.TopBar;
import com.taoyr.widget.widgets.dialog.ConfirmDialog;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by taoyr on 2018/1/6.
 */

public class ZhiboDetailActivity extends BaseActivity<ZhiboDetailContract.Presenter> implements ZhiboDetailContract.View {

    @BindView(R.id.detail_video_view)
    IjkVideoView mVideoView;
    @BindView(R.id.top_bar)

    TopBar top_bar;
    String mProductId;
    int position;
    int maxPosition;

    String mUrl;
    private PlayerManager player;

    @BindView(R.id.ll_loading)
    LinearLayout ll_loading;
    @BindView(R.id.txt_progress)
    TextView txt_progress;

    @BindView(R.id.img_loading)
    ImageView img_loading;

    ArrayList<PictureInfo> listObj;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_zhibo_detail;
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        mProductId = intent.getStringExtra(Configs.EXTRA_ID);
        position = intent.getIntExtra(Configs.EXTRA_POSITION, 0);
        listObj = (ArrayList<PictureInfo>) getIntent().getSerializableExtra(Configs.EXTRA_LIST);
        maxPosition = listObj.size();
    }

    @Override
    protected void initView() {
        this.setStatusBarTransparent(true);
        //得到当前界面的装饰视图


        initVideo(position);
        //mPresenter.getProductDetail(mProductId);
        //mPresenter.operateProduct(mProductId, DataProvider.OPERATION_TYPE_VIEW);

        setGestureListener();
    }

//    @Override
//    public void getProductDetailOnUi(PictureDetailInfo info) {
//        if (info != null && !ValidUtil.isEmpty(info.url)) {
//            mUrl = info.url;
//            initVideo(info.url);
//        } else {
//            showToast("播放异常");
//            this.finish();
//        }
//
//    }

    private void initVideo(int position) {
        top_bar.setVisibility(View.VISIBLE);

        if (mVideoView!=null&& mVideoView.isPlaying()) {
            mVideoView.release(true);
        }

        player = new PlayerManager(this, mVideoView);
        player.live(true);

        player.setScaleType(PlayerManager.SCALETYPE_FILLPARENT);

        PictureInfo pictureInfo = listObj.get(position);
        mUrl = pictureInfo.url;
        top_bar.setTitle(pictureInfo.title);

        player.showActionBar(true);
        player.play(mUrl);

        img_loading.setVisibility(View.VISIBLE);
        ll_loading.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(ZhiboDetailActivity.this, com.goose.pictureviewer.R.anim.roll_image_loading);
        // 加载图标转起来
        img_loading.setImageResource(com.goose.pictureviewer.R.drawable.roll_icon_loading);
        img_loading.startAnimation(anim);

        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_FIND_STREAM_INFO:
                        IjkMediaPlayer mediaPlayer = (IjkMediaPlayer) mp;
                        long speed = mediaPlayer.getTcpSpeed();
                        txt_progress.setText(speed / 1024 + "kb/s");
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        // statusChange(STATUS_LOADING);
                        if (ll_loading.getVisibility() == View.INVISIBLE) {
                            ll_loading.setVisibility(View.VISIBLE);
                            img_loading.setVisibility(View.VISIBLE);
                            txt_progress.setVisibility(View.VISIBLE);
                        }
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //statusChange(STATUS_PLAYING);
                        if (ll_loading.getVisibility() == View.VISIBLE) {
                            ll_loading.setVisibility(View.INVISIBLE);
                            img_loading.setVisibility(View.INVISIBLE);
                            txt_progress.setVisibility(View.INVISIBLE);

                        }
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        //显示下载速度
//                      Toast.show("download rate:" + extra);
                        txt_progress.setText(extra + "%");
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        //statusChange(STATUS_PLAYING);
                        if (ll_loading.getVisibility() == View.VISIBLE) {
                            ll_loading.setVisibility(View.INVISIBLE);
                            img_loading.setVisibility(View.INVISIBLE);
                            txt_progress.setVisibility(View.INVISIBLE);
                            top_bar.setVisibility(View.GONE);
                        }
                        break;
                }
                return false;
            }
        });
        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                ZhiboDetailActivity.this.finish();
                return false;
            }
        });
    }


    @OnClick({R.id.detail_video_view})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_view:
                top_bar.setVisibility(View.VISIBLE);
                timer.schedule(task, 1000, 1000); // 1s后执行task,经过1s再次执行
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
                ZhiboDetailActivity.this.finish();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
     //   player.stop();
        super.onDestroy();
    }

    @Override
    protected void onResume() {

//        if (player != null && !player.isPlaying()) {
//            initVideo(position);
//        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.isPlaying()) {
            player.stop();
        }
        super.onBackPressed();
    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //tvShow.setText(Integer.toString(i++));
                top_bar.setVisibility(View.INVISIBLE);
                timer.cancel();
            }
            super.handleMessage(msg);
        }
    };
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };


    /**
     * 设置上下滑动作监听器
     *
     * @author jczmdeveloper
     */
    float mPosX, mPosY, mCurPosX, mCurPosY;

    @SuppressLint("ClickableViewAccessibility")
    private void setGestureListener() {
        mVideoView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mPosX = event.getX();
                        mPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurPosX = event.getX();
                        mCurPosY = event.getY();

                        break;
                    case MotionEvent.ACTION_UP:
                        if (mCurPosY - mPosY > 0 && (Math.abs(mCurPosY - mPosY) > 1000)) {
                            //向下滑動
                            position--;
                            if (position < 0) {
                                showToast("已经到顶了");
                                position=0;
                            } else {
                                player.stop();
                                initVideo(position);
                            }

                        } else if (mCurPosY - mPosY < 0 && (Math.abs(mCurPosY - mPosY) > 1000)) {
                            //向上滑动
                            position++;
                            if (position >= maxPosition - 1) {
                                showToast("无更多数据");
                                position=maxPosition;
                            } else {
                                player.stop();
                                initVideo(position);
                            }

                        }

                        break;
                }
                return true;
            }

        });
    }
}
