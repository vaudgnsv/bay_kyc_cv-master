package com.thaivan.bay.branch.blink;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.thaivan.bay.branch.CardManager;
import com.thaivan.bay.branch.FailActivity;
import com.thaivan.bay.branch.IDresultActivity;
import com.thaivan.bay.branch.MainActivity;
import com.thaivan.bay.branch.PDPAActivity;
import com.thaivan.bay.branch.Preference;
import com.thaivan.bay.branch.R;
import com.thaivan.bay.branch.ReadTHIDActivity;
import com.thaivan.bay.branch.RtnActivity;
import com.thaivan.bay.branch.ScanQrActivity;
import com.thaivan.bay.branch.SuccessActivity;
import com.thaivan.bay.branch.THID_info;
import com.thaivan.bay.branch.TNCActivity;
import com.thaivan.bay.branch.Utility;
import com.thaivan.bay.branch.apimanager.ApiInterface;
import com.thaivan.bay.branch.apimanager.RetrofitClientInstance;
import com.thaivan.bay.branch.customerData.ModelCitizenId;
import com.thaivan.bay.branch.customerData.ModelCitizenIdResponse;
import com.thaivan.bay.branch.util.AES256Util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RealTimeFaceDetectionActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_PERMISSION = 101;
    public static final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private TextureView tv;
    private ImageView iv;
    private String type;
    private String chipNo;
    private String uuidRef;
    private String consents;
    private boolean flag_pic = false;
    private ModelCitizenIdResponse modelCitizenIdResponse;
    Handler timer;
    private int retry_cnt = 0;
    private Dialog dialogLoading;
    private static final String TAG = "RealTimeFaceDetectionActivity";
    private AES256Util AES256 = new AES256Util();
    public static CameraX.LensFacing lens = CameraX.LensFacing.FRONT;
    private boolean camera_front = true;

    private ImageAnalysis imageAnalysis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_face_detection);
        timer = new Handler(); //Handler 생성
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
                if(!flag_pic){
                    flag_pic = true;
                    imageAnalysis.removeAnalyzer();
                    savePicture();
                    Intent intent = new Intent(RealTimeFaceDetectionActivity.this, IDresultActivity.class);
                    intent.putExtra("type" , type);
                    intent.putExtra("chipNo" , chipNo);
                    intent.putExtra("uuid" , uuidRef);
                    intent.putExtra("fr" , "Y");
                    intent.putExtra("consents" , consents);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
        });

        tv = findViewById(R.id.face_texture_view);
        iv = findViewById(R.id.face_image_view);
        if (allPermissionsGranted()) {
            tv.post(this::startCamera);
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION);
        }
    }

    @SuppressLint("RestrictedApi")
    private void startCamera() {
        initCamera();
    }

    private void initCamera() {
        CameraX.unbindAll();
        PreviewConfig pc = new PreviewConfig
                .Builder()
                .setTargetResolution(new Size(tv.getWidth(), tv.getHeight()))
                .setLensFacing(lens)
                .build();

        Preview preview = new Preview(pc);
        preview.setOnPreviewOutputUpdateListener(output -> {
            ViewGroup vg = (ViewGroup) tv.getParent();
            vg.removeView(tv);
            vg.addView(tv, 0);
            tv.setSurfaceTexture(output.getSurfaceTexture());
        });

        ImageAnalysisConfig iac = new ImageAnalysisConfig
                .Builder()
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .setTargetResolution(new Size(tv.getWidth(), tv.getHeight()))
                .setLensFacing(lens)
                .build();

        imageAnalysis = new ImageAnalysis(iac);
        imageAnalysis.setAnalyzer(Runnable::run,
                new MLKitFacesAnalyzer(tv, iv, lens, new MLKitFacesAnalyzer.CameraListener() {
                    @Override
                    public void onTakePicture() {
                        if(!flag_pic) {
                            flag_pic = true;
                            imageAnalysis.removeAnalyzer();
                            savePicture();
                            Intent intent = new Intent(RealTimeFaceDetectionActivity.this, IDresultActivity.class);
                            intent.putExtra("type" , type);
                            intent.putExtra("chipNo" , chipNo);
                            intent.putExtra("uuid" , uuidRef);
                            intent.putExtra("fr" , "Y");
                            intent.putExtra("consents" , consents);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    }
                }));
        CameraX.bindToLifecycle(this, preview, imageAnalysis);
    }

    private void savePicture() {
        Bitmap CapImg = Bitmap.createBitmap(tv.getBitmap(), 120, 200, 480, 640);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CapImg.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        byte[] aaa = android.util.Base64.encode(byteArray, android.util.Base64.NO_WRAP);
        makeFile(new String(aaa), "pic_photo");

        String FilePath = "/storage/emulated/0/Pictures/";
        File file = new File(FilePath + "pic_photo.bmp");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            CapImg.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeFile(String data, String fileName) {
        String str = data;

        File saveFile = new File("/sdcard/oversea_ct/bay_branch"); // 저장 경로

        if(!saveFile.exists()){ // 폴더 없을 경우
            saveFile.mkdir(); // 폴더 생성
        }
        try {
            File existFile = new File("/sdcard/oversea_ct/bay_branch/" + fileName + ".txt");
            existFile.delete();

            BufferedWriter buf = new BufferedWriter(new FileWriter(saveFile+"/"+fileName+".txt", true));
            buf.append(str); // 파일 쓰기
            buf.newLine(); // 개행
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (allPermissionsGranted()) {
                tv.post(this::startCamera);
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
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
        TextView toolbar_btn2 = toolbar.findViewById(R.id.toolbar_btn2);
        toolbar_btn2.setVisibility(View.VISIBLE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");
            chipNo = bundle.getString("chipNo") == null ? "" : bundle.getString("chipNo");
            uuidRef = bundle.getString("uuid") == null ? "" : bundle.getString("uuid");
            consents = bundle.getString("consents") == null ? "" : bundle.getString("consents");
        }

        mTitle.setText("กรุณาสแกนใบหน้า");
    }

    public void back_menu(View view) {
        finish();
    }

    public void rotate(View view) {
        if(camera_front){
            camera_front = false;
            lens = CameraX.LensFacing.BACK;
            initCamera();
        }else{
            camera_front = true;
            lens = CameraX.LensFacing.FRONT;
            initCamera();
        }
    }
}