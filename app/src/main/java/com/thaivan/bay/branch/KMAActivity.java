package com.thaivan.bay.branch;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;


public class KMAActivity extends AppCompatActivity {
//    private String type;

    private Dialog dialogPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_kma);
        setCustomToolbar();
        dialogPrivacyNotice();
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
        mTitle.setText("ยืนยันตัวตนกับกรุงศรี");
    }

    public void privacy(View view) {
        dialogPrivacy.show();
    }

    public void scan(View view) {
        ScanQrActivity.flag = 1;
        Intent intent = new Intent(KMAActivity.this, ScanQrActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    void dialogPrivacyNotice() {
        dialogPrivacy = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        View view = dialogPrivacy.getLayoutInflater().inflate(R.layout.dialog_private_notice, null);
        dialogPrivacy.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPrivacy.setContentView(view);
        dialogPrivacy.setCancelable(false);
    }

    public void back_menu(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
