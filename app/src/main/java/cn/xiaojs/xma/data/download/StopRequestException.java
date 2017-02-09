package cn.xiaojs.xma.data.download;

/**
 * Created by maxiaobao on 2017/2/8.
 */

public class StopRequestException extends Exception {
    private final int status;

    public StopRequestException(int status, String message) {
        super(message);
        this.status = status;
    }

    public StopRequestException(int status, Throwable t) {
        this(status, t.getMessage());
        initCause(t);
    }

    public StopRequestException(int status, String message, Throwable t) {
        this(status,message);
        initCause(t);
    }

    public int getStatus() {
        return status;
    }

    public static StopRequestException throwUnhandledHttpError(int code, String message)
            throws StopRequestException {
        final String error = "Unhandled HTTP response: " + code + " " + message;
        if (code >= 400 && code < 600) {
            throw new StopRequestException(code, error);
        } else if (code >= 300 && code < 400) {
            throw new StopRequestException(DownloadInfo.DownloadStatus.STATUS_UNHANDLED_REDIRECT, error);
        } else {
            throw new StopRequestException(DownloadInfo.DownloadStatus.STATUS_UNHANDLED_HTTP_CODE, error);
        }
    }
}
