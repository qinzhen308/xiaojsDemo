package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CLEResponse;
import cn.xiaojs.xma.model.CLResponse;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.model.ctl.ClassParams;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.message.ChooseClassActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;

/**
 * Created by maxiaobao on 2017/5/17.
 */

public class CreateClassActivity extends BaseActivity {

    public static final int MAX_CLASS_CHAR = 50;

    private final int REQUEST_CLASS_LESSON_CODE = 0x1;

    @BindView(R.id.tips_content)
    TextView tipsContentView;
    @BindView(R.id.lay_tips)
    LinearLayout tipsRootView;

    @BindView(R.id.class_name)
    EditTextDel classNameEdt;
    @BindView(R.id.teacher_name)
    TextView teacherNameView;
    @BindView(R.id.verify_group)
    RadioGroup verifyGroupView;
    @BindView(R.id.not_verify)
    RadioButton notVerifyBtn;

    private ArrayList<ClassLesson> classLessons;


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
                startActivityForResult(new Intent(this, CreateTimetableActivity.class),
                        REQUEST_CLASS_LESSON_CODE);
                break;
            case R.id.lay_class_student://学生
                addStudents();
                break;

        }
    }

    private void initView() {

        tipsContentView.setText(R.string.class_create_tips);

        classNameEdt.setHint(getString(R.string.live_lesson_name_hint, MAX_CLASS_CHAR));
        classNameEdt.setForbidEnterChar(true);
        classNameEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CLASS_CHAR)});


        Account account = AccountDataManager.getAccont(this);
        if (account != null && account.getBasic() != null) {
            String name = account.getBasic().getName();
            teacherNameView.setText(name);
        }

        notVerifyBtn.setChecked(true);



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
        //创建班

        String name = classNameEdt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this,R.string.class_name_not_null,Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.length() > MAX_CLASS_CHAR) {
            String nameEr = getString(R.string.class_name_char_error, MAX_CLASS_CHAR);
            Toast.makeText(this, nameEr, Toast.LENGTH_SHORT).show();
            return;
        }

        ClassParams params = new ClassParams();

        params.title = name;
        params.join = verifyGroupView.getCheckedRadioButtonId() == R.id.need_verify?
                Ctl.JoinMode.VERIFICATION : Ctl.JoinMode.OPEN;
        params.lessons = classLessons;



        showProgress(true);
        LessonDataManager.createClass(this, params, new APIServiceCallback<CLResponse>() {
            @Override
            public void onSuccess(CLResponse object) {
                cancelProgress();
                Toast.makeText(CreateClassActivity.this,
                        R.string.create_class_success,
                        Toast.LENGTH_SHORT)
                        .show();

                finish();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(CreateClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case REQUEST_CLASS_LESSON_CODE:
                    if (data != null) {

                        ClassLesson clesson = (ClassLesson) data.getSerializableExtra(
                                CreateTimetableActivity.EXTRA_CLASS_LESSON);
                        if (clesson !=null) {
                            if(classLessons == null) {
                                classLessons = new ArrayList<>();
                            }
                            classLessons.add(clesson);
                        }
                    }
                    break;
            }
        }
    }
}

