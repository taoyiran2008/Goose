package com.taoyr.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.Toast;

import com.mob.MobSDK;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by taoyr on 2018/3/6.
 */

public class ShareUtils {

    private static final String[] PACKAGES_SINA = {"com.sina.weibo"};
    private static final String[] PACKAGES_QQ = {"com.tencent.mobileqq", "com.tencent.mobileqqi", "com.tencent.qqlite", "com.tencent.minihd.qq", "com.tencent.tim"};
    private static final String[] PACKAGES_QZONE = {"com.tencent.mobileqq", "com.tencent.mobileqqi", "com.tencent.qqlite", "com.tencent.minihd.qq", "com.tencent.tim"};
    private static final String[] PACKAGES_WECHAT = {"com.tencent.mm"};
    private static final String[] PACKAGES_WECHAT_MOMENTS = {"com.tencent.mm"};

    public static void init(Context context) {
        // app模块没法使用MobSDK相关jar包
        MobSDK.init(context);
    }

    public static void shareLink(Context context, String shareCode, String url, PlatformActionListener listener) {
        Platform platform = ShareSDK.getPlatform(shareCode);
        Platform.ShareParams shareParams = new Platform.ShareParams();

        String shareTitle = "来自小皇叔的邀请";
        String shareUrl = TextUtils.isEmpty(url) ? "http://mob.com" : url;
        // ShareSDK一个bug，在分享到QZone、Wechat，如果文本中包含http://xxx，在分享后会被替换为
        // http://b4bh.link.sharesdk.cn/9VgiXC0J7q（mob.com）
        shareUrl = shareUrl.replaceFirst("(http://)|(https://)", "");
        String shareContent = shareTitle + ": " + shareUrl;
        /**
         * 微博分享只支持分享文本、图片、视频。而微信、QQ支持分享链接，也就是分享的内容可以在客户端内置
         * 的浏览器打开。为了兼容，我们只分享文本，并将连接隐藏在文本中，用户仍然可以通过点击连接打开。
         */
        if (SinaWeibo.NAME.equals(shareCode)) {
            if (checkShareClient(PACKAGES_SINA)) {
                shareParams.setText(url);
                shareParams.setUrl(url);
            }
        } else if (QQ.NAME.equals(shareCode)) {
            if (checkShareClient(PACKAGES_QQ)) {
                shareParams.setText(shareContent);
                shareParams.setTitle(shareTitle);
                shareParams.setTitleUrl(shareUrl);
                // shareParams.setShareType(Platform.SHARE_WEBPAGE);
                shareParams.setShareType(Platform.SHARE_TEXT);
            }
        } else if (QZone.NAME.equals(shareCode)) {
            if (checkShareClient(PACKAGES_QZONE)) {
                shareParams.setText(shareContent);
                shareParams.setTitle(shareTitle);
                shareParams.setTitleUrl(shareUrl);
                shareParams.setShareType(Platform.SHARE_TEXT);
            }
        } else if (Wechat.NAME.equals(shareCode)) {
            if (checkShareClient(PACKAGES_WECHAT)) {
                shareParams.setText(shareContent);
                shareParams.setTitle(shareTitle);
                shareParams.setShareType(Platform.SHARE_TEXT);
            }
        } else if (WechatMoments.NAME.equals(shareCode)) {
            if (checkShareClient(PACKAGES_WECHAT_MOMENTS)) {
                // 绕过审核分享到朋友圈一定要加一张图片才可以的，不然提示
                // 获取资源失败 仅支持分享照片至朋友圈。如果设置bypassApproval false，没有配置正确的appId也打不开微信朋友圈的分享界面。
                shareParams.setText(shareContent);
                shareParams.setTitle(shareTitle);
                //shareParams.setImagePath("file:///android_asset/logo.png");
                shareParams.setImageData(getPlaceHolder(context));
                shareParams.setImageUrl(shareUrl); // 必须的参数
                shareParams.setShareType(Platform.SHARE_IMAGE);
            }
        }

        platform.setPlatformActionListener(listener);
        platform.share(shareParams);
    }


