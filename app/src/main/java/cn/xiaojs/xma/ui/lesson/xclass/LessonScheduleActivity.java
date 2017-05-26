package cn.xiaojs.xma.ui.lesson.xclass;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.Intent;
import android.view.View;

import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;

import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.lesson.util.ScheduleFilter;
import cn.xiaojs.xma.util.TimeUtil;

/**
 * Created by Paul Z on 2017/5/18.
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
        calendarView.setOnCalendarClickListener(new OnCalendarClickListener() {
            @Override
            public void onClickDate(int year, int month, int day) {
                String s=year+"-"+month+"-"+day;
//                TimeUtil.getTimeMils()
            }

            @Override
            public void onPageChange(int year, int month, int day) {
            }
        });

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
                    datas=ScheduleFilter.buildScheduleByDay(CreateClassActivity.classLessons);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
