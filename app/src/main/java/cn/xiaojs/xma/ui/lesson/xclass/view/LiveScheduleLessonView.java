package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.model.live.LiveSchedule;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.LabelImageView;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class LiveScheduleLessonView extends RelativeLayout implements IViewModel<LiveSchedule> {


    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_total_time)
    TextView tvTotalTime;
    @BindView(R.id.tv_lesson)
    TextView tvLesson;
    @BindView(R.id.iv_avatar)
    LabelImageView ivAvatar;
    @BindView(R.id.tv_speaker)
    TextView tvSpeaker;
    @BindView(R.id.iv_avatar1)
    LabelImageView ivAvatar1;
    @BindView(R.id.tv_assistant)
    TextView tvAssistant;
    @BindView(R.id.iv_avatar2)
    LabelImageView ivAvatar2;
    @BindView(R.id.layout_teachers)
    LinearLayout layoutTeachers;
    @BindView(R.id.bottom_line)
    View bottomLine;
    @BindView(R.id.v_line)
    View vLine;
    @BindView(R.id.state_point)
    View statePoint;
    @BindView(R.id.icon_live)
    ProgressBar iconLive;

    LiveSchedule mData;
    @BindView(R.id.tv_state)
    TextView tvState;

    int position;
    @BindView(R.id.btn_replay)
    TextView btnReplay;



    private CircleTransform circleTransform;

    public LiveScheduleLessonView(Context context) {
        super(context);
        init();
    }

    public LiveScheduleLessonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init() {
        inflate(getContext(), R.layout.item_live_schedule_lesson, this);
        ButterKnife.bind(this);
        circleTransform = new CircleTransform(getContext());
    }



    @Override
    public void bindData(int position, LiveSchedule data) {
        mData = data;
        this.position = position;
        tvDate.setText(ScheduleUtil.getHMDate(data.schedule.getStart()));
        tvTotalTime.setText(data.schedule.getDuration() + "分钟");

        tvLesson.setText(data.title);


        if (false && Ctl.LiveLessonState.FINISHED.equals(data.state)) {
            btnReplay.setVisibility(VISIBLE);
        } else {
            btnReplay.setVisibility(GONE);
        }

        tvState.setVisibility(GONE);

        if (Ctl.LiveLessonState.PENDING_FOR_LIVE.equals(data.state)) {
            statePoint.setVisibility(VISIBLE);
            statePoint.setBackgroundResource(R.drawable.shape_orange_point);
            iconLive.setVisibility(GONE);

        } else if (Ctl.LiveLessonState.LIVE.equals(data.state)) {
            statePoint.setVisibility(INVISIBLE);
            iconLive.setVisibility(VISIBLE);
        } else if(Ctl.LiveLessonState.CANCELLED.equals(data.state)){
            statePoint.setVisibility(VISIBLE);
            statePoint.setBackgroundResource(R.drawable.shape_grey_point);
            iconLive.setVisibility(GONE);
            tvState.setText("已取消");
            tvState.setVisibility(VISIBLE);
        }else if(Ctl.LiveLessonState.FINISHED.equals(data.state)){
            statePoint.setVisibility(VISIBLE);
            statePoint.setBackgroundResource(R.drawable.shape_grey_point);
            iconLive.setVisibility(GONE);

        }else {
            statePoint.setVisibility(VISIBLE);
            statePoint.setBackgroundResource(R.drawable.shape_orange_point);
            iconLive.setVisibility(GONE);
        }

        if (data.lead == null&&data.lead.getBasic()!=null&&data.lead.getBasic().getName()!=null) {
            tvSpeaker.setVisibility(INVISIBLE);
            ivAvatar.setVisibility(INVISIBLE);
        } else {
            tvSpeaker.setVisibility(VISIBLE);
            ivAvatar.setVisibility(VISIBLE);
            tvSpeaker.setText(data.lead.getBasic().getName());
            Glide.with(getContext())
                    .load(Account.getAvatar(data.lead.getId(), 300))
                    .bitmapTransform(circleTransform)
                    .placeholder(R.drawable.default_avatar_grey)
                    .error(R.drawable.default_avatar_grey)
                    .into(ivAvatar);
        }

//        showAssistantLogic1();
        showAssistantLogic2();
    }




    //显示1个助教头像和名字，2个以及以上名字换成人数
    private void showAssistantLogic2() {
        layoutTeachers.setVisibility(GONE);
//        if (mData.lead != null && mData.assistants.length > 0) {
//            ivAvatarMore.setVisibility(INVISIBLE);
//            ivAvatar2.setVisibility(INVISIBLE);
//            layoutTeachers.setVisibility(VISIBLE);
//            Glide.with(getContext())
//                    .load(Account.getAvatar(mData.assistants[0].getId(), 300))
//                    .bitmapTransform(circleTransform)
//                    .placeholder(R.drawable.default_avatar_grey)
//                    .error(R.drawable.default_avatar_grey)
//                    .into(ivAvatar1);
//            if (mData.assistants.length == 1) {
//                tvAssistant.setText(mData.assistants[0].name);
//            } else {
//                tvAssistant.setText(mData.assistants.length + "名助教");
//            }
//        } else {
//            layoutTeachers.setVisibility(INVISIBLE);
//        }
    }


    @OnClick(R.id.btn_replay)
    public void replay() {
        if (mEventCallback != null) {
            //目前给教室里面的课表回调出去
            mEventCallback.onEvent(EventCallback.EVENT_1, position, mData);
        } else {
            //其余地方课表的逻辑
        }
    }





    private EventCallback mEventCallback;

    public void setCallback(EventCallback eventCallback) {
        mEventCallback = eventCallback;
    }
}
