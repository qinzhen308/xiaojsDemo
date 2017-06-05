package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CLResponse;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.ctl.ClassInfo;
import cn.xiaojs.xma.model.ctl.ClassInfoData;
import cn.xiaojs.xma.model.ctl.ModifyClassParams;
import cn.xiaojs.xma.model.ctl.ModifyModeParam;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.grade.ClassMaterialActivity;
import cn.xiaojs.xma.ui.grade.ScheduleActivity;
import cn.xiaojs.xma.util.TimeUtil;

/**
 * Created by maxiaobao on 2017/5/21.
 */

public class ClassInfoActivity extends BaseActivity {

    public static final String EXTRA_CLASSID = "classid";
    public static final String EXTRA_TEACHING = "teaching";
    public static final String EXTRA_VERI = "verify";
    public final int REQUEST_NAME_CODE = 0x1;
    public final int REQUEST_ADD_ONE_STUDENT_CODE = 0x2;
    public final int REQUEST_STUDENT_LIST_CODE = 0x3;

    @BindView(R.id.name)
    TextView nameView;
    @BindView(R.id.teacher_name)
    TextView teacherNameView;
    @BindView(R.id.student_num)
    TextView studentNumView;
    @BindView(R.id.open_time)
    TextView opentimeView;
    @BindView(R.id.creator)
    TextView creatorView;
    @BindView(R.id.ver_layout)
    LinearLayout verLayout;
    @BindView(R.id.veri_switcher)
    ToggleButton toggleButton;
    @BindView(R.id.num_lesson)
    TextView numLessonView;

    private String classId;
    private ClassInfo classInfo;

