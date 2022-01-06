package com.thaivan.bay.branch.cv;

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
import com.thaivan.bay.branch.CamfaceActivity;
import com.thaivan.bay.branch.MainActivity;
import com.thaivan.bay.branch.Preference;
import com.thaivan.bay.branch.R;
import com.thaivan.bay.branch.ReadTHIDActivity;
import com.thaivan.bay.branch.RtnActivity;
import com.thaivan.bay.branch.SuccessActivity;
import com.thaivan.bay.branch.THID_info;
import com.thaivan.bay.branch.Tool;
import com.thaivan.bay.branch.Utility;
import com.thaivan.bay.branch.apimanager.ApiInterface;
import com.thaivan.bay.branch.apimanager.RetrofitClientInstance;
import com.thaivan.bay.branch.blink.RealTimeFaceDetectionActivity;
import com.thaivan.bay.branch.customerData.DopaOnly;
import com.thaivan.bay.branch.customerData.DopaOnlyResponse;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Dopa_IDresultActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button_cancel;
    private Button button_send;
    private TextView txt_zipCode;
    private TextView CitizenId_tv;
    private TextView ThaiName_tv;
    private TextView EngName_tv;

    private TextView Birth_tv;
    private TextView Birth2_tv;
    private TextView Address_tv;
    private TextView Address2_tv;
    private TextView CardIssue_tv;
    private TextView CardIssue2_tv;
    private TextView CardExpireDate_tv;
    private TextView CardExpireDate2_tv;
    private ImageView Pic_img;

    private String CitizenId;
    private String ThaiName;
    private String Title_TH;
    private String Title_EN;
    private String EngFirst;
    private String EngLast;
    private String THFirst;
    private String THLast;
    private String Birth;
    private String Address;
