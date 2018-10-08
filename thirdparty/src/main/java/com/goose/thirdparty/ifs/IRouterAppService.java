package com.goose.thirdparty.ifs;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface IRouterAppService extends IProvider {
    String SERVICE_PATH = "/service/app";
    String SERVICE_NAME = "service_app";

    String getToken();
}
