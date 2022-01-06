package com.thaivan.bay.branch;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.thaivan.bay.branch.Adapter.zipCodeAdapter;
import com.thaivan.bay.branch.apimanager.ApiInterface;
import com.thaivan.bay.branch.apimanager.RetrofitClientInstance;
import com.thaivan.bay.branch.customerData.ValidateQR;
import com.thaivan.bay.branch.customerData.ValidateQrResponse;
import com.thaivan.bay.branch.cv.DopaActivity;
import com.thaivan.bay.branch.cv.Dopa_IDresultActivity;
import com.thaivan.bay.branch.cv.Dopa_PDPAActivity;
import com.thaivan.bay.branch.util.AES256Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadTHIDActivity extends AppCompatActivity {
    protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private CardManager cardManager;
    private String chipNo;
    private String uuid = "";
    private String type = "";
    private String consents = "";
    private String PostCode = "";
    private AnimationDrawable ani;
    private Dialog dialogLoading;
    private Dialog dialogIdResult;
    private Dialog dialogZipcode;
    private Dialog dialogZipcode2;
    private EditText edit_zipcode;
    private TextView txt_alert;
    private zipCodeAdapter zipCodeAdapter;
    private RecyclerView recyclerView_zipcode;
    private TextView txt_zipCode;
    private TextView CitizenId_tv;
    private TextView ThaiName_tv;
    private TextView EngName_tv;
    private TextView Birth_tv;
    private TextView Address_tv;
    private TextView CardExpireDate_tv;
    private ImageView Pic_img;
    private List<String> array_PostCode;
    private Spinner zipcodeSpinner;
    private ArrayAdapter adapterType;
    private AES256Util AES256 = new AES256Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_thid);
        Tool.setTitle("ReadTHIDActivity onCreate");

        setCustomToolbar();
        customDialogLoading();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            uuid = bundle.getString("uuid");
            consents = bundle.getString("consents") == null? "" : bundle.getString("consents");
        }
        ImageView img_insert = findViewById(R.id.img_insert);
        ani = (AnimationDrawable) img_insert.getDrawable();
        ani.start();
        cardManager = MainApplication.getCardManager();
        cardManager.setChipListener(new CardManager.ChipListener() {
            @Override
            public void onSuccess(String chip) {
                Tool.setTitle("ReadTHIDActivity onSuccess");

                ani.stop();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogLoading.show();
                    }
                });
                chipNo = chip;
                cardManager.stop_pboc2();
                cardManager.read_THID();
            }

            @Override
            public void onFail() {
                Tool.setTitle("ReadTHIDActivity onFail");

                cardManager.stop_pboc2();
                Intent intent = new Intent(ReadTHIDActivity.this, RtnActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("code", "THID");
                intent.putExtra("reason" , "ID card is broken");
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }

            @Override
            public void onTimeout() {
                Tool.setTitle("ReadTHIDActivity onTimeout");

                cardManager.stop_pboc2();
                dialogLoading.dismiss();
                Intent intent = new Intent(ReadTHIDActivity.this, RtnActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("code", "THID");
                intent.putExtra("reason" , "TIME OUT");
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        cardManager.setIdListener(new CardManager.IdListener() {

            @Override
            public void onFindID() {
                Tool.setTitle("ReadTHIDActivity onFindID");

                try {
                    Tool.setTitle("ReadTHIDActivity onFindID try");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cardManager.stop_THID();
                        }
                    });
                    JSONObject IDresp = new JSONObject(THID_info.getTh_info());
                    long time = System.currentTimeMillis();
                    SimpleDateFormat dayTime = new SimpleDateFormat("yyyymmdd");
                    String nowdate = dayTime.format(new Date(time));

                    String cmp_date = IDresp.getString("CardExpireDate");
                    cmp_date = String.valueOf(Integer.parseInt(cmp_date.substring(4,8))-543) + cmp_date.substring(2,4) + cmp_date.substring(0,2);

                    if(Long.parseLong(cmp_date) <= Long.parseLong(nowdate)) {
                        Tool.setTitle("ReadTHIDActivity onFindID try if");

                        Intent intent = new Intent(ReadTHIDActivity.this, RtnActivity.class);
                        intent.putExtra("code", "EXPIRED");
                        intent.putExtra("reason", "ExpiredDate");
                        startActivity(intent);
                        finish();
                    }
                } catch(Exception e) {
                    Tool.setTitle("ReadTHIDActivity onFindID catch");

                    e.printStackTrace();
                }

                if(type.equals("TEST")){
                    Tool.setTitle("ReadTHIDActivity onFindID TEST");

                    Intent intent = new Intent(ReadTHIDActivity.this, IDresultActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("type", type);
                    intent.putExtra("chipNo", chipNo);
                    intent.putExtra("fr", "Y");
                    intent.putExtra("uuid", uuid);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }else{
                    Tool.setTitle("ReadTHIDActivity onFindID else");

                    try {
                        Tool.setTitle("ReadTHIDActivity onFindID else try");

                        JSONObject IDresp = new JSONObject(THID_info.getTh_info());
                        String CitizenId = IDresp.getString("CitizenId");
                        String SubDistrict = IDresp.getString("SubDistrict");
                        String District = IDresp.getString("District");
                        String Province = IDresp.getString("Province");
                        PostCode = "";
                        JSONArray jsonArray = new JSONArray(Preference.getInstance(getApplicationContext()).getValueString(Preference.POST_CODE));
                        if(Utility.IsDebug)
                            Log.d("Test::","Province : " + Province + ",District : " + District + ", + SubDistrict : " + SubDistrict);

                        String pr = Province.replace("จังหวัด",""); // province parsing
                        String dis = District.replace("เขต","").replace("อำเภอ","").replace(pr,""); // 방콕인경우(เขต), 방콕이 아닌경우(อำเภอ)
                        String subdis = SubDistrict.replace("แขวง","").replace("ตำบล",""); // 방콕인경우(แขวง), 방콕이 아닌경우(ตำบล)

                        array_PostCode = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject();
                        int cnt_postCode = 0;

                        for(int i = 0; i<jsonArray.length(); i++) {
                            jsonObject = (JSONObject)jsonArray.get(i); // jsonarray의 각 jsonobject
                            String[] value = jsonObject.getString("value").split(","); // post_code 비교할 City, District, subDistrict 분리
                            if(pr.equals(value[0]) && dis.contains(value[1]) && subdis.equals(value[2])) {
                                array_PostCode.add(value[3]);
                                cnt_postCode++;
                            }
                        }

                        if(uuid.equals("dopa")){
                            Tool.setTitle("ReadTHIDActivity onFindID else try dopa");

                            if(Utility.IsDebug)
                                Log.d("Test::","DOPA_ONLY");
                            //Dopa Only
                            Intent intent = new Intent(ReadTHIDActivity.this, Dopa_IDresultActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("chipNo", chipNo);
                            intent.putExtra("fr", "N");
                            intent.putExtra("uuid", uuid);
                            intent.putExtra("consents", consents);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }else{
                            Tool.setTitle("ReadTHIDActivity onFindID else try else");

                            //Normal Process
                            if(cnt_postCode == 1){
                                Tool.setTitle("ReadTHIDActivity onFindID else try else 1");

                                validateQR();
                            }else{
                                Tool.setTitle("ReadTHIDActivity onFindID else try else else");

                                Intent intent = new Intent(ReadTHIDActivity.this, IDresultActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.putExtra("type", type);
                                intent.putExtra("chipNo", chipNo);
                                intent.putExtra("fr", "N");
                                intent.putExtra("uuid", uuid);
                                intent.putExtra("consents", "NONE");
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        Tool.setTitle("ReadTHIDActivity onFindID else try else catch");

                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFail() {
                Tool.setTitle("ReadTHIDActivity onFail 2");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cardManager.stop_THID();
                    }
                });
                dialogLoading.dismiss();
                Intent intent = new Intent(ReadTHIDActivity.this, RtnActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("code", "THID");
                intent.putExtra("reason" , "Read ID card");
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }

            @Override
            public void onError() {
                Tool.setTitle("ReadTHIDActivity onError");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cardManager.stop_THID();
                    }
                });
                dialogLoading.dismiss();
                Intent intent = new Intent(ReadTHIDActivity.this, RtnActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("code", "THID");
                intent.putExtra("reason" , "Try again.");
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        cardManager.allOperateStart(false, true, false);
    }

    public String dateThai(String strDate) {
        Tool.setTitle("ReadTHIDActivity dateThai");

        String Months[] = {
                "มกราคม", "กุมภาพันธ์ ", "มีนาคม", "เมษายน",

                "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม",

                "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};

        int year = Integer.parseInt(strDate.substring(4, 8));
        int month = Integer.parseInt(strDate.substring(2, 4)) - 1;
        int day = Integer.parseInt(strDate.substring(0, 2));

        return String.format("%s %s %s", day, Months[month], year);
    }

    private void setPicture(final ImageView pic_img, final byte[] pic) {
        Tool.setTitle("ReadTHIDActivity setPicture");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String szPhoto = new String(bytesToHex(pic));
                String szCheck = szPhoto.substring(szPhoto.length() - 4, szPhoto.length());
                if (!szCheck.equalsIgnoreCase("FF9D"))
                    szPhoto = szPhoto + "FF9D";

                byte[] ppp;
                ppp = hexStringToByteArray(szPhoto);
                Bitmap _bm = BitmapFactory.decodeByteArray(ppp, 0, ppp.length);
//                byte[] aaa = android.util.Base64.encode(ppp, android.util.Base64.NO_WRAP);
//                makeFile(new String (aaa), "pic_id");
                pic_img.setImageBitmap(_bm);

                //old file delete(V30)
                File existFile = new File("/sdcard/oversea_ct/bay_branch/" + "pic_id.txt");
                if(existFile.exists())
                    existFile.delete();
            }
        });
    }

    public static String bytesToHex(byte[] bytes) {
        Tool.setTitle("ReadTHIDActivity bytesToHex");

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        Tool.setTitle("ReadTHIDActivity hexStringToByteArray");

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private void customDialogLoading() {
        Tool.setTitle("ReadTHIDActivity customDialogLoading");

        dialogLoading = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        View view = dialogLoading.getLayoutInflater().inflate(R.layout.dialog_custom_load_process, null);
        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoading.setContentView(view);
        dialogLoading.setCancelable(false);
    }

//    private void customDialogConfirm() {
//        dialogIdResult = new Dialog(ReadTHIDActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
//        View view = dialogIdResult.getLayoutInflater().inflate(R.layout.dialog_idresult, null);
//        dialogIdResult.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogIdResult.setContentView(view);
//        dialogIdResult.setCancelable(false);
//
//        Toolbar toolbar = dialogIdResult.findViewById(R.id.item_menu);
//        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
//        mTitle.setText("ยืนยันข้อมูล");
//        Button button_cancel = dialogIdResult.findViewById(R.id.button_cancel);
//        Button button_send = dialogIdResult.findViewById(R.id.button_send);
//
//        button_send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogIdResult.dismiss();
//                validateQR();
//            }
//        });
//        button_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ReadTHIDActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(intent);
//                overridePendingTransition(0, 0);
//                finish();
//            }
//        });
//
//        txt_zipCode = dialogIdResult.findViewById(R.id.txt_Zipcode);
//        CitizenId_tv = dialogIdResult.findViewById(R.id.txt_citizenid);
//        ThaiName_tv = dialogIdResult.findViewById(R.id.txt_thainame);
//        EngName_tv = dialogIdResult.findViewById(R.id.txt_engname);
//        Birth_tv = dialogIdResult.findViewById(R.id.txt_birth);
//        Address_tv = dialogIdResult.findViewById(R.id.txt_address);
//        CardExpireDate_tv = dialogIdResult.findViewById(R.id.txt_expire);
//        Pic_img = dialogIdResult.findViewById(R.id.img_pic);
//        zipcodeSpinner = dialogIdResult.findViewById(R.id.spinner);
//    }
//
//    private void customDialogZipCode() {
//        dialogZipcode = new Dialog(ReadTHIDActivity.this, R.style.ThemeWithBay);
//        View view = dialogZipcode.getLayoutInflater().inflate(R.layout.dialog_zipcode, null);
//        dialogZipcode.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogZipcode.setContentView(view);
//        dialogZipcode.setCancelable(false);
//        edit_zipcode = view.findViewById(R.id.edit_zipcode);
//        txt_alert = view.findViewById(R.id.txt_alert);
//    }
//
//    private void customDialogZipCode2() {
//
//        dialogZipcode2 = new Dialog(ReadTHIDActivity.this, R.style.ThemeWithBay);
//        View view = dialogZipcode2.getLayoutInflater().inflate(R.layout.dialog_zipcode2, null);
//        dialogZipcode2.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogZipcode2.setContentView(view);
//        dialogZipcode2.setCancelable(false);
//        recyclerView_zipcode = view.findViewById(R.id.recycler_zipcode);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView_zipcode.setLayoutManager(layoutManager);
//    }
//
//    public void zipCode(View view) {
//        PostCode = edit_zipcode.getText().toString();
//
//        if(PostCode.length() == 5){
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    txt_zipCode.setText(PostCode);
//                    txt_alert.setVisibility(View.GONE);
//                    edit_zipcode.setBackground(getResources().getDrawable(R.drawable.border_gray));
//                    dialogZipcode.dismiss();
//                }
//            });
//        }else{
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    edit_zipcode.setBackground(getResources().getDrawable(R.drawable.border_red));
//                    txt_alert.setVisibility(View.VISIBLE);
//                }
//            });
//        }
//
//    }

    void setCustomToolbar() {
        Tool.setTitle("ReadTHIDActivity setCustomToolbar");

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.tool_bar));

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.custom_toolbar, null);

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
        mTitle.setText("ยืนยันตัวตน");
    }


    public void validateQR(){
        Tool.setTitle("ReadTHIDActivity validateQR");

        Preference.getInstance(getApplicationContext()).setValueString(Preference.TEMP_POST_CODE, PostCode);
        try {
            Tool.setTitle("ReadTHIDActivity validateQR try");

            JSONObject jsonObject = new JSONObject();
            JSONObject payLoad = new JSONObject();

            //REAL
            jsonObject.put("reference", uuid);
            JSONObject IDresp = new JSONObject(THID_info.getTh_info());
            jsonObject.put("identifier", IDresp.getString("CitizenId"));
            jsonObject.put("namespace", "citizen_id");

            String secretKey = AES256.generate_secretkey(); //  비밀키 생성
            String encryptedKey = "";
            String crc = "";
            String encryptedData = "";

            PublicKey pubKey;
            if(Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_JSON_VERSION).equals("SIT"))
                pubKey = readPublicKeyFromAssets("pem/sit-alt.pub.pem");
            else if(Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_JSON_VERSION).equals("UAT"))
                pubKey = readPublicKeyFromAssets("pem/uat-alt.pub.pem");
            else
                pubKey = readPublicKeyFromAssets("pem/prd-alt.pub.pem");

            encryptedKey = AES256.encryptRSA(secretKey, pubKey); // 비밀키 RSA 공개키로 암호화
            crc = encryptedKey;  // IV와 암호화된 비밀키 붙여서 CRC에 넣음
            encryptedData = AES256.strEncode_secret(jsonObject.toString(), secretKey).replace("\n", ""); // 비밀키로 data AES256 암호화

            payLoad.put("data", encryptedData);
            payLoad.put("crc", crc);
            sendValidateQR_Primary(payLoad);
        } catch (Exception e) {
            Tool.setTitle("ReadTHIDActivity validateQR catch");

            e.printStackTrace();
        }
    }

    private void sendValidateQR_Primary(final JSONObject jsonObject_payload) {
        Tool.setTitle("ReadTHIDActivity sendValidateQR_Primary");

        ValidateQR validateQR = new ValidateQR();

        validateQR.tid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_TERMINAL_ID);
        validateQR.mid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_MERCHANT_ID);
        validateQR.sn = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_SERIAL_NUMBER);
        validateQR.segment = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_SEGMENT);
        validateQR.payload = jsonObject_payload.toString();

        ApiInterface apiInterface = RetrofitClientInstance.getInstance().getService();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");

        Call<ValidateQrResponse> call = apiInterface.scan(headerMap, validateQR);
        call.enqueue(new Callback<ValidateQrResponse>() {
            @Override
            public void onResponse(Call<ValidateQrResponse> call, final Response<ValidateQrResponse> response) {
                Tool.setTitle("ReadTHIDActivity sendValidateQR_Primary onResponse");

                if(response.code() == HttpsURLConnection.HTTP_OK){
                    dialogLoading.dismiss();

                    if(response.body().statusCode.equals("0000")) {
                        Tool.setTitle("ReadTHIDActivity sendValidateQR_Primary onResponse 0000");

                        try {
                            Tool.setTitle("ReadTHIDActivity sendValidateQR_Primary onResponse 0000 try");

                            Gson gson = new Gson();
                            String resp = gson.toJson(response.body().data);
//                            String resp = "{\"reference\":\"cb99cf1440f24b7db6e2c5fbb7980e7799144202108251112007335\",\"ref1\":\"test ref1\",\"ref2\":\"test ref2\",\"ref3\":\"test ref3\",\"faceCompare\":\"Y\",\"channelCode\":\"MIRAI\",\"consents\":[{\"type\":\"103\",\"version\":\"2.00.00\",\"contentTh\":\"ยินยอมให้กลุ่มกรุงศรีเก็บรวบรวม ข้อมูลชีวภาพของคุณ เช่น ข้อมูลภาพจำลองใบหน้า ข้อมูลจำลองลายนิ้วมือ เป็นต้นไม่ว่ากลุ่มกรุงศรีจะได้รับจากคุณโดยตรงหรือจากแหล่งอื่น ใช้ และ\\/หรือ เปิดเผยข้อมูลดังกล่าวให้แก่ ผู้ให้บริการภายนอกของกลุ่มกรุงศรี (outsourcer) ตัวแทนของกลุ่มกรุงศรี (agent) ผู้รับจ้างช่วงงานต่อของกลุ่มกรุงศรี (subcontractor)  เพื่อการระบุและพิสูจน์ตัวตนทางอิเล็กทรอนิกส์ ตามกระบวนการทำความรู้จักลูกค้าทางอิเล็กทรอนิกส์ (Electronic Know Your Customer (E-KYC)) จากการสมัครหรือใช้ผลิตภัณฑ์หรือบริการ ของกลุ่มกรุงศรี และเพื่อวัตถุประสงค์ในการสร้างลายมือชื่ออิเล็กทรอนิกส์ เช่น การเปิดบัญชีเงินฝากออนไลน์ หรือการขอสินเชื่อออนไลน์ เป็นต้น\",\"contentEn\":\"Give consent for Krungsri Group to collect your biometric data, such as facial and fingerprint (either directly from you or from other sources), use, and\\/or disclose such Data to Krungsri Group's outsourcers, agents, and subcontractors for electronic identity authentication according to the Electronic Know Your Customer (E-KYC) process from applying for Krungsri Group's products and services, and for the creation of electronic signatures such as opening online accounts or online loan applications.\"},{\"type\":\"104\",\"version\":\"2.00.00\",\"contentTh\":\"ยินยอมให้กลุ่มกรุงศรี เปิดเผย ส่งหรือโอนข้อมูลของคุณไปยังต่างประเทศ อันได้แก่ (1) ผู้ให้บริการภายนอกของกลุ่มกรุงศรี (outsourcer) ตัวแทนของกลุ่มกรุงศรี (agent) ผู้รับจ้างช่วงงานต่อของกลุ่มกรุงศรี (subcontractor) (2) MUFG Bank Ltd. และบริษัทในเครือของ MUFG Bank Ltd.*** รวมทั้งบริษัทแม่ ซึ่งได้แก่ มิตซูบิชิ ยูเอฟเจ ไฟแนนเชียล กรุ๊ป (MUFG) เพื่อวัตถุประสงค์โดยชอบด้วยกฎหมายและการควบคุมภายในตามเงื่อนไขที่กำหนดไว้ในประกาศการคุ้มครองข้อมูลส่วนบุคคลของกลุ่มกรุงศรี\",\"contentEn\":\"Give consent for Krungsri Group to disclose, send, or transfer your Data to other countries including (1) to Krungsri Group's outsourcers, agents, and subcontractors; and (2) to MUFG Bank, Ltd. and its affiliates*** as well as its ultimate parent company, Mitsubishi UFJ Financial Group (MUFG), for lawful and internal control purposes as set out in Krungsri Group's Privacy Notice.\"\n" +
//                                    "},{\"type\":\"105\",\"version\":\"2.00.00\",\"contentTh\":\"ยินยอมให้กลุ่มกรุงศรี เปิดเผย ส่งหรือโอนข้อมูลของคุณไปยังต่างประเทศ อันได้แก่ (1) ผู้ให้บริการภายนอกของกลุ่มกรุงศรี (outsourcer) ตัวแทนของกลุ่มกรุงศรี (agent) ผู้รับจ้างช่วงงานต่อของกลุ่มกรุงศรี (subcontractor) (2) MUFG Bank Ltd. และบริษัทในเครือของ MUFG Bank Ltd.*** รวมทั้งบริษัทแม่ ซึ่งได้แก่ มิตซูบิชิ ยูเอฟเจ ไฟแนนเชียล กรุ๊ป (MUFG) เพื่อวัตถุประสงค์โดยชอบด้วยกฎหมายและการควบคุมภายในตามเงื่อนไขที่กำหนดไว้ในประกาศการคุ้มครองข้อมูลส่วนบุคคลของกลุ่มกรุงศรี\",\n" +
//                                    "\"contentEn\":\"Give consent for Krungsri Group to disclose, send, or transfer your Data to other countries including (1) to Krungsri Group's outsourcers, agents, and subcontractors; and (2) to MUFG Bank, Ltd. and its affiliates*** as well as its ultimate parent company, Mitsubishi UFJ Financial Group (MUFG), for lawful and internal control purposes as set out in Krungsri Group's Privacy Notice.\"\n" +
//                                    "}]}";
                            JSONObject resp_data = new JSONObject(resp);
//                            Log.d("Test::", resp_data.toString());
//                            String consents_1 = "[{\"type\":\"105\",\"version\":\"2.00.00\",\"contentTh\":\"ยินยอมให้กลุ่มกรุงศรี เปิดเผย ส่งหรือโอนข้อมูลของคุณไปยังต่างประเทศ อันได้แก่ (1) ผู้ให้บริการภายนอกของกลุ่มกรุงศรี (outsourcer) ตัวแทนของกลุ่มกรุงศรี (agent) ผู้รับจ้างช่วงงานต่อของกลุ่มกรุงศรี (subcontractor) (2) MUFG Bank Ltd. และบริษัทในเครือของ MUFG Bank Ltd.*** รวมทั้งบริษัทแม่ ซึ่งได้แก่ มิตซูบิชิ ยูเอฟเจ ไฟแนนเชียล กรุ๊ป (MUFG) เพื่อวัตถุประสงค์โดยชอบด้วยกฎหมายและการควบคุมภายในตามเงื่อนไขที่กำหนดไว้ในประกาศการคุ้มครองข้อมูลส่วนบุคคลของกลุ่มกรุงศรี\",\"contentEn\":\"Give consent for Krungsri Group to disclose, send, or transfer your Data to other countries including (1) to Krungsri Group's outsourcers, agents, and subcontractors; and (2) to MUFG Bank, Ltd. and its affiliates*** as well as its ultimate parent company, Mitsubishi UFJ Financial Group (MUFG), for lawful and internal control purposes as set out in Krungsri Group's Privacy Notice.\"}]";
//                            String consents_2 = "[{\"type\":\"103\",\"version\":\"2.00.00\",\"contentTh\":\"ยินยอมให้กลุ่มกรุงศรีเก็บรวบรวม ข้อมูลชีวภาพของคุณ เช่น ข้อมูลภาพจำลองใบหน้า ข้อมูลจำลองลายนิ้วมือ เป็นต้นไม่ว่ากลุ่มกรุงศรีจะได้รับจากคุณโดยตรงหรือจากแหล่งอื่น ใช้ และ\\/หรือ เปิดเผยข้อมูลดังกล่าวให้แก่ ผู้ให้บริการภายนอกของกลุ่มกรุงศรี (outsourcer) ตัวแทนของกลุ่มกรุงศรี (agent) ผู้รับจ้างช่วงงานต่อของกลุ่มกรุงศรี (subcontractor)  เพื่อการระบุและพิสูจน์ตัวตนทางอิเล็กทรอนิกส์ ตามกระบวนการทำความรู้จักลูกค้าทางอิเล็กทรอนิกส์ (Electronic Know Your Customer (E-KYC)) จากการสมัครหรือใช้ผลิตภัณฑ์หรือบริการ ของกลุ่มกรุงศรี และเพื่อวัตถุประสงค์ในการสร้างลายมือชื่ออิเล็กทรอนิกส์ เช่น การเปิดบัญชีเงินฝากออนไลน์ หรือการขอสินเชื่อออนไลน์ เป็นต้น\",\"contentEn\":\"Give consent for Krungsri Group to collect your biometric data, such as facial and fingerprint (either directly from you or from other sources), use, and\\/or disclose such Data to Krungsri Group's outsourcers, agents, and subcontractors for electronic identity authentication according to the Electronic Know Your Customer (E-KYC) process from applying for Krungsri Group's products and services, and for the creation of electronic signatures such as opening online accounts or online loan applications.\"},{\"type\":\"105\",\"version\":\"2.00.00\",\"contentTh\":\"ยินยอมให้กลุ่มกรุงศรี เปิดเผย ส่งหรือโอนข้อมูลของคุณไปยังต่างประเทศ อันได้แก่ (1) ผู้ให้บริการภายนอกของกลุ่มกรุงศรี (outsourcer) ตัวแทนของกลุ่มกรุงศรี (agent) ผู้รับจ้างช่วงงานต่อของกลุ่มกรุงศรี (subcontractor) (2) MUFG Bank Ltd. และบริษัทในเครือของ MUFG Bank Ltd.*** รวมทั้งบริษัทแม่ ซึ่งได้แก่ มิตซูบิชิ ยูเอฟเจ ไฟแนนเชียล กรุ๊ป (MUFG) เพื่อวัตถุประสงค์โดยชอบด้วยกฎหมายและการควบคุมภายในตามเงื่อนไขที่กำหนดไว้ในประกาศการคุ้มครองข้อมูลส่วนบุคคลของกลุ่มกรุงศรี\",\"contentEn\":\"Give consent for Krungsri Group to disclose, send, or transfer your Data to other countries including (1) to Krungsri Group's outsourcers, agents, and subcontractors; and (2) to MUFG Bank, Ltd. and its affiliates*** as well as its ultimate parent company, Mitsubishi UFJ Financial Group (MUFG), for lawful and internal control purposes as set out in Krungsri Group's Privacy Notice.\"}]";
//                            String consents_3 = "[{"type":"105","version":"2.00.00","contentTh":"ยินยอมให้กลุ่มกรุงศรี เปิดเผย ส่งหรือโอนข้อมูลของคุณไปยังต่างประเทศ อันได้แก่ (1) ผู้ให้บริการภายนอกของกลุ่มกรุงศรี (outsourcer) ตัวแทนของกลุ่มกรุงศรี (agent) ผู้รับจ้างช่วงงานต่อของกลุ่มกรุงศรี (subcontractor) (2) MUFG Bank Ltd. และบริษัทในเครือของ MUFG Bank Ltd.*** รวมทั้งบริษัทแม่ ซึ่งได้แก่ มิตซูบิชิ ยูเอฟเจ ไฟแนนเชียล กรุ๊ป (MUFG) เพื่อวัตถุประสงค์โดยชอบด้วยกฎหมายและการควบคุมภายในตามเงื่อนไขที่กำหนดไว้ในประกาศการคุ้มครองข้อมูลส่วนบุคคลของกลุ่มกรุงศรี","contentEn":"Give consent for Krungsri Group to disclose, send, or transfer your Data to other countries including (1) to Krungsri Group's outsourcers, agents, and subcontractors; and (2) to MUFG Bank, Ltd. and its affiliates*** as well as its ultimate parent company, Mitsubishi UFJ Financial Group (MUFG), for lawful and internal control purposes as set out in Krungsri Group's Privacy Notice."}]},"statusCode":"0000","statusMessage":"Success"}]";
//                            String faceCompare = "N";

                            Intent intent = new Intent(ReadTHIDActivity.this, PDPAActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("type", resp_data.getString("channelCode"));
                            intent.putExtra("chipNo", chipNo);
                            intent.putExtra("fr",  resp_data.getString("faceCompare"));
                            intent.putExtra("consents", resp_data.getString("consents"));
                            intent.putExtra("uuid", uuid);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        } catch (JSONException e) {
                            Tool.setTitle("ReadTHIDActivity sendValidateQR_Primary onResponse 0000 catch");

                            e.printStackTrace();
                            Intent intent = new Intent(ReadTHIDActivity.this, RtnActivity.class);
                            intent.putExtra("code" , "ER");
                            intent.putExtra("reason" , "Please Contact THAIVAN");
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    } else{
                        Tool.setTitle("ReadTHIDActivity sendValidateQR_Primary onResponse else");

                        try{
                            Tool.setTitle("ReadTHIDActivity sendValidateQR_Primary onResponse else try");

                            Intent intent = new Intent(ReadTHIDActivity.this, RtnActivity.class);
                            intent.putExtra("code" , response.body().statusCode);
                            intent.putExtra("reason" , response.body().statusMessage);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }catch (Exception e){
                            Tool.setTitle("ReadTHIDActivity sendValidateQR_Primary onResponse else catch");

                            e.printStackTrace();
                            Intent intent = new Intent(ReadTHIDActivity.this, RtnActivity.class);
                            intent.putExtra("code" , "ER");
                            intent.putExtra("reason" , "Please Contact THAIVAN");
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    }
                }else{
                    Tool.setTitle("ReadTHIDActivity sendValidateQR_Primary onResponse else else");

                    dialogLoading.dismiss();

                    Intent intent = new Intent(ReadTHIDActivity.this, RtnActivity.class);
                    intent.putExtra("code" , String.valueOf(response.code()));
                    intent.putExtra("reason" , "API ERROR");
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ValidateQrResponse> call, Throwable t) {
                Tool.setTitle("ReadTHIDActivity sendValidateQR_Primary onFailure");

                sendValidateQR_Secondary(jsonObject_payload);
            }
        });
    }

    private void sendValidateQR_Secondary(final JSONObject jsonObject_payload) {
        Tool.setTitle("ReadTHIDActivity sendValidateQR_Secondary");

        ValidateQR validateQR = new ValidateQR();

        validateQR.tid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_TERMINAL_ID);
        validateQR.mid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_MERCHANT_ID);
        validateQR.sn = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_SERIAL_NUMBER);
        validateQR.segment = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_SEGMENT);
        validateQR.payload = jsonObject_payload.toString();

        ApiInterface apiInterface = RetrofitClientInstance.getInstance().getService2();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        Call<ValidateQrResponse> call = apiInterface.scan(headerMap, validateQR);
        call.enqueue(new Callback<ValidateQrResponse>() {
            @Override
            public void onResponse(Call<ValidateQrResponse> call, final Response<ValidateQrResponse> response) {
                Tool.setTitle("ReadTHIDActivity sendValidateQR_Secondary onResponse");

                if(response.code() == HttpsURLConnection.HTTP_OK){
                    if(response.body().statusCode.equals("0000")) {
                        Tool.setTitle("ReadTHIDActivity sendValidateQR_Secondary onResponse 0000");

                        try {
                            Tool.setTitle("ReadTHIDActivity sendValidateQR_Secondary onResponse 0000 try");

                            JSONObject resp_data = new JSONObject(response.body().data.toString());
                            String faceCompare = resp_data.getString("faceCompare");
                            Intent intent = new Intent(ReadTHIDActivity.this, PDPAActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("type", type);
                            intent.putExtra("chipNo", chipNo);
                            intent.putExtra("fr", faceCompare);
                            intent.putExtra("uuid", uuid);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        } catch (JSONException e) {
                            Tool.setTitle("ReadTHIDActivity sendValidateQR_Secondary onResponse 0000 catch");

                            e.printStackTrace();
                            Intent intent = new Intent(ReadTHIDActivity.this, PDPAActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("type", type);
                            intent.putExtra("chipNo", chipNo);
                            intent.putExtra("fr", "N");
                            intent.putExtra("uuid", uuid);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    } else{
                        Tool.setTitle("ReadTHIDActivity sendValidateQR_Secondary onResponse else");

                        dialogLoading.dismiss();
                        try{
                            Tool.setTitle("ReadTHIDActivity sendValidateQR_Secondary onResponse else try");

                            Intent intent = new Intent(ReadTHIDActivity.this, RtnActivity.class);
                            intent.putExtra("code" , response.body().statusCode);
                            intent.putExtra("reason" , response.body().statusMessage);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }catch (Exception e){
                            Tool.setTitle("ReadTHIDActivity sendValidateQR_Secondary onResponse else catch");

                            e.printStackTrace();
                            Intent intent = new Intent(ReadTHIDActivity.this, RtnActivity.class);
                            intent.putExtra("code" , "ER");
                            intent.putExtra("reason" , "Please Contact THAIVAN");
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    }
                }else{
                    Tool.setTitle("ReadTHIDActivity sendValidateQR_Secondary onResponse else else");

                    dialogLoading.dismiss();

                    Intent intent = new Intent(ReadTHIDActivity.this, RtnActivity.class);
                    intent.putExtra("code" , String.valueOf(response.code()));
                    intent.putExtra("reason" , "API ERROR");
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ValidateQrResponse> call, Throwable t) {
                Tool.setTitle("ReadTHIDActivity sendValidateQR_Secondary onFailure");

                dialogLoading.dismiss();
                Intent intent = new Intent(ReadTHIDActivity.this, RtnActivity.class);
                intent.putExtra("code" , "HTTPS");
                intent.putExtra("reason" , "Connection Error");
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private PublicKey readPublicKeyFromAssets(String file) {
        Tool.setTitle("ReadTHIDActivity readPublicKeyFromAssets");

        try {
            Tool.setTitle("ReadTHIDActivity readPublicKeyFromAssets try");

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
            Tool.setTitle("ReadTHIDActivity readPublicKeyFromAssets catch 1");

            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            Tool.setTitle("ReadTHIDActivity readPublicKeyFromAssets catch 2");

            e.printStackTrace();
        } catch (IOException e) {
            Tool.setTitle("ReadTHIDActivity readPublicKeyFromAssets catch 3");

            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        Tool.setTitle("ReadTHIDActivity onBackPressed");

    }

    public void back_menu(View view) {
        Tool.setTitle("ReadTHIDActivity back_menu");

        cardManager.stop_THID();
        cardManager.stop_pboc2();
        dialogLoading.dismiss();
        if(type.equals("")){
            Tool.setTitle("ReadTHIDActivity back_menu 1");

            Intent intent = new Intent(ReadTHIDActivity.this, Dopa_PDPAActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("consents", consents);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }else{
            Tool.setTitle("ReadTHIDActivity back_menu 2");

            Intent intent = new Intent(ReadTHIDActivity.this, ScanQrActivity.class);
            intent.putExtra("type", type);
            startActivity(intent);
            finish();
        }
    }

    private void makeFile(String data, String fileName) {
        Tool.setTitle("ReadTHIDActivity makeFile");

        String str = data;

        File saveFile = new File("/sdcard/oversea_ct/bay_branch"); // 저장 경로

        if(!saveFile.exists()){ // 폴더 없을 경우
            saveFile.mkdir(); // 폴더 생성
        }
        try {
            Tool.setTitle("ReadTHIDActivity makeFile try");

            File existFile = new File("/sdcard/oversea_ct/bay_branch/" + fileName + ".txt");
            existFile.delete();

            BufferedWriter buf = new BufferedWriter(new FileWriter(saveFile+"/"+fileName+".txt", true));
            buf.append(str); // 파일 쓰기
            buf.newLine(); // 개행
            buf.close();
        } catch (FileNotFoundException e) {
            Tool.setTitle("ReadTHIDActivity makeFile catch 1");

            e.printStackTrace();
        } catch (IOException e) {
            Tool.setTitle("ReadTHIDActivity makeFile catch 2");

            e.printStackTrace();
        }
    }
}
