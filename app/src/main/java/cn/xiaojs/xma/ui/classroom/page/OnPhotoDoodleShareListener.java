package cn.xiaojs.xma.ui.classroom.page;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/3/15
 * Desc:
 *
 * ======================================================================================== */

import android.graphics.Bitmap;

import cn.xiaojs.xma.model.live.Attendee;

public interface OnPhotoDoodleShareListener {
    public void onPhotoShared(Attendee attendee, Bitmap bmp);
}
