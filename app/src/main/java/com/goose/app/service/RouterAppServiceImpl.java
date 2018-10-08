package com.goose.app.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.goose.app.GooseApplication;
import com.goose.thirdparty.ifs.IRouterAppService;

@Route(path = IRouterAppService.SERVICE_PATH/*, name = IRouterAppService.SERVICE_NAME*/)
public class RouterAppServiceImpl implements IRouterAppService {

    private Context mContext;

    @Override
    public String getToken() {
        return GooseApplication.getInstance().getToken();
    }

    @Override
    public void init(Context context) {
        mContext = context;
    }
}
