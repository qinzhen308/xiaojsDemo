package cn.xiaojs.xma.ui.classroom.main;
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
 * Date:2017/5/12
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;

public class TipsHelper {
    private View mTipView;
    private TextView mTipTitleTv;
    private TextView mTipDescTv;

    private Context mContext;

    public TipsHelper(Context context, View tipRoot) {
        mContext = context;

        mTipView = tipRoot;
        mTipTitleTv = (TextView) tipRoot.findViewById(R.id.tip_title_view);
        mTipDescTv = (TextView) tipRoot.findViewById(R.id.tip_txt_view);
    }

    public void setTips(int resTitleId, int resDescId) {
        setTips(resTitleId <= 0 ? "" : mContext.getString(resTitleId), resDescId <= 0 ? "" : mContext.getString(resDescId));
    }

    public void setTipsByState(String liveSessionState) {
        Constants.User user = LiveCtlSessionManager.getInstance().getUser();
        if (Live.LiveSessionState.SCHEDULED.equals(liveSessionState)) {
            setTips(R.string.cls_not_on_class_title, R.string.cls_not_on_class_desc);
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveSessionState)) {
            if (user == Constants.User.TEACHER) {
                setTips(R.string.cls_pending_class_title, R.string.cls_pending_class_desc);
            } else {
                setTips(R.string.cls_pending_class_stu_title, R.string.cls_pending_class_stu_desc);
            }
        } else if (Live.LiveSessionState.RESET.equals(liveSessionState)) {
            if (user == Constants.User.TEACHER
                    || user == Constants.User.ASSISTANT
                    || user == Constants.User.REMOTE_ASSISTANT) {
                setTips(R.string.cls_break_title, R.string.cls_break_desc_teacher);
            } else {
                setTips(R.string.cls_break_title, R.string.cls_break_desc);
            }
        } else if (Live.LiveSessionState.LIVE.equals(liveSessionState)) {
            hideTips();
        } else if (Live.LiveSessionState.FINISHED.equals(liveSessionState)) {
            setTips(R.string.cls_finish_title, R.string.cls_not_on_class_desc);
        }
    }

    public void setTips(String title, String desc) {
        if (mTipView.getVisibility() != View.VISIBLE) {
            mTipView.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(title)) {
            if (mTipTitleTv.getVisibility() != View.VISIBLE) {
                mTipTitleTv.setVisibility(View.VISIBLE);
            }
            mTipTitleTv.setText(title);
        } else {
            if (mTipTitleTv.getVisibility() != View.GONE) {
                mTipTitleTv.setVisibility(View.GONE);
            }
        }

        if (!TextUtils.isEmpty(desc)) {
            if (mTipDescTv.getVisibility() != View.VISIBLE) {
                mTipDescTv.setVisibility(View.VISIBLE);
            }
            mTipDescTv.setText(desc);
        } else {
            if (mTipDescTv.getVisibility() != View.GONE) {
                mTipDescTv.setVisibility(View.GONE);
            }
        }
    }

    public void hideTips() {
        mTipView.setVisibility(View.GONE);
    }
}
