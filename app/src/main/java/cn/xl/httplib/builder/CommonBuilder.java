package cn.xl.httplib.builder;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import cn.xl.httplib.HttpCallBack;
import cn.xl.httplib.HttpClient;
import cn.xl.httplib.HttpMethod;
import cn.xl.httplib.HttpStateCode;
import cn.xl.httplib.ProgressRequestBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * author: wenshenghui
 * created on: 2018/8/7 10:15
 * description:
 */
public abstract class CommonBuilder<T> {

    private String TAG = "AsyncBuilder";

    protected Map<String, String> mHttpParams;
    protected Map<String, String> mHttpHeader;
    protected String mStrHeader;


    protected abstract String getPath();
    protected abstract String getBaseUrl();
    protected abstract @HttpMethod.IMethed String getMethod();
    private JSONObject mJsonObject;
    private Object mBodyObj;

    /**
     *  Function: addJsonQuery()
     *          添加http查询参数，注意，两次调用该方法添加参数，后来添加的会将前面添加的清空
     *
     **/
    public CommonBuilder<T> addParamsMap(Map<String, String> params){
        mHttpParams = params;
        return this;
    }

    public CommonBuilder<T> addBodyObj(Object bodyObj){
        mBodyObj = bodyObj;
        mJsonObject = null;
        return this;
    }


    public CommonBuilder<T> addBodyMap(Map<String, String> mapValue){
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

        mBodyObj = mJsonObject;
        return this;
    }

    /**
     *
     * @param mapValue
     * @return
     */
    public CommonBuilder<T> addHeader(Map<String, String> mapValue){
        mHttpHeader = mapValue;
        mStrHeader = null;
        return this;
    }

    /**
     *
     * @param strHeader
     * @return
     */
    public CommonBuilder<T> addHeader(String strHeader){
        mStrHeader = strHeader;
        mHttpHeader = null;
        return this;
    }

    /**
     *
     * @param objHeader
     * @return
     */
    public CommonBuilder<T> addHeader(Object objHeader){
        if (objHeader == null) {
            return this;
        }
        mStrHeader = objHeader.toString();
        mHttpHeader = null;
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
        request(onUiCallBack, callback);
    }

