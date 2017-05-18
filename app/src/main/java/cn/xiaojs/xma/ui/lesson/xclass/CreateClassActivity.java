package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.text.InputFilter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;

/**
 * Created by maxiaobao on 2017/5/17.
 */

public class CreateClassActivity extends BaseActivity {

    private final int MAX_LESSON_CHAR = 50;

    @BindView(R.id.tips_content)
    TextView tipsContentView;
    @BindView(R.id.lay_tips)
    LinearLayout tipsRootView;

    @BindView(R.id.live_lesson_name)
    EditTextDel classNameEdt;



    @Override
    protected void addViewContent() {
        addView(R.layout.activity_create_class);
        setMiddleTitle(getString(R.string.create_class));


        initView();
    }


    @OnClick({R.id.left_image, R.id.lesson_creation_tips_close,R.id.sub_btn,
            R.id.lay_time_table,R.id.lay_class_student})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.lesson_creation_tips_close://关闭提醒
                closeCourCreateTips();
                break;
            case R.id.sub_btn://完成
                break;
            case R.id.lay_time_table://课表
                startActivity(new Intent(this,CreateTimetableActivity.class));
                break;
            case R.id.lay_class_student://学生
                break;

        }
    }

    private void initView() {

        tipsContentView.setText(R.string.class_create_tips);

        classNameEdt.setHint(getString(R.string.live_lesson_name_hint, MAX_LESSON_CHAR));
        classNameEdt.setForbidEnterChar(true);
        classNameEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LESSON_CHAR)});
    }

    private void closeCourCreateTips() {
        tipsRootView.setVisibility(View.GONE);
    }

}
