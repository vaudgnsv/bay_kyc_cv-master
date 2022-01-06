package com.thaivan.bay.branch.manager.api;

import com.google.gson.JsonElement;
import com.thaivan.bay.branch.model.CheckJson;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GTMSAPI {

//    @Headers({
//            "Content-Type:application/json"
//    })
//    @POST("cpay-jsonparam-gate/termParam/updateAppSuccess")
//    Observable<Response<JsonElement>> updatedApp(@Body CheckApp checkApp);

    @Headers({
            "Content-Type:application/json"
    })
    @POST("cpay-jsonparam-gate/termParam/updateJsonSuccess")
    Observable<Response<JsonElement>> updatedJson(@Body CheckJson checkJson);
}
