package cn.xl.httplib;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import cn.xl.httplib.converter.ConvertFactory;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * author: wenshenghui
 * created on: 2018/8/2 12:01
 * description:
 */
public class HttpClient<T> implements GenericLifecycleObserver {
    private static final String TAG = "HttpClient";

    private Retrofit mCurrentRetrofit;
    private HttpServices mCurrentServices;
    private static volatile HttpClient sInstance;
    private final HashMap<Integer, List<Pair<Integer, Disposable>>> mDisposableCache = new HashMap<>();
    private HashMap<Integer, Retrofit> mRetrofitMap = new HashMap<>();
    private final static int MAX_CACHE_SIZE = 100;
    private final Gson mGson = new Gson();

    private HttpClient() {
    }

    public static HttpClient getInstance() {
        if (sInstance == null) {
            synchronized (HttpClient.class) {
                if (sInstance == null) {
                    sInstance = new HttpClient();
                }
            }
        }
        return sInstance;
    }

    /**
     * @param url
     * @return
     */
    public HttpClient init(String url) {
        return init(url, null);
    }

    /**
     * @param url
     * @param headerConfig
     * @return
     */
    public HttpClient init(String url, HttpHeaderConfig headerConfig) {

        //获取缓存Retrofit
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        int key = getHashKey(url, headerConfig);
        mCurrentRetrofit = getCacheRetrofit(key);
        if (mCurrentRetrofit != null) {
            mCurrentServices = mCurrentRetrofit.create(HttpServices.class);
            return this;
        }

        OkHttpClient client = getOkHttpClient(headerConfig);
        try {
            mCurrentRetrofit = new Retrofit.Builder()
                    .client(client)
                    .addConverterFactory(new ConvertFactory(mGson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();

        } catch (Exception e) {
            mCurrentRetrofit = null;
            e.printStackTrace();
            return null;
        }

        mCurrentServices = mCurrentRetrofit.create(HttpServices.class);
        putCacheRetrofit(key, mCurrentRetrofit);
        return this;
    }

    /**
     * 生成缓存Retrofit用的key
     * 如果url一样，headerConfig不一样，需要判定为不一样的请求，所以需要两个参数共同生成key值
     *
     * @param url
     * @param headerConfig
     */
    private int getHashKey(String url, HttpHeaderConfig headerConfig) {
        if (headerConfig == null) {
            return url.hashCode();
        }
        return (url + headerConfig.toString()).hashCode();
    }

    private OkHttpClient getOkHttpClient(HttpHeaderConfig headerConfig) {
        if (headerConfig == null) {
            headerConfig = HttpHeaderConfig.create();

        }
        final HttpHeaderConfig config = headerConfig;
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(config.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(config.getWriteTimeout(), TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Request.Builder builder = chain.request().newBuilder();
                        if (!config.getHeaders().isEmpty()) {
                            Set<Map.Entry<String, String>> entrySet = config.getHeaders().entrySet();
                            for (Map.Entry<String, String> entry : entrySet) {
                                builder.addHeader(entry.getKey(), entry.getValue());
                            }
                        }
                        return chain.proceed(builder.build());
                    }
                }).build();
        return client;
    }

    /**
     * 获取缓存的Retrofit对象，如果没有缓存，则会返回null
     *
     * @param key
     * @return
     */
    private Retrofit getCacheRetrofit(int key) {
        return mRetrofitMap.get(key);
    }

    /**
     * 缓存Retrofit对象，超过MAX_CACHE_SIZE则清空
     *
     * @param key
     * @param retrofit
     */
    private void putCacheRetrofit(int key, Retrofit retrofit) {
        if (retrofit == null) {
            return;
        }

        if (mRetrofitMap == null) {
            mRetrofitMap = new HashMap<>();
        }

        if (mRetrofitMap.size() > MAX_CACHE_SIZE) {
            mRetrofitMap.clear();
        }

        mRetrofitMap.put(key, retrofit);
    }

    public void post(String path, Map<String, String> params, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onResult(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }
        Observable<retrofit2.Response<String>> observable = mCurrentServices.post(path, params);
        String disposableCacheKey = getDisposableCacheKey(path, null, null, params, null);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void post(String path, JSONObject bodyJson, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onResult(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }
        RequestBody requestBody = getRequestBody(bodyJson);
        Observable<retrofit2.Response<String>> observable = mCurrentServices.post(path, requestBody);
        String disposableCacheKey = getDisposableCacheKey(path, null,null, null, bodyJson);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void post(String path, Map<String, String> params, JSONObject bodyJson, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onResult(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }
        RequestBody requestBody = getRequestBody(bodyJson);
        Observable<retrofit2.Response<String>> observable = mCurrentServices.post(path, params, requestBody);
        String disposableCacheKey = getDisposableCacheKey(path, null, null, params, bodyJson);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void post(String path, String authHeader, JSONObject bodyJson, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onResult(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }

        RequestBody requestBody = getRequestBody(bodyJson);
        Observable<retrofit2.Response<String>> observable = mCurrentServices.post(path, authHeader, requestBody);
        String disposableCacheKey = getDisposableCacheKey(path, authHeader, null, null, bodyJson);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void post(String path, String authHeader, Map<String, String> params, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onResult(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }

        Observable<retrofit2.Response<String>> observable = mCurrentServices.post(path, authHeader, params);
        String disposableCacheKey = getDisposableCacheKey(path, authHeader, null, params, null);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    /**
     * @param path
     * @param callback
     */
    public void get(String path, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onResult(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }

        Observable<retrofit2.Response<String>> observable = mCurrentServices.get(path);
        String disposableCacheKey = getDisposableCacheKey(path, null, null, null, null);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }


    public void get(String path, String authHeader, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onResult(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }

        Observable<retrofit2.Response<String>> observable = mCurrentServices.get(path, authHeader);
        String disposableCacheKey = getDisposableCacheKey(path, authHeader, null, null, null);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }


    public void get(String path, String authHeader, Map<String, String> params, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onResult(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }

        Observable<retrofit2.Response<String>> observable = mCurrentServices.get(path, authHeader, params);
        String disposableCacheKey = getDisposableCacheKey(path, authHeader, null, params, null);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void get(String path, Map<String, String> authHeader, Map<String, String> params, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onResult(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }

        Observable<retrofit2.Response<String>> observable = mCurrentServices.get(path, authHeader, params);
        String disposableCacheKey = getDisposableCacheKey(path, null, authHeader,  params, null);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void uploadPhotoSet(String path, String header, JSONObject bodyJson, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        Log.d(TAG, "uploadPhotoSet()");
        if (mCurrentServices == null) {
            callback.onResult(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }
        RequestBody requestBody = getRequestBody(bodyJson);
        Observable<retrofit2.Response<String>> observable = mCurrentServices.uploadPhotoSet(path, header, requestBody);
        String disposableCacheKey = getDisposableCacheKey(path, header, null,  null, bodyJson);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void getPhotos(String path, String header, Map<String, String> params, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        Log.d(TAG, "uploadPhotoSet()");
        if (mCurrentServices == null) {
            callback.onResult(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }

        Observable<retrofit2.Response<String>> observable = mCurrentServices.getPhotos(path, header, params);
        String disposableCacheKey = getDisposableCacheKey(path, header, null,  params, null);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    /**
     * 拼接存储Disposable的key值
     * @param path
     * @param params
     * @return
     */
    private String getDisposableCacheKey(String path, String strHeader, Map<String, String> mapHeader, Map<String, String> params, JSONObject bodyJson) {
        StringBuffer keyBuffer = new StringBuffer("");
        if (!TextUtils.isEmpty(path)) {
            keyBuffer.append(path).append(":");
        }

        if (!TextUtils.isEmpty(strHeader)) {
            keyBuffer.append(strHeader).append(":");
        }
        if (mapHeader != null && mapHeader.size() > 0) {
            Set<Map.Entry<String, String>> entries = mapHeader.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                keyBuffer.append(entry.getKey()).append("_").append(entry.getValue()).append("_");
            }
        }
        if (params != null && params.size() > 0) {
            Set<Map.Entry<String, String>> entries = params.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                keyBuffer.append(entry.getKey()).append("_").append(entry.getValue()).append("_");
            }
        }
        if (bodyJson != null) {
            keyBuffer.append(bodyJson.toString());
        }

        return keyBuffer.toString();
    }

    /**
     * 获取RequestBody
     * @param jsonObject
     * @return
     */
    private RequestBody getRequestBody(JSONObject jsonObject){
        if(jsonObject != null){
            return RequestBody.create(HttpConstants.sJsonType, jsonObject.toString());
        } else {
            return null;
        }
    }

    /**
     * 处理请求，并回调结果
     *
     * @param observable
     * @param callback
     */
    private void doSubscribe(final String pathKey, final int tagHash, Observable<retrofit2.Response<String>> observable, final boolean onUiCallBack, final HttpCallBack<T> callback) {

        Observable<Pair<String, T>> mapObservable = observable.map(new Function<Response<String>, Pair<String, T>>() {
            @Override
            public Pair<String, T> apply(retrofit2.Response<String> response) throws Exception {
                int code;
                String msg;
                Pair<String, T> pair;
                code = response.code();
                if (code == HttpStateCode.RESULT_OK) {
                    String data = response.body();
                    if (data != null) {
                        Class<T> cls = getParameterizedTypeClass(callback);
                        T t = mGson.fromJson(data, cls);
                        if (t != null) {
                            pair = new Pair<>(data, t);
                        } else {
                            msg = "json parse fail";
                            pair = new Pair<>(msg, null);
                        }
                        if (!onUiCallBack && t != null){ //子线程返回
                            callback.onResult(HttpStateCode.RESULT_OK, t);
                        }else if (!onUiCallBack && t == null){
                            callback.onResult(HttpStateCode.ERROR_SUBSCRIBE_ERROR, null);
                        }
                        return pair;
                    } else {
                        msg = "response body is null";
                    }
                } else {
                    ResponseBody errorBody = response.errorBody();
                    if (errorBody == null) {
                        msg = "errorBody is null";
                    } else {
                        msg = errorBody.string();
                    }
                }
                Log.e(TAG, "apply: " + msg);
                pair = new Pair<>(msg, null);
                if (!onUiCallBack){
                    callback.onResult(HttpStateCode.ERROR_SUBSCRIBE_ERROR, null);
                }
                return pair;
            }
        });

        mapObservable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<String, T>>() {

                    @Override
                    public void onSubscribe(Disposable disposable) {
                        cacheDisposableIfNeed(disposable, tagHash, pathKey);
                    }

                    @Override
                    public void onNext(Pair<String, T> pair) {
                        if (!onUiCallBack) {
                            return;
                        }
                        T t = pair.second;
                        if (t != null) {
                            callback.onResult(HttpStateCode.RESULT_OK, t);
                        } else {
                            callback.onResult(HttpStateCode.ERROR_ONNEXT_NULL, null);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        dispose(tagHash, pathKey);
                        if (onUiCallBack) {
                            callback.onResult(HttpStateCode.ERROR_SUBSCRIBE_ERROR, null);
                        }
                    }

                    @Override
                    public void onComplete() {
                        dispose(tagHash, pathKey);
                    }
                });
    }

    /**
     * 缓存Disposable
     *
     * @param disposable
     * @param hash
     * @param key
     */
    private void cacheDisposableIfNeed(Disposable disposable, int hash, String key) {
        if (disposable == null) {
            return;
        }
        Pair<Integer, Disposable> pair = Pair.create(key.hashCode(), disposable);
        List<Pair<Integer, Disposable>> list = mDisposableCache.get(hash);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(pair);
        mDisposableCache.put(hash, list);
    }

    public static <T> Class<T> getParameterizedTypeClass(Object obj) {
        ParameterizedType pt = (ParameterizedType) obj.getClass().getGenericSuperclass();
        Type[] atr = pt.getActualTypeArguments();
        if (atr != null && atr.length > 0) {
            return (Class<T>) atr[0];
        }
        return null;
    }

    /**
     * @param tag 请求时传入的tag
     */
    public void cancel(Object tag) {
        if (tag == null) return;
        List<Pair<Integer, Disposable>> disposableList;
        disposableList = mDisposableCache.get(tag.hashCode());
        if (disposableList != null && disposableList.size() > 0) {
            for (Pair<Integer, Disposable> pair : disposableList) {
                pair.second.dispose();
            }
            mDisposableCache.remove(tag.hashCode());
        }
    }

    private void dispose(int hash, @NonNull String key) {
        List<Pair<Integer, Disposable>> list = mDisposableCache.get(hash);
        Pair<Integer, Disposable> removePair = null;
        if (list != null) {
            for (Pair<Integer, Disposable> pair : list) {
                if (key.hashCode() == pair.first) {
                    pair.second.dispose();
                    removePair = pair;
                    break;
                }
            }
        }
        if (list != null && removePair != null) {
            list.remove(removePair);
        }
    }

    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        if (source.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            source.getLifecycle().removeObserver(this);
            cancel(source);
        }
    }
}
