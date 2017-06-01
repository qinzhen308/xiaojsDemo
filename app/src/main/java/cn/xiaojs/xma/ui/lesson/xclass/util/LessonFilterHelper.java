package cn.xiaojs.xma.ui.lesson.xclass.util;


import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;

/**
 * Created by Paul Z on 2017/6/1.
 */

public class LessonFilterHelper {

    public static String getState(int position){
        String state="All";
        switch (position){
            case 0:
                break;
            case 1:
                state= Ctl.LiveLessonState.LIVE;

                break;
            case 2:
                state= Ctl.LiveLessonState.PENDING_FOR_LIVE;

                break;
            case 3:
                state= Ctl.LiveLessonState.FINISHED;

                break;
            case 4:
                state= Ctl.LiveLessonState.DRAFT;

                break;
            case 5:
                //待上架??????
                state= Ctl.LiveLessonState.PENDING_FOR_APPROVAL;

                break;
            case 6:
                state= Ctl.LiveLessonState.PENDING_FOR_APPROVAL;

                break;
            case 7:
                state= Ctl.LiveLessonState.REJECTED;

                break;
            case 8:
                state= Ctl.LiveLessonState.PENDING_FOR_ACK;
                break;
            case 9:
                state= Ctl.LiveLessonState.ACKNOWLEDGED;
                break;
            case 10:
                state= Ctl.LiveLessonState.STOPPED;
                break;
            case 11:
                state= Ctl.LiveLessonState.CANCELLED;
                break;

        }
        return state;
    }


    public static String getType(int position){
        String type="All";
        switch (position){
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }
        return type;
    }
}
