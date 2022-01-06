package com.thaivan.bay.branch.util;

import android.util.Log;

import com.centerm.centermposoversealib.util.HexUtil;
import com.thaivan.bay.branch.Utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Chiper {

    public static String FILE_PATH = "/cache/uvo/";

    public static String TMK_PATH = FILE_PATH + "onuspinKey.key";
    public static String NAME_PATH = FILE_PATH + "name.conf";
//    public static String TMK_PATH = "/sdcard/oversea_ct/gtms/onuspinKey.key";
    private static final String TAG = "Chiper";

    public static String Load_name(){
        try {

            File tmk_file = new File(NAME_PATH);
            if( !tmk_file.isFile()) return null;
            BufferedReader bufrd  = new BufferedReader(new FileReader(tmk_file));
            String TMK = bufrd.readLine();
            bufrd.close();
            return TMK;
        } catch (IOException e) {
            return null;
        }

    }


//    public static String Store_name(String tmk){
//        try {
//            File tmk_file = new File(NAME_PATH);
//            if( tmk_file.isFile()) tmk_file.delete();
//            /*
//            try {
//                int i = changePermissons(tmk_file,0777);
//            } catch (Exception e) {
//                return "STORE_FAIL";
//            }
//            */
//
//            BufferedWriter bufwd  = new BufferedWriter(new FileWriter( tmk_file));
//            bufwd.write(tmk);
//            bufwd.flush();
//            bufwd.close();
//            grant(NAME_PATH);
//        } catch (IOException e) {
//            return "STORE_FAIL";
//        }
//        return "OK";
//    }

    public static String Load_TMK(){
        try {

            File tmk_file = new File(TMK_PATH);
            if( !tmk_file.isFile()) return null;
            BufferedReader bufrd  = new BufferedReader(new FileReader(tmk_file));
            String TMK = bufrd.readLine();
            bufrd.close();
            return TMK;
        } catch (IOException e) {
            return null;
        }

    }


//    public static String Store_TMK(String tmk){
//        try {
//            File tmk_file = new File(TMK_PATH);
//            if( tmk_file.isFile()) tmk_file.delete();
//            /*
//            try {
//                int i = changePermissons(tmk_file,0777);
//            } catch (Exception e) {
//                return "STORE_FAIL";
//            }
//            */
//
//            BufferedWriter bufwd  = new BufferedWriter(new FileWriter( tmk_file));
//            bufwd.write(tmk);
//            bufwd.flush();
//            bufwd.close();
//            grant(TMK_PATH);
//        } catch (IOException e) {
//            return "STORE_FAIL";
//        }
//        return "OK";
//    }

//    public static String Store_TMK(byte [] tmk){
//        try {
//            File tmk_file = new File(TMK_PATH);
//            if( tmk_file.isFile()) tmk_file.delete();
//            /*
//            try {
//                int i = changePermissons(tmk_file,0777);
//            } catch (Exception e) {
//                return "STORE_FAIL";
//            }
//            */
//
//            BufferedWriter bufwd  = new BufferedWriter(new FileWriter( tmk_file));
//            bufwd.write( HexUtil.bytesToHexString(tmk));
//            bufwd.flush();
//            bufwd.close();
//            grant(TMK_PATH);
//        } catch (IOException e) {
//            return "STORE_FAIL";
//        }
//        return "OK";
//    }

//    private static boolean grant(String path) {
//        ShellmoniterUtil shellMonitor = null;
//        shellMonitor = new ShellmoniterUtil();
//        try {
//            shellMonitor.excuteCmd( "chmod 777 " + path );
//            return true;
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            Log.d("AAA", e.getMessage());
//            /*
//            try {
//                com.centerm.frame.core.DeviceManager dm = com.centerm.frame.core.DeviceManager.getInstance( ICCardActivity.this );
//                com.centerm.frame.inf.IFileOperationManager fm = (com.centerm.frame.inf.IFileOperationManager) dm.getManager( com.centerm.frame.core.DeviceManager.FILE_OPERATION_MANAGER );
//                fm.chmod( path, "777" );
//                return true;
//            } catch (ServiceNotFoundException e1) {
//                e1.printStackTrace();
//            }
//            */
//        }
//        return false;
//    }

    public static int changePermissons(File path, int mode) throws Exception {
        Class<?> fileUtils = Class.forName("android.os.FileUtils");
        Method setPermissions = fileUtils.getMethod("setPermissions",
                String.class, int.class, int.class, int.class);

        return (Integer) setPermissions.invoke(null, path.getAbsolutePath(),mode, -1, -1);
    }


    public static byte[] Des_Encode(byte[] clear_text, byte[] key_byte) {
        try {
            DESKeySpec keySpec = new DESKeySpec(key_byte);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES/ECB/noPadding");
            cipher.init( Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(clear_text);

        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] Des_Decode(byte[] enc_text, byte[] key_byte) {
        try {
            DESKeySpec keySpec = new DESKeySpec(key_byte);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES/ECB/noPadding");
            cipher.init( Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(enc_text);

        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] Des3_Encode(byte[] clear_text, byte[] key_byte) {
        try {
            Cipher c3des = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            SecretKeySpec myKey = new SecretKeySpec(key_byte, "DESede");
            c3des.init( Cipher.ENCRYPT_MODE, myKey);
            return c3des.doFinal(clear_text);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] Des3_Decode(byte[] clear_text, byte[] key_byte) {

        try {
            Cipher c3des = Cipher.getInstance("DESede/ECB/noPadding");
            SecretKeySpec myKey = new SecretKeySpec(key_byte, "DESecb");
            c3des.init( Cipher.DECRYPT_MODE, myKey);
            return c3des.doFinal(clear_text);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String sha512Hash(String toHash)
    {
        String hash = null;
        try
        {
            MessageDigest digest = MessageDigest.getInstance( "SHA-512" );
            byte[] bytes = toHash.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            // This is ~55x faster than looping and String.formating()
            hash = bytesToHex( bytes );
        }
        catch( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex( byte[] bytes )
    {
        char[] hexChars = new char[ bytes.length * 2 ];
        for( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[ j ] & 0xFF;
            hexChars[ j * 2 ] = hexArray[ v >>> 4 ];
            hexChars[ j * 2 + 1 ] = hexArray[ v & 0x0F ];
        }
        return new String( hexChars );
    }

    public static String Make_KCV(String KeyComp) {

        byte[] clear_text = new byte[16];

        if (KeyComp.length() != 32 && KeyComp.length() != 16) {
            return "";
        }

        byte[] byteKey = HexUtil.hexStringToByte(KeyComp);
        if( byteKey == null || byteKey.length < 16) return "empty key";

        byte [] enckey = Des3_Encode(clear_text, byteKey);
        if( enckey == null || enckey.length < 16) return "enc fail";
        String KCV = HexUtil.bytesToHexString(enckey);
        if( KCV == null ||  KCV.length() < 6) return "KCV err";
        if(Utility.IsDebug)
            Log.d(TAG, "KCV : " + KCV);

        return KCV.substring(0, 6);

        //return HexUtil.bytesToHexString(Des3_Encode(clear_text, byteKey)).substring(0,6);
    }

    public static String Make_KCV(byte[] KeyComp) {

        byte[] clear_text = new byte[16];

        if (KeyComp.length != 16) {
            return "";
        }

        byte [] enckey = Des3_Encode(clear_text, KeyComp);
        if( enckey == null || enckey.length < 16) return "";
        String KCV = HexUtil.bytesToHexString(enckey);
        if( KCV == null ||  KCV.length() < 6) return "";
        if(Utility.IsDebug)
            Log.d(TAG, "KCV : " + KCV);

        return KCV.substring(0, 6);
    }

    public static byte[] Make_Clear_Key(String KeyComp1, String KeyComp2) {

        byte[] clear_text = new byte[8];

        if (KeyComp1.length() != 32) {
            return null;
        }
        if (KeyComp2.length() != 32) {
            return null;
        }

        byte[] byteT1 = HexUtil.hexStringToByte(KeyComp1);
        byte[] byteT2 = HexUtil.hexStringToByte(KeyComp2);
        byte[] Key = new byte[16];
        for (int i = 0; i < byteT1.length; i++) {
            Key[i] = (byte) (byteT1[i] ^ byteT2[i]);
        }

        if(Utility.IsDebug){
            Log.d(TAG, "component1 KCV : " + Make_KCV(KeyComp1));
            Log.d(TAG, "component2 KCV : " + Make_KCV(KeyComp2));
            Log.d(TAG, "clear KEY KCV : " +Make_KCV(Key));
        }

        return Key;
    }
}
