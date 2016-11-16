package com.benyuan.xiaojs.util;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/11/13
 * Desc:
 *
 * ======================================================================================== */

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import java.security.MessageDigest;

public class StringUtil {

    public static Spannable getSpecialString(String origin,String target,int fontSize,int fontColor){
        Spannable span = new SpannableString(origin);
        int start = origin.indexOf(target);
        int end = start + target.length();
        span.setSpan(new ForegroundColorSpan(fontColor),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new AbsoluteSizeSpan(fontSize),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

}
