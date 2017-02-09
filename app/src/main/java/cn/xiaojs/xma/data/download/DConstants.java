package cn.xiaojs.xma.data.download;

/**
 * Created by maxiaobao on 2017/2/9.
 */

public class DConstants {

    /** The buffer size used to stream the data */
    public static final int BUFFER_SIZE = 8192;

    /**
     * The maximum number of redirects.
     * 最大重定项次数
     */
    public static final int MAX_REDIRECTS = 5; // can't be more than 7.

    /** The minimum amount of progress that has to be done before the progress bar gets updated */
    public static final int MIN_PROGRESS_STEP = 65536;

    /** The minimum amount of time that has to elapse before the progress bar gets updated, in ms */
    public static final long MIN_PROGRESS_TIME = 2000;
}
