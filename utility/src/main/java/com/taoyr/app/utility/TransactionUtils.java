package com.taoyr.app.utility;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taoyr on 2018/2/8.
 * 业务逻辑(事务)相关代码
 */

public class TransactionUtils {

    /**
     * 将天风文章，研报，资讯，这几种不同的数据结构，解析成我们需要的数据结构。
     */
    public static <T> List<T> parseData(List<JsonObject> list, Class<T> clz) {
        List<T> infoList = new ArrayList<>();
        if (list != null) {
            for (JsonObject obj : list) {
                infoList.add(parseData(obj, clz));
            }
        }
        return infoList;
    }

    /**
     * 将天风文章，研报，资讯，这几种不同的数据结构，解析成我们需要的数据结构。
     */
    public static <T> T parseData(JsonObject obj, Class<T> clz/*T clz*/) {
        try {
            Gson gson = new Gson();
            T info = gson.fromJson(obj, clz);
            return info;
        } catch (Exception e) {
            return null;
        }
    }
}
