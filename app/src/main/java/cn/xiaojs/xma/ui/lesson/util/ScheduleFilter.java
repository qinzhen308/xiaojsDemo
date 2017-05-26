package cn.xiaojs.xma.ui.lesson.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import cn.xiaojs.xma.model.ctl.ClassLesson;

/**
 * Created by Paul Z on 2017/5/26.
 */

public class ScheduleFilter {

    public static final String simpleYMDFormatStr="yyyy-MM-dd GMT-8:00";
    public static final SimpleDateFormat simpleYMDFormat=new SimpleDateFormat(simpleYMDFormatStr);

    public final static long DAY=3600*24;

    public static List<ClassLesson> sort(List<ClassLesson> src) {
        for (int i=1;i<src.size();i++) {
            long d1 = src.get(i-1).schedule.getStart().getTime();
            long d2 = src.get(i).schedule.getStart().getTime();
            if(d2>d1){
                ClassLesson cl=src.get(i-1);
                src.set(i-1,src.get(i));
                src.set(i,cl);
            }
        }
        return src;
    }


    public static Map<Long,List<ClassLesson>> buildScheduleByDay(List<ClassLesson> src) {
        List<ClassLesson> dest = sort(src);
        Map<Long,List<ClassLesson>> map=new HashMap<>();
        List<ClassLesson> temp = new ArrayList<>();
        long tempDayIndex=0;
        for (int i=0 ;i<dest.size();i++) {
            Date d = dest.get(i).schedule.getStart();
            long dayIndex=d.getTime()/DAY;
            if(dayIndex>tempDayIndex){
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
        cal1.setTimeZone(TimeZone.getTimeZone("GMT-8:00"));
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeZone(TimeZone.getTimeZone("GMT-8:00"));
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
}
