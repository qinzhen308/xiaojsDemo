package cn.xiaojs.xma.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
 * Date:2016/11/7
 * Desc:
 *
 * ======================================================================================== */
public class TimeUtil {

    public static final String TIME_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String TIME_YYYY_MM_DD2 = "yyyy'/'MM'/'dd";
    public static final String TIME_YYYY_MM_DD3 = "yyyy.MM.dd";
    public static final String TIME_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_YYYY_MM_DD_HH_MM_SS2 = "yyyy'/'MM'/'dd HH:mm:ss";
    public static final String TIME_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String TIME_YYYY_MM_DD_HH_MM2 = "yyyy'/'MM'/'dd HH:mm";
    public static final String TIME_MM_DD_HH_MM = "MM-dd HH:mm";
    public static final String TIME_MM_DD_HH_MM_SS = "MM-dd HH:mm:ss";
    public static final String TIME_HH_MM = "HH:mm";
    public static final String TIME_HH_MM_SS = "HH:mm:ss";
    public static final String TIME_MM_SS = "mm:ss";
    public static final String TIME_MM_DD = "MM-dd";

    /**
     * 时间字符串格式化
     *
     * @param date      需要被处理的日期字符串
     * @param parseStr  需要被处理的日期的格式串
     * @param formatStr 最终返回的日期字符串的格式串
     * @return 已经格式化的日期字符串
     */
    public static String formatDate(String date, String parseStr,
                                    String formatStr) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(parseStr,
                Locale.getDefault());
        Date d = null;
        try {
            d = sdf.parse(date);
            sdf.applyPattern(formatStr);
            return sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatDate(long date, String formatStr) {
        DateFormat sdf = new SimpleDateFormat(formatStr, Locale.getDefault());
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatDate(String date, String format) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        String str = date;
        if (str.contains("T")) {
            str = str.replace("T", " ");
        }
        if (str.contains("/")) {
            str = str.replaceAll("/", "-");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Date d = null;
        try {
            d = sdf.parse(str);
            return sdf.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 时间为 昨天或今天时,不显示年月日改为显示"昨天"或"今天" 比如 "2014-01-01 12:10:26 --> 今天 12:10:26" , "2015-12-23 18:51
     * --> 昨天 18:51"
     *
     * @param time 当天时间
     * @param f    时间格式
     */
    public static String formatDate2(String time, String f) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(
                    TIME_YYYY_MM_DD_HH_MM, Locale.ENGLISH);
            Date date = null;
            time = formatDate(time, f);
            date = format.parse(time);

            Calendar current = Calendar.getInstance();
            current.setTime(date);
            Calendar today = getSomeDay(0);
            Calendar tomorrow = getSomeDay(1);
            Calendar yesterday = getSomeDay(-1);

            if (current.before(tomorrow) && current.after(today)) {// 今天
                return "今天 " + time.split(" ")[1];
            } else if (current.before(today) && current.after(yesterday)) {// 昨天
                return "昨天 " + time.split(" ")[1];
            } else if (current.before(yesterday)) {// 昨天前面的时间
                return time;
            } else if (current.after(tomorrow)) {// 今天之后的时间
                return time;
            } else {// 应该不会存在
                return time;
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 今天的消息显示“时分”、昨天的消息显示“昨天 时:分”，昨天之前的信息显示“年-月-日 时:分”
     *
     * @param time 当天时间
     */
    public static String formatDate3(long time) {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat(TIME_HH_MM,
                    Locale.ENGLISH);
            SimpleDateFormat format2 = new SimpleDateFormat(
                    TIME_YYYY_MM_DD_HH_MM, Locale.ENGLISH);
            Calendar current = Calendar.getInstance();
            Date date = new Date(time);
            current.setTime(date);
            Calendar today = getSomeDay(0);
            Calendar tomorrow = getSomeDay(1);
            Calendar yesterday = getSomeDay(-1);

            if (current.before(tomorrow) && current.after(today)) {// 今天
                return format1.format(date);
            } else if (current.before(today) && current.after(yesterday)) {// 昨天
                return "昨天" + format1.format(date);
            } else if (current.before(yesterday)) {// 昨天前面的时间
                return format2.format(date);
            } else if (current.after(tomorrow)) {// 今天之后的时间
                return format2.format(date);
            } else {// 应该不会存在
                return format2.format(date);
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 得到某天零点零分零秒的Calendar对象
     *
     * @param dayOfMonth 今天传0 昨天传-1 明天传1 其他天++或者--
     * @return Calendar对象
     */
    private static Calendar getSomeDay(int dayOfMonth) {
        Calendar current = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, current.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, current.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH)
                + dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    public static String getWeak(String date, String format) {
        try {
            return getWeak(new SimpleDateFormat(format, Locale.ENGLISH).parse(
                    date).getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getWeak(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String week = "星期日";
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                week = "星期日";
                break;
            case 2:
                week = "星期一";
                break;
            case 3:
                week = "星期二";
                break;
            case 4:
                week = "星期三";
                break;
            case 5:
                week = "星期四";
                break;
            case 6:
                week = "星期五";
                break;
            case 7:
                week = "星期六";
                break;
            default:
                break;
        }
        return week;
    }

    /**
     * 字符时间转换成Date
     *
     * @param format 需要的Date格式
     */
    public static Date strToDate(String value, String format) {
        if (TextUtils.isEmpty(value)) {
            return new Date();
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format,
                    Locale.getDefault());
            Date strtodate = formatter.parse(value);
            return strtodate;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long strToMillis(String time) {
        Date date = strToDate(time, "yyyy-MM-dd");
        if (date != null) {
            return date.getTime();
        } else {
            return 0;
        }
    }

    public static long strToMillis(String time, String format) {
        Date date = strToDate(time, format);
        if (date != null) {
            return date.getTime();
        } else {
            return 0;
        }
    }

    /**
     * 计算两个时间之间的相差天数，第二个参数减第一个参数
     */
    public static int compareDifference(long first, long second) {
        try {
            long time = second - first;
            int diff = (int) (time / 60 / 60 / 1000 / 24);
            return diff;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 比较两个日期大小，如果第一个小于第二个返回true，
     */
    public static boolean compareDate(String first, String second) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date firstDate;
        try {
            firstDate = sdf.parse(first);
            Date secondDate = sdf.parse(second);
            return firstDate.before(secondDate);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据传入date获取前后多少天的日期
     *
     * @param day         正数就是后几天 负数就是前几天
     * @param returnParse 返回时间的格式
     */
    public static String getDateByDay(String date, int day, String returnParse) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(strToMillis(date));
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return formatDate(calendar.getTimeInMillis(), returnParse);
    }

    /**
     * 比较两个日期的大小（前者大 返回true,比较到分钟数）
     */
    public static boolean compareDate(Date one, Date two) {
        if (one.getYear() > two.getYear()) {
            return true;
        } else if (one.getYear() == two.getYear()
                && one.getMonth() > two.getMonth()) {
            return true;
        } else if (one.getYear() == two.getYear()
                && one.getMonth() == two.getMonth()
                && one.getDate() > two.getDate()) {
            return true;
        } else if (one.getYear() == two.getYear()
                && one.getMonth() == two.getMonth()
                && one.getDate() == two.getDate()
                && one.getHours() > two.getHours()) {
            return true;
        } else if (one.getYear() == two.getYear()
                && one.getMonth() == two.getMonth()
                && one.getDate() == two.getDate()
                && one.getHours() == two.getHours()
                && one.getMinutes() > two.getMinutes()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 比较两个日期的大小（前者大 返回true,比较到天）
     */
    public static boolean compareTwoDate(Date one, Date two) {
        if (one.getYear() > two.getYear()) {
            return true;
        } else if (one.getYear() == two.getYear()
                && one.getMonth() > two.getMonth()) {
            return true;
        } else if (one.getYear() == two.getYear()
                && one.getMonth() == two.getMonth()
                && one.getDate() > two.getDate()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean compareTwoDate1(Date one, Date two) {
        if (one.getYear() > two.getYear()) {
            return true;
        } else if (one.getYear() == two.getYear()
                && one.getMonth() > two.getMonth()) {
            return true;
        } else if (one.getYear() == two.getYear()
                && one.getMonth() == two.getMonth()
                && one.getDate() >= two.getDate()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getTimeStrFromDatePicker(DatePicker date,
                                                  TimePicker time) {
        String timeStr = date.getYear() + "-";
        if (date.getMonth() + 1 < 10) {
            timeStr += "0" + (date.getMonth() + 1);
        } else {
            timeStr += (date.getMonth() + 1);
        }
        timeStr += "-";
        if (date.getDayOfMonth() < 10) {
            timeStr += "0" + date.getDayOfMonth();
        } else {
            timeStr += date.getDayOfMonth();
        }
        if (time != null) {
            if (time.getCurrentHour() < 10) {
                timeStr += " 0" + time.getCurrentHour();
            } else {
                timeStr += " " + time.getCurrentHour();
            }
            if (time.getCurrentMinute() < 10) {
                timeStr += ":0" + time.getCurrentMinute();
            } else {
                timeStr += ":" + time.getCurrentMinute();
            }
        }
        return timeStr;
    }

    /**
     * @return 1天5小时20分
     */
    @SuppressLint("SimpleDateFormat")
    public static String distanceDay(String date) {
        String reulst = "";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = formatDate(date, "yyyy-MM-dd HH:mm:ss");
        // 给定的时间
        Date end = null;
        try {
            end = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 当前时间
        Date now = new Date();
        // 得到时间差
        long diff = (end.getTime() - now.getTime()) / 1000;
        long mm = (diff / 60) % 60;
        long hh = (diff / 60 / 60) % 24;
        long dd = (diff / 60 / 60 / 24);

        reulst = (dd + "天" + hh + "小时" + mm + "分");

        return reulst;
    }

    /**
     * @return 获取当前月第一天
     */
    @SuppressLint("SimpleDateFormat")
    public static String getMonthForFirstDay() {
        SimpleDateFormat format = new SimpleDateFormat(TIME_YYYY_MM_DD);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        return format.format(cal.getTime());
    }

    /**
     * @return 获取当前月最后一天
     */
    @SuppressLint("SimpleDateFormat")
    public static String getMonthForLastDay() {
        SimpleDateFormat format = new SimpleDateFormat(TIME_YYYY_MM_DD);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH,
                cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return format.format(cal.getTime());
    }

    /**
     * 刚刚------2分钟及以内的
     * <p/>
     * 今天 hh:mm------除了刚刚，且在当天24:00内的
     * <p/>
     * yy-mm-dd hh:mm----除了以上的，就是它
     */
    public static String getProductHistory(long time) {
        String result = null;
        if ((System.currentTimeMillis() - time) <= 2 * 1000 * 60) {
            result = "刚刚";
        } else if (isSameDay(time, System.currentTimeMillis())) {
            result = "今天" + formatDate(time, TIME_HH_MM);
        } else {
            result = formatDate(time, TimeUtil.TIME_YYYY_MM_DD_HH_MM);
        }

        return result;
    }

    public static boolean isSameDay(long timeOne, long timeTwo) {
        Date dateOne = new Date(timeOne);
        Date dateTwo = new Date(timeTwo);
        if (dateOne.getYear() == dateTwo.getYear()
                && dateOne.getMonth() == dateTwo.getMonth()
                && dateOne.getDate() == dateTwo.getDate()) {
            return true;
        }
        return false;
    }

    public static boolean isSameDay(String timeOne, String timeTwo) {
        Date dateOne = strToDate(timeOne, TIME_YYYY_MM_DD);
        Date dateTwo = strToDate(timeTwo, TIME_YYYY_MM_DD);
        if (dateOne.getYear() == dateTwo.getYear()
                && dateOne.getMonth() == dateTwo.getMonth()
                && dateOne.getDate() == dateTwo.getDate()) {
            return true;
        }
        return false;
    }

    public static boolean isYesterdayDay(long timeOne, long timeTwo) {
        Date dateOne = new Date(timeOne);
        Date dateTwo = new Date(timeTwo);
        if (dateOne.getYear() == dateTwo.getYear()
                && dateOne.getMonth() == dateTwo.getMonth()
                && (dateOne.getDate() == dateTwo.getDate() - 1)) {
            return true;
        }
        return false;
    }

    public static String getPostBoardHeaderTime(long time) {
        String result = null;
        if (isSameDay(time, System.currentTimeMillis())) {
            result = "今天";
        } else if (isYesterdayDay(time, System.currentTimeMillis())) {
            result = "昨天";
        } else {
            result = formatDate(time, TimeUtil.TIME_YYYY_MM_DD);
        }

        return result;
    }

    public static String getPostBoardHeaderTime(String time) {
        long localTime = strToMillis(
                formatDate(time, TIME_YYYY_MM_DD_HH_MM_SS),
                TIME_YYYY_MM_DD_HH_MM_SS);
        return getPostBoardHeaderTime(localTime);
    }

    /**
     * 截取格式 **年**月**日
     */
    public static String formatDate(String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        date = formatDate(date, TIME_YYYY_MM_DD);
        String[] arr = date.split("-");
        if (arr != null && arr.length == 3) {
            String s = arr[0] + "年" + arr[1] + "月" + arr[2] + "日";
            return s;
        }
        return "";
    }

    /**
     * 当前时间推迟一年加一天
     *
     * @param format 返回数据的时间格式
     */
    public static String getTheDayNextYear(String format) {
        String nextYear = "";
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
            calendar.set(Calendar.DAY_OF_YEAR,
                    calendar.get(Calendar.DAY_OF_YEAR) + 1);
            SimpleDateFormat sdf = new SimpleDateFormat(format,
                    Locale.getDefault());
            nextYear = sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nextYear;
    }

    // time 的格式为 HH:MM:SS
    public static long getTimeMils(String time) {
        if (TextUtils.isEmpty(time) || (!time.contains(":"))) {
            return 0;
        }
        String[] tmp = time.split(":");
        if (tmp.length != 3) {
            return 0;
        }
        long tmpTime = Integer.parseInt(tmp[0]) * 3600 * 1000
                + Integer.parseInt(tmp[1]) * 60 * 1000
                + Integer.parseInt(tmp[2]) * 1000;
        return tmpTime;
    }

    // 获取两个时间的相差天、时、分、秒钟
    public static long[] getSpaceTime(String startTime, String endTime) {
        long[] time = new long[4];
        if (!TextUtils.isEmpty(startTime) && (!TextUtils.isEmpty(endTime))) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            startTime = formatDate(startTime, "yyyy-MM-dd HH:mm:ss");
            endTime = formatDate(endTime, "yyyy-MM-dd HH:mm:ss");
            long nd = 1000 * 24 * 60 * 60;//一天的毫秒数
            long nh = 1000 * 60 * 60;//一小时的毫秒数
            long nm = 1000 * 60;//一分钟的毫秒数
            long ns = 1000;//一秒钟的毫秒数long diff;
            try {
                long diff = format.parse(endTime).getTime() - format.parse(startTime).getTime();
                long day = diff / nd;//计算差多少天
                long hour = diff % nd / nh;//计算差多少小时
                long min = diff % nd % nh / nm;//计算差多少分钟
                long sec = diff % nd % nh % nm / ns;//计算差多少秒//输出结果
                time[0] = day;
                time[1] = hour;
                time[2] = min;
                time[3] = sec;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return time;
    }

    // 获取两个时间的相差天、时、分、秒钟
    public static long[] getSpaceTime(long startTime, long endTime) {
        long[] time = new long[4];
        if (startTime > 0 && endTime > 0) {
            long nd = 1000 * 24 * 60 * 60;//一天的毫秒数
            long nh = 1000 * 60 * 60;//一小时的毫秒数
            long nm = 1000 * 60;//一分钟的毫秒数
            long ns = 1000;//一秒钟的毫秒数long diff;
            try {
                long diff = endTime - startTime;
                long day = diff / nd;//计算差多少天
                long hour = diff % nd / nh;//计算差多少小时
                long min = diff % nd % nh / nm;//计算差多少分钟
                long sec = diff % nd % nh % nm / ns;//计算差多少秒//输出结果
                time[0] = day;
                time[1] = hour;
                time[2] = min;
                time[3] = sec;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return time;
    }

    public static String getTimeFormat(Date date, int duration) {

        return duration + "分钟";
    }

    public static Date original() {
        return new Date(0);
    }

    public static Date now() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * 获取凌晨0点时间
     */
    public static Date beforeDawn() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return new Date(cal.getTimeInMillis());
    }

    public static Date middleNight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.MILLISECOND, 999);

        return new Date(cal.getTimeInMillis());
    }

    public static Date monthBefore(int before) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(cal.MONTH, -before);
        return new Date(cal.getTimeInMillis());
    }

    public static Date monthAfter(int after) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(cal.MONTH, after);
        return new Date(cal.getTimeInMillis());
    }

    public static Date yearBefore(int before) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(cal.YEAR, -before);
        return new Date(cal.getTimeInMillis());
    }

    public static Date yearAfter(int after) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(cal.YEAR, after);
        return new Date(cal.getTimeInMillis());
    }

    public static String getTimeFromNow(Date date){
        StringBuilder time = new StringBuilder();
        Date now = new Date(System.currentTimeMillis());
        if (inOneHour(date,now)){
            int target = getTime(date,Calendar.MINUTE);
            int nowMin = getTime(now,Calendar.MINUTE);

            int fact = nowMin - target;
            if (fact > 0){
                time.append(fact);
                time.append("分钟前");
            }else {
                time.append("刚刚");
            }

        }else if (isSameDay(date,now)){
            time.append(format(date,TIME_HH_MM));
        }else if (isYesterday(date,now)){
            time.append("昨天 ");
            time.append(format(date,TIME_HH_MM));
        }else {
            time.append(format(date,TIME_YYYY_MM_DD_HH_MM));
        }

        return time.toString();
    }

    /**
     * 根据传入的日期返回xx分钟之前/后,今天、明天、昨天或者具体时间
     */
    public static String getTimeByNow(Date date) {
        StringBuilder time = new StringBuilder();
        Date now = new Date(System.currentTimeMillis());
        if (inOneHour(date, now)) {
            time.append(getTimeInOneHour(date, now));
        } else if (isSameDay(date, now)) {
            time.append("今天 ");
            time.append(format(date, TIME_HH_MM));
        } else if (isYesterday(date, now)) {
            time.append("昨天 ");
            time.append(format(date, TIME_HH_MM));
        } else if (isTomorrow(date, now)) {
            time.append("明天 ");
            time.append(format(date, TIME_HH_MM));
        } else if (getTime(date, Calendar.YEAR) == getTime(now, Calendar.YEAR)) {//同一年
            time.append(format(date, TIME_MM_DD_HH_MM));
        } else {
            time.append(format(date, TIME_YYYY_MM_DD_HH_MM));
        }

        return time.toString();
    }

    private static boolean isSameDay(Date dayOne, Date dayTwo) {
        return getTime(dayOne, Calendar.YEAR) == getTime(dayTwo, Calendar.YEAR)
                && getTime(dayOne, Calendar.MONTH) == getTime(dayTwo, Calendar.MONTH)
                && getTime(dayOne, Calendar.DAY_OF_MONTH) == getTime(dayTwo, Calendar.DAY_OF_MONTH);
    }

    private static boolean inOneHour(Date target, Date now) {
        if (isSameDay(target, now)) {
            return getTime(target, Calendar.HOUR_OF_DAY) == getTime(now, Calendar.HOUR_OF_DAY);
        }
        return false;
    }

    private static boolean isYesterday(Date target, Date now) {
        long dis = now.getTime() - target.getTime();
        if (dis > 1000 * 3600 * 24 && dis < 1000 * 3600 * 24 * 2) {
            return true;
        }
        return false;
    }

    private static boolean isTomorrow(Date target, Date now) {
        long dis = target.getTime() - now.getTime();
        if (dis > 1000 * 3600 * 24 && dis < 1000 * 3600 * 24 * 2) {
            return true;
        }
        return false;
    }


    private static String getTimeInOneHour(Date target, Date now) {
        StringBuilder time = new StringBuilder();
        if (target.getTime() >= now.getTime()) {//之后
            int dis = getTime(target, Calendar.MINUTE) - getTime(now, Calendar.MINUTE);
            if (dis > 0){
                time.append(dis);
                time.append("分钟之后");
            }else {
                time.append("刚刚");
            }
            return time.toString();
        } else {//之前
            int dis = getTime(now, Calendar.MINUTE) - getTime(target, Calendar.MINUTE);
            if (dis > 0 ){
                time.append(dis);
                time.append("分钟之前");
            }else {
                time.append("刚刚");
            }
            return time.toString();
        }

    }

    private static int getTime(Date date, int type) {
        Calendar calendarTarget = Calendar.getInstance();
        calendarTarget.setTime(date);

        return calendarTarget.get(type);
    }

    public static String format(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return formatter.format(cal.getTime());
    }

    public static String format(long date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }
}
