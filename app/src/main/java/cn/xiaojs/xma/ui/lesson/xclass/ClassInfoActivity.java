package cn.xiaojs.xma.ui.lesson.xclass;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CLResponse;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.ctl.Adviser;
import cn.xiaojs.xma.model.ctl.ClassInfo;
import cn.xiaojs.xma.model.ctl.ClassInfoData;
import cn.xiaojs.xma.model.ctl.ModifyModeParam;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.grade.ClassMaterialActivity;
import cn.xiaojs.xma.ui.widget.Common2Dialog;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
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

    @OnClick({R.id.left_image, R.id.enter_btn,R.id.teacher_name,
            R.id.lay_time_table, R.id.lay_material,
            R.id.lay_student, R.id.lay_qrcode, R.id.name_lay, R.id.veri_switcher, R.id.right_image})
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
                //FIXME 进入教室,没有ticket
                enterClass(this,"");
                break;
            case R.id.lay_time_table:
                //课表
                ClassScheduleActivity.invoke(this, classInfo.id, classInfo.title, teaching);
                break;
            case R.id.lay_material:
                //资料库
                databank();
                break;
            case R.id.lay_student://学生列表


                Intent i = new Intent(this, StudentsListActivity.class);
                i.putExtra(StudentsListActivity.EXTRA_CLASS, classId);
                i.putExtra(EXTRA_TEACHING, teaching);
                boolean verflag = (classInfo.join != null && classInfo.join.mode == Ctl.JoinMode.VERIFICATION) ?
                        true : false;
                i.putExtra(EXTRA_VERI, verflag);
                startActivityForResult(i, REQUEST_STUDENT_LIST_CODE);

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
            case R.id.right_image:
                showMoreDlg();
                break;
            case R.id.teacher_name:
                teacherNamesDialog();

                break;
        }


    }

    private void teacherNamesDialog(){
        if(classInfo==null||classInfo.advisers==null||classInfo.advisers.length<3){
            return;
        }
        Common2Dialog common2Dialog=new Common2Dialog(this);
        ListView content= new ListView(this);
        content.setDivider(new ColorDrawable(getResources().getColor(R.color.main_bg)));
        content.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.px2));
        String[] teachers=new String[classInfo.advisers.length];
        for(int i=0;i<classInfo.advisers.length;i++)teachers[i]=classInfo.advisers[i].name;
        content.setAdapter(new ArrayAdapter<String>(this,R.layout.item_simple_text,R.id.textview,teachers));
        common2Dialog.setTitle(R.string.head_teacher);
        common2Dialog.setCustomView(content);
        common2Dialog.show();
    }

    //资料库
    private void databank() {
        Intent intent = new Intent(this, ClassMaterialActivity.class);
        intent.putExtra(ClassMaterialActivity.EXTRA_DELETEABLE, teaching ? true : false);
        intent.putExtra(ClassMaterialActivity.EXTRA_ID, classInfo.id);
        intent.putExtra(ClassMaterialActivity.EXTRA_TITLE, classInfo.title);
        intent.putExtra(ClassMaterialActivity.EXTRA_SUBTYPE, Collaboration.SubType.PRIVATE_CLASS);
        startActivity(intent);
    }

    //进入教室
    private void enterClass(Activity context, String ticket) {
        Intent i = new Intent();
        i.putExtra(Constants.KEY_TICKET, ticket);
        i.setClass(context, ClassroomActivity.class);
        context.startActivity(i);
    }


    private void bingView() {

        if (classInfo == null) {
            return;
        }

        String mid = AccountDataManager.getAccountID(this);
        //创建者
        if (Ctl.ClassState.IDLE.equals(classInfo.state) && mid.equals(classInfo.createdBy)) {
            setRightImage(R.drawable.ic_title_more_selector);
        }

        teaching = initTeaching();

        if (!teaching) {//学生UI
            nameView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        nameView.setText(classInfo.title);

        //班主任的显示逻辑
        String teacher = "";
        if(classInfo.advisers != null && classInfo.advisers.length > 0){
            teacher+=classInfo.advisers[0].name;
            if(classInfo.advisers.length>1){
                teacher+="、"+classInfo.advisers[0].name;
            }
            if( classInfo.advisers.length>2 ){//大于2个才能点击弹框
                teacher+="...";
                teacherNameView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_entrance,0);
            }else {
                teacherNameView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }
        }else {
            teacher=classInfo.ownerName;
        }

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
        if (classInfo == null)
            return false;

        String mid = AccountDataManager.getAccountID(this);

        //创建者
        if (mid.equals(classInfo.createdBy)) {
            return true;
        }

        //班主任
        if (classInfo.advisers != null && classInfo.advisers.length > 0) {
            for (Account account : classInfo.advisers) {
                if (mid.equals(account.getId())) {
                    return true;
                }
            }
        }

        return false;
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

                        Intent addIntent = new Intent(ClassInfoActivity.this,
                                AddStudentActivity.class);
                        addIntent.putExtra(AddStudentActivity.EXTRA_CLASS_ID, classId);
                        startActivityForResult(addIntent, REQUEST_ADD_ONE_STUDENT_CODE);

                        break;
                    case 1://从已有班级中添加

                        Intent i = new Intent(ClassInfoActivity.this,
                                ImportStudentFormClassActivity.class);
                        i.putExtra(ClassInfoActivity.EXTRA_CLASSID, classId);
                        startActivity(i);
                        break;
                }

            }

        });
        dialog.show();
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

    private void showMoreDlg() {
        ListBottomDialog dialog = new ListBottomDialog(this);
        String[] items = new String[]{getString(R.string.disband_class)};
        dialog.setItems(items);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0://解散
                        showDisbandConfirmDlg(classInfo.id);
                        break;
                }
            }
        });
    }

    //删除
    private void showDisbandConfirmDlg(final String classId) {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.setTitle(R.string.disband_class);
        dialog.setDesc(R.string.disband_class_tip);
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
                disbandClass(classId);
            }
        });
        dialog.show();
    }

    private void disbandClass(String classId) {

        showProgress(true);
        LessonDataManager.removeClass(this, classId, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();
                Toast.makeText(ClassInfoActivity.this, R.string.disband_success, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(ClassInfoActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void modifyVerify(final boolean verify) {

        ModifyModeParam modeParam = new ModifyModeParam();

        int mode = verify ? Ctl.JoinMode.VERIFICATION : Ctl.JoinMode.OPEN;
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
