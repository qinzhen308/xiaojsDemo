package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.os.Bundle;
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
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataChangeHelper;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CLResponse;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.ctl.ClassEnroll;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.model.ctl.ClassParams;
import cn.xiaojs.xma.model.ctl.EnrollImport;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.message.ChooseClassActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;

/**
 * Created by maxiaobao on 2017/5/17.
 */

public class CreateClassActivity extends BaseActivity {

    public static final int MAX_CLASS_CHAR = 50;

    private final int REQUEST_CLASS_LESSON_CODE = 0x1;
    private final int REQUEST_CLASS_SCHEDULE_CODE = 0x2;
    private final int REQUEST_MANUAL_STUDENTS_CODE = 0x3;
    private final int REQUEST_IMPORT_STUDENTS_CODE = 0x4;
    private final int REQUEST_ZERO_ADD_STUDENT_CODE = 0x5;

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

    @BindView(R.id.student_num_tips)
    TextView studentTipsView;
    @BindView(R.id.time_table_tips)
    TextView timetableTipsView;


    public static ArrayList<ClassLesson> classLessons;
    private ArrayList<StudentEnroll> enrollStudents;
    private ArrayList<EnrollImport> enrollImports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classLessons=new ArrayList<>();
    }

    public static void addClassLesson(ClassLesson lesson) {

        if (lesson == null) return;

        if (classLessons == null) {
            classLessons = new ArrayList<>();
        }

        classLessons.add(lesson);

    }

    public static void removeClassLesson(ClassLesson lesson) {
        if (lesson == null) return;

        if (classLessons != null && classLessons.size()>0) {
            classLessons.remove(lesson);
        }
    }

    public static void clearClassLessons() {
        if (classLessons != null) {
            classLessons.clear();
            classLessons = null;
        }
    }

    @Override
    protected void onDestroy() {

        clearClassLessons();

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateCountView();
    }

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
                startActivityForResult(new Intent(this, LessonScheduleActivity.class),
                        REQUEST_CLASS_SCHEDULE_CODE);
//                if (classLessons != null && classLessons.size() > 0) {
//                    startActivityForResult(new Intent(this, LessonScheduleActivity.class),
//                            REQUEST_CLASS_SCHEDULE_CODE);
//                } else {
//                    startActivityForResult(new Intent(this, CreateTimetableActivity.class),
//                            REQUEST_CLASS_LESSON_CODE);
//                }

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
        dialog.setMiddleText(getString(R.string.add_student_tips));
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

                        if (enrollImports !=null && enrollImports.size() > 0) {
                            showOverlayTips(false);
                        } else {
                            enterStudentPage();
                        }

                        //if (enrollStudents !=null && enrollStudents.size()>0) {
