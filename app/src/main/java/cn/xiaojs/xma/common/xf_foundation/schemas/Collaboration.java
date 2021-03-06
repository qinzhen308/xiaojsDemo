package cn.xiaojs.xma.common.xf_foundation.schemas;

import android.text.TextUtils;

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
        public static final int HAND_HOLD = 5;
        public static final int DRAWING_IN_TALK = 7;
    }

    public class OfficeMimeTypes {
        public static final String DOC = "application/msword";
        public static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        public static final String PPT = "application/vnd.ms-powerpoint";
        public static final String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    }

    public class PictureMimeTypes {
        public static final String ALL = "image/";
        public static final String BMP = "image/bmp";
        public static final String GIF = "image/gif";
        public static final String PNG = "image/png";
        public static final String JPEG = "image/jpeg";
        public static final String JPG = "image/jpeg";
        public static final String TIFF = "image/tiff";
    }

    public class AudioMimeTypes {
        public static final String ALL = "audio/";
        public static final String MP3 = "audio/mpeg";
        public static final String WAV = "audio/x-wav";
    }

    public class VideoMimeTypes {
        public static final String ALL = "video/";
        public static final String MPEG = "video/mpeg";
        public static final String MPG = "video/mpeg";
        public static final String MOV = "video/quicktime";
        public static final String QT = "video/quicktime";
        public static final String AVI = "video/x-msvideo";
        public static final String THREE_GPP = "video/3gpp";
        public static final String MP4 = "video/mp4";
        public static final String OGG = "video/ogg";
    }

    public class StreamingTypes {
        public static final String HLS = "application/x-mpegurl";
    }

    public class ApplicationMimeTypes {
        public static final String PDF = "application/pdf";
    }

    public class SubType{
        public static final String STANDA_LONE_LESSON = "StandaloneLesson";
        public static final String PERSON = "Person";
        public static final String ORGANIZATION = "Organization";
        public static final String PRIVATE_CLASS = "PrivateClass";
        public static final String RECORDED_COURSE = "RecordedCourse";


    }

    public class ShareType{
        public static final String SHORTCUT = "Shortcut";
        public static final String SEND = "Send";
        public static final String COPY = "Copy";
    }

    public class State{
        public static final String INIT = "Init";
        public static final String CONVERTING = "Converting";
        public static final String READY = "Ready";
        public static final String FAULTED = "Faulted";
    }

    public class TypeName{
        public static final String RECORDING_IN_LIBRARY = "RecordingInLibrary";
        public static final String PICTURE_IN_LIBRARY = "PictureInLibrary";
        public static final String MEDIA_IN_LIBRARY = "MediaInLibrary";
        public static final String ALL = "All";
        public static final String PPT_IN_LIBRARY = "PPTInLibrary";
        public static final String PDF_IN_LIBRARY = "PDFInLibrary";
        public static final String WORD_IN_LIBRARY = "WordInLibrary";
        public static final String DIRECTORY_IN_LIBRARY = "DirectoryInLibrary";
    }


    public static boolean isImage(String mimeType) {
        if (TextUtils.isEmpty(mimeType)) return false;

        if (mimeType.equals(PictureMimeTypes.BMP)
                || mimeType.equals(PictureMimeTypes.GIF)
                || mimeType.equals(PictureMimeTypes.JPEG)
                || mimeType.equals(PictureMimeTypes.JPG)
                || mimeType.equals(PictureMimeTypes.PNG)
                || mimeType.equals(PictureMimeTypes.TIFF)) {
            return true;
        }

        return false;

    }

    public static boolean isVideo(String mimeType) {
        if (TextUtils.isEmpty(mimeType)) return false;

        if (mimeType.equals(VideoMimeTypes.AVI)
                || mimeType.equals(VideoMimeTypes.MOV)
                || mimeType.equals(VideoMimeTypes.MP4)
                || mimeType.equals(VideoMimeTypes.MPEG)
                || mimeType.equals(VideoMimeTypes.MPG)
                || mimeType.equals(VideoMimeTypes.OGG)
                || mimeType.equals(VideoMimeTypes.QT)
                || mimeType.equals(VideoMimeTypes.THREE_GPP)) {
            return true;
        }

        return false;

    }

    public static boolean isPPT(String mimeType) {
        if (TextUtils.isEmpty(mimeType)) return false;

        if (mimeType.equals(OfficeMimeTypes.PPT) || mimeType.equals(OfficeMimeTypes.PPTX)) {
            return true;
        }

        return false;

    }
    public static boolean isDoc(String mimeType) {
        if (TextUtils.isEmpty(mimeType)) return false;

        if (mimeType.equals(OfficeMimeTypes.DOC) || mimeType.equals(OfficeMimeTypes.DOCX)) {
            return true;
        }

        return false;

    }

    public static boolean isPDF(String mimeType) {
        if (TextUtils.isEmpty(mimeType)) return false;

        if (mimeType.equals(ApplicationMimeTypes.PDF) ) {
            return true;
        }
        return false;

    }

    public static boolean isStreaming(String mimeType) {
        if (TextUtils.isEmpty(mimeType)) return false;

        if (mimeType.equals(StreamingTypes.HLS)) {
            return true;
        }

        return false;
    }

}
