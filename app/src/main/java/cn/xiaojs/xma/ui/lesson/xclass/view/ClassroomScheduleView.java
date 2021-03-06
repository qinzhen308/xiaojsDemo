package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
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
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.pageload.IEventer;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.model.ctl.ScheduleLesson;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.schedule.SLOpModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.view.TextInBgSpan;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.Common3Dialog;
import cn.xiaojs.xma.ui.widget.LabelImageView;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/11/1.
 */

public class ClassroomScheduleView extends RelativeLayout implements IViewModel<ScheduleLesson> ,IBindFragment,IEventer{


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

    ScheduleLesson mData;
    @BindView(R.id.tv_state)
    TextView tvState;

    int mPosition;

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
    public void bindData(int position, ScheduleLesson data) {
        mData = data;
        this.mPosition = position;
        tvDate.setText(ScheduleUtil.getHMDate(data.schedule.getStart()));
        tvTotalTime.setText(data.schedule.getDuration() + "分钟");

        if (!TextUtils.isEmpty(data.playback)) {
            SpannableString ss = new SpannableString(" 回放  " + data.title);
            ss.setSpan(new TextInBgSpan(getResources().getDrawable(R.drawable.bg_orange_rect), getResources().getColor(R.color.white)), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvLesson.setText(ss);
            if (mEventCallback == null) {//目前教室的课表会有这个回调
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //目前给教室里面的课表回调出去
                        mEventCallback.onEvent(EventCallback.EVENT_1, mPosition, mData);
                    }
                });
            }
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

        if(ClassroomEngine.getEngine().isVistor()){
            btnMore.setVisibility(INVISIBLE);
        }else {
            btnMore.setVisibility(VISIBLE);
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
       /* if (mData.classInfo == null || mData.classInfo.advisers == null) return false;
        String id = AccountPref.getAccountID(getContext());
        for (cn.xiaojs.xma.model.account.Account ad : mData.classInfo.advisers) {
            if (id.equals(ad.getId())) return true;
        }*/
        return mData.imLead;
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


    @OnClick(R.id.btn_more)
    public void onViewClicked() {
        ListBottomDialog dialog = new ListBottomDialog(getContext());
        final List<SLOpModel> lOpModelList = classLessonOperate();
        String[] names = new String[lOpModelList.size()];
        for (int i = 0; i < lOpModelList.size(); i++) {
            names[i] = getResources().getString(LessonOperateBoard.names[lOpModelList.get(i).getId()]);
        }
        dialog.setItems(names);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                lOpModelList.get(position).onClick(mFragment, mData, position);
            }
        });
        dialog.show();
//        new LessonOperateBoard(getContext()).setOpGroup2(fromSchedule ? classLessonOperate() : classOperate()).maybe((Activity) getContext(), mData, mPosition).show();
    }

    //课表的操作，对班级课表页面的课的操作，只有班主任和课所有者能操作
    public List<SLOpModel> classLessonOperate() {
        List<SLOpModel> list = new ArrayList<>();
        if (Ctl.StandaloneLessonState.PENDING_FOR_LIVE.equals(mData.state)) {//待开课
            if (mData.schedule.getStart().getTime() - System.currentTimeMillis() > 5 * 60 * 1000) {
                list.add(new SLOpModel(LOpModel.OP_EDIT));
            }
            list.add(new SLOpModel(LOpModel.OP_CANCEL_LESSON));
        } else if (Ctl.StandaloneLessonState.LIVE.equals(mData.state)) {//上课中
        } else if (Ctl.StandaloneLessonState.FINISHED.equals(mData.state)) {//已完课
            list.add(new SLOpModel(LOpModel.OP_DELETE));
        } else if (Ctl.StandaloneLessonState.CANCELLED.equals(mData.state)) {//已取消
            list.add(new SLOpModel(LOpModel.OP_DELETE));
        } else {//其余状态，班课除了上面的几个状态，其余的就是待开课前的状态（排课中）
            list.add(new SLOpModel(LOpModel.OP_EDIT));
            list.add(new SLOpModel(LOpModel.OP_CANCEL_LESSON));
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
        content.setPadding(0,0,0,getResources().getDimensionPixelSize(R.dimen.px20));
        content.setSelector(new ColorDrawable());
        TeacherAdapter adapter=new TeacherAdapter( mFragment.getActivity());
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

    Fragment mFragment;
    @Override
    public void bindFragment(Fragment fragment) {
        mFragment=fragment;
    }

    private EventCallback mEventCallback;
    @Override
    public void setEventCallback(EventCallback callback) {
        mEventCallback = callback;
    }
}
