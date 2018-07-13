package com.taoyr.app.utility;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taoyr.utility.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static android.os.Build.VERSION_CODES.N;

/**
 * Created by taoyr on 2017/4/26.
 */

public class CommonUtils {

    public static String readFileFromAssets(Context context, String fileName) {
        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            return new String(buffer, "UTF-8");
        } catch (Exception e) {
            // e.printStackTrace();
            LogMan.logDebug(e.getMessage());
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
            }
        }
        return "";
    }

    public static int[] getScreenDimensionInPx(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        /*int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();*/
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }

    /**
     * 获取View的2D尺寸（宽和高），解决页面中的控件因为没有初始化完毕，得不到正确的大小（通常返回0）。
     * @return 包含控件宽高的二维数组，width = int[0], height = int[1]。
     */
    public static int[] getViewDimensionInPx(View view) {
        int[] dimension = new int[2];
        int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(measureSpec, measureSpec);
        dimension[0] = view.getMeasuredWidth();
        dimension[1] = view.getMeasuredHeight();
        return dimension;
    }

    public static int[] getViewDimensionInPx2(View view) { // -1, 630(Carousel image)
        int[] dimension = new int[2];
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        dimension[0] = lp.width;
        dimension[1] = lp.height;
        return dimension;
    }

    public static int[] getViewDimensionInPx3(final View view) {
        final CountDownLatch latch = new CountDownLatch(1);
        final int[] dimension = new int[2];
        view.post(new Runnable() {
            @Override
            public void run() {
                dimension[0] = view.getWidth();
                dimension[1] = view.getHeight();
                latch.countDown();
            }
        });
        try {
            // latch.wait(1000); object.wait()
            latch.await(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
        return dimension;
    }

    public static void showSoftKeyBoard(Activity activity, boolean show) {
        LogMan.logDebug("showSoftKeyBoard");
        InputMethodManager imm = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (activity.getCurrentFocus() == null) {
            LogMan.logDebug("getCurrentFocus is null!");
            return;
        }

        if (show) {
            imm.showSoftInput(activity.getCurrentFocus(), InputMethodManager.SHOW_FORCED);
        } else {
            // imm.hideSoftInputFromInputMethod();
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    0);
        }
    }

    public static void setViewScale(View view, int width, int height) {
        int w = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, width,
                view.getContext().getResources().getDisplayMetrics());
        int h = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, height,
                view.getContext().getResources().getDisplayMetrics());

        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = w;
        lp.height = h;

        if (view instanceof ImageView) {
            ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
            ((ImageView) view).setAdjustViewBounds(true);
        }

        view.setLayoutParams(lp);
    }

    public static String getHashKey(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
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

    public static boolean isLetter(char c) {
        return c <= 'z' && c >= 'A';
    }

    public static boolean isLetter(String str) {
        return str.matches("[a-zA-Z]+");
    }

    public static boolean isHanzi(String str) {
        return str.matches("[\\u4E00-\\u9FA5]+");
    }

    public static void hightlightWithRedStar(TextView textView, String content) {
        // Null check
        if (TextUtils.isEmpty(content) || textView == null) return;

        String suffix = "*";
        if (!content.endsWith(suffix)) {
            content += suffix;
        }

        SpannableStringBuilder style = new SpannableStringBuilder(content);
        int highLightColor = textView.getContext().getResources().getColor(R.color.red);
        int begin = content.lastIndexOf("*");
        int end = begin + 1;
        style.setSpan(new ForegroundColorSpan(highLightColor), begin, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(style);
    }

    public static void hightlightDynamicText(TextView textView, int begin, int end, int colorResId) {
        String text = textView.getText().toString();
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        int highLightColor = textView.getContext().getResources().getColor(colorResId);
        style.setSpan(new ForegroundColorSpan(highLightColor), begin, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(style);
    }

    /**
     * 将文本中的一段特定字符串标色。
     * NOT 如果textview被放到scrollview中，使用spannable渲染后，如果文本超长换行，有一部分会不可见。
     * 因为横向的linearlayout，一个checkbox为wrap cotent，不知道为什么，它会限制textview的高度。
     */
    public static void underlineDynamicText(TextView textView, String content,
                                            final View.OnClickListener listener, final int colorResId) {
        String text = textView.getText().toString();
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        final int highLightColor = textView.getContext().getResources().getColor(colorResId);
        int begin = text.indexOf(content);
        int end = begin + content.length();

        style.setSpan(new ClickableSpan() {
                          @Override
                          public void onClick(View widget) {
                              listener.onClick(widget);
                          }

                          public void updateDrawState(TextPaint ds) {
                              ds.setColor(highLightColor);
                              // 去除超链接的下划线
                              ds.setUnderlineText(false);
                          }
                      }, begin, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(style);
        // 不然onClick无效
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
    }

    public static void underlineDynamicText(TextView textView, String content,
                                            final View.OnClickListener listener) {
        String text = textView.getText().toString();
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        int begin = text.indexOf(content);
        int end = begin + content.length();

        style.setSpan(new ClickableSpan() {
                          @Override
                          public void onClick(View widget) {
                              listener.onClick(widget);
                          }

                          public void updateDrawState(TextPaint ds) {
                              ds.setUnderlineText(true);
                          }
                      }, begin, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(style);
        // 不然onClick无效
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
    }

    public static boolean isInMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }

    /**
     * 检查入力的姓/名是否合法，比如不能有数字，不能有特殊字符。一期只对应中英文。
     */
    public static boolean checkNameValidity(String name) {
        if (name == null) return false;
        String regex = "^([\\u4e00-\\u9fa5]+|([a-zA-Z]+\\s?)+)$";
        return name.trim().matches(regex);
    }

    public static boolean checkPhoneValidity(String phone) {
        if (phone == null) return false;
        String regex = "^(13[4,5,6,7,8,9]|15[0,8,9,1,7]|188|187)\\d{8}$"; // 中国境内号码检测，分别对应电信，移动，联通
        //String regex = "^\\d+-?\\d+$"; // 允许数字和中划线
        return phone.trim().matches(regex);
    }

    /**
     * Unit Test Case:
     * print(checkEmailValidity("sfsf@12.com"));
     * print(checkEmailValidity("taf.2008@sf.com"));
     * print(checkEmailValidity("it_wangmeng@sf.com"));
     * print(checkEmailValidity("wuwu-dfdf@sf.com"));
     */
    public static boolean checkEmailValidity(String email) {
        if (email == null) return false;
        String regex = "^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return email.trim().matches(regex);
    }

    /**
     * 获取到需要持久储存的文件句柄
     */
    public static File getStorageFile(Context context, String fileName) {
        String repoDir;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                repoDir = context.getExternalFilesDir(null).getPath();
            } else {
                repoDir = context.getFilesDir().getPath();
            }
        } catch (Exception e) {
            // getExternalFilesDir可能在某些机型不适用
            repoDir = context.getFilesDir().getPath();
        }

        return new File(repoDir, fileName);
    }

    public static File getExternalFile(String fileName) {
        return new File(Environment.getExternalStorageDirectory().getPath(), fileName);
    }

    public static File getCacheFile(Context context, String fileName) {
        String cachePath;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                cachePath = context.getExternalCacheDir().getPath();
            } else {
                cachePath = context.getCacheDir().getPath();
            }
        } catch (Exception e) {
            // 在某些机种上，context.getExternalCacheDir()反空。
            // cachePath = context.getFilesDir().getPath();
            cachePath = context.getCacheDir().getPath();
        }

        return new File(cachePath, fileName);
    }

    public static String getDeviceToken(Context context) {
        String device_id = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            String tmDevice = "", tmSerial = "", androidId = "";
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                tmDevice = "" + tm.getDeviceId();
                tmSerial = "" + tm.getSimSerialNumber();
            }

            androidId = "" + android.provider.Settings.Secure.getString(
                    context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            UUID deviceUuid = new UUID(androidId.hashCode(),
                    ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            device_id = deviceUuid.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return device_id;
    }

    public static void sendEmailWithAttachment(Context context, String emailTitle,
                                               String emailContent, File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        // 这种二进制流的形式，系统调出来的默认打开程序会很多。
        // intent.setType("application/octet-stream");
        intent.setType("message/rfc882");
        // 如果不设置下面的flag，红米手机会抛出：
        // java.lang.RuntimeException: Unable to start xxx, Calling startActivity() from outside of
        // an Activity context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // 邮件接收者（数组，可以是多位接收者）
        //String[] receiver = new String[]{"123@share_qq.com", "456@163.com"};
        //intent.putExtra(android.content.Intent.EXTRA_EMAIL, receiver);

        // 设置邮件标题
        intent.putExtra(Intent.EXTRA_SUBJECT, emailTitle);

        // 设置发送的内容
        intent.putExtra(Intent.EXTRA_TEXT, emailContent);

        // android.os.FileUriExposedException: file:///storage/emulated/0/Android/data/
        // com.ceair.ticketapp/files/visa_service_pdf.apk exposed beyond app through ClipData.Item.getUri()
        if (Build.VERSION.SDK_INT >= N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        // 添加附件
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

        // 调用系统的邮件
        context.startActivity(intent);
        //context.startActivity(Intent.createChooser(intent, "请选择邮件发送软件"));
    }

    /**
     * 用于判断文件是否过期。
     */
    public static boolean checkIfTimestampExpired(Context context, String id, long timeoutInMs) {
        SharedPreferences sp = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        String timestampKey = "timestamp_" + id;
        long lastUpdateTime = sp.getLong(timestampKey, 0);
        long currentTime = new Date().getTime();
        long timeSpan = currentTime - lastUpdateTime;

        if (lastUpdateTime == 0) { // 第一次保存时间戳
            // sp.edit().putLong(timestampKey, currentTime).apply();
            // 标记时间戳，应该在响应的业务逻辑中做。比如下载pdf成功并保存后。
        } else {
            if (timeSpan > timeoutInMs) {
                return true;
            }
        }
        return false;
    }

    public static void addTimestamp(Context context, String id) {
        SharedPreferences sp = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        String timestampKey = "timestamp_" + id;
        long currentTime = new Date().getTime();
        sp.edit().putLong(timestampKey, currentTime).apply();
    }

    public static void resetTimestamp(Context context, String id) {
        SharedPreferences sp = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        String timestampKey = "timestamp_" + id;
        sp.edit().putLong(timestampKey, 0).apply();
    }

    /**
     * 用于强制更新头像的缓存。
     */
    public static long getImageTimeStamp(Context context) {
        SharedPreferences sp = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        long value = sp.getLong("image_time_stamp", System.currentTimeMillis());
        return value;
    }

    public static void updateTimeStamp(Context context) {
        SharedPreferences sp = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        sp.edit().putLong("image_time_stamp", System.currentTimeMillis()).commit();
    }

    public static void backgroundLightOff(Activity activity, boolean lightOff) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (lightOff) {
            lp.alpha = 0.3f;
        } else {
            lp.alpha = 1.0f;
        }
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 适用于所有的带点击事件的View，可以不用写selector，实现enable/disable状态下的不同效果
     */
    public static void disableView(View view, boolean disable) {
        if (disable) {
            view.setEnabled(false);
            view.setAlpha(0.4f);
        } else {
            view.setEnabled(true);
            view.setAlpha(1.0f);
        }
    }

    /**
     * 根据Class的泛型类别，返回其对应的Class字节码。支持
     * Callback1 implements Callback<T> {}
     * Base1 extends Base<T> {}
     * JAVA1.5后才支持getGenericSuperclass。
     */
    public static Class getSuperClassGenricType(Class clazz) throws IndexOutOfBoundsException {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            genType = clazz.getGenericInterfaces()[0]; // 可能是implements interface(Callback)
            if (!(genType instanceof ParameterizedType)) {
                return Object.class;
            }
        }

        Type[] params = new Type[0];
        try {
            params = ((ParameterizedType) genType).getActualTypeArguments();
            return (Class) params[0];
        } catch (Exception e) {
            return Object.class;
        }
    }

    /**
     * 设定窗口为沉浸模式（透明/全屏）。设定后，只影响被设定的Activity。
     * 在setContent之前调用。
     */
    public static void setFullScreen(Activity activity) {
        Window window = activity.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * @param et
     * @param show
     */
    public static void showSoftKeyboard(EditText et, boolean show) {
        InputMethodManager imm = (InputMethodManager)
                et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            imm.showSoftInput(et, 0);
            // imm.showSoftInputFromInputMethod(editText.getWindowToken(),0);
            // activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } else {
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
        }
    }

    /**
     * 判断class为用户自定义的class，还是java源码自带的class。主要用于json自动解析时，仅自定义的
     * struct才能够被gson正确的解析。
     */
    public static boolean isJavaClass(Class<?> clz) {
        return clz != null && clz.getClassLoader() == null;
    }

    /**
     * 这个方法有个bug：adapter里设置的图片，偶尔会不显示。
     */
    public static void setVectorImage(ImageView imageView, int svgId, int colorId, int width, int height) {
        Context context = imageView.getContext();
        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(
                context.getResources(), svgId, context.getTheme());

        vectorDrawableCompat.setTint(context.getResources().getColor(colorId));
        // setBounds 不好用
        //vectorDrawableCompat.setBounds(0, 0, width, height);
        //imageView.setImageDrawable(vectorDrawableCompat);

        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        lp.width = PictureUtils.dip2px(context, width);
        lp.height = PictureUtils.dip2px(context, height);
        imageView.setBackgroundDrawable(vectorDrawableCompat);
    }

    public static void requestFocusAndShowKeyboard(final EditText view) {
        //view.getHandler() // NPE
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        showSoftKeyboard(view, true);
    }

    // 通用的动画逻辑
    public static void expandView(final LinearLayout content, ImageView arrow, boolean expand) {
        content.measure(0, 0);
        int contentHeight = content.getMeasuredHeight();
        content.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        if (expand) {
            arrow.animate().rotation(180).setDuration(300).start();
            ObjectAnimator.ofInt(new AnimationWrapper(content), "height", contentHeight).setDuration(300).start();
            // 当我们需要初始状态为收缩状态时，比较取巧的做法是，在外部将content设置为不可见GONE（竟然还能测出高度）
            // ，这样不会影响我们获取其高度。
            content.postDelayed(new Runnable() {
                @Override
                public void run() {
                    content.setVisibility(View.VISIBLE);
                }
            }, 100);
        } else {
            arrow.animate().rotation(0).setDuration(300).start();
            ObjectAnimator.ofInt(new AnimationWrapper(content), "height", 0).setDuration(300).start();
        }
        content.setLayerType(View.LAYER_TYPE_NONE, null);
    }

    private static class AnimationWrapper {
        View mTarget;

        public AnimationWrapper(View target) {
            mTarget = target;
        }

        public int getHeight() {
            return mTarget.getLayoutParams().height;
        }

        public void setHeight(int height) {
            mTarget.getLayoutParams().height = height;
            mTarget.requestLayout();
        }
    }

    public static String getVerName(Context context) {
        String versionName = "-1";
        try {
            String packageName = context.getPackageName();
            versionName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (Exception e) {
        }
        return versionName;
    }

    public static void launchAppMarket(Context context, String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg))
                return;
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg))
                intent.setPackage(marketPkg);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            LogMan.logError("launchAppMarket error, e: " + e.getMessage());
        }
    }

