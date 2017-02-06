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

    public class OfficeMimeTypes {
        public static final String DOC = "application/msword";
        public static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        public static final String PPT = "application/vnd.ms-powerpoint";
        public static final String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    }

    public class PictureMimeTypes {
        public static final String BMP = "image/bmp";
        public static final String GIF = "image/gif";
        public static final String PNG = "image/png";
        public static final String JPEG = "image/jpeg";
        public static final String JPG = "image/jpeg";
        public static final String TIFF = "image/tiff";
    }

    public class AudioMimeTypes {
        public static final String MP3 = "audio/mpeg";
        public static final String WAV = "audio/x-wav";
    }

    public class VideoMimeTypes {
        public static final String MPEG = "video/mpeg";
        public static final String MPG = "video/mpeg";
        public static final String MOV = "video/quicktime";
        public static final String QT = "video/quicktime";
        public static final String AVI = "video/x-msvideo";
        public static final String THREE_GPP = "video/3gpp";
        public static final String MP4 = "video/mp4";
        public static final String OGG = "video/ogg";
    }

    public class ApplicationMimeTypes {
        public static final String PDF = "application/pdf";
    }

}
