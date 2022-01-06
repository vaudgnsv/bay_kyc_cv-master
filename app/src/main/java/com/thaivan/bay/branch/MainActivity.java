package com.thaivan.bay.branch;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.centerm.system.sdk.aidl.ISystemOperation;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thaivan.bay.branch.cv.DopaActivity;
import com.thaivan.bay.branch.cv.Dopa_resultActivity;
import com.thaivan.bay.branch.util.Chiper;
import com.thaivan.bay.branch.util.CustomProgressDialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int PASSWORD = 0;
    private ISystemOperation mSystemOperation;
    private Dialog dialogSetting = null;
    private Dialog dialogUpdate = null;

    private CountDownTimer timer = null;
    private Timer update_timer;
    private int timer_update;
    private CardManager cardManager;

    //Update
    private CustomProgressDialog customProgressDialog = null;
    private Handler handler = null;
    private int progress_cnt = 0;
    private int progress_max = 100;
    private TextView txt_version;
    private Button btn_later;
    private Button btn_now;

    private Button btn_json;
    private Button btn_thvtms;
    private Button btn_next;
    private Button btn_verify;

    private String APP_VER = BuildConfig.VERSION_NAME;
    private SystemManager systemManager;
    private PowerManager pm;
    private PowerManager.WakeLock sCpuWakeLock;
    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Tool.setTitle("MainActivity onCreate");

        cardManager = MainApplication.getCardManager();
        systemManager = MainApplication.getSystemManager();
        setJsonData();
        initWidget();
        checkHash();
        setCustomToolbar();
        customDialogSettlng();
        setVersion();
        customDialogUpdate();

        CountDownTimer timer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                try {
                    systemManager.setHomeKeyDisabled();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        timer.start();
    }

    private void checkHash(){
        Tool.setTitle("MainActivity checkHash");

      String appPath = null;
      String hash = null;
      int numBytes;

        PackageInfo packageInfo = null;
        try {
            packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature certSignature = packageInfo.signatures[0];
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(certSignature.toByteArray());
            String cert = Base64.encodeToString(md.digest(), Base64.DEFAULT).replace("\n", "");
            if(Utility.IsDebug)
                Log.d("cert", "::: " + cert);
            if(cert.equals("ddYVzEUGCWB0WVyeJn6y7T/CCqI="))
                Toast.makeText(MainActivity.this, "Secutiry checked", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "Secutiry checked Fail\n" + cert, Toast.LENGTH_SHORT).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void check_THVTMS() {
        Tool.setTitle("MainActivity check_THVTMS");

        if (getPackageList("com.thaivan.tms_service")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_thvtms.setVisibility(View.VISIBLE);
                }
            });
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_thvtms.setVisibility(View.GONE);
                }
            });
        }
    }

    private void initWidget() {
        Tool.setTitle("MainActivity initWidget");

        btn_next = findViewById(R.id.btn_next);
        btn_verify = findViewById(R.id.btn_verify);
        txt_version = findViewById(R.id.txt_version);
        btn_next.setOnClickListener(this);
        btn_verify.setOnClickListener(this);

        pm = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            sCpuWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, TAG);
        }
        if (sCpuWakeLock.isHeld() == false)
            sCpuWakeLock.acquire();
    }

    private void setShowLayoutView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    private void setVersion() {
        Tool.setTitle("MainActivity setVersion");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String version;
                try{
                    version = Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_JSON_VERSION);
                } catch (Exception e) {
                    version = "Version Error";
                }
                txt_version.setText("Version "+APP_VER+"."+version);
            }
        });
    }

    public boolean getPackageList(String name) {
        Tool.setTitle("MainActivity getPackageList");

        boolean isExist = false;

        PackageManager pkgMgr = getPackageManager();
        List<ResolveInfo> mApps;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = pkgMgr.queryIntentActivities(mainIntent, 0);

        try {
            for (int i = 0; i < mApps.size(); i++) {
                if (mApps.get(i).activityInfo.toString().contains(name)) {
                    isExist = true;
                    break;
                }
            }
        } catch (Exception e) {
            isExist = false;
        }
        return isExist;
    }

    private void setJsonData() {
        Tool.setTitle("MainActivity setJsonData");

        if(Preference.getInstance(getApplicationContext()).getValueString(Preference.KEY_ADMIN_PIN).equals("")) {
            Preference.getInstance(getApplicationContext()).setValueString(Preference.KEY_ADMIN_PIN, "64FCC6F6BC7A815041B4DB51F00F4BEA8E51C13B27F422DA0A8522C94641C7E483C3F17B28D0A59ADD0C8A44A4E4FC1DD3A9EA48BAD8CF5B707AC0F44A5F3536"); // Json 파일을 못읽어도 admin pw 설정}
        }

        File file = new File("/sdcard/oversea_ct/gtms/print_param.json");
        FileInputStream stream = null;
        try {
            String jString;
            String param = null;
            stream = new FileInputStream(file);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            jString = Charset.defaultCharset().decode(bb).toString();
            try {
                JSONObject jsonObject = new JSONObject(jString);
                JSONObject ObjParam = jsonObject.getJSONObject("param");


                param = ObjParam.getString("ip");
                Preference.getInstance(getApplicationContext()).setValueString(Preference.KEY_IP, param);
                param = ObjParam.getString("port");
                Preference.getInstance(getApplicationContext()).setValueString(Preference.KEY_PORT, param);
                param = ObjParam.getString("ip2");
                Preference.getInstance(getApplicationContext()).setValueString(Preference.KEY_IP2, param);
                param = ObjParam.getString("port2");
                Preference.getInstance(getApplicationContext()).setValueString(Preference.KEY_PORT2, param);
                param = ObjParam.getString("termSeq");
                Preference.getInstance(getApplicationContext()).setValueString(Preference.KEY_SERIAL_NUMBER, param);
                param = ObjParam.getString("tid");
                Preference.getInstance(getApplicationContext()).setValueString(Preference.KEY_TERMINAL_ID, param);
                param = ObjParam.getString("mid");
                Preference.getInstance(getApplicationContext()).setValueString(Preference.KEY_MERCHANT_ID, param);
                param = ObjParam.getString("json_version");
                Preference.getInstance(getApplicationContext()).setValueString(Preference.KEY_JSON_VERSION, param);
                param = ObjParam.getString("segment");
                Preference.getInstance(getApplicationContext()).setValueString(Preference.KEY_SEGMENT, param);
                param = ObjParam.getString("admin_pin");
                Preference.getInstance(getApplicationContext()).setValueString(Preference.KEY_ADMIN_PIN, Chiper.sha512Hash(param));
                param = ObjParam.getString("kovan_cer");
                makeFile(param.replaceAll("@", System.getProperty("line.separator")));
                String tmp;
                param = ObjParam.getString("postCode_1");
                tmp = param.replace("]", ",");
                param = ObjParam.getString("postCode_2");
                tmp += param.replace("[","").replace("]",",");
                param = ObjParam.getString("postCode_3");
                tmp += param.replace("[","").replace("]",",");
                param = ObjParam.getString("postCode_4");
                tmp += param.replace("[","").replace("]",",");
                param = ObjParam.getString("postCode_5");
                tmp += param.replace("[","").replace("]",",");
                param = ObjParam.getString("postCode_6");
                tmp += param.replace("[","").replace("]",",");
                param = ObjParam.getString("postCode_7");
                tmp += param.replace("[","").replace("]",",");
                param = ObjParam.getString("postCode_8");
                tmp += param.replace("[","").replace("]",",");
                param = ObjParam.getString("postCode_9");
                tmp += param.replace("[","").replace("]",",");
                param = ObjParam.getString("postCode_10");
                tmp += param.replace("[","").replace("]",",");
                param = ObjParam.getString("postCode_11");
                tmp += param.replace("[","").replace("]",",");
                param = ObjParam.getString("postCode_12");
                tmp += param.replace("[","");
                Preference.getInstance(getApplicationContext()).setValueString(Preference.POST_CODE, tmp);
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Json format error  :: " + param, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "IOException error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void makeFile(String data) {
        Tool.setTitle("MainActivity makeFile");

        String str = data;

        File saveFile = new File("/data/ct/"); // 저장 경로

        if(!saveFile.exists()){ // 폴더 없을 경우
            saveFile.mkdir(); // 폴더 생성
        }
        try {
            File existFile = new File("/data/ct/kovan_cert.cer");
            existFile.delete();

            BufferedWriter buf = new BufferedWriter(new FileWriter(saveFile+"/kovan_cert.cer", true));
            buf.append(str); // 파일 쓰기
            buf.newLine(); // 개행
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void customDialogSettlng() {
        Tool.setTitle("MainActivity customDialogSettlng");

        dialogSetting = new Dialog(this, R.style.ThemeWithCorners);
        View view = dialogSetting.getLayoutInflater().inflate(R.layout.dialog_custom_setting, null);
        dialogSetting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSetting.setContentView(view);
        dialogSetting.setCancelable(false);
        btn_json = dialogSetting.findViewById(R.id.btn_json);
        btn_thvtms = dialogSetting.findViewById(R.id.btn_thvtms);
    }

    public void openGTMS(View view) {
        Tool.setTitle("MainActivity openGTMS");

        Intent intent = getPackageManager().getLaunchIntentForPackage("com.centerm.cpay.gtms");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        dialogSetting.dismiss();
    }

    public void openTHV_TMS(View view) {
        Tool.setTitle("MainActivity openTHV_TMS");

        Intent intent = getPackageManager().getLaunchIntentForPackage("com.thaivan.tms_service");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        dialogSetting.dismiss();
    }

    public void openSetting(View view) {
        Tool.setTitle("MainActivity openSetting");

        Intent intent = new Intent(MainActivity.this, Password2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, PASSWORD);
        dialogSetting.dismiss();
    }

    public void openInfo(View view) {
        Tool.setTitle("MainActivity openInfo");

        Intent info = new Intent(MainActivity.this, InfoActivity.class);
        info.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(info);
        dialogSetting.dismiss();
    }

//    public void update(View view) {
////        dialogUpdate.show();
//    }

    public void config(View view) {
        Tool.setTitle("MainActivity config");

//        initdata();
        check_THVTMS();
        Intent intent = new Intent(MainActivity.this, PasswordActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, PASSWORD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tool.setTitle("MainActivity onActivityResult");

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(requestCode == PASSWORD) {
            if (resultCode == PasswordActivity.SUCCESS)
                dialogSetting.show();
            if (resultCode == PasswordActivity.SUCCESS_ADMIN)
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void close(View view) {
        Tool.setTitle("MainActivity close");

        dialogSetting.dismiss();
    }

//    private void setCheckUpdate() {
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                //set Task
//                TimerTask autoUpdate = new TimerTask() {
//                    @Override
//                    public void run() {
//                        cardManager.checkUpdate();
////                        update_timer.cancel();
//                        if(Utility.IsDebug)
//                            Log.d("UPDATE_TASK", "::: DO IT! ");
//
//                    }
//                };
//
//                timer_update = 10000;
//
//                //connect Task
//                update_timer = null;
//                update_timer = new Timer();
//                update_timer.schedule(autoUpdate, timer_update, 120000);
//            }
//        });
//    }
//
//    public void customDialogAlertSuccess() {
//        final Dialog dialogAlert = new Dialog(MainActivity.this, R.style.ThemeWithCorners);
//        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_success, null);
//        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView(view);
//        dialogAlert.setCancelable(false);
//        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
//
//        msgLabel.setText("อัพเดทข้อมูลสำเร็จ");
//
//        try {
//            dialogAlert.show();
//        } catch (Exception e) {
//            dialogAlert.dismiss();
//        }
//
//        CountDownTimer timer = new CountDownTimer(2500, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//            }
//
//            @Override
//            public void onFinish() {
//                dialogAlert.dismiss();
//                cardManager.updateFile();
//            }
//        };
//        timer.start();
//    }
//
//    public void customDialogAlertFail() {
//        final Dialog dialogAlert = new Dialog(MainActivity.this, R.style.ThemeWithCorners);
//        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_fail, null);
//        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView(view);
//        dialogAlert.setCancelable(false);
//        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
//        msgLabel.setText("อัปเดตข้อมูลล้มเหลว");
//
//        try {
//            dialogAlert.show();
//        } catch (Exception e) {
//            dialogAlert.dismiss();
//        }
//
//        CountDownTimer timer = new CountDownTimer(2500, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//            }
//
//            @Override
//            public void onFinish() {
//                dialogAlert.dismiss();
//            }
//        };
//        timer.start();
//    }

    private void customDialogUpdate() {
        Tool.setTitle("MainActivity customDialogUpdate");

        dialogUpdate = new Dialog(this, R.style.ThemeWithCorners);
        View view = dialogUpdate.getLayoutInflater().inflate(R.layout.dialog_custom_update, null);
        dialogUpdate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUpdate.setContentView(view);
        dialogUpdate.setCancelable(false);
        btn_later = dialogUpdate.findViewById(R.id.btn_later);
        btn_now = dialogUpdate.findViewById(R.id.btn_now);


        btn_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tool.setTitle("MainActivity customDialogUpdate onClick 1");

                dialogUpdate.dismiss();
            }
        });

        btn_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tool.setTitle("MainActivity customDialogUpdate onClick 2");

                dialogUpdate.dismiss();
                cardManager.updateFile();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Tool.setTitle("MainActivity onClick");

        switch(v.getId()) {
            case R.id.btn_next:
                Tool.setTitle("MainActivity onClick btn_next");

                Preference.getInstance(getApplicationContext()).setValueString(Preference.TEMP_POST_CODE, ""); //Init
                Intent intent = new Intent(MainActivity.this, KMAActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.btn_verify:
                Tool.setTitle("MainActivity onClick btn_verify");

                Preference.getInstance(getApplicationContext()).setValueString(Preference.TEMP_POST_CODE, ""); //Init
                Intent intent_dopa = new Intent(MainActivity.this, DopaActivity.class);
                intent_dopa.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent_dopa);
                break;
            default:
                break;
        }
    }

    void setCustomToolbar() {
        Tool.setTitle("MainActivity setCustomToolbar");

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.bayyellow));

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.custom_toolbar, null);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(mCustomView);
        }
    }

    @Override
    public void onBackPressed() {
        Tool.setTitle("MainActivity onBackPressed");

    }

    public void DEMO_TEST(View view) {
        Tool.setTitle("MainActivity onClick onBackPressed");

        Intent intent_KMA = new Intent(MainActivity.this, KMAActivity.class);
        intent_KMA.putExtra("type", "TEST");
        startActivity(intent_KMA);
    }

    public void UpdateJson(View view) {
        Tool.setTitle("MainActivity UpdateJson");

        dialogSetting.dismiss();
        dialogUpdate.show();
    }
}
