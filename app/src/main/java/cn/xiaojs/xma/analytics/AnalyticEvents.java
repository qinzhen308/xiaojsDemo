package cn.xiaojs.xma.analytics;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

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



}
