package cn.xiaojs.xma.common.xf_foundation;

/**
 * Created by maxiaobao on 2016/12/4.
 */

public class Su {

    public class Permission {

        //region Competency | 教学能力

        // 教学能力权限
        public static final int COMPETENCY = 13;
        // 申明教学能力
        public static final int COMPETENCY_CLAIM = 1301;

        ////////////////////////////////////////////////////////////////////////////////////////////

        //region Course | 课程

        // 课程权限
        public static final int COURSE = 14;
        // 开课（单次直播课程）
        public static final int COURSE_OPEN = 1401;
        // 创建课程
        public static final int COURSE_OPEN_CREATE = 140101;
    }


    public class EventCategory {
        //Server-side in-class events (emitted by XLS-Live runtime)
        public static final int LIVE = 6000;
        //Class-side in-class events (emitted by classroom clients)
        public static final int CLASSROOM = 6001;
    }

    public class EventType {//参考su.js
        //教室交流
        public static final int TALK = 690001;
        //绘制
        public static final int DRAW = 680001;
        //同步绘制
        public static final int SYNC_DRAW = 680002;

        // Live/Classroom events
        public static final int SAVE_MEDIA_DEVICES = 670005;
        public static final int SET_PRIMARY = 610001;
        public static final int STREAMING_STARTED = 670010;
        public static final int STREAMING_STOPPED = 670011;

        // Live/Classroom events (StartMedia, StartStreaming)
        public static final int OPEN_MEDIA = 670001;
        // Live/Classroom events (StopStreaming, StopMedia)
        public static final int CLOSE_MEDIA = 670002;


        //进入教室
        public static final int JOIN = 660000;
        //离开教室
        public static final int LEAVE = 660001;

        //同步状态
        public static final int SYNC_STATE = 640000;
        //班同步状态
        public static final int SYNC_CLASS_STATE = 640005;

        public static final int PRoVISION_FEEDBACK = 630002;
        public static final int CLAIM_STREAMING = 670012;
        public static final int MEDIA_FEEDBACK = 670018;
        public static final int MEDIA_ABORTED = 670019;

        //教室踢人事件
        public static final int KICKOUT_DUE_TO_NEW_CONNECTION = 600108;
        public static final int STREAM_RECLAIMED = 600110;
        public static final int STOP_STREAM_BY_EXPIRATION = 600111;

        public static final int REFRESH_MEDIA_DEVICES = 670005;
        public static final int CLASS_MODE_SWITCH = 670021;
        public static final int CLOSE_PREVIEW_BY_CLASS_OVER = 600112;
        public static final int PLAYBACK_SAVED = 630005;


        public static final int REMIND_FINALIZATION = 600114;
        public static final int REFRESH_STREAMING_QUALITY = 670025;

    }


    public static String getEventSignature(int eventCategory, int eventType) {
        return eventCategory + ":" + eventType;
    }

}
