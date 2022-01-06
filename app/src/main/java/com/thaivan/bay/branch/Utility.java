package com.thaivan.bay.branch;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

public class Utility {

    private static Dialog dialogAlert = null;
    public static boolean IsDebug = false;
    public static void customDialogAlert(Context context, String msg) {
        dialogAlert = new Dialog(context, R.style.ThemeWithCorners);
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_alert, null);
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlert.setContentView(view);
        dialogAlert.setCancelable(false);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        if (msg != null) {
            msgLabel.setText(msg);
        }

        dialogAlert.show();

        CountDownTimer timer = new CountDownTimer(2500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                dialogAlert.dismiss();
            }
        };
        timer.start();
    }

    public static void customDialogAlertSuccess(Context context, @Nullable String msg, final OnClickCloseImage onClickCloseImage) {
        final Dialog dialogAlert = new Dialog(context, R.style.ThemeWithCorners);
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_success, null);
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlert.setContentView(view);
        dialogAlert.setCancelable(false);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        if (msg != null) {
            msgLabel.setText(msg);
        }

        dialogAlert.show();

        CountDownTimer timer = new CountDownTimer(2500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                dialogAlert.dismiss();
            }
        };
        timer.start();
    }



    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a)
            sb.append(String.format("%02x", b&0xff));
        return sb.toString();
    }

    public static String calNumTraceNo(String trace) {
        String traceNo = "";
        for (int i = trace.length(); i < 6; i++) {
            traceNo += "0";
        }
        return traceNo + trace;
    }

    public static String idValue(String szQr, String szId, String szValue) {
        String szLen = null;
        szLen = String.valueOf(szValue.length());
        if (szLen.length() == 1)
            szLen = "0" + szLen;

        szQr += szId + szLen + szValue;

        return szQr;
    }



    private static Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap) {

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth - overlay.getWidth()) / 2;
        int centreY = (canvasHeight - overlay.getHeight()) / 2;
        canvas.drawBitmap(overlay, centreX, centreY, null);

        return combined;
    }

    public static String CheckSumCrcCCITT(String SourceString) {
        String OutString = null;
        int crc = 0xffff;
//        int polynomial = 0xffff;
        int polynomial = 0x1021;

//        byte[] array = HexUtil.hexStringToByte( SourceString );
        byte[] array = SourceString.getBytes();
        for (byte b : array) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }
        crc &= 0xFFFF;
        OutString = Integer.toHexString(crc);
        if (OutString.length() < 4) {
            for (int i = OutString.length(); i < 4; i++) {
                OutString = "0" + OutString;
            }
        }
        System.out.printf("utility:: CheckSumCrcCCITT AAAAAAAAAAAAAAA  %s \n", OutString);
        return OutString;
    }

    public interface OnClickCloseImage {
        void onClickImage(Dialog dialog);
    }
}
