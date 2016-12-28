package cn.xiaojs.xma.ui.home;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/12/19
 * Desc:
 *
 * ======================================================================================== */

public interface HomeConstant {
    public static final String KEY_COMMENT_TYPE = "key_comment_type";//评论类型
    public static final int COMMENT_TYPE_WRITE = 1;//写评论
    public static final int COMMENT_TYPE_REPLY = 2;//回复评论
    public static final int COMMENT_TYPE_REPLY_REPLY = 3;//回复评论的回复

    public static final String KEY_COMMENT_REPLY_NAME = "key_comment_reply_name";//回复对象的名称

    public static final String KEY_MOMENT_ID = "key_moment_id";
    public static final String KEY_MOMENT_REPLY_ID = "key_moment_reply_id";//评论回复的id

    public static final int REQUEST_CODE_COMMENT = 1;
}
