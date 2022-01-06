package com.thaivan.bay.branch;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.centerm.centermposoversealib.thailand.AidlIdCardTha;
import com.centerm.centermposoversealib.thailand.AidlIdCardThaListener;
import com.centerm.centermposoversealib.thailand.ThiaIdInfoBeen;
import com.centerm.cpay.securitysuite.aidl.IVirtualPinPad;
import com.centerm.smartpos.aidl.iccard.AidlICCard;
import com.centerm.smartpos.aidl.pboc.AidlCheckCardListener;
import com.centerm.smartpos.aidl.pboc.AidlEMVL2;
import com.centerm.smartpos.aidl.pboc.ParcelableTrackData;
import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.qrscan.AidlQuickScanZbar;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.aidl.sys.AidlSystemSettingService;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.util.CompactUtil;
import com.centerm.smartpos.util.HexUtil;
import com.centerm.system.sdk.aidl.IDeviceService;
import com.centerm.system.sdk.aidl.ISystemOperation;
import com.centerm.system.sdk.aidl.SystemFunctionType;
import com.google.gson.JsonElement;
import com.thaivan.bay.branch.manager.HttpManager;
import com.thaivan.bay.branch.model.CheckJson;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;


public class CardManager {

    public static final String TAG = "CardManager";
    public final static byte[] MASK = new byte[]{(byte) 0x13, (byte) 0x43, (byte) 0x45, (byte) 0x35
            , (byte) 0x56, (byte) 0x23, (byte) 0x15, (byte) 0x35, (byte) 0x56, (byte) 0x58, (byte) 0x43,
            (byte) 0x55, (byte) 0x32, (byte) 0x45, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x35,
            (byte) 0x56, (byte) 0x23, (byte) 0x15, (byte) 0x34, (byte) 0x45, (byte) 0x35, (byte) 0x34,
            (byte) 0x66, (byte) 0x33, (byte) 0x43, (byte) 0x77, (byte) 0x18, (byte) 0x19, (byte) 0x37};



    public static CardManager instance = null;
    private static AidlSystemSettingService settingService;
    private static AidlEMVL2 pboc2;
    private AidlICCard iccard;
    private Context context = null;
    private String JSON_VERSION = "";
    private String _cmd = "00A4040008";
    private String _thai_id_card = "A000000054480001";
    private String _thai_id_card_extend = "A000000084060002";
    private String _req_version = "80b00000020004";
    private final Charset _UTF8_CHARSET = Charset.forName("TIS-620");

    private AidlDeviceManager manager = null;
    private AidlDeviceManager managerTle = null;
    private AidlDeviceManager manager_thid = null;
    private ISystemOperation mSystemOperation = null;
    private AidlIdCardTha aidlIdCardTha;
    private IVirtualPinPad mRemoteService;
    private AidlPrinter printDev;
    private AidlQuickScanZbar aidlQuickScanService = null;

    private IdListener idListener = null;
    private UpdateListner updateLister = null;
    private ChipListener chipListener = null;

    private Dialog dialogWaiting;
    public static String TMS = "CPAY";
    public static boolean updateFlag = false;
    private byte[] photoBytes;
    private boolean readPhotoSuccess = false;