/*    public static void setIndicatorMargin(TabLayout tab) {
        LinearLayout linearLayout = (LinearLayout) tab.getChildAt(0);
        int tabWidth = CommonUtils.getViewDimensionInPx(linearLayout.getChildAt(0))[0];
        setIndicatorMargin(tab, tabWidth/2, tabWidth/2);
    }*/

    public static void setIndicatorMargin(TabLayout tabs, final int tabMarginDp) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (Exception e) {
            LogMan.logError(e);
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            LogMan.logError(e);
        }

        //int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        //int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            final View tabView = llTab.getChildAt(i);

            tabView.setPadding(0, 0, 0, 0);
            tabs.post(new Runnable() {
                @Override
                public void run() {
                    // 字多宽线就多宽，所以测量mTextView的宽度
                    int width = 0;
                    try {
                        Field textViewField = tabView.getClass().getDeclaredField("mTextView");
                        textViewField.setAccessible(true);
                        TextView textView = (TextView) textViewField.get(tabView);
                        textView.setPadding(0, 0, 0, 0);
                        width = textView.getWidth();
                        if (width == 0) {
                            textView.measure(0, 0);
                            width = textView.getMeasuredWidth();
                        }
                    } catch (Exception e) {
                        LogMan.logError(e);
                    }

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                    //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                    //params.width = width ;

                    int margin = PictureUtils.dip2px(tabView.getContext(), tabMarginDp);
                    params.leftMargin = margin;
                    params.rightMargin = margin;
                    tabView.setLayoutParams(params);
                    tabView.invalidate();
                }
            });
        }
    }

    public static void tuneIndicatorMode(final TabLayout tabs) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (Exception e) {
            LogMan.logError(e);
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            LogMan.logError(e);
        }

        final View tabView = llTab.getChildAt(llTab.getChildCount() - 1);
        // 需要先将tab layout设置为可滚动的布局，这样内容的宽度为内容所占的实际宽度
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.post(new Runnable() {
            @Override
            public void run() {
                int screenWidth = getScreenDimensionInPx(tabs.getContext())[0];
                if (tabView.getRight() >= screenWidth - 10) {
                    tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
                    setIndicatorMargin(tabs, 6);
                } else {
                    tabs.setTabMode(TabLayout.MODE_FIXED);
                    tabs.setTabGravity(TabLayout.GRAVITY_FILL);
                }
            }
        });
    }

    /**
     * 动态调整图像的高度，适用于banner等宽度为全屏的控件，并根据宽高的比例，动态的调整控件高度。
     */
    public static void tuneHeightRatio(final Context context, final View view, final int height, final int width){ // 300/750
        view.post(new Runnable() {
            @Override
            public void run() {
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                /*int measureSpec = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
                view.measure(measureSpec, measureSpec);
                int originWidth = view.getMeasuredWidth();
                int originHeight = view.getMeasuredHeight();*/
                //int originWidth = CommonUtils.getViewDimensionInPx(view)[0];
                int originWidth = view.getWidth();
                // 在OPP R9s手机上，通过view.getWidth()，有的recycler item view的宽度获取为0
                if (originWidth <= 0) {
                    originWidth = getViewDimensionInPx2(view)[0];
                }

                int screenWidth = wm.getDefaultDisplay().getWidth();

                // 进行等比缩放，宽度始终等于屏幕宽度的80%。不考虑高度大于宽度的情况（会员卡原图宽高比例是3:2）
                // 注意两个整形相除，不会保留小数，当除数大于被除数时，等于0
                double ratio = (double) height / width;
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                //lp.width = screenWidth;
                //lp.height = (int) (screenWidth * ratio);
                lp.height = (int) (originWidth * ratio);
                view.setLayoutParams(lp);
            }
        });
    }

    public static void scrollToBottom(final View scroll, final View inner) {
        scroll.post(new Runnable() {
            public void run() {
                if (scroll == null || inner == null) {
                    return;
                }
                int offset = inner.getMeasuredHeight() - scroll.getHeight();
                if (offset < 0) {
                    offset = 0;
                }

                scroll.scrollTo(0, offset);
            }
        });
    }

    /**
     * {RESEARCH_REPORT:3,PRODUCT:4,THEME:4,ANALYST:3}
     * 经过该函数url编码 -> %7BRESEARCH_REPORT%3A3%2CPRODUCT%3A4%2CTHEME%3A4%2CANALYST%3A3%7D
     * 但是retrofit log请求的参数却是
     * %257BRESEARCH_REPORT%253A3%252CPRODUCT%253A4%252CTHEME%253A4%252CANALYST%253A3%257D
     * 这是第二次url编码的结果。
     * 前端需要二次编码，这样才能保证最终结果是ASCII，不然特殊字符还是有问题。
     */
    public static String urlEncode(String origin) {
        String str = "";
        try {
            str = URLEncoder.encode(origin/*new String(origin.getBytes(), "UTF-8")*/, "UTF-8");
        } catch (Exception e) {
        }
        return str;
    }

    public static void requestPermissions(Activity activity) {
        int REQUEST_PERMISSIONS = 1;
        String[] PERMISSIONS_REQUIRED = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE, // getDeviceToken
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.SYSTEM_ALERT_WINDOW
        };

        // We don't have permission so prompt the user
        ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_REQUIRED,
                REQUEST_PERMISSIONS
        );
    }

    public static void requestPermission(Activity activity, String permission) {
        int REQUEST_PERMISSIONS = 1;
        // new String[] {permission}
        String[] PERMISSIONS_REQUIRED = {
                permission
        };

        // We don't have permission so prompt the user
        ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_REQUIRED,
                REQUEST_PERMISSIONS
        );
    }
}
