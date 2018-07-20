package com.taoyr.widget.widgets.progress;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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
import com.taoyr.widget.R;
import com.taoyr.widget.R2;
import com.taoyr.widget.utils.LogMan;
import com.taoyr.widget.widgets.progress.glide.ProgressModelLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by taoyr on 2018/3/26.
 */

public class ProgressImageView extends RelativeLayout {

    @BindView(R2.id.ll_loading)
    LinearLayout ll_loading;
    @BindView(R2.id.img_loading)
    ImageView img_loading;
    @BindView(R2.id.txt_progress)
    TextView txt_progress;
    @BindView(R2.id.img_content)
    ImageView img_content;

    Unbinder mUnBinder;

    Context mContext;

    ProgressModelLoader mProgressModelLoader;

    ProgressListener mProgressListener = new ProgressListener() {
        @Override
        public void update(long bytesRead, long contentLength, boolean done) {
            final int readInKb = (int) (bytesRead / 1024);
            final int totalInKb = (int) (contentLength / 1024);
            final int progress = readInKb * 100 / totalInKb;
            LogMan.logDebug("progress info: " + progress);

            post(new Runnable() {
                @Override
                public void run() {
                    txt_progress.setText(mContext.getString(R.string.common_loading_progress, progress));
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

    public ProgressImageView(Context context) {
        this(context, null, 0);
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.custom_progress_image_view, this);

        mUnBinder = ButterKnife.bind(this);

        mProgressModelLoader = new ProgressModelLoader(mProgressListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mUnBinder.unbind();
    }

    public void loadImage(String url) {
        // 显示加载UI，如果image view不可见，则glide无法进行加载
        //ll_loading.setVisibility(VISIBLE);
        //img_content.setVisibility(GONE);
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.image_loading);
        // 加载图标转起来
        img_loading.setImageResource(R.drawable.icon_loading);
        img_loading.startAnimation(anim);
        txt_progress.setText(mContext.getString(R.string.common_loading_progress, 0));

        // 使用Glide加载图像
        Glide.with(mContext)
                .using(mProgressModelLoader)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                //.centerCrop()
                //.error(R.drawable.logo)
                .listener(mGlideListener)
                .into(img_content);
    }
}
