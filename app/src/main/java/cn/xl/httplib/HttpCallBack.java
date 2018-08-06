package cn.xl.httplib;

/**
 * author: wenshenghui
 * created on: 2018/8/2 16:27
 * description:
 */
public abstract class HttpCallBack<T> {

    public void onStart(){}
    public abstract void onResult(int stateCode, T t);
    public void onProgress(int progress){}
    public void onDownloaded(byte[] file){}
}
