package cn.xl.httplib.builder;

import android.arch.lifecycle.LifecycleOwner;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import cn.xl.httplib.HttpCallBack;
import cn.xl.httplib.HttpClient;
import cn.xl.httplib.HttpHeaderConfig;
import cn.xl.httplib.HttpMethod;
import cn.xl.httplib.HttpStateCode;

/**
 * author: wenshenghui
 * created on: 2018/8/2 16:52
 * description:
 */
public abstract class AsyncBuilder<T> {

    private String TAG = "AsyncBuilder";

    protected Map<String, String> mHttpParams;
    protected Map<String, String> mHttpHeader;


    protected abstract String getPath();
    protected abstract String getUrl();
    protected abstract @HttpMethod.IMethed String getMethod();
    private int mTagHash;
    private HttpHeaderConfig mHeaderConfig;
    private JSONObject mJsonObject;

    /**
     * 如果要绑定生命周期，界面销毁时取消请求，
     * 则tag需要传Activity或者Fragmeng对象
     * @param tag
     * @return
     */
    public AsyncBuilder<T> setTag(Object tag) {
        registerLifecycle(tag);
        mTagHash = tag == null ? TAG.hashCode() : tag.hashCode();
        return this;
    }

    /**
     *  Function: addJsonQuery()
     *          添加http查询参数，注意，两次调用该方法添加参数，后来添加的会将前面添加的清空
     *
     **/
    public AsyncBuilder<T> addParamsMap(Map<String, String> params){
        mHttpParams = params;
        return this;
    }

    public AsyncBuilder<T> addBodyMap(Map<String, String> mapValue){
        if(mapValue == null && mapValue.size() <= 0){
            Log.d(TAG, "Error! input param mapValue = " + mapValue);
            return this;
        }

        if(mJsonObject == null){
            mJsonObject = new JSONObject();
        }

        for (String key : mapValue.keySet()) {
            try {
                mJsonObject.put(key, mapValue.get(key));
            }catch (Exception e){
                Log.d(TAG, "Error! When get JSONObject.");
                mJsonObject = null;
            }
        }

        return this;
    }

    /**
     * 设置HeaderConfig
     * @param headerConfig
     * @return
     */
    public AsyncBuilder<T> setHeaderConfig(HttpHeaderConfig headerConfig) {
        mHeaderConfig = headerConfig;
        return this;
    }

    /**
     *
     * @param mapValue
     * @return
     */
    public AsyncBuilder<T> addHeader(Map<String, String> mapValue){
        mHttpHeader = mapValue;
        return this;
    }

    /**
     *  Function: getParams()
     *  NOTE: 该方法可以被重写。如果不重写，则默认使用addJsonQuery()调用时设置的参数。如果重写，则是
     *      添加通用参数，需要创建新的Map<String, String>，在添加通用参数的同时，将mHttpParams中的参数
     *      也填写进去，切不可直接在mHttpParams中直接添加通用参数并返回。
     *
     **/
    protected Map<String, String> getParams(){
        return mHttpParams;
    }

    /**
     * @param tag
     */
    public void registerLifecycle(Object tag) {
        LifecycleOwner owner;
        if (tag instanceof LifecycleOwner) {
            owner = (LifecycleOwner) tag;
            owner.getLifecycle().addObserver(HttpClient.getInstance());
        }
    }

    /**
     * 创建一个请求，回调默认在主线程
     * @param callback
     */
    final public void build(HttpCallBack<T> callback){
        build(true, callback);
    }

    /**
     * 创建一个请求，并指定回调是否在UI线程
     * @param onUiCallBack
     * @param callback
     */
    final public void build(boolean onUiCallBack, HttpCallBack<T> callback){
        //todo 此处进行数据初始化判断，如果没有进行初始化数据设置，则直接返回相应错误码
        fromJson(onUiCallBack, callback);
    }

    protected void fromJson(boolean onUiCallBack, final HttpCallBack<T> callback){

        getHttpClient();
        HttpClient client = HttpClient.getInstance().init(getUrl());
        if (client == null) {
            callback.onResult(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }
        callback.onStart();
        switch (getMethod()){
            case HttpMethod.POST:
                if(getPath() != null) {
                    if(getParams() != null){
                        if(mJsonObject != null){
                            client.post(getPath(), getParams(), mJsonObject, getTagHash(), onUiCallBack, callback);
                        } else {
                            client.post(getPath(), getParams(), getTagHash(), onUiCallBack, callback);
                        }
                    } else {
                        if(mJsonObject != null){
                            client.post(getPath(), mJsonObject, getTagHash(), onUiCallBack, callback);
                        } else {
                            // do nothing;
                        }
                    }
                } else {
//                    client.post(getParams(), completion);
                }
                break;
            case HttpMethod.GET:
                if(!TextUtils.isEmpty(getPath())) {
                    if(getParams() != null){
                        if(mHttpHeader != null){
                            client.get(getPath(), mHttpHeader, getParams(), getTagHash(), onUiCallBack, callback);
                        } else {
                            client.get(getPath(), new HashMap<String, String>(), getParams(), getTagHash(), onUiCallBack, callback);
                        }
                    } else {
                        if(mHttpHeader != null){
                            client.get(getPath(), mHttpHeader, new HashMap<String, String>(), getTagHash(), onUiCallBack, callback);
                        } else {
                            client.get(getPath(), new HashMap<String, String>(), new HashMap<String, String>(), getTagHash(), onUiCallBack, callback);
                        }
                    }

                } else {
                    callback.onResult(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
                }
                break;
        }
    }

    private HttpClient getHttpClient() {
        if (mHeaderConfig == null) {
            return HttpClient.getInstance().init(getUrl());
        }

        return HttpClient.getInstance().init(getUrl(), mHeaderConfig);
    }

    private int getTagHash() {
        if (mTagHash == 0) {
            return TAG.hashCode();
        }else {
            return mTagHash;
        }
    }
}
