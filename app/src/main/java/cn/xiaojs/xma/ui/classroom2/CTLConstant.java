package cn.xiaojs.xma.ui.classroom2;

/**
 * Created by maxiaobao on 2017/7/4.
 */

public class CTLConstant {
    public static class StateChannel {
        public static final int NEW_LESSON= 0x001;                //有课
        public static final int NO_LESSON= 0x002;                 //无课

        public static final int START_LESSON= 0x003;              //开始上课
        public static final int FINISH_LESSON= 0x004;             //开始上课

    }

    public static class BaseChannel {
        public static final int START_LESSON = 0x001;             //开始上课
        public static final int FINISH_LESSON = 0x002;            //下课
        public static final int JOIN_AVAILABLE= 0x003;            //进入Pending for jion状态

        public static final int START_LIVE_SHOW = 0x004;          //开始直播秀
        public static final int STOP_LIVE_SHOW = 0x005;           //结束直播秀
        public static final int START_PLAY_LIVE_SHOW = 0x006;     //开始播放直播秀
        public static final int STOP_PLAY_LIVE_SHOW = 0x007;      //结束播放直播秀
    }

    public static class StandloneChannel extends BaseChannel{
        public static final int DELAY_LESSON = 0x1001;             //课拖堂
        public static final int RESET_LESSON = 0x1002;             //课间休息

    }

    public static class NetworkType {
        public static final int TYPE_NONE= -1;                    //没网
        public static final int TYPE_WIFI= 1;                     //Wi-Fi状态
        public static final int TYPE_MOBILE= 2;                   //gprs/2/3/4/5G
    }
}
