package com.thaivan.bay.branch.apimanager;


import com.thaivan.bay.branch.FaceComp.ModelFaceResponse;
import com.thaivan.bay.branch.customerData.DopaOnly;
import com.thaivan.bay.branch.customerData.DopaOnlyResponse;
import com.thaivan.bay.branch.customerData.GetConsent;
import com.thaivan.bay.branch.customerData.GetConsentResponse;
import com.thaivan.bay.branch.customerData.ModelCitizenId;
import com.thaivan.bay.branch.customerData.ModelCitizenIdResponse;
import com.thaivan.bay.branch.customerData.ValidateQR;
import com.thaivan.bay.branch.customerData.ValidateQrResponse;
import com.thaivan.bay.branch.scan.ModelScan;
import com.thaivan.bay.branch.scan.ModelScanResponse;

import java.util.Map;

import io.reactivex.annotations.Nullable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ApiInterface {
//    String header[] = {"Content-Type: application/json;charset=utf-8",
//            "token: Bearer MTAwMDEwMTIwMTkwNzI1MDcyMzE4VDk5MDM1",
//            "timestamp: 20190725072318",
//            "clientId: T9901000101"};

    @FormUrlEncoded
    @POST("auth/oauth/v2/token")
    Call<ModelCitizenIdResponse> login(@Field("scope") String scope,
                                       @Field("grant_type") String grant_type,
                                       @Field("client_id") String client_id,
                                       @Field("client_secret") String client_secret
                                   );
//    Call<ModelCitizenIdResponse> login(@HeaderMap Map<String, String> headers, @Body ModelCitizenId model);

    @FormUrlEncoded
    @POST("customer/facial/recognition")
    Call<ModelFaceResponse> facecomp(@Field("X-Client-Transaction-ID") String ID,
                                  @Field("X-Client-Transaction-DateTime") String DateTime,
                                  @Field("Authorization") String Authorization,
                                     @Field("API-Key") String Key,
                                     @Field("X-Signature") String Signature,
                                     @Field("channel") String channel,
                                     @Field("sourceImage") String sourceImage,
                                     @Field("destinationImage") String destinationImage
    );

    @Nullable
    @POST(" bay/api/qrValidation")
    Call<ValidateQrResponse> scan(@HeaderMap Map<String, String> headers, @Body ValidateQR validateQR);

    @Nullable
    @POST("bay/api/submitCustomerData")
    Call<ModelCitizenIdResponse> submitTransactionData(@HeaderMap Map<String, String> headers, @Body ModelCitizenId model);

    @Nullable
    @POST("bay/api/consents/dopa")
    Call<GetConsentResponse> getConsent(@HeaderMap Map<String, String> headers, @Body GetConsent model);

    @Nullable
    @POST("bay/api/submitCustomerData/dopa")
    Call<DopaOnlyResponse> checkDopaOnly(@HeaderMap Map<String, String> headers, @Body DopaOnly dopaOnly);

}
