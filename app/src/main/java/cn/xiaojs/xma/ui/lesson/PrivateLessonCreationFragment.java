package cn.xiaojs.xma.ui.lesson;

import android.view.LayoutInflater;
import android.view.View;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseFragment;

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
 * Date:2016/11/2
 * Desc:
 *
 * ======================================================================================== */

public class PrivateLessonCreationFragment extends BaseFragment {
    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_private_lesson_creation, null);
    }

    @Override
    protected void init() {

    }
}
