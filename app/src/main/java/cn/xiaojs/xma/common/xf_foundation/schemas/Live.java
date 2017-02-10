package cn.xiaojs.xma.common.xf_foundation.schemas;

/**
 * Created by maxiaobao on 2017/2/6.
 */

public class Live {

    //
    // Defines the board types.
    //
    public class BoardType {
        public static final int NOT_SPECIFIED = 0;
        // Whiteboard
        public static final int WHITE = 1;
        // A series of slides
        public static final int SLIDES = 2;
        // Show a media file, e.g. a recorded video
        public static final int MEDIA = 3;
        // Show a test
        public static final int TEST = 4;
        // A sharing or collaboration board created by another participant
        public static final int REMOTING = 5;
        // A split board to contain reflection of other boards
        public static final int SPLIT = 10;

    }


    public class StreamMode {
        // No need to start streaming
        public static final int NO = 0;
        // Enabled both Video & Audio
        public static final int AV = 1;
        // Enabled audio only
        public static final int AUDIO_ONLY = 2;
        // Enabled video but filtered out audio channels
        public static final int MUTE = 3;
        // Turned off both audio & video and streaming board only, effective on XWC only
        public static final int BOARD_ONLY = 4;
    }

}
