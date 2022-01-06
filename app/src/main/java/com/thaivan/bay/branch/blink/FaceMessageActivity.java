package com.thaivan.bay.branch.blink;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thaivan.bay.branch.FailActivity;
import com.thaivan.bay.branch.Preference;
import com.thaivan.bay.branch.R;
import com.thaivan.bay.branch.RtnActivity;
import com.thaivan.bay.branch.SuccessActivity;
import com.thaivan.bay.branch.Utility;
import com.thaivan.bay.branch.apimanager.ApiInterface;
import com.thaivan.bay.branch.apimanager.RetrofitClientInstance;
import com.thaivan.bay.branch.customerData.ModelCitizenId;
import com.thaivan.bay.branch.customerData.ModelCitizenIdResponse;
import com.thaivan.bay.branch.util.AES256Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class FaceMessageActivity extends AppCompatActivity {

    private String type;
    private String chipNo;
    private String uuidRef;
    private JSONObject payload;
    private String data;
    private String consents;
    private String appId;
    private ModelCitizenIdResponse modelCitizenIdResponse;
    private Handler timer;
    private View mCustomView;
    private int retry_cnt = 0;
    private Dialog dialogLoading;
    private static final String TAG = "RealTimeFaceDetectionActivity";
    private AES256Util AES256 = new AES256Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_message);
        timer = new Handler(); //Handler 생성
        setCustomToolbar();
        customDialogLoading();
        generateData();
    }

    private void generateData() {
        try {
            dialogLoading.show();
            //set datatime
            Date date = new Date();
            DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
            String str_date = dateFormat2.format(date);

            String FilePath = "/storage/emulated/0/Pictures/";
            File file = new File(FilePath + "pic_photo.bmp");
            String filePath = file.getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            ByteArrayOutputStream aa = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, aa);
            byte[] a = aa.toByteArray();

            JSONObject jsonObject = new JSONObject(data);
            jsonObject.remove("transactionDateTime");
            jsonObject.remove("customerSelfieImage");

            jsonObject.put("transactionDateTime", str_date);
            jsonObject.put("customerSelfieImage", Base64.encodeToString(a, Base64.DEFAULT).replace("\n", "").replace("\\", ""));

            JSONArray consent_array = new JSONArray(consents);
            JSONObject consent1 = new JSONObject(consent_array.get(0).toString());
            JSONObject consent2 = new JSONObject(consent_array.get(1).toString());
            consent1.remove("contentTh");
            consent1.remove("contentEn");
            consent1.put("consent", "Y");
            consent2.remove("contentTh");
            consent2.remove("contentEn");
            consent2.put("consent", "Y");

            JSONArray customerConsents = new JSONArray();
            customerConsents.put(consent1);
            customerConsents.put(consent2);
            jsonObject.putOpt("customerConsents", customerConsents);

            String secretKey = AES256.generate_secretkey(); //  비밀키 생성

            PublicKey pubKey;
            if(Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_JSON_VERSION).equals("SIT"))
                pubKey = readPublicKeyFromAssets("pem/sit-alt.pub.pem");
            else if(Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_JSON_VERSION).equals("UAT"))
                pubKey = readPublicKeyFromAssets("pem/uat-alt.pub.pem");
            else
                pubKey = readPublicKeyFromAssets("pem/prd-alt.pub.pem");

//            makeFile(jsonObject.toString().replace("\n", "").replace("\\/",""), "submittrans_1");
            String payload_data = AES256.strEncode_secret(jsonObject.toString().replace("\n", "").replace("\\/",""), secretKey).replace("\n", ""); // 비밀키로 data AES256 암호화
            String payload_crc = AES256.encryptRSA(secretKey, pubKey); // 비밀키 RSA 공개키로 암호화
//            makeFile(payload_data, "submittrans_2");
            payload = new JSONObject();
            payload.put("data", payload_data);
            payload.put("crc", payload_crc);
            sendSubmitTransactionData_Primary(payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a)
            sb.append(String.format("%02x", b&0xff));
        return sb.toString();
    }

    private PublicKey readPublicKeyFromAssets(String file) {
        try {
            InputStream is = getAssets().open(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            //파일읽기
            String strResult = "";
            String line = "";
            while((line=reader.readLine()) != null){
                strResult += line;
            }

            String pubKeyPEM = strResult.replace("-----BEGIN PUBLIC KEY-----", "");
            pubKeyPEM = pubKeyPEM.replace("-----END PUBLIC KEY-----", "");

            // Base64 decode the data
            byte[] encoded = Base64.decode(pubKeyPEM, Base64.DEFAULT);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey pubkey = kf.generatePublic(keySpec);

            return pubkey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        File file = new File(fileName);
//        FileInputStream stream = null;
//        stream = new FileInputStream(file);
//        FileChannel fc = stream.getChannel();
//        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
//        /* Instead of using default, pass in a decoder. */
//        String jString = Charset.defaultCharset().decode(bb).toString();

        return null;
    }

    private void sendSubmitTransactionData_Primary(JSONObject payload) {
        ModelCitizenId citizenId = new ModelCitizenId();

        citizenId.tid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_TERMINAL_ID);
        citizenId.mid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_MERCHANT_ID);
        citizenId.sn = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_SERIAL_NUMBER);
        citizenId.appId = appId;
        citizenId.segment = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_SEGMENT);
        citizenId.payload = payload.toString();
