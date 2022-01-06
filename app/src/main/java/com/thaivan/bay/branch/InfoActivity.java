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

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setCustomToolbar();
        setWidget();
    }

    private void setWidget() {
        TextView txt_tid = findViewById(R.id.txt_tid);
        TextView txt_mid = findViewById(R.id.txt_mid);
        TextView txt_ip = findViewById(R.id.txt_ip);
        TextView txt_port = findViewById(R.id.txt_port);
        TextView txt_ip2 = findViewById(R.id.txt_ip2);
        TextView txt_port2 = findViewById(R.id.txt_port2);

        txt_tid.setText(Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_TERMINAL_ID));
        txt_mid.setText(Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_MERCHANT_ID));
        txt_ip.setText(Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_IP));
        txt_port.setText(Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_PORT));
        txt_ip2.setText(Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_IP2));
        txt_port2.setText(Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_PORT2));
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

        mTitle.setText("Info");
    }

    public void Finish(View view) {

        Intent intent = new Intent(InfoActivity.this, MainActivity.class);
        startActivity(intent);
        ActivityCompat.finishAffinity(this);
    }
}
