package com.example.payge.network.response;

import com.google.gson.Gson;

import cn.xl.httplib.HttpMethod;
import cn.xl.httplib.builder.ResetHeaderConfigBuilder;


public class ModelUserLogin {

    private String account;
    private String realName;
    private String accessToken;

    public ModelUserLogin() {
    }

    public String getRealName(){
        return realName;
    }

    public void setRealName(String realName){
        this.realName = realName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public static class Builder extends ResetHeaderConfigBuilder<ModelUserLogin> {
        protected String getBaseUrl(){
            return "http://sztest.msyuns.com/api/salesman/";
        }

        protected String getPath(){
            return "login";
        }

        @Override
        protected String getMethod() {
            return HttpMethod.POST;
        }

        protected ModelUserLogin parseJson(String json){
            Gson gson = new Gson();
            ModelUserLogin data = null;
            try {
                data = gson.fromJson(json, ModelUserLogin.class);
            } catch (Exception e){
                data = null;
                e.printStackTrace();
            }

            return data;
        }
    }
}
