package cn.xiaojs.xma.common.xf_foundation.schemas;

/**
 * Created by maxiaobao on 2017/1/17.
 */

public class Communications {

    public class TalkType {

        public static final int OPEN = 1;  // 课堂公开交流，仅课堂内和录播场景（直播时间段）可见
        public static final int PEER = 2; // 课堂单独交流，仅课堂内可见
        public static final int FACULTY = 3; // 课堂教学组交流
        public static final int DISCUSSION = 4; // 课堂讨论组交流
        public static final int SYSTEM = 10; // 课堂消息
    }
}
