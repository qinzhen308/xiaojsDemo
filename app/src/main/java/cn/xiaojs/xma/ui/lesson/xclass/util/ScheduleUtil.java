package cn.xiaojs.xma.ui.lesson.xclass.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.util.TimeUtil;

/**
 * Created by Paul Z on 2017/5/26.
 */

public class ScheduleUtil {

    public static final String simpleYMDFormatStr="yyyy-MM-dd";
    public static final String simpleUTCFormatStr="yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String simpleMDFormatStr="MM-dd";
    public static final String simpleYMFormatStr="yyyy-MM";
    public static final String simpleHMFormatStr="HH:mm";
    public static final String simpleYMDHMSFormatStr="yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat simpleYMDFormat=new SimpleDateFormat(simpleYMDFormatStr, Locale.CHINA);
    public static final SimpleDateFormat simpleUTCFormat=new SimpleDateFormat(simpleUTCFormatStr);
    public static final SimpleDateFormat simpleMDFormat=new SimpleDateFormat(simpleMDFormatStr,Locale.CHINA);
    public static final SimpleDateFormat simpleHMFormat=new SimpleDateFormat(simpleHMFormatStr,Locale.CHINA);
    public static final SimpleDateFormat simpleYMFormat=new SimpleDateFormat(simpleYMFormatStr,Locale.CHINA);
    public static final SimpleDateFormat simpleYMDHMSFormat=new SimpleDateFormat(simpleYMDHMSFormatStr,Locale.CHINA);

    public final static long DAY=3600*24*1000;

    public static List<ClassLesson> sort(List<ClassLesson> src) {
        for (int i=1;i<src.size();i++) {
            long d1 = src.get(i-1).schedule.getStart().getTime();
            long d2 = src.get(i).schedule.getStart().getTime();
            if(d2<d1){
                ClassLesson cl=src.get(i-1);
                src.set(i-1,src.get(i));
                src.set(i,cl);
            }
        }
        return src;
    }


    /**
     * 创建以一月为一组的二维数据集
     * 某月没课，map里就没有该key
     * key:2014-05
     * @param src
     * @return
     */
    public static Map<String,List<ClassLesson>> buildScheduleByMonth(List<ClassLesson> src) {
        List<ClassLesson> dest = sort(src);
        Map<String,List<ClassLesson>> map=new HashMap<>();
        List<ClassLesson> temp = new ArrayList<>();
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        long tempMonthIndex=-1;
        for (int i=0 ;i<dest.size();i++) {
            Date d = dest.get(i).schedule.getStart();
            calendar.setTime(d);
            long monthIndex=calendar.get(Calendar.YEAR)*12+calendar.get(Calendar.MONTH);
            if(monthIndex>tempMonthIndex){
                tempMonthIndex=monthIndex;
                temp=new ArrayList<>();
                map.put(getDateYM(d),temp);
                temp.add(src.get(i));
            }else {
                temp.add(src.get(i));
            }
        }
        return map;
    }

    /**
     * ------错误------
     * 创建以一天为一组的二维数据集
     * 某天没课，map里就没有该key
     * key:2014-05-06
     * @param src
     * @return
     */
    @Deprecated
    public static Map<Long,List<ClassLesson>> buildScheduleByDay(List<ClassLesson> src) {
        List<ClassLesson> dest = sort(src);
        Map<Long,List<ClassLesson>> map=new HashMap<>();
        List<ClassLesson> temp = new ArrayList<>();
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        long tempDayIndex=0;
        for (int i=0 ;i<dest.size();i++) {
            Date d = dest.get(i).schedule.getStart();
            calendar.setTime(d);
            long dayIndex=calendar.get(Calendar.DAY_OF_MONTH);
            if(dayIndex>tempDayIndex){
                tempDayIndex=dayIndex;
                temp=new ArrayList<>();
                map.put(dayIndex,temp);
            }else {
                temp.add(src.get(i));
            }
        }
        return map;
    }

    private static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        cal2.setTime(date2);
        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
        return isSameDate;
    }


    public static long getDayIndex(String date){
        try {
            Date d=simpleYMDFormat.parse(date);
            return d.getTime()/DAY;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static String getDateYMD(int year,int month,int day){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date(0));
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.set(year,month,day,0,0,0);
        return simpleYMDFormat.format(new Date(calendar.getTimeInMillis()));
    }

    public static String getDateYMD(Date date){
        return simpleYMDFormat.format(date);
    }
    public static Date getDateYMD(String date){
        try {
            Date d=simpleYMDFormat.parse(date);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDateYM(int year,int month,int day){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date(0));
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.set(year,month,day,0,0,0);
        return simpleYMFormat.format(new Date(calendar.getTimeInMillis()));
    }

    public static String getDateYM(Date date){
        return simpleYMFormat.format(date);
    }

    public static String getDateYMDHMS(Date date){
        return simpleYMDHMSFormat.format(date);
    }
    public static String getDateYMDHMS(long date){
        return simpleYMDHMSFormat.format(new Date(date));
    }


    public static long ymdToTimeMill(int year,int month,int day){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date(0));
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.set(year,month,day,0,0,0);
        return calendar.getTimeInMillis();
    }

    public static String getWeek(int year,int month,int day){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date(0));
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.set(year,month,day,0,0,0);

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


    public static String getDateYMDW(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date(0));
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.setTime(date);
        return simpleYMDFormat.format(date)+" "+TimeUtil.getWeak(date.getTime());
    }

    public static boolean isSameDay(Date date,Calendar c){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date(0));
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.setTime(date);
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return calendar.get(Calendar.DAY_OF_MONTH)==c.get(Calendar.DAY_OF_MONTH)&&calendar.get(Calendar.MONTH)==c.get(Calendar.MONTH)&&calendar.get(Calendar.YEAR)==c.get(Calendar.YEAR);
    }


    public static String getMMDDDate(Date date){
        return simpleMDFormat.format(date);
    }

    public static String getHMDate(Date date){
        return simpleHMFormat.format(date);
    }

    public static String getUTCDate(long date){
        return simpleUTCFormat.format(new Date(date));
    }

    public static Date getUTCDate(String date){
        try {
            Date d=simpleHMFormat.parse(date);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
