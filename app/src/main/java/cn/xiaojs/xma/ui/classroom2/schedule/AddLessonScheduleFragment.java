package cn.xiaojs.xma.ui.classroom2.schedule;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.ctl.CheckLesson;
import cn.xiaojs.xma.model.ctl.CheckOverlapParams;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.model.ctl.ScheduleParams;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.lesson.xclass.AddLessonNameActivity;
import cn.xiaojs.xma.ui.lesson.xclass.CreateClassActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.DataPicker;
import cn.xiaojs.xma.util.TimeUtil;

/**
 * Created by Paul Z on 2017/11/2.
 */

public class AddLessonScheduleFragment extends BottomSheetFragment
        implements DialogInterface.OnKeyListener {

    private final static int HALF_HOUR = 30 * 60 * 1000; //30 minutes
    private final int MAX_LESSON_DURATION = 600; //600 minutes, 10 hours
    private final int MIN_CLASS_LESSON_DURATION = 10; //10 minutes

    public static final String EXTRA_CLASS_LESSON = "class_lesson";
    public static final String EXTRA_TARGET_DATE = "extra_target_date";

    private final int REQUEST_NAME_CODE = 0x1;


    @BindView(R.id.lesson_name_text)
    TextView nameView;
    @BindView(R.id.lesson_time)
    TextView timeView;
    @BindView(R.id.lesson_duration)
    EditTextDel durationView;
    @BindView(R.id.record_view)
    CheckBox recordView;

    private long lessonStartTime;

    private long targetTime;
    private String classId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classId=ClassroomEngine.getEngine().getCtlSession().cls.id;
    }

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_add_lesson_schedule, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @OnClick({R.id.iv_back,R.id.btn_finish, R.id.lay_class_name, R.id.lay_begin_time})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                dismiss();
                break;
            case R.id.btn_finish://完成
                complete();
                break;
            case R.id.lay_class_name://填写班级名称
                AddLessonNameFragment fragment=AddLessonNameFragment.createInstance(classId,nameView.getText().toString(),AddLessonNameFragment.ROLE_LESSON);
                fragment.setTargetFragment(this,REQUEST_NAME_CODE);
                fragment.show(getChildFragmentManager(),"modify_name");
                break;
            case R.id.lay_begin_time://选择上课时间
                selectStartTime();
                break;
        }
    }


    private void initView() {

    }

    private void selectStartTime() {

        long timesp = targetTime == -1 ? System.currentTimeMillis() + HALF_HOUR : targetTime;

        DataPicker.pickFutureDate(getActivity(), new Date(timesp),
                new DataPicker.OnDatePickListener() {
                    @Override
                    public void onDatePicked(int year, int month, int day,
                                             int hour, int minute, int second) {

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, hour, minute, second);

                        lessonStartTime = calendar.getTimeInMillis();

                        String dateStr = TimeUtil.formatDate(calendar.getTimeInMillis(),
                                TimeUtil.TIME_YYYY_MM_DD_HH_MM);
                        timeView.setText(dateStr);

                    }
                });
    }

    private void complete() {

        String name = nameView.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getActivity(), R.string.live_lesson_name_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.length() > CreateClassActivity.MAX_CLASS_CHAR) {
            String nameEr = this.getString(R.string.live_lesson_name_error, CreateClassActivity.MAX_CLASS_CHAR);
            Toast.makeText(getActivity(), nameEr, Toast.LENGTH_SHORT).show();
            return;
        }

        String startTime = timeView.getText().toString().trim();
        if (TextUtils.isEmpty(startTime)) {
            Toast.makeText(getActivity(), R.string.lesson_start_time_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (lessonStartTime <= System.currentTimeMillis()) {
            Toast.makeText(getActivity(), R.string.lesson_start_time_error, Toast.LENGTH_SHORT).show();
            return;
        }

        String durationStr = durationView.getText().toString().trim();
        if (TextUtils.isEmpty(durationStr)) {
            Toast.makeText(getActivity(), R.string.lesson_duration_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (Integer.parseInt(durationStr) > MAX_LESSON_DURATION) {
            String tips = String.format(getString(R.string.lesson_duration_must_be_less_than), MAX_LESSON_DURATION);
            Toast.makeText(getActivity(), tips, Toast.LENGTH_SHORT).show();
            return;
        }

        if (Integer.parseInt(durationStr) < MIN_CLASS_LESSON_DURATION) {
            String tips = String.format(getString(R.string.class_lesson_duration_must_be_more_than), MIN_CLASS_LESSON_DURATION);
            Toast.makeText(getActivity(), tips, Toast.LENGTH_SHORT).show();
            return;
        }


        //此处判断时间是否有冲突
        new CheckConflictTask().execute(lessonStartTime, 
                Long.parseLong(durationView.getText().toString()));


        

    }


    private boolean checkLocalConflict(long startTime,long duration) {

        long endTime = startTime + duration * 60 * 1000;

        ArrayList<ClassLesson> classLessons = CreateClassActivity.classLessons;
        if (classLessons != null && classLessons.size() > 0) {

            for (ClassLesson clesson: classLessons) {
                Schedule schedule = clesson.schedule;
                long start = schedule.getStart().getTime();
                long end = start + duration * 60 * 1000;

                if (startTime >= start && startTime <= (end + 5*60*1000)) {//每节课间隔不能小于五分钟
                    return true;
                }

                if (endTime >= start && endTime <= end) {
                    return true;
                }
            }


        }

        return false;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return false;
    }



    private class CheckConflictTask extends AsyncTask<Long,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            showProgress(false);
        }

        @Override
        protected Boolean doInBackground(Long... params) {

            boolean result = checkLocalConflict(params[0],params[1]);

            if (TextUtils.isEmpty(classId)) {
                return result;
            }

            if (!result) {

                try {

                    Schedule schedule = new Schedule();
                    schedule.setStart(new Date(params[0]));
                    schedule.setDuration(params[1]);

                    CheckLesson checkLesson = new CheckLesson();
                    checkLesson.schedule = schedule;

                    CheckOverlapParams overlapParams = new CheckOverlapParams();
                    overlapParams.lessons = new ArrayList<>(1);
                    overlapParams.lessons.add(checkLesson);
                    overlapParams.classes = classId;

                    // 去服务器检测
                    if(!LessonDataManager.checkOverlap(getActivity(),overlapParams)) {
                        result = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean conflict) {
            cancelProgress();
            if (conflict) {
                Toast.makeText(getActivity(),
                        R.string.clesson_time_has_conflict,
                        Toast.LENGTH_SHORT).show();
            }else {
                submit();
            }
        }
    }
    
    
    private void submit() {
        
        ClassLesson classLesson = new ClassLesson();
        classLesson.title = nameView.getText().toString().trim();
        classLesson.recordable = recordView.isChecked();

        Schedule schedule = new Schedule();
        schedule.setStart(new Date(lessonStartTime));
        schedule.setDuration(Integer.parseInt(durationView.getText().toString()));

        classLesson.schedule = schedule;

        if (TextUtils.isEmpty(classId)) {
            finishCompleted(classLesson);
        } else{
            commitToClass(classLesson);
        }

        
    }

    private void commitToClass(final ClassLesson classLesson){

        List<ClassLesson> lessons = new ArrayList<>(1);
        lessons.add(classLesson);

        ScheduleParams params = new ScheduleParams();
        params.lessons = lessons;

        showProgress(true);
        LessonDataManager.scheduleClassLesson(getActivity(), classId, params, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();
                Toast.makeText(getActivity(),R.string.add_success,Toast.LENGTH_SHORT).show();
                finishCompleted(classLesson);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(getActivity(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void finishCompleted(ClassLesson classLesson) {
        Intent i = new Intent();
        i.putExtra(EXTRA_CLASS_LESSON, classLesson);
        if(getTargetFragment()!=null){
            getTargetFragment().onActivityResult(getTargetRequestCode(),Activity.RESULT_OK,i);
        }
        dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case REQUEST_NAME_CODE:
                    if (data != null) {
                        String name = data.getStringExtra(AddLessonNameActivity.EXTRA_NAME);
                        if (!TextUtils.isEmpty(name)) {
                            nameView.setText(name);
                        }
                    }
                    break;
            }
        }
    }

    public static AddLessonScheduleFragment createInstance(){
        AddLessonScheduleFragment fragment=new AddLessonScheduleFragment();
        Bundle data=new Bundle();
        fragment.setArguments(data);
        return fragment;
    }
}
