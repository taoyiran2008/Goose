package com.goose.app.ifs;

import com.google.gson.JsonObject;
import com.goose.app.model.BannerInfo;
import com.goose.app.model.CategoryInfo;
import com.goose.app.model.PictureDetailInfo;
import com.goose.app.model.PictureInfo;
import com.goose.app.model.sign.LastSignInfo;
import com.taoyr.app.model.HttpResultInfo;
import com.taoyr.app.model.UserDetailInfo;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

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

    @POST("api/user/login")
    Observable<HttpResultInfo<UserDetailInfo>> login(
            @Query("username") String username, @Query("password") String password
    );

    @POST("api/user/register")
    Observable<HttpResultInfo<UserDetailInfo>> register(
            @Query("username") String username, @Query("password") String password
    );

    @POST("api/user/update")
    Observable<HttpResultInfo> updateUser(
            @Query("id") String id, @Query("displayName") String displayName,
            @Query("file") String file
    );

    @Multipart
    @POST("api/upload/file")
    Observable<HttpResultInfo<JsonObject>> uploadFile(
            @Part MultipartBody.Part file
    );

    @POST("api/category/list")
    Observable<HttpResultInfo<List<CategoryInfo>>> getCategoryList(@Query("type") String type);

    @POST("api/product/queryProduct")
    Observable<HttpResultInfo<List<PictureInfo>>> getProductList(
            @Query("type") String type,
            @Query("category") String category,
            @Query("keyword") String keyword,
            @Query("pageSize") int pageSize,
            @Query("pageNo") int pageNo);

    @GET("api/product/detail")
    Observable<HttpResultInfo<PictureDetailInfo>> getProductDetail(@Query("id") String id);

    /**
     * 操作类型：
     * 01 播放（查看）
     * 02 下载
     * 03 收藏
     */
    @POST("api/product/operate")
    Observable<HttpResultInfo> operateProduct(@Query("productId") String productId,@Query("type") String type
    );

    @POST("api/product/collectList")
    Observable<HttpResultInfo<List<PictureInfo>>> getFavorList(
            @Query("id") String userId, @Query("pageSize") String pageSize,
            @Query("pageNo") String pageNo
    );

    @GET("api/banner/list")
    Observable<HttpResultInfo<List<BannerInfo>>> getBannerList(@Query("type") String type);

    /*@GET("api/user/lastSignInfo")
    Observable<HttpResultInfo<LastSignInfo>> getLastSignInfo(@Query("uid") String uid);

    @GET("api/user/sign")
    Observable<HttpResultInfo> sign(@Query("uid") String uid);*/
    @GET("api/user/lastSignInfo")
    Observable<HttpResultInfo<LastSignInfo>> getLastSignInfo();

    @GET("api/user/sign")
    Observable<HttpResultInfo> sign();
}
