package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
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
    @BindView(R.id.tv_state)
    TextView tvState;

    int position;
    @BindView(R.id.btn_replay)
    TextView btnReplay;


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
    public void bindData(int position, CLesson data) {
        mData = data;
        this.position = position;
        tvDate.setText(ScheduleUtil.getHMDate(data.schedule.getStart()));
        tvTotalTime.setText(data.schedule.getDuration() + "分钟");

        tvLesson.setText(data.title);
        if (Account.TypeName.CLASS_LESSON.equals(data.type) && data.classInfo != null) {
            tvClassName.setText(data.classInfo.title);
            tvClassName.setVisibility(VISIBLE);

        } else if (Account.TypeName.STAND_ALONE_LESSON.equals(data.type) && data.enroll != null) {
            tvClassName.setText(data.enroll.current + "人");
            tvClassName.setVisibility(VISIBLE);
        } else {
            tvClassName.setVisibility(INVISIBLE);
        }

        if (Ctl.LiveLessonState.FINISHED.equals(data.state)) {
            btnReplay.setVisibility(VISIBLE);
        }else {
            btnReplay.setVisibility(GONE);
        }


        if (Ctl.LiveLessonState.PENDING_FOR_LIVE.equals(data.state)) {
            statePoint.setVisibility(VISIBLE);
            statePoint.setBackgroundResource(R.drawable.shape_orange_point);
            iconLive.setVisibility(GONE);

        } else if (Ctl.LiveLessonState.LIVE.equals(data.state)) {
            statePoint.setVisibility(INVISIBLE);
            iconLive.setVisibility(VISIBLE);

        } else {
            statePoint.setVisibility(VISIBLE);
            statePoint.setBackgroundResource(R.drawable.shape_grey_point);
            iconLive.setVisibility(GONE);
        }

        if (Ctl.LiveLessonState.PENDING_FOR_ACK.equals(data.state)) {
            tvState.setText("待确认");
            tvState.setVisibility(VISIBLE);
        } else if (Ctl.LiveLessonState.DRAFT.equals(data.state)) {
            tvState.setText("草稿");
            tvState.setVisibility(VISIBLE);
        } else if (Ctl.LiveLessonState.PENDING_FOR_APPROVAL.equals(data.state)) {
            tvState.setText("待审核");
            tvState.setVisibility(VISIBLE);
        } else if (Ctl.LiveLessonState.ACKNOWLEDGED.equals(data.state)) {
            tvState.setText("已确认");
            tvState.setVisibility(VISIBLE);
        } else if (Ctl.LiveLessonState.CANCELLED.equals(data.state)) {
            tvState.setText("已取消");
            tvState.setVisibility(VISIBLE);
        } else if (Ctl.LiveLessonState.REJECTED.equals(data.state)) {
            tvState.setText("已拒绝");
            tvState.setVisibility(VISIBLE);
        } else if (Ctl.LiveLessonState.STOPPED.equals(data.state)) {
            tvState.setText("已停止");
            tvState.setVisibility(VISIBLE);
        } else {
            tvState.setVisibility(GONE);
        }

        if (data.teacher == null) {
            tvSpeaker.setVisibility(INVISIBLE);
            ivAvatar.setVisibility(INVISIBLE);
        } else {
            tvSpeaker.setVisibility(VISIBLE);
            ivAvatar.setVisibility(VISIBLE);
            tvSpeaker.setText(data.teacher.name);
            Glide.with(getContext())
                    .load(Account.getAvatar(data.teacher.getId(), 300))
                    .bitmapTransform(circleTransform)
                    .placeholder(R.drawable.default_avatar_grey)
                    .error(R.drawable.default_avatar_grey)
                    .into(ivAvatar);
        }
        if (data.assistants != null && data.assistants.length > 0) {
            ivAvatarMore.setVisibility(INVISIBLE);
            layoutTeachers.setVisibility(VISIBLE);
            Glide.with(getContext())
                    .load(Account.getAvatar(data.assistants[0].getId(), 300))
                    .bitmapTransform(circleTransform)
                    .placeholder(R.drawable.default_avatar_grey)
                    .error(R.drawable.default_avatar_grey)
                    .into(ivAvatar1);
            if (data.assistants.length == 1) {
                tvAssistant.setVisibility(VISIBLE);
                tvAssistant.setText(data.assistants[0].name);
                ivAvatar2.setVisibility(INVISIBLE);

            } else if (data.assistants.length == 2) {
                tvAssistant.setVisibility(GONE);
                Glide.with(getContext())
                        .load(Account.getAvatar(data.assistants[1].getId(), 300))
                        .bitmapTransform(circleTransform)
                        .placeholder(R.drawable.default_avatar_grey)
                        .error(R.drawable.default_avatar_grey)
                        .into(ivAvatar2);
                ivAvatar2.setVisibility(VISIBLE);
            } else {
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
        } else {
            layoutTeachers.setVisibility(INVISIBLE);
        }

        if(mEventCallback==null){//目前教室的课表会有这个回调
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterClassroom();
                }
            });
        }
    }

    private void enterClassroom(){
        if(Account.TypeName.CLASS_LESSON.equals(mData.type)&&(Ctl.ClassState.IDLE.equals(mData.state)||Ctl.ClassState.LIVE.equals(mData.state)||Ctl.ClassState.PENDING_FOR_LIVE.equals(mData.state))
                ||Account.TypeName.STAND_ALONE_LESSON.equals(mData.type)&&(Ctl.StandaloneLessonState.FINISHED.equals(mData.state)||Ctl.StandaloneLessonState.FINISHED.equals(mData.state)||Ctl.StandaloneLessonState.FINISHED.equals(mData.state))){//公开课能进教室的状态
            Intent i = new Intent();
            i.putExtra(Constants.KEY_TICKET, mData.ticket);
            i.setClass(getContext(), ClassroomActivity.class);
            getContext().startActivity(i);
        }

    }

    @OnClick(R.id.btn_replay)
    public void replay(){
        if(mEventCallback!=null){
            //目前给教室里面的课表回调出去
            mEventCallback.onEvent(EventCallback.EVENT_1,position,mData);
        }else {
            //其余地方课表的逻辑
        }
    }

    @OnClick(R.id.btn_more)
    public void onViewClicked() {
        if (Account.TypeName.STAND_ALONE_LESSON.equals(mData.type)) {//公开课
            List<LOpModel> group1 = new ArrayList<>();
            List<LOpModel> publics = createPublicLessonMode();
            for (int i = 0; i < publics.size(); i++) {
                LOpModel op = publics.get(i);
                if (op.getId() == LOpModel.OP_APPLY) {
                    publics.remove(i--);
                    group1.add(new LOpModel(LOpModel.OP_APPLY));
                } else if (op.getId() == LOpModel.OP_DATABASE1) {
                    publics.remove(i--);
                    group1.add(new LOpModel(LOpModel.OP_DATABASE2));
                } else if (op.getId() == LOpModel.OP_ENTER) {
                    publics.remove(i--);
                    group1.add(new LOpModel(LOpModel.OP_ENTER_2));
                } else if (op.getId() == LOpModel.OP_SHARE) {
                    publics.remove(i--);
                    group1.add(new LOpModel(LOpModel.OP_SHARE2));
                }
            }
            new LessonOperateBoard(getContext()).setOpGroup1(group1).setOpGroup2(publics).maybe((Activity) getContext(), mData, position).show();
        } else {
            new LessonOperateBoard(getContext()).setOpGroup2(classLesson()).maybe((Activity) getContext(), mData, position).show();
        }
    }

    public List<LOpModel> createPublicLessonMode() {
        List<LOpModel> ops;
        if (mData.owner != null && mData.owner.getId().equals(AccountPref.getAccountID(getContext()))) {//我是所有者，无论我是不是讲师
            ops = imOnwer();
        } else if (mData.teacher != null && mData.teacher.getId().equals(AccountPref.getAccountID(getContext()))) {//我虽然不是所有者，但我是讲师
            ops = imSpeaker();
        } else {//我就是个学生
            ops = imStudent();
        }
        return ops;
    }


    //首页班课的操作，对班的操作
    public List<LOpModel> classLesson() {
        List<LOpModel> list = new ArrayList<>();
        list.add(new LOpModel(LOpModel.OP_SCHEDULE));
        list.add(new LOpModel(LOpModel.OP_DATABASE1));
        list.add(new LOpModel(LOpModel.OP_ENTER));
        list.add(new LOpModel(LOpModel.OP_CLASS_INFO));
        return list;
    }

    public List<LOpModel> imOnwer() {
        List<LOpModel> list = new ArrayList<>();

        if (Ctl.StandaloneLessonState.DRAFT.equals(mData.state)) {//草稿
            list.add(new LOpModel(LOpModel.OP_DELETE));
//            list.add(new LOpModel(LOpModel.OP_APPLY));
        } else if (Ctl.StandaloneLessonState.PENDING_FOR_ACK.equals(mData.state)) {//待确认
            list.add(new LOpModel(LOpModel.OP_LOOK));
            list.add(new LOpModel(LOpModel.OP_CANCEL_SUBMIT));
//            list.add(new LOpModel(LOpModel.OP_APPLY));

        } else if (Ctl.StandaloneLessonState.ACKNOWLEDGED.equals(mData.state)) {//已确认

//            list.add(new LOpModel(LOpModel.OP_APPLY));

        } else if (Ctl.StandaloneLessonState.PENDING_FOR_APPROVAL.equals(mData.state)) {//待审核
            list.add(new LOpModel(LOpModel.OP_LOOK));
//            list.add(new LOpModel(LOpModel.OP_APPLY));
            list.add(new LOpModel(LOpModel.OP_CANCEL_CHECK));

        } else if (Ctl.StandaloneLessonState.PENDING_FOR_LIVE.equals(mData.state)) {//待开课
            list.add(new LOpModel(LOpModel.OP_LOOK));
            list.add(new LOpModel(LOpModel.OP_SIGNUP));
            list.add(new LOpModel(LOpModel.OP_MODIFY_TIME));
            list.add(new LOpModel(LOpModel.OP_CANCEL_LESSON));
            list.add(new LOpModel(LOpModel.OP_PUBLIC));
            list.add(new LOpModel(LOpModel.OP_PRIVATE));
            list.add(new LOpModel(LOpModel.OP_APPLY));
            //报名学生名单没有
//            list.add(new LOpModel(LOpModel.OP_));
            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_ENTER));

        } else if (Ctl.StandaloneLessonState.LIVE.equals(mData.state)) {//已开课
            list.add(new LOpModel(LOpModel.OP_LOOK));
            list.add(new LOpModel(LOpModel.OP_SIGNUP));
            list.add(new LOpModel(LOpModel.OP_PUBLIC));
            list.add(new LOpModel(LOpModel.OP_PRIVATE));
            //强行停止 没有
//            list.add(new LOpModel(LOpModel.OP_));

            list.add(new LOpModel(LOpModel.OP_APPLY));
            //报名学生名单没有
//            list.add(new LOpModel(LOpModel.OP_));
            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_ENTER));

        } else if (Ctl.StandaloneLessonState.FINISHED.equals(mData.state)) {//已完课
            list.add(new LOpModel(LOpModel.OP_LOOK));
            list.add(new LOpModel(LOpModel.OP_PUBLIC));
            list.add(new LOpModel(LOpModel.OP_PRIVATE));
            list.add(new LOpModel(LOpModel.OP_RECREATE_LESSON));
            list.add(new LOpModel(LOpModel.OP_APPLY));
            //报名学生名单没有
//            list.add(new LOpModel(LOpModel.OP_));
            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_ENTER));

        } else if (Ctl.StandaloneLessonState.REJECTED.equals(mData.state)) {//审核失败
            list.add(new LOpModel(LOpModel.OP_LOOK));
            list.add(new LOpModel(LOpModel.OP_DELETE));
            list.add(new LOpModel(LOpModel.OP_RECREATE_LESSON));
//            list.add(new LOpModel(LOpModel.OP_APPLY));

        } else if (Ctl.StandaloneLessonState.CANCELLED.equals(mData.state)) {//已取消
            list.add(new LOpModel(LOpModel.OP_LOOK));
            list.add(new LOpModel(LOpModel.OP_DELETE));
//            list.add(new LOpModel(LOpModel.OP_APPLY));
        } else if (Ctl.StandaloneLessonState.STOPPED.equals(mData.state)) {//强制停止
            list.add(new LOpModel(LOpModel.OP_LOOK));
            list.add(new LOpModel(LOpModel.OP_DELETE));
        } else {//失效

        }
        return list;
    }


    public List<LOpModel> imSpeaker() {
        List<LOpModel> list = new ArrayList<>();

        if (Ctl.StandaloneLessonState.DRAFT.equals(mData.state)) {//草稿

        } else if (Ctl.StandaloneLessonState.PENDING_FOR_ACK.equals(mData.state)) {//待确认
            list.add(new LOpModel(LOpModel.OP_AGREE_INVITE));
            list.add(new LOpModel(LOpModel.OP_DISAGREE_INVITE));
//            list.add(new LOpModel(LOpModel.OP_APPLY));

        } else if (Ctl.StandaloneLessonState.ACKNOWLEDGED.equals(mData.state)) {//已确认
//            list.add(new LOpModel(LOpModel.OP_APPLY));

        } else if (Ctl.StandaloneLessonState.PENDING_FOR_APPROVAL.equals(mData.state)) {//待审核
//            list.add(new LOpModel(LOpModel.OP_APPLY));

        } else if (Ctl.StandaloneLessonState.PENDING_FOR_LIVE.equals(mData.state)) {//待开课
            //备课  还没有
//            list.add(new LOpModel(LOpModel.OP_));
            list.add(new LOpModel(LOpModel.OP_ENTER));
            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_APPLY));

        } else if (Ctl.StandaloneLessonState.LIVE.equals(mData.state)) {//已开课
            //备课  还没有
//            list.add(new LOpModel(LOpModel.OP_));
            list.add(new LOpModel(LOpModel.OP_ENTER));
            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_APPLY));
        } else if (Ctl.StandaloneLessonState.FINISHED.equals(mData.state)) {//已完课
            list.add(new LOpModel(LOpModel.OP_ENTER));
            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_APPLY));
        } else if (Ctl.StandaloneLessonState.REJECTED.equals(mData.state)) {//审核失败
//            list.add(new LOpModel(LOpModel.OP_APPLY));

        } else if (Ctl.StandaloneLessonState.CANCELLED.equals(mData.state)) {//已取消
            list.add(new LOpModel(LOpModel.OP_RECREATE_LESSON));
//            list.add(new LOpModel(LOpModel.OP_APPLY));
            list.add(new LOpModel(LOpModel.OP_DELETE));

        } else if (Ctl.StandaloneLessonState.STOPPED.equals(mData.state)) {//强制停止
//            list.add(new LOpModel(LOpModel.OP_));
            list.add(new LOpModel(LOpModel.OP_DELETE));
        } else {//失效

        }
        return list;

    }

    public List<LOpModel> imStudent() {
        List<LOpModel> list = new ArrayList<>();
        if (Ctl.StandaloneLessonState.DRAFT.equals(mData.state)) {//草稿

        } else if (Ctl.StandaloneLessonState.PENDING_FOR_ACK.equals(mData.state)) {//待确认

        } else if (Ctl.StandaloneLessonState.ACKNOWLEDGED.equals(mData.state)) {//已确认

        } else if (Ctl.StandaloneLessonState.PENDING_FOR_APPROVAL.equals(mData.state)) {//待审核

        } else if (Ctl.StandaloneLessonState.PENDING_FOR_LIVE.equals(mData.state)) {//待开课
            list.add(new LOpModel(LOpModel.OP_DATABASE1));
            list.add(new LOpModel(LOpModel.OP_ENTER));
            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_APPLY));
        } else if (Ctl.StandaloneLessonState.LIVE.equals(mData.state)) {//已开课
            list.add(new LOpModel(LOpModel.OP_DATABASE1));
            list.add(new LOpModel(LOpModel.OP_ENTER));
            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_APPLY));

        } else if (Ctl.StandaloneLessonState.FINISHED.equals(mData.state)) {//已完课
            list.add(new LOpModel(LOpModel.OP_DATABASE1));
            list.add(new LOpModel(LOpModel.OP_ENTER));
            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_APPLY));
        } else if (Ctl.StandaloneLessonState.REJECTED.equals(mData.state)) {//审核失败

        } else if (Ctl.StandaloneLessonState.CANCELLED.equals(mData.state)) {//已取消
            list.add(new LOpModel(LOpModel.OP_RECREATE_LESSON));
            list.add(new LOpModel(LOpModel.OP_DELETE));

        } else if (Ctl.StandaloneLessonState.STOPPED.equals(mData.state)) {//强制停止
            list.add(new LOpModel(LOpModel.OP_RECREATE_LESSON));
            list.add(new LOpModel(LOpModel.OP_DELETE));

        } else {//失效

        }
        return list;

    }

    private EventCallback mEventCallback;
    public void setCallback(EventCallback eventCallback){
        mEventCallback=eventCallback;
    }
}
