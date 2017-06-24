package com.myjo.ordercat.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;

/**
 * Created by lee5hx on 17/6/19.
 */
public class OcEncryptionUtils {


    //加密


    public static String base64Encoder(String str,int count) {
        String s = str;
        for(int i=0;i<count;i++){
            s = base64Encoder(s);
        }
        return s;
    }


    public static String base64Encoder(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }
    //解密

    public static String base64Decoder(String str,int count){
        String s = str;
        for(int i=0;i<count;i++){
            s = base64Decoder(s);
        }
        return s;
    }

    public static String base64Decoder(String s) {
        byte[] b = null;
        String result = null;
        if (s != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(s);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


}
