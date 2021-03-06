package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SearchManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionResult;
import cn.xiaojs.xma.model.ctl.ClassEnroll;
import cn.xiaojs.xma.model.ctl.ClassEnrollParams;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.model.search.AccountSearch;
import cn.xiaojs.xma.model.search.SearchResultV2;
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

        numEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone=numEdit.getText().toString().trim();
                if(phone.length()==11){
                    previewStudent(phone);
                }
            }
        });
    }

//    private void closeCourCreateTips() {
//        tipsRootView.setVisibility(View.GONE);
//    }

    private void previewStudent(String phone){
        if (!VerifyUtils.checkPhoneNum(phone)) {
            return;
        }

        StudentEnroll studentEnroll = new StudentEnroll();
        studentEnroll.mobile = phone;

        checkAccount(studentEnroll,true);
    }


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

        checkAccount(studentEnroll,false);

    }


    private void checkAccount(final StudentEnroll studentEnroll, final boolean preview) {
        try {
            showProgress(true);
            //因为已经注册的用户，需要上传用户的ID，所以在这里要检测是否注册，如果注册了就获取ID.
            SearchManager.search(this,
                    Social.SearchType.PERSON,
                    String.valueOf(studentEnroll.mobile),
                    1,
                    10,
                    new APIServiceCallback<CollectionResult<SearchResultV2>>() {
                        @Override
                        public void onSuccess(CollectionResult<SearchResultV2> result) {
                            cancelProgress();
                            boolean hasExist = false;
                            SearchResultV2 searchResultV2 = null;

                            if (result != null && result.results != null && !result.results.isEmpty()) {
                                searchResultV2 = result.results.get(0);
                                hasExist = true;
                            }

                            if (searchResultV2 == null || TextUtils.isEmpty(searchResultV2.id)) {
                                hasExist = false;
                            }

                            if (hasExist) {
                                studentEnroll.id = searchResultV2.id;
                                studentEnroll.name=searchResultV2.basic.getName();
                                if(preview){
                                    nameEdit.setText(studentEnroll.name);
                                }
//                        studentEnroll.name = "";
//                        studentEnroll.mobile = "";
                            }
                            //搜索时不用跳转
                            if(preview)return;

                            //OK
                            toAdd(studentEnroll);

                        }

                        @Override
                        public void onFailure(String errorCode, String errorMessage) {
                            cancelProgress();
                            if(preview)return;
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
