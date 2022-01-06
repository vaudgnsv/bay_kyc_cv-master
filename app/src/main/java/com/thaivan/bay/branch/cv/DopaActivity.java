package com.thaivan.bay.branch.cv;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.thaivan.bay.branch.Preference;
import com.thaivan.bay.branch.R;
import com.thaivan.bay.branch.RtnActivity;
import com.thaivan.bay.branch.Tool;
import com.thaivan.bay.branch.apimanager.ApiInterface;
import com.thaivan.bay.branch.apimanager.RetrofitClientInstance;
import com.thaivan.bay.branch.customerData.GetConsent;
import com.thaivan.bay.branch.customerData.GetConsentResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DopaActivity extends AppCompatActivity {

    private LinearLayout linear_main;
    private Dialog dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_private_notice);
        Tool.setTitle("DopaActivity onCreate");

        linear_main = findViewById(R.id.linear_main);
        customDialogLoading();
        setCustomToolbar();
    }

    void setCustomToolbar() {
        Tool.setTitle("DopaActivity setCustomToolbar");

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blank));
    }

    public void scan(View view) {
        Tool.setTitle("DopaActivity scan");

        linear_main.setVisibility(View.INVISIBLE);
        dialogLoading.show();
        //Get Consent
        GetConsent getConsent = new GetConsent();
        getConsent.tid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_TERMINAL_ID);
        getConsent.mid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_MERCHANT_ID);
        getConsent.sn = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_SERIAL_NUMBER);

        ApiInterface apiInterface = RetrofitClientInstance.getInstance().getService();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        Call<GetConsentResponse> call = apiInterface.getConsent(headerMap, getConsent);
        call.enqueue(new Callback<GetConsentResponse>() {
            @Override
            public void onResponse(Call<GetConsentResponse> call, final Response<GetConsentResponse> response) {
                Tool.setTitle("DopaActivity scan onResponse");

                Tool.setTitle("DopaActivity scan onResponse code = " + response.body().statusCode);
                Tool.setTitle("DopaActivity scan onResponse reason = " + response.body().statusMessage);

                if(response.code() == HttpsURLConnection.HTTP_OK){
//                    dialogLoading.dismiss();
                    if(response.body().statusCode.equals("0000")) {
                        Tool.setTitle("DopaActivity scan onResponse 0000");

                        try {
                            Tool.setTitle("DopaActivity scan onResponse 0000 try");

                            Gson gson = new Gson();
                            String resp = gson.toJson(response.body().data);
                            JSONObject resp_data = new JSONObject(resp);
                            Intent intent = new Intent(DopaActivity.this, Dopa_PDPAActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("consents", resp_data.getString("consents"));
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        } catch (JSONException e) {
                            Tool.setTitle("DopaActivity scan onResponse 0000 catch");

                            e.printStackTrace();
                            Intent intent = new Intent(DopaActivity.this, RtnActivity.class);
                            intent.putExtra("code" , "ER");
                            intent.putExtra("reason" , "Please Contact THAIVAN");
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    } else{
                        try{
                            Tool.setTitle("DopaActivity scan onResponse try");

                            Intent intent = new Intent(DopaActivity.this, RtnActivity.class);
                            intent.putExtra("code" , response.body().statusCode);
                            intent.putExtra("reason" , response.body().statusMessage);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }catch (Exception e){
                            Tool.setTitle("DopaActivity scan onResponse catch");

                            e.printStackTrace();
                            Intent intent = new Intent(DopaActivity.this, RtnActivity.class);
                            intent.putExtra("code" , "ER");
                            intent.putExtra("reason" , "Please Contact THAIVAN");
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    }
                }else{
                    Tool.setTitle("DopaActivity scan onResponse else");

//                    dialogLoading.dismiss();
                    Intent intent = new Intent(DopaActivity.this, RtnActivity.class);
                    intent.putExtra("code" , String.valueOf(response.code()));
                    intent.putExtra("reason" , "API ERROR");
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<GetConsentResponse> call, Throwable t) {
                Tool.setTitle("DopaActivity scan onFailure");

                getConsent_Secondary();
            }
        });
    }

    private void getConsent_Secondary() {
        Tool.setTitle("DopaActivity getConsent_Secondary");

        GetConsent getConsent = new GetConsent();
        getConsent.tid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_TERMINAL_ID);
        getConsent.mid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_MERCHANT_ID);
        getConsent.sn = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_SERIAL_NUMBER);

        ApiInterface apiInterface = RetrofitClientInstance.getInstance().getService2();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        Call<GetConsentResponse> call = apiInterface.getConsent(headerMap, getConsent);
        call.enqueue(new Callback<GetConsentResponse>() {
            @Override
            public void onResponse(Call<GetConsentResponse> call, final Response<GetConsentResponse> response) {
                Tool.setTitle("DopaActivity getConsent_Secondary onResponse");

                if(response.code() == HttpsURLConnection.HTTP_OK){
                    dialogLoading.dismiss();
                    if(response.body().statusCode.equals("0000")) {
                        Tool.setTitle("DopaActivity getConsent_Secondary onResponse 0000");

                        try {
                            Tool.setTitle("DopaActivity getConsent_Secondary onResponse 0000 try");

                            JSONObject resp_data = new JSONObject(response.body().data.toString());
                            Intent intent = new Intent(DopaActivity.this, Dopa_PDPAActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("consents", resp_data.toString());
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        } catch (JSONException e) {
                            Tool.setTitle("DopaActivity getConsent_Secondary onResponse 0000 catch");

                            e.printStackTrace();
                            Intent intent = new Intent(DopaActivity.this, RtnActivity.class);
                            intent.putExtra("code" , "ER");
                            intent.putExtra("reason" , "Please Contact THAIVAN");
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    } else{
                        try{
                            Tool.setTitle("DopaActivity getConsent_Secondary onResponse try");

                            Intent intent = new Intent(DopaActivity.this, RtnActivity.class);
                            intent.putExtra("code" , response.body().statusCode);
                            intent.putExtra("reason" , response.body().statusMessage);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }catch (Exception e){
                            Tool.setTitle("DopaActivity getConsent_Secondary onResponse catch");

                            e.printStackTrace();
                            Intent intent = new Intent(DopaActivity.this, RtnActivity.class);
                            intent.putExtra("code" , "ER");
                            intent.putExtra("reason" , "Please Contact THAIVAN");
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    }
                }else{
                    Tool.setTitle("DopaActivity getConsent_Secondary onResponse else");

                    dialogLoading.dismiss();
                    Intent intent = new Intent(DopaActivity.this, RtnActivity.class);
                    intent.putExtra("code" , String.valueOf(response.code()));
                    intent.putExtra("reason" , "API ERROR");
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<GetConsentResponse> call, Throwable t) {
                Tool.setTitle("DopaActivity getConsent_Secondary onFailure");

                Intent intent = new Intent(DopaActivity.this, RtnActivity.class);
                intent.putExtra("code" , "ER");
                intent.putExtra("reason" , "Please Contact THAIVAN");
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private void customDialogLoading() {
        Tool.setTitle("DopaActivity customDialogLoading");

        dialogLoading = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        View view = dialogLoading.getLayoutInflater().inflate(R.layout.dialog_custom_load_process, null);
        TextView txt_msg = view.findViewById(R.id.txt_msg);
        txt_msg.setText("อยู่ระหว่างประมวลผล");
        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoading.setContentView(view);
        dialogLoading.setCancelable(false);
    }

    public void back_menu(View view) {
        Tool.setTitle("DopaActivity back_menu");

        finish();
    }

    @Override
    public void onBackPressed() {
        Tool.setTitle("DopaActivity onBackPressed");

    }


}
