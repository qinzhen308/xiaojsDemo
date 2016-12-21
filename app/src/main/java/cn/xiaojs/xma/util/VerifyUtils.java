package cn.xiaojs.xma.util;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:Administrator
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyUtils {
    /**
     * 验证手机号是否合法
     *
     * @param phone
     * @return
     */
    public static boolean checkPhoneNum(String phone) {
        Pattern p = Pattern.compile("^1[3,4,5,7,8]\\d{9}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 验证手机号是否支持短信验证 180,181不支持
     *
     * @param phone
     * @return
     */
    public static boolean checkPhoneNumCanAuth(String phone) {
        Pattern p = Pattern.compile("^1[8][0,1]\\d{8}$");
        Matcher m = p.matcher(phone);
        return !m.matches();
    }

    /**
     * 检测普通电话号码是否符合规则（仅检测位数）
     */
    public static boolean checkNormalPhone(String phone) {
        int length = phone.length();
        if (length == 7 || length == 8 || length == 10 || length == 11
                || length == 12) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkUserPhone(String phone) {
        int length = phone.length();
        if (length == 11) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检验邮箱地址是否正确
     */
    public static boolean checkEmail(String email) {
        Pattern p = Pattern
                .compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 检验QQ号码是否合法
     */
    public static boolean checkQQ(String qq) {
        Pattern p = Pattern.compile("^[1-9]\\d{4,10}$");
        Matcher m = p.matcher(qq);
        return m.matches();
    }

    /**
     * 电话号码转换为十六进制字符串
     *
     * @param numStr
     * @return
     */
    public static String numToHexString(String numStr) {
        String hexString = "";
        try {
            hexString = Long.toHexString(Long.valueOf(numStr));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return hexString;
    }

}
