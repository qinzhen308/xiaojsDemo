package cn.xiaojs.xma.ui.lesson.xclass.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Paul Z on 2017/6/1.
 */

public class ClassFilterHelper {

    public static String getStartTime(int position){
        String start="";
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int y=calendar.get(Calendar.YEAR);
        int m=calendar.get(Calendar.MONTH);
        int d=calendar.get(Calendar.DAY_OF_MONTH);
        int wd=calendar.get(Calendar.DAY_OF_WEEK);
        switch (position){
            case 0:
                //全部
                start=ScheduleUtil.getUTCDate(0);
                break;
            case 1:
                //今天
                calendar.setTime(new Date(0));
                calendar.set(y,m,d);
                start=ScheduleUtil.getUTCDate(calendar.getTimeInMillis());
                break;
            case 2:
                //本周
                calendar.setTime(new Date(0));
                calendar.set(y,m,d);
                calendar.add(Calendar.DAY_OF_MONTH,1-wd);
                start=ScheduleUtil.getUTCDate(calendar.getTimeInMillis());
                break;
            case 3:
                //上周
                calendar.setTime(new Date(0));
                calendar.set(y,m,d);
                calendar.add(Calendar.DAY_OF_MONTH,1-wd-7);
                start=ScheduleUtil.getUTCDate(calendar.getTimeInMillis());
                break;
            case 4:
                //下周
                calendar.setTime(new Date(0));
                calendar.set(y,m,d);
                calendar.add(Calendar.DAY_OF_MONTH,1-wd+7);
                start=ScheduleUtil.getUTCDate(calendar.getTimeInMillis());
                break;
            case 5:
                //本月
                calendar.setTime(new Date(0));
                calendar.set(y,m,1);
                start=ScheduleUtil.getUTCDate(calendar.getTimeInMillis());
                break;
            case 6:
                //上月
                calendar.setTime(new Date(0));
                calendar.set(y,m,1);
                calendar.add(Calendar.MONTH,-1);
                start=ScheduleUtil.getUTCDate(calendar.getTimeInMillis());

                break;
            case 7:
                //下月
                calendar.setTime(new Date(0));
                calendar.set(y,m,1);
                calendar.add(Calendar.MONTH,1);
                start=ScheduleUtil.getUTCDate(calendar.getTimeInMillis());

                break;
            case 8:
                //今年
                calendar.setTime(new Date(0));
                calendar.set(y,0,1);
                start=ScheduleUtil.getUTCDate(calendar.getTimeInMillis());
                break;
            case 9:
                //今年
                calendar.setTime(new Date(0));
                calendar.set(y,0,1);
                calendar.add(Calendar.YEAR,-1);
                start=ScheduleUtil.getUTCDate(calendar.getTimeInMillis());
                break;
        }
        return start;
    }


    public static String getEndTime(int position){
        String end="";
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int y=calendar.get(Calendar.YEAR);
        int m=calendar.get(Calendar.MONTH);
        int d=calendar.get(Calendar.DAY_OF_MONTH);
        int wd=calendar.get(Calendar.DAY_OF_WEEK);
        switch (position){
            case 0:
                //全部
                end=ScheduleUtil.getUTCDate(ScheduleUtil.ymdToTimeMill(2100,0,1));
                break;
            case 1:
                //今天
                calendar.setTime(new Date(0));
                calendar.set(y,m,d);
                calendar.add(Calendar.DAY_OF_MONTH,1);
                end=ScheduleUtil.getUTCDate(calendar.getTimeInMillis()-1);
                break;
            case 2:
                //本周
                calendar.setTime(new Date(0));
                calendar.set(y,m,d);
                calendar.add(Calendar.DAY_OF_MONTH,1-wd+7);
                end=ScheduleUtil.getUTCDate(calendar.getTimeInMillis()-1);
                break;
            case 3:
                //上周
                calendar.setTime(new Date(0));
                calendar.set(y,m,d);
                calendar.add(Calendar.DAY_OF_MONTH,1-wd);
                end=ScheduleUtil.getUTCDate(calendar.getTimeInMillis()-1);
                break;
            case 4:
                //下周
                calendar.setTime(new Date(0));
                calendar.set(y,m,d);
                calendar.add(Calendar.DAY_OF_MONTH,1-wd+14);
                end=ScheduleUtil.getUTCDate(calendar.getTimeInMillis()-1);
                break;
            case 5:
                //本月
                calendar.setTime(new Date(0));
                calendar.set(y,m,1);
                calendar.add(Calendar.MONTH,1);
                end=ScheduleUtil.getUTCDate(calendar.getTimeInMillis()-1);
                break;
            case 6:
                //上月
                calendar.setTime(new Date(0));
                calendar.set(y,m,1);
                end=ScheduleUtil.getUTCDate(calendar.getTimeInMillis()-1);

                break;
            case 7:
                //下月
                calendar.setTime(new Date(0));
                calendar.set(y,m,1);
                calendar.add(Calendar.MONTH,2);
                end=ScheduleUtil.getUTCDate(calendar.getTimeInMillis()-1);

                break;
            case 8:
                //今年
                calendar.setTime(new Date(0));
                calendar.set(y,0,1);
                calendar.add(Calendar.YEAR,1);
                end=ScheduleUtil.getUTCDate(calendar.getTimeInMillis()-1);
                break;
            case 9:
                //去年
                calendar.setTime(new Date(0));
                calendar.set(y,0,1);
                end=ScheduleUtil.getUTCDate(calendar.getTimeInMillis()-1);
                break;
        }
        return end;
    }

    public static String getType(int position){
        String type=null;
        switch (position){
            case 0:
                type=null;
                break;
            case 1:
                type="Teacher";
                break;
            case 2:
                type="Student";
                break;
        }
        return type;
    }
}
