package com.thaivan.bay.branch;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thaivan.bay.branch.apimanager.ApiInterface;
import com.thaivan.bay.branch.apimanager.RetrofitClientInstance;
import com.thaivan.bay.branch.customerData.ModelCitizenId;
import com.thaivan.bay.branch.customerData.ModelCitizenIdResponse;
import com.thaivan.bay.branch.customerData.ValidateQR;
import com.thaivan.bay.branch.customerData.ValidateQrResponse;
import com.thaivan.bay.branch.scan.CaptureExtends;
import com.thaivan.bay.branch.scan.ModelScan;
import com.thaivan.bay.branch.scan.ModelScanResponse;
import com.thaivan.bay.branch.util.AES256Util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanQrActivity extends AppCompatActivity {

    public IntentIntegrator qrScan;
    public static int flag = 0;
    private Dialog dialogLoading;
    private String uuidRef="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customDialogLoading();
        qrScan = new IntentIntegrator(this);
        qrScan.setCaptureActivity(CaptureExtends.class);
        qrScan.setOrientationLocked(false);
        qrScan.setCameraId(flag);
        qrScan.initiateScan();
    }
    
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(Utility.IsDebug) {
            Log.d("requestCode :: ", String.valueOf(requestCode));
            Log.d("resultCode :: ", String.valueOf(resultCode));
            Log.d("data :: ", String.valueOf(data));
            Log.d("result :: ", String.valueOf(result));
        }
        if (result != null) {
            if( resultCode == 3){
                qrScan = null;
                qrScan = new IntentIntegrator(this);
                qrScan.setCaptureActivity(CaptureExtends.class);
                qrScan.setOrientationLocked(false);
                qrScan.setCameraId(flag);
                qrScan.initiateScan();
            }else if (resultCode == 4 ){
                qrScan = null;
                qrScan = new IntentIntegrator(this);
                qrScan.setCaptureActivity(CaptureExtends.class);
                qrScan.setOrientationLocked(false);
                qrScan.setCameraId(flag);
                qrScan.initiateScan();
            }else if (resultCode == 5 ){
                Intent intent = new Intent(ScanQrActivity.this, KeyinActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }else {
                if (result.getContents() == null) {
                    //backbutton
                    System.out.println("backbutton");
                    overridePendingTransition(0, 0);
                    finish();
                } else {
                    uuidRef = result.getContents();
                    Intent intent = new Intent(ScanQrActivity.this, ReadTHIDActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("uuid", uuidRef);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void customDialogLoading() {
        dialogLoading = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        View view = dialogLoading.getLayoutInflater().inflate(R.layout.dialog_custom_load_process, null);
        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoading.setContentView(view);
        dialogLoading.setCancelable(false);
    }

    @Override
    public void onBackPressed() {

    }
}