    protected void request(boolean onUiCallBack, HttpCallBack<T> callback){

        HttpClient client = getHttpClient(callback);
        if (client == null) {
            callback.onResult(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }
        callback.onStart();
        switch (getMethod()){
            case HttpMethod.POST:
                processPostQuest(client, onUiCallBack, callback);
                break;
            case HttpMethod.GET:
                processGetQuest(client, onUiCallBack, callback);
                break;
        }
    }

    private void processPostQuest(HttpClient client, boolean onUiCallBack, HttpCallBack<T> callback) {
        boolean isPathEmpty = TextUtils.isEmpty(getPath());
        boolean paramsEmpty = getParams() == null;
        boolean bodyObEmpty = mBodyObj == null;
        boolean strHeaderEmpty = TextUtils.isEmpty(mStrHeader);
        boolean mapHeaderEmpty = (mHttpHeader == null || mHttpHeader.size() <= 0);
        if (isPathEmpty) {
            callback.onResult(HttpStateCode.ERROR_PATH_EMPTY, null);
            return;
        }

        if (paramsEmpty && bodyObEmpty && strHeaderEmpty && mapHeaderEmpty) {
            client.post(getPath(), getTagHash(), onUiCallBack, callback);
        }else if (!paramsEmpty && bodyObEmpty && strHeaderEmpty && mapHeaderEmpty) {
            client.postWithParamsMap(getPath(), getParams(), getTagHash(), onUiCallBack, callback);
        }else if(paramsEmpty && !bodyObEmpty && strHeaderEmpty && mapHeaderEmpty){
            client.post(getPath(), mBodyObj, getTagHash(), onUiCallBack, callback);
        }else if(paramsEmpty && bodyObEmpty && !strHeaderEmpty && mapHeaderEmpty){
            client.post(getPath(), mStrHeader, getTagHash(), onUiCallBack, callback);
        }else if(paramsEmpty && bodyObEmpty && strHeaderEmpty && !mapHeaderEmpty){
            client.postWithHeaderMap(getPath(), mHttpHeader, getTagHash(), onUiCallBack, callback);
        }else if(!paramsEmpty && !bodyObEmpty && strHeaderEmpty && mapHeaderEmpty){
            client.post(getPath(), getParams(), mBodyObj, getTagHash(), onUiCallBack, callback);
        }else if(!paramsEmpty && bodyObEmpty && !strHeaderEmpty && mapHeaderEmpty){
            client.post(getPath(), getParams(), mStrHeader, getTagHash(), onUiCallBack, callback);
        }else if(!paramsEmpty && bodyObEmpty && strHeaderEmpty && !mapHeaderEmpty){
            client.post(getPath(), getParams(), mHttpHeader, getTagHash(), onUiCallBack, callback);
        }else if(paramsEmpty && !bodyObEmpty && !strHeaderEmpty && mapHeaderEmpty){
            client.post(getPath(), mStrHeader, mBodyObj, getTagHash(), onUiCallBack, callback);
        }else if(paramsEmpty && bodyObEmpty && !strHeaderEmpty && !mapHeaderEmpty){
            client.post(getPath(), mHttpHeader, mBodyObj, getTagHash(), onUiCallBack, callback);
        }else if(!paramsEmpty && !bodyObEmpty && !strHeaderEmpty && mapHeaderEmpty){
            client.post(getPath(), getParams(), mStrHeader, mBodyObj, getTagHash(), onUiCallBack, callback);
        }else if(!paramsEmpty && !bodyObEmpty && strHeaderEmpty && !mapHeaderEmpty){
            client.post(getPath(), getParams(), mHttpHeader, mBodyObj, getTagHash(), onUiCallBack, callback);
        }
    }

    private void processGetQuest(HttpClient client, boolean onUiCallBack, HttpCallBack<T> callback) {
        boolean isPathEmpty = TextUtils.isEmpty(getPath());
        boolean paramsEmpty = getParams() == null;
        boolean strHeaderEmpty = TextUtils.isEmpty(mStrHeader);
        boolean mapHeaderEmpty = (mHttpHeader == null || mHttpHeader.size() <= 0);
        if (isPathEmpty) {
            callback.onResult(HttpStateCode.ERROR_PATH_EMPTY, null);
            return;
        }

        if (paramsEmpty && strHeaderEmpty && mapHeaderEmpty) {
            //验证Ok
            client.get(getPath(), getTagHash(), onUiCallBack, callback);
        }else if (!paramsEmpty && strHeaderEmpty && mapHeaderEmpty) {
            //验证Ok
            client.getWithParamsMap(getPath(), getParams(), getTagHash(), onUiCallBack, callback);
        }else if(paramsEmpty && !strHeaderEmpty && mapHeaderEmpty){
            //关于Header的设置不验证，目前没有测试条件
            client.get(getPath(), mStrHeader, getTagHash(), onUiCallBack, callback);
        }else if(paramsEmpty && strHeaderEmpty && !mapHeaderEmpty){
            //关于Header的设置不验证，目前没有测试条件
            client.getWithHeaderMap(getPath(), mHttpHeader, getTagHash(), onUiCallBack, callback);
        }else if(!paramsEmpty && !strHeaderEmpty && mapHeaderEmpty){
            //关于Header的设置不验证，目前没有测试条件
            client.post(getPath(), getParams(), mStrHeader, getTagHash(), onUiCallBack, callback);
        }else if(!paramsEmpty && strHeaderEmpty && !mapHeaderEmpty){
            //关于Header的设置不验证，目前没有测试条件
            client.post(getPath(), getParams(), mHttpHeader, getTagHash(), onUiCallBack, callback);
        }
    }

    protected HttpClient getHttpClient(HttpCallBack<T> callBack) {
        return HttpClient.getInstance().init(getBaseUrl(), callBack);
    }

    protected int getTagHash() {
        return TAG.hashCode();
    }
}
