package com.thaivan.bay.branch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.thaivan.bay.branch.blink.RealTimeFaceDetectionActivity;

import java.io.File;

public class RtnActivity extends AppCompatActivity {
    private String code = "";
    private String type = "";
    private String Chipno = "";
    private String uuid = "";
    private String consents = "";
    private String data1 = "";
    private String data2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtn);
        setCustomToolbar();
        TextView txt_title = findViewById(R.id.txt_title);
        TextView txt_reason = findViewById(R.id.txt_reason);
        ImageView iv_result = findViewById(R.id.iv_result);
        Button btn_ok = findViewById(R.id.btn_ok);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            code = bundle.getString("code");
            type = bundle.getString("type") == null ? "" : bundle.getString("type");
            Chipno = bundle.getString("chipNo") == null ? "" : bundle.getString("chipNo");
            uuid = bundle.getString("uuid") == null ? "" : bundle.getString("uuid");
            consents = bundle.getString("consents") == null ? "" : bundle.getString("consents");
            data1 = bundle.getString("data1") == null ? "" : bundle.getString("data1");
            data2 = bundle.getString("data2") == null ? "" : bundle.getString("data2");
        }

        String text1 = "";
        String text2 = "";

        switch (code){
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
            case "1301":
            case "1303":
            case "1399":
            case "1401":
            case "1402":
            case "1501":
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจากระบบขัดข้อง";
                text2 = "กรุณาทำรายการใหม่อีกครั้ง";
                break;
            case "1299":
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจากระบบการเชื่อมต่อ DOPA ขัดข้อง";
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
                text1 = "ไม่สามารถทำรายการได้\nเนื่องจากท่านไม่ได้ให้ความยินยอมนโยบายความเป็นส่วนตัว";
                text2 = "กรุณาทำรายการใหม่อีกครั้ง\n(กรุณาให้ความยินยอมนโยบายความเป็นส่วนตัว)";
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
            case "DEBUG":
                text1 = data1;
                text2 = data2;
                break;
            default:
                text1 = "ขออภัย";
                text2 = "ระบบขัดข้องชั่วคราว กรุณาทำรายการใหม่";
                break;
        }
        txt_title.setText(text1);
        txt_reason.setText(text2);
    }

    void setCustomToolbar() {
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blank));
    }

    public void EXIT(View view) {

        switch (code){
            case "9300" :
            case "9301" :
            case "9302" :
                deleteFile();
                Intent intent = new Intent(RtnActivity.this, RealTimeFaceDetectionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("type", type);
                intent.putExtra("chipNo", Chipno);
                intent.putExtra("uuid", uuid);
                intent.putExtra("consents", consents);
                startActivity(intent);
                finish();
                break;
            default:
                deleteFile();
                Intent intent2 = new Intent(RtnActivity.this, MainActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                overridePendingTransition(0, 0);
                finish();
                break;
        }
    }

    private void deleteFile() {
        File existFile = new File("/sdcard/oversea_ct/bay_branch/pic_photo.txt");
        existFile.delete();
    }

    @Override
    public void onBackPressed() {

    }
}
