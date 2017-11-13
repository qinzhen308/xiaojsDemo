package cn.xiaojs.xma.ui.classroom2.util;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * Created by maxiaobao on 2017/11/12.
 */

public class VibratorUtil {

    public static void Vibrate(final Context context, long milliseconds) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }
    public static void Vibrate(final Context context, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }

}
