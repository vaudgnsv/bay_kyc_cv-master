package com.thaivan.bay.branch.util;

import com.centerm.smartpos.util.HexUtil;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class DemoHMAC {
    private static final String SIGNATURE_KEY = "12345678901234567890123456789012";
    private static final String ALGORITHM = "HmacSHA256";

    public static String doHmacSha256Base64(String payload) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        byte[] hexKey = SIGNATURE_KEY.getBytes("UTF-8");
        byte[] body = payload.getBytes("UTF-8");
        Mac sha256HMAC = Mac.getInstance(ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(hexKey, ALGORITHM);
        sha256HMAC.init(secretKey);
//warning: a Mac instance is not thread safe
        byte[] hmacByte = sha256HMAC.doFinal(body);
        String base64Hmac = null;
        base64Hmac = android.util.Base64.encodeToString(hmacByte, android.util.Base64.NO_WRAP);

        return base64Hmac;
    }

    public static void main(String[] args) throws Exception {
        long startTime = Calendar.getInstance().getTimeInMillis();
        String requestBody = "hello";
        String hmac1 = DemoHMAC.doHmacSha256Base64(requestBody);
//request.setHeader("X-Signature", hmac1);
        long elapsedTime = Calendar.getInstance().getTimeInMillis() - startTime;
        System.out.println("Base64 Signature : " + hmac1);
        System.out.println("Timeto calculate : " + elapsedTime + "ms");
    }
}
