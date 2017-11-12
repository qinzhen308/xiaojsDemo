package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataChangeHelper;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CLResponse;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.ctl.ClassEnroll;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.model.ctl.ClassParams;
import cn.xiaojs.xma.model.ctl.EnrollImport;
import cn.xiaojs.xma.model.ctl.EnrollStatus;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.common.ShareQrcodeClassroomActivity;
import cn.xiaojs.xma.ui.contact2.ContactFragment;
import cn.xiaojs.xma.ui.contact2.model.AbsContactItem;
import cn.xiaojs.xma.ui.contact2.model.ClassItem;
import cn.xiaojs.xma.ui.contact2.model.FriendItem;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.Common3Dialog;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.util.ShareUtil;
import cn.xiaojs.xma.util.StringUtil;

import static cn.xiaojs.xma.R.string.student;

/**
 * Created by maxiaobao on 2017/5/17.
 */

public class CreateClassActivity extends BaseActivity implements ContactFragment.ChoiceCompletedListener {

    public static final int MAX_CLASS_CHAR = 50;

    private final int REQUEST_CLASS_LESSON_CODE = 0x1;
    private final int REQUEST_CLASS_SCHEDULE_CODE = 0x2;
    private final int REQUEST_MANUAL_STUDENTS_CODE = 0x3;
    private final int REQUEST_IMPORT_STUDENTS_CODE = 0x4;
    private final int REQUEST_ZERO_ADD_STUDENT_CODE = 0x5;

    @BindView(R.id.class_name)
    EditTextDel classNameEdt;
    @BindView(R.id.teacher_name)
    TextView teacherNameView;
    @BindView(R.id.veri_switcher)
    ToggleButton veriSwitcher;
    @BindView(R.id.public_switcher)
    ToggleButton publicSwitcher;

    @BindView(R.id.student_num_tips)
    TextView studentTipsView;
    @BindView(R.id.time_table_tips)
    TextView timetableTipsView;

    @BindView(R.id.label_class)
    TextView labelClass;
    @BindView(R.id.label_teacher)
    TextView labelTeacher;
    @BindView(R.id.label_add_verify)
    TextView labelAddVerify;
    @BindView(R.id.label_public)
    TextView labelPublic;

    @BindView(R.id.cb_allow_guest_chart)
    CheckBox cbAllowGuestChart;
    @BindView(R.id.cb_allow_guest_read)
    CheckBox cbAllowGuestRead;
    @BindView(R.id.guest_switcher)
    ToggleButton guestSwitcher;
    @BindView(R.id.layout_guest_authority)
    View layoutGuestAuthority;

    public static ArrayList<ClassLesson> classLessons;

    private ArrayList<StudentEnroll> enrollStudents;
    private ArrayList<EnrollImport> enrollImports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classLessons = new ArrayList<>();
        ShareQrcodeClassroomActivity.invoke(this,1,"","",new Account());
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

        if (classLessons != null && classLessons.size() > 0) {
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
        setMiddleTitle("新建教室");

        initView();
    }


    @OnClick({R.id.left_image, R.id.sub_btn,
            R.id.lay_time_table, R.id.lay_class_student})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
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

        labelClass.setText(StringUtil.getSpecialString(labelClass.getText().toString() + " *", " *", getResources().getColor(R.color.main_orange)));
//        labelAddVerify.setText(StringUtil.getSpecialString(labelAddVerify.getText().toString()+" *"," *",getResources().getColor(R.color.main_orange)));
//        labelTeacher.setText(StringUtil.getSpecialString(labelTeacher.getText().toString()+" *"," *",getResources().getColor(R.color.main_orange)));


