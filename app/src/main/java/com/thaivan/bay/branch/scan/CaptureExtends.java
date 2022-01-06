package com.thaivan.bay.branch.scan;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.thaivan.bay.branch.KeyinActivity;
import com.thaivan.bay.branch.R;
import com.thaivan.bay.branch.ScanQrActivity;


public class CaptureExtends extends AppCompatActivity implements DecoratedBarcodeView.TorchListener{

    public static String type;
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(type.equals("QR"))
            setContentView(R.layout.activity_custom_scanner);
//        else
//            setContentView(R.layout.activity_custom_camera);
        setCustomToolbar();
        barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    void setCustomToolbar() {
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.tool_bar));

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/krungsri_con_med.ttf");
        mTitle.setTypeface(typeface);
        mTitle.setText("สแกน QR code");
    }

    public void back_menu(View view) {
        finish();
    }

    public void rotate(View view) {
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

    public void qr_keyin(View view) {
        Intent resultIntent = new Intent();
        setResult(5, resultIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }


    @Override
    public void onTorchOn() {

    }

    @Override
    public void onTorchOff() {

    }
}
