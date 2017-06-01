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
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.model.ctl.PrivateClass;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.grade.ClassMaterialActivity;
import cn.xiaojs.xma.ui.grade.MaterialActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassScheduleActivity;
import cn.xiaojs.xma.ui.view.AnimationView;

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
                ClassScheduleActivity.invoke(getContext(),mData.id,mData.title);
                break;
            case R.id.op_data_view:
                intent = new Intent(getContext(), ClassMaterialActivity.class);
                intent.putExtra(ClassMaterialActivity.EXTRA_DELETEABLE,true);
                intent.putExtra(ClassMaterialActivity.EXTRA_LESSON_ID, mData.id);
                intent.putExtra(ClassMaterialActivity.EXTRA_LESSON_NAME, mData.title);
                getContext().startActivity(intent);
                break;
            case R.id.op_more_view:
                break;
            case R.id.op_room_view:
                intent=new Intent(getContext(),ClassroomActivity.class);
                getContext().startActivity(intent);
                break;
        }
    }

    @Override
    public void bindData(PrivateClass data) {
        mData=data;
        titleView.setText(mData.title);
//        if(mData.learning){
////            flagView.setImageResource(R.drawable.ic_flag_learn);
//            statusView.setVisibility(VISIBLE);
//            entranceCount.setVisibility(VISIBLE);
//        }else {
////            flagView.setImageResource(R.drawable.ic_flag_teach);
//            statusView.setVisibility(GONE);
//            entranceCount.setVisibility(GONE);
//        }
        memberView.setText(mData.enrolled+"人");
        String teachers="班主任：";
        for(int i=0;i<mData.advisers.length;i++){
            teachers+=mData.advisers[i].name;
        }
        teachersView.setText(teachers);

    }
}