        classNameEdt.setHint(getString(R.string.live_lesson_name_hint, MAX_CLASS_CHAR));
        classNameEdt.setForbidEnterChar(true);
        classNameEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CLASS_CHAR)});


        Account account = AccountDataManager.getAccont(this);
        if (account != null && account.getBasic() != null) {
            String name = account.getBasic().getName();
            teacherNameView.setText(name);
        }

        veriSwitcher.setChecked(true);
        publicSwitcher.setChecked(false);
        veriSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AnalyticEvents.onEvent(CreateClassActivity.this, 39);
                } else {
                    AnalyticEvents.onEvent(CreateClassActivity.this, 40);
                }
            }
        });
        guestSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layoutGuestAuthority.setVisibility(View.VISIBLE);
                } else {
                    layoutGuestAuthority.setVisibility(View.GONE);
                }
            }
        });
        publicSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    showSetPublicTip();
                    String a = getString(R.string.class_need_public);
                    SpannableString spannableString = new SpannableString(a + "\n" + getString(R.string.set_public_tip2));
                    spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.common_text)), a.length() + 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new RelativeSizeSpan(0.8f), a.length() + 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    labelPublic.setText(spannableString);
                } else {
                    labelPublic.setText(R.string.class_need_public);
                }
            }
        });

    }

    private void showSetPublicTip() {
        ListBottomDialog dialog = new ListBottomDialog(this);
        dialog.setTopTitle(R.string.set_public_tip);
        String[] items = new String[]{"设为公开"};
        dialog.setItems(items);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {

            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                publicSwitcher.setChecked(false);
            }
        });
        dialog.setOnCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicSwitcher.setChecked(false);
            }
        });
        dialog.show();
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

                        if (enrollImports != null && enrollImports.size() > 0) {
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
                    case 1://从通讯录中添加
                        if (enrollStudents != null && enrollStudents.size() > 0) {
                            showOverlayTips(true);
                        } else {
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

        } else {
            timetableTipsView.setText("");
            //timetableTipsView.setHint(R.string.arrange_class);
        }

        if (enrollStudents != null && enrollStudents.size() > 0) {
            studentTipsView.setText(getString(R.string.number_student, enrollStudents.size()));
        } else if (enrollImports != null && enrollImports.size() > 0) {
            studentTipsView.setText(getString(R.string.import_class_students_count, enrollImports.size()));
            //studentTipsView.setHint(R.string.please_select);
        } else {
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
        params.join = veriSwitcher.isChecked() ? Ctl.JoinMode.VERIFICATION : Ctl.JoinMode.OPEN;
        params.accessible = publicSwitcher.isChecked();
        params.lessons = classLessons;

        if (guestSwitcher.isChecked()) {
            params.visitor = true;
            params.library = cbAllowGuestRead.isChecked();
            params.talk = cbAllowGuestChart.isChecked();
        } else {
            params.visitor = false;
        }

        ClassEnroll classEnroll = null;
        if (enrollStudents != null && enrollStudents.size() > 0) {
            for (StudentEnroll e : enrollStudents) {
                if (!TextUtils.isEmpty(e.id)) {
                    e.name = "";
                    e.mobile = "";
                }
            }

            if (classEnroll == null) {
                classEnroll = new ClassEnroll();
            }

            classEnroll.students = enrollStudents;
        }

        if ((enrollImports != null && enrollImports.size() > 0)) {

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
                showSuccessTip(object.getId());
                clearClassLessons();

                DataChangeHelper.getInstance()
                        .notifyDataChanged(SimpleDataChangeListener.CREATE_CLASS_CHANGED);
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
        } else {
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
                } else {
                    enterStudentPage();
                }
            }
        });
        dialog.show();

    }

    private void enterImportPage() {

        ContactFragment.invokeWithChoice(getSupportFragmentManager(), ListView.CHOICE_MODE_MULTIPLE, this);

//        Intent i = new Intent(CreateClassActivity.this,
//                ImportStudentFormClassActivity.class);
//        startActivityForResult(i, REQUEST_IMPORT_STUDENTS_CODE);
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
    public void onChoiceData(boolean person, ArrayList<AbsContactItem> data) {

        if (data == null || data.size() <= 0)
            return;

        if (person) {
            ArrayList<StudentEnroll> senrolls = new ArrayList<>(data.size());
            for (AbsContactItem item : data) {
                FriendItem friendItem = (FriendItem) item;
                StudentEnroll studentEnroll = new StudentEnroll();
                studentEnroll.id = friendItem.contact.id;
                studentEnroll.name = friendItem.contact.name;

                senrolls.add(studentEnroll);
            }
            enrollStudents = senrolls;

            //清空导入班级的学生
            if (enrollImports != null) {
                enrollImports.clear();
                enrollImports = null;
            }
        } else {

            ArrayList<EnrollImport> imports = new ArrayList<>(data.size());
            for (AbsContactItem item : data) {
                ClassItem classItem = (ClassItem) item;
                EnrollImport enrollImport = new EnrollImport();
                enrollImport.id = classItem.contact.id;
                enrollImport.subtype = classItem.contact.subtype;

                imports.add(enrollImport);
            }

            enrollImports = imports;

            //清空手动加入的学生
            if (enrollStudents != null) {
                enrollStudents.clear();
                enrollStudents = null;
            }

        }

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
                        if (enrollStudents != null) {
                            //清空导入班级的学生
                            if (enrollImports != null) {
                                enrollImports.clear();
                                enrollImports = null;
                            }
                        }
                    }
                    break;
                case REQUEST_IMPORT_STUDENTS_CODE:
                    if (data != null) {
                        enrollImports = data.getParcelableArrayListExtra(ImportStudentFormClassActivity.EXTRA_IMPORTS);
                        if (enrollImports != null) {
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
                            if (enrollImports != null) {
                                enrollImports.clear();
                                enrollImports = null;
                            }

                        }

                    }
                    break;
            }
        }
    }

    private boolean wannaExit() {

        int status = checkExit();
        if (status < 0) return true;

        final CommonDialog dialog = new CommonDialog(this);
        dialog.setTitle(R.string.create_class_exit_tip2);
        dialog.setLefBtnText(R.string.exit);
        dialog.setRightBtnText(R.string.countine_input);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
                finish();
            }
        });
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
        return false;
    }

    private int checkExit() {
        int tipCount = 0;
        String name = classNameEdt.getText().toString().trim();
        if (!TextUtils.isEmpty(name) && name.length() <= MAX_CLASS_CHAR) {
            tipCount++;
        }
        return tipCount >= 1 ? 1 : -1;

    }

    private void showSuccessTip(final String classId) {
        final Common3Dialog tipsDialog = new Common3Dialog(this);
        View view = LayoutInflater.from(this).inflate(
                R.layout.layout_classroom2_dlg_tips_stopped_live, null);

        tipsDialog.setCancelable(false);
        tipsDialog.setCanceledOnTouchOutside(false);

        ImageView avatorView = (ImageView) view.findViewById(R.id.avator);
        final TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView tipsView = (TextView) view.findViewById(R.id.tips);
        Button okBtn = (Button) view.findViewById(R.id.ok_btn);
        ImageView closeBtn = (ImageView) view.findViewById(R.id.close_btn);

        avatorView.setImageResource(R.drawable.ic_create_classroom_success);

        titleView.setText("创建成功");
        tipsView.setText("一个人玩儿好孤单，邀请好友加入吧");
        okBtn.setText("分享给好友");

        closeBtn.setVisibility(View.VISIBLE);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = ApiManager.getShareLessonUrl(classId, cn.xiaojs.xma.common.xf_foundation.schemas.Account.TypeName.CLASS_LESSON);
                if (url.contains("?")) {
                    url += "&app=android";
                } else {
                    url += "?app=android";
                }
                ShareUtil.shareUrlByUmeng(CreateClassActivity.this, classNameEdt.getText().toString(), AccountDataManager.getAccont(CreateClassActivity.this).getBasic().getName(), url);
            }
        });

        tipsDialog.setOnCloseClickListener(new Common3Dialog.OnClickListener() {
            @Override
            public void onClick() {
                setResult(RESULT_OK);
                finish();
            }
        });
        tipsDialog.setCustomView(view);
        tipsDialog.needCloseBtn(true);
        tipsDialog.show();
        isCreateSuccess = true;

    }

    boolean isCreateSuccess = false;

    @Override
    public void onBackPressed() {
        if (isCreateSuccess) {
            finish();
            return;
        }
        super.onBackPressed();
    }
}

