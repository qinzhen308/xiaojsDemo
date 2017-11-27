package cn.xiaojs.xma.ui.classroom2.util;

import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;

/**
 * Created by maxiaobao on 2017/11/27.
 */

public class ClassroomUtil {

    public static CTLConstant.UserIdentity getUserIdentity(String psType) {

        if ("LeadSession".equals(psType)) {
            return CTLConstant.UserIdentity.LEAD;
        } else if ("TeacherSession".equals(psType)) {
            return CTLConstant.UserIdentity.TEACHER2;
        } else if ("AssistantSession".equals(psType)) {
            return CTLConstant.UserIdentity.ASSISTANT;
        } else if ("RemoteAssistantSession".equals(psType)) {
            return CTLConstant.UserIdentity.REMOTE_ASSISTANT;
        } else if ("StudentSession".equals(psType)) {
            return CTLConstant.UserIdentity.STUDENT;
        } else if ("ManagerSession".equals(psType)) {
            return CTLConstant.UserIdentity.MANAGER;
        } else if ("AuditorSession".equals(psType)) {
            return CTLConstant.UserIdentity.AUDITOR;
        } else if ("AuditorSession".equals(psType)) {
            return CTLConstant.UserIdentity.ADMINISTRATOR;
        } else if ("AdviserSession".equals(psType)) {
            return CTLConstant.UserIdentity.ADVISER;
        } else if ("VisitorSession".equals(psType)) {
            return CTLConstant.UserIdentity.VISITOR;
        }

        return CTLConstant.UserIdentity.NONE;
    }

}