    private boolean teaching;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_class_info);
        setMiddleTitle(getString(R.string.class_info));
        classId = getIntent().getStringExtra(EXTRA_CLASSID);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadClassInfo();
    }

    @OnClick({R.id.left_image, R.id.enter_btn,
            R.id.lay_time_table, R.id.lay_material,
            R.id.lay_student, R.id.lay_qrcode, R.id.name_lay,R.id.veri_switcher})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.name_lay:
                if (teaching) {
                    modifyName();
                }
                break;
            case R.id.enter_btn:
                //TODO 进入教室
                break;
            case R.id.lay_time_table:
                //课表
                ClassScheduleActivity.invoke(this,classInfo.id, classInfo.title,teaching);
                break;
            case R.id.lay_material:
                //TODO 资料库
                //databank();
                break;
            case R.id.lay_student://学生列表
                if (!teaching ||
                        (classInfo != null && classInfo.join != null && classInfo.join.current > 0)) {
                    Intent i = new Intent(this, StudentsListActivity.class);
                    i.putExtra(StudentsListActivity.EXTRA_CLASS, classId);
                    i.putExtra(EXTRA_TEACHING, teaching);
                    boolean verflag = (classInfo.join != null && classInfo.join.mode == Ctl.JoinMode.VERIFICATION)?
                            true :false;
                    i.putExtra(EXTRA_VERI,verflag);
                    startActivityForResult(i, REQUEST_STUDENT_LIST_CODE);
                } else {
                    Intent addIntent = new Intent(ClassInfoActivity.this,
                            AddStudentActivity.class);
                    addIntent.putExtra(AddStudentActivity.EXTRA_CLASS_ID, classId);
                    startActivityForResult(addIntent, REQUEST_ADD_ONE_STUDENT_CODE);
                }
                break;
            case R.id.lay_qrcode:
                // 二维码
                Intent qrIntent = new Intent(this, ShareQrcodeActivity.class);
                qrIntent.putExtra(ShareQrcodeActivity.EXTRA_QRCODE_TYPE,
                        ShareQrcodeActivity.CLASS_QRCODE);
                startActivity(qrIntent);
                break;
            case R.id.veri_switcher:
                //是否需要验证
                boolean checked = ((ToggleButton) v).isChecked();
                modifyVerify(checked);
                break;
        }


    }

    //资料库
    private void databank() {
//        Intent intent = new Intent(this, ClassMaterialActivity.class);
//        intent.putExtra(ClassMaterialActivity.EXTRA_DELETEABLE,true);
//        intent.putExtra(ClassMaterialActivity.EXTRA_LESSON_ID, bean.getId());
//        intent.putExtra(ClassMaterialActivity.EXTRA_LESSON_NAME, bean.getTitle());
//        mContext.startActivity(intent);
    }


    private void bingView() {

        if (classInfo == null) {
            return;
        }

        teaching = initTeaching();

        if (!teaching) {
            //学生UI
            nameView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        }

        nameView.setText(classInfo.title);

        String teacher = (classInfo.advisers != null && classInfo.advisers.length > 0) ?
                classInfo.advisers[0].name : classInfo.ownerName;

        teacherNameView.setText(teacher);

        numLessonView.setText(getString(R.string.number_lesson, classInfo.lessons));

        int studentCount = classInfo.join != null ? classInfo.join.current : 0;
        studentNumView.setText(getString(R.string.number_student, studentCount));
        opentimeView.setText(TimeUtil.format(classInfo.createdOn, TimeUtil.TIME_YYYY_MM_DD));
        creatorView.setText(classInfo.ownerName);

        if (teaching) {
            verLayout.setVisibility(View.VISIBLE);
            boolean verify = (classInfo.join != null && classInfo.join.mode == Ctl.JoinMode.VERIFICATION) ?
                    true : false;

            toggleButton.setChecked(verify);
        }


    }


    private boolean initTeaching() {
        if (classInfo== null)
            return false;

        String mid = AccountDataManager.getAccountID(this);

        //创建者
        if (mid.equals(classInfo.createdBy)) {
            return true;
        }

        //班主任
        if (classInfo.advisers !=null && classInfo.advisers.length>0) {
            for (Account account : classInfo.advisers){
                if (mid.equals(account.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

//    private void updateStudentsView(int count) {
//        studentNumView.setText(getString(R.string.number_student,count));
//    }
//
//    private void updateLessonsView(int count) {
//        numLessonView.setText(getString(R.string.number_lesson,count));
//    }

    private void modifyName() {
        Intent i = new Intent(this, AddLessonNameActivity.class);
        i.putExtra(AddLessonNameActivity.EXTRA_CLASSID, classId);
        //i.putExtra(AddLessonNameActivity.EXTRA_VER_MODE, classInfo.join.mode);
        i.putExtra(AddLessonNameActivity.EXTRA_NAME, nameView.getText().toString());
        i.putExtra(AddLessonNameActivity.EXTRA_ROLE, AddLessonNameActivity.ROLE_CLASS);
        startActivityForResult(i, REQUEST_NAME_CODE);
    }

    private void loadClassInfo() {
        showProgress(true);
        LessonDataManager.getClassInfo(this, classId, new APIServiceCallback<ClassInfoData>() {
            @Override
            public void onSuccess(ClassInfoData object) {

                cancelProgress();

                if (object != null && object.adviserclass != null) {
                    classInfo = object.adviserclass;
                    bingView();
                } else {
                    showEmptyView(getString(R.string.empty_tips));
                }

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(ClassInfoActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                showFailedView(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadClassInfo();
                    }
                });
            }
        });
    }

    private void modifyVerify(final boolean verify) {

        ModifyModeParam modeParam = new ModifyModeParam();

        int mode = verify? Ctl.JoinMode.VERIFICATION: Ctl.JoinMode.OPEN;
        modeParam.mode = mode;

        showProgress(true);
        LessonDataManager.modifyClass(this, classId, modeParam, new APIServiceCallback<CLResponse>() {
            @Override
            public void onSuccess(CLResponse object) {
                cancelProgress();
                toggleButton.setChecked(verify);
                Toast.makeText(ClassInfoActivity.this,
                        R.string.lesson_edit_success,
                        Toast.LENGTH_SHORT)
                        .show();



            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                toggleButton.setChecked(!verify);
                Toast.makeText(ClassInfoActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_NAME_CODE:
                    if (data != null) {
                        String newName = data.getStringExtra(AddLessonNameActivity.EXTRA_NAME);
                        if (!TextUtils.isEmpty(newName)) {
                            nameView.setText(newName);
                        }
                    }

                    break;
                case REQUEST_STUDENT_LIST_CODE:
                    if (data != null) {
                        //
                    }
                    break;
                case REQUEST_ADD_ONE_STUDENT_CODE:
                    if (data != null) {
                        if (classInfo != null && classInfo.join != null) {
                            classInfo.join.current = 1;
                        }
                    }

                    break;
            }
        }
    }
}
