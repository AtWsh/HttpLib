package cn.xl.httplib;

import com.example.payge.network.BuildConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * author: wenshenghui
 * created on: 2018/8/3 12:13
 * description:
 */
public class HttpHeaderConfig {

    private final static int CONNECT_TIME_OUT_DEFAULT = 3;
    private final static int READ_TIME_OUT_DEFAULT = 5;
    private final static int WRITE_TIME_OUT_DEFAULT = 5;

    private final static int NOT_DEFINED = -1;
    private int mUserConnectTimeout = NOT_DEFINED;
    private int mUserReadTimeout = NOT_DEFINED;
    private int mUserWriteTimeout = NOT_DEFINED;
    private Map<String, String> mHeaders = new HashMap<>();

    public static HttpHeaderConfig create() {
        return new HttpHeaderConfig();
    }

    private HttpHeaderConfig(){
        initHeaderMap();
    }

    /**
     * 此处定义固定Header内容，与业务相关请走addHeader方法
     * 根据实际情况，也要做出调整
     */
    private void initHeaderMap() {
        mHeaders.put("Content-Type", "application/json");
        mHeaders.put("terminalType", "android");
        mHeaders.put("FrontType", "scp-mobile-patrol-ui");
        mHeaders.put("versionCode", BuildConfig.VERSION_CODE + "");
        mHeaders.put("versionName", BuildConfig.VERSION_NAME);
        mHeaders.put("traceId", String.format("%s0201%s00000000000000000000000000000000",
                System.currentTimeMillis(), String.valueOf((int) ((Math.random()*9+1)*Math.pow(10, 6)))));
    }

    public HttpHeaderConfig addHeader(String name, String value) {
        mHeaders.put(name, value);
        return this;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    /**
     * 如果不需要init里面的数据，就clear
     */
    public void clear() {
        if (mHeaders != null) {
            mHeaders.clear();
        }
    }

    public int getConnectTimeout() {
        if (mUserConnectTimeout <= 0){
            return CONNECT_TIME_OUT_DEFAULT;
        }
        return mUserConnectTimeout;
    }

    public HttpHeaderConfig connectTimeout(int connectTimeout) {
        this.mUserConnectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout() {
        if (mUserReadTimeout <= 0){
            return READ_TIME_OUT_DEFAULT;
        }
        return mUserReadTimeout;
    }

    public HttpHeaderConfig readTimeout(int readTimeout) {
        this.mUserReadTimeout = readTimeout;
        return this;
    }

    public int getWriteTimeout() {
        if (mUserWriteTimeout <= 0){
            return WRITE_TIME_OUT_DEFAULT;
        }
        return mUserWriteTimeout;
    }

    public HttpHeaderConfig writeTimeout(int writeTimeout) {
        this.mUserWriteTimeout = writeTimeout;
        return this;
    }

    @Override
    public String toString() {
        return "HttpHeaderConfig{" +
                "mUserConnectTimeout='" + mUserConnectTimeout + '\'' +
                ", mUserReadTimeout='" + mUserReadTimeout + '\'' +
                ", mUserWriteTimeout='" + mUserWriteTimeout + '\'' +
                mHeaders.toString()+
                '}';
    }
}
