package cn.xiaojs.xma.ui.classroom2.util;

/**
 * Created by maxiaobao on 2017/10/24.
 */

public class TimeUtil {

    public static String formatSecondTime(long second) {
        if (second <= 0) {
            return "00:00:00";
        }

        long h = (second % (24 * 3600)) / 3600;
        long m = (second % 3600) / 60;
        long s = second % 60;
        long day = second / (24 * 3600);

        String hh = "00";
        if (h < 10) {
            hh = "0" + h;
        } else if (h >= 0) {
            hh = String.valueOf(h);
        }

        String mm = "00";
        if (m < 10) {
            mm = "0" + m;
        } else if (m >= 0 && m <= 59) {
            mm = String.valueOf(m);
        }

        String ss = "00";
        if (s < 10) {
            ss = "0" + s;
        } else if (s >= 0 && s <= 59) {
            ss = String.valueOf(s);
        }

        if (day > 0) {
            return day + "天";
        } else {
            return hh + ":" + mm + ":" + ss;
        }
    }

}
