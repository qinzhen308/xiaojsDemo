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

import java.util.Locale;
import java.util.UUID;

import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;

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



    public static final String suffixLib=".aiff.avi.mov.mpeg.mpg.ogg.mp4.3gp.doc.docx.ppt.pptx.pdf.bm.bmp.png.jpg.jpeg.gif.tiff";
    /**
     * 去掉指定mimetype的后缀
     * @param src
     * @param mimeType
     * @return
     */
    public static String wipeSuffix(String src,String mimeType){
        if(!src.contains(".")||src.endsWith(".")){
            return src;
        }
        String suffix=src.substring(src.lastIndexOf(".")+1,src.length()).toLowerCase(Locale.ENGLISH);
        if(!Collaboration.isVideo(mimeType)&&
                !Collaboration.isStreaming(mimeType)&&
                !Collaboration.isDoc(mimeType)&&
                !Collaboration.isPDF(mimeType)&&
                !Collaboration.isPPT(mimeType)&&
                !Collaboration.isImage(mimeType)){
            return src;
        }

        if(!suffixLib.contains(suffix)){
            return src;
        }
        return src.substring(0,src.lastIndexOf("."));
    }



    public static String getPercentString(float percent) {
        return String.format(Locale.US, "%d%%", (int) (percent * 100));
    }
    /**
     * 删除字符串中的空白符
     *
     * @param content
     * @return String
     */
    public static String removeBlanks(String content) {
        if (content == null) {
            return null;
        }
        StringBuilder buff = new StringBuilder();
        buff.append(content);
        for (int i = buff.length() - 1; i >= 0; i--) {
            if (' ' == buff.charAt(i) || ('\n' == buff.charAt(i)) || ('\t' == buff.charAt(i))
                    || ('\r' == buff.charAt(i))) {
                buff.deleteCharAt(i);
            }
        }
        return buff.toString();
    }
    /**
     * 获取32位uuid
     *
     * @return
     */
    public static String get32UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static boolean isEmpty(String input) {
        return TextUtils.isEmpty(input);
    }

    /**
     * 生成唯一号
     *
     * @return
     */
    public static String get36UUID() {
        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString();
        return uniqueId;
    }


    public static final String filterUCS4(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }

        if (str.codePointCount(0, str.length()) == str.length()) {
            return str;
        }

        StringBuilder sb = new StringBuilder();

        int index = 0;
        while (index < str.length()) {
            int codePoint = str.codePointAt(index);
            index += Character.charCount(codePoint);
            if (Character.isSupplementaryCodePoint(codePoint)) {
                continue;
            }

            sb.appendCodePoint(codePoint);
        }

        return sb.toString();
    }

    /**
     * counter ASCII character as one, otherwise two
     *
     * @param str
     * @return count
     */
    public static int counterChars(String str) {
        // return
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            int tmp = (int) str.charAt(i);
            if (tmp > 0 && tmp < 127) {
                count += 1;
            } else {
                count += 2;
            }
        }
        return count;
    }
}
