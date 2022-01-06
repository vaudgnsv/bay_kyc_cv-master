package com.thaivan.bay.branch.util;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.content.res.AssetManager;
import android.util.Base64;
import android.util.Log;
import com.thaivan.bay.branch.Utility;


public class AES256Util {

    private  byte[] ivBytes;
    private static int ITERATIONS = 1;

    public String  generate_secretkey() {
        String secretKey;
        ivBytes = new byte[16];
        StringBuffer temp = new StringBuffer();
        StringBuffer temp2 = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < 32; i++) {
            int rIndex = rnd.nextInt(3);
            switch (rIndex) {
                case 0:
                    // a-z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    // A-Z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    // 0-9
                    temp.append((rnd.nextInt(10)));
                    break;
            }
        }

        secretKey = temp.toString(); // 비밀키 랜덤으로 생성
        rnd = new Random();
        for (int i = 0; i < 16; i++) {
            int rIndex = rnd.nextInt(3);
            switch (rIndex) {
                case 0:
                    // a-z
                    temp2.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    // A-Z
                    temp2.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    // 0-9
                    temp2.append((rnd.nextInt(10)));
                    break;
            }
        }
        ivBytes = temp2.toString().getBytes(); // IV 랜덤으로 생성
//        String fix_key = "E3EprL8DH61i5AKj";
//        ivBytes = fix_key.getBytes();
//        secretKey = "l79gm9kP8W59C8F9u64QM8HVnO8rV8WR";
        return secretKey;
    }

    //public static final String secretKey = "9A2B049157B38251";

    /**
     * 일반 문자열을 지정된 키를 이용하여 AES256 으로 암호화

     * @exception
     */


    public  String strEncode_secret(String str, String key) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        byte[] textBytes = str.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        return Base64.encodeToString(cipher.doFinal(textBytes), 0);
    }

    public String encryptRSA(String data, PublicKey pubKey) throws Exception {
        data = data + "@" + Utility.hexToAscii(bcd2Str(ivBytes));
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);

        byte[] encoded = Base64.encode(cipher.doFinal(data.getBytes()),Base64.DEFAULT);
        return  new String(encoded).replace("\n", "");
    }

    private PublicKey readPublicKeyFromFile(String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File file = new File(fileName);
        FileInputStream stream = null;
        stream = new FileInputStream(file);
        FileChannel fc = stream.getChannel();
        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        /* Instead of using default, pass in a decoder. */
        String jString = Charset.defaultCharset().decode(bb).toString();

        String pubKeyPEM = jString.replace("-----BEGIN PUBLIC KEY-----\n", "");
        pubKeyPEM = pubKeyPEM.replace("-----END PUBLIC KEY-----", "");

        // Base64 decode the data
        byte[] encoded = Base64.decode(pubKeyPEM, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pubkey = kf.generatePublic(keySpec);

        return pubkey;
    }

    // IV
    public  byte[] getIvBytes() {
        return ivBytes;
    }

    public static String AsciiTohex(String str){
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for(int i = 0; i < chars.length; i++){
            hex.append(Integer.toHexString((int)chars[i]));
        }
        return hex.toString();
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a)
            sb.append(String.format("%02x", b&0xff));
        return sb.toString();
    }

    public static String bcd2Str(byte[] b) {
        if (b==null) {
            return null;
        }
        char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; ++i) {
            sb.append(HEX_DIGITS[((b[i] & 0xF0) >>> 4)]);
            sb.append(HEX_DIGITS[(b[i] & 0xF)]);
        }

        return sb.toString();
    }

}
