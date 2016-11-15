package com.benyuan.xiaojs.ui.course;
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

import com.benyuan.xiaojs.common.xf_foundation.LessonState;
import com.benyuan.xiaojs.model.Criteria;
import com.benyuan.xiaojs.model.Duration;
import com.benyuan.xiaojs.util.TimeUtil;

import java.util.Date;

public class LessonBusiness {

    public static Criteria getFilter(int timePosition,int statePosition,boolean isTeacher){
        Criteria criteria = new Criteria();
        criteria.setState(getStateByPosition(statePosition,isTeacher));

        Duration duration = new Duration();
        duration.setStart(getDateByPosition(timePosition));
        duration.setEnd(TimeUtil.now());

        criteria.setDuration(duration);
        return criteria;
    }

    public static Date getDateByPosition(int position){
        switch (position){
            case 0:
                return TimeUtil.original();
            case 1:
                return TimeUtil.beforeDawn();
            case 2:
                return new Date(System.currentTimeMillis() - 1000 * 3600 * 24 * 7);
            case 3:
                return TimeUtil.weekBefore(1);
            case 4:
                return TimeUtil.weekBefore(3);
            case 5:
                return TimeUtil.yearBefore(1);
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
                    return LessonState.CANCELLED;
                case 8:
                    return LessonState.STOPPED;
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
        duration.setEnd(TimeUtil.now());

        criteria.setTitle(key);
        criteria.setDuration(duration);
        return criteria;
    }
}
