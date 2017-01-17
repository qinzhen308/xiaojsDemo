package cn.xiaojs.xma.ui.classroom;
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
 * Date:2016/11/28
 * Desc: 通知消息
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import cn.xiaojs.xma.R;

public class MessagePanel extends Panel {

    public MessagePanel(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_classroom_msg, null);
    }

    @Override
    public void initData() {

    }
}
