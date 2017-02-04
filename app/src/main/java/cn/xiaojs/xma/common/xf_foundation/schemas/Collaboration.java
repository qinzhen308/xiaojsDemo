package cn.xiaojs.xma.common.xf_foundation.schemas;

/**
 * Created by maxiaobao on 2017/2/4.
 */

public class Collaboration {

    /**
     * The type of upload tokens for uploading specific type of files on the client onto Qiniu-based XCFS implementation.
     */

    public class UploadTokenType {
        public static final int AVATAR = 1;
        public static final int COVER_OF_CTL = 2;
        public static final int DRAWING_IN_ACTIVITY = 3;
        public static final int DOCUMENT_IN_LIBRARY = 4;

    }

}
