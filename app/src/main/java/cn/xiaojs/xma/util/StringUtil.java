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
 * Author:zhanghui
 * Date:2016/11/13
 * Desc:
 *
 * ======================================================================================== */

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import cn.xiaojs.xma.common.xf_foundation.schemas.Account;

public class StringUtil {

    public static Spannable getSpecialString(String origin,String target,int fontSize,int fontColor){
        Spannable span = new SpannableString(origin);
        int start = origin.indexOf(target);
        int end = start + target.length();
        span.setSpan(new ForegroundColorSpan(fontColor),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (fontSize > 0){
            span.setSpan(new AbsoluteSizeSpan(fontSize),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return span;
    }

    public static Spannable getSpecialString(String origin,String target,int fontColor){
        return getSpecialString(origin,target,0,fontColor);
    }

    public static String getTa(String sex){
        if (TextUtils.isEmpty(sex))
            return "Ta";
        if (Account.Sex.MALE.equalsIgnoreCase(sex)){
            return "他";
        }else if (Account.Sex.FEMALE.equalsIgnoreCase(sex)){
            return "她";
        }else {
            return "Ta";
        }
    }

    public static String protectCardNo(String phone) {
        if (TextUtils.isEmpty(phone) || phone.length() != 11) {
            return phone;
        }

        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");

    }

    public static SpannableString setHighlightText(String source,String tagText,int color){
        if(TextUtils.isEmpty(source))return new SpannableString("");
        SpannableString ss=new SpannableString(source);
        if(TextUtils.isEmpty(tagText))return ss;
        int index=0;
        int tagLength=tagText.length();
        while (index>=0&&index<source.length()) {
            index=source.indexOf(tagText,index);
            if(index<0){
                break;
            }
            ss.setSpan(new ForegroundColorSpan(color),index,index+tagLength,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            index=index+tagLength;
        }
        return ss;
    }

    public static SpannableString setHighlightText2(String source,String tagText,int color){
        String prefix="<em>";
        String suffix="</em>";
        if(TextUtils.isEmpty(source))return new SpannableString("");
        SpannableString ss=new SpannableString(source);
        if(TextUtils.isEmpty(tagText))return ss;
        try {
            int index=0;
            int tagLength=tagText.length();
            int tempLength=0;
            int offset=0;
            while (index>=0&&index<tagLength) {
                index=tagText.indexOf(prefix,index);
                if(index<0){
                    break;
                }
                tempLength=tagText.indexOf(suffix,index)-index-prefix.length();
                ss.setSpan(new ForegroundColorSpan(color),index-offset,index+tempLength-offset,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                offset+=prefix.length()+suffix.length();
                index=index+tempLength+prefix.length()+suffix.length();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ss;
    }
}
