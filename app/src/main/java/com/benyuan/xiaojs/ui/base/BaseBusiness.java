package com.benyuan.xiaojs.ui.base;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:Administrator
 * Date:2016/11/9
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.schemas.Ctl;

public class BaseBusiness {

    public static String getSession() {
        if (XiaojsConfig.mLoginUser == null) {
            return null;
        }

        return XiaojsConfig.mLoginUser.getSessionID();

    }

    public static int getTeachingMode(@NonNull Context context, @NonNull String teachingModeStr) {
        if (TextUtils.isEmpty(teachingModeStr)) {
            return Ctl.TeachingMode.ONE_2_MANY;
        }

        if (teachingModeStr.equals(context.getString(R.string.teach_form_one2many))) {
            return Ctl.TeachingMode.ONE_2_MANY;
        } else if (teachingModeStr.equals(context.getString(R.string.teach_form_one2one))) {
            return Ctl.TeachingMode.ONE_2_ONE;
        } else if (teachingModeStr.equals(context.getString(R.string.teach_form_lecture))) {
            return Ctl.TeachingMode.LECTURE;
        }

        return Ctl.TeachingMode.ONE_2_MANY;
    }
}
