package cn.xiaojs.xma.ui.lesson.xclass;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.model.ctl.CheckLesson;
import cn.xiaojs.xma.model.ctl.CheckOverlapParams;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.model.ctl.ScheduleLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.schedule.AddLessonNameFragment;
import cn.xiaojs.xma.ui.classroom2.schedule.AddLessonScheduleFragment;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.DataPicker;
import cn.xiaojs.xma.util.TimeUtil;

/**
 * Created by Paul Z on 2017/11/16.
 */

public class EditTimetableFragment extends BottomSheetFragment
        implements DialogInterface.OnKeyListener {

    private final static int HALF_HOUR = 30 * 60 * 1000; //30 minutes
    private final int MAX_LESSON_DURATION = 600; //600 minutes, 10 hours
    private final int MIN_CLASS_LESSON_DURATION = 10; //10 minutes

    public static final String EXTRA_C_LESSON = "class_lesson";

    private final int REQUEST_NAME_CODE = 0x1;


    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.lesson_name_text)
    TextView nameView;
    @BindView(R.id.lesson_time)
    TextView timeView;
    @BindView(R.id.lesson_duration)
    EditTextDel durationView;
    @BindView(R.id.record_view)
    CheckBox recordView;

    private long lessonStartTime;

    private String classId;
    private ScheduleLesson cLesson;

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

    protected void initView() {
        titleView.setText("编辑课");
        classId = ClassroomEngine.getEngine().getCtlSession().cls.id;
        cLesson = (ScheduleLesson) getArguments().getSerializable(EXTRA_C_LESSON);
        lessonStartTime=cLesson.schedule.getStart().getTime();
        nameView.setText(cLesson.title);
        timeView.setText(TimeUtil.format(cLesson.schedule.getStart(), TimeUtil.TIME_YYYY_MM_DD_HH_MM));
        durationView.setText(String.valueOf(cLesson.schedule.getDuration()));
        recordView.setChecked(cLesson.recordable);
    }

    @OnClick({R.id.iv_back, R.id.btn_finish, R.id.lay_class_name, R.id.lay_begin_time})
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
                fragment.show(getFragmentManager(),"modify_name");
                break;
            case R.id.lay_begin_time://选择上课时间
                selectStartTime();
                break;
        }
    }


    private void selectStartTime() {

        long timesp = System.currentTimeMillis() + HALF_HOUR;

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


        //FIXME 如果时间未修改，择不需要检测冲突
        //此处判断时间是否有冲突
        new CheckConflictTask().execute(lessonStartTime,
                Long.parseLong(durationView.getText().toString()));


    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return false;
    }



    private class CheckConflictTask extends AsyncTask<Long, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            showProgress(false);
        }

        @Override
        protected Boolean doInBackground(Long... params) {

            boolean result = false;
            try {
                Schedule schedule = new Schedule();
                schedule.setStart(new Date(params[0]));
                schedule.setDuration(params[1]);

                CheckLesson checkLesson = new CheckLesson();
                checkLesson.schedule = schedule;
                if(cLesson!=null){
                    checkLesson.id=cLesson.id;
                }
                CheckOverlapParams overlapParams = new CheckOverlapParams();
                overlapParams.classes = classId;
                overlapParams.lessons = new ArrayList<>(1);
                overlapParams.lessons.add(checkLesson);

                // 去服务器检测
                if (!LessonDataManager.checkOverlap(getActivity(), overlapParams)) {
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            } else {
                submit();
            }
        }
    }


    private void submit() {

        ClassLesson classLesson = new ClassLesson();

        String title = nameView.getText().toString().trim();
        if (!title.equals(cLesson.title)) {
            classLesson.title = title;
        }

        //FIXME 需要修改
        classLesson.recordable = recordView.isChecked();

        Schedule schedule = new Schedule();
        schedule.setStart(new Date(lessonStartTime));
        schedule.setDuration(Integer.parseInt(durationView.getText().toString()));

        classLesson.schedule = schedule;
        editClasslesson(classLesson);


    }

    private void editClasslesson(final ClassLesson classLesson) {

        showProgress(true);
        LessonDataManager.modifyClassesLesson(getActivity(), classId, cLesson.id, classLesson, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();
                Toast.makeText(getActivity(), R.string.lesson_edit_success, Toast.LENGTH_SHORT).show();
                if(getTargetFragment()!=null){
                    getTargetFragment().onActivityResult(getTargetRequestCode(),Activity.RESULT_OK,new Intent());
                }
                dismiss();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }


//    private void finishCompleted(ClassLesson classLesson) {
//        Intent i = new Intent();
//        i.putExtra(EXTRA_C_LESSON, classLesson);
//        setResult(RESULT_OK,i);
//        finish();
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
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


    public static EditTimetableFragment createInstance(ScheduleLesson lesson){
        EditTimetableFragment fragment=new EditTimetableFragment();
        Bundle data=new Bundle();
        data.putSerializable(EXTRA_C_LESSON,lesson);
        fragment.setArguments(data);
        return fragment;
    }
}
