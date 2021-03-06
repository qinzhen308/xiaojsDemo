package cn.xiaojs.xma.ui.widget;
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

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;

import butterknife.BindView;
import cn.xiaojs.xma.R;

import butterknife.ButterKnife;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.ctl.RoomData;
import cn.xiaojs.xma.ui.view.AnimationView;
import cn.xiaojs.xma.util.TimeUtil;

public class LiveProgress extends LinearLayout {

    @BindView(R.id.live_progress_cur_time)
    TextView curTime;
    @BindView(R.id.live_progress_total_time)
    TextView totalTime;
    @BindView(R.id.live_progress)
    ProgressBar progress;

    @BindView(R.id.animation)
    AnimationView animation;
    @BindView(R.id.live_progress_cur_state)
    TextView state;


    public LiveProgress(Context context) {
        super(context);
        init();
    }

    public LiveProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public LiveProgress(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        inflate(getContext(), R.layout.layout_live_progress, this);
        ButterKnife.bind(this);
    }


    public void animation(boolean enable) {
        if (enable) {
            animation.setVisibility(VISIBLE);
            state.setVisibility(VISIBLE);
        } else {
            animation.setVisibility(GONE);
            state.setVisibility(GONE);
        }
    }

    private void showLiveStateAndTime(String liveState, long duration, long finishOn) {


    }

    public void showTimeBar(RoomData data, long duration) {
        if (data == null) {
            return;
        }

        String liveState = data.liveState;
        long finishOn = data.finishOn;
        long cur = duration * 60 - finishOn;
        long total = duration * 60;

        if (Live.LiveSessionState.DELAY.equals(liveState)) {
            long delat = cur - (duration * 60);
            state.setText("已超时" + (delat / 60) + "分钟");

            String timestr = TimeUtil.formatSecondTime(cur);
            curTime.setText(timestr);
            totalTime.setText(timestr);
            progress.setProgress(100);
            return;

        } else if (Live.LiveSessionState.RESET.equals(liveState)) {
            state.setText("课间休息");
            cur = data.hasTaken;
            curTime.setText(TimeUtil.formatSecondTime(data.hasTaken));
            totalTime.setText(TimeUtil.formatSecondTime(total));
            int pro = (int) (((float) cur / total) * 100);
            progress.setProgress(pro);
        } else if (Live.LiveSessionState.LIVE.equals(liveState)){
            state.setText("上课中");
            cur = total - finishOn;
            curTime.setText(TimeUtil.formatSecondTime(cur));
            totalTime.setText(TimeUtil.formatSecondTime(total));
            int pro = (int) (((float) cur / total) * 100);
            progress.setProgress(pro);
        }
    }
}
