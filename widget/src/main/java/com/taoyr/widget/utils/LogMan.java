package com.taoyr.widget.utils;

import android.text.TextUtils;
import android.util.Log;

public class LogMan {
    //private static boolean LOG_DEBUG = "userdebug".equals(Build.TYPE) ;
    private static final boolean LOG_DEBUG = true;
    private static final boolean LOG_ERROR = true;
    private static final String DEFAULT_TAG = "taoyr";

    /* Android Vanilla Log Daemon*/
    public static void logDebug(String msg) {
        if (TextUtils.isEmpty(msg)) msg = "NULL";
        if (LOG_DEBUG) Log.d(DEFAULT_TAG, msg);
    }

    public static void logDebug(String tag, String msg) {
        if (TextUtils.isEmpty(msg)) msg = "NULL";
        if (LOG_DEBUG) Log.d(tag, msg);
    }

    public static void logError(String msg) {
        if (TextUtils.isEmpty(msg)) msg = "NULL";
        if (LOG_ERROR) Log.e(DEFAULT_TAG, msg);
    }

    public static void logError(Exception e) {
        if (LOG_ERROR) Log.e(DEFAULT_TAG, "Exception: e: " + e == null ? "NULL" : e.getMessage());
    }

    public static void logError(Throwable e) {
        if (LOG_ERROR) Log.e(DEFAULT_TAG, "Exception: e: " + e == null ? "NULL" : e.getMessage());
    }

    public static void logError(String tag, String msg) {
        if (TextUtils.isEmpty(msg)) msg = "NULL";
        if (LOG_ERROR) Log.e(tag, msg);
    }
}
