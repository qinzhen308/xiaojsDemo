package cn.xiaojs.xma.ui.view.sharetemplate;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/11/11.
 */

public interface IClassroomTemplate {

    public void setTeacherName(String str);
    public void setTeacherAvatar(Bitmap avatar);
    public void setTeacherDescrib(String str);
    public void setClassName(String str);
    public void setQRCodeImage(Bitmap qrCode);

}
