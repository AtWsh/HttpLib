package com.example.payge.network.response;

import com.example.payge.network.model.Banner;
import com.example.payge.network.model.ZhiHuStories;

import java.util.List;

import cn.xl.httplib.HttpMethod;
import cn.xl.httplib.builder.LifeCycleBuilder;

public class WeatherResponse {
    public String message;
    public String status;


    public static class Builder extends LifeCycleBuilder<WeatherResponse> {
        @Override
        protected String getPath() {
            return "telematics/v3/weather";
        }

        @Override
        protected String getBaseUrl() {
            return "http://api.map.baidu.com/";
        }

        @Override
        protected String getMethod() {
            return HttpMethod.GET;
        }
    }

}
