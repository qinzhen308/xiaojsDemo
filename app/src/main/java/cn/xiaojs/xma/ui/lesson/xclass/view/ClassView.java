package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.ctl.Adviser;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.model.ctl.PrivateClass;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.grade.ClassMaterialActivity;
import cn.xiaojs.xma.ui.grade.MaterialActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassInfoActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassScheduleActivity;
import cn.xiaojs.xma.ui.view.AnimationView;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class ClassView extends RelativeLayout implements IViewModel<PrivateClass>{


    @BindView(R.id.flag_view)
    ImageView flagView;
    @BindView(R.id.title_view)
    TextView titleView;
    @BindView(R.id.member_view)
    TextView memberView;
    @BindView(R.id.status_view)
    TextView statusView;
    @BindView(R.id.living_view)
    AnimationView livingView;
    @BindView(R.id.teachers_view)
    TextView teachersView;
    @BindView(R.id.op_schedule_view)
    ImageButton opScheduleView;
    @BindView(R.id.op_data_view)
    ImageButton opDataView;
    @BindView(R.id.op_more_view)
    ImageButton opMoreView;
    @BindView(R.id.op_room_view)
    Button opRoomView;
    @BindView(R.id.entrance_count)
    TextView entranceCount;

    PrivateClass mData;

    public ClassView(Context context) {
        super(context);
        init();
    }

    public ClassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_class_item, this);
        ButterKnife.bind(this);
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


    @OnClick({R.id.op_schedule_view, R.id.op_data_view, R.id.op_more_view, R.id.op_room_view})
    public void onViewClicked(View view) {
        Intent intent=null;
        switch (view.getId()) {
            case R.id.op_schedule_view:
                ClassScheduleActivity.invoke(getContext(),mData.id,mData.title,isTeaching());
                break;
            case R.id.op_data_view:
                intent = new Intent(getContext(), ClassMaterialActivity.class);
//                intent.putExtra(ClassMaterialActivity.EXTRA_DELETEABLE,true);
                intent.putExtra(ClassMaterialActivity.EXTRA_ID, mData.id);
                intent.putExtra(ClassMaterialActivity.EXTRA_TITLE, mData.title);
                intent.putExtra(ClassMaterialActivity.EXTRA_SUBTYPE, Collaboration.SubType.PRIVATE_CLASS);
                getContext().startActivity(intent);
                break;
            case R.id.op_more_view:
                break;
            case R.id.op_room_view:
                intent=new Intent(getContext(),ClassroomActivity.class);
                intent.putExtra(Constants.KEY_TICKET,mData.ticket);
                getContext().startActivity(intent);
                break;
        }
    }

    @Override
    public void bindData(int position,PrivateClass data) {
        mData=data;
        titleView.setText(mData.title);
        flagView.setVisibility(INVISIBLE);
//        if(mData.learning){
////            flagView.setImageResource(R.drawable.ic_flag_learn);
//            statusView.setVisibility(VISIBLE);
//            entranceCount.setVisibility(VISIBLE);
//        }else {
////            flagView.setImageResource(R.drawable.ic_flag_teach);
//            statusView.setVisibility(GONE);
//            entranceCount.setVisibility(GONE);
//        }
        if(Ctl.LiveLessonState.LIVE.equals(mData.state)){
            livingView.setVisibility(VISIBLE);
            statusView.setVisibility(INVISIBLE);
        }else {
            livingView.setVisibility(INVISIBLE);
            if(Ctl.LiveLessonState.PENDING_FOR_LIVE.equals(mData.state)){
                statusView.setText("待上课");
                statusView.setVisibility(VISIBLE);
            }else if(Ctl.LiveLessonState.FINISHED.equals(mData.state)){
                statusView.setText("已完课");
                statusView.setVisibility(VISIBLE);
            }else {
                statusView.setVisibility(INVISIBLE);
            }
        }

        if(mData.enroll!=null){
            memberView.setText(mData.enroll.current+"人");
        }else {
            memberView.setText(0+"人");
        }
        String teachers="班主任：";
        for(int i=0;i<mData.advisers.length;i++){
            teachers+=mData.advisers[i].name;
        }
        teachersView.setText(teachers);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),ClassInfoActivity.class);
                intent.putExtra(ClassInfoActivity.EXTRA_CLASSID,mData.id);
                getContext().startActivity(intent);
            }
        });
    }


    //判断权限是否大于等于班主任
    private boolean isTeaching(){
        String id=AccountPref.getAccountID(getContext());
        if(mData.owner!=null&&id.equals(mData.owner.getId())){//我是拥有者
            return true;
        }else if(!ArrayUtil.isEmpty(mData.advisers)){//我是班主任
            for(Adviser ad:mData.advisers){
                if(id.equals(ad.id))return true;
            }
        }
        return false;
    }
}
