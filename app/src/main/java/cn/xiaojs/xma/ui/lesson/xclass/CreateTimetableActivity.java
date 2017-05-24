package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.DataPicker;
import cn.xiaojs.xma.util.TimeUtil;

/**
 * Created by maxiaobao on 2017/5/17.
 */

public class CreateTimetableActivity extends BaseActivity {

    private final static int HALF_HOUR = 30 * 60 * 1000; //30 minutes

    @BindView(R.id.lesson_subject)
    TextView beginTimeView;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_create_timetable);
        setMiddleTitle(R.string.time_table);
        setRightText(R.string.finish);

        initView();
    }

    @OnClick({R.id.left_image, R.id.right_image2, R.id.lay_class_name, R.id.lay_begin_time})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image2://完成
                break;
            case R.id.lay_class_name://填写班级名称
                startActivity(new Intent(this, AddLessonNameActivity.class));
                break;
            case R.id.lay_begin_time://选择上课时间
                selectStartTime();
                break;
        }
    }


    private void initView() {

    }

    private void selectStartTime() {

        DataPicker.pickFutureDate(this, new Date(System.currentTimeMillis() + HALF_HOUR),
                new DataPicker.OnDatePickListener() {
                    @Override
                    public void onDatePicked(int year, int month, int day,
                                             int hour, int minute, int second) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, hour, minute, second);

                        String dateStr = TimeUtil.formatDate(calendar.getTimeInMillis(),
                                TimeUtil.TIME_YYYY_MM_DD_HH_MM);
                        beginTimeView.setText(dateStr);

                    }
                });
    }
}
