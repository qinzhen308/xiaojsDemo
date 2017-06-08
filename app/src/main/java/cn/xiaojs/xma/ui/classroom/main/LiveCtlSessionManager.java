package cn.xiaojs.xma.ui.classroom.main;
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
 * Date:2017/5/12
 * Desc:
 *
 * ======================================================================================== */

import android.text.TextUtils;

import cn.xiaojs.xma.model.live.CtlSession;

public class LiveCtlSessionManager {
    private CtlSession mCtlSession;
    private Constants.User mUser;
    private Constants.User mUserInLesson;
    private Constants.UserMode mUserMode;
    private String mTicket = "";
    private Constants.ClassroomType mClassroomType;

    private static LiveCtlSessionManager mInstance;

    public static synchronized LiveCtlSessionManager getInstance() {
        if (mInstance == null) {
            mInstance = new LiveCtlSessionManager();
        }

        return mInstance;
    }

    public void init(CtlSession ctlSession, String ticket) {
        mCtlSession = ctlSession;
        mUser = ClassroomBusiness.getUser(ctlSession.psType, Constants.User.STUDENT);
        mUserInLesson = ClassroomBusiness.getUser(ctlSession.psTypeInLesson, Constants.User.NONE);
        mUserMode = ClassroomBusiness.getUserByCtlSession(ctlSession);
        mTicket = ticket;
        mClassroomType = ctlSession.cls == null?
                Constants.ClassroomType.StandaloneLesson : Constants.ClassroomType.PrivateClass;
    }

    public synchronized void updateCtlSessionState(String liveState) {
        if (mCtlSession != null && !TextUtils.isEmpty(liveState)) {
            mCtlSession.state = liveState;
        }
    }

    public synchronized void updateCtlSessionMode(int mode) {
        if (mCtlSession != null) {
            mCtlSession.mode = mode;
            mUserMode = ClassroomBusiness.getUserByCtlSession(mCtlSession);
        }
    }

    public synchronized CtlSession getCtlSession() {
        return mCtlSession;
    }

    public synchronized String getLiveState() {
        return mCtlSession != null ? mCtlSession.state : "";
    }

    public synchronized int getLiveMode() {
        return mCtlSession != null ? mCtlSession.mode : 0;
    }

    public synchronized String getClassOrLessonState() {
        if (mCtlSession != null) {
            if (mCtlSession.cls != null) {
                return mCtlSession.cls.state;
            }

            return mCtlSession.state;
        }

        return "";
    }

    public String getTicket() {
        return mTicket;
    }

    public Constants.UserMode getUserMode() {
        return mUserMode != null ? mUserMode : ClassroomBusiness.getUserByCtlSession(mCtlSession);
    }

    public Constants.ClassroomType getClassroomType() {
        return mClassroomType;
    }

    public Constants.User getUser() {
        return mUser;
    }

    public Constants.User getUserInLesson() {
        return mUserInLesson;
    }

    public void release() {
        mCtlSession = null;
        mUser = null;
        mTicket = null;
        mInstance = null;
    }
}
