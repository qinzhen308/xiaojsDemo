package cn.xiaojs.xma.data.download;

/**
 * Created by maxiaobao on 2017/2/9.
 */

public class DConstants {

    public static final String LOG_TAG = "download_log";

    /** 获取不到下载更新的安装包的名称时，默认使用的安装包名称 */
    public static final String DOWNLOAD_CLIENT_FILE_NAME = "xiaojs_xma.apk";



    /** The buffer size used to stream the data */
    public static final int BUFFER_SIZE = 8192;

    /**
     * The maximum number of redirects.
     * 最大重定项次数
     */
    public static final int MAX_REDIRECTS = 5; // can't be more than 7.

    /** The minimum amount of progress that has to be done before the progress bar gets updated */
    public static final int MIN_PROGRESS_STEP = 5536;//65536

    /** The minimum amount of time that has to elapse before the progress bar gets updated, in ms */
    public static final long MIN_PROGRESS_TIME = 500;//2000

    /**
     * The number of times that the download manager will retry its network
     * operations when no progress is happening before it gives up.
     */
    public static final int MAX_RETRIES = 5;


    /** update xiaojs client download notify id*/
    public static final int UPDATE_NOTIFY_ID = 0x1000;

    public static final String EXTRA_URL = "url";
}
