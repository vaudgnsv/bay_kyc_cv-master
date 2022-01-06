package com.thaivan.bay.branch.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thaivan.bay.branch.manager.api.GTMSAPI;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class HttpManager {

    private Context mContext;
    private GTMSAPI gtmsapi;
    private static HttpManager instance;
    private String BASE_URL;


    public static HttpManager getInstance() {
        if (instance == null)
            instance = new HttpManager();
        return instance;
    }

    private HttpManager() {
        mContext = Contextor.getInstance().getContext();

        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();



        BASE_URL = "http://203.151.66.181:8089";
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        gtmsapi = retrofit2.create(GTMSAPI.class);
    }

    public GTMSAPI getGtmsapi() {
        return gtmsapi;
    }
}
