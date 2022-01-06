package com.thaivan.bay.branch;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class TNCActivity extends AppCompatActivity {
    private String type = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_n_c);
        setCustomToolbar();

        Button btn_cancel = findViewById(R.id.btn_cancel);
        Button btn_ok = findViewById(R.id.btn_ok);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanQrActivity.flag = 1;
                Intent intent = new Intent(TNCActivity.this, ScanQrActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("type", type);
                startActivity(intent);
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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");
            mTitle.setText("เงื่อนไขการให้บริการ");
        }
    }


    public void back_menu(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}