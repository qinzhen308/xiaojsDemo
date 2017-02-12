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

import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

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

    public static <T> T parseSocketBean(Object obj, Class<T> valueType) {
        if (obj == null) {
            return null;
        }

        try {
            String result = null;
            if ((obj instanceof JSONObject)) {
                result = obj.toString();
            } else if (obj instanceof String ){
                result = (String) obj;
            } else {
                result = obj.toString();
            }

            if (TextUtils.isEmpty(result)) {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(result, valueType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject wrapSocketBean(Object obj) {
        JSONObject data = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String sendJson = mapper.writeValueAsString(obj);
            if (sendJson == null) {
                return null;
            }
            data = new JSONObject(sendJson);
        } catch (Exception e) {

        }

        return data;
    }
}