//        ModelCitizenId.PayLoad payLoad = citizenId.new PayLoad();
//        payLoad.setData(data);
//        payLoad.setCrc(crc);
//        citizenId.setPayload(payLoad);

        ApiInterface apiInterface = RetrofitClientInstance.getInstance().getService();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
//        Gson gson = new Gson();
//        String req_data = gson.toJson(citizenId);
//        makeFile(req_data, "submittrans_3");
//        if(Utility.IsDebug)
//            Log.d("Test::", "send::" + req_data);
        Call<ModelCitizenIdResponse> call = apiInterface.submitTransactionData(headerMap, citizenId);
        call.enqueue(new Callback<ModelCitizenIdResponse>() {
            @Override
            public void onResponse(Call<ModelCitizenIdResponse> call, final Response<ModelCitizenIdResponse> response) {
                modelCitizenIdResponse = response.body();
                timer.postDelayed(new Runnable(){
                    public void run(){
                        mCustomView.setVisibility(View.INVISIBLE);
                        if(response.code() == HttpsURLConnection.HTTP_OK){
                            if(modelCitizenIdResponse.statusCode.equals("0000")) {
                                retry_cnt = 0;
                                if(Utility.IsDebug)
                                    Log.d("Test::", "tostring:" + response.toString());
                                Intent intent = new Intent(FaceMessageActivity.this, SuccessActivity.class);
                                intent.putExtra("type" , type);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                finish();
                            } else if(modelCitizenIdResponse.statusCode.equals("-201")||modelCitizenIdResponse.statusCode.equals("9997") || modelCitizenIdResponse.statusCode.equals("9999")) {
                                if(retry_cnt >= 2){
                                    retry_cnt = 0;
                                    Intent intent = new Intent(FaceMessageActivity.this, RtnActivity.class);
                                    intent.putExtra("code" , modelCitizenIdResponse.statusCode);
                                    intent.putExtra("reason" , "TIME OUT");
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }else {
                                    retry_cnt++;
                                    sendSubmitTransactionData_Primary(payload);
                                }
                            } else if(modelCitizenIdResponse.statusCode.equals("9300")||modelCitizenIdResponse.statusCode.equals("9301") || modelCitizenIdResponse.statusCode.equals("9302")) {
                                Intent intent = new Intent(FaceMessageActivity.this, RealTimeFaceDetectionActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.putExtra("type", type);
                                intent.putExtra("data", data);
                                intent.putExtra("consents", consents);
                                startActivity(intent);
                                finish();
                            } else{
                                retry_cnt = 0;
                                try{
                                    int error_code = Integer.parseInt(modelCitizenIdResponse.statusCode);
                                    Intent intent = new Intent(FaceMessageActivity.this, RtnActivity.class);
                                    intent.putExtra("code" , modelCitizenIdResponse.statusCode);
                                    intent.putExtra("reason" , modelCitizenIdResponse.statusMessage);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Intent intent = new Intent(FaceMessageActivity.this, RtnActivity.class);
                                    intent.putExtra("code" , modelCitizenIdResponse.statusCode);
                                    intent.putExtra("reason" , modelCitizenIdResponse.statusMessage);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }
                            }
                        }else{
                            retry_cnt = 0;
                            Intent intent = new Intent(FaceMessageActivity.this, RtnActivity.class);
                            intent.putExtra("code" , String.valueOf(response.code()));
                            intent.putExtra("reason" , "API ERROR");
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    }
                }, 1000); //1000은 1초를 의미한다.
            }

            @Override
            public void onFailure(Call<ModelCitizenIdResponse> call, Throwable t) {
                sendSubmitTransactionData_Secondary(payload);
            }
        });
    }

    private void sendSubmitTransactionData_Secondary(JSONObject payload) {
        ModelCitizenId citizenId = new ModelCitizenId();

        citizenId.tid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_TERMINAL_ID);
        citizenId.mid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_MERCHANT_ID);
        citizenId.sn = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_SERIAL_NUMBER);
        citizenId.appId = appId;
        citizenId.segment = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_SEGMENT);
        citizenId.payload = payload.toString();
