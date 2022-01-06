package com.thaivan.bay.branch;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


public class SuccessActivity extends AppCompatActivity {
    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucess);
        setCustomToolbar();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");
        }
        TextView txt_result = findViewById(R.id.txt_result);
        ImageView imageView = findViewById(R.id.result_icon);

        if(type.equals("KMA")){
            imageView.setImageResource(R.drawable.group_5_copy);
            txt_result.setText("กรุณาเปิดแอปฯ "+type+"\nบนเครื่องของคุณเพื่อดำเนินการต่อ");
        }else if(type.equals("Kept")) {
            imageView.setImageResource(R.drawable.group_5);
            txt_result.setText("กรุณาเปิดแอปฯ "+type+"\nบนเครื่องของคุณเพื่อดำเนินการต่อ");
        }else if(type.equals("UChoose")) {
            imageView.setImageResource(R.drawable.group_2656);
            txt_result.setText("กรุณาเปิดแอปฯ " + type + "\nบนเครื่องของคุณเพื่อดำเนินการต่อ");
        }else{
            imageView.setImageResource(R.drawable.group_2655);
            txt_result.setText("หลักทรัพย์กรุงศรี จะแจ้งผลอนุมัติ\n"+"ผ่านทางอีเมลล์ที่ท่านแจ้งไว้");
        }
    }

    void setCustomToolbar() {
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.tool_bar));
    }

    public void Finish(View view) {
        deleteFile();
        Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
        startActivity(intent);
        ActivityCompat.finishAffinity(this);
//        overridePendingTransition(0, 0);
//        finish();
    }

    private void deleteFile() {
        File existFile = new File("/sdcard/oversea_ct/bay_branch/pic_photo.txt");
        existFile.delete();
    }
}
