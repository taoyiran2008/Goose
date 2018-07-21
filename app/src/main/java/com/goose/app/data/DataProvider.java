package com.goose.app.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.goose.app.ifs.IApiService;
import com.goose.app.model.BannerInfo;
import com.goose.app.model.CategoryInfo;
import com.goose.app.model.PictureDetailInfo;
import com.goose.app.model.PictureInfo;
import com.goose.app.model.sign.LastSignInfo;
import com.taoyr.app.base.BaseApplication;
import com.taoyr.app.configs.BaseConfig;
import com.taoyr.app.model.HttpResultInfo;
import com.taoyr.app.model.LoginVo;
import com.taoyr.app.model.RegisterVo;
import com.taoyr.app.model.UserDetailInfo;
import com.taoyr.app.utility.CommonUtils;
import com.taoyr.app.utility.PictureUtils;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by taoyr on 2018/7/10.
 */

public class DataProvider {

    IApiService mService;
    Context mContext;
    Gson mGson;

    public static String DATA_TYPE_PICTURE = "picture";
    public static String DATA_TYPE_NOVEL = "novel";
    public static String DATA_TYPE_VIDEO = "video";

    public static String OPERATION_TYPE_VIEW = "01";
    public static String OPERATION_TYPE_DOWNLOAD = "02";
    public static String OPERATION_TYPE_FAVOR = "03";

    public enum OperationType {
        LOGIN,
        REGISTER,
        UPDATE_USER,
        UPLOAD_FILE,
        GET_CATEGORY_LIST,
        GET_PRODUCT_LIST,
        GET_PRODUCT_DETAIL,
        OPERATE_PRODUCT,
        GET_FAVOR_LIST,
        GET_BANNER_LIST,
        SIGN,
        GET_LAST_SIGN_INFO,
    }

    public DataProvider(IApiService service, Gson gson) {
        mService = service;
        mGson = gson;
        mContext = BaseApplication.getContext();
    }

    /**
     * 最初的想法是，使用泛型结合传进来的Class<T> clazz，根据传入的对象类型，在mocking环境下返回相应的dummy数据，
     * 以及在真实环境下调用对应的接口。但是有个问题，有可能不同的请求地址，返回的结果数据结构一致，比如登录和注册。
     */
    public Observable provideObservable(final OperationType opt, Object... params) {
        Observable request = null;

        if (BaseConfig.LOCAL_MOCKING) {
            request = new Observable<HttpResultInfo>() {
                @Override
                protected void subscribeActual(Observer observer) {
                    SystemClock.sleep(BaseConfig.MOCKING_TIME_IN_MS); // 模拟响应时间;

                    String responseBody = "";

                    HttpResultInfo mDummyHttpResultInfo = null;

                    switch (opt) {
                        case LOGIN:
                        case REGISTER:
                            responseBody = CommonUtils.readFileFromAssets(mContext, "dummy/login.json");
                            mDummyHttpResultInfo = mGson.fromJson(responseBody, new TypeToken<HttpResultInfo<UserDetailInfo>>() {
                            }.getType());
                            break;
                        case UPDATE_USER:
                            break;
                        case UPLOAD_FILE:
                            responseBody = CommonUtils.readFileFromAssets(mContext, "dummy/file.json");
                            mDummyHttpResultInfo = mGson.fromJson(responseBody, new TypeToken<HttpResultInfo<JsonObject>>() {
                            }.getType());
                            break;
                        case GET_CATEGORY_LIST:
                            responseBody = CommonUtils.readFileFromAssets(mContext, "dummy/category_list.json");
                            mDummyHttpResultInfo = mGson.fromJson(responseBody, new TypeToken<HttpResultInfo<List<CategoryInfo>>>() {
                            }.getType());
                            break;
                        case GET_FAVOR_LIST:
                        case GET_PRODUCT_LIST:
                            responseBody = CommonUtils.readFileFromAssets(mContext, "dummy/product_list.json");
                            mDummyHttpResultInfo = mGson.fromJson(responseBody, new TypeToken<HttpResultInfo<List<PictureInfo>>>() {
                            }.getType());
                            break;
                        case GET_PRODUCT_DETAIL:
                            responseBody = CommonUtils.readFileFromAssets(mContext, "dummy/product_detail.json");
                            mDummyHttpResultInfo = mGson.fromJson(responseBody, new TypeToken<HttpResultInfo<PictureDetailInfo>>() {
                            }.getType());
                            break;
                        case OPERATE_PRODUCT:
                            break;
                        case GET_BANNER_LIST:
                            responseBody = CommonUtils.readFileFromAssets(mContext, "dummy/banner_list.json");
                            mDummyHttpResultInfo = mGson.fromJson(responseBody, new TypeToken<HttpResultInfo<List<BannerInfo>>>() {
                            }.getType());
                            break;
                        case GET_LAST_SIGN_INFO:
                            responseBody = CommonUtils.readFileFromAssets(mContext, "dummy/last_sign_info.json");
                            mDummyHttpResultInfo = mGson.fromJson(responseBody, new TypeToken<HttpResultInfo<LastSignInfo>>() {
                            }.getType());
                            break;
                        default:
                            break;
                    }

                    if (mDummyHttpResultInfo == null) {
                        mDummyHttpResultInfo = new HttpResultInfo();
                        mDummyHttpResultInfo.code = "200";
                    }

                    observer.onNext(mDummyHttpResultInfo);
                }
            };
        } else {
            switch (opt) {
                case LOGIN:
                    String uname = (String) params[0];
                    String passwd = (String) params[1];
                    request = mService.login(new LoginVo(uname, passwd));
                    break;
                case REGISTER:
                    uname = (String) params[0];
                    passwd = (String) params[1];
                    request = mService.register(new RegisterVo(uname, passwd));
                    break;
                case UPDATE_USER:
                    String id = (String) params[0];
                    String displayName = (String) params[1];
                    String avatar = (String) params[2];
                    request = mService.updateUser(id, displayName, avatar);
                    break;
                case UPLOAD_FILE:
                    Bitmap bitmap = (Bitmap) params[0];
                    File file = PictureUtils.compressAndSaveImage(mContext, bitmap, "profile.jpg", 80, 100, 100);

                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("file", file.getName(), requestBody);

                    request = mService.uploadFile(body);
                    break;
            }
        }

        return request;
    }
}
