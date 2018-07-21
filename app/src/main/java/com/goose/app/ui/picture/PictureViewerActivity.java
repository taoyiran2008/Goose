package com.goose.app.ui.picture;

import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.taoyr.app.base.SimpleActivity;
import com.taoyr.widget.utils.LogMan;
import com.taoyr.widget.widgets.progress.ProgressListener;
import com.taoyr.widget.widgets.progress.glide.ProgressModelLoader;

import butterknife.BindView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.view.View.GONE;

/**
 * Created by taoyr on 2018/7/19.
 */

public class PictureViewerActivity extends SimpleActivity {

    @BindView(R.id.ll_loading)
    LinearLayout ll_loading;
    @BindView(R.id.img_content)
    PhotoView img_content;
    @BindView(R.id.txt_progress)
    TextView txt_progress;
    @BindView(R.id.img_loading)
    ImageView img_loading;

    String mUrl;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_picture_viewer2;
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        mUrl = intent.getStringExtra(Configs.EXTRA_URL);
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
                    txt_progress.setText(mContext.getString(com.taoyr.widget.R.string.common_loading_progress, progress));
                }
            });
        }
    };

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

    @Override
    protected void initView() {
        mProgressModelLoader = new ProgressModelLoader(mProgressListener);
        img_content.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });
        loadImage(mUrl);
    }

    public void loadImage(String url) {
        // 显示加载UI，如果image view不可见，则glide无法进行加载
        //ll_loading.setVisibility(VISIBLE);
        //img_content.setVisibility(GONE);
        Animation anim = AnimationUtils.loadAnimation(mContext, com.taoyr.widget.R.anim.image_loading);
        // 加载图标转起来
        img_loading.setImageResource(com.taoyr.widget.R.drawable.icon_loading);
        img_loading.startAnimation(anim);
        txt_progress.setText(mContext.getString(com.taoyr.widget.R.string.common_loading_progress, 0));

        // 使用Glide加载图像
        Glide.with(mContext)
                .using(mProgressModelLoader)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                //.centerCrop() // 不然图片会显示不全
                .error(R.drawable.logo)
                .listener(mGlideListener)
                .into(img_content);
    }
}
