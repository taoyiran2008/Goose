package com.taoyr.app.configs;

import com.taoyr.app.utility.CommonUtils;

/**
 * Created by taoyr on 2018/7/10.
 */

public class BaseConfig {

    //    public static boolean isDebug = BuildConfig.BUILD_TYPE.equals("debug");
    //public static boolean isDebug = true;

    public static final boolean LOCAL_MOCKING = false;
    public static final int MOCKING_TIME_IN_MS = 200;

    /**
     * 测试版本地址
     */
    public static String TEST_URL = "http://192.168.8.174:8670";

    /**
     * 生产版本地址
     */
    public static String URL = "http://yellow.cash-ico.com:8670";

    public static String API_SERVER = true ? TEST_URL : URL;
}