//    private String Address2;
    private String CardIssue;
    private String CardExpireDate;
    private String Engname;

    private String Homenumber;
    private String Moo;
    private String Trok;
    private String Soi;
    private String Road;
    private String SubDistrict;
    private String District;
    private String Province;
    private String PostCode;
    private int cnt_postCode =0;
    private List<String> array_PostCode;
    private String Bp1no;
    private String Chipno;
    private String appId;
    private String uuidRef = "";
    private String faceCompare = "";
    private String consents = "";

    protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private Dialog dialogLoading;
    private Dialog mSelect;
    private Dialog dialogZipcode;
    private Dialog dialogZipcode2;
    private EditText edit_zipcode;
    private TextView txt_alert;
    private DopaOnlyResponse dopaOnlyResponse;
    private zipCodeAdapter zipCodeAdapter;
    private RecyclerView recyclerView_zipcode;

    private Spinner zipcodeSpinner;
    private ArrayAdapter adapterType;
    Handler timer;
    private int retry_cnt = 0;

    private AES256Util AES256 = new AES256Util();
    private JSONObject payload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idresult);
        Tool.setTitle("Dopa_IDresultActivity onCreate");

        setCustomToolbar();
        customDialogLoading();
        customDialogZipCode();
        customDialogZipCode2();
        customDialogSelectCamera();
        timer = new Handler(); //Handler 생성

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Chipno = bundle.getString("chipNo");
            uuidRef = bundle.getString("uuid");
            faceCompare = bundle.getString("fr") == null ? "N" : bundle.getString("fr");
            consents = bundle.getString("consents");
        }

        button_cancel = findViewById(R.id.button_cancel);
        button_send = findViewById(R.id.button_send);
        button_cancel.setOnClickListener(this);
        button_send.setOnClickListener(this);

        txt_zipCode = findViewById(R.id.txt_Zipcode);
        CitizenId_tv = findViewById(R.id.txt_citizenid);
        ThaiName_tv = findViewById(R.id.txt_thainame);
        EngName_tv = findViewById(R.id.txt_engname);
        Birth_tv = findViewById(R.id.txt_birth);
        Address_tv = findViewById(R.id.txt_address);
        CardExpireDate_tv = findViewById(R.id.txt_expire);
        Pic_img = findViewById(R.id.img_pic);
        zipcodeSpinner = findViewById(R.id.spinner);

        try {
            Tool.setTitle("Dopa_IDresultActivity onCreate try");

            JSONObject IDresp = new JSONObject(THID_info.getTh_info());
            CitizenId = IDresp.getString("CitizenId");
            CitizenId = CitizenId.substring(0, 1) + " " + CitizenId.substring(1,5) + " " + CitizenId.substring(5,10) + " " + CitizenId.substring(10,12) + " " +CitizenId.substring(12);
            ThaiName = IDresp.getString("ThaiName").replaceAll("(#)+", " ");
            EngFirst = IDresp.getString("EnglishFirstName").replaceAll("(#)+", " ");
            EngLast = IDresp.getString("EnglishLastName").replaceAll("(#)+", " ");
            Engname = IDresp.getString("EnglishName").replaceAll("(#)+", " ");

            THFirst = IDresp.getString("ThaiFirstName").replaceAll("(#)+", " ");
            THLast = IDresp.getString("ThaiLastName").replaceAll("(#)+", " ");
            Title_EN = IDresp.getString("EnglishTitle").replaceAll("(#)+", " ");
            Title_TH = IDresp.getString("ThaiTitle").replaceAll("(#)+", " ");

            Birth = IDresp.getString("BirthDate").replaceAll("(#)+", " ");
            Address = IDresp.getString("Address").replaceAll("(#)+", " ");
            CardIssue = IDresp.getString("CardIssueDate");
            CardExpireDate = IDresp.getString("CardExpireDate");

            Homenumber = IDresp.getString("HomeNumber");
            Moo = IDresp.getString("Moo");
            Trok = IDresp.getString("Trok");
            Soi = IDresp.getString("Soi");
            Road = IDresp.getString("Road");
            SubDistrict = IDresp.getString("SubDistrict");
            District = IDresp.getString("District");
            Province = IDresp.getString("Province");
            Bp1no = IDresp.getString("Bp1no");
            PostCode = "";
            long beforeTime = System.currentTimeMillis();

            JSONArray jsonArray = new JSONArray(Preference.getInstance(getApplicationContext()).getValueString(Preference.POST_CODE));
            JSONObject jsonObject = new JSONObject();
            if(Utility.IsDebug)
                Log.d("Test::","Province : " + Province + ",District : " + District + ", + SubDistrict : " + SubDistrict);

            String pr = Province.replace("จังหวัด",""); // province parsing
            String dis = District.replace("เขต","").replace("อำเภอ","").replace(pr,""); // 방콕인경우(เขต), 방콕이 아닌경우(อำเภอ)
            String subdis = SubDistrict.replace("แขวง","").replace("ตำบล",""); // 방콕인경우(แขวง), 방콕이 아닌경우(ตำบล)

//Test Case : 포스트 코드 3개
//            pr = "กระบี่";
//            dis = "เมือง";
//            subdis = "อ่าวนาง";
            if(Utility.IsDebug)
                Log.d("Test::","pr : " + pr + ", dis : " + dis + ", subdis : " + subdis);

            if (array_PostCode == null) {
                array_PostCode = new ArrayList<>();
            } else {
                array_PostCode.clear();
            }

            for(int i = 0; i<jsonArray.length(); i++) {
                jsonObject = (JSONObject)jsonArray.get(i); // jsonarray의 각 jsonobject
                String[] value = jsonObject.getString("value").split(","); // post_code 비교할 City, District, subDistrict 분리
                if(pr.equals(value[0]) && dis.contains(value[1]) && subdis.equals(value[2])) {
                    array_PostCode.add(value[3]);
                    cnt_postCode++;
                }
            }

            long afterTime = System.currentTimeMillis();
            long secDiffTime = (afterTime - beforeTime);
            if(Utility.IsDebug)
                Log.d("Test::","time:" + secDiffTime); // 수행시간 출력
        } catch (JSONException e) {
            Tool.setTitle("Dopa_IDresultActivity onCreate catch");

            e.printStackTrace();
        }


        CitizenId_tv.setText(CitizenId);
        ThaiName_tv.setText(ThaiName);
        EngName_tv.setText(Engname);
        Birth_tv.setText(dateThai(Birth));
        Address_tv.setText(Address);
        if(CardExpireDate.equals("99999999")){
            Tool.setTitle("Dopa_IDresultActivity onCreate CardExpireDate 1");
            CardExpireDate_tv.setText("LIFELONG");
        } else{
            Tool.setTitle("Dopa_IDresultActivity onCreate CardExpireDate 2");
            CardExpireDate_tv.setText(dateThai(CardExpireDate));
        }
        setPicture(Pic_img, THID_info.getPic());

