package com.example.payge.network.response;

import com.example.payge.network.model.Banner;
import com.example.payge.network.model.ZhiHuStories;

import java.util.List;

import cn.xl.httplib.HttpMethod;
import cn.xl.httplib.HttpStateCode;
import cn.xl.httplib.builder.LifeCycleBuilder;

public class StoriesResponse {
    public List<ZhiHuStories> stories;
    public List<Banner> top_stories;


    public static class Builder extends LifeCycleBuilder<StoriesResponse> {
        @Override
        protected String getPath() {
            return "api/4/news/latest";
        }

        @Override
        protected String getBaseUrl() {
            return "https://news-at.zhihu.com/";
        }

        @Override
        protected @HttpMethod.IMethed String getMethod() {
            return HttpMethod.GET;
        }
    }

}
