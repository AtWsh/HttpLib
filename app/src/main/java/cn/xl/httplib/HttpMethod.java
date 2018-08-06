package cn.xl.httplib;

import android.support.annotation.StringDef;

/**
 * author: wenshenghui
 * created on: 2018/8/2 9:58
 * description:
 */
public class HttpMethod {

    public static final String POST = "post";

    public static final String GET = "get";

    @StringDef({POST, GET})
    public @interface IMethed{}
}
