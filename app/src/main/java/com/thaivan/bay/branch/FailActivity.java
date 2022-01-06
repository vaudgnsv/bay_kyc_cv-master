package com.thaivan.bay.branch;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class FailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fail);
        setCustomToolbar();

        String code = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            code = bundle.getString("code");
        }

        TextView txt_text1 = findViewById(R.id.txt_text1);
        TextView txt_text2 = findViewById(R.id.txt_text2);
        String text1 = "";
        String text2 = "";

        switch (code){
            case"1302":
                text1 = "ไม่สามารถทำรายการได้ เนื่องจากไม่พบข้อมูลการลงทะเบียน";
                text2 = "กรุณาตรวจสอบและทำรายการใหม่อีกครั้ง";
                break;
            case"2001":
                text1 = "ไม่สามารถทำรายการได้ เนื่องจากไม่พบข้อมูลการลงทะเบียน";
                text2 = "กรุณาทำรายการใหม่บน Mobile Application อีกครั้ง";
                break;
        }
        txt_text1.setText(text1);
        txt_text2.setText(text2);
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
        TextView btn_back = toolbar.findViewById(R.id.toolbar_btn);
        btn_back.setVisibility(View.INVISIBLE);

        mTitle.setText("ไม่สำเร็จ");
    }

    public void Finish(View view) {

        Intent intent = new Intent(FailActivity.this, MainActivity.class);
        startActivity(intent);
        ActivityCompat.finishAffinity(this);
//        overridePendingTransition(0, 0);
//        finish();
    }
}
