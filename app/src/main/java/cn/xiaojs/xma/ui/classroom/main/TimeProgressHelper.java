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
import android.view.View;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.util.TimeUtil;

public class TimeProgressHelper {
    TextView mTitleBarTimeInfoTv;
    TextView mFullScreenTimeInfoTv;
    TextView mLiveShowTv;

    private final static int MSG_COUNT_TIME = 1 << 1;

    private final static int TYPE_LIVE_SCHEDULED = 0;
    private final static int TYPE_LIVE_PENDING = 1;
    private final static int TYPE_LIVE_PLAYING = 2;
    private final static int TYPE_LIVE_RESET = 3;
    private final static int TYPE_LIVE_DELAY = 4;
    private final static int TYPE_LIVE_FINISH = 5;
    private final static int TYPE_LIVE_INDIVIDUAL = 6;

    private long mLessonDuration;
    private long mCountTime = 0;
    private long mIndividualStreamDuration = 0;

    private Context mContext;
    private Handler mHandler;
    private String mOriginStreamState;

    public TimeProgressHelper(Context context, TextView timeInfoView, View timeStatusBar) {
        mContext = context;

        mTitleBarTimeInfoTv = timeInfoView;
        mFullScreenTimeInfoTv = (TextView) timeStatusBar.findViewById(R.id.count_down_time);
        mLiveShowTv = (TextView) timeStatusBar.findViewById(R.id.live_show);
        CtlSession session = LiveCtlSessionManager.getInstance().getCtlSession();
        //TODO get duration
        mLessonDuration = session.ctl != null ? session.ctl.duration : 0;

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

    public void setTimeProgress(long countTime, String liveState) {
        setTimeProgress(countTime, 0, liveState, null, null, true);
    }

    public void setTimeProgress(long countTime, String liveState, boolean play) {
        setTimeProgress(countTime, 0, liveState, null, null, play);
    }

    public void setTimeProgress(long countTime, long individualDuration, String liveState, Object extra, boolean play) {
        setTimeProgress(countTime, individualDuration, liveState, null, null, play);
    }

    public void setTimeProgress(long countTime, long individualDuration, String liveState, Object extra, String originState, boolean play) {
        if (mHandler == null) {
            return;
        }

        int type = getTypeByState(liveState, individualDuration);
        mLiveShowTv.setVisibility(type == TYPE_LIVE_INDIVIDUAL ? View.VISIBLE : View.GONE);
        mHandler.removeCallbacksAndMessages(null);

        mOriginStreamState = originState;
        mCountTime = countTime;
        mIndividualStreamDuration = individualDuration;

        Message msg = mHandler.obtainMessage(MSG_COUNT_TIME);
        if (msg == null) {
            msg = new Message();
        }
        msg.what = MSG_COUNT_TIME;
        msg.arg1 = type;
        msg.arg2 = play ? 1 : 0;
        msg.obj = extra;
        mHandler.sendMessage(msg);
    }

    private int getTypeByState(String state, long individualDuration) {
        if (individualDuration > 0) {
            return TYPE_LIVE_INDIVIDUAL;
        }

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
        } else if (Live.LiveSessionState.DELAY.equals(state)) {
            return TYPE_LIVE_DELAY;
        } else if (Live.LiveSessionState.INDIVIDUAL.equals(state)) {
            return TYPE_LIVE_INDIVIDUAL;
        }

        return TYPE_LIVE_PENDING;
    }