    public static CardManager init(Context context) {
        if (instance == null) {
            instance = new CardManager();
            instance.context = context;
        }

        //  Must enter this few code to make
        //  CompactUtil can call context without null
        if (context != null) {
            //context = instance.getBaseContext();
            CompactUtil.instance(context);
        }

        return instance;
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            manager = null;
            //LogUtil.print(getResources().getString(R.string.bind_service_fail));
            //LogUtil.print("manager = " + manager);
            if(Utility.IsDebug) {
                Log.d(TAG, "bind service failed");
                Log.d(TAG, "manager = " + manager);
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            manager = AidlDeviceManager.Stub.asInterface(service);
            //LogUtil.print(getResources().getString(R.string.bind_service_success));
            if(Utility.IsDebug) {
                Log.d(TAG, "bind service success");
                Log.d(TAG, "mamnager = " + manager);
            }
            if (null != manager) {
                try {
                    pboc2 = AidlEMVL2.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PBOC2));
                    printDev = AidlPrinter.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PRINTERDEV));
                    iccard = AidlICCard.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_ICCARD));
                    settingService = AidlSystemSettingService.Stub.asInterface(manager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_SYS));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public ServiceConnection conn_thid = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            manager_thid = null;
            if(Utility.IsDebug) {
                Log.d(TAG, "bind service failed");
                Log.d(TAG, "manager = " + manager_thid + " name " + name);
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            manager_thid = AidlDeviceManager.Stub.asInterface(service);
            if(Utility.IsDebug) {
                Log.d(TAG, "bind service success");
                Log.d(TAG, "manager = " + manager_thid + " name " + name);
            }
            if (null != manager_thid) {
                loadThid();
            }
        }
    };

    private void loadThid() {
        try {
            aidlIdCardTha = AidlIdCardTha.Stub.asInterface(manager_thid.getDevice(com.centerm.centermposoversealib.constant.Constant.OVERSEA_DEVICE_CODE.OVERSEA_DEVICE_TYPE_THAILAND_ID));
            THID_info.setAidlIdCardTha(aidlIdCardTha);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection mRemoteConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemoteService = (IVirtualPinPad) IVirtualPinPad.Stub.asInterface(service);
           /* try {
                mRemoteService.loadMainKey("4A9509351367EA729CCC9B25ED2B4C8C",null);
                mRemoteService.loadWorkKey(0x01, "7795B2437869E837F6E69F066AF667F4", null);
                mRemoteService.loadWorkKey(0x02,"9B725F2483ADE643E97ED1104D7FF166",null);
                mRemoteService.loadWorkKey(0x03,"BE628C34DD27F447D583E796DACDF443",null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/
        }
    };



    public void bindService() {
        Intent intent = new Intent();
        intent.setPackage("com.centerm.smartposservice");
        intent.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);

        Intent intent_thid = new Intent();
        intent_thid.setPackage("com.centerm.centermposoverseaservice");
        intent_thid.setAction("com.centerm.CentermPosOverseaService.MANAGER_SERVICE");
        context.bindService(intent_thid, conn_thid, Context.BIND_AUTO_CREATE);

        Intent intentCpay = new Intent();
        intentCpay.setAction("com.centerm.cpay.securitysuite.AIDL_SERVICE");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intentCpay.setPackage("com.centerm.cpay.securitysuite");
        }
        context.bindService(intentCpay, mRemoteConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService() throws RemoteException {
        pboc2.cancelCheckCard();
        context.unbindService(conn);
        context.unbindService(conn_thid);
        context.unbindService(mRemoteConnection);
    }

    public void setIdListener(IdListener idListener) {
        this.idListener = idListener;
    }
    public void setUpdateLister(UpdateListner updateLister) {
        this.updateLister = updateLister;
    }
    public void setChipListener(ChipListener chipListener) {
        this.chipListener = chipListener;
    }

    public void read_THID() {
        try {
            THID_info.setReading_start_time(System.currentTimeMillis());
            aidlIdCardTha.searchIDCard(60000, new AidlIdCardThaListener.Stub() {
                @Override
                public void onFindIDCard(final ThiaIdInfoBeen been){
                    /**
                    //Version : V34
                    THID_info.setTh_info(been.toJSONString());
                    THID_info.setReading_end_time(System.currentTimeMillis());
                    THID_info.setPic(been.getPhoto());
                    THID_info.setisReading(false);
                    idListener.onFindID();
                     */
                    readInfo();
                    sendCommandForPhoto();
                    if (readPhotoSuccess) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
                        THID_info.setPic(bmp);
                        idListener.onFindID();
                    }else
                        idListener.onFail();
                }

                @Override
                public void onTimeout() throws RemoteException {
                    idListener.onFail();
                    THID_info.setTh_info(null);
                    THID_info.clearPic();
                    THID_info.setisReading(false);
                }

                @Override
                public void onError(int i, String s) throws RemoteException {
                    idListener.onError();
                    THID_info.setTh_info(null);
                    THID_info.clearPic();
                    THID_info.setisReading(false);
                }
            });

        } catch (RemoteException e) {
            THID_info.setTh_info(null);
            THID_info.clearPic();
            THID_info.setisReading(false);
        }
    }

    public void stop_THID() {
        try {
            aidlIdCardTha.stopSearch();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void allOperateStart(final boolean isCheckMag, final boolean isCheckIC, final boolean isCheckRF) {
        if (Utility.IsDebug)
            Log.d(TAG, "Searching the card");
        try {

            int findCardTimeout = 60000;
            pboc2.checkCard(isCheckMag, isCheckIC, isCheckRF, findCardTimeout,
                    new AidlCheckCardListener.Stub() {

                        @Override
                        public void onCanceled() {
                            if (Utility.IsDebug)
                                Log.d(TAG, "pboc2 onCanceled");
                        }

                        @Override
                        public void onError(int arg0) {
                            if (Utility.IsDebug)
                                Log.d(TAG, "pboc2 onError : " + arg0);
                        }

                        @Override
                        public void onFindICCard() {

                            if (Utility.IsDebug)
                                Log.d(TAG, "pboc2 onFindICCard");


                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        iccard.open();
                                        if (iccard.status() == 1) {
                                            if (iccard.reset() != null) {

//                                                String cmd_resp_ar = HexUtil.bytesToHexString(iccard.sendAsync(HexUtil.hexStringToByte("00A40400")));
//                                                if (Utility.IsDebug)
//                                                    Log.d(TAG, "apdu resp " + cmd_resp_ar);
//                                                String cmd1_resp_ar = HexUtil.bytesToHexString(iccard.sendAsync(HexUtil.hexStringToByte("80CA9F7F")));

                                                String cmd_resp_ar = HexUtil.bytesToHexString(iccard.sendAsync(HexUtil.hexStringToByte("00A40400")));
                                                if (Utility.IsDebug)
                                                    Log.d(TAG, "apdu resp " + cmd_resp_ar);
                                                // Paul_20220105 add to cmd.   if result code 6CXX is one more command + XX (length)
                                                if(cmd_resp_ar.length() == 4 && cmd_resp_ar.substring( 0,2 ).equals( "6C" )) {
                                                    String sendcmd = "00A40400" + cmd_resp_ar.substring( 2,4 );
                                                    String cmd_resp_ar1 = HexUtil.bytesToHexString(iccard.sendAsync(HexUtil.hexStringToByte(sendcmd)));
                                                    if (Utility.IsDebug)
                                                        Log.d(TAG, "apdu resp " + cmd_resp_ar1);
                                                }
                                                String cmd1_resp_ar = HexUtil.bytesToHexString(iccard.sendAsync(HexUtil.hexStringToByte("80CA9F7F")));

                                                if (Utility.IsDebug)
                                                    Log.d(TAG, "apdu resp 1 " + cmd1_resp_ar);
                                                String chip_no_resp_ar = HexUtil.bytesToHexString(iccard.sendAsync(HexUtil.hexStringToByte("80CA9F7F2D")));

                                                if (Utility.IsDebug)
                                                    Log.d(TAG, "apdu resp 2 " + chip_no_resp_ar);

                                                // apdu resp code : 9000 is OK
                                                if (!chip_no_resp_ar.substring(90).equals("9000")) {
                                                    chipListener.onFail();
                                                }else{
                                                    if (Utility.IsDebug)
                                                        Log.d(TAG, "CHIP :: " + chip_no_resp_ar.substring(26, 42).toLowerCase());
                                                    chipListener.onSuccess(chip_no_resp_ar.substring(26, 42).toLowerCase());
                                                }
                                            }else
                                                chipListener.onFail();
                                        }

                                    } catch (RemoteException e) {
                                        if (Utility.IsDebug)
                                            Log.d(TAG, e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }, 400);
                        }

                        @Override
                        public void onFindMagCard(ParcelableTrackData arg0) {
                        }

                        @Override
                        public void onFindRFCard() {
                        }

                        @Override
                        public void onSwipeCardFail() {
                        }

                        @Override
                        public void onTimeout() {
                            if (Utility.IsDebug)
                                Log.d(TAG, "pboc2 onTimeout");
                            chipListener.onTimeout();
                        }

                    });
        } catch (Exception e) {
            if (Utility.IsDebug)
                Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public void checkUpdate() {
        checkAPK_THVTMS();
    }

    private void checkAPK_THVTMS() {
        if (isAppExist(context, "com.thaivan.tms_service")) {
            String json_path = "/storage/emulated/0/Thaivan/Temp/print_param.json";
            File THV_file = new File(json_path);
            if (THV_file.exists()) {
                updateLister.onFindJson();
            }else{
                final String apk_path = "/storage/emulated/0/Thaivan/Temp/";
                THV_file = new File(apk_path);
                File[] files = THV_file.listFiles();
                if(files != null && files.length != 0){
                    final String apk_name = files[0].getName();
                    TMS = "THV";
                    if(!updateFlag && apk_name.contains(".apk")) {
                        updateFlag = true;
                        updateLister.onRunTHVInstaller(apk_path + apk_name, TMS);
                    }
                }else {
                    updateFlag = false;
                    updateLister.onNone();
                }
            }
        }else
            updateLister.onNone();
    }

    public void updateFile() {
        updateJSN_THVTMS();
    }

    private void updateJSN_THVTMS() {
        if (isAppExist(context, "com.thaivan.tms_service")) {
            String json_path = "/storage/emulated/0/Thaivan/Temp/print_param.json";
            File THV_file = new File(json_path);
            if (THV_file.exists()) {
                File fileToMove = new File("/storage/emulated/0/oversea_ct/gtms/print_param.json");
                boolean isMoved = THV_file.renameTo(fileToMove);
                if (isMoved) {
                    updateLister.onUpdateJson();
                } else {
                    updateLister.onUpdateFail();
                }
            }
        }else
            updateLister.onNone();
    }

    private String getDevMask() {
        String serial;
        String data = null;
        try {
            serial = settingService.readSerialNum();
            byte[] snBytes = serial.getBytes();
            byte[] sn = new byte[32];
            for (int i = 0; i < sn.length; i++) {
                sn[i] = 0x20;
            }
            System.arraycopy(snBytes, 0, sn, 0, snBytes.length);

            int len = sn.length;
            int maxLen = MASK.length;
            byte[] result = new byte[len < maxLen ? len : maxLen];
            for (int i = 0; (i < len && i < maxLen); i++) {
                result[i] = (byte) (MASK[i] ^ sn[i]);
            }
            data = Base64.encodeToString(result,Base64.DEFAULT);
            return data.replace("\n", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data.replace("\n", "");
    }


    public static String  getRandomWithLength(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append("0123456789".charAt(random.nextInt(10)));
        }
        return sb.toString();
    }

    private void readInfo() {
        try {
            String LASER_ID ="";
            try {
                if (iccard.sendAsync(HexUtil.hexStringToByte(_cmd + _thai_id_card_extend)) != null) {
                    String data = new String(iccard.sendAsync(HexUtil.hexStringToByte("80000000FF")), _UTF8_CHARSET);
                    LASER_ID = data.substring(7,19);
                    if(Utility.IsDebug)
                        Log.d(TAG, "CHIP_LASER_ID :: " + LASER_ID);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (iccard.sendAsync(HexUtil.hexStringToByte(_cmd + _thai_id_card)) != null) {
                String version = new String(iccard.sendAsync(HexUtil.hexStringToByte(_req_version)), _UTF8_CHARSET);
                version = version.trim();
                if(Utility.IsDebug)
                    Log.i("Chock", "a" + version + "D");
                if (version.startsWith("0003")) {
                    String CID = trimData(sendApdu("80b0000402000d"));
                    String NAME_TH = trimData(sendApdu("80b00011020064"));
                    String NAME_EN = trimData(sendApdu("80b00075020064"));
                    String BIRTH_GENDER_BP1NO = trimData(sendApdu("80b000D902001D"));
                    String BIRTH  = BIRTH_GENDER_BP1NO.substring(0,8); //YYYYMMDD
                    BIRTH = BIRTH.substring(6) + BIRTH.substring(4,6) + BIRTH.substring(0,4);
                    String GENDER = BIRTH_GENDER_BP1NO.substring(8,9); // 1=M, 2=F
                    if(GENDER.equals("1"))
                        GENDER = "M";
                    else
                        GENDER = "F";
                    String BP1NO = BIRTH_GENDER_BP1NO.substring(9, BIRTH_GENDER_BP1NO.indexOf("/"));
                    String ISSUE_BY = trimData(sendApdu("80b000F6020064"));
                    ISSUE_BY = ISSUE_BY.replace("/", " ");
                    String ISSUE_CODE = trimData(sendApdu("80b0015a02000d"));
                    String DATE = trimData(sendApdu("80b00167020012"));
                    String ISSUE_DATE = DATE.substring(0,8); // YYYYMMDD
                    ISSUE_DATE = ISSUE_DATE.substring(6) + ISSUE_DATE.substring(4,6) + ISSUE_DATE.substring(0,4);
                    String EXP_DATE = DATE.substring(8,16); //YYYYMMDD
                    EXP_DATE = EXP_DATE.substring(6) + EXP_DATE.substring(4,6) + EXP_DATE.substring(0,4);
                    String CARD_TYPE = trimData(sendApdu("80b00177020002"));
                    String ADDRESS = trimData(sendApdu("80b015790200A0"));
                    String[] split_address = ADDRESS.split("#");
                    String HOME_NO = split_address[0];
                    String MOO = split_address[1];
                    String TROK = split_address[2];
                    String SOI = split_address[3];
                    String ROAD = split_address[4];
                    String SUB_DIS = split_address[5];
                    String DIS = split_address[6];
                    String PRO = split_address[7];
                    String NO_UNDER_PIC = trimData(sendApdu("80b0161902000E"));
                    String SIGN = trimData(sendApdu("80b01627020100"));
                    String RESULT = CID + "|" + NAME_TH + "|" + NAME_EN + "|" + BIRTH+"|" + GENDER +"|" +BP1NO + "|" + ISSUE_BY + "|" + ISSUE_CODE + "|" + ISSUE_DATE +"|" +EXP_DATE + "|" + CARD_TYPE + "|" + ADDRESS + "|" + NO_UNDER_PIC + "|" + SIGN;
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Version", version.substring(0,4));
                    jsonObject.put("CitizenId", CID);
                    jsonObject.put("ThaiName", NAME_TH);
                    String[] split_thainame = NAME_TH.split("#");
                    switch (split_thainame.length){
                        case 1:
                            jsonObject.put("ThaiTitle", split_thainame[0]);
                            jsonObject.put("ThaiFirstName", "");
                            jsonObject.put("ThaiMiddleName", "");
                            jsonObject.put("ThaiLastName", "");
                            break;
                        case 2:
                            jsonObject.put("ThaiTitle", split_thainame[0]);
                            jsonObject.put("ThaiFirstName", split_thainame[1]);
                            jsonObject.put("ThaiMiddleName", "");
                            jsonObject.put("ThaiLastName", "");
                            break;
                        case 3:
                            jsonObject.put("ThaiTitle", split_thainame[0]);
                            jsonObject.put("ThaiFirstName", split_thainame[1]);
                            jsonObject.put("ThaiMiddleName", split_thainame[2]);
                            jsonObject.put("ThaiLastName", "");
                            break;
                        case 4:
                            jsonObject.put("ThaiTitle", split_thainame[0]);
                            jsonObject.put("ThaiFirstName", split_thainame[1]);
                            jsonObject.put("ThaiMiddleName", split_thainame[2]);
                            jsonObject.put("ThaiLastName", split_thainame[3]);
                            break;
                    }
                    jsonObject.put("EnglishName", NAME_EN);
                    String[] split_enname = NAME_EN.split("#");
                    switch (split_enname.length){
                        case 1:
                            jsonObject.put("EnglishTitle", split_enname[0]);
                            jsonObject.put("EnglishFirstName", "");
                            jsonObject.put("EnglishMiddleName", "");
                            jsonObject.put("EnglishLastName", "");
                            break;
                        case 2:
                            jsonObject.put("EnglishTitle", split_enname[0]);
                            jsonObject.put("EnglishFirstName", split_enname[1]);
                            jsonObject.put("EnglishMiddleName", "");
                            jsonObject.put("EnglishLastName", "");
                            break;
                        case 3:
                            jsonObject.put("EnglishTitle", split_enname[0]);
                            jsonObject.put("EnglishFirstName", split_enname[1]);
                            jsonObject.put("EnglishMiddleName", split_enname[2]);
                            jsonObject.put("EnglishLastName", "");
                            break;
                        case 4:
                            jsonObject.put("EnglishTitle", split_enname[0]);
                            jsonObject.put("EnglishFirstName", split_enname[1]);
                            jsonObject.put("EnglishMiddleName", split_enname[2]);
                            jsonObject.put("EnglishLastName", split_enname[3]);
                            break;
                    }
                    jsonObject.put("BirthDate", BIRTH);
                    jsonObject.put("CardIssueCenter", ISSUE_BY);
                    jsonObject.put("CardIssueCode", ISSUE_CODE);
                    jsonObject.put("CardIssueDate", ISSUE_DATE);
                    jsonObject.put("CardExpireDate", EXP_DATE);
                    jsonObject.put("CardType", CARD_TYPE);
                    jsonObject.put("NoUnderPic", NO_UNDER_PIC);
                    jsonObject.put("Gender", GENDER);
                    jsonObject.put("Address", ADDRESS);
                    jsonObject.put("HomeNumber", HOME_NO);
                    jsonObject.put("Moo", MOO);
                    jsonObject.put("Trok", TROK);
                    jsonObject.put("Soi", SOI);
                    jsonObject.put("Road", ROAD);
                    jsonObject.put("SubDistrict", SUB_DIS);
                    jsonObject.put("District", DIS);
                    jsonObject.put("Province", PRO);
                    jsonObject.put("Bp1no", BP1NO);
                    jsonObject.put("laserId", LASER_ID);
                    THID_info.setTh_info(jsonObject.toString());
                    if(Utility.IsDebug)
                        Log.d(TAG, RESULT);
                } else {
                    String CID = trimData(sendApdu("80b1000402000d"));
                    String NAME_TH = trimData(sendApdu("80b10011020064"));
                    String NAME_EN = trimData(sendApdu("80b10075020064"));
                    String BIRTH_GENDER_BP1NO = trimData(sendApdu("80b100D902001D"));
                    String BIRTH  = BIRTH_GENDER_BP1NO.substring(0,8); //YYYYMMDD
                    BIRTH = BIRTH.substring(6) + BIRTH.substring(4,6) + BIRTH.substring(0,4);
                    String GENDER = BIRTH_GENDER_BP1NO.substring(8,9); // 1=M, 2=F
                    if(GENDER.equals("1"))
                        GENDER = "M";
                    else
                        GENDER = "F";
                    String ISSUE_BY = trimData(sendApdu("80b100F6020064"));
                    ISSUE_BY = ISSUE_BY.replace("/", " ");
                    String ISSUE_CODE = trimData(sendApdu("80b1015a02000d"));
                    String DATE = trimData(sendApdu("80b10167020012"));
                    String ISSUE_DATE = DATE.substring(0,8); // YYYYMMDD
                    ISSUE_DATE = ISSUE_DATE.substring(6) + ISSUE_DATE.substring(4,6) + ISSUE_DATE.substring(0,4);
                    String EXP_DATE = DATE.substring(8,16); //YYYYMMDD
                    EXP_DATE = EXP_DATE.substring(6) + EXP_DATE.substring(4,6) + EXP_DATE.substring(0,4);
                    String CARD_TYPE = trimData(sendApdu("80b10177020002"));
                    String ADDRESS = trimData(sendApdu("80b10004020096"));
                    String[] split_address = ADDRESS.split("#");
                    String HOME_NO = split_address[0];
                    String MOO = split_address[1];
                    String TROK = split_address[2];
                    String SOI = split_address[3];
                    String ROAD = split_address[4];
                    String SUB_DIS = split_address[5];
                    String DIS = split_address[6];
                    String PRO = split_address[7];
                    String NO_UNDER_PIC = trimData(sendApdu("80b1161902000E"));
                    String RESULT = CID + "|" + NAME_TH + "|" + NAME_EN + "|" + BIRTH+"|" + GENDER + "|" + ISSUE_BY + "|" + ISSUE_CODE + "|" + ISSUE_DATE +"|" +EXP_DATE + "|" + CARD_TYPE + "|" + ADDRESS + "|" + NO_UNDER_PIC;
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Version", version.substring(0,4));
                    jsonObject.put("CitizenId", CID);
                    jsonObject.put("ThaiName", NAME_TH);
                    String[] split_thainame = NAME_TH.split("#");
                    jsonObject.put("ThaiTitle", split_thainame[0]);
                    jsonObject.put("ThaiFirstName", split_thainame[1]);
                    jsonObject.put("ThaiMiddleName", split_thainame[2]);
                    jsonObject.put("ThaiLastName", split_thainame[3]);
                    jsonObject.put("EnglishName", NAME_EN);
                    String[] split_enname = NAME_EN.split("#");
                    jsonObject.put("EnglishTitle", split_enname[0]);
                    jsonObject.put("EnglishFirstName", split_enname[1]);
                    jsonObject.put("EnglishMiddleName", split_enname[2]);
                    jsonObject.put("EnglishLastName", split_enname[3]);
                    jsonObject.put("BirthDate", BIRTH);
                    jsonObject.put("CardIssueCenter", ISSUE_BY);
                    jsonObject.put("CardIssueCode", ISSUE_CODE);
                    jsonObject.put("CardIssueDate", ISSUE_DATE);
                    jsonObject.put("CardExpireDate", EXP_DATE);
                    jsonObject.put("CardType", CARD_TYPE);
                    jsonObject.put("NoUnderPic", NO_UNDER_PIC);
                    jsonObject.put("Gender", GENDER);
                    jsonObject.put("Address", ADDRESS);
                    jsonObject.put("HomeNumber", HOME_NO);
                    jsonObject.put("Moo", MOO);
                    jsonObject.put("Trok", TROK);
                    jsonObject.put("Soi", SOI);
                    jsonObject.put("Road", ROAD);
                    jsonObject.put("SubDistrict", SUB_DIS);
                    jsonObject.put("District", DIS);
                    jsonObject.put("Province", PRO);
                    jsonObject.put("Bp1no", "");
                    jsonObject.put("laserId", LASER_ID);
                    THID_info.setTh_info(jsonObject.toString());
                    if(Utility.IsDebug)
                        Log.d(TAG, RESULT);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String sendApdu(String apdu) throws RemoteException {
        return new String(sendAPDUForRetry(apdu), this._UTF8_CHARSET);
    }

    private boolean reset() {
        try {
            this.iccard.close();
            this.iccard.open();
            if (this.iccard.status() == 1 && this.iccard.reset() != null) {
                AidlICCard aidlICCard = this.iccard;
                if (aidlICCard.send(HexUtil.hexStringToByte(this._cmd + this._thai_id_card)) != null) {

                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sendCommandForPhoto() {
        try {
            String str = new String(sendAPDUForRetry(this._req_version), this._UTF8_CHARSET).trim().startsWith("0003") ? "80B0" : "80B1";
            int bytes2short = com.centerm.smartpos.util.HexUtil.bytes2short(m9r(sendAPDUForRetry(str + "0179020002")));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bytes2short);
            int i = bytes2short / 250;
            int i2 = bytes2short % 250;
            int i3 = 0;
            while (true) {
                if (i3 >= i + 1) {
                    break;
                }
                int i4 = (i3 * 250) + 379;
                int i5 = i3 == i ? i2 : 250;
                if (i5 != 0) {
                    String e = m8e((i4 >> 8) & 255);
                    String e2 = m8e(i4 & 255);
                    String e3 = m8e(i5 & 255);
                    byte[] r = m9r(sendAPDUForRetry(str + e + e2 + "0200" + e3));
                    if (r == null) {
                        break;
                    }
                    byteArrayOutputStream.write(r, 0, r.length);
                    i3++;
                } else {
                    break;
                }
            }
            this.photoBytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            this.readPhotoSuccess = true;
        } catch (Throwable th) {
            th.printStackTrace();
            this.photoBytes = null;
            this.readPhotoSuccess = false;
        }
    }

    private static String m8e(int i) {
        String hexString = Integer.toHexString(i);
        if (hexString.length() % 2 == 1) {
            hexString = "0" + hexString;
        }
        return hexString.toUpperCase();
    }

    private byte[] m9r(byte[] bArr) {
        if (bArr != null && bArr.length >= 2) {
            return Arrays.copyOfRange(bArr, 0, bArr.length - 2);
        }
        throw new RuntimeException("Read IC card error.");
    }

    private String trimData(String str) {
        if (str != null && str.length() > 2) {
            String trim = str.substring(0, str.length() - 2).trim();

            return trim;
        }
        return "TRIM_ERROR";
    }

    private byte[] sendAPDUForRetry(String str) {
        byte[] hexStringToByte = HexUtil.hexStringToByte(str);
        boolean z = false;
        byte[] bArr = null;
        byte b = 0;
        boolean z2 = false;
        while (true) {
            byte b2 = (byte) (b + 1);
            if (b >= 5) {
                break;
            }
            try {
                bArr = this.iccard.sendAsync(hexStringToByte);
                if (bArr != null) {
                    break;
                }
                z2 = reset();
                if (z2 && (bArr = this.iccard.sendAsync(hexStringToByte)) != null) {
                    break;
                }
                b = b2;
            } catch (Exception e) {
                e.printStackTrace();
                z2 = reset();
                if (z2) {
                    try {
                        bArr = this.iccard.sendAsync(hexStringToByte);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    if (bArr != null) {
                        break;
                    }
                } else {
                    continue;
                }
            }
        }
        return bArr;
    }

    public AidlPrinter getInstancesPrint() {
        if (printDev != null) {
            return printDev;
        }
        return null;
    }

    public boolean isAppExist(Context context, String appPackageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (appPackageName.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void stop_pboc2() {
        try {
            pboc2.cancelCheckCard();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setUpdateFlag() {
        updateFlag = false;
    }

    public interface ChipListener {
        void onSuccess(String chip);
        void onFail();
        void onTimeout();
    }

    public interface IdListener {
        void onFindID();
        void onFail();
        void onError();
    }
    public interface UpdateListner {
        void onFindJson();
        void onFindApk();
        void onFindJsonandApk();
        void onNone();
        void onRunTHVInstaller(String path, String TMS);
        void onUpdateJson();
        void onUpdateFail();
    }
}
