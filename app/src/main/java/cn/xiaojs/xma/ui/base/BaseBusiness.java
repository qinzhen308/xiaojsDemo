package cn.xiaojs.xma.ui.base;
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

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.SingleSelectDialog;
import cn.xiaojs.xma.util.JpushUtil;
import cn.xiaojs.xma.util.LeanCloudUtil;
import cn.xiaojs.xma.util.StringUtil;

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
        if (TextUtils.isEmpty(state)) {
            return 0;
        }

        int drawable = 0;
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

    public static void advisory(final Context context, boolean followed, String accountId, String name, String sex, final OnFollowListener listener) {
        if (followed) {
            //进入聊天界面
            //JpushUtil.launchChat(context, accountId, name);
            LeanCloudUtil.lanchChatPage(context, accountId,name);
        } else {
            final CommonDialog dialog = new CommonDialog(context);

            dialog.setDesc(context.getString(R.string.none_follow_tip, StringUtil.getTa(sex), StringUtil.getTa(sex)));
            dialog.setOkText(context.getString(R.string.follow_somebody, StringUtil.getTa(sex)));
            dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    dialog.dismiss();
                }
            });
            dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    dialog.dismiss();
                    showFollowDialog(context, listener);
                }
            });

            dialog.show();
        }
    }

    public static void showFollowDialog(Context context, final OnFollowListener listener) {
        SingleSelectDialog dialog = new SingleSelectDialog(context);
        final Map<Long, ContactGroup> group = DataManager.getGroupData(context);
        Iterator<ContactGroup> its = group.values().iterator();
        if (its != null) {
            final String[] items = new String[group.size()];
            int i = 0;
            while (its.hasNext()) {
                ContactGroup cg = its.next();
                items[i] = cg.name;
                i++;
            }
            dialog.setItems(items);
            dialog.setTitle(R.string.add_contact_to);
            dialog.setOnOkClick(new SingleSelectDialog.OnOkClickListener() {
                @Override
                public void onOk(int position) {
                    if (listener != null) {
                        String name = items[position];
                        long groupId = getGroup(group, name);
                        listener.onFollow(groupId);
                    }
                }
            });
            dialog.show();
        }

    }

    private static long getGroup(Map<Long, ContactGroup> group, String name) {
        if (group != null && !TextUtils.isEmpty(name)) {
            for (ContactGroup g : group.values()) {
                if (g.name.equalsIgnoreCase(name)) {
                    return g.group;
                }
            }
        }

        return -1;
    }

    public interface OnFollowListener {
        void onFollow(long group);
    }

}
