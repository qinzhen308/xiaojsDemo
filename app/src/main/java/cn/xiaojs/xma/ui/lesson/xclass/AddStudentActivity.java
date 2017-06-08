package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SearchManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ctl.ClassEnroll;
import cn.xiaojs.xma.model.ctl.ClassEnrollParams;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.model.search.AccountSearch;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.VerifyUtils;

/**
 * Created by maxiaobao on 2017/6/2.
 */

public class AddStudentActivity extends BaseActivity {


    public static final String EXTRA_CLASS_ID = "classid";
    public static final String EXTRA_STUDENT = "student";


//    @BindView(R.id.tips_content)
//    TextView tipsContentView;
//    @BindView(R.id.lay_tips)
//    LinearLayout tipsRootView;


    @BindView(R.id.student_num)
    EditText numEdit;
    @BindView(R.id.divider)
    View lineView;
    @BindView(R.id.student_name)
    EditText nameEdit;

    @BindView(R.id.student_list)
    PullToRefreshSwipeListView studentsListView;

    private String classId;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_manual_add_student);
        setMiddleTitle(getString(R.string.manual_add_student));

        classId = getIntent().getStringExtra(EXTRA_CLASS_ID);

        initView();
    }

    @OnClick({R.id.left_image, R.id.right_image2})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
//            case R.id.lesson_creation_tips_close://关闭提醒
//                closeCourCreateTips();
//                break;
            case R.id.right_image2://确定
                addNewStudent();
                break;

        }
    }


    private void initView() {

//        tipsRootView.setVisibility(View.VISIBLE);

        numEdit.setText("");
        nameEdit.setText("");
        numEdit.setVisibility(View.VISIBLE);
        nameEdit.setVisibility(View.VISIBLE);
        lineView.setVisibility(View.VISIBLE);

        studentsListView.setVisibility(View.GONE);
        setRightText(R.string.ok);
    }

//    private void closeCourCreateTips() {
//        tipsRootView.setVisibility(View.GONE);
//    }


    private void addNewStudent() {
        String phone = numEdit.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, R.string.phone_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!VerifyUtils.checkPhoneNum(phone)) {
            Toast.makeText(this, R.string.phone_error, Toast.LENGTH_SHORT).show();
            return;
        }


        String name = nameEdit.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "学生姓名必须输入", Toast.LENGTH_SHORT).show();
            return;
        }

        StudentEnroll studentEnroll = new StudentEnroll();
        studentEnroll.mobile = phone;
        studentEnroll.name = name;

        checkAccount(studentEnroll);

    }


    private void checkAccount(final StudentEnroll studentEnroll) {
        try {
            showProgress(false);
            //因为已经注册的用户，需要上传用户的ID，所以在这里要检测是否注册，如果注册了就获取ID.
            SearchManager.searchAccounts(this, String.valueOf(studentEnroll.mobile), new APIServiceCallback<ArrayList<AccountSearch>>() {
                @Override
                public void onSuccess(ArrayList<AccountSearch> object) {
                    boolean hasExist = false;
                    AccountSearch currSearch = null;
                    if (object != null && !object.isEmpty()) {
                        for (AccountSearch search : object) {
                            //FIXME 此处的目的是判断个人账号，排除掉机构账号。但是目前个人账号的type返回的是account
                            if (Account.TypeName.PERSION.equals(search._type) || "account".equals(search._type)) {
                                hasExist = true;
                                currSearch = search;
                                break;
                            }
                        }
                    }

                    if (currSearch == null || TextUtils.isEmpty(currSearch._id)) {
                        hasExist = false;
                    }

                    if (hasExist) {
                        studentEnroll.id = currSearch._id;
//                        studentEnroll.name = "";
//                        studentEnroll.mobile = "";
                    }

                    //OK
                    toAdd(studentEnroll);

                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();

                    //OK
                    toAdd(studentEnroll);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            //次数如果发生异常，也要添加
            toAdd(studentEnroll);

        }
    }


    private void toAdd(StudentEnroll studentEnroll) {
        if (TextUtils.isEmpty(classId)) {
            addCompleted(studentEnroll);
        }else {
            submitAdd(studentEnroll);
        }
    }

    private void addCompleted(StudentEnroll studentEnroll) {
        Intent i = new Intent();
        i.putExtra(EXTRA_STUDENT, studentEnroll);
        setResult(RESULT_OK, i);
        finish();
    }

    private void submitAdd(final StudentEnroll studentEnroll) {

        if (!TextUtils.isEmpty(studentEnroll.id)) {
            studentEnroll.name = "";
            studentEnroll.mobile = "";
        }


        final ArrayList<StudentEnroll> studentEnrolls = new ArrayList<>(1);

        ClassEnrollParams enrollParams = new ClassEnrollParams();

        ClassEnroll classEnroll = new ClassEnroll();

        studentEnrolls.add(studentEnroll);
        classEnroll.students = studentEnrolls;
        enrollParams.enroll = classEnroll;

        showProgress(true);
        LessonDataManager.addClassStudent(this, classId, enrollParams, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

                cancelProgress();
                addCompleted(studentEnroll);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(AddStudentActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
