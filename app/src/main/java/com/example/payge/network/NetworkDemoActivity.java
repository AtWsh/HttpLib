package com.example.payge.network;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.payge.network.controller.NetworkDemoController;
import com.example.payge.network.model.Banner;
import com.example.payge.network.model.ZhiHuStories;
import com.example.payge.network.response.ModelUserLogin;
import com.example.payge.network.response.StoriesResponse;
import com.example.payge.network.response.WeatherResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.xl.httplib.HttpCallBack;
import cn.xl.network.http.Http;

public class NetworkDemoActivity extends AppCompatActivity implements View.OnClickListener, Runnable {

    NetworkDemoController controller;
    TextView httpView;
    RecyclerView dataView;
    LinearLayoutManager layoutManager;
    PagerSnapHelper helper;
    BannerAdapter bannerAdapter;
    CommonAdapter commonAdapter;
    boolean showList = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_demo);

        controller = new NetworkDemoController(this);

        httpView = findViewById(R.id.http);
        httpView.setOnClickListener(this);
        httpView.setText("list");

        dataView = findViewById(R.id.data);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        dataView.setLayoutManager(layoutManager);
        helper = new PagerSnapHelper();
        helper.attachToRecyclerView(dataView);

        bannerAdapter = new BannerAdapter();

        commonAdapter = new CommonAdapter();

        /*controller.getStoryList(new Http.Callback<StoriesResponse>() {
            @Override
            protected void onSuccess(StoriesResponse response) {
                Log.d("wsh_log", "onSuccess: " + response.stories.toString());
                commonAdapter.dataSet.addAll(response.stories);
                bannerAdapter.banners.addAll(response.top_stories);
                dataView.setAdapter(bannerAdapter);
            }

            @Override
            protected void onError(int errorCode, String msg) {
                Log.d("wsh_log", "msg: " + msg);
            }
        });*/
        //测试普通Get请求，只传url
       /*new StoriesResponse.Builder().setTag(this).build(new HttpCallBack<StoriesResponse>() {
           @Override
           public void onResult(int stateCode, StoriesResponse response) {
               Log.d("wsh_log", "stateCode = " + stateCode);
               commonAdapter.dataSet.addAll(response.stories);
               bannerAdapter.banners.addAll(response.top_stories);
               dataView.setAdapter(bannerAdapter);
           }
       });*/

        //测试Get请求，只传url + params
        //location=嘉兴&output=json&ak=5slgyqGDENN7Sy7pw29IUvrZ
       /* HashMap<String, String> map = new HashMap<>();
        map.put("location","嘉兴");
        map.put("output","json");
        map.put("ak","5slgyqGDENN7Sy7pw29IUvrZ");
       new WeatherResponse.Builder().setTag(this)
               .addParamsMap(map)
               .build(new HttpCallBack<WeatherResponse>() {
           @Override
           public void onResult(int stateCode, WeatherResponse response) {
               Log.d("wsh_log", "stateCode = " + stateCode);
           }
       });*/

        //controller.login();
        //controller.download();
    }

    @Override
    public void run() {
        /*controller.getStoryList(new Http.Callback<StoriesResponse>() {
            @Override
            protected void onSuccess(StoriesResponse response) {
                Log.d("wsh_log", "onSuccess: " + response.stories.toString());
                commonAdapter.dataSet.addAll(response.stories);
                bannerAdapter.banners.addAll(response.top_stories);
                dataView.setAdapter(bannerAdapter);
            }

            @Override
            protected void onError(int errorCode, String msg) {
                Log.d("wsh_log", "msg: " + msg);
            }
        });*/
    }

    @Override
    public void onClick(View view) {
        if (showList) {
            httpView.setText("banner");
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            helper.attachToRecyclerView(null);
            dataView.setAdapter(commonAdapter);
          /*  new StoriesResponse.Builder().setTag(this).build(new HttpCallBack<StoriesResponse>() {
                @Override
                public void onResult(int stateCode, StoriesResponse storiesResponse) {
                    Log.d("wsh_log", "stateCode = " + stateCode);
                }
            });*/

            HashMap<String, String> map = new HashMap<>();
            map.put("location","嘉兴");
            map.put("output","json");
            map.put("ak","5slgyqGDENN7Sy7pw29IUvrZ");
            new WeatherResponse.Builder().setTag(this)
                    .addParamsMap(map)
                    .build(new HttpCallBack<WeatherResponse>() {
                        @Override
                        public void onResult(int stateCode, WeatherResponse response) {
                            Log.d("wsh_log", "stateCode = " + stateCode);
                            //todo stateCode == HttpStateCode.RESULT_OK 表示请求成功  然后处理逻辑
                        }
                    });


           /* HashMap<String, String> map = new HashMap<String, String>();
            map.put("account", "123");
            map.put("password", "123");
            map.put("deviceNumber", "123");
            new ModelUserLogin.Builder().addBodyMap(map).build(new HttpCallBack<ModelUserLogin>() {
                @Override
                public void onResult(int stateCode, ModelUserLogin modelUserLogin) {
                    Log.d("wsh_log", "stateCode = " + stateCode);
                }
            });*/

        } else {
            httpView.setText("list");
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            helper.attachToRecyclerView(dataView);
            dataView.setAdapter(bannerAdapter);

             /*controller.getStoryList(new Http.Callback<StoriesResponse>() {
                @Override
                protected void onSuccess(StoriesResponse response) {
                    Log.d("wsh_log", "onSuccess: " + response.stories.toString());
                    commonAdapter.dataSet.addAll(response.stories);
                    bannerAdapter.banners.addAll(response.top_stories);
                    dataView.setAdapter(bannerAdapter);
                }

                @Override
                protected void onError(int errorCode, String msg) {
                    Log.d("wsh_log", "msg: " + msg);
                }
            });*/
        }
        showList = !showList;
    }

    static class BannerAdapter extends RecyclerView.Adapter<ViewHolder> {

        List<Banner> banners = new ArrayList<>();
        ColorDrawable holder = new ColorDrawable(Color.parseColor("#f0f0f0"));

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_banner, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            Banner banner = banners.get(i);
//            Glide.with(viewHolder.imageView.getContext())
//                    .load(banner.image).placeholder(holder)
//                    .crossFade().into(viewHolder.imageView);
            viewHolder.imageView.setImageDrawable(holder);
            viewHolder.textView.setText(banner.title);
        }

        @Override
        public int getItemCount() {
            return banners.size();
        }
    }

    static class CommonAdapter extends RecyclerView.Adapter<CommonViewHolder> {

        List<ZhiHuStories> dataSet = new ArrayList<>();
        ColorDrawable holder = new ColorDrawable(Color.parseColor("#f0f0f0"));

        @NonNull
        @Override
        public CommonViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_common, viewGroup, false);
            return new CommonViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CommonViewHolder viewHolder, int i) {
            ZhiHuStories itemData = dataSet.get(i);
            String imagePath = itemData.images.get(0);
            Glide.with(viewHolder.iconView.getContext())
                    .load(imagePath).placeholder(holder)
                    .crossFade().into(viewHolder.iconView);
            viewHolder.textView.setText(itemData.title);
            viewHolder.summaryView.setText(itemData.ga_prefix);
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.title);
            textView.setBackgroundColor(0x39000000);
            textView.setTextColor(Color.WHITE);
        }
    }

    static class CommonViewHolder extends RecyclerView.ViewHolder {

        ImageView iconView;
        TextView textView;
        TextView summaryView;

        CommonViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setBackgroundColor(0x39000000);
            iconView = itemView.findViewById(R.id.icon);
            textView = itemView.findViewById(R.id.title);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(16);
            summaryView = itemView.findViewById(R.id.summary);
        }
    }

}
