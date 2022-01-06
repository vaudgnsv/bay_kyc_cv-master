package com.thaivan.bay.branch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.thaivan.bay.branch.blink.RealTimeFaceDetectionActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PDPAActivity extends AppCompatActivity {
    private String type = "";
    private String chipNo = "";
    private String consents = "";
    private String uuidRef = "";
    private String faceCompare = "";

    private CheckBox check_btn1;
    private CheckBox check_btn2;
    private CheckBox check_btn3;
    private int consent_size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_p_a);
        setCustomToolbar();
        setConsents();

        Button btn_ok = findViewById(R.id.btn_ok);
        Button button_cancel = findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (consent_size){
                case 1:
                    if(!check_btn1.isChecked()) {
                        Toast.makeText(PDPAActivity.this, "หากคุณไม่ให้ความยินยอมอาจส่งผลให้ กลุ่มกรุงศรีไม่สามารถให้บริการที่จำเป็นต้องใช้ข้อมูลที่เกี่ยวข้องได้", Toast.LENGTH_SHORT).show();
                    }else {
                        if(faceCompare.equals("Y")){
                            ScanQrActivity.flag = 1;
                            Intent intent = new Intent(PDPAActivity.this, RealTimeFaceDetectionActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("type", type);
                            intent.putExtra("chipNo", chipNo);
                            intent.putExtra("uuid", uuidRef);
                            intent.putExtra("consents", consents);
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent = new Intent(PDPAActivity.this, IDresultActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("type", type);
                            intent.putExtra("chipNo", chipNo);
                            intent.putExtra("fr", "N");
                            intent.putExtra("uuid", uuidRef);
                            intent.putExtra("consents", consents);
                            startActivity(intent);
                            finish();
                        }
                    }
                    break;
                    case 2:
                        if(!check_btn1.isChecked()) {
                            Toast.makeText(PDPAActivity.this, "หากคุณไม่ให้ความยินยอมอาจส่งผลให้ กลุ่มกรุงศรีไม่สามารถให้บริการที่จำเป็นต้องใช้ข้อมูลที่เกี่ยวข้องได้", Toast.LENGTH_SHORT).show();
                        }else if(!check_btn2.isChecked()) {
                            Toast.makeText(PDPAActivity.this, "หากคุณไม่ให้ความยินยอมอาจส่งผลให้ กลุ่มกรุงศรีไม่สามารถให้บริการที่จำเป็นต้องใช้ข้อมูลที่เกี่ยวข้องได้", Toast.LENGTH_SHORT).show();
                        }else {
                            if(faceCompare.equals("Y")){
                                ScanQrActivity.flag = 1;
                                Intent intent = new Intent(PDPAActivity.this, RealTimeFaceDetectionActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.putExtra("type", type);
                                intent.putExtra("chipNo", chipNo);
                                intent.putExtra("uuid", uuidRef);
                                intent.putExtra("consents", consents);
                                startActivity(intent);
                                finish();
                            }else{
                                Intent intent = new Intent(PDPAActivity.this, IDresultActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.putExtra("type", type);
                                intent.putExtra("chipNo", chipNo);
                                intent.putExtra("fr", "N");
                                intent.putExtra("uuid", uuidRef);
                                intent.putExtra("consents", consents);
                                startActivity(intent);
                                finish();
                            }
                        }
                        break;
                    case 3:
                        if(!check_btn1.isChecked()) {
                            Toast.makeText(PDPAActivity.this, "หากคุณไม่ให้ความยินยอมอาจส่งผลให้ กลุ่มกรุงศรีไม่สามารถให้บริการที่จำเป็นต้องใช้ข้อมูลที่เกี่ยวข้องได้", Toast.LENGTH_SHORT).show();
                        }else if(!check_btn2.isChecked()) {
                            Toast.makeText(PDPAActivity.this, "หากคุณไม่ให้ความยินยอมอาจส่งผลให้ กลุ่มกรุงศรีไม่สามารถให้บริการที่จำเป็นต้องใช้ข้อมูลที่เกี่ยวข้องได้", Toast.LENGTH_SHORT).show();
                        }else if(!check_btn3.isChecked()) {
                            Toast.makeText(PDPAActivity.this, "หากคุณไม่ให้ความยินยอมอาจส่งผลให้ กลุ่มกรุงศรีไม่สามารถให้บริการที่จำเป็นต้องใช้ข้อมูลที่เกี่ยวข้องได้", Toast.LENGTH_SHORT).show();
                        }else {
                            if(faceCompare.equals("Y")){
                                ScanQrActivity.flag = 1;
                                Intent intent = new Intent(PDPAActivity.this, RealTimeFaceDetectionActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.putExtra("type", type);
                                intent.putExtra("chipNo", chipNo);
                                intent.putExtra("uuid", uuidRef);
                                intent.putExtra("consents", consents);
                                startActivity(intent);
                                finish();
                            }else{
                                Intent intent = new Intent(PDPAActivity.this, IDresultActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.putExtra("type", type);
                                intent.putExtra("chipNo", chipNo);
                                intent.putExtra("fr", "N");
                                intent.putExtra("uuid", uuidRef);
                                intent.putExtra("consents", consents);
                                startActivity(intent);
                                finish();
                            }
                        }
                        break;
                }
            }
        });
    }

    private void setConsents() {
        check_btn1 = findViewById(R.id.check_btn1);
        check_btn2 = findViewById(R.id.check_btn2);
        check_btn3 = findViewById(R.id.check_btn3);

        TextView txt_param1 = findViewById(R.id.txt_param1);
        TextView txt_param2 = findViewById(R.id.txt_param2);
        TextView txt_param3 = findViewById(R.id.txt_param3);
        TextView txt_param4 = findViewById(R.id.txt_param4);
        TextView txt_param5 = findViewById(R.id.txt_param5);
        TextView txt_param5_1 = findViewById(R.id.txt_param5_1);
        TextView txt_param5_2 = findViewById(R.id.txt_param5_2);
        TextView txt_param5_3 = findViewById(R.id.txt_param5_3);
        TextView txt_consent1_th = findViewById(R.id.txt_consent1_th);
        TextView txt_consent2_th = findViewById(R.id.txt_consent2_th);
        TextView txt_consent3_th = findViewById(R.id.txt_consent3_th);

        txt_param1.setText(R.string.pdpa_param1);
        txt_param2.setText(R.string.pdpa_param2);
        txt_param3.setText(R.string.pdpa_param3);
        txt_param4.setText(R.string.pdpa_param4);
        txt_param5.setText(R.string.pdpa_param5);
        txt_param5_1.setText(R.string.pdpa_param5_1);
        txt_param5_2.setText(R.string.pdpa_param5_2);
        txt_param5_3.setText(R.string.pdpa_param5_3);
        JSONArray resp_data = null;
        try {
            resp_data = new JSONArray(consents);
            consent_size = resp_data.length();
            if(consent_size == 1){
                JSONObject jsonObject = new JSONObject(resp_data.get(0).toString());
                String consents_th1 = jsonObject.getString("contentTh");
                txt_consent1_th.setText(consents_th1);
                txt_consent2_th.setVisibility(View.GONE);
                txt_consent3_th.setVisibility(View.GONE);
                check_btn2.setVisibility(View.GONE);
                check_btn3.setVisibility(View.GONE);
            }else if(consent_size == 2){
                JSONObject jsonObject = new JSONObject(resp_data.get(0).toString());
                JSONObject jsonObject2 = new JSONObject(resp_data.get(1).toString());
                String consents_th1 = jsonObject.getString("contentTh");
                String consents_th2 = jsonObject2.getString("contentTh");
                txt_consent1_th.setText(consents_th1);
                txt_consent2_th.setText(consents_th2);
                txt_consent3_th.setVisibility(View.GONE);
                check_btn3.setVisibility(View.GONE);
            }else{
                JSONObject jsonObject = new JSONObject(resp_data.get(0).toString());
                JSONObject jsonObject2 = new JSONObject(resp_data.get(1).toString());
                JSONObject jsonObject3 = new JSONObject(resp_data.get(2).toString());
                String consents_th1 = jsonObject.getString("contentTh");
                String consents_th2 = jsonObject2.getString("contentTh");
                String consents_th3 = jsonObject3.getString("contentTh");
                txt_consent1_th.setText(consents_th1);
                txt_consent2_th.setText(consents_th2);
                txt_consent3_th.setText(consents_th3);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void setCustomToolbar() {
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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");
            chipNo = bundle.getString("chipNo") == null ? "" : bundle.getString("chipNo");
            consents = bundle.getString("consents") == null ? "" : bundle.getString("consents");
            uuidRef = bundle.getString("uuid") == null ? "" : bundle.getString("uuid");
            faceCompare = bundle.getString("fr") == null ? "" : bundle.getString("fr");

            mTitle.setText(R.string.pdpa_title);
        }
    }


    public void back_menu(View view) {
        Intent intent = new Intent(PDPAActivity.this, ReadTHIDActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("type", type);
        intent.putExtra("uuid", uuidRef);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        check_btn1.setChecked(false);
        check_btn2.setChecked(false);
        check_btn3.setChecked(false);
    }
}