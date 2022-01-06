package com.thaivan.bay.branch.cv;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.thaivan.bay.branch.R;
import com.thaivan.bay.branch.ReadTHIDActivity;
import com.thaivan.bay.branch.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Dopa_PDPAActivity extends AppCompatActivity {
    private String consents = "";

    private CheckBox check_btn1;
    private CheckBox check_btn2;
    private CheckBox check_btn3;
    private int consent_size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_p_a);
        Tool.setTitle("Dopa_PDPAActivity onCreate");

        setCustomToolbar();
        setConsents();

        Button btn_ok = findViewById(R.id.btn_ok);
        Button button_cancel = findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tool.setTitle("Dopa_PDPAActivity button_cancel");
                finish();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tool.setTitle("Dopa_PDPAActivity btn_ok");

                switch (consent_size){
                case 1:
                    Tool.setTitle("Dopa_PDPAActivity btn_ok case 1");

                    if(!check_btn1.isChecked()) {
                        Tool.setTitle("Dopa_PDPAActivity btn_ok case 1 if");

                        Toast.makeText(Dopa_PDPAActivity.this, "หากคุณไม่ให้ความยินยอมอาจส่งผลให้ กลุ่มกรุงศรีไม่สามารถให้บริการที่จำเป็นต้องใช้ข้อมูลที่เกี่ยวข้องได้", Toast.LENGTH_SHORT).show();
                    }else {
                        Tool.setTitle("Dopa_PDPAActivity btn_ok case 1 else");

                        Intent intent = new Intent(Dopa_PDPAActivity.this, ReadTHIDActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("uuid", "dopa");
                        intent.putExtra("consents", consents);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        finish();
                    }
                    break;
                    case 2:
                        Tool.setTitle("Dopa_PDPAActivity btn_ok case 2");

                        if(!check_btn1.isChecked()) {
                            Tool.setTitle("Dopa_PDPAActivity btn_ok case 2 if");

                            Toast.makeText(Dopa_PDPAActivity.this, "หากคุณไม่ให้ความยินยอมอาจส่งผลให้ กลุ่มกรุงศรีไม่สามารถให้บริการที่จำเป็นต้องใช้ข้อมูลที่เกี่ยวข้องได้", Toast.LENGTH_SHORT).show();
                        }else if(!check_btn2.isChecked()) {
                            Tool.setTitle("Dopa_PDPAActivity btn_ok case 2 else if");

                            Toast.makeText(Dopa_PDPAActivity.this, "หากคุณไม่ให้ความยินยอมอาจส่งผลให้ กลุ่มกรุงศรีไม่สามารถให้บริการที่จำเป็นต้องใช้ข้อมูลที่เกี่ยวข้องได้", Toast.LENGTH_SHORT).show();
                        }else {
                            Tool.setTitle("Dopa_PDPAActivity btn_ok case 2 else");

                            Intent intent = new Intent(Dopa_PDPAActivity.this, ReadTHIDActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("uuid", "dopa");
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                        break;
                    case 3:
                        Tool.setTitle("Dopa_PDPAActivity btn_ok case 3");

                        if(!check_btn1.isChecked()) {
                            Tool.setTitle("Dopa_PDPAActivity btn_ok case 3 if check_btn1");

                            Toast.makeText(Dopa_PDPAActivity.this, "หากคุณไม่ให้ความยินยอมอาจส่งผลให้ กลุ่มกรุงศรีไม่สามารถให้บริการที่จำเป็นต้องใช้ข้อมูลที่เกี่ยวข้องได้", Toast.LENGTH_SHORT).show();
                        }else if(!check_btn2.isChecked()) {
                            Tool.setTitle("Dopa_PDPAActivity btn_ok case 3 if check_btn2");

                            Toast.makeText(Dopa_PDPAActivity.this, "หากคุณไม่ให้ความยินยอมอาจส่งผลให้ กลุ่มกรุงศรีไม่สามารถให้บริการที่จำเป็นต้องใช้ข้อมูลที่เกี่ยวข้องได้", Toast.LENGTH_SHORT).show();
                        }else if(!check_btn3.isChecked()) {
                            Tool.setTitle("Dopa_PDPAActivity btn_ok case 3 if check_btn3");

                            Toast.makeText(Dopa_PDPAActivity.this, "หากคุณไม่ให้ความยินยอมอาจส่งผลให้ กลุ่มกรุงศรีไม่สามารถให้บริการที่จำเป็นต้องใช้ข้อมูลที่เกี่ยวข้องได้", Toast.LENGTH_SHORT).show();
                        }else {
                            Tool.setTitle("Dopa_PDPAActivity btn_ok case 3 else");

                            Intent intent = new Intent(Dopa_PDPAActivity.this, ReadTHIDActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("uuid", "dopa");
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                        break;
                }
            }
        });
    }

    private void setConsents() {
        Tool.setTitle("Dopa_PDPAActivity setConsents");

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
            Tool.setTitle("Dopa_PDPAActivity setConsents try");

            resp_data = new JSONArray(consents);
            consent_size = resp_data.length();
            if(consent_size == 1){
                Tool.setTitle("Dopa_PDPAActivity setConsents try 1");

                JSONObject jsonObject = new JSONObject(resp_data.get(0).toString());
                String consents_th1 = jsonObject.getString("contentTh");
                txt_consent1_th.setText(consents_th1);
                txt_consent2_th.setVisibility(View.GONE);
                txt_consent3_th.setVisibility(View.GONE);
                check_btn2.setVisibility(View.GONE);
                check_btn3.setVisibility(View.GONE);
            }else if(consent_size == 2){
                Tool.setTitle("Dopa_PDPAActivity setConsents try 2");

                JSONObject jsonObject = new JSONObject(resp_data.get(0).toString());
                JSONObject jsonObject2 = new JSONObject(resp_data.get(1).toString());
                String consents_th1 = jsonObject.getString("contentTh");
                String consents_th2 = jsonObject2.getString("contentTh");
                txt_consent1_th.setText(consents_th1);
                txt_consent2_th.setText(consents_th2);
                txt_consent3_th.setVisibility(View.GONE);
                check_btn3.setVisibility(View.GONE);
            }else{
                Tool.setTitle("Dopa_PDPAActivity setConsents try else");

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
            Tool.setTitle("Dopa_PDPAActivity setConsents try catch");

            e.printStackTrace();
        }

    }

    void setCustomToolbar() {
        Tool.setTitle("Dopa_PDPAActivity setCustomToolbar");

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
            consents = bundle.getString("consents") == null ? "" : bundle.getString("consents");
            mTitle.setText(R.string.pdpa_title);
        }
    }


    public void back_menu(View view) {
        Tool.setTitle("Dopa_PDPAActivity back_menu");

        Intent intent = new Intent(Dopa_PDPAActivity.this, DopaActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Tool.setTitle("Dopa_PDPAActivity onBackPressed");

    }

    @Override
    protected void onResume() {
        Tool.setTitle("Dopa_PDPAActivity onResume");

        super.onResume();
        check_btn1.setChecked(false);
        check_btn2.setChecked(false);
        check_btn3.setChecked(false);
    }
}