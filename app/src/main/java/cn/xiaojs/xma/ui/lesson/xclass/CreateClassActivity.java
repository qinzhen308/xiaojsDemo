package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.text.InputFilter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.message.ChooseClassActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;

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


    @OnClick({R.id.left_image, R.id.lesson_creation_tips_close, R.id.sub_btn,
            R.id.lay_time_table, R.id.lay_class_student})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.lesson_creation_tips_close://关闭提醒
                closeCourCreateTips();
                break;
            case R.id.sub_btn://完成
                createClass();
                //startActivity(new Intent(this, ShareQrcodeActivity.class));
                break;
            case R.id.lay_time_table://课表
                startActivity(new Intent(this, CreateTimetableActivity.class));
                break;
            case R.id.lay_class_student://学生
                addStudents();
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

    private void addStudents() {

        ListBottomDialog dialog = new ListBottomDialog(this);

        String[] items = getResources().getStringArray(R.array.add_student);
        dialog.setMiddleText(getString(R.string.add_student_type_tips));
        dialog.setItems(items);
        dialog.setTitleVisibility(View.VISIBLE);
        dialog.setTitleBackground(R.color.white);
        dialog.setRightBtnVisibility(View.GONE);
        dialog.setLeftBtnVisibility(View.GONE);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0://手动添加
                        startActivity(new Intent(CreateClassActivity.this,
                                ManualAddStudentActivity.class));
                        break;
                    case 1://从已有班级中添加

                        Intent i = new Intent(CreateClassActivity.this,
                                ChooseClassActivity.class);
                        i.putExtra(ChooseClassActivity.EXTRA_ENTER_TYPE,
                                ChooseClassActivity.ENTER_TYPE_ADD_STUDENT);

                        startActivity(i);
                        break;
                }

            }

        });
        dialog.show();
    }


    private void createClass() {
        //TODO 创建班




    }




}
