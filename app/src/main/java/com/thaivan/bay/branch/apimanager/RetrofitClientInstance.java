package com.thaivan.bay.branch.apimanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thaivan.bay.branch.Preference;
import com.thaivan.bay.branch.manager.Contextor;
import com.thaivan.bay.branch.manager.safeOkHttpClient;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static String BASE_URL = "";
    private static String BASE_URL2 = "";

    private static RetrofitClientInstance instance;

    private ApiInterface service;
    private ApiInterface service2;


    public static RetrofitClientInstance getInstance() {
        if (instance == null)
            instance = new RetrofitClientInstance();
        else{
            instance = null;
            instance = new RetrofitClientInstance();
        }
        return instance;
    }


    public RetrofitClientInstance() {

//        OkHttpClient okHttpClient = safeOkHttpClient.getsafeOkHttpClient();
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        String ip = Preference.getInstance(Contextor.getInstance().getContext()).getValueString(Preference.KEY_IP);
        String port = Preference.getInstance(Contextor.getInstance().getContext()).getValueString(Preference.KEY_PORT);
        String ip2 = Preference.getInstance(Contextor.getInstance().getContext()).getValueString(Preference.KEY_IP2);
        String port2 = Preference.getInstance(Contextor.getInstance().getContext()).getValueString(Preference.KEY_PORT2);
//        ip = "121.254.239.136";
        BASE_URL = "https://"+ ip +":" + port + "/";
        BASE_URL2 = "https://"+ ip2 +":" + port2 + "/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        service = retrofit.create(ApiInterface.class);

        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(BASE_URL2)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        service2 = retrofit2.create(ApiInterface.class);
    }

    public ApiInterface getService() {
        return service;
    }

    public ApiInterface getService2() {
        return service2;
    }
}
