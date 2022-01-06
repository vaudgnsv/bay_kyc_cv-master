package com.thaivan.bay.branch;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class KeyinActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txt_uuid;
    private String uuidRef = "";
    private int max_lc = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyin);
        setCustomToolbar();
        txt_uuid = findViewById(R.id.txt_uuid);
        TextView txt_pin1 = findViewById(R.id.txt_pin1);
        TextView txt_pin2 = findViewById(R.id.txt_pin2);
        TextView txt_pin3 = findViewById(R.id.txt_pin3);
        TextView txt_pin4 = findViewById(R.id.txt_pin4);
        TextView txt_pin5 = findViewById(R.id.txt_pin5);
        TextView txt_pin6 = findViewById(R.id.txt_pin6);
        TextView txt_pin7 = findViewById(R.id.txt_pin7);
        TextView txt_pin8 = findViewById(R.id.txt_pin8);
        TextView txt_pin9 = findViewById(R.id.txt_pin9);
        TextView txt_pin0 = findViewById(R.id.txt_pin0);
        ImageView img_del = findViewById(R.id.img_del);

        Button btn_cancel = findViewById(R.id.cancelBtn);
        Button btn_ok = findViewById(R.id.okBtn);

        txt_pin1.setOnClickListener(this);
        txt_pin2.setOnClickListener(this);
        txt_pin3.setOnClickListener(this);
        txt_pin4.setOnClickListener(this);
        txt_pin5.setOnClickListener(this);
        txt_pin6.setOnClickListener(this);
        txt_pin7.setOnClickListener(this);
        txt_pin8.setOnClickListener(this);
        txt_pin9.setOnClickListener(this);
        txt_pin0.setOnClickListener(this);
        img_del.setOnClickListener(this);

        btn_cancel.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_pin1 :
                setUuid(uuidRef, 1, max_lc);
                break;
            case R.id.txt_pin2 :
                setUuid(uuidRef, 2, max_lc);
                break;
            case R.id.txt_pin3 :
                setUuid(uuidRef, 3, max_lc);
                break;
            case R.id.txt_pin4 :
                setUuid(uuidRef, 4, max_lc);
                break;
            case R.id.txt_pin5 :
                setUuid(uuidRef, 5, max_lc);
                break;
            case R.id.txt_pin6 :
                setUuid(uuidRef, 6, max_lc);
                break;
            case R.id.txt_pin7 :
                setUuid(uuidRef, 7, max_lc);
                break;
            case R.id.txt_pin8 :
                setUuid(uuidRef, 8, max_lc);
                break;
            case R.id.txt_pin9 :
                setUuid(uuidRef, 9, max_lc);
                break;
            case R.id.txt_pin0 :
                setUuid(uuidRef, 0, max_lc);
                break;
            case R.id.img_del :
                delete();
                break;
            case R.id.cancelBtn :
                ScanQrActivity.flag = 1;
                Intent intent = new Intent(KeyinActivity.this, ScanQrActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                break;
            case R.id.okBtn:
                confirm();
                break;

        }
    }

    private void delete() {
        if(uuidRef.length() != 0)
            uuidRef = uuidRef.substring(0, uuidRef.length()-1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt_uuid.setText(uuidRef);
            }
        });
    }

    private void confirm() {
        //check pw
        if(uuidRef.length() == max_lc){
            Intent intent = new Intent(KeyinActivity.this, ReadTHIDActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("uuid", uuidRef);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }else{
            Toast.makeText(KeyinActivity.this, "Incorrect Length", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUuid(String str_pw, int i, int int_lc) {
        if(str_pw.length() < int_lc)
            uuidRef += i;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt_uuid.setText(uuidRef);
            }
        });
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

        mTitle.setText("กรอกรหัสอ้างอิง");
    }

    public void back_menu(View view) {
        ScanQrActivity.flag = 1;
        Intent intent = new Intent(KeyinActivity.this, ScanQrActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}