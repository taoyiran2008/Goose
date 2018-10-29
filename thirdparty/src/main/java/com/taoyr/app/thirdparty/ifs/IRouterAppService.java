package com.taoyr.app.thirdparty.ifs;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * ARouter service exposure and discovery.
 * 阿里云ARouter框架，使用的服务注册和发现的用法，跟android aidl服务的用法类似。这个共用的抽象接口可以放到
 * base模块，以便跨模块访问，也可以使用相同的类全名（包名.类名）在各自模块中各存一份。其原理都是java的动态
 * 代理。但是在编译正式版的时候，会提示Multiple dex files define，拿IRouterAppService为例，因为app依赖
 * utility，而app和utility中都有一个IRouterAppService，只能把它放到base模块中（包名不同，类名相同不行）
 *
 * Trouble Shooting: ARouter service null
 * 1. 并不是service path命名存在命名问题（带有系统保留字？）
 * 2. 并不是arouter不支持gradle plugin 3.0以上的版本（东航海外app使用gradle2.2.3）
 * 3. 并不是抽象接口类必须只能使用base模块中相同的文件
 * 4. 并不是ARouter.init(application)的位置放的有问题（attachBaseContext()?, onCreate()?)
 * 5. 并不是arguments = [AROUTER_MODULE_NAME: project.getName()]配置项，AROUTER_MODULE_NAME需要单独命
 *    名（因为文档也没说的太清楚，文档甚至没有说每个需要用到跳转的模块都需要单独配置它）
 *
 * google和baidu都找不到问题原因，fall back to强（文档）大（简陋）的官网
 * https://github.com/alibaba/ARouter
 * Q&A：开启InstantRun之后无法跳转(高版本Gradle插件下无法跳转)
 */
public interface IRouterAppService extends IProvider {
    String SERVICE_PATH = "/service/app";
    String SERVICE_NAME = "service_app";

    String getToken();
}
