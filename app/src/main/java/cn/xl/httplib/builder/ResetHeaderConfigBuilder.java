package cn.xl.httplib.builder;

import cn.xl.httplib.HttpCallBack;
import cn.xl.httplib.HttpClient;
import cn.xl.httplib.HttpHeaderConfig;

/**
 * author: wenshenghui
 * created on: 2018/8/7 10:27
 * description:  需要重置掉Header基本信息的Builder
 */
public abstract class ResetHeaderConfigBuilder<T> extends LifeCycleBuilder<T> {

    private String TAG = "ResetHeaderConfigBuilder";

    private HttpHeaderConfig mDefaultHeaderConfig;

    /**
     * 重置全局默认Header
     * 一般不需要重置，如果Header初始东西不一样，就重置吧
     * @param headerConfig
     * @return
     */
    public CommonBuilder<T> resetDefaultHeaderConfig(HttpHeaderConfig headerConfig) {
        mDefaultHeaderConfig = headerConfig;
        return this;
    }

    @Override
    protected HttpClient getHttpClient(HttpCallBack<T> callback) {
        if (mDefaultHeaderConfig == null) {
            return HttpClient.getInstance().init(getBaseUrl(), callback);
        }

        return HttpClient.getInstance().init(getBaseUrl(), mDefaultHeaderConfig, callback);
    }

}
