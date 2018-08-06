package cn.xl.httplib;

import cn.xl.httplib.HttpClient;

/**
 * author: wenshenghui
 * created on: 2018/8/2 12:06
 * description:
 */
public class HttpStateCode {

    /**
     * 正常返回
     */
    public static final int RESULT_OK = 200;

    /**
     * 请求失败1000~2000
     */
    public static final int ERROR_HTTPCLIENT_CREATE_FAILED = 1001;

    public static final int ERROR_GSON_PARSE = 1002;

    /**
     * onNext返回T为null
     */
    public static final int ERROR_ONNEXT_NULL = 1003;

    /**
     * Observable中返回onError
     */
    public static final int ERROR_SUBSCRIBE_ERROR = 1004;

    /**
     * path为空
     */
    public static final int ERROR_PATH_EMPTY = 1005;

}
