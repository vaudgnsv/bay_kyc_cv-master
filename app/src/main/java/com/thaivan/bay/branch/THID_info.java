package com.thaivan.bay.branch;

import android.graphics.Bitmap;

import com.centerm.centermposoversealib.thailand.AidlIdCardTha;

import java.io.ByteArrayOutputStream;

public class THID_info {
    private static long reading_start_time = 0;
    private static long reading_end_time = 0;
    private static long processing_start_time = 0;
    private static long processing_end_time = 0;

    private static AidlIdCardTha aidlIdCardTha;
    private static String th_info;
    private static String th_postcode; //when EDC detect 2-3 postcode
    private static byte[] pic;
    private static boolean isReading;

    public static boolean is_isReading() {
        return isReading;
    }

    public static void setisReading(boolean is_reading) {
        THID_info.isReading = is_reading;
    }

    public static byte[] getPic() {
        return pic;
    }

    public static void clearPic() {
        THID_info.pic = null;
    }

    public static void setPic(Bitmap pic) {
        THID_info.pic = bitmapToByteArray(pic);
    }

    public static String getTh_info() {
        return th_info;
    }

    public static void setTh_postcode(String th_postcode) {
        THID_info.th_postcode = th_postcode;
    }

    public static String getTh_postcode() {
        return th_postcode;
    }

    public static void setTh_info(String th_info) {
        THID_info.th_info = th_info;
    }

    private static byte[] bitmapToByteArray(Bitmap $bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        $bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
        return stream.toByteArray();
    }

    public static AidlIdCardTha getAidlIdCardTha() {
        return aidlIdCardTha;
    }

    public static void setAidlIdCardTha(AidlIdCardTha aidlIdCardTha) {
        THID_info.aidlIdCardTha = aidlIdCardTha;
    }

    public static void setReading_start_time(long reading_start_time) {
        THID_info.reading_start_time = reading_start_time;
    }

    public static String getReading_time() {

        if( reading_end_time-reading_start_time < 0) return "0";
        else return add_point(reading_end_time-reading_start_time);

    }

    public static void setReading_end_time(long reading_end_time) {
        THID_info.reading_end_time = reading_end_time;
    }

    public static void setProcessing_start_time(long processing_start_time) {
        THID_info.processing_start_time = processing_start_time;
    }

    public static String getProcessing_time() {
        if( processing_end_time-processing_start_time < 0) return "0";
        else return add_point(processing_end_time-processing_start_time);

        //return add_point(processing_end_time-processing_start_time);
    }

    public static void setProcessing_end_time(long processing_end_time) {
        THID_info.processing_end_time = processing_end_time;
    }

    private static String add_point(long num){
        return String.format("%d.%03d", num/1000, num%1000);
    }
}
