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
 * Date:2017/2/9
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.ui.classroom.talk.ContactManager;
import cn.xiaojs.xma.util.Base64;
import cn.xiaojs.xma.util.BitmapUtils;

public class ClassroomBusiness {
    public static final int NETWORK_NONE = 1;
    public static final int NETWORK_WIFI = 2;
    public static final int NETWORK_OTHER = 3;
    private static final String BASE64_JPEG_HEADER = "data:image/jpeg;base64,";


    public static boolean isAdviserSession() {
        String ps = LiveCtlSessionManager.getInstance().getCtlSession().psType;
        if (Constants.User.ADVISER == getUser(ps, Constants.User.STUDENT)) {
            return true;
        }
        return false;
    }

    /**
     * 获取用户身份
     */
    public static Constants.User getUser(String psType, Constants.User defaultUser) {
        Constants.User user = defaultUser;
        if ("LeadSession".equals(psType)) {
            user = Constants.User.LEAD;
        } else if ("TeacherSession".equals(psType)) {
            user = Constants.User.TEACHER2;
        } else if ("AssistantSession".equals(psType)) {
            user = Constants.User.ASSISTANT;
        } else if ("RemoteAssistantSession".equals(psType)) {
            user = Constants.User.REMOTE_ASSISTANT;
        } else if ("StudentSession".equals(psType)) {
            user = Constants.User.STUDENT;
        } else if ("ManagerSession".equals(psType)) {
            user = Constants.User.MANAGER;
        } else if ("AuditorSession".equals(psType)) {
            user = Constants.User.AUDITOR;
        } else if ("AuditorSession".equals(psType)) {
            user = Constants.User.ADMINISTRATOR;
        } else if ("AdviserSession".equals(psType)) {
            user = Constants.User.ADVISER;
        }

        return user;
    }

    //用户场景模式：预览、参与者等模式
    public static Constants.UserMode getUserByCtlSession(CtlSession session) {
        if (session == null) {
            return Constants.UserMode.PARTICIPANT;
        }

        Constants.UserMode mode = Constants.UserMode.PARTICIPANT;
        if (hasTeachingAbility()) {
            mode = Constants.UserMode.TEACHING;
            if (Constants.PREVIEW_MODE == session.mode) {
                mode = Constants.UserMode.PREVIEW;
            } else if (Constants.PARTICIPANT_MODE == session.mode) {
                mode = Constants.UserMode.PARTICIPANT;
            }
        } else {
            //all stu
            mode = Constants.UserMode.PARTICIPANT;
        }

        return mode;
    }

    /**
     * 是否具有教学能力
     */
    public static boolean hasTeachingAbility() {

        if (XiaojsConfig.DEBUG) {
            Logger.d("hasTeachingAbility---------------------");
        }

        Constants.User user = LiveCtlSessionManager.getInstance().getUser();
        Constants.User userInLesson = LiveCtlSessionManager.getInstance().getUserInLesson();
        return user == Constants.User.LEAD
                || user == Constants.User.ASSISTANT
                || user == Constants.User.REMOTE_ASSISTANT
                || userInLesson == Constants.User.LEAD
                || userInLesson == Constants.User.ASSISTANT
                || userInLesson == Constants.User.REMOTE_ASSISTANT;
    }


    /**
     * 是否能个人推流
     */
    public static boolean canIndividualByState(String liveState) {

        if (Live.LiveSessionState.SCHEDULED.equals(liveState) ||
                Live.LiveSessionState.FINISHED.equals(liveState) ||
                Live.LiveSessionState.IDLE.equals(liveState)) {
            return true;
        }
        return false;
    }


    public static <T> T parseSocketBean(Object obj, Class<T> valueType) {
        if (obj == null) {
            return null;
        }

        try {
            String result = null;
            if (obj instanceof JSONObject) {
                result = obj.toString();
            } else if (obj instanceof String) {
                result = (String) obj;
            } else {
                result = obj.toString();
            }

            if (TextUtils.isEmpty(result)) {
                return null;
            }


            if (XiaojsConfig.DEBUG) {
                Logger.d("socket callback: " + result);
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

    public static String getSnapshot(String name, int size) {
        return ApiManager.getFileBucket() + "/" + name + "?imageView2/0/w/" + size;
    }

    /**
     * 得到文件对应在七牛服务器的url
     */
    public static String getFileUrl(String name) {
        return ApiManager.getFileBucket() + "/" + name;
    }

    /**
     * 获得视频在七牛服务器的url
     */
    public static String getVideoUrl(String name, String mimeType) {
        if (Collaboration.isStreaming(mimeType)) {
            return ApiManager.getLiveBucket() + "/" + name + ".m3u8";
        }

        return ApiManager.getFileBucket() + "/" + name;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        byte[] data = BitmapUtils.bmpToByteArray(bitmap, Bitmap.CompressFormat.JPEG, 50, false);
        try {
            return BASE64_JPEG_HEADER + Base64.encode(data);
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap base64ToBitmap(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }

        String imgData = content.substring(BASE64_JPEG_HEADER.length());
        try {
            byte[] data = Base64.decode(imgData);
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] base64ToByteData(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }

        String imgData = content.substring(BASE64_JPEG_HEADER.length());
        try {
            byte[] data = Base64.decode(imgData);
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public static int getCurrentNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo currentInfo = cm.getActiveNetworkInfo();
        if (currentInfo == null || !currentInfo.isConnected()) {
            return NETWORK_NONE;
        } else if (currentInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return NETWORK_WIFI;
        } else {
            return NETWORK_OTHER;
        }
    }

    public static boolean isMyself(Context context, String accountId) {
        String mySelfAccountId = AccountDataManager.getAccountID(context);
        return mySelfAccountId != null && mySelfAccountId.equals(accountId);
    }

    public static long getCountTimeByCtlSession() {
        long countTime = 0;
        CtlSession session = LiveCtlSessionManager.getInstance().getCtlSession();
        if (Live.LiveSessionState.LIVE.equals(session.state)) {
            //autoCountTime = true;
            if (session.ctl != null) {
                countTime = session.ctl.duration * 60 - session.finishOn;
            }

        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(session.state) ||
                Live.LiveSessionState.SCHEDULED.equals(session.state)) {//TODO 是否要加入班的PEND_FOR_LIVE ？
            countTime = session.startOn;
        } else if (Live.LiveSessionState.RESET.equals(session.state)) {
            //autoCountTime = false;
            countTime = session.hasTaken;
        } else if (Live.LiveSessionState.CANCELLED.equals(session.state)) {
            //autoCountTime = false;
            countTime = 0;
        } else if (Live.LiveSessionState.FINISHED.equals(session.state)) {
            countTime = 0;
        } else if (Live.LiveSessionState.DELAY.equals(session.state)) {
            countTime = Math.abs(session.finishOn);
        }

        return countTime;
    }

    public static String getNameByAccountId(String accountId) {
        LiveCollection<Attendee> liveCollection = ContactManager.getInstance().getAttendees();
        if (accountId != null && liveCollection != null && liveCollection.attendees != null) {
            for (Attendee attendee : liveCollection.attendees) {
                if (accountId != null && accountId.equals(attendee.accountId)) {
                    return attendee.name;
                }
            }
        }

        return accountId;
    }
}
