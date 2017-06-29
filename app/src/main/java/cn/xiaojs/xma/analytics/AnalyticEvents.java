package cn.xiaojs.xma.analytics;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by Paul Z on 2017/6/29.
 */

public class AnalyticEvents {
    //定义的统计事件id，顺序不能乱
    public final static String[] EVENT_IDS={
            "lesson_Pluskey",//1
            "Lesson_Search",
            "Lesson_navigationbar_lesson",
            "Lesson_navigationbar_dynamic",
            "Lesson_navigationbar_my",
            "Lesson_Classtable_entry",
            "My_to_homepage",
            "My_to_message",
            "My_myclass",
            "My_datebase",//10
            "My_addressbook",
            "My_Teachingability",
            "My_Realname",
            "My_setup",
            "My_share",
            "Dynamic_Release",
            "teachingability_search",
            "Createclass_needsignup",
            "Createclass_notsignup",
            "Createclass_Optional_information",//20
            "Createclass_Automatic_shelves",
            "Createclass_public",
            "Createclass_Video_playback",
            "Message_platform",
            "Message_focus",
            "Message_class",
            "Message_invitation",
            "ShareApp_saveimage",
            "ShareApp_share",
            "Createclass_op_upimage",//30
            "Createclass_Introduction",
            "Createclass_tag",
            "Lesson_plus_createclass",
            "Lesson_plus_createlesson",
            "Lesson_Scan",
            "Lesson_Classtabel_to_classroom",
            "Lesson_Classtable_schedule",
            "Lesson_Classtable_datebase",
            "Createclass_NeedVerification",
            "Createclass_notVerification",//40
            "Classroom_liveshow",
            "Classroom_datebase",
            "Classroom_userlist",
            "Classroom_chatmode_chat",
            "Classroom_schedule",
            "Classroom_chatmode_fullscreen",
            "Classroom_landscape_Screenswitch",
            "Classroom_landscape_userlist",
            "Classroom_landscape_chatshow",
            "Classroom_landscape_chat",//50
            "Classroom_landscape_screenshots",
            "liveshow_Switch_lens",
            "liveshow_Screenswitch",
            "liveshow_userlist",
            "liveshow_screenshots",
            "liveshow_chatshow",
            "liveshow_chat",
            "liveshow_pause",
            "liveshow_Invitelive",
    };


    /**
     *
     * @param context
     * @param event 友盟定义的事件名  目前名字就是 1,2,3,4...n
     */
    public static void onEvent(Context context,int event){
        if(event<1||event>EVENT_IDS.length)return;
        MobclickAgent.onEvent(context,EVENT_IDS[event-1]);
    }




    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }
    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = null;
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }
            String mac = null;
            FileReader fstream = null;
            try {
                fstream = new FileReader("/sys/class/net/wlan0/address");
            } catch (FileNotFoundException e) {
                fstream = new FileReader("/sys/class/net/eth0/address");
            }
            BufferedReader in = null;
            if (fstream != null) {
                try {
                    in = new BufferedReader(fstream, 1024);
                    mac = in.readLine();
                } catch (IOException e) {
                } finally {
                    if (fstream != null) {
                        try {
                            fstream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            json.put("mac", mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
