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
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.ui.classroom.live.StreamType;
import cn.xiaojs.xma.util.TimeUtil;

public class TimeProgressHelper {
    TextView mCountTimeTv;
    TextView mCountDownTimeTv;
    TextView mLiveShowTv;

    private final static int MSG_COUNT_TIME = 1 << 1;
    private final static int MSG_COUNT_DOWN_TIME = 1 << 2;
    private final static int MSG_LIVE_SHOW_COUNT_DOWN_TIME = 1 << 3;

    private final static int TYPE_LIVE_SCHEDULED = 0;
    private final static int TYPE_LIVE_PENDING = 1;
    private final static int TYPE_LIVE_PLAYING = 2;
    private final static int TYPE_LIVE_RESET = 3;
    private final static int TYPE_LIVE_DELAY = 4;
    private final static int TYPE_LIVE_FINISH = 5;

    private long mLessonDuration;
    private long mCountTime = 0;
    private long mCountDownTime = 0;
    private long mLiveShowCountDownTime = 0;
    private long mDelayTime = 0;
    private long mIndividualStreamDuration = 10 * 60; //s 10minute

    private Context mContext;
    private Handler mHandler;

    public TimeProgressHelper(Context context, TextView timeInfoView, View timeStatusBar) {
        mContext = context;

        mCountTimeTv = timeInfoView;
        mCountDownTimeTv = (TextView) timeStatusBar.findViewById(R.id.count_down_time);
        mLiveShowTv = (TextView) timeStatusBar.findViewById(R.id.live_show);
        mLessonDuration = LiveCtlSessionManager.getInstance().getCtlSession().ctl.duration;

        mHandler = new Handler(mContext.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                onHandleMessage(msg);
            }
        };
    }

    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    public void setCountDownTimes(long time, String liveState) {
        setCountDownTimes(time, getTypeByState(liveState));
    }

    public void setCountDownTimes(long time, int type) {
        if (mHandler == null) {
            return;
        }

        mHandler.removeMessages(MSG_COUNT_DOWN_TIME);
        mCountDownTime = time;
        Message msg = mHandler.obtainMessage(MSG_COUNT_DOWN_TIME);
        if (msg == null) {
            msg = new Message();
        }
        msg.what = MSG_COUNT_DOWN_TIME;
        msg.arg1 = type;
        mHandler.sendMessage(msg);
    }

    public void setLiveShowCountDownTime(long time) {
        if (mHandler == null) {
            return;
        }

        mHandler.removeMessages(MSG_LIVE_SHOW_COUNT_DOWN_TIME);
        mLiveShowCountDownTime = time > 0 ? time : 0; //default 10 minute

        mLiveShowTv.setVisibility(View.VISIBLE);

        Message msg = mHandler.obtainMessage(MSG_LIVE_SHOW_COUNT_DOWN_TIME);
        if (msg == null) {
            msg = new Message();
        }
        msg.what = MSG_LIVE_SHOW_COUNT_DOWN_TIME;
        mHandler.sendMessage(msg);
    }

    public void hideLiveShowCountDownTime() {
        if (mHandler == null) {
            return;
        }

        mHandler.removeMessages(MSG_LIVE_SHOW_COUNT_DOWN_TIME);
        mLiveShowTv.setVisibility(View.GONE);
        mCountDownTimeTv.setVisibility(View.VISIBLE);
    }

    public void setCountTime(long time, boolean play) {
        if (mHandler == null) {
            return;
        }

        mHandler.removeMessages(MSG_COUNT_TIME);
        mCountTime = time;

        Message msg = new Message();
        msg.what = MSG_COUNT_TIME;
        msg.arg1 = play ? 1 : 0;
        mHandler.sendMessage(msg);
    }

    private int getTypeByState(String state) {
        if (Live.LiveSessionState.SCHEDULED.equals(state)) {
            return TYPE_LIVE_PENDING;
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(state)) {
            return TYPE_LIVE_PENDING;
        } else if (Live.LiveSessionState.LIVE.equals(state)) {
            return TYPE_LIVE_PLAYING;
        } else if (Live.LiveSessionState.RESET.equals(state)) {
            return TYPE_LIVE_RESET;
        } else if (Live.LiveSessionState.FINISHED.equals(state)) {
            return TYPE_LIVE_FINISH;
        }

        return TYPE_LIVE_PENDING;
    }

    private void onHandleMessage(Message msg) {
        if (msg != null && mHandler != null) {
            Message m = mHandler.obtainMessage();
            String time;
            switch (msg.what) {
                case MSG_COUNT_DOWN_TIME:
                    int type = msg.arg1;
                    mCountDownTimeTv.setVisibility(View.VISIBLE);
                    switch (type) {
                        //上课或者待上课
                        case TYPE_LIVE_PENDING:
                        case TYPE_LIVE_SCHEDULED:
                            mCountDownTime--;
                            time = TimeUtil.formatSecondTime(mCountDownTime);
                            mCountDownTimeTv.setText(mContext.getString(R.string.cls_distance_class, time));
                            mCountDownTimeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_stop, 0, 0, 0);
                            break;
                        //正在上课
                        case TYPE_LIVE_PLAYING:
                            mCountDownTime--;
                            time = TimeUtil.formatSecondTime(mCountDownTime);
                            mCountDownTimeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_start, 0, 0, 0);
                            if (mCountDownTime > 0) {
                                mCountDownTimeTv.setText(time);
                            } else {
                                mCountDownTimeTv.setText(R.string.cls_live_arrived);
                            }

                            m.arg1 = type;
                            m.what = MSG_COUNT_DOWN_TIME;
                            mHandler.sendMessageDelayed(m, 1000);
                            break;
                        case TYPE_LIVE_RESET:
                            //课件休息
                            mCountDownTimeTv.setText(mContext.getString(R.string.cls_break_time_desc, TimeUtil.formatSecondTime(mCountDownTime)));
                            mCountDownTimeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_stop, 0, 0, 0);
                            break;
                        case TYPE_LIVE_DELAY:
                            //拖堂
                            mDelayTime++;
                            time = mContext.getString(R.string.cls_live_delay, TimeUtil.formatSecondTime(mDelayTime));
                            mCountDownTimeTv.setText(mContext.getString(R.string.cls_distance_class, time));
                            mCountDownTimeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_stop, 0, 0, 0);

                            m.arg1 = type;
                            m.what = MSG_COUNT_DOWN_TIME;
                            mHandler.sendMessageDelayed(m, 1000);
                            break;
                        case TYPE_LIVE_FINISH:
                            //完课
                            mCountDownTimeTv.setText(R.string.cls_live_finish);
                            mCountDownTimeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_stop, 0, 0, 0);
                            break;
                    }
                    break;

                case MSG_COUNT_TIME:
                    String total = TimeUtil.formatSecondTime(mLessonDuration * 60);
                    mCountTimeTv.setVisibility(View.VISIBLE);
                    if (msg.arg1 == 1) {
                        mCountTime++;
                        if (mCountTime > mLessonDuration * 60) {
                            mCountTime = mLessonDuration * 60;
                        }
                        String t = TimeUtil.formatSecondTime(mCountTime);
                        mCountTimeTv.setText(t + "/" + total);

                        if (mCountDownTime < mLessonDuration * 60) {
                            m.arg1 = 1;
                            m.what = MSG_COUNT_TIME;
                            mHandler.sendMessageDelayed(m, 1000);
                        }
                    } else if (msg.arg1 == 0) {
                        String t = TimeUtil.formatSecondTime(mCountTime);
                        mCountTimeTv.setText(t + "/" + total);
                    }
                    break;

                case MSG_LIVE_SHOW_COUNT_DOWN_TIME:
                    //live show
                    mLiveShowCountDownTime--;
                    if (mLiveShowCountDownTime > 0) {
                        mLiveShowTv.setVisibility(View.VISIBLE);
                        mCountDownTimeTv.setVisibility(View.GONE);
                        String name = "";
                        time = TimeUtil.formatSecondTime(mLiveShowCountDownTime);
                        mLiveShowTv.setText(mContext.getString(R.string.cls_live_show, name, time));

                        m.what = MSG_LIVE_SHOW_COUNT_DOWN_TIME;
                        mHandler.sendMessageDelayed(m, 1000);
                    } else {
                        mCountDownTimeTv.setVisibility(View.VISIBLE);
                        mLiveShowTv.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }

    public long getLessonDuration() {
        return mLessonDuration;
    }


    public long getCountTime() {
        return mCountTime;
    }

    public long getCountDownTime() {
        return mCountDownTime;
    }

    public long getLiveShowCountDownTime() {
        return mLiveShowCountDownTime;
    }

    public long getDelayTime() {
        return mDelayTime;
    }

    public long getIndividualStreamDuration() {
        return mIndividualStreamDuration;
    }

}