    public static void shareImage(Context context, String shareCode, Bitmap imageData, String url, PlatformActionListener listener) {
        Platform platform = ShareSDK.getPlatform(shareCode);
        Platform.ShareParams shareParams = new Platform.ShareParams();
        String shareTitle = "来自小皇叔的邀请";
        String shareUrl = TextUtils.isEmpty(url) ? "http://mob.com" : url;
        // ShareSDK一个bug，在分享到QZone、Wechat，如果文本中包含http://xxx，在分享后会被替换为
        // http://b4bh.link.sharesdk.cn/9VgiXC0J7q（mob.com）
        shareUrl = shareUrl.replaceFirst("(http://)|(https://)", "");
        String shareContent = shareTitle + ": " + shareUrl;
        /**
         * 微博分享只支持分享文本、图片、视频。而微信、QQ支持分享链接，也就是分享的内容可以在客户端内置
         * 的浏览器打开。为了兼容，我们只分享文本，并将连接隐藏在文本中，用户仍然可以通过点击连接打开。
         */
        if (SinaWeibo.NAME.equals(shareCode)) {
            if (checkShareClient(PACKAGES_SINA)) {
                shareParams.setText(url);
                shareParams.setUrl(url);
            }
        } else if (QQ.NAME.equals(shareCode)) {
            if (checkShareClient(PACKAGES_QQ)) {
                shareParams.setText(shareContent);
                shareParams.setTitle(shareTitle);
                shareParams.setTitleUrl(shareUrl);
                shareParams.setImageUrl("http://image.cash-ico.com/2018/11/13/975a5e7ccfeb11dc4939bc2e2431a015.jpg_thumb.webp");
                shareParams.setShareType(Platform.SHARE_IMAGE);
            }
        } else if (QZone.NAME.equals(shareCode)) {
            if (checkShareClient(PACKAGES_QZONE)) {
                shareParams.setText(shareContent);
                shareParams.setTitle(shareTitle);
                shareParams.setTitleUrl(shareUrl);
                shareParams.setImageData(imageData);
                shareParams.setShareType(Platform.SHARE_TEXT);
            }
        } else if (Wechat.NAME.equals(shareCode)) {
            if (checkShareClient(PACKAGES_WECHAT)) {
                shareParams.setText(shareContent);
                shareParams.setTitle(shareTitle);
                shareParams.setShareType(Platform.SHARE_TEXT);
            }
        } else if (WechatMoments.NAME.equals(shareCode)) {
            if (checkShareClient(PACKAGES_WECHAT_MOMENTS)) {
                // 绕过审核分享到朋友圈一定要加一张图片才可以的，不然提示
                // 获取资源失败 仅支持分享照片至朋友圈。如果设置bypassApproval false，没有配置正确的appId也打不开微信朋友圈的分享界面。
                shareParams.setText(shareContent);
                shareParams.setTitle(shareTitle);
                //shareParams.setImagePath("file:///android_asset/logo.png");
                shareParams.setImageData(getPlaceHolder(context));
                shareParams.setImageUrl(shareUrl); // 必须的参数
                shareParams.setShareType(Platform.SHARE_IMAGE);
            }
        }

        platform.setPlatformActionListener(listener);
        platform.share(shareParams);
    }

    @SuppressLint("WrongConstant")
    public static boolean checkShareClient(String[] pkgs) {
        int length = pkgs.length;
        PackageInfo pi = null;

        for (int i = 0; i < length; i++) {
            String packages = pkgs[i];
            try {
                pi = MobSDK.getContext().getPackageManager().getPackageInfo(
                        packages, PackageManager.GET_RESOLVED_FILTER);
                if (pi != null) {
                    break;
                }
            } catch (Throwable t) {
            }
        }
        if (pi == null) {
            Toast.makeText(MobSDK.getContext(), "分享的客户端没安装或版本过低", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private static Bitmap sPlaceholder;

    private static Bitmap getPlaceHolder(Context context) {
        // TODO 以后再扩展参数，当前工程中需要圆形加载的最大尺寸为100x100
        if (sPlaceholder == null) {
            sPlaceholder = decodeStream(context, R.drawable.logo, 100, 100);
        }
        return sPlaceholder;
    }

    private static Bitmap decodeStream(Context context, int resId, int width, int height) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), resId, opts);

            if (opts.outWidth > width && opts.outHeight > height) {
                opts.inSampleSize = calculateInSampleSize(opts, width, height);
                opts.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeResource(context.getResources(), resId, opts);
            } else {
                bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            }
        } catch (Exception e) {
        }
        return bitmap;
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
