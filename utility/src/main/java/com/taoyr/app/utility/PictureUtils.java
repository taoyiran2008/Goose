package com.taoyr.app.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 一些独立的方法从PictureLoader中分离出来，以免其显得过于臃肿。
 */

public class PictureUtils {

    public static File createTempFile(Context context, String fileName) {
        File file = CommonUtils.getCacheFile(context, fileName);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            LogMan.logError(e.getMessage());
        }

        return file;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * InputStream转byte[] 数组，用于将下载图片获得的inputstream，转换成byte[]数组，进行编码
     */
    public static byte[] readStream(InputStream inStream) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            return outStream.toByteArray();
        } catch (IOException e) {
            LogMan.logError("readStream error: " + e.getMessage());
        } finally {
            try {
                if (outStream != null) outStream.close();
                if (inStream != null) inStream.close();
            } catch (IOException e) {
                LogMan.logError("io close error: " + e.getMessage());
            }
        }

        return null;
    }

    public static String hashKeyForDisk(String key) {
        String cacheKey;
        if (key == null) {
            return "";
        }
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        // 并不是因为hash后的文件名过长，导致4a2da3d.0.tmp丢失tmp后缀
        // 是glide下的DiskLruCache有问题
        // cacheKey = cacheKey.substring(0, 2);
        return cacheKey;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    // 下载URL地址下的图片到文件系统
    public static boolean downloadUrlToFile(String imgUrl, String filePath) {
        HttpURLConnection connection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            URL url = new URL(imgUrl);
            connection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(connection.getInputStream());
            out = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            out.flush();
            return true;
        } catch (final Exception e) {
            LogMan.logError("downloadUrlToFile error: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (out != null) out.close();
                if (in != null) in.close();
            } catch (IOException e) {
                LogMan.logError("close io error: " + e.getMessage());
            }
        }

        return false;
    }

    public static InputStream getPictureInputStream(String imgUrl) {
        HttpURLConnection connection = null;
        BufferedInputStream in = null;
        try {
            URL url = new URL(imgUrl);
            connection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(connection.getInputStream());
            return in;
        } catch (final Exception e) {
            LogMan.logError("getPictureInputStream error: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (in != null) in.close();
            } catch (final IOException e) {
                LogMan.logError("close io error: " + e.getMessage());
            }
        }
        return in;
    }

    // 下载从URL读取获得的图片字节流输出到OutputStream中，用于LruDisckCache
    public static boolean cachePictureStream(InputStream input, OutputStream output) {
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(input);
            out = new BufferedOutputStream(output);
            byte[] buffer = new byte[512];
            int len = in.read(buffer);
            while (len != -1) {
                out.write(buffer, 0, len);
                len = in.read(buffer);
            }
            out.flush();
            return true;
        } catch (final Exception e) {
            LogMan.logError("cachePictureStream error: " + e.getMessage());
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
            } catch (final IOException e) {
                LogMan.logError("close io error: " + e.getMessage());
            }
        }
        return false;
    }

    public static Bitmap base64StringToBitmap(String base64Data) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(base64Data, Base64.DEFAULT);
            //bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            bitmap = decodeStream(bitmapArray, 500, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getScaledBitmap(Context context, Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, dip2px(context, width), dip2px(context, height), true);
    }

    public static void saveImageToAlbum(Context context, Bitmap bmp) {
        try {
            // 首先保存图片
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String fileName = System.currentTimeMillis() + ".jpg";
            File file = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // 这一行不需要，不然会同时save两张图片，并显示到相册（图库）中
            /*// 把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file
                    .getAbsolutePath(), fileName, null);*/

            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + file.getPath())));

            Toast.makeText(context, "图片保存成功", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "图片保存失败", Toast.LENGTH_LONG).show();
        }
    }

    /*==================================压缩图片相关 START==================================*/

    public static final int DEFAULT_WIDTH = 480;
    public static final int DEFAULT_HEIGHT = 800;

    public static File compressAndSaveImage(Context context, String filePath, String fileName, int quality, int width, int height) {
        Bitmap bm = getCompressedBitmap(filePath, width, height); // 获取一定尺寸的图片
        int degree = readPictureDegree(filePath); // 获取相片拍摄角度
        if (degree != 0) { // 旋转照片角度，防止头像横着显示
            // 有createBitmap的处理，但是图片已经是压缩过后的，就不存在内存溢出的问题了
            bm = rotateBitmap(bm, degree);
        }
        File file = CommonUtils.getCacheFile(context, fileName); // "profile_image.jpg"

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
            // out.flush, out.close 会在compress中处理
        } catch (Exception e) {
        }
        return file;
    }

    /**
     * 适用的case：
     * 选择本地图像上传时，从本地图库或者照相获得的picture，直接处理bitmap，而不是将它先保存到临时
     * 文件，然后再使用compressAndSaveImage(String filePath)来读取和处理，多此一举。
     */
    public static File compressAndSaveImage(Context context, Bitmap bitmap, String fileName,
                                            int quality, int width, int height) {
        Bitmap bm = getCompressedBitmap(bitmap, quality, width, height);
        int degree = readPictureDegree(bitmap);
        if (degree != 0) { // API LEVEL 24以下不支持判断bitmap的方向（除非将它保存为文件）
            bm = rotateBitmap(bm, degree);
        }
        File file = CommonUtils.getCacheFile(context, fileName);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
            // out.flush, out.close 会在compress中处理
        } catch (Exception e) {
        }
        return file;
    }

    /**
     * 压缩Bitmap
     */
    public static Bitmap getCompressedBitmap(Bitmap bitmap, int quality, int width, int height) {
        // 解析图像的原始宽高
        final BitmapFactory.Options options = new BitmapFactory.Options();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        options.inJustDecodeBounds = true; // 只解析图片的bounds，获取宽高
        byte[] bytes = baos.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, width, height);

        // 按照缩放比压缩图片，并返回bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        // 有更简单的压缩bitmap的方法，但是不适用于图片过大的情况，因为这种做法是需要将整个图片
        // 加载到内存中作处理的。
        //Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    /**
     * 拍照后 压缩图片奔溃  修复方法
     * @param path
     * @return
     */
    public static Bitmap decodeStream(String path) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        // Do not compress
        newOpts.inSampleSize = 1;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(path, newOpts);

    }

    /**
     * 根据imageview宽高 decode 图片
     */
    public static Bitmap decodeStream(InputStream is) {
        Bitmap bitmap = null;
        try {

            //解决java.io.IOException: mark/reset not supported bug
            if(!is.markSupported()) {
                is = new BufferedInputStream(is);
            }
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;

            is.mark(0);
            // 避免使用decodeStream(bytes)时，因为baos.write，出现OOM
            BitmapFactory.decodeStream(is, null, opts);
            is.reset();

            opts.inSampleSize = calculateInSampleSize(opts,100, 100);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(is, null, opts);
        } catch (Exception e) {
            LogMan.logError("Exception: " + e.getMessage());
        } finally {
            try{
                is.close();
            }catch (Exception e){
                LogMan.logError("Exception: " + e.getMessage());
            }
        }
        return bitmap;
    }

    public static Bitmap decodeStream(InputStream is, int width, int height) {
        Bitmap bitmap = null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = null;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            bis = new BufferedInputStream(is);
            baos = new ByteArrayOutputStream();

            byte buffer[] = new byte[1024];
            int len;
            while ((len = bis.read(buffer, 0, buffer.length)) > 0) {
                baos.write(buffer, 0, len);
            }
            byte[] imageData = baos.toByteArray();
            BitmapFactory.decodeByteArray(imageData, 0, imageData.length, opts);

            if (opts.outWidth > width && opts.outHeight > height) {
                opts.inSampleSize = calculateInSampleSize(opts, width, height);
                opts.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, opts);
            } else {
                bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            }
        } catch (Exception e) {
            LogMan.logError("Exception: " + e.getMessage());
        } finally {
            try{
                bis.close();
                baos.close();
                is.close();
            }catch (Exception e){
                LogMan.logError("Exception: " + e.getMessage());
            }
        }
        return bitmap;
    }

    public static Bitmap decodeStream(byte[] bytes, int width, int height) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            byte[] imageData = bytes;
            BitmapFactory.decodeByteArray(imageData, 0, imageData.length, opts);

            // 仅当原始图片尺寸大，而需要展示的图片尺寸小，需要按比例压缩时，才计算采样率。如果是
            // 原图不大，则不计算采样率，直接按原图的尺寸加载。
            // 不然会造成OOM。比如圆形头像的控件，其长宽始终等于屏幕像素尺寸，如果计算采样率，
            // 会导致占用的内存过大。
            // TODO 处理原始图片尺寸和展示尺寸都巨大时的情况
            if (opts.outWidth > width && opts.outHeight > height) {
                opts.inSampleSize = calculateInSampleSize(opts, width, height);
                opts.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, opts);
            } else {
                bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            }
        } catch (Exception e) {
            LogMan.logError("Exception: " + e.getMessage());
        }
        return bitmap;
    }

    public static Bitmap decodeStream(Context context, int resId, int width, int height) {
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
            LogMan.logError("Exception: " + e.getMessage());
        }
        return bitmap;
    }

    public static Bitmap getCompressedBitmap(String filePath, int width, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options);
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, width, height);
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }


    /**
     * 获取照片角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static int readPictureDegree(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int degree = 0;
        try {
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            ExifInterface exifInterface;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                exifInterface = new ExifInterface(is);
                int orientation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转照片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) { // Not degress
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degrees);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    // 根据显示控件的大小计算采样率
    // 如果用imageView.getMesuredHeight, 返回值为像素大小px（2200），这样反而会使得采样率变高，使得
    // 压缩后的图片比原图更大。因此如果要使用，应该使用dp为单位
    public static int calculateInSampleSize(
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

    /*==================================压缩图片相关 END==================================*/
}
