package cn.xiaojs.xma.ui.classroom2.chat;

/**
 * Created by maxiaobao on 2017/9/28.
 */

public class MessageType {
    public static final int SEND_OUT = 0;              //发出的消息
    public static final int RECEIVED = 1;              //收到的消息
    public static final int TIPS = 2;                  //提示消息
    public static final int LAND = 3;                  //横屏模式下消息
    public static final int SYSTEM = 4;                  //系统消息
    public static final int FOLLOWED = 5;                  //被关注了
    public static final int JOIN_APPLY = 6;                  //加入教室申请



    public static class TypeName{
        public static final String SYSTEM = "System";
    }
}
