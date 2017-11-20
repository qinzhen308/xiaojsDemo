package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.view.TextInBgSpan;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.Common3Dialog;
import cn.xiaojs.xma.ui.widget.LabelImageView;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.MaterialUtil;

/**
 * Created by Paul Z on 2017/11/1.
 */

public class MyScheduleView extends RelativeLayout implements IViewModel<CLesson> {


    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_total_time)
    TextView tvTotalTime;
    @BindView(R.id.tv_lesson)
    TextView tvLesson;
    @BindView(R.id.iv_avatar)
    LabelImageView ivAvatar;
    @BindView(R.id.iv_avatar1)
    LabelImageView ivAvatar1;
    @BindView(R.id.btn_more)
    ImageView btnMore;
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

//    @BindView(R.id.state_label)
//    TextView stateLabel;

    int position;

    boolean fromSchedule;
    boolean fromHome;


    private CircleTransform circleTransform;

    public MyScheduleView(Context context) {
        super(context);
        init();
    }

    public MyScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init() {
        inflate(getContext(), R.layout.item_my_schedule, this);
        ButterKnife.bind(this);
        circleTransform = new CircleTransform(getContext());
        if (getContext() instanceof MainActivity) {
            fromHome = true;
        }
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

    //是不是课表里面的课
    public void setIsFromSchedule(boolean fromSchedule) {
        this.fromSchedule = fromSchedule;
    }


    @Override
    public void bindData(int position, CLesson data) {
        mData = data;
        this.position = position;
        tvDate.setText(ScheduleUtil.getHMDate(data.schedule.getStart()));
        tvTotalTime.setText(data.schedule.getDuration() + "分钟");

        if (!TextUtils.isEmpty(data.playback)) {
            SpannableString ss = new SpannableString(" 回放  " + data.title);
            ss.setSpan(new TextInBgSpan(getResources().getDrawable(R.drawable.bg_orange_rect), getResources().getColor(R.color.white)), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvLesson.setText(ss);
        } else {
            tvLesson.setText(data.title);
        }

        showOperateBtn();

        if (Account.TypeName.CLASS_LESSON.equals(data.type) && data.classInfo != null) {
            tvState.setVisibility(GONE);
            //stateLabel.setVisibility(GONE);

            if (Ctl.LiveLessonState.PENDING_FOR_LIVE.equals(data.state)) {
                statePoint.setVisibility(VISIBLE);
                statePoint.setBackgroundResource(R.drawable.shape_orange_point);
                iconLive.setVisibility(GONE);

            } else if (Ctl.LiveLessonState.LIVE.equals(data.state)) {
                statePoint.setVisibility(INVISIBLE);
                iconLive.setVisibility(VISIBLE);
            } else if (Ctl.LiveLessonState.CANCELLED.equals(data.state)) {
                statePoint.setVisibility(VISIBLE);
                statePoint.setBackgroundResource(R.drawable.shape_grey_point);
                iconLive.setVisibility(GONE);
                tvState.setText("已取消");
                tvState.setVisibility(VISIBLE);
            } else if (Ctl.LiveLessonState.FINISHED.equals(data.state)) {
                statePoint.setVisibility(VISIBLE);
                statePoint.setBackgroundResource(R.drawable.shape_grey_point);
                iconLive.setVisibility(GONE);

            } else {
                statePoint.setVisibility(VISIBLE);
                statePoint.setBackgroundResource(R.drawable.shape_orange_point);
                iconLive.setVisibility(GONE);
            }

        } else {
            if (Ctl.LiveLessonState.PENDING_FOR_LIVE.equals(data.state)) {
                statePoint.setVisibility(VISIBLE);
                statePoint.setBackgroundResource(R.drawable.shape_orange_point);
                if (fromHome) {
                    //stateLabel.setVisibility(VISIBLE);
                    //stateLabel.setBackgroundResource(R.drawable.class_live_attend);
                }
                iconLive.setVisibility(GONE);

            } else if (Ctl.LiveLessonState.LIVE.equals(data.state)) {
                statePoint.setVisibility(INVISIBLE);
                iconLive.setVisibility(VISIBLE);
                if (fromHome) {
                    //stateLabel.setVisibility(GONE);
                }
            } else {
                statePoint.setVisibility(VISIBLE);
                statePoint.setBackgroundResource(R.drawable.shape_grey_point);
                if (fromHome) {
                    //stateLabel.setVisibility(VISIBLE);
                    //stateLabel.setBackgroundResource(R.drawable.class_live_finished);
                }
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
        }

        if (data.teacher == null) {
            ivAvatar.setVisibility(INVISIBLE);
        } else {
            ivAvatar.setVisibility(VISIBLE);
            Glide.with(getContext())
                    .load(Account.getAvatar(data.teacher.getId(), 300))
                    .bitmapTransform(circleTransform)
                    .placeholder(R.drawable.default_avatar_grey)
                    .error(R.drawable.default_avatar_grey)
                    .into(ivAvatar);
        }

//        showAssistantLogic1();
        showAssistantLogic2();

    }

    private void showOperateBtn() {
        btnMore.setVisibility(INVISIBLE);
    }

    private boolean isAdviser() {
        if (mData.classInfo == null || mData.classInfo.advisers == null) return false;
        String id = AccountPref.getAccountID(getContext());
        for (cn.xiaojs.xma.model.account.Account ad : mData.classInfo.advisers) {
            if (id.equals(ad.getId())) return true;
        }
        return false;
    }


    //显示1个助教头像和名字，2个以及以上名字换成人数
    private void showAssistantLogic2() {
        if (mData.assistants != null && mData.assistants.length > 0) {
            ivAvatar1.setVisibility(VISIBLE);
            Glide.with(getContext())
                    .load(Account.getAvatar(mData.assistants[0].getId(), 300))
                    .bitmapTransform(circleTransform)
                    .placeholder(R.drawable.default_avatar_grey)
                    .error(R.drawable.default_avatar_grey)
                    .into(ivAvatar1);
            if (mData.assistants.length > 1) {
                ivAvatar1.setMaskText("" + mData.assistants.length);
            } else {
                ivAvatar1.setMaskText("");
            }
        } else {
            ivAvatar1.setVisibility(GONE);
        }
    }

    private void enterClassroom() {
        if (Account.TypeName.CLASS_LESSON.equals(mData.type)) {
            if (XiaojsConfig.DEBUG) Logger.d("--------qz--------ticket=" + mData.classInfo.ticket);
            Intent i = new Intent();
            i.putExtra(Constants.KEY_TICKET, mData.classInfo.ticket);
            i.setClass(getContext(), ClassroomActivity.class);
            getContext().startActivity(i);
        } else if (Account.TypeName.STAND_ALONE_LESSON.equals(mData.type) && (Ctl.StandaloneLessonState.FINISHED.equals(mData.state)
                || Ctl.StandaloneLessonState.LIVE.equals(mData.state)
                || Ctl.StandaloneLessonState.PENDING_FOR_LIVE.equals(mData.state))) {
            Intent i = new Intent();
            i.putExtra(Constants.KEY_TICKET, mData.ticket);
            i.setClass(getContext(), ClassroomActivity.class);
            getContext().startActivity(i);
        }
    }


    public void replay() {
        if (mEventCallback != null) {
            //目前给教室里面的课表回调出去
            mEventCallback.onEvent(EventCallback.EVENT_1, position, mData);
        } else {
            //其余地方课表的逻辑
            LibDoc doc = new LibDoc();
            doc.key = mData.playback;

            doc.mimeType = mData.mimeType;

            if (TextUtils.isEmpty(doc.mimeType)) {
                doc.mimeType = Collaboration.StreamingTypes.HLS;
            } else {
                doc.typeName = Collaboration.TypeName.RECORDING_IN_LIBRARY;
            }

            MaterialUtil.openMaterial((Activity) getContext(), doc);
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
            new LessonOperateBoard(getContext()).setOpGroup2(fromSchedule ? classLessonOperate() : classOperate()).maybe((Activity) getContext(), mData, position).show();
        }
    }

    public List<LOpModel> createPublicLessonMode() {
        List<LOpModel> ops;
        if (mData.owner != null && AccountPref.getAccountID(getContext()).equals(mData.owner.getId())) {//我是所有者，无论我是不是讲师
            ops = imOnwer();
        } else if (mData.teacher != null && AccountPref.getAccountID(getContext()).equals(mData.teacher.getId())) {//我虽然不是所有者，但我是讲师
            ops = imSpeaker();
        } else {//我就是个学生
            ops = imStudent();
        }
        return ops;
    }

    //临时处理：草稿、待审核和已取消状态的课，一定是我的课
    private boolean forceDealToOwner() {
        if (Ctl.StandaloneLessonState.DRAFT.equals(mData.state) || Ctl.StandaloneLessonState.PENDING_FOR_APPROVAL.equals(mData.state) || Ctl.StandaloneLessonState.CANCELLED.equals(mData.state)) {
            return true;
        }
        return false;
    }


    //首页班课的操作，对班的操作
    public List<LOpModel> classOperate() {
        List<LOpModel> list = new ArrayList<>();
        list.add(new LOpModel(LOpModel.OP_SCHEDULE));
        list.add(new LOpModel(LOpModel.OP_DATABASE1));
        list.add(new LOpModel(LOpModel.OP_ENTER));
        list.add(new LOpModel(LOpModel.OP_CLASS_INFO));
        return list;
    }

    //课表的操作，对班级课表页面的课的操作，只有班主任和课所有者能操作
    public List<LOpModel> classLessonOperate() {
        List<LOpModel> list = new ArrayList<>();
        if (Ctl.StandaloneLessonState.PENDING_FOR_LIVE.equals(mData.state)) {//待开课
            if (mData.schedule.getStart().getTime() - System.currentTimeMillis() > 5 * 60 * 1000) {
                list.add(new LOpModel(LOpModel.OP_EDIT));
            }
            list.add(new LOpModel(LOpModel.OP_CANCEL_LESSON));
//            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_ENTER));
        } else if (Ctl.StandaloneLessonState.LIVE.equals(mData.state)) {//上课中
//            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_ENTER));
        } else if (Ctl.StandaloneLessonState.FINISHED.equals(mData.state)) {//已完课
            list.add(new LOpModel(LOpModel.OP_DELETE));
//            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_ENTER));
        } else if (Ctl.StandaloneLessonState.CANCELLED.equals(mData.state)) {//已取消
            list.add(new LOpModel(LOpModel.OP_DELETE));
            list.add(new LOpModel(LOpModel.OP_ENTER));
        } else {//其余状态，班课除了上面的几个状态，其余的就是待开课前的状态（排课中）
            list.add(new LOpModel(LOpModel.OP_EDIT));
            list.add(new LOpModel(LOpModel.OP_CANCEL_LESSON));
//            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_ENTER));
        }
        return list;
    }

    public List<LOpModel> imOnwer() {
        List<LOpModel> list = new ArrayList<>();

        if (Ctl.StandaloneLessonState.DRAFT.equals(mData.state)) {//草稿
//            list.add(new LOpModel(LOpModel.OP_SUBMIT));
            list.add(new LOpModel(LOpModel.OP_PUBLISH));
            list.add(new LOpModel(LOpModel.OP_EDIT));
            list.add(new LOpModel(LOpModel.OP_DELETE));
//            list.add(new LOpModel(LOpModel.OP_APPLY));
        } else if (Ctl.StandaloneLessonState.PENDING_FOR_ACK.equals(mData.state)) {//待确认
            list.add(new LOpModel(LOpModel.OP_LOOK));
//            list.add(new LOpModel(LOpModel.OP_CANCEL_SUBMIT));
//            list.add(new LOpModel(LOpModel.OP_APPLY));

        } else if (Ctl.StandaloneLessonState.ACKNOWLEDGED.equals(mData.state)) {//已确认
            list.add(new LOpModel(LOpModel.OP_PUBLISH));
            list.add(new LOpModel(LOpModel.OP_EDIT));
//            list.add(new LOpModel(LOpModel.OP_APPLY));

        } else if (Ctl.StandaloneLessonState.PENDING_FOR_APPROVAL.equals(mData.state)) {//待审核
            list.add(new LOpModel(LOpModel.OP_LOOK));
//            list.add(new LOpModel(LOpModel.OP_APPLY));
            list.add(new LOpModel(LOpModel.OP_CANCEL_CHECK));

        } else if (Ctl.StandaloneLessonState.PENDING_FOR_LIVE.equals(mData.state)) {//待开课
            list.add(new LOpModel(LOpModel.OP_LOOK));
            if (mData.enroll != null && mData.enroll.mandatory) {//是否需要报名
                list.add(new LOpModel(LOpModel.OP_SIGNUP));
            }
//            list.add(new LOpModel(LOpModel.OP_MODIFY_TIME));
            list.add(new LOpModel(LOpModel.OP_CANCEL_LESSON));
            if (mData.accessible) {//已公开
                list.add(new LOpModel(LOpModel.OP_PRIVATE));
            } else {
                list.add(new LOpModel(LOpModel.OP_PUBLIC));
            }
            list.add(new LOpModel(LOpModel.OP_APPLY));
            //报名学生名单没有
//            list.add(new LOpModel(LOpModel.OP_));
            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_ENTER));

        } else if (Ctl.StandaloneLessonState.LIVE.equals(mData.state)) {//已开课
            list.add(new LOpModel(LOpModel.OP_LOOK));
            if (mData.enroll != null && mData.enroll.mandatory) {//是否需要报名
                list.add(new LOpModel(LOpModel.OP_SIGNUP));
            }
            if (mData.accessible) {//已公开
                list.add(new LOpModel(LOpModel.OP_PRIVATE));
            } else {
                list.add(new LOpModel(LOpModel.OP_PUBLIC));
            }
            //强行停止 没有
//            list.add(new LOpModel(LOpModel.OP_));

            list.add(new LOpModel(LOpModel.OP_APPLY));
            //报名学生名单没有
//            list.add(new LOpModel(LOpModel.OP_));
            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_ENTER));

        } else if (Ctl.StandaloneLessonState.FINISHED.equals(mData.state)) {//已完课
            list.add(new LOpModel(LOpModel.OP_LOOK));
            if (mData.accessible) {//已公开
                list.add(new LOpModel(LOpModel.OP_PRIVATE));
            } else {
                list.add(new LOpModel(LOpModel.OP_PUBLIC));
            }
            list.add(new LOpModel(LOpModel.OP_CREATE_LESSON_AGAIN));
            list.add(new LOpModel(LOpModel.OP_APPLY));
            //报名学生名单没有
//            list.add(new LOpModel(LOpModel.OP_));
            list.add(new LOpModel(LOpModel.OP_SHARE));
            list.add(new LOpModel(LOpModel.OP_ENTER));

        } else if (Ctl.StandaloneLessonState.REJECTED.equals(mData.state)) {//审核失败
            list.add(new LOpModel(LOpModel.OP_LOOK));
            list.add(new LOpModel(LOpModel.OP_DELETE));
            //不知道再次开课和重新开课的区别
            list.add(new LOpModel(LOpModel.OP_RECREATE_LESSON));
            list.add(new LOpModel(LOpModel.OP_APPLY));

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
            list.add(new LOpModel(LOpModel.OP_REJECT_REASON));
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
            list.add(new LOpModel(LOpModel.OP_REJECT_REASON));
            list.add(new LOpModel(LOpModel.OP_DELETE));

        } else if (Ctl.StandaloneLessonState.STOPPED.equals(mData.state)) {//强制停止
            list.add(new LOpModel(LOpModel.OP_REJECT_REASON));
            list.add(new LOpModel(LOpModel.OP_DELETE));

        } else {//失效

        }
        return list;

    }

    @OnClick(R.id.iv_avatar)
    public void clickTeacher(){
        enterTalk(mData.teacher.getId(),mData.teacher.getBasic()==null||TextUtils.isEmpty(mData.teacher.getBasic().getName())?mData.teacher.name:mData.teacher.getBasic().getName());
    }

    @OnClick(R.id.iv_avatar1)
    public void showAssistantDialog(){
        if(ArrayUtil.isEmpty(mData.assistants)){
            return;
        }
        if(mData.assistants.length==1){
            enterTalk(mData.assistants[0].getId(),
                    mData.assistants[0].getBasic()==null||
                            TextUtils.isEmpty(mData.assistants[0].getBasic().getName())?
                            mData.assistants[0].name:mData.assistants[0].getBasic().getName());
            return;
        }
        Common3Dialog dialog=new Common3Dialog(getContext());
        ListView content= new ListView(getContext());
        content.setDivider(new ColorDrawable(getResources().getColor(R.color.main_bg)));
        content.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.px2));
        TeacherAdapter adapter=new TeacherAdapter((Activity) getContext());
        adapter.setList(mData.assistants);
        content.setAdapter(adapter);
        content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                enterTalk(mData.assistants[position].getId(),
                        mData.assistants[position].getBasic()==null||
                                TextUtils.isEmpty(mData.assistants[position].getBasic().getName())?
                                mData.assistants[position].name:mData.assistants[position].getBasic().getName());
            }
        });
        dialog.setTitle("助教（"+mData.assistants.length+"）");
        dialog.setCustomView(content);
        dialog.needCloseBtn(true);
        dialog.show();
    }

    private void enterTalk(String id, String title){
        if(mEventCallback!=null){
            mEventCallback.onEvent(EventCallback.EVENT_3,id,title);
        }
    }

    private EventCallback mEventCallback;

    public void setCallback(EventCallback eventCallback) {
        mEventCallback = eventCallback;
    }

}