//                        Intent ic = new Intent(CreateClassActivity.this,
//                                ManualAddStudentActivity.class);
//                        if (enrollStudents != null && enrollStudents.size() > 0) {
//                            ic.putExtra(ManualAddStudentActivity.EXTRA_STUDENTS, enrollStudents);
//                        }
//                        startActivityForResult(ic, REQUEST_MANUAL_STUDENTS_CODE);
//                        }else {
//
//                            Intent i = new Intent(CreateClassActivity.this, AddStudentActivity.class);
//                            startActivityForResult(i, REQUEST_ZERO_ADD_STUDENT_CODE);
//                        }

                        break;
                    case 1://从已有班级中添加
                        if (enrollStudents !=null && enrollStudents.size()>0) {
                            showOverlayTips(true);
                        }else {
                            enterImportPage();
                        }

                        break;
                }

            }

        });
        dialog.show();
    }

    private void updateCountView() {

        if (classLessons != null && classLessons.size() > 0) {
            timetableTipsView.setText(getString(R.string.number_lesson, classLessons.size()));

        } else{
            timetableTipsView.setText("");
            //timetableTipsView.setHint(R.string.arrange_class);
        }

        if (enrollStudents != null && enrollStudents.size() > 0) {
            studentTipsView.setText(getString(R.string.number_student, enrollStudents.size()));
        } else if (enrollImports != null && enrollImports.size() > 0){
            studentTipsView.setText(getString(R.string.import_class_students_count, enrollImports.size()));
            //studentTipsView.setHint(R.string.please_select);
        }else {
            studentTipsView.setText("");
        }
    }


    private void createClass() {
        //创建班

        String name = classNameEdt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.class_name_not_null, Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.length() > MAX_CLASS_CHAR) {
            String nameEr = getString(R.string.class_name_char_error, MAX_CLASS_CHAR);
            Toast.makeText(this, nameEr, Toast.LENGTH_SHORT).show();
            return;
        }

        ClassParams params = new ClassParams();

        params.title = name;
        params.join = verifyGroupView.getCheckedRadioButtonId() == R.id.need_verify ?
                Ctl.JoinMode.VERIFICATION : Ctl.JoinMode.OPEN;
        params.lessons = classLessons;

        ClassEnroll classEnroll = null;
        if (enrollStudents != null && enrollStudents.size() > 0) {
            for(StudentEnroll e:enrollStudents){
                if(!TextUtils.isEmpty(e.id)){
                    e.name="";
                    e.mobile="";
                }
            }

            if (classEnroll == null) {
                classEnroll = new ClassEnroll();
            }

            classEnroll.students = enrollStudents;
        }

        if((enrollImports != null && enrollImports.size() > 0)){

            if (classEnroll == null) {
                classEnroll = new ClassEnroll();
            }

            classEnroll.importe = enrollImports;

        }
        params.enroll = classEnroll;
        showProgress(true);
        LessonDataManager.createClass(this, params, new APIServiceCallback<CLResponse>() {
            @Override
            public void onSuccess(CLResponse object) {
                cancelProgress();
                Toast.makeText(CreateClassActivity.this,
                        R.string.create_class_success,
                        Toast.LENGTH_SHORT)
                        .show();
                clearClassLessons();

                DataChangeHelper.getInstance()
                        .notifyDataChanged(SimpleDataChangeListener.CREATE_CLASS_CHANGED);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(CreateClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showOverlayTips(final boolean imports) {

        final CommonDialog dialog = new CommonDialog(this);
        //dialog.setTitle(R.string.disband_class);
        if (imports) {
            dialog.setDesc(R.string.overlay_students_tips);
        }else {
            dialog.setDesc(R.string.overlay_import_tips);
        }
        dialog.setRightBtnText(R.string.go_on);
        dialog.setLefBtnText(R.string.cancel);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
            }
        });
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
                if (imports) {
                    enterImportPage();
                }else {
                    enterStudentPage();
                }
            }
        });
        dialog.show();

    }

    private void enterImportPage() {
        Intent i = new Intent(CreateClassActivity.this,
                ImportStudentFormClassActivity.class);
        startActivityForResult(i, REQUEST_IMPORT_STUDENTS_CODE);
    }

    private void enterStudentPage() {
        Intent ic = new Intent(CreateClassActivity.this,
                ManualAddStudentActivity.class);
        if (enrollStudents != null && enrollStudents.size() > 0) {
            ic.putExtra(ManualAddStudentActivity.EXTRA_STUDENTS, enrollStudents);
        }
        startActivityForResult(ic, REQUEST_MANUAL_STUDENTS_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CLASS_LESSON_CODE:
                    if (data != null) {

                        ClassLesson clesson = (ClassLesson) data.getSerializableExtra(
                                CreateTimetableActivity.EXTRA_CLASS_LESSON);
                        addClassLesson(clesson);

                    }
                    break;
                case REQUEST_MANUAL_STUDENTS_CODE:
                    if (data != null) {
                        enrollStudents = data.getParcelableArrayListExtra(ManualAddStudentActivity.EXTRA_STUDENTS);
                        if (enrollStudents !=null) {
                            //清空导入班级的学生
                            if(enrollImports !=null) {
                                enrollImports.clear();
                                enrollImports = null;
                            }
                        }
                    }
                    break;
                case REQUEST_IMPORT_STUDENTS_CODE:
                    if (data != null) {
                        enrollImports = data.getParcelableArrayListExtra(ImportStudentFormClassActivity.EXTRA_IMPORTS);
                        if (enrollImports !=null) {
                            //清空手动加入的学生
                            if (enrollStudents != null) {
                                enrollStudents.clear();
                                enrollStudents = null;
                            }
                        }
                    }
                    break;
                case REQUEST_ZERO_ADD_STUDENT_CODE:
                    if (data != null) {
                        StudentEnroll studentEnroll = data.getParcelableExtra(AddStudentActivity.EXTRA_STUDENT);

                        if (studentEnroll != null) {
                            if (enrollStudents == null) {
                                enrollStudents = new ArrayList<>();
                            }
                            enrollStudents.add(studentEnroll);

                            //清空导入班级的学生
                            if(enrollImports !=null) {
                                enrollImports.clear();
                                enrollImports = null;
                            }

                        }

                    }
                    break;
            }
        }
    }


}

