package cn.xiaojs.xma.ui.view.sharetemplate;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Paul Z on 2017/11/11.
 */

public class BaseShareTemplateView extends View implements IClassroomTemplate {
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (height / (float) width > 1130f / 750f) {
            height = (int) (width * 1130f / 750f);
        } else {
            width = (int) (height * 750f / 1130f);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public void setTeacherName(String str) {
        techerName = str;
        invalidate();
    }

    @Override
    public void setTeacherAvatar(Bitmap avatar) {
        bmAvatar = avatar;
        invalidate();
    }

    @Override
    public void setTeacherDescrib(String str) {
        techerDescrib = str;
        invalidate();
    }

    @Override
    public void setClassName(String str) {
        className = str;
        invalidate();
    }

    @Override
    public void setQRCodeImage(Bitmap qrCode) {
        if (bmQrCode == qrCode) {
            return;
        }
        bmQrCode = qrCode;
        invalidate();
    }

    public int getQrCodeSize() {
        return (int) (getWidth() * 150f / 750f);
    }
}
