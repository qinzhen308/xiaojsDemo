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
import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.LessonState;
import com.benyuan.xiaojs.common.xf_foundation.schemas.Ctl;

import java.text.DecimalFormat;

public class BaseBusiness {
    public final static DecimalFormat mPriceDecimalFormat = new DecimalFormat("0.00");
    public final static DecimalFormat mDiscountDecimalFormat = new DecimalFormat("0.0");
    public final static String MONEY = "￥";

    public static String getSession() {
        if (XiaojsConfig.mLoginUser == null) {
            return null;
        }

        return XiaojsConfig.mLoginUser.getSessionID();

    }

    public static String getUserId() {
        if (XiaojsConfig.mLoginUser == null) {
            return null;
        }

        return XiaojsConfig.mLoginUser.getId();

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

    public static String getTeachingMode(@NonNull Context context, @NonNull int teachingMode) {
        String str = context.getString(R.string.teach_form_one2many);
        switch (teachingMode) {
            case Ctl.TeachingMode.ONE_2_MANY:
                str = context.getString(R.string.teach_form_one2many);
                break;
            case Ctl.TeachingMode.ONE_2_ONE:
                str = context.getString(R.string.teach_form_one2one);
                break;
            case Ctl.TeachingMode.LECTURE:
                str = context.getString(R.string.teach_form_lecture);
                break;
        }

        return str;
    }

    public static int getLessonStatusDrawable(String state) {
        int drawable = -1;
        if (state.equalsIgnoreCase(LessonState.DRAFT)) {
            drawable = R.drawable.course_state_draft_bg;
        } else if (state.equalsIgnoreCase(LessonState.REJECTED)) {
            drawable = R.drawable.course_state_failure_bg;
        } else if (state.equalsIgnoreCase(LessonState.CANCELLED)) {
            drawable = R.drawable.course_state_cancel_bg;
        } else if (state.equalsIgnoreCase(LessonState.STOPPED)) {
            drawable = R.drawable.course_state_stop_bg;
        } else if (state.equalsIgnoreCase(LessonState.PENDING_FOR_APPROVAL)) {
            drawable = R.drawable.course_state_examine_bg;
        } else if (state.equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)) {
            drawable = R.drawable.course_state_wait_bg;
        } else if (state.equalsIgnoreCase(LessonState.LIVE)) {
            drawable = R.drawable.course_state_on_bg;
        } else if (state.equalsIgnoreCase(LessonState.FINISHED)) {
            drawable = R.drawable.course_state_end_bg;
        }

        return drawable;
    }

    public static int getLessonStatusTextResId(String state) {
        if (TextUtils.isEmpty(state)) {
            return R.string.pending_shelves;
        }

        int status = R.string.pending_shelves;
        if (state.equalsIgnoreCase(LessonState.DRAFT)) {
            status = R.string.pending_shelves;
        } else if (state.equalsIgnoreCase(LessonState.REJECTED)) {
            status = R.string.examine_failure;
        } else if (state.equalsIgnoreCase(LessonState.CANCELLED)) {
            status = R.string.course_state_cancel;
        } else if (state.equalsIgnoreCase(LessonState.STOPPED)) {
            status = R.string.force_stop;
        } else if (state.equalsIgnoreCase(LessonState.PENDING_FOR_APPROVAL)) {
            status = R.string.examining;
        } else if (state.equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)) {
            status = R.string.pending_for_course;
        } else if (state.equalsIgnoreCase(LessonState.LIVE)) {
            status = R.string.living;
        } else if (state.equalsIgnoreCase(LessonState.FINISHED)) {
            status = R.string.course_state_end;
        }

        return status;
    }

    public static String getLessonStatusText(Context context, String state) {
        if (TextUtils.isEmpty(state)) {
            return "";
        }

        return context.getString(getLessonStatusTextResId(state));
    }


    public static String formatPrice(float price) {
        return mPriceDecimalFormat.format(price);
    }

    public static String formatPrice(double price) {
        return mPriceDecimalFormat.format(price);
    }

    public static String formatPrice(float price, boolean withPrefix) {
        if (withPrefix) {
            return MONEY + mPriceDecimalFormat.format(price);
        }

        return mPriceDecimalFormat.format(price);
    }

    public static String formatPrice(double price, boolean withPrefix) {
        if (withPrefix) {
            return MONEY + mPriceDecimalFormat.format(price);
        }

        return mPriceDecimalFormat.format(price);
    }

    public static String formatDiscount(float price) {
        return mDiscountDecimalFormat.format(price);
    }

}
