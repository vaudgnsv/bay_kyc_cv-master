package com.thaivan.bay.branch.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thaivan.bay.branch.R;

public class CustomProgressDialog extends Dialog {

    public static TextView txt_proceed;
    public static ProgressBar progressBar;

    public CustomProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 지저분한(?) 다이얼 로그 제목을 날림
        setContentView(R.layout.customdialog); // 다이얼로그에 박을 레이아웃

        txt_proceed = findViewById(R.id.txt_proceed);
        progressBar = findViewById(R.id.progressBar);
        }
    }

