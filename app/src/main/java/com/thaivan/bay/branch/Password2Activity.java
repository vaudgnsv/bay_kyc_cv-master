package com.thaivan.bay.branch;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.thaivan.bay.branch.util.Chiper;

public class Password2Activity extends AppCompatActivity implements View.OnClickListener {

    public static final int SUCCESS = 200;
    public static final int SUCCESS_ADMIN = 300;
    private ImageView img_pin01;
    private ImageView img_pin02;
    private ImageView img_pin03;
    private ImageView img_pin04;
    private ImageView img_pin05;
    private ImageView img_pin06;
    private String pin = "";
    private String admin_pin = "";
    private String block_pin = "";
    private String unblock_pin = "";
    private String password = "";
    private int pw_lc = 6;
    //    private String type = "";

    private SystemManager systemManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        setCustomToolbar();

        systemManager = MainApplication.getSystemManager();
        pin = Preference.getInstance(Password2Activity.this).getValueString(Preference.KEY_ADMIN_PIN);
        admin_pin = "E6D48E209E4A2E8628C246E627029B3BA6799E13C4125074CC880CACF6DC0714A61DCD15D860245DDF181DC0D0969DC0A4943132957E479F32B2C167B8D95FCE";
        block_pin = "B19A0BC7234B38B8D8F03C793E0194EC98345AFD1EACC5B07A1FE6A87B927D8EF336A6E143570DB1406A704EA36214D310DB28F58B10D9EBBC3FB57E53A482AA";
        unblock_pin = "184343A2F7AA4D3007D335762CADA0DC799D79EB13C3ABEC9784544756942AEFD959E777E302A37BC43687D7DB625E2D28CEFA4FB221D2DB208EE5B0542032CB";


        TextView txt_pin1 = findViewById(R.id.txt_pin1);
        TextView txt_pin2 = findViewById(R.id.txt_pin2);
        TextView txt_pin3 = findViewById(R.id.txt_pin3);
        TextView txt_pin4 = findViewById(R.id.txt_pin4);
        TextView txt_pin5 = findViewById(R.id.txt_pin5);
        TextView txt_pin6 = findViewById(R.id.txt_pin6);
        TextView txt_pin7 = findViewById(R.id.txt_pin7);
        TextView txt_pin8 = findViewById(R.id.txt_pin8);
        TextView txt_pin9 = findViewById(R.id.txt_pin9);
        TextView txt_pin0 = findViewById(R.id.txt_pin0);
        ImageView img_del = findViewById(R.id.img_del);

        Button btn_cancel = findViewById(R.id.cancelBtn);
        Button btn_ok = findViewById(R.id.okBtn);

        txt_pin1.setOnClickListener(this);
        txt_pin2.setOnClickListener(this);
        txt_pin3.setOnClickListener(this);
        txt_pin4.setOnClickListener(this);
        txt_pin5.setOnClickListener(this);
        txt_pin6.setOnClickListener(this);
        txt_pin7.setOnClickListener(this);
        txt_pin8.setOnClickListener(this);
        txt_pin9.setOnClickListener(this);
        txt_pin0.setOnClickListener(this);
        img_del.setOnClickListener(this);

        btn_cancel.setOnClickListener(this);
        btn_ok.setOnClickListener(this);

