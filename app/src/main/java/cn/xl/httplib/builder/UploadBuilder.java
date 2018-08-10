package cn.xl.httplib.builder;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.xl.httplib.HttpCallBack;
import cn.xl.httplib.HttpClient;
import cn.xl.httplib.HttpConstants;
import cn.xl.httplib.HttpMethod;
import cn.xl.httplib.HttpStateCode;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * author: wenshenghui
 * created on: 2018/8/10 11:52
 * description:
 */
public abstract class UploadBuilder<T> extends LifeCycleBuilder<T> {

    Map<String, RequestBody> mPartMap = new HashMap<>();

    public void addPartMap(Map<String, RequestBody> partMap) {
        if (partMap == null) {
            return;
        }
        mPartMap = partMap;
    }

    public void addFile(File file) {
        if (file == null) {
            return;
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        mPartMap.put(HttpConstants.FILE_NAME + file.getName(), requestBody);
    }

    public void addFiles(List<File> files) {
        if (files == null || files.size() == 0) {
            return;
        }
        for (File file : files) {
            addFile(file);
        }
    }

    @Override
    protected void request(boolean onUiCallBack, final HttpCallBack<T> callback){
        HttpClient client = getHttpClient(callback);
        if (client == null) {
            callback.onResult(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }
        callback.onStart();
        client.upload(getPath(), mHttpHeader, mPartMap, getTagHash(), callback);
    }

    @Override
    protected @HttpMethod.IMethed String getMethod() {
        return HttpMethod.POST;
    };
}


