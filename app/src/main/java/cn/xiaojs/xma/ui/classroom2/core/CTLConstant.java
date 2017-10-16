package cn.xiaojs.xma.ui.classroom2.core;

/**
 * Created by maxiaobao on 2017/7/4.
 */

public class CTLConstant {

    public static final String ACTION_STREAMING_EXCEPTION = "cn.xiaojs.xma.streaming_exception";
    public static final String EXTRA_EXCEPTION_TIPS = "exception_tips";
    public static final String EXTRA_TICKET = "ticket";

    public static final String EXTRA_DIRECTORY_ID = "dir_id";
    public static final String EXTRA_CONTACT = "econtact";
    public static final String EXTRA_DOC_ID= "edoc_id";
    public static final String EXTRA_DOC= "edoc";
    public static final int REQUEST_MATERIAL_ADD_NEW = 1;
    public static final int REQUEST_CHOOSE_CLASSES = 2;
    public static final int REQUEST_MATERIAL_MOVE = 3;

    public static class StateChannel {
        public static final int NEW_LESSON= 0x001;                   //有课
        public static final int NO_LESSON= 0x002;                    //无课

        public static final int START_LESSON= 0x003;                 //开始上课
        public static final int FINISH_LESSON= 0x004;                //开始上课

    }

    public static class BaseChannel {
        public static final int START_LESSON = 0x001;                //开始上课
        public static final int FINISH_LESSON = 0x002;               //下课
        public static final int JOIN_AVAILABLE= 0x003;               //进入Pending for jion状态

        public static final int START_LIVE_SHOW = 0x004;             //开始直播秀
        public static final int STOP_LIVE_SHOW = 0x005;              //结束直播秀
        public static final int START_PLAY_LIVE_SHOW = 0x006;        //开始播放直播秀
        public static final int STOP_PLAY_LIVE_SHOW = 0x007;         //结束播放直播秀

        public static final int RESUME_LESSON = 0x008;               //恢复上课

        public static final int TRANSITION_STATE = 0x009;            //切换状态
    }

    public static class StandloneChannel extends BaseChannel{
        public static final int DELAY_LESSON = 0x1001;               //课拖堂
        public static final int PAUSE_LESSON = 0x1002;               //课间休息


    }

    public static class ClassChannel extends BaseChannel{
        public static final int ENTER_PENDING_4_LIVE_SATE = 0x200001;//进入peng4live状态
        public static final int ENTER_SCHEDULED = 0x200002;          //进入scheduled状态

    }

    public static class NetworkType {
        public static final int TYPE_NONE= -1;                       //没网
        public static final int TYPE_WIFI= 1;                        //Wi-Fi状态
        public static final int TYPE_MOBILE= 2;                      //gprs/2/3/4/5G
    }

    public static class StreamingType {
        public static final int PLAY_LIVE= 1;                        //播放流
        public static final int PUBLISH_LIVE= 2;                     //直播推流
        public static final int PLAY_PEER_TO_PEER= 3;                //一对一播放流
        public static final int PUBLISH_PEER_TO_PEER= 4;             //一对一推送流
        public static final int PLAY_INDIVIDUAL= 5;                  //播放直播秀流
        public static final int PUBLISH_INDIVIDUAL= 6;               //直播秀推流
        public static final int RECLAIMED_INDIVIDUAL= 7;             //个人推流(回收)
    }


    /**
     * 用户身份
     */
    public enum UserIdentity {
        NONE("None"),                                                //无效身份
        ADVISER("AdviserSession"),                                   //班主任
        LEAD("LeadSession"),                                         //主讲
        TEACHER2("TeacherSession"),                                  //老师
        ASSISTANT("AssistantSession"),                               //助教
        REMOTE_ASSISTANT("RemoteAssistantSession"),                  //远程助教
        STUDENT("StudentSession"),                                   //学生
        MANAGER("ManagerSession"),                                   //监管
        AUDITOR("AuditorSession"),                                   //旁听
        ADMINISTRATOR("AdministrationSession");                      //管理员

        private final String text;

        UserIdentity(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

}
