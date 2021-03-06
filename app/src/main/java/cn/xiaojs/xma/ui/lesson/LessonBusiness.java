package cn.xiaojs.xma.ui.lesson;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/11/10
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Duration;
import cn.xiaojs.xma.util.TimeUtil;

import java.util.Date;

public class LessonBusiness {

    public static Criteria getFilter(int timePosition,int statePosition,int sourcePosition,boolean isTeacher){
        Criteria criteria = new Criteria();
        criteria.setState(getStateByPosition(statePosition,isTeacher));

        Duration duration = new Duration();
        duration.setStart(getStartDate(timePosition));
        duration.setEnd(getEndDate(timePosition));

        criteria.setDuration(duration);
        criteria.setSource(getSource(sourcePosition));
        return criteria;
    }

    public static String getSource(int position){
        switch (position){
            case 0:
                return Ctl.LessonSource.ALL;
            case 1:
                return Ctl.LessonSource.MYSELF;
            case 2:
                return Ctl.LessonSource.INVITATIONS;
        }
        return null;
    }

    public static Date getStartDate(int position){

        Logger.d("getStartDate: " + position);

        switch (position){
            case 0:
                return TimeUtil.original();
            case 1:
            case 2:
            case 3:
            case 4:
                return TimeUtil.beforeDawn();
            case 5:
                return TimeUtil.monthBefore(6);
            case 6:
                return TimeUtil.yearBefore(1);
            default:
                return TimeUtil.original();
        }

    }

    public static Date getEndDate(int position){

        Logger.d("getEndDate: " + position);

        switch (position){
            case 0:
                return TimeUtil.yearAfter(10);
            case 1:
                return TimeUtil.middleNight();
            case 2:
                return TimeUtil.dayAfter(7);
            case 3:
                return TimeUtil.monthAfter(1);
            case 4:
                return TimeUtil.monthAfter(3);
            case 5:
            case 6:
                return TimeUtil.middleNight();

            default:
                return TimeUtil.original();
        }
    }


    public static String getStateByPosition(int position,boolean isTeacher){
        if (isTeacher){
            switch (position){
                case 0:
                    return null;
                case 1:
                    return LessonState.DRAFT;
                case 2:
                    return LessonState.PENDING_FOR_APPROVAL;
                case 3:
                    return LessonState.PENDING_FOR_LIVE;
                case 4:
                    return LessonState.LIVE;
                case 5:
                    return LessonState.FINISHED;
                case 6:
                    return LessonState.REJECTED;
                case 7:
                    return LessonState.STOPPED;
                case 8:
                    return LessonState.CANCELLED;
                default:
                    return null;
            }
        }else {
            switch (position){
                case 0:
                    return null;
                case 1:
                    return LessonState.PENDING_FOR_LIVE;
                case 2:
                    return LessonState.LIVE;
                case 3:
                    return LessonState.FINISHED;
                case 4:
                    return LessonState.CANCELLED;
                default:
                    return null;
            }
        }
    }

    public static Criteria getSearch(String key){
        Criteria criteria = new Criteria();

        Duration duration = new Duration();
        duration.setStart(TimeUtil.original());
        duration.setEnd(TimeUtil.yearAfter(10));

        criteria.setTitle(key);
        criteria.setDuration(duration);
        return criteria;
    }

    public static void enterEnrollRegisterPage(Context context, String lessonId, String cover, String title, long startTime, long duration) {
        Intent intent = new Intent(context, EnrollRegisterActivity.class);
        intent.putExtra(EnrollRegisterActivity.KEY_LESSON, lessonId);
        intent.putExtra(EnrollRegisterActivity.KEY_COVER, cover);
        intent.putExtra(EnrollRegisterActivity.KEY_TITLE, title);
        intent.putExtra(EnrollRegisterActivity.KEY_START_TIME, startTime);
        intent.putExtra(EnrollRegisterActivity.KEY_DURATION, duration);
        context.startActivity(intent);
    }
}