//        ModelCitizenId.PayLoad payLoad = citizenId.new PayLoad();
//        payLoad.setData(data);
//        payLoad.setCrc(crc);
//        citizenId.setPayload(payLoad);

        ApiInterface apiInterface = RetrofitClientInstance.getInstance().getService2();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        Call<ModelCitizenIdResponse> call = apiInterface.submitTransactionData(headerMap, citizenId);
        call.enqueue(new Callback<ModelCitizenIdResponse>() {
            @Override
            public void onResponse(Call<ModelCitizenIdResponse> call, final Response<ModelCitizenIdResponse> response) {
                modelCitizenIdResponse = response.body();
                timer.postDelayed(new Runnable(){
                    public void run(){
                        mCustomView.setVisibility(View.INVISIBLE);
                        if(response.code() == HttpsURLConnection.HTTP_OK){
                            if(modelCitizenIdResponse.statusCode.equals("0000")) {
                                retry_cnt = 0;
                                Intent intent = new Intent(FaceMessageActivity.this, SuccessActivity.class);
                                intent.putExtra("type" , type);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                finish();
                            } else if(modelCitizenIdResponse.statusCode.equals("-201")) {
                                if(retry_cnt >= 2){
                                    retry_cnt = 0;
                                    Intent intent = new Intent(FaceMessageActivity.this, RtnActivity.class);
                                    intent.putExtra("code" , modelCitizenIdResponse.statusCode);
                                    intent.putExtra("reason" , modelCitizenIdResponse.statusMessage);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }else {
                                    retry_cnt++;
                                    sendSubmitTransactionData_Secondary(payload);
                                }
                            } else if(modelCitizenIdResponse.statusCode.equals("9997") || modelCitizenIdResponse.statusCode.equals("9999")) {
                                if(retry_cnt >= 2){
                                    retry_cnt = 0;
                                    Intent intent = new Intent(FaceMessageActivity.this, RtnActivity.class);
                                    intent.putExtra("code" , modelCitizenIdResponse.statusCode);
                                    intent.putExtra("reason" , modelCitizenIdResponse.statusMessage);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }else {
                                    retry_cnt++;
                                    sendSubmitTransactionData_Secondary(payload);
                                }
                            } else {
                                Intent intent = new Intent(FaceMessageActivity.this, RtnActivity.class);
                                intent.putExtra("code" , modelCitizenIdResponse.statusCode);
                                intent.putExtra("reason" , modelCitizenIdResponse.statusMessage);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                finish();
                            }
                        }
                    }
                }, 1000); //1000은 1초를 의미한다.
            }

            @Override
            public void onFailure(Call<ModelCitizenIdResponse> call, Throwable t) {
                Intent intent = new Intent(FaceMessageActivity.this, RtnActivity.class);
                intent.putExtra("code" , "HTTPS");
                intent.putExtra("reason" , "Connection Error");
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private void customDialogLoading() {
        dialogLoading = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        View view = dialogLoading.getLayoutInflater().inflate(R.layout.dialog_custom_load_process, null);
        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoading.setContentView(view);
        dialogLoading.setCancelable(false);
    }

    private void makeFile(String data, String fileName) {
        String str = data;

        File saveFile = new File("/mnt/sdcard"); // 저장 경로

        if(!saveFile.exists()){ // 폴더 없을 경우
            saveFile.mkdir(); // 폴더 생성
        }
        try {
            File existFile = new File("/mnt/sdcard/" + fileName + ".txt");
            existFile.delete();

            BufferedWriter buf = new BufferedWriter(new FileWriter(saveFile+"/"+fileName+".txt", true));
            buf.append(str); // 파일 쓰기
            buf.newLine(); // 개행
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setCustomToolbar() {
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.tool_bar));

        mCustomView = LayoutInflater.from(this).inflate(R.layout.custom_toolbar, null);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(mCustomView);
        }

        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        Toolbar toolbar = mCustomView.findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");
            chipNo = bundle.getString("chipNo") == null ? "" : bundle.getString("chipNo");
            uuidRef = bundle.getString("uuid") == null ? "" : bundle.getString("uuid");
            consents = bundle.getString("consents") == null ? "" : bundle.getString("consents");

            if(type.equals("KMA"))
                appId = "1";
            else if(type.equals("Kept"))
                appId = "2";
            else if(type.equals("UChoose"))
                appId = "3";
            else
                appId = "4";
            mTitle.setText("กรุณาสแกนใบหน้า");
        }
    }
}