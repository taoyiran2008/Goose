package com.taoyr.app.configs;

/**
 * Created by taoyr on 2018/7/10.
 */

public class BaseConfig {

    //    public static boolean isDebug = BuildConfig.BUILD_TYPE.equals("debug");
    public static boolean isDebug = true;

    public static final boolean LOCAL_MOCKING = true;
    public static final int MOCKING_TIME_IN_MS = 200;

    /**
     * 测试版本地址
     */
    public static String TEST_URL = "http://118.190.158.176:7000";

    /**
     * 生产版本地址
     */
    public static String URL = "http://118.190.158.176:7000";

    public static String API_SERVER = isDebug ? TEST_URL : URL;
}
