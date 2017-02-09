package cn.xiaojs.xma.ui.classroom;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/2/9
 * Desc:
 *
 * ======================================================================================== */

public class ClassroomBusiness {
    public static Constants.User getUser(String session) {
        Constants.User user = Constants.User.TEACHER;
        if ("LeadSession".equals(session)) {
            user = Constants.User.TEACHER;
        } else if ("AssistantSession".equals(session)) {
            user = Constants.User.ASSISTANT;
        } else if ("RemoteAssistantSession".equals(session)) {
            user = Constants.User.REMOTE_ASSISTANT;
        } else if ("StudentSession".equals(session)) {
            user = Constants.User.STUDENT;
        } else if ("ManagerSession".equals(session)) {
            user = Constants.User.MANAGER;
        } else if ("AuditorSession".equals(session)) {
            user = Constants.User.AUDITOR;
        } else if ("AuditorSession".equals(session)) {
            user = Constants.User.ADMINISTRATOR;
        }

        return user;
    }
}
