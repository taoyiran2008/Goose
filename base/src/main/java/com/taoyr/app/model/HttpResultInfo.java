package com.taoyr.app.model;

/**
 * Created by taoyr on 2018/1/4.
 *
 * 请求返回格式统一为：
 * {"success": true/false, "resultCode":"10000", "resultDesc":"error", "result":{}|[]}
 *
 * 合理的Info后缀还是沿用之前的开发惯例。
 * XXXInfo 网络请求原始数据转换后的实体
 * XXXVo 网络请求作为传递的实体
 * XXXStruct 本地自定义的实体（可用于保存网络请求实体中我们需要的属性字段）
 */

public class HttpResultInfo<T> {

    public String code; // 200
    public String message;
    public T datas;
}
