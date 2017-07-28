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
    public static final String simpleYM_ChFormatStr="yyyy年MM月";
    public static final String simpleHMFormatStr="HH:mm";
    public static final String simpleYMDHMSFormatStr="yyyy-MM-dd HH:mm:ss";
    public static final String simpleYMDHMFormatStr="yyyy-MM-dd HH:mm";
    public static final SimpleDateFormat simpleYMDFormat=new SimpleDateFormat(simpleYMDFormatStr, Locale.CHINA);
    public static final SimpleDateFormat simpleUTCFormat=new SimpleDateFormat(simpleUTCFormatStr);
    public static final SimpleDateFormat simpleMDFormat=new SimpleDateFormat(simpleMDFormatStr,Locale.CHINA);
    public static final SimpleDateFormat simpleHMFormat=new SimpleDateFormat(simpleHMFormatStr,Locale.CHINA);
    public static final SimpleDateFormat simpleYMFormat=new SimpleDateFormat(simpleYMFormatStr,Locale.CHINA);
    public static final SimpleDateFormat simpleYM_ChFormat=new SimpleDateFormat(simpleYM_ChFormatStr,Locale.CHINA);
    public static final SimpleDateFormat simpleYMDHMSFormat=new SimpleDateFormat(simpleYMDHMSFormatStr,Locale.CHINA);
    public static final SimpleDateFormat simpleYMDHMFormat=new SimpleDateFormat(simpleYMDHMFormatStr,Locale.CHINA);

    public final static long DAY=3600*24*1000;

    public static List<ClassLesson> sort(List<ClassLesson> src) {
        int size=src.size();
        ClassLesson cl=null;
        for(int i=0,limit=size-1;i<limit;i++){
            for (int j=i+1;j<size;j++) {
                long cursorItem = src.get(i).schedule.getStart().getTime();
                long compareItem = src.get(j).schedule.getStart().getTime();
                if(compareItem<cursorItem){
                    cl=src.get(i);
                    src.set(i,src.get(j));
                    src.set(j,cl);
                }
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
    public static String getDateYM_Ch(int year,int month,int day){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date(0));
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.set(year,month,day,0,0,0);
        return simpleYM_ChFormat.format(new Date(calendar.getTimeInMillis()));
    }

    public static String getDateYM(Date date){
        return simpleYMFormat.format(date);
    }
    public static String getDateYM_Ch(Date date){
        return simpleYM_ChFormat.format(date);
    }

    public static String getDateYMDHMS(Date date){
        return simpleYMDHMSFormat.format(date);
    }
    public static String getDateYMDHMS(long date){
        return simpleYMDHMSFormat.format(new Date(date));
    }
    public static String getDateYMDHM(Date date){
        return simpleYMDHMFormat.format(date);
    }
    public static String getDateYMDHM(long date){
        return simpleYMDHMFormat.format(new Date(date));
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

        String week = "周日";
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                week = "周日";
                break;
            case 2:
                week = "周一";
                break;
            case 3:
                week = "周二";
                break;
            case 4:
                week = "周三";
                break;
            case 5:
                week = "周四";
                break;
            case 6:
                week = "周五";
                break;
            case 7:
                week = "周六";
                break;
            default:
                break;
        }
        return week;
    }

    public static String getWeek(long timeMills){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date(timeMills));
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        String week = "周日";
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                week = "周日";
                break;
            case 2:
                week = "周一";
                break;
            case 3:
                week = "周二";
                break;
            case 4:
                week = "周三";
                break;
            case 5:
                week = "周四";
                break;
            case 6:
                week = "周五";
                break;
            case 7:
                week = "周六";
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
        return simpleYMDFormat.format(date)+" "+getWeek(date.getTime());
    }

    /**
     *
     * @param dateStr 格式 yyyy-MM-dd  or  yyyy-M-d
     * @return yyyy-MM-dd 周日
     */
    public static String getDateYMDW(String dateStr){
        String[] ymd=dateStr.split("-");
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date(0));
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.set(Integer.valueOf(ymd[0]),Integer.valueOf(ymd[1])-1,Integer.valueOf(ymd[2]));
        return simpleYMDFormat.format(new Date(calendar.getTimeInMillis()))+" "+getWeek(calendar.getTimeInMillis());
    }

    public static boolean isSameDay(Date date,Calendar c){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date(0));
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.setTime(date);
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return calendar.get(Calendar.DAY_OF_MONTH)==c.get(Calendar.DAY_OF_MONTH)&&calendar.get(Calendar.MONTH)==c.get(Calendar.MONTH)&&calendar.get(Calendar.YEAR)==c.get(Calendar.YEAR);
    }

    public static boolean isSameDay(Date d1,Date d2){
        Calendar calendar1=Calendar.getInstance();
        calendar1.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar1.setTime(d1);
        Calendar calendar2=Calendar.getInstance();
        calendar2.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar2.setTime(d2);
        return calendar1.get(Calendar.DAY_OF_MONTH)==calendar2.get(Calendar.DAY_OF_MONTH)&&calendar1.get(Calendar.MONTH)==calendar2.get(Calendar.MONTH)&&calendar1.get(Calendar.YEAR)==calendar2.get(Calendar.YEAR);
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

    public static Date getUTCDate(String date,String format){
        try {
            Date d=new SimpleDateFormat(format).parse(date);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *
     * @return 0:相等  1:前者大于后者  -1:后者大于前者
     */
    public static int compare(int y1,int m1,int d1,int y2,int m2,int d2){
        Calendar calendar1=Calendar.getInstance(Locale.CHINA);
        calendar1.setTime(new Date(0));
        calendar1.set(y1,m1,d1);
        Calendar calendar2=Calendar.getInstance(Locale.CHINA);
        calendar2.setTime(new Date(0));
        calendar2.set(y2,m2,d2);
        long delta=calendar1.getTimeInMillis()-calendar2.getTimeInMillis();
        if(delta==0){
            return 0;
        }else if(delta>0){
            return 1;
        }else {
            return -1;
        }
    }
}
