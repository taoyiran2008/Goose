package com.taoyr.app.utility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.taoyr.utility.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by taoyr on 2018/1/17.
 */

public class PictureLoader {

    private static Bitmap sPlaceholder;

    private static RequestListener mGlideListener = new RequestListener<Object, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirssource) {
            // 在三星Android5.0手机上加载未认证的https图片，会报错java.security.cert.CertPathValidatorException:
            // Trust anchor for certification path not found（S7 7.0系统则正常加载
            if (e != null) {
                LogMan.logError("Glide Exception e: " + e.getMessage() + ", model: " + model);
            }
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target,
                                       boolean isFromMemoryCache, boolean isFirstResource) {
            LogMan.logError("Glide onResourceReady, model: " + model);
            return false;
        }
    };

    static class GlideCircleTransform extends BitmapTransformation {

        private Paint mBorderPaint;
        private float mBorderWidth;

        public GlideCircleTransform(Context context) {
            super(context);
        }

        public GlideCircleTransform(Context context, int borderWidth, int borderColor) {
            super(context);
            mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;

            mBorderPaint = new Paint();
            mBorderPaint.setDither(true);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(borderColor);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(mBorderWidth);
        }

        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = (int) (Math.min(source.getWidth(), source.getHeight()) - (mBorderWidth / 2));
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            if (mBorderPaint != null) {
                float borderRadius = r - mBorderWidth / 2;
                canvas.drawCircle(r, r, borderRadius, mBorderPaint);
            }
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }

    public static void loadImageViaGlide(ImageView imageView, Bitmap bitmap, boolean isCircle,
                                         boolean border, int borderColorId) {
        Context context = imageView.getContext();
        if (context == null) return;

        try {
            DrawableTypeRequest drawableTypeRequest = Glide.with(context).load(PictureUtils.bitmap2Bytes(bitmap));
            DrawableRequestBuilder drawableRequestBuilder = drawableTypeRequest
                    .listener(mGlideListener)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop();

            if (isCircle) {
                //drawableRequestBuilder = drawableRequestBuilder.bitmapTransform(new CropCircleTransformation(context));
                // 边框会作为image的一部分被缓存。因此当第一次加载不用边框，第二次加载设置边框属性，边框也不会粗来。
                // 除此以外，这个圆形转换的类也存在其他几个已知的bug，比如使用place holder图片不能圆形加载，需要使用
                // handler.postDelay才能圆形加载
                /*if (border) {
                    drawableRequestBuilder = drawableRequestBuilder.transform(
                            new GlideCircleTransform(context, 1, context.getResources().getColor(borderColorId)));
                } else {
                    drawableRequestBuilder = drawableRequestBuilder.transform(
                            new GlideCircleTransform(context));
                }*/
                // 避免使用placeholder，导致圆形显示不完整的bug（即便我们把place holder也设置为圆角）
                drawableRequestBuilder = drawableRequestBuilder.dontAnimate();
                drawableRequestBuilder = drawableRequestBuilder.bitmapTransform(new CropCircleTransformation(context));
            }
            drawableRequestBuilder.into(imageView);
        } catch (Exception e) {
            LogMan.logError("error loading picture e: " + (e == null ? "" : e.getMessage()));
        }
    }

    public static void loadImageViaGlide(ImageView imageView, int resId, boolean isCircle,
                                  boolean border, int borderColorId) {
        Context context = imageView.getContext();
        if (context == null) return;

        try {
            DrawableTypeRequest drawableTypeRequest = Glide.with(context).load(resId);
            DrawableRequestBuilder drawableRequestBuilder = drawableTypeRequest
                    .listener(mGlideListener)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop();

            if (isCircle) {
                //drawableRequestBuilder = drawableRequestBuilder.bitmapTransform(new CropCircleTransformation(context));
                // 边框会作为image的一部分被缓存。因此当第一次加载不用边框，第二次加载设置边框属性，边框也不会粗来。
                /*if (border) {
                    drawableRequestBuilder = drawableRequestBuilder.transform(
                            new GlideCircleTransform(context, 1, context.getResources().getColor(borderColorId)));
                } else {
                    drawableRequestBuilder = drawableRequestBuilder.transform(
                            new GlideCircleTransform(context));
                }*/
                drawableRequestBuilder = drawableRequestBuilder.dontAnimate();
                drawableRequestBuilder = drawableRequestBuilder.bitmapTransform(new CropCircleTransformation(context));
            }
            drawableRequestBuilder.into(imageView);
        } catch (Exception e) {
            LogMan.logError("error loading picture e: " + (e == null ? "" : e.getMessage()));
        }
    }

    public static void simpleLoad(ImageView imageView, int resId) {
        loadImageViaGlide(imageView, resId, false, false, -1);
    }

    public static void simpleLoad(ImageView imageView, String url) {
        loadImageViaGlide(imageView, url,
                -1, R.drawable.logo, false, false, null);
    }

    public static void simpleLoadCircle(ImageView imageView, String url) {
        loadImageViaGlide(imageView, url,
                -1, R.drawable.logo, true, false, null); // 圆形加载，使用默认图，会有显示异常的bug（不是正圆）
    }

    /**
     * 使用Glide图片加载框架处理图片交互。该接口不仅用于替换原接口，也直接暴露给外部使用。
     * 加载本地资源(Uri, File, Bitmap)等不使用PictureLoader统一封装的接口，PictureLoader仅针
     * 对url类图片的加载，参考AccountManagementActivity#takePictureBg。
     *
     * 异步加载，没有阻塞的动作，可以在主线程调用。
     */
    public static void loadImageViaGlide(ImageView imageView, String url, long timeStamp,
                                  int defaultResId, boolean isCircle,
                                  boolean border, ImageView.ScaleType scaleType) {
        Context context = imageView.getContext();
        if (context == null) return;

        try {
            DrawableTypeRequest drawableTypeRequest = Glide.with(context).load(url);
            DrawableRequestBuilder drawableRequestBuilder = drawableTypeRequest
                    .listener(mGlideListener);
            //.diskCacheStrategy(DiskCacheStrategy.SOURCE);

            // 按image view的显示尺寸，压缩加载图片
/*            int[] dimension = CommonUtils.getViewDimensionInPx(imageView);
            int width = dimension[0];
            int height = dimension[1];
            LogMan.logDebug("Glide override width = " + width + " , height = " + height);
            if (width > 0 && height > 0) {
                drawableRequestBuilder = drawableRequestBuilder.override(width, height);
            }*/

            // 暂时只适配两种类型，通常ScaleType设为center crop，一些特殊地方使用fit center（联名卡专区）
            //if (scaleType == ImageView.ScaleType.CENTER_CROP)
            if (scaleType == ImageView.ScaleType.FIT_CENTER) {
                drawableRequestBuilder = drawableRequestBuilder.fitCenter();
            } else {
                drawableRequestBuilder = drawableRequestBuilder.centerCrop();
            }

            if (isCircle) {
                //drawableRequestBuilder = drawableRequestBuilder.bitmapTransform(new CropCircleTransformation(context));
                // 边框会作为image的一部分被缓存。因此当第一次加载不用边框，第二次加载设置边框属性，边框也不会粗来。
                /*if (border) {
                    drawableRequestBuilder = drawableRequestBuilder.transform(
                            new GlideCircleTransform(context, 1, context.getResources().getColor(R.color.colorPrimary)));
                } else {
                    drawableRequestBuilder = drawableRequestBuilder.transform(
                            new GlideCircleTransform(context));
                }*/
                drawableRequestBuilder = drawableRequestBuilder.dontAnimate();
                drawableRequestBuilder = drawableRequestBuilder.bitmapTransform(new CropCircleTransformation(context));
            }
            if (defaultResId != -1) {
                // 如果不设置默认图像，即便imageview在xml中设置了默认的，也会显示黑底的默认加载图。
                //drawableRequestBuilder = drawableRequestBuilder.placeholder(defaultResId);

                // OOM
                /*drawableRequestBuilder = drawableRequestBuilder.placeholder(transformDrawable(context,
                        defaultResId, new CropCircleTransformation(context)));
                drawableRequestBuilder = drawableRequestBuilder.error(transformDrawable(context,
                        defaultResId, new CropCircleTransformation(context)));*/

                // 仅加载url时才有placeholder的选项，因为加载本地resId或者Bitmap，基本不会发生加载延迟和加载错误
                if (isCircle) {
                    // OOM
                    // Bitmap placeholder = BitmapFactory.decodeResource(context.getResources(), defaultResId);

                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), getPlaceHolder(context));
                    circularBitmapDrawable.setCircular(true);
                    //BitmapDrawable bd = new BitmapDrawable(circularBitmapDrawable.getBitmap());
                    drawableRequestBuilder = drawableRequestBuilder.placeholder(circularBitmapDrawable);
                    drawableRequestBuilder = drawableRequestBuilder.error(circularBitmapDrawable);
                } else {
                    drawableRequestBuilder = drawableRequestBuilder.placeholder(defaultResId);
                }
            }
            if (timeStamp != -1) {
                drawableRequestBuilder = drawableRequestBuilder.signature(new StringSignature(String.valueOf(timeStamp)));
            }
            drawableRequestBuilder.into(imageView);
        } catch (Exception e) {
            LogMan.logError("error loading picture e: " + (e == null ? "" : e.getMessage()));
        }
    }

    public static Bitmap getPlaceHolder(Context context) {
        // TODO 以后再扩展参数，当前工程中需要圆形加载的最大尺寸为100x100
        if (sPlaceholder == null) {
            sPlaceholder = PictureUtils.decodeStream(context, R.drawable.logo, 100, 100);
        }
        return sPlaceholder;
    }

    private static BitmapDrawable transformDrawable(Context context,
                                                    int resourceId,
                                                    Transformation<Bitmap> transformation) {
        Drawable drawable = ContextCompat.getDrawable(context, resourceId);
        // 这里会导致OOM
        Bitmap bitmap = Bitmap.createBitmap(drawable.getMinimumWidth(), drawable.getMinimumHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawable.draw(canvas);

        Resource<Bitmap> original = BitmapResource.obtain(bitmap, Glide.get(context).getBitmapPool());
        Resource<Bitmap> rounded = transformation.transform(original, bitmap.getWidth(), bitmap.getHeight());

        if (!original.equals(rounded)) {
            original.recycle();
        }
        return new BitmapDrawable(context.getResources(), rounded.get());
    }
}
