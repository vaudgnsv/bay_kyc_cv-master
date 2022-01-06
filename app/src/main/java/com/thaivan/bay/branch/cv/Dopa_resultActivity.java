package com.thaivan.bay.branch.cv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.constant.Constant;
import com.thaivan.bay.branch.CardManager;
import com.thaivan.bay.branch.MainActivity;
import com.thaivan.bay.branch.MainApplication;
import com.thaivan.bay.branch.R;
import com.thaivan.bay.branch.Tool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class Dopa_resultActivity extends AppCompatActivity {
    private String code;
    private TextView mTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dopa_result);
        Tool.setTitle("Dopa_resultActivity onCreate");

        setCustomToolbar();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            code = bundle.getString("code") == null? "" : bundle.getString("code");
        }
        initWidget();
    }

    private void initWidget() {
        Tool.setTitle("Dopa_resultActivity initWidget");

        ImageView img_result = findViewById(R.id.img_result);
        TextView txt_msg = findViewById(R.id.txt_msg);
        TextView txt_submsg = findViewById(R.id.txt_submsg);
        TextView txt_reason = findViewById(R.id.txt_reason);

        String text1 ="";
        String text2 ="";

        Tool.setTitle("Dopa_resultActivity initWidget code = " + code);
        switch (code){
            case "0000":
                text1 = "ตรวจสอบข้อมูลสำเร็จ";
                text2 = "ระบบกำลังพิมพ์ใบบันทึกรายการ";
                break;
            case "1100":
            case "1103":
            case "1600":
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจาก QR Code/Barcode/Reference ไม่ถูกต้อง";
                text2 = "กรุณาทำรายการใหม่อีกครั้ง";
                break;
            case "1102":
            case "1601":
            case "1602":
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจาก QR Code/Barcode/Reference ถูกใช้ไปแล้ว";
                text2 = "กรุณาทำรายการใหม่บน\nMobile Application อีกครั้ง";
                break;
            case "1101":
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจาก QR Code/Barcode/Reference หมดอายุ";
                text2 = "กรุณาทำรายการใหม่บน\nMobile Application อีกครั้ง";
                break;
            case "1201":
            case "1202":
                text1 = "รายการไม่สำเร็จ";
                text2 = "เนื่องจากบัตรประชาชนผิดปกติ";
                break;
            case "1299":
                text1 = "รายการไม่สำเร็จ";
                text2 = "กรุณาขอเอกสารเพิ่มเติม";
                break;
            case "1301":
            case "1303":
            case "1399":
            case "1401":
            case "1402":
            case "1501":
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจากระบบขัดข้อง";
                text2 = "กรุณาทำรายการใหม่อีกครั้ง";
                break;
            case "1302":
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจากไม่พบข้อมูลการลงทะเบียน";
                text2 = "กรุณาตรวจสอบและทำรายการใหม่อีกครั้ง";
                break;
            case "1700":
            case "1799":
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจากระบบการเชื่อมต่อขัดข้อง";
                text2 = "กรุณาทำรายการใหม่อีกครั้ง";
                break;
            case "1899":
            case "9400" :
            case "9998" :
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจากระบบขัดข้อง ";
                text2 = "กรุณาทำรายการใหม่อีกครั้ง";
                break;
            case "9401" :
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจากระบบขัดข้อง";
                text2 = "กรุณาติดต่อเจ้าหน้าที่";
                break;
            case "1900":
                text1 = "รายการไม่สำเร็จ";
                text2 = "กรุณาให้ความยินยอมนโยบายความเป็นส่วนตัว";
                break;
            case "2001":
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจากไม่พบข้อมูลการลงทะเบียน";
                text2 = "กรุณาทำรายการใหม่บน\nMobile Application อีกครั้ง";
                break;
            case "2002":
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจากระบบประมวลผลใบหน้าไม่ถูกต้อง";
                text2 = "กรุณาทำรายการใหม่อีกครั้ง";
                break;
            case "2003":
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจากข้อมูลของท่านไม่ผ่านตามเงื่อนไขที่กำหนด";
                text2 = "กรุณากลับไปยัง\nMobile App/Website [RP]\nเพื่อทำรายการใหม่อีกครั้ง";
                break;
            case "9300" :
            case "9301" :
            case "9302" :
                text1 = "การเปรียบเทียบใบหน้าไม่สำเร็จ";
                text2 = "กรุณาลองใหม่อีกครั้ง";
                break;
            case "9402" :
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจากไม่พบข้อมูลการลงทะเบียนผ่านช่องทางนี้";
                text2 = "กรุณาตรวจสอบช่องทางทำรายการบน\nMobile Application อีกครั้ง";
                break;
            case "9997" :
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจากไม่สามารถติดต่อกับระบบของธนาคารได้\nณ ขณะนี้ ";
                text2 = "กรุณาทำรายการใหม่อีกครั้ง";
                break;
            case "9999" :
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจากไม่สามารถติดต่อกับระบบของธนาคารได้\nณ ขณะนี้";
                text2 = "กรุณาทำรายการใหม่อีกครั้ง";
                break;
            case "THID":
                text1 = "รายการไม่สำเร็จ";
                text2 = "ไม่สามารถอ่านบัตรได้\nกรุณาทำรายการใหม่";
                break;
            case "EXPIRED":
                text1 = "รายการไม่สำเร็จ";
                text2 = "เนื่องจากบัตรประชาชนหมดอายุ\nกรุณาขอเอกสารเพิ่มเติม";
                break;
            default:
                text1 = "รายการไม่สำเร็จ";
                text2 = "เนื่องจากระบบขัดข้อง\nกรุณาทำรายการใหม่";
                break;
        }
        if(code.equals("0000")){
            Tool.setTitle("Dopa_resultActivity initWidget if");

            mTitle.setText(R.string.dopa_success_title);
            txt_msg.setText(text1);
            txt_submsg.setText(text2);
            txt_submsg.setVisibility(View.VISIBLE);
            txt_reason.setVisibility(View.GONE);
            img_result.setBackgroundResource(R.drawable.group_success);
        }else{
            Tool.setTitle("Dopa_resultActivity initWidget else");

            mTitle.setText("");
            txt_msg.setText(text1);
            txt_submsg.setVisibility(View.GONE);
            txt_reason.setVisibility(View.VISIBLE);
            txt_reason.setText(text2);
            img_result.setBackgroundResource(R.drawable.ic_fail);
        }
    }

    void setCustomToolbar() {
        Tool.setTitle("Dopa_resultActivity setCustomToolbar");

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.tool_bar));

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.custom_toolbar, null);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Tool.setTitle("Dopa_resultActivity setCustomToolbar if");

            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(mCustomView);
        }

        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        Toolbar toolbar = mCustomView.findViewById(R.id.toolbar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        TextView btn_back = toolbar.findViewById(R.id.toolbar_btn);
        btn_back.setVisibility(View.INVISIBLE);
    }

    public void Finish(View view) {
        Tool.setTitle("Dopa_resultActivity Finish");

        deleteFile();
        Intent intent = new Intent(Dopa_resultActivity.this, MainActivity.class);
        startActivity(intent);
        ActivityCompat.finishAffinity(this);
//        overridePendingTransition(0, 0);
//        finish();
    }

    private void deleteFile() {
        Tool.setTitle("Dopa_resultActivity deleteFile");

        File existFile = new File("/sdcard/oversea_ct/bay_branch/pic_photo.txt");
        existFile.delete();
    }
}
