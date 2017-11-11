package cn.xiaojs.xma.ui.view.sharetemplate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Paul Z on 2017/11/11.
 */

public class BaseShareTemplateView extends View implements IClassroomTemplate{
    protected String techerName;
    protected String className;
    protected Bitmap bmQrCode;
    protected Bitmap bmAvatar;
    protected String techerDescrib;



    public BaseShareTemplateView(Context context) {
        super(context);
    }

    public BaseShareTemplateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseShareTemplateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setTeacherName(String str) {
        techerName=str;
    }

    @Override
    public void setTeacherAvatar(Bitmap avatar) {
        bmAvatar=avatar;
    }

    @Override
    public void setTeacherDescrib(String str) {
        techerDescrib=str;
    }

    @Override
    public void setClassName(String str) {
        className=str;
    }

    @Override
    public void setQRCodeImage(Bitmap qrCode) {
        bmQrCode=qrCode;

    }
}
