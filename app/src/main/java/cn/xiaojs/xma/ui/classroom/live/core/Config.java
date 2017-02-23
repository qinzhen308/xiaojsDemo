package cn.xiaojs.xma.ui.classroom.live.core;

import android.content.pm.ActivityInfo;

import cn.xiaojs.xma.R;
import com.qiniu.pili.droid.streaming.StreamingProfile;

/**
 * Created by jerikc on 15/12/8.
 */
public class Config {

    public static final int AUDIO_SAMPLING_RATE = 44100;
    public static final int AUDIO_BITRATE = 48 * 1024;
    public static final int VIDEO_FLUENT_FPS = 24;
    public static final int VIDEO_STANDARD_FPS = 30;
    public static final int VIDEO_FLUENT_BITRATE = 264 * 1024;
    public static final int VIDEO_STANDARD_BITRATE = 512 * 1024;
    public static final int VIDEO_HIGH_BITRATE = 1000 * 1024;
    public static final int VIDEO_MAX_KEY_FRAME_INTERVAL = 48;

    public static final int NORMAL_WIDTH = R.dimen.px160;
    public static final int NORMAL_HEIGHT = R.dimen.px90;
    public static final int SCALED_WIDTH = R.dimen.px266;
    public static final int SCALED_HEIGHT = R.dimen.px200;


    public static final int MAX_PLAYERS = 8;//老师端显示学生视频的最大数量
    public static final boolean DEBUG_MODE = false;
    public static final boolean FILTER_ENABLED = false;
    public static final int ENCODING_LEVEL = StreamingProfile.VIDEO_ENCODING_HEIGHT_240;
    public static final int SCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    public static final String EXTRA_PUBLISH_URL_PREFIX = "URL:";
    public static final String EXTRA_PUBLISH_JSON_PREFIX = "JSON:";

    public static final String VERSION_HINT = "v2.0.4";

    public static final String EXTRA_KEY_PUB_URL = "pub_url";

    public static final String HINT_ENCODING_ORIENTATION_CHANGED =
            "Encoding orientation had been changed. Stop streaming first and restart streaming will take effect";

    public static String pathCfu = "rtmp://pili-live-rtmp.ps.qiniucdn.com/NIU7PS/xiaojiaoshi-test";
    public static String pathHK = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
    public static String pathPush = "rtmp://pili-publish.ps.qiniucdn.com/NIU7PS/xiaojiaoshi-test?key=efdbc36f-8759-44c2-bdd8-873521b6724a";
}
