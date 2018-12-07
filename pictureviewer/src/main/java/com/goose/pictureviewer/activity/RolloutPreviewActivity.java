package com.goose.pictureviewer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.goose.pictureviewer.R;
import com.goose.pictureviewer.model.RolloutBDInfo;
import com.goose.pictureviewer.model.RolloutInfo;
import com.goose.pictureviewer.tools.RCommonUtil;
import com.goose.pictureviewer.tools.RGlideUtil;
import com.goose.pictureviewer.view.RolloutViewPager;
import com.taoyr.widget.utils.LogMan;
import com.taoyr.widget.widgets.progress.ProgressListener;
import com.taoyr.widget.widgets.progress.glide.ProgressModelLoader;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.view.View.GONE;


public class RolloutPreviewActivity extends RolloutBaseActivity implements ViewPager.OnPageChangeListener {

    private int index = 0;
    private int selectIndex = -1;
    protected Handler mHandler = new Handler();
    private RelativeLayout main_show_view;

    private ViewPager viewpager;
    private LinearLayout ll_loading;
    private ImageView img_loading;
    private TextView txt_progress;
    private SamplePagerAdapter pagerAdapter;

    private ArrayList<RolloutInfo> ImgList;

    private float moveheight;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rollout_preview);

        findID();
        Listener();
        InData();
        getValue();

    }

    @Override
    public void findID() {
        super.findID();
        viewpager = (RolloutViewPager) findViewById(R.id.bi_viewpager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        main_show_view = (RelativeLayout) findViewById(R.id.main_show_view);
        img_loading = (ImageView) findViewById(R.id.img_loading);
        txt_progress = (TextView) findViewById(R.id.txt_progress);
    }

    @Override
    public void Listener() {
        super.Listener();
        viewpager.setOnPageChangeListener(this);
    }

    ProgressModelLoader mProgressModelLoader;

    ProgressListener mProgressListener = new ProgressListener() {
        @Override
        public void update(long bytesRead, long contentLength, boolean done) {
            final int readInKb = (int) (bytesRead / 1024);
            final int totalInKb = (int) (contentLength / 1024);
            final int progress = readInKb * 100 / totalInKb;
            LogMan.logDebug("progress info: " + progress);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    txt_progress.setText(RolloutPreviewActivity.this.getString(com.taoyr.widget.R.string.common_loading_progress, progress));
                }
            });
        }
    };

    @Override
    public void InData() {
        super.InData();

        index = getIntent().getIntExtra("index", 0);
        type = getIntent().getIntExtra("type", 0);
        ImgList = (ArrayList<RolloutInfo>) getIntent().getSerializableExtra("data");

        Log.e("1", ImgList.size() + "数量");

        imageInfo = ImgList.get(index);
        bdInfo = (RolloutBDInfo) getIntent().getSerializableExtra("bdinfo");

        pagerAdapter = new SamplePagerAdapter();
        viewpager.setAdapter(pagerAdapter);
        viewpager.setCurrentItem(index);

        if (type == 1) {
            moveheight = RCommonUtil.dip2px(this, 70);
        } else if (type == 2) {
            moveheight = (Width - 3 * RCommonUtil.dip2px(this, 2)) / 3;
        } else if (type == 3) {
            moveheight = (Width - RCommonUtil.dip2px(this, 80) - RCommonUtil.dip2px(this, 2)) / 3;
        }
    }


    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        if (showimg == null) {
            return;
        }
        RolloutInfo info = ImgList.get(arg0);
        //单张
        if (type == 0) {
            RGlideUtil.setImage(RolloutPreviewActivity.this, info.url, showimg);
        } else if (type == 1) {//listview
            selectIndex = arg0;
            int move_index = arg0 - index;
            to_y = move_index * moveheight;
        } else if (type == 2) {//gridview，计算图片原始的位置，某行某列
            selectIndex = arg0;
            int a = index / 3;
            int b = index % 3;
            int a1 = arg0 / 3;
            int b1 = arg0 % 3;
            to_y = (a1 - a) * moveheight + (a1 - a) * RCommonUtil.dip2px(this, 2);
            to_x = (b1 - b) * moveheight + (b1 - b) * RCommonUtil.dip2px(this, 2);
        } else if (type == 3) {//类似与朋友圈
            selectIndex = arg0;
            int a = index / 3;
            int b = index % 3;
            int a1 = arg0 / 3;
            int b1 = arg0 % 3;
            to_y = (a1 - a) * moveheight + (a1 - a) * RCommonUtil.dip2px(this, 1);
            to_x = (b1 - b) * moveheight + (b1 - b) * RCommonUtil.dip2px(this, 1);
        }
    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return ImgList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            img_loading.setVisibility(View.VISIBLE);
            txt_progress.setVisibility(View.VISIBLE);
            ll_loading.setVisibility(View.VISIBLE);
            mProgressModelLoader = new ProgressModelLoader(mProgressListener);
            Animation anim = AnimationUtils.loadAnimation(RolloutPreviewActivity.this, R.anim.roll_image_loading);
            // 加载图标转起来
            img_loading.setImageResource(R.drawable.roll_icon_loading);
            img_loading.startAnimation(anim);
            txt_progress.setText(RolloutPreviewActivity.this.getString(com.taoyr.widget.R.string.common_loading_progress, 0));

            PhotoView photoView = new PhotoView(container.getContext());
            String path = ImgList.get(position).url;
 //           Glide.with(RolloutPreviewActivity.this).load(path).into(photoView);
            // 使用Glide加载图像
            Glide.with(RolloutPreviewActivity.this)
                    .using(mProgressModelLoader)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    //.centerCrop() // 不然图片会显示不全
                   // .error(com.taoyr.widget.R.drawable.default_pic_02)
                    .listener(mGlideListener)
                    .into(photoView);

            // Now just add PhotoView to ViewPager and return it
            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {

                @Override
                public void onViewTap(View arg0, float arg1, float arg2) {
                    viewpager.setVisibility(View.GONE);
                    showimg.setVisibility(View.VISIBLE);
                    if (selectIndex != -1) {
                        RGlideUtil.setImage(RolloutPreviewActivity.this, ImgList.get(selectIndex).url, showimg);
                    }
                    setShowimage();
                }
            });
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewpager.setVisibility(View.GONE);
            showimg.setVisibility(View.VISIBLE);
            setShowimage();
        }
        return true;
    }

    @Override
    protected void EndSoring() {
        super.EndSoring();
        viewpager.setVisibility(View.VISIBLE);
        showimg.setVisibility(View.GONE);
    }

    @Override
    protected void EndMove() {
        super.EndMove();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (selectIndex != -1) {
            selectIndex = -1;
        }
        RGlideUtil.clearMemory(this);
    }

    RequestListener mGlideListener = new RequestListener<Object, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirssource) {
            if (e != null) {
                LogMan.logError("Glide Exception e: " + e.getMessage() + ", model: " + model);
            }
            ll_loading.setVisibility(GONE);
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target,
                                       boolean isFromMemoryCache, boolean isFirstResource) {
            LogMan.logDebug("Glide onResourceReady, model: " + model);
            // 图像加载完毕后，显示图像，并隐藏加载UI
            ll_loading.setVisibility(GONE);
            return false;
        }
    };
}
