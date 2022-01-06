package com.thaivan.bay.branch.scan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class BitmapHandler {
    //private static Bitmap bmp;
    private static byte[] msg_type;
    private static byte[] bitmap_byte;
    private static String TXT_msg;
    private static ArrayList<String> DocNameArr = new ArrayList<>();
    private static ArrayList<Bitmap> DocArr = new ArrayList<>();

    public static synchronized void addBitmapArray(String Filepath, Bitmap Thumbnail) {
        DocNameArr.add(Filepath);
        if (Thumbnail != null)
            DocArr.add(Thumbnail);
    }

    public static synchronized void setPassportText(byte[] type, String msg) {
        msg_type = type;
        TXT_msg = msg;
    }

    public static synchronized byte[] getPassportTextType() {
        return msg_type;
    }

    public static synchronized String getPassportText() {
        return TXT_msg;
    }

    public static synchronized void addBitmapArray_passport(String Filepath, Bitmap Thumbnail) {
        File orgFile = new File(Filepath);
        Bitmap temp_bmp = BitmapFactory.decodeFile(Filepath);

        String CompFilePath = "/storage/emulated/0/Pictures/";
        String compress_file = CompFilePath + "c_" + orgFile.getName();
        File file = new File(compress_file);

        try {

            OutputStream outStream = new FileOutputStream(file);
            temp_bmp.compress(Bitmap.CompressFormat.JPEG, 10, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        temp_bmp.recycle();

        DocNameArr.add(compress_file);
        if (Thumbnail != null)
            DocArr.add(Thumbnail);
    }

    public static synchronized void changeBitmapArray(int index, String name, Bitmap Thumbnail) {
        del_array(index);
        DocNameArr.add(index, name);
        DocArr.add(index, Thumbnail);
    }

    public static synchronized void del_array(int index) {
        File file = new File(DocNameArr.get(index));
        file.delete();
        DocNameArr.remove(index);
        DocArr.remove(index);

    }

    public static synchronized void array_clear() {
        for (int index = 0; index < DocNameArr.size(); index++) {
            File file = new File(DocNameArr.get(index));
            file.delete();
        }
        DocNameArr.clear();
        DocArr.clear();
    }

    public static synchronized String getBitmap(int index) {
        return DocNameArr.get(index);
    }

    public static synchronized Bitmap getBitmapThumbnail(int index) {
        return DocArr.get(index);
    }

    public static synchronized int getBitmapArrSize() {
        return DocNameArr.size();
    }

    public static synchronized byte[] getBitmap_byte() {
        return bitmap_byte;
    }

    public static synchronized Bitmap getBitmap() {
        return BitmapFactory.decodeByteArray(bitmap_byte, 0, bitmap_byte.length);
    }

    public static synchronized void setBitmap(byte[] bmp_byte) {
        bitmap_byte = bmp_byte;
    }

    public static synchronized void setBitmap(Bitmap bmp) {
        bitmap_byte = bitmapToByteArray(bmp);
    }

    private static byte[] bitmapToByteArray(Bitmap $bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        $bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        return stream.toByteArray();
    }
}
