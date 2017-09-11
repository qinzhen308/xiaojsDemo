package cn.xiaojs.xma.common.xf_foundation.schemas;

/**
 * Created by maxiaobao on 2017/2/6.
 */

public class Live {

    /**
     * The type of streaming within a live session.
     */
    public static class StreamType {
        public static final int NONE = -1;
        public static final int LIVE = 1;
        public static final int INDIVIDUAL = 2;
        public static final int DISCUSSION = 3;
        public static final int TALK = 4;
    }

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

    public class LiveSessionState {
        public static final String IDLE = "Idle";
        public static final String SCHEDULED = "Scheduled";
        public static final String PENDING_FOR_JOIN = "PendingForJoin";
        public static final String PENDING_FOR_LIVE = "PendingForLive";
        public static final String LIVE = "Live";
        public static final String DELAY = "Delayed";
        public static final String FINISHED = "Finished";
        public static final String TERMINATED = "Terminated";
        public static final String ABORTED = "Aborted";
        public static final String CANCELLED = "Cancelled";
        public static final String RESET = "Rest";
        public static final String INDIVIDUAL = "Individual";
        public static final String NONE = "none";
    }


    public class MediaStatus {
        public static final int READY = 1;
        public static final int FAILED_DUE_TO_NETWORK_ISSUES = 5;
        public static final int FAILED_DUE_TO_NO_DEVICE = 6;
        public static final int FAILED_DUE_TO_PRIVACY = 7;
        public static final int FAILED_DUE_TO_DENIED = 8;
        public static final int FAILED_DUE_TO_OTHER_ISSUES = 9;

    }

    /**
     * @summary Defines the classroom modes.
     */
    public class ClassroomMode {
        public static final int TEACHING =1;
        public static final int PARTICIPANT = 2;
        public static final int MEDIA = 3;                                // Not supported right now
        public static final int PREVIEW = 4;
    }

    /**
     * value of stg
     */
    public class SyncStage {
        public static final int READY =0;// Tool or shape chosen
        public static final int BEGIN = 1;// Draw or action began
        public static final int ONGOING = 2;// Drawing in progress or being executed
        public static final int FINISH = 3;// Finish or stop action
    }

    /**
     * @summary Defines the board events which will be synchronized during board collaboration.
     */
    public class SyncEvent {
        public static final int SELECT = 1;
        public static final int UNDO = 2;
        public static final int REDO = 3;
        public static final int CLEAR = 4;
        public static final int ERASER = 5;
        public static final int COPY = 6;
        public static final int CUT = 7;
        public static final int PASTE = 8;
        public static final int SHOT = 9;
        public static final int GROUP = 10;
        public static final int UNGROUP = 11;
        public static final int STROKESTYLE = 12;
        public static final int GRANTCONTROL = 13;
        public static final int PAGING = 14;

        public static final int PEN = 20;
        public static final int TEXT = 21;
        public static final int IMAGE = 22;
        public static final int HANDSCALE = 23;
        public static final int GAP = 24;


        public static final int DASHEDLINE = 30;
        public static final int PENTAGON = 31;
        public static final int HEXAGON = 32;
        public static final int LINE = 33;
    }

    /**
     * @summary Defines the states of a board.
     * @readonly
     * @enum {Number}
     */
    public class BoardState {
        public static final String OPEN ="Open";
        public static final String CLOSED = "Closed";
    }

    public class ShapeType {
        public static final String DRAW_CONTINUOUS ="drawContinuous";
        public static final String DRAW_IMAGE ="drawImage";
    }


}
