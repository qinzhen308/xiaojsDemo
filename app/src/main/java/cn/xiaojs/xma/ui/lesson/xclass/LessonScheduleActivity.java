package cn.xiaojs.xma.ui.lesson.xclass;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.Intent;
import android.view.View;

import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LastEmptyModel;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.TimeUtil;

/**
 * Created by Paul Z on 2017/5/18.
 * 创建班时的课表
 */

public class LessonScheduleActivity extends BaseActivity{

    @BindView(R.id.over_layout)
    RecyclerView mListView;
    public final int REQUEST_NEW_LESSON_CODE = 0x1;


    HomeClassAdapter mAdapter;

    Map<Long,List<ClassLesson>> datas;
    @BindView(R.id.slSchedule)
    ScheduleLayout calendarView;
    long curDayIndex=System.currentTimeMillis()/(3600*24*1000);


    @Override
    protected void addViewContent() {
        needHeader(true);
        setRightText(R.string.add);
        setMiddleTitle(R.string.schedule);
        addView(R.layout.layout_home_schedule);
        mListView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        mAdapter=new HomeClassAdapter();
        mListView.setAdapter(mAdapter);
        datas= ScheduleUtil.buildScheduleByDay(CreateClassActivity.classLessons);
        bindData();
        calendarView.setOnCalendarClickListener(new OnCalendarClickListener() {
            @Override
            public void onClickDate(int year, int month, int day) {
                if(XiaojsConfig.DEBUG){
                    Logger.d("-----qz--------"+(year+"-"+(month+1)+"-"+day)+"---curDayIndex="+curDayIndex);
                }
                curDayIndex= ScheduleUtil.getDayIndex(year+"-"+(month+1)+"-"+day);
                bindData();
            }

            @Override
            public void onPageChange(int year, int month, int day) {
            }
        });

    }


    private void bindData(){
        ArrayList list2=new ArrayList();
        if(datas!=null){
            List list=datas.get(curDayIndex);
            if(ArrayUtil.isEmpty(list)){
                list2.add(new LessonLabelModel(TimeUtil.getWeak(curDayIndex*3600*24*1000),0,false));
            }else {
                list2.add(new LessonLabelModel(TimeUtil.getWeak(curDayIndex*3600*24*1000),list.size(),true));
                list2.addAll(list);
                list2.add(new LastEmptyModel());
            }
        }else {
            list2.add(new LessonLabelModel(TimeUtil.getWeak(curDayIndex*3600*24*1000),0,false));
        }
        mAdapter.setList(list2);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.right_view})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.right_view:
                startActivityForResult(new Intent(this, CreateTimetableActivity.class),
                        REQUEST_NEW_LESSON_CODE);
                break;
            case R.id.left_view:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_NEW_LESSON_CODE:
                    //新添加的课
                    ClassLesson classLesson =
                            (ClassLesson) data.getSerializableExtra(CreateTimetableActivity.EXTRA_CLASS_LESSON);
                    CreateClassActivity.addClassLesson(classLesson);
                    //TODO 更新显示
                    datas= ScheduleUtil.buildScheduleByDay(CreateClassActivity.classLessons);
                    bindData();
                    break;
            }
        }
    }
}
