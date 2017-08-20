package cn.xiaojs.xma.ui.recordlesson;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SearchManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionResult;
import cn.xiaojs.xma.model.ctl.ClassEnroll;
import cn.xiaojs.xma.model.ctl.ClassEnrollParams;
import cn.xiaojs.xma.model.ctl.EnrollImport;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.model.search.SearchResultV2;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.xclass.ImportStudentFormClassActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ManualAddStudentActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.VerifyUtils;
import okhttp3.ResponseBody;

import static cn.xiaojs.xma.R.id.encode_failed;
import static cn.xiaojs.xma.R.id.mobile_network_set_layout;
import static cn.xiaojs.xma.R.id.text;
import static cn.xiaojs.xma.ui.lesson.xclass.ImportStudentFormClassActivity.EXTRA_IMPORTS;

/**
 * Created by maxiaobao on 2017/7/24.
 */

public class ManualRegistrationActivity extends BaseActivity {

    private final int REQUEST_IMPORT_CODE = 0x1;

    @BindView(R.id.tips_content)
    TextView tipsContentView;
    @BindView(R.id.lay_tips)
    LinearLayout tipsRootView;

    @BindView(R.id.phone_num)
    EditTextDel phoneView;
    @BindView(R.id.name)
    EditTextDel nameView;
    @BindView(R.id.remark)
    EditText remarkView;
    @BindView(R.id.limit_count)
    TextView limltCountView;

    private String courseId;

    private String studentId;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_manual_registration);
        setMiddleTitle(R.string.man_register);
        setRightText(R.string.import_from_class_or_lesson);
        tipsContentView.setText(R.string.phone_usage_tips);

        courseId = getIntent().getStringExtra(CourseConstant.KEY_LESSON_ID);

    }

    @OnClick({R.id.left_image,R.id.right_view,R.id.complete_btn, R.id.lesson_creation_tips_close})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:                  //返回
                finish();
                break;
            case R.id.right_view:                //从班／课中导入
                Intent i = new Intent(this, ImportStudentFormClassActivity.class);
                startActivityForResult(i,REQUEST_IMPORT_CODE);
                break;
            case R.id.lesson_creation_tips_close:  //关闭提醒
                closeCourCreateTips();
                break;
            case R.id.complete_btn:                //完成
                complete();
                break;
        }
    }

    @OnTextChanged(value = R.id.remark, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void remarkTextChanged(Editable editable) {
        limltCountView.setText(editable.length()+ "/100");
    }

    @OnTextChanged(value = R.id.phone_num, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void phoneTextChanged(Editable editable) {

        String phone = editable.toString().trim();

        if (phone.length() ==11 && VerifyUtils.checkPhoneNum(phone)) {

            searchAccountInfo(phone);
        }else {
            studentId = "";
        }

    }



    private void closeCourCreateTips() {
        tipsRootView.setVisibility(View.GONE);
    }


    private void searchAccountInfo(String keyWord) {
        showProgress(true);
        SearchManager.search(this,
                Social.SearchType.PERSON,
                keyWord,
                1,
                10,
                new APIServiceCallback<CollectionResult<SearchResultV2>>() {
                    @Override
                    public void onSuccess(CollectionResult<SearchResultV2> result) {
                        if (result != null && result.results != null && result.results.size()>0) {

                            SearchResultV2 searchResultV2 = result.results.get(0);
                            if (searchResultV2 != null && searchResultV2.basic!=null ) {
                                studentId = searchResultV2.id;
                                nameView.setText(searchResultV2.basic.getName());
                            }
                        }
                        cancelProgress();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        cancelProgress();
                    }
                });
    }


    public void complete() {
        String phone = phoneView.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return ;
        }

        if (!VerifyUtils.checkPhoneNum(phone)) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return ;
        }

        String name = nameView.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            return ;
        }

        if(AccountDataManager.getPhone(this).equals(phone)) {
            Toast.makeText(this, "自己的课，无需报名", Toast.LENGTH_SHORT).show();
            return;
        }


        StudentEnroll enroll = new StudentEnroll();

        if (TextUtils.isEmpty(studentId)) {
            enroll.mobile = phone;
            enroll.name = name;
        }else {
            enroll.id = studentId;
        }


        addStudent(enroll, null);


    }


    private void addStudent(StudentEnroll enroll, ArrayList<EnrollImport> imports) {
        ArrayList<StudentEnroll> studentEnrolls = null;
        if (enroll !=null) {
            studentEnrolls = new ArrayList<>(1);
            studentEnrolls.add(enroll);
        }

        ClassEnrollParams params = new ClassEnrollParams();
        ClassEnroll classEnroll = new ClassEnroll();

        classEnroll.students = studentEnrolls;
        classEnroll.importe = imports;
        params.enroll = classEnroll;

        showProgress(true);
        LessonDataManager.addRecordedCourseStudent(this, courseId, params, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {

                cancelProgress();
                Toast.makeText(ManualRegistrationActivity.this, R.string.add_success, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Toast.makeText(ManualRegistrationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                cancelProgress();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMPORT_CODE:
                    if (data !=null) {
                        ArrayList<EnrollImport> imports = data.getParcelableArrayListExtra(EXTRA_IMPORTS);
                        if (imports !=null && imports.size() >0) {
                            addStudent(null, imports);
                        }
                    }

                    break;
            }
        }
    }
}
