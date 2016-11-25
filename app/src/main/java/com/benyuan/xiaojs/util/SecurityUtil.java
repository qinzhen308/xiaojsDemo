package com.benyuan.xiaojs.util;

import java.security.MessageDigest;

/**
 * Created by maxiaobao on 2016/11/25.
 */

public class SecurityUtil {

    public static String md5(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());

            byte messageDigest[] = md.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++){
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }

            return hexString.toString();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return string;
    }


}
