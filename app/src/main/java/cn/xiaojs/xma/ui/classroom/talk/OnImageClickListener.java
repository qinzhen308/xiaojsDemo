package cn.xiaojs.xma.ui.classroom.talk;
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
 * Date:2017/3/17
 * Desc:
 *
 * ======================================================================================== */

public interface OnImageClickListener {
    /**
     * base64对应的图片
     */
    public final static int IMG_FROM_BASE64 = 1;
    /**
     * 七牛对应的图片key
     */
    public final static int IMG_FROM_QINIU = 2;

    public void onImageClick(int type, String key);
}
