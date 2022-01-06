package com.thaivan.bay.branch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.thaivan.bay.branch.scan.BitmapHandler;
import com.thaivan.bay.branch.scan.CameraShow;

import java.io.File;
import java.io.FileOutputStream;

public class AutoPicActivity extends AppCompatActivity {
    CameraShow cs = null;
    LayoutInflater controlInflater = null;
    Typeface typeface = null;
    private int flash_mode = 0;
    private ProgressDialog mLoading;
    public static int flag = 0;
    private String type = "";
    private String appId = "";
    private String FilePath = "/storage/emulated/0/Pictures/";
    private String cnt = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_pic);
        typeface = Typeface.createFromAsset(getAssets(), "font/krungsri_con_med.ttf");
        setCustomToolbar();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");
            if(type.equals("KMA"))
                appId = "1";
            else if(type.equals("Kept"))
                appId = "2";
            else if(type.equals("UChoose"))
                appId = "3";
            else
                appId = "4";
        }

        //System status bar hide
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mLoading = new ProgressDialog(this);
        mLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mLoading.setCanceledOnTouchOutside(false);
        mLoading.setMessage("Cropping...");
        if(cs == null)
            cs = new CameraShow(this, 1, flash_mode);
        setContentView(cs);
        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.control_cam_person, null);
        ViewGroup.LayoutParams lpc = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, lpc);
        final TextView btn_cancel = findViewById(R.id.btn_cancel);
        final TextView btn_ok = findViewById(R.id.btn_ok);
        btn_cancel.setTypeface(typeface);
        btn_ok.setTypeface(typeface);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_cancel.setVisibility(View.INVISIBLE);
                finish();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_ok.setVisibility(View.INVISIBLE);
                cs.CusTakePhoto();
                mLoading.show();
                Bitmap bestImage = BitmapHandler.getBitmap();
                File file = new File(FilePath + "THID_"+cnt +".bmp");
                try {
                    if(Utility.IsDebug)
                        Log.d("AAA", "AAA");
                    FileOutputStream fos = new FileOutputStream(file);
                    bestImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(0);






            }
        });
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int rtn = cs.is_Done();
            if (rtn == 0) {
                mHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
                mLoading.dismiss();
//                Intent id_intent = new Intent(AutoPicActivity.this, CamResultActivity.class);
//                id_intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivityForResult(id_intent, 0);
//                overridePendingTransition(0, 0);
                finish();
            }
        }
    };

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
            mTitle.setTypeface(typeface);
            mTitle.setText(R.string.cam_person);
        }
    }

}