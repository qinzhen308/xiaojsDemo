package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.LabelImageView;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class HomeLessonView extends RelativeLayout implements IViewModel<CLesson> {


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
    @BindView(R.id.iv_avatar_more)
    TextView ivAvatarMore;
    @BindView(R.id.layout_teachers)
    LinearLayout layoutTeachers;
    @BindView(R.id.btn_more)
    ImageView btnMore;
    @BindView(R.id.tv_class_name)
    TextView tvClassName;
    @BindView(R.id.bottom_line)
    View bottomLine;
    @BindView(R.id.v_line)
    View vLine;
    @BindView(R.id.state_point)
    View statePoint;
    @BindView(R.id.icon_live)
    ProgressBar iconLive;

    CLesson mData;


    private CircleTransform circleTransform;

    public HomeLessonView(Context context) {
        super(context);
        init();
    }

    public HomeLessonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init() {
        inflate(getContext(), R.layout.item_home_lesson, this);
        ButterKnife.bind(this);
        circleTransform = new CircleTransform(getContext());
    }

    //待上课
    public void pendingLiveStatus() {

    }

    //上课中
    public void livingStatus() {

    }

    //草稿
    public void draftStatus() {

    }


    @Override
    public void bindData(CLesson data) {
        mData=data;
        tvDate.setText(ScheduleUtil.getHMDate(data.schedule.getStart()));
        tvTotalTime.setText(data.schedule.getDuration()+"分钟");
        tvLesson.setText(data.title);
        if(data.owner!=null){
            tvClassName.setText(data.owner.name);
            tvClassName.setVisibility(VISIBLE);

        }else {
            tvClassName.setVisibility(INVISIBLE);
        }

        tvSpeaker.setText(data.teacher.name);
        Glide.with(getContext())
                .load(Account.getAvatar(data.teacher.getId(), 300))
                .bitmapTransform(circleTransform)
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(ivAvatar);
        if(data.assistants!=null&&data.assistants.length>0){
            ivAvatarMore.setVisibility(INVISIBLE);
            layoutTeachers.setVisibility(VISIBLE);
            Glide.with(getContext())
                    .load(Account.getAvatar(data.assistants[0].getId(), 300))
                    .bitmapTransform(circleTransform)
                    .placeholder(R.drawable.default_avatar_grey)
                    .error(R.drawable.default_avatar_grey)
                    .into(ivAvatar1);
            if(data.assistants.length==1){
                tvAssistant.setVisibility(VISIBLE);
                tvAssistant.setText(data.assistants[0].name);
                ivAvatar2.setVisibility(INVISIBLE);

            }else if(data.assistants.length==2){
                tvAssistant.setVisibility(GONE);
                Glide.with(getContext())
                        .load(Account.getAvatar(data.assistants[1].getId(), 300))
                        .bitmapTransform(circleTransform)
                        .placeholder(R.drawable.default_avatar_grey)
                        .error(R.drawable.default_avatar_grey)
                        .into(ivAvatar2);
                ivAvatar2.setVisibility(VISIBLE);
            }else {
                tvAssistant.setVisibility(GONE);
                ivAvatar2.setVisibility(VISIBLE);
                Glide.with(getContext())
                        .load(Account.getAvatar(data.assistants[1].getId(), 300))
                        .bitmapTransform(circleTransform)
                        .placeholder(R.drawable.default_avatar_grey)
                        .error(R.drawable.default_avatar_grey)
                        .into(ivAvatar2);
                ivAvatarMore.setVisibility(VISIBLE);
            }
        }else {
            layoutTeachers.setVisibility(INVISIBLE);
        }
    }

    @OnClick(R.id.btn_more)
    public void onViewClicked() {
        new LessonOperateBoard(getContext()).show();
    }



}
