package com.taoyr.app.ifs;

/**
 * 包名不能用interface，这是系统保留字
 * <p>
 * 请求的数据最好格式统一，比如
 * {"errorCode":100, "errorMessage":"abc", "data":{}}
 * <p>
 * pageNum:页码，从1开始
 * pageSize:每页条目数（默认一页15条）
 */

public interface IApiService { // RetrofitService
}