//Test Case : 포스트 코드 없음
//cnt_postCode = 0;

        if(cnt_postCode == 0){
            Tool.setTitle("Dopa_IDresultActivity onCreate cnt_postCode 0");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogZipcode.show();
                    txt_zipCode.setText(PostCode);
                    zipcodeSpinner.setVisibility(View.GONE);
                }
            });
        }else if(cnt_postCode == 1){
            Tool.setTitle("Dopa_IDresultActivity onCreate cnt_postCode 1");

            PostCode = array_PostCode.get(0);
            txt_zipCode.setText(PostCode);
            zipcodeSpinner.setVisibility(View.GONE);
        }else{
            Tool.setTitle("Dopa_IDresultActivity onCreate cnt_postCode 2");

            PostCode = array_PostCode.get(0); //default
            txt_zipCode.setVisibility(View.GONE);

            zipCodeAdapter = new zipCodeAdapter(this);
            recyclerView_zipcode.setAdapter(zipCodeAdapter);
            zipCodeAdapter.setItem(array_PostCode);
            zipCodeAdapter.notifyDataSetChanged();
            zipCodeAdapter.setOnItemClickListener(new zipCodeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(com.thaivan.bay.branch.Adapter.zipCodeAdapter.ViewHolder holder, View view, int position) {
                    zipcodeSpinner.setSelection(position);
                    PostCode = array_PostCode.get(position);
                    dialogZipcode2.dismiss();
                }
            });
            array_PostCode = zipCodeAdapter.getItem();
            dialogZipcode2.show();

            adapterType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, array_PostCode);
            zipcodeSpinner.setAdapter(adapterType);
            zipcodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (zipcodeSpinner.getSelectedItemPosition() != 0) {
                        Tool.setTitle("Dopa_IDresultActivity onCreate onItemSelected");

                        PostCode = array_PostCode.get(zipcodeSpinner.getSelectedItemPosition());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Tool.setTitle("Dopa_IDresultActivity onCreate onNothingSelected");

                }
            });
        }


    }

    void setCustomToolbar() {
        Tool.setTitle("Dopa_IDresultActivity setCustomToolbar");

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
        mTitle.setText("ยืนยันข้อมูล");
    }

    private void setPicture(final ImageView pic_img, final byte[] pic) {
        Tool.setTitle("Dopa_IDresultActivity setPicture");

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

    private void makeFile(String data, String fileName) {
        Tool.setTitle("Dopa_IDresultActivity makeFile");

        String str = data;

        File saveFile = new File("/sdcard/oversea_ct/bay_branch"); // 저장 경로

        if(!saveFile.exists()){ // 폴더 없을 경우
            saveFile.mkdir(); // 폴더 생성
        }
        try {
            Tool.setTitle("Dopa_IDresultActivity makeFile try");

            File existFile = new File("/sdcard/oversea_ct/bay_branch/" + fileName + ".txt");
            existFile.delete();

            BufferedWriter buf = new BufferedWriter(new FileWriter(saveFile+"/"+fileName+".txt", true));
            buf.append(str); // 파일 쓰기
            buf.newLine(); // 개행
            buf.close();
        } catch (FileNotFoundException e) {
            Tool.setTitle("Dopa_IDresultActivity makeFile catch 1");

            e.printStackTrace();
        } catch (IOException e) {
            Tool.setTitle("Dopa_IDresultActivity makeFile catch 2");

            e.printStackTrace();
        }
    }
    public static String bytesToHex(byte[] bytes) {
        Tool.setTitle("Dopa_IDresultActivity bytesToHex");

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        Tool.setTitle("Dopa_IDresultActivity hexStringToByteArray");

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public String dateThai(String strDate) {
        Tool.setTitle("Dopa_IDresultActivity dateThai");

        String Months[] = {
                "มกราคม", "กุมภาพันธ์ ", "มีนาคม", "เมษายน",

                "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม",

                "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};

        int year = Integer.parseInt(strDate.substring(4, 8));
        int month, day = 0;
        if(strDate.substring(2, 4).equals("00"))
            month = 99;
        else
            month = Integer.parseInt(strDate.substring(2, 4)) - 1;

        if(strDate.substring(0, 2).equals("00"))
            day = 99;
        else
            day = Integer.parseInt(strDate.substring(0, 2)) - 1;

        if(day == 99 && month == 99)
            return String.format("- - %s", year);
        else if(day == 99)
            return String.format("- %s %s", Months[month], year);
        else if(month == 99)
            return String.format("%s - %s", day, year);
        else
            return String.format("%s %s %s", day, Months[month], year);
    }

    public String dateEng(String strDate) {
        Tool.setTitle("Dopa_IDresultActivity dateEng");

        String Months[] = {
                "Jan.", "Feb. ", "Mar.", "Apr.",

                "May.", "Jun.", "Jul.", "Aug.",

                "Sep.", "Oct.", "Nov.", "Dec"};

        int year = Integer.parseInt(strDate.substring(4, 8));
        int month = Integer.parseInt(strDate.substring(2, 4)) - 1;
        int day = Integer.parseInt(strDate.substring(0, 2));

        return String.format("%s %s %s", day, Months[month], year - 543);
    }

    private void customDialogLoading() {
        Tool.setTitle("Dopa_IDresultActivity customDialogLoading");

        dialogLoading = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        View view = dialogLoading.getLayoutInflater().inflate(R.layout.dialog_custom_load_process, null);
        TextView txt_msg = view.findViewById(R.id.txt_msg);
        txt_msg.setText("อยู่ระหว่างประมวลผล");
        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoading.setContentView(view);
        dialogLoading.setCancelable(false);
    }

    private void customDialogZipCode() {
        Tool.setTitle("Dopa_IDresultActivity customDialogZipCode");

        dialogZipcode = new Dialog(this, R.style.ThemeWithBay);
        View view = dialogZipcode.getLayoutInflater().inflate(R.layout.dialog_zipcode, null);
        dialogZipcode.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogZipcode.setContentView(view);
        dialogZipcode.setCancelable(false);
        edit_zipcode = view.findViewById(R.id.edit_zipcode);
        txt_alert = view.findViewById(R.id.txt_alert);
    }

    private void customDialogZipCode2() {
        Tool.setTitle("Dopa_IDresultActivity customDialogZipCode2");

        dialogZipcode2 = new Dialog(this, R.style.ThemeWithBay);
        View view = dialogZipcode2.getLayoutInflater().inflate(R.layout.dialog_zipcode2, null);
        dialogZipcode2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogZipcode2.setContentView(view);
        dialogZipcode2.setCancelable(false);
        recyclerView_zipcode = view.findViewById(R.id.recycler_zipcode);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView_zipcode.setLayoutManager(layoutManager);
    }

    private PublicKey readPublicKeyFromAssets(String file) {
        Tool.setTitle("Dopa_IDresultActivity readPublicKeyFromAssets");

        try {
            Tool.setTitle("Dopa_IDresultActivity readPublicKeyFromAssets try");

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
            Tool.setTitle("Dopa_IDresultActivity readPublicKeyFromAssets catch 1");

            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            Tool.setTitle("Dopa_IDresultActivity readPublicKeyFromAssets catch 2");

            e.printStackTrace();
        } catch (IOException e) {
            Tool.setTitle("Dopa_IDresultActivity readPublicKeyFromAssets catch 3");

            e.printStackTrace();
        }
        return null;
    }

    private void sendDopaOnly_Primary(JSONObject payload) {
        Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Primary");

        DopaOnly dopaOnly = new DopaOnly();

        dopaOnly.tid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_TERMINAL_ID);
        dopaOnly.mid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_MERCHANT_ID);
        dopaOnly.sn = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_SERIAL_NUMBER);
        dopaOnly.segment = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_SEGMENT);
        dopaOnly.payload = payload.toString();

        ApiInterface apiInterface = RetrofitClientInstance.getInstance().getService();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        Call<DopaOnlyResponse> call = apiInterface.checkDopaOnly(headerMap, dopaOnly);
        call.enqueue(new Callback<DopaOnlyResponse>() {
            @Override
            public void onResponse(Call<DopaOnlyResponse> call, final Response<DopaOnlyResponse> response) {
                Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Primary onResponse");

                dopaOnlyResponse = response.body();
                timer.postDelayed(new Runnable(){
                    public void run(){
                        if(response.code() == HttpsURLConnection.HTTP_OK){
                            if(dopaOnlyResponse.statusCode.equals("0000")) {
                                Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Primary onResponse 0000");

                                retry_cnt = 0;
                                if(Utility.IsDebug)
                                    Log.d("Test::", "tostring:" + response.toString());
                                dialogLoading.dismiss();
                                try{
                                    Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Primary onResponse 0000 try");

                                    String mer_name = response.body().merchantName;
                                    Preference.getInstance(Dopa_IDresultActivity.this).setValueString(Preference.KEY_MERCHANTNAME_ID, mer_name);
                                    Date date = new Date();
                                    DateFormat dateFormat2 = new SimpleDateFormat("ddMMyyyy hh:mm");
                                    String str_date = dateFormat2.format(date);
                                    String year = String.valueOf(Integer.parseInt(str_date.substring(4,8))+ 543);
                                    str_date = str_date.substring(0,2)+"/"+ str_date.substring(2,4)+"/"+year.substring(2) + str_date.substring(8);
                                    Gson gson = new Gson();
                                    String data = gson.toJson(response.body().data);
                                    JSONObject dopa_only_data = new JSONObject(data);
                                    dopa_only_data.put("firstName", THFirst);
                                    dopa_only_data.put("lastName", THLast);
                                    dopa_only_data.put("merchant", mer_name);
                                    dopa_only_data.put("diis", dopa_only_data.getString("transactionRef"));
                                    dopa_only_data.put("date", str_date);
                                    Intent intent = new Intent(Dopa_IDresultActivity.this, Dopa_SuccessActivity.class);
                                    intent.putExtra("data" , dopa_only_data.toString());
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }catch (Exception e){
                                    Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Primary onResponse 0000 catch");

                                    e.printStackTrace();
                                    Intent intent = new Intent(Dopa_IDresultActivity.this, Dopa_resultActivity.class);
                                    intent.putExtra("code" , "ER");
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }
                            } else if(dopaOnlyResponse.statusCode.equals("-201")|| dopaOnlyResponse.statusCode.equals("9997") || dopaOnlyResponse.statusCode.equals("9999")) {
                                Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Primary onResponse else if 1");

                                if(retry_cnt >= 2){
                                    Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Primary onResponse else if 1 if");

                                    retry_cnt = 0;
                                    dialogLoading.dismiss();
                                    Intent intent = new Intent(Dopa_IDresultActivity.this, Dopa_resultActivity.class);
                                    intent.putExtra("code" , dopaOnlyResponse.statusCode);
                                    intent.putExtra("reason" , "TIME OUT");
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }else {
                                    Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Primary onResponse else if 1 else");

                                    retry_cnt++;
                                    sendDopaOnly_Primary(payload);
                                }
                            } else if(dopaOnlyResponse.statusCode.equals("9300")|| dopaOnlyResponse.statusCode.equals("9301") || dopaOnlyResponse.statusCode.equals("9302")) {
                                Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Primary onResponse else if 2");

                                Intent intent = new Intent(Dopa_IDresultActivity.this, Dopa_resultActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.putExtra("code" , dopaOnlyResponse.statusCode);
                                intent.putExtra("chipNo", Chipno);
                                intent.putExtra("uuid", uuidRef);
                                intent.putExtra("consents", consents);
                                startActivity(intent);
                                finish();
                            } else{
                                Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Primary onResponse else");

                                retry_cnt = 0;
                                dialogLoading.dismiss();
                                try{
                                    Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Primary onResponse else try");

                                    Intent intent = new Intent(Dopa_IDresultActivity.this, Dopa_resultActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    intent.putExtra("code" , dopaOnlyResponse.statusCode);
                                    intent.putExtra("reason" , dopaOnlyResponse.statusMessage);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }catch (Exception e){
                                    Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Primary onResponse else catch");

                                    e.printStackTrace();
                                    Intent intent = new Intent(Dopa_IDresultActivity.this, Dopa_resultActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    intent.putExtra("code" , dopaOnlyResponse.statusCode);
                                    intent.putExtra("reason" , dopaOnlyResponse.statusMessage);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }
                            }
                        }else{
                            Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Primary onResponse else 2");

                            retry_cnt = 0;
                            dialogLoading.dismiss();

                            Intent intent = new Intent(Dopa_IDresultActivity.this, RtnActivity.class);
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
            public void onFailure(Call<DopaOnlyResponse> call, Throwable t) {
                Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Primary onFailure");

                sendDopaOnly_Secondary(payload);
            }
        });
    }

    private void sendDopaOnly_Secondary(JSONObject payload) {
        Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Secondary");

        DopaOnly dopaOnly = new DopaOnly();

        dopaOnly.tid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_TERMINAL_ID);
        dopaOnly.mid = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_MERCHANT_ID);
        dopaOnly.sn = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_SERIAL_NUMBER);
        dopaOnly.segment = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_SEGMENT);
        dopaOnly.payload = payload.toString();

        ApiInterface apiInterface = RetrofitClientInstance.getInstance().getService2();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        Call<DopaOnlyResponse> call = apiInterface.checkDopaOnly(headerMap, dopaOnly);
        call.enqueue(new Callback<DopaOnlyResponse>() {
            @Override
            public void onResponse(Call<DopaOnlyResponse> call, final Response<DopaOnlyResponse> response) {
                Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Secondary response");

                dopaOnlyResponse = response.body();
                timer.postDelayed(new Runnable(){
                    public void run(){
                        if(response.code() == HttpsURLConnection.HTTP_OK){
                            if(dopaOnlyResponse.statusCode.equals("0000")) {
                                Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Secondary response 0000");

                                retry_cnt = 0;
                                dialogLoading.dismiss();
                                try{
                                    Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Secondary response 0000 try");

                                    String mer_name = response.body().merchantName;
                                    Preference.getInstance(Dopa_IDresultActivity.this).setValueString(Preference.KEY_MERCHANTNAME_ID, mer_name);
                                    Date date = new Date();
                                    DateFormat dateFormat2 = new SimpleDateFormat("ddMMyyyy");
                                    String str_date = dateFormat2.format(date);
                                    JSONObject dopa_only_data = new JSONObject(response.body().data.toString());
                                    dopa_only_data.put("firstName", THFirst);
                                    dopa_only_data.put("lastName", THLast);
                                    dopa_only_data.put("merchant", mer_name);
                                    dopa_only_data.put("diis", dopa_only_data.getString("transactionRef"));
                                    dopa_only_data.put("date", str_date);
                                    Intent intent = new Intent(Dopa_IDresultActivity.this, Dopa_SuccessActivity.class);
                                    intent.putExtra("data" , dopa_only_data.toString());
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }catch (Exception e){
                                    Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Secondary response 0000 catch");

                                    e.printStackTrace();
                                    Intent intent = new Intent(Dopa_IDresultActivity.this, Dopa_resultActivity.class);
                                    intent.putExtra("code" , "ER");
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }
                            } else if(dopaOnlyResponse.statusCode.equals("-201")) {
                                Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Secondary response 0000 else if 1");

                                if(retry_cnt >= 2){
                                    Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Secondary response 0000 else if 1 if");

                                    retry_cnt = 0;
                                    dialogLoading.dismiss();
                                    Intent intent = new Intent(Dopa_IDresultActivity.this, Dopa_resultActivity.class);
                                    intent.putExtra("code" , dopaOnlyResponse.statusCode);
                                    intent.putExtra("reason" , dopaOnlyResponse.statusMessage);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                }else {
                                    Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Secondary response 0000 else if 1 else");

                                    retry_cnt++;
                                    sendDopaOnly_Secondary(payload);
                                }
                            } else if(dopaOnlyResponse.statusCode.equals("9997") || dopaOnlyResponse.statusCode.equals("9999")) {
                                Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Secondary response 0000 else if 2");

                                if(retry_cnt >= 2){
                                    Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Secondary response 0000 else if 2 if");

                                    retry_cnt = 0;
                                    dialogLoading.dismiss();
                                    Intent intent = new Intent(Dopa_IDresultActivity.this, Dopa_resultActivity.class);
                                    intent.putExtra("code" , dopaOnlyResponse.statusCode);
                                    intent.putExtra("reason" , dopaOnlyResponse.statusMessage);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                } {
                                    Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Secondary response 0000 else if 2 else");

                                    retry_cnt++;
                                    sendDopaOnly_Secondary(payload);
                                }
//                            } else if(dopaOnlyResponse.statusCode.equals("9300")|| dopaOnlyResponse.statusCode.equals("9301") || dopaOnlyResponse.statusCode.equals("9302")) {
//                                Intent intent = new Intent(Dopa_IDresultActivity.this, RealTimeFaceDetectionActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                intent.putExtra("chipNo", Chipno);
//                                intent.putExtra("uuid", uuidRef);
//                                intent.putExtra("consents", consents);
//                                startActivity(intent);
//                                finish();
                            } else{
                                Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Secondary response else");

                                dialogLoading.dismiss();
                                Intent intent = new Intent(Dopa_IDresultActivity.this, Dopa_resultActivity.class);
                                intent.putExtra("code" , dopaOnlyResponse.statusCode);
                                intent.putExtra("reason" , dopaOnlyResponse.statusMessage);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                finish();
                            }
                        }
                    }
                }, 1000); //1000은 1초를 의미한다.
            }

            @Override
            public void onFailure(Call<DopaOnlyResponse> call, Throwable t) {
                Tool.setTitle("Dopa_IDresultActivity sendDopaOnly_Secondary onFailure");

                dialogLoading.dismiss();
                Intent intent = new Intent(Dopa_IDresultActivity.this, RtnActivity.class);
                intent.putExtra("code" , "HTTPS");
                intent.putExtra("reason" , "Connection Error");
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private void customDialogSelectCamera() {
        Tool.setTitle("Dopa_IDresultActivity customDialogSelectCamera");

        mSelect = new Dialog(this, R.style.ThemeWithCorners);
        View view = mSelect.getLayoutInflater().inflate(R.layout.dialog_custom_select_camera, null);
        mSelect.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSelect.setContentView(view);
        mSelect.setCancelable(false);
    }

    public void front_camera(View view) {
        Tool.setTitle("Dopa_IDresultActivity front_camera");

        CamfaceActivity.flag = 1;
        Intent intent = new Intent(Dopa_IDresultActivity.this, CamfaceActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void back_camera(View view) {
        Tool.setTitle("Dopa_IDresultActivity back_camera");

        CamfaceActivity.flag = 0;
        Intent intent = new Intent(Dopa_IDresultActivity.this, CamfaceActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
    @Override
    public void onBackPressed() {
        Tool.setTitle("Dopa_IDresultActivity onBackPressed");

    }

    public void back_menu(View view) {
        Tool.setTitle("Dopa_IDresultActivity back_menu");

        if(consents.equals("NONE")){
            Tool.setTitle("Dopa_IDresultActivity back_menu if");

            Intent intent = new Intent(Dopa_IDresultActivity.this, ReadTHIDActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("uuid", "dopa");
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }else{
            Tool.setTitle("Dopa_IDresultActivity back_menu else");

            Intent intent = new Intent(Dopa_IDresultActivity.this, Dopa_PDPAActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("consents", consents);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }

    public static String bcd2Str(byte[] b) {
        Tool.setTitle("Dopa_IDresultActivity bcd2Str");

        if (b==null) {
            return null;
        }
        char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; ++i) {
            sb.append(HEX_DIGITS[((b[i] & 0xF0) >>> 4)]);
            sb.append(HEX_DIGITS[(b[i] & 0xF)]);
        }

        return sb.toString();
    }

    public static String hexToAscii(String hexStr) {
        Tool.setTitle("Dopa_IDresultActivity hexToAscii");

        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static String byteArrayToHex(byte[] a) {
        Tool.setTitle("Dopa_IDresultActivity byteArrayToHex");

        StringBuilder sb = new StringBuilder();
        for(final byte b: a)
            sb.append(String.format("%02x", b&0xff));
        return sb.toString();
    }

    public void zipCode(View view) {
        Tool.setTitle("Dopa_IDresultActivity zipCode");

        PostCode = edit_zipcode.getText().toString();

        if(PostCode.length() == 5){
            Tool.setTitle("Dopa_IDresultActivity zipCode if");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txt_zipCode.setText(PostCode);
                    txt_alert.setVisibility(View.GONE);
                    edit_zipcode.setBackground(getResources().getDrawable(R.drawable.border_gray));
                    dialogZipcode.dismiss();
                }
            });
        }else{
            Tool.setTitle("Dopa_IDresultActivity zipCode else");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    edit_zipcode.setBackground(getResources().getDrawable(R.drawable.border_red));
                    txt_alert.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        Tool.setTitle("Dopa_IDresultActivity onClick");

        switch(v.getId()) {
            case R.id.button_cancel:
                Tool.setTitle("Dopa_IDresultActivity onClick button_cancel");

                Intent intent = new Intent(Dopa_IDresultActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.button_send:
                Tool.setTitle("Dopa_IDresultActivity onClick button_send");

                dialogLoading.show();
                button_send.setClickable(false);
                JSONObject customerData = new JSONObject();
                JSONObject customerData2 = new JSONObject();
                try {
                    Tool.setTitle("Dopa_IDresultActivity onClick button_send try");

                    Date date = new Date();
                    DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
                    String str_date = dateFormat2.format(date);
                    customerData.put("transactionRef", uuidRef);
                    customerData.put("transactionDateTime", str_date);
                    byte[] aaa = Base64.encode(THID_info.getPic(), Base64.NO_WRAP);
                    String bbb = byteArrayToHex(aaa);
                    String ccc = hexToAscii(bbb);

                    String FilePath = "/storage/emulated/0/Pictures/";
                    File file = new File(FilePath + "pic_photo.bmp");
                    if(file.exists()){
                        Tool.setTitle("Dopa_IDresultActivity onClick button_send file exists");

                        String filePath = file.getPath();
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                        ByteArrayOutputStream aa = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, aa);
                        byte[] a = aa.toByteArray();
                        customerData.put("customerSelfieImage", Base64.encodeToString(a, Base64.DEFAULT).replace("\n", "").replace("\\", ""));
                        customerData.put("customerSelfieImageFormat", "JPEG");
                        file.delete();
                    }

                    customerData.put("customerImage", ccc);
                    customerData.put("chipNo", Chipno);
                    customerData.put("bp1No", Bp1no);
                    customerData.put("branchId", "");
                    customerData.put("terminalId", Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_TERMINAL_ID));
                    String citizenid = CitizenId.replace(" ", "");
                    customerData2.put("citizenId", citizenid);
                    customerData2.put("prefixTH", Title_TH);
                    customerData2.put("firstNameTH", THFirst);
                    customerData2.put("lastNameTH", THLast);
                    customerData2.put("prefixEN", Title_EN);
                    customerData2.put("firstNameEN", EngFirst);
                    customerData2.put("lastNameEN", EngLast);
                    String tmp = Integer.parseInt(Birth.substring(4))-543 +"-"+ Birth.substring(2,4) +"-"+ Birth.substring(0,2); //New requirement 2020.01.22 (K.Kacidit)
                    customerData2.put("dateOfBirth", tmp);
                    tmp =Integer.parseInt(CardIssue.substring(4))-543 +"-"+ CardIssue.substring(2,4) +"-"+ CardIssue.substring(0,2); //New requirement 2020.01.22 (K.Kacidit)
                    customerData2.put("citizenIdCardIssueDate", tmp);
                    tmp = Integer.parseInt(CardExpireDate.substring(4))-543 +"-"+ CardExpireDate.substring(2,4) +"-"+ CardExpireDate.substring(0,2); //New requirement 2020.01.22 (K.Kacidit)
                    customerData2.put("citizenIdCardExpireDate", tmp);
                    customerData2.put("fullAddress", Address);
                    customerData2.put("countryCode", "TH");
                    if(!PostCode.equals("")){
                        Tool.setTitle("Dopa_IDresultActivity onClick button_send file exists");
                        customerData2.put("zipCode", PostCode);
                    }

                    customerData2.put("province", Province);
                    customerData2.put("subDistrict", SubDistrict);
                    customerData2.put("district", District);
                    customerData2.put("road", Road);
                    customerData2.put("soi", Soi);
                    customerData2.put("moo", Moo);
                    customerData2.put("addrNo", Homenumber);
                    customerData.putOpt("customerData", customerData2);

                    JSONArray consent_array = new JSONArray(consents);
                    if(consent_array.length() == 1){
                        Tool.setTitle("Dopa_IDresultActivity onClick button_send 1");

                        JSONObject consent1 = new JSONObject(consent_array.get(0).toString());
                        consent1.remove("contentTh");
                        consent1.remove("contentEn");
                        consent1.put("consent", "Y");
                        JSONArray customerConsents = new JSONArray();
                        customerConsents.put(consent1);
                        customerData.putOpt("customerConsents", customerConsents);
                    }else if(consent_array.length() == 2){
                        Tool.setTitle("Dopa_IDresultActivity onClick button_send 2");

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
                        customerData.putOpt("customerConsents", customerConsents);
                    }else if(consent_array.length() == 3){
                        Tool.setTitle("Dopa_IDresultActivity onClick button_send 3");

                        JSONObject consent1 = new JSONObject(consent_array.get(0).toString());
                        JSONObject consent2 = new JSONObject(consent_array.get(1).toString());
                        JSONObject consent3 = new JSONObject(consent_array.get(2).toString());
                        consent1.remove("contentTh");
                        consent1.remove("contentEn");
                        consent1.put("consent", "Y");
                        consent2.remove("contentTh");
                        consent2.remove("contentEn");
                        consent2.put("consent", "Y");
                        consent3.remove("contentTh");
                        consent3.remove("contentEn");
                        consent3.put("consent", "Y");
                        JSONArray customerConsents = new JSONArray();
                        customerConsents.put(consent1);
                        customerConsents.put(consent2);
                        customerConsents.put(consent3);
                        customerData.putOpt("customerConsents", customerConsents);
                    }
                    String secretKey = AES256.generate_secretkey(); //  비밀키 생성

                    PublicKey pubKey;
                    if(Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_JSON_VERSION).equals("SIT"))
                        pubKey = readPublicKeyFromAssets("pem/sit-alt.pub.pem");
                    else if(Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_JSON_VERSION).equals("UAT"))
                        pubKey = readPublicKeyFromAssets("pem/uat-alt.pub.pem");
                    else
                        pubKey = readPublicKeyFromAssets("pem/prd-alt.pub.pem");
                    String payload_data = AES256.strEncode_secret(customerData.toString(), secretKey).replace("\n", ""); // 비밀키로 data AES256 암호화
                    String payload_crc = AES256.encryptRSA(secretKey, pubKey); // 비밀키 RSA 공개키로 암호화
                    payload = new JSONObject();
                    payload.put("data", payload_data);
                    payload.put("crc", payload_crc);
                    sendDopaOnly_Primary(payload);
                } catch (JSONException e) {
                    Tool.setTitle("Dopa_IDresultActivity onClick button_send catch 1");

                    e.printStackTrace();
                } catch (Exception e) {
                    Tool.setTitle("Dopa_IDresultActivity onClick button_send catch 2");

                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        Tool.setTitle("Dopa_IDresultActivity onResume");

        super.onResume();
        button_send.setClickable(true);
    }

}