    private void onHandleMessage(Message msg) {
        if (msg != null && mHandler != null) {
            Message m = mHandler.obtainMessage(MSG_COUNT_TIME);
            m.what = msg.what;
            m.arg1 = msg.arg1;
            m.arg2 = msg.arg2;
            m.obj = msg.obj;

            String time;
            String simpleTime = TimeUtil.formatSecondTime(mCountTime);
            String total = TimeUtil.formatSecondTime(mLessonDuration * 60);
            switch (msg.what) {
                case MSG_COUNT_TIME:
                    int type = msg.arg1;
                    mFullScreenTimeInfoTv.setVisibility(View.VISIBLE);
                    switch (type) {
                        //上课或者待上课
                        case TYPE_LIVE_PENDING:
                        case TYPE_LIVE_SCHEDULED:
                            mCountTime--;
                            simpleTime = TimeUtil.formatSecondTime(0);
                            time = TimeUtil.formatSecondTime(mCountTime);
                            if (mCountTime > 0) {
                                mFullScreenTimeInfoTv.setText(mContext.getString(R.string.cls_distance_class, time));
                                mHandler.sendMessageDelayed(m, 1000);
                            } else {
                                mFullScreenTimeInfoTv.setText(mContext.getString(R.string.cls_live_arrived));
                            }
                            mTitleBarTimeInfoTv.setText(simpleTime + "/" + total);
                            mFullScreenTimeInfoTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_stop, 0, 0, 0);
                            break;
                        //正在上课
                        case TYPE_LIVE_PLAYING:
                            if (msg.arg2 == 1) {
                                mCountTime++;
                                if (mCountTime > mLessonDuration * 60) {
                                    mCountTime = mLessonDuration * 60;
                                }
                                simpleTime = TimeUtil.formatSecondTime(mCountTime);
                                mTitleBarTimeInfoTv.setText(simpleTime + "/" + total);
                                mFullScreenTimeInfoTv.setText(simpleTime);
                                mFullScreenTimeInfoTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_start, 0, 0, 0);
                                if (mCountTime < mLessonDuration * 60) {
                                    mHandler.sendMessageDelayed(m, 1000);
                                }
                            } else if (msg.arg2 == 0) {
                                simpleTime = TimeUtil.formatSecondTime(mCountTime);
                                mTitleBarTimeInfoTv.setText(simpleTime + "/" + total);
                                mFullScreenTimeInfoTv.setText(simpleTime);
                            }
                            break;
                        case TYPE_LIVE_RESET:
                            //课件休息
                            simpleTime = TimeUtil.formatSecondTime(mCountTime);
                            time = mContext.getString(R.string.cls_break_time_desc, simpleTime);
                            mTitleBarTimeInfoTv.setText(simpleTime + "/" + total);
                            mFullScreenTimeInfoTv.setText(time);
                            mFullScreenTimeInfoTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_stop, 0, 0, 0);
                            break;
                        case TYPE_LIVE_DELAY:
                            //拖堂
                            mCountTime++;
                            simpleTime = TimeUtil.formatSecondTime(mCountTime);
                            time = mContext.getString(R.string.cls_live_delay, simpleTime);
                            mTitleBarTimeInfoTv.setText(total + "/" + total);
                            mFullScreenTimeInfoTv.setText(time);
                            mFullScreenTimeInfoTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_stop, 0, 0, 0);

                            mHandler.sendMessageDelayed(m, 1000);
                            break;
                        case TYPE_LIVE_FINISH:
                            //完课
                            mTitleBarTimeInfoTv.setText(R.string.cls_live_finish);
                            mFullScreenTimeInfoTv.setText(R.string.cls_live_finish);
                            mFullScreenTimeInfoTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_stop, 0, 0, 0);
                            break;
                        case TYPE_LIVE_INDIVIDUAL:
                            //live show
                            mIndividualStreamDuration--;
                            if (mIndividualStreamDuration > 0) {
                                mLiveShowTv.setVisibility(View.VISIBLE);
                                mFullScreenTimeInfoTv.setVisibility(View.GONE);
                                String name = "";
                                time = TimeUtil.formatSecondTime(mIndividualStreamDuration);
                                mLiveShowTv.setText(mContext.getString(R.string.cls_live_show, name, time));

                                mHandler.sendMessageDelayed(m, 1000);
                            } else {
                                mFullScreenTimeInfoTv.setVisibility(View.VISIBLE);
                                mLiveShowTv.setVisibility(View.GONE);
                            }
                            if (Live.LiveSessionState.FINISHED.equals(mOriginStreamState)) {
                                mTitleBarTimeInfoTv.setText(R.string.cls_live_finish);
                            } else {
                                simpleTime = TimeUtil.formatSecondTime(0);
                                mTitleBarTimeInfoTv.setText(simpleTime + "/" + total);
                            }
                            break;
                    }
                    break;
            }
        }
    }

    public long getCountTime() {
        return mCountTime;
    }

    public long getIndividualStreamDuration() {
        return mIndividualStreamDuration;
    }
}
