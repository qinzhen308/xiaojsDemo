package cn.xiaojs.xma.ui.recordlesson.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.recordedlesson.RLesson;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.ui.base.AbsOpModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.lesson.xclass.view.LessonOperateBoard;
import cn.xiaojs.xma.ui.recordlesson.RLDirListActivity;
import cn.xiaojs.xma.ui.recordlesson.RecordedLessonDetailActivity;
import cn.xiaojs.xma.ui.recordlesson.RecordedLessonEnrollActivity;
import cn.xiaojs.xma.ui.recordlesson.model.RLOpModel;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class HomeRecordedLessonView extends RelativeLayout implements IViewModel<RLesson> {


    RLesson mData;
    @BindView(R.id.flag_view)
    ImageView flagView;
    @BindView(R.id.title_view)
    TextView titleView;
    @BindView(R.id.status_view)
    TextView statusView;
    @BindView(R.id.teachers_view)
    TextView teachersView;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.member_view)
    TextView memberView;
    @BindView(R.id.op_dir_view)
    ImageButton opDirView;
    @BindView(R.id.op_more_view)
    ImageButton opMoreView;
    @BindColor(R.color.chocolate_light)
    int teacherColor;
    Dimension dimension;
    int position;

    public HomeRecordedLessonView(Context context) {
        super(context);
        init();
    }

    public HomeRecordedLessonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_home_recorded_lesson, this);
        setBackgroundResource(R.drawable.bg_classlist_item);
        ButterKnife.bind(this);
        dimension=new Dimension();
        dimension.width=getResources().getDimensionPixelSize(R.dimen.px250);
        dimension.height=getResources().getDimensionPixelSize(R.dimen.px132);
    }


    @Override
    public void bindData(int position, RLesson data) {
        mData = data;
        this.position = position;
        titleView.setText(mData.title);
        if(mData.createdBy!=null&& AccountDataManager.getAccountID(getContext()).equals(mData.createdBy.getId())){//是这个课的创建者
            if (Ctl.RecordedCourseState.DRAFT.equals(mData.state)) {
                statusView.setVisibility(VISIBLE);
                statusView.setText("待上架");
            }else if (Ctl.RecordedCourseState.PENDING_FOR_APPROVAL.equals(mData.state)) {
                statusView.setVisibility(VISIBLE);
                statusView.setText("待审核");
            } else if (Ctl.RecordedCourseState.REJECTED.equals(mData.state)) {
                statusView.setVisibility(VISIBLE);
                statusView.setText("审核失败");
            }else {
                statusView.setVisibility(INVISIBLE);
            }
            if(mData.expire!=null&&mData.expire.effective>0){
                tvDate.setText("有效期："+mData.expire.effective+"天");
            }else {
                tvDate.setText("有效期：永久");
            }
        }else {//学生

            if(mData.enrollOfCurrentAccount!=null&&mData.enrollOfCurrentAccount.isExpired){
                statusView.setText("已过期");
                statusView.setVisibility(VISIBLE);
            }else {
                statusView.setVisibility(INVISIBLE);
            }

            if(mData.expire!=null&&mData.enrollOfCurrentAccount!=null&&mData.enrollOfCurrentAccount.deadline!=null){
                tvDate.setText("有效期至"+ ScheduleUtil.getDateYMD(mData.enrollOfCurrentAccount.deadline));
            }else {
                tvDate.setText("有效期：永久");
            }
        }

        if (mData.enroll != null) {
            memberView.setText(mData.enroll.current + "人报名");
        } else {
            memberView.setText(0 + "人报名");
        }

        if(!ArrayUtil.isEmpty(mData.teachers)){

            SpannableString ss=new SpannableString("主讲："+handleTeachersNames());
            ss.setSpan(new ForegroundColorSpan(teacherColor),3,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            teachersView.setText(ss);
        }else {
            teachersView.setText("主讲：");
        }


        Glide.with(getContext()).load(Ctl.getCover(mData.cover,dimension)).fitCenter().placeholder(R.drawable.default_lesson_cover).into(flagView);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (isOwner()) {//所有者
//                    getContext().startActivity(new Intent(getContext(),RecordedLessonDetailActivity.class).putExtra(RecordedLessonDetailActivity.EXTRA_RLESSON_ID,mData.id));
//                } else {//我就是个学生
//                    RecordedLessonEnrollActivity.invoke(getContext(),mData.id);
//                }
                if(Ctl.RecordedCourseState.ONSHELVES.equals(mData.state)){
                    RecordedLessonEnrollActivity.invoke(getContext(),mData.id);
                }
            }
        });
    }



    @OnClick({R.id.op_dir_view, R.id.op_more_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.op_dir_view:
                RLDirListActivity.invoke(getContext(),mData.id,mData.enrollOfCurrentAccount!=null&&mData.enrollOfCurrentAccount.isExpired);
                break;
            case R.id.op_more_view:
                moreOperate();
                break;
        }
    }

    public void moreOperate() {
        List<RLOpModel> group1 = createRecordedLessonOpMode();
        List<RLOpModel> group2 = new ArrayList<>();
        if(group1.size()>4){
            while (group1.size()>3){
                group2.add(group1.remove(3));
            }
        }
        new LessonOperateBoard(getContext()).setOpGroup1(group1).setOpGroup2(group2).maybe((Activity) getContext(), mData, position).show();
    }

    public List<RLOpModel> createRecordedLessonOpMode() {
        List<RLOpModel> ops;
        if (isOwner()) {//我虽然不是所有者，但我是讲师
            ops = imSpeaker();
        } else {//我就是个学生
            ops = imStudent();
        }
        return ops;
    }

    public List<RLOpModel> imSpeaker() {
        List<RLOpModel> list = new ArrayList<>();
        if (Ctl.RecordedCourseState.DRAFT.equals(mData.state)) {//待上架
            list.add(new RLOpModel(AbsOpModel.OP_PUBLISH));
            list.add(new RLOpModel(AbsOpModel.OP_EDIT));
//            list.add(new RLOpModel(AbsOpModel.OP_DELETE));
            //查看课程目录
//            list.add(new RLOpModel(AbsOpModel.OP_));
            list.add(new RLOpModel(AbsOpModel.OP_RECREATE_LESSON));
//            list.add(new RLOpModel(AbsOpModel.OP_APPLY));

        } else if (Ctl.RecordedCourseState.PENDING_FOR_APPROVAL.equals(mData.state)) {//待审核
            //查看课程目录
//            list.add(new RLOpModel(AbsOpModel.OP_));
            list.add(new RLOpModel(AbsOpModel.OP_LOOK));
//            list.add(new RLOpModel(AbsOpModel.OP_CANCEL_CHECK));
            list.add(new RLOpModel(AbsOpModel.OP_RECREATE_LESSON));
//            list.add(new RLOpModel(AbsOpModel.OP_APPLY));

        } else if (Ctl.RecordedCourseState.ONSHELVES.equals(mData.state)) {//审核通过
            if(mData.publish!=null&&mData.publish.accessible){
                list.add(new RLOpModel(AbsOpModel.OP_PRIVATE));
            }else {
                list.add(new RLOpModel(AbsOpModel.OP_PUBLIC));
            }
            if(AccountDataManager.isVerified(getContext())){
                list.add(new RLOpModel(AbsOpModel.OP_EDIT));
            }else {
                list.add(new RLOpModel(AbsOpModel.OP_LOOK));
            }
            list.add(new RLOpModel(AbsOpModel.OP_RECREATE_LESSON));
//            list.add(new RLOpModel(AbsOpModel.OP_APPLY));
            list.add(new RLOpModel(AbsOpModel.OP_APPLY_STUDENTS_LIST));
            list.add(new RLOpModel(AbsOpModel.OP_SIGNUP));
            list.add(new RLOpModel(AbsOpModel.OP_SHARE));
        } else if (Ctl.RecordedCourseState.REJECTED.equals(mData.state)) {//审核失败
            list.add(new RLOpModel(AbsOpModel.OP_LOOK));
//            list.add(new RLOpModel(AbsOpModel.OP_DELETE));
            list.add(new RLOpModel(AbsOpModel.OP_RECREATE_LESSON));
//            list.add(new RLOpModel(AbsOpModel.OP_APPLY));
        }
        return list;
    }

    public List<RLOpModel> imStudent() {
        List<RLOpModel> list = new ArrayList<>();
        if (Ctl.RecordedCourseState.DRAFT.equals(mData.state)) {//待上架


        } else if (Ctl.RecordedCourseState.PENDING_FOR_APPROVAL.equals(mData.state)) {//待审核

        } else if (Ctl.RecordedCourseState.ONSHELVES.equals(mData.state)) {//审核通过
            //查看课程目录
//            list.add(new RLOpModel(AbsOpModel.Op_));
//            list.add(new RLOpModel(AbsOpModel.OP_APPLY));
            list.add(new RLOpModel(AbsOpModel.OP_SIGNUP));
            list.add(new RLOpModel(AbsOpModel.OP_SHARE));

        } else if (Ctl.RecordedCourseState.REJECTED.equals(mData.state)) {//审核失败

        }
        return list;
    }

    private String handleTeachersNames(){
        String teachersName="";
        for(int i=0,size=Math.min(mData.teachers.length,3);i<size;i++){
            Account t=mData.teachers[i];
            if(t.getBasic()!=null){
                teachersName+=t.getBasic().getName()+"、";
            }
        }
        if(teachersName.endsWith("、")){
            teachersName=teachersName.substring(0,teachersName.length()-1);
        }
        if(mData.teachers.length>3){
            teachersName+="等"+mData.teachers.length+"人";
        }
        return teachersName;
    }

    private boolean isOwner(){
        return mData.createdBy!=null&&AccountDataManager.getAccountID(getContext()).equals(mData.createdBy.getId());
    }

}
