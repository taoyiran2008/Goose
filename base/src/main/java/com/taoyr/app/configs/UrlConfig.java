package com.taoyr.app.configs;

/**
 * Created by taoyr on 2018/1/4.
 */

public class UrlConfig {

    // java.lang.IllegalArgumentException: baseUrl must end in /: http://47.104.130.10:7000/server/index.php
    //public static final String BASE_URL = "http://47.104.130.10:6900/";
    public static final String BASE_URL = "http://118.190.158.176:7000";

    /**
     * 下载文件。
     * http://118.190.158.176:7000/tf/file/get?filePath=2018/2/4/15/58/30/1517731110327.jpg
     *
     * 不支持
     * http://118.190.158.176:7000/tf/file/2018/2/4/15/58/30/1517731110327.jpg
     */
    public static String getServerFilePath(String relativePath) {
        return String.format("%s/tf/file/get?filePath=%s", BASE_URL, relativePath);
    }

    public static String getResearchAttachmentPath(String relativePath) {
        return String.format("%s/tf/research-reports/attachment/get?filePath=%s", BASE_URL, relativePath);
    }

    public static String getArticleDetailUrl(String id) {
        return String.format("http://infosim.dangdaifintech.com/tybody.html?id=%s", id);
    }

    // 研报，资讯的链接。天风文章使用getArticleDetailUrl。
    public static String getShareLinkPath(String id) {
        return String.format("%s/tf/article/%s.html", BASE_URL, id);
    }

    public static String getEventShareLinkPath(String id) {
        return String.format("%s/tf/event/%s.html", BASE_URL, id);
    }
}
