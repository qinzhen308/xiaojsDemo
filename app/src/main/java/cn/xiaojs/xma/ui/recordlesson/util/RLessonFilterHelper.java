package cn.xiaojs.xma.ui.recordlesson.util;


import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;

/**
 * Created by Paul Z on 2017/7/25.
 */

public class RLessonFilterHelper {

    public static String getState(int position){
        String state=null;
        switch (position){
            case 0:
                state=null;
                break;
            case 1:
                state= Ctl.RecordedCourseState.DRAFT;
                break;
            case 2:
                state= Ctl.RecordedCourseState.FROZEN;
                break;
            case 3:
                state= Ctl.RecordedCourseState.ONSHELVES;
                break;
            case 4:
                state= Ctl.RecordedCourseState.REJECTED;
                break;
        }
        return state;
    }


    public static String getType(int position){
        String type="All";
        switch (position){
            case 0:
                type= "All";
                break;
            case 1:
                type= "CourseTeacher";

                break;
            case 2:
                type= "CourseStudent";
                break;
        }
        return type;
    }
}
