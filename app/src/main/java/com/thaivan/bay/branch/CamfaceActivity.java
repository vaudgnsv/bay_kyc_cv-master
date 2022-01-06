package com.thaivan.bay.branch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.thaivan.bay.branch.blink.Preview;
import com.thaivan.bay.branch.scan.FaceCameraPreview;


public class CamfaceActivity extends AppCompatActivity {
    LayoutInflater controlInflater = null;

    private FaceCameraPreview surfaceView;
    private int flash_mode = 0;
    public static int flag = 0;
    private static final int K9_FLASH_OFF = 0;
    private static final int K9_FLASH_ON = 1;
    private static final int K9_FLASH_AUTO = 2;
    private TextView bt_flash;

    private ProgressDialog mLoading;
    private int comm_flag = 0;
    private String type = "";


    private int mFrameOrientation;
    private static final int ORIENTATION_PORTRAIT = 1;
    private static final int ORIENTATION_PORTRAIT_UPSIDE_DOWN = 2;
    private static final int ORIENTATION_LANDSCAPE_RIGHT = 3;
    private static final int ORIENTATION_LANDSCAPE_LEFT = 4;
    public static final String EXTRA_GUIDE_COLOR = "io.card.payment.guideColor";
    public static final String EXTRA_SCAN_INSTRUCTIONS = "io.card.payment.scanInstructions";
    public static final String EXTRA_SCAN_OVERLAY_LAYOUT_ID = "io.card.payment.scanOverlayLayoutId";

    private RelativeLayout mMainLayout;
    private LinearLayout customOverlayLayout;
    private static TextView textView;
    private static final int FRAME_ID = 1;
    private static final int UIBAR_ID = 2;
    private static final float UIBAR_VERTICAL_MARGIN_DP = 15.0f;
    Preview mPreview;
    private RelativeLayout mUIBar;
    static AppCompatActivity thisActivity = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        setContentView(R.layout.activity_custom_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setCustomToolbar();

        mLoading = new ProgressDialog(this);
        mLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mLoading.setCanceledOnTouchOutside(false);
        mLoading.setMessage("Cropping...");

//        surfaceView = findViewById(R.id.main);
//        SurfaceHolder holder = surfaceView.getHolder();
//        holder.addCallback(surfaceView);
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mFrameOrientation = ORIENTATION_PORTRAIT;
//        mCardScanner = new FaceScanner(this, mFrameOrientation);

        TextView bt_exit = findViewById(R.id.toolbar_btn);
        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CamfaceActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        bt_flash = findViewById(R.id.toolbar_btn2);

//        flash_mode = preferences.getInt("flash_mode", 0);
        switch (flash_mode) {
            case K9_FLASH_OFF:
                bt_flash.setBackgroundResource(R.drawable.flash_off);
                break;
            case K9_FLASH_ON:
                bt_flash.setBackgroundResource(R.drawable.flash_on);
                break;
            case K9_FLASH_AUTO:
                bt_flash.setBackgroundResource(R.drawable.flash_auto);
                break;
        }
        bt_flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flash_mode++;
                setFlash();
            }
        });

//        Button btn_ok = findViewById(R.id.btn_ok);
//        Button btn_cancel = findViewById(R.id.btn_cancel);
//        btn_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              finish();
//            }
//        });
//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (flash_mode == K9_FLASH_AUTO)
//                    surfaceView.set_flash_stat("torch");
//                surfaceView.takePhoto();
//                if (flash_mode == K9_FLASH_AUTO)
//                    surfaceView.set_flash_stat("off");
//                mLoading.show();
//                mHandler.sendEmptyMessage(0);
//            }
//        });
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int rtn = surfaceView.is_Done();
            if (rtn == 0) {
                mHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
                mLoading.dismiss();
                Intent intent = new Intent(CamfaceActivity.this, FaceCompActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        }
    };
    protected void onResume(){
        super.onResume();
    }

    public static void showScore(int score)
    {
        Toast.makeText(thisActivity,"blink your eye", Toast.LENGTH_SHORT).show();
    }

    private void setFlash() {
//        if(IsDebug.LOG)
//            Log.d(TAG, "" + flash_mode);
        if (flash_mode >= 3) flash_mode = 0;
        switch (flash_mode) {
            case K9_FLASH_OFF:
                bt_flash.setBackgroundResource(R.drawable.flash_off);
                surfaceView.set_flash_stat("off");
                break;
            case K9_FLASH_ON:
                bt_flash.setBackgroundResource(R.drawable.flash_on);
                surfaceView.set_flash_stat("torch");
                break;
            case K9_FLASH_AUTO:
                bt_flash.setBackgroundResource(R.drawable.flash_auto);
                surfaceView.set_flash_stat("off");
                break;
        }
//        edit.putInt("flash_mode", flash_mode);
//        edit.apply();
    }

    public void switchRotate(View view) {
//        public static final String FEATURE_CAMERA_EXTERNAL = "android.hardware.camera.external";
//        public static final String FEATURE_CAMERA_FLASH = "android.hardware.camera.flash";
//        public static final String FEATURE_CAMERA_FRONT = "android.hardware.camera.front";
        if(ScanQrActivity.flag == 0){
            ScanQrActivity.flag = 1;
            Intent resultIntent = new Intent();
            setResult(3, resultIntent);
            finish();
        }else{
            ScanQrActivity.flag = 0;
            Intent resultIntent = new Intent();
            setResult(4, resultIntent);
            finish();
        }
    }



    @Override
    public void onBackPressed() {

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
            mTitle.setText(R.string.cam_person);
        }
    }
}
