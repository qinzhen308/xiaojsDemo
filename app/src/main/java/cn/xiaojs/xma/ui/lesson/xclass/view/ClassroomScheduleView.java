package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
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
import cn.xiaojs.xma.ui.widget.LabelImageView;
import cn.xiaojs.xma.util.MaterialUtil;

/**
 * Created by Paul Z on 2017/11/1.
 */

public class ClassroomScheduleView extends RelativeLayout implements IViewModel<CLesson> {


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

    int position;

    boolean fromSchedule;
    boolean fromHome;


    private CircleTransform circleTransform;

    public ClassroomScheduleView(Context context) {
        super(context);
        init();
    }

    public ClassroomScheduleView(Context context, AttributeSet attrs) {
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

        tvState.setVisibility(GONE);
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
        if (mEventCallback == null) {//目前教室的课表会有这个回调
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterClassroom();
                }
            });
        }
    }

    private void showOperateBtn() {
        if (fromSchedule) {//课表里
            Logger.d("----qz---my id=" + AccountPref.getAccountID(getContext()));
            if (!(isAdviser() || mData.owner != null && AccountPref.getAccountID(getContext()).equals(mData.owner.getId()))) {//只有班主任和所有者有操作权限显示按钮
                btnMore.setVisibility(INVISIBLE);
                Logger.d("----qz---is adviser=" + isAdviser());
            }
//            else if(!(Ctl.StandaloneLessonState.FINISHED.equals(mData.state)||Ctl.StandaloneLessonState.PENDING_FOR_LIVE.equals(mData.state)||
//                    Ctl.StandaloneLessonState.LIVE.equals(mData.state)||Ctl.StandaloneLessonState.CANCELLED.equals(mData.state))){//除了这个状态，其他不要操作
//                btnMore.setVisibility(INVISIBLE);
//            }
        }
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
        new LessonOperateBoard(getContext()).setOpGroup2(fromSchedule ? classLessonOperate() : classOperate()).maybe((Activity) getContext(), mData, position).show();
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

    private EventCallback mEventCallback;

    public void setCallback(EventCallback eventCallback) {
        mEventCallback = eventCallback;
    }
}
