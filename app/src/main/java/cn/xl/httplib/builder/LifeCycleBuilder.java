package cn.xl.httplib.builder;

import android.arch.lifecycle.LifecycleOwner;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.Map;

import cn.xl.httplib.HttpCallBack;
import cn.xl.httplib.HttpClient;
import cn.xl.httplib.HttpMethod;
import cn.xl.httplib.HttpStateCode;

/**
 * author: wenshenghui
 * created on: 2018/8/7 10:15
 * description:  需要绑定生命周期的Builder
 */
public abstract class LifeCycleBuilder<T> extends CommonBuilder<T>{

    private String TAG = "LifeCycleBuilder";

    private int mTagHash;

    /**
     * 如果要绑定生命周期，界面销毁时取消请求，
     * 则tag需要传Activity或者Fragmeng对象
     * @param tag
     * @return
     */
    public CommonBuilder<T> setTag(Object tag) {
        registerLifecycle(tag);
        mTagHash = tag == null ? TAG.hashCode() : tag.hashCode();
        return this;
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

    @Override
    protected int getTagHash() {
        if (mTagHash == 0) {
            return TAG.hashCode();
        }else {
            return mTagHash;
        }
    }
}