        img_pin01 = findViewById(R.id.img_pin1);
        img_pin02 = findViewById(R.id.img_pin2);
        img_pin03 = findViewById(R.id.img_pin3);
        img_pin04 = findViewById(R.id.img_pin4);
        img_pin05 = findViewById(R.id.img_pin5);
        img_pin06 = findViewById(R.id.img_pin6);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_pin1 :
                setPassword(password, 1, pw_lc);
                break;
            case R.id.txt_pin2 :
                setPassword(password, 2, pw_lc);
                break;
            case R.id.txt_pin3 :
                setPassword(password, 3, pw_lc);
                break;
            case R.id.txt_pin4 :
                setPassword(password, 4, pw_lc);
                break;
            case R.id.txt_pin5 :
                setPassword(password, 5, pw_lc);
                break;
            case R.id.txt_pin6 :
                setPassword(password, 6, pw_lc);
                break;
            case R.id.txt_pin7 :
                setPassword(password, 7, pw_lc);
                break;
            case R.id.txt_pin8 :
                setPassword(password, 8, pw_lc);
                break;
            case R.id.txt_pin9 :
                setPassword(password, 9, pw_lc);
                break;
            case R.id.txt_pin0 :
                setPassword(password, 0, pw_lc);
                break;
            case R.id.img_del :
                delPassword();
                break;
            case R.id.cancelBtn :
                Intent intent = new Intent(Password2Activity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.okBtn:
                check_pin();
                break;

        }
    }

    private void check_pin() {
        //check pw
        if(password.length() == 6){
            if(admin_pin.equals(Chiper.sha512Hash(password))) {
                Intent resultIntent = new Intent(Password2Activity.this, MainActivity.class);
                setResult(SUCCESS_ADMIN, resultIntent);
                finish();
            }else if(block_pin.equals(Chiper.sha512Hash(password))){
                systemManager.setHomeKeyDisabled();
                Toast.makeText(Password2Activity.this, "Complete Block !", Toast.LENGTH_SHORT).show();
                finish();
            }else if(unblock_pin.equals(Chiper.sha512Hash(password))){
                systemManager.setHomeKeyEnabled();
                Toast.makeText(Password2Activity.this, "Complete Unblock !", Toast.LENGTH_SHORT).show();
                finish();
            }else
                Toast.makeText(Password2Activity.this, "รหัสผ่านไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(Password2Activity.this, "กรุณากรอกรหัสผ่าน", Toast.LENGTH_SHORT).show();
        }
    }

    private void delPassword() {
        if(password.length() != 0)
            password = password.substring(0, password.length()-1);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (password.length() == 0) {
                    img_pin01.setBackgroundResource(R.drawable.empty_pin);
                    img_pin02.setBackgroundResource(R.drawable.empty_pin);
                    img_pin03.setBackgroundResource(R.drawable.empty_pin);
                    img_pin04.setBackgroundResource(R.drawable.empty_pin);
                    img_pin05.setBackgroundResource(R.drawable.empty_pin);
                    img_pin06.setBackgroundResource(R.drawable.empty_pin);
                }else if(password.length() == 1) {
                    img_pin01.setBackgroundResource(R.drawable.input_pin);
                    img_pin02.setBackgroundResource(R.drawable.empty_pin);
                    img_pin03.setBackgroundResource(R.drawable.empty_pin);
                    img_pin04.setBackgroundResource(R.drawable.empty_pin);
                    img_pin05.setBackgroundResource(R.drawable.empty_pin);
                    img_pin06.setBackgroundResource(R.drawable.empty_pin);
                }else if(password.length() == 2) {
                    img_pin01.setBackgroundResource(R.drawable.input_pin);
                    img_pin02.setBackgroundResource(R.drawable.input_pin);
                    img_pin03.setBackgroundResource(R.drawable.empty_pin);
                    img_pin04.setBackgroundResource(R.drawable.empty_pin);
                    img_pin05.setBackgroundResource(R.drawable.empty_pin);
                    img_pin06.setBackgroundResource(R.drawable.empty_pin);
                }else if(password.length() == 3) {
                    img_pin01.setBackgroundResource(R.drawable.input_pin);
                    img_pin02.setBackgroundResource(R.drawable.input_pin);
                    img_pin03.setBackgroundResource(R.drawable.input_pin);
                    img_pin04.setBackgroundResource(R.drawable.empty_pin);
                    img_pin05.setBackgroundResource(R.drawable.empty_pin);
                    img_pin06.setBackgroundResource(R.drawable.empty_pin);
                }else if(password.length() == 4) {
                    img_pin01.setBackgroundResource(R.drawable.input_pin);
                    img_pin02.setBackgroundResource(R.drawable.input_pin);
                    img_pin03.setBackgroundResource(R.drawable.input_pin);
                    img_pin04.setBackgroundResource(R.drawable.input_pin);
                    img_pin05.setBackgroundResource(R.drawable.empty_pin);
                    img_pin06.setBackgroundResource(R.drawable.empty_pin);
                }else if(password.length() == 5) {
                    img_pin01.setBackgroundResource(R.drawable.input_pin);
                    img_pin02.setBackgroundResource(R.drawable.input_pin);
                    img_pin03.setBackgroundResource(R.drawable.input_pin);
                    img_pin04.setBackgroundResource(R.drawable.input_pin);
                    img_pin05.setBackgroundResource(R.drawable.input_pin);
                    img_pin06.setBackgroundResource(R.drawable.empty_pin);
                }else if(password.length() == 6) {
                    img_pin01.setBackgroundResource(R.drawable.input_pin);
                    img_pin02.setBackgroundResource(R.drawable.input_pin);
                    img_pin03.setBackgroundResource(R.drawable.input_pin);
                    img_pin04.setBackgroundResource(R.drawable.input_pin);
                    img_pin05.setBackgroundResource(R.drawable.input_pin);
                    img_pin06.setBackgroundResource(R.drawable.input_pin);
                }
            }
        });
    }

    private void setPassword(String str_pw, int i, int int_lc) {
        //add password
        if(str_pw.length() < int_lc)
            password += i;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (password.length() == 0) {
                    img_pin01.setBackgroundResource(R.drawable.empty_pin);
                    img_pin02.setBackgroundResource(R.drawable.empty_pin);
                    img_pin03.setBackgroundResource(R.drawable.empty_pin);
                    img_pin04.setBackgroundResource(R.drawable.empty_pin);
                    img_pin05.setBackgroundResource(R.drawable.empty_pin);
                    img_pin06.setBackgroundResource(R.drawable.empty_pin);
                }else if(password.length() == 1) {
                    img_pin01.setBackgroundResource(R.drawable.input_pin);
                    img_pin02.setBackgroundResource(R.drawable.empty_pin);
                    img_pin03.setBackgroundResource(R.drawable.empty_pin);
                    img_pin04.setBackgroundResource(R.drawable.empty_pin);
                    img_pin05.setBackgroundResource(R.drawable.empty_pin);
                    img_pin06.setBackgroundResource(R.drawable.empty_pin);
                }else if(password.length() == 2) {
                    img_pin01.setBackgroundResource(R.drawable.input_pin);
                    img_pin02.setBackgroundResource(R.drawable.input_pin);
                    img_pin03.setBackgroundResource(R.drawable.empty_pin);
                    img_pin04.setBackgroundResource(R.drawable.empty_pin);
                    img_pin05.setBackgroundResource(R.drawable.empty_pin);
                    img_pin06.setBackgroundResource(R.drawable.empty_pin);
                }else if(password.length() == 3) {
                    img_pin01.setBackgroundResource(R.drawable.input_pin);
                    img_pin02.setBackgroundResource(R.drawable.input_pin);
                    img_pin03.setBackgroundResource(R.drawable.input_pin);
                    img_pin04.setBackgroundResource(R.drawable.empty_pin);
                    img_pin05.setBackgroundResource(R.drawable.empty_pin);
                    img_pin06.setBackgroundResource(R.drawable.empty_pin);
                }else if(password.length() == 4) {
                    img_pin01.setBackgroundResource(R.drawable.input_pin);
                    img_pin02.setBackgroundResource(R.drawable.input_pin);
                    img_pin03.setBackgroundResource(R.drawable.input_pin);
                    img_pin04.setBackgroundResource(R.drawable.input_pin);
                    img_pin05.setBackgroundResource(R.drawable.empty_pin);
                    img_pin06.setBackgroundResource(R.drawable.empty_pin);
                }else if(password.length() == 5) {
                    img_pin01.setBackgroundResource(R.drawable.input_pin);
                    img_pin02.setBackgroundResource(R.drawable.input_pin);
                    img_pin03.setBackgroundResource(R.drawable.input_pin);
                    img_pin04.setBackgroundResource(R.drawable.input_pin);
                    img_pin05.setBackgroundResource(R.drawable.input_pin);
                    img_pin06.setBackgroundResource(R.drawable.empty_pin);
                }else if(password.length() == 6) {
                    img_pin01.setBackgroundResource(R.drawable.input_pin);
                    img_pin02.setBackgroundResource(R.drawable.input_pin);
                    img_pin03.setBackgroundResource(R.drawable.input_pin);
                    img_pin04.setBackgroundResource(R.drawable.input_pin);
                    img_pin05.setBackgroundResource(R.drawable.input_pin);
                    img_pin06.setBackgroundResource(R.drawable.input_pin);
                }
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
        TextView btn_back = toolbar.findViewById(R.id.toolbar_btn);
        btn_back.setVisibility(View.INVISIBLE);

        mTitle.setText("ตั้งค่า (สำหรับเจ้าหน้าที่)");
    }
}