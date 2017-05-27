package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.VerifyUtils;

/**
 * Created by maxiaobao on 2017/5/18.
 */

public class ManualAddStudentActivity extends BaseActivity {

    @BindView(R.id.tips_content)
    TextView tipsContentView;
    @BindView(R.id.lay_tips)
    LinearLayout tipsRootView;

    @BindView(R.id.add_btn)
    Button addButton;
    @BindView(R.id.student_num)
    EditText numEdit;
    @BindView(R.id.divider)
    View lineView;
    @BindView(R.id.student_name)
    EditText nameEdit;


    @BindView(R.id.student_list)
    PullToRefreshSwipeListView studentsListView;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_manual_add_student);
        setMiddleTitle(getString(R.string.manual_add_student));
        setRightText(R.string.ok);
        initView();
    }

    @OnClick({R.id.left_image, R.id.right_image2,R.id.add_btn, R.id.lesson_creation_tips_close})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.lesson_creation_tips_close://关闭提醒
                closeCourCreateTips();
                break;
            case R.id.right_image2://确定
                addNewStudent();
                break;
            case R.id.add_btn://添加
                break;

        }
    }

    private void closeCourCreateTips() {
        tipsRootView.setVisibility(View.GONE);
    }

    private void initView() {

        studentsListView.setMode(PullToRefreshBase.Mode.DISABLED);
        tipsContentView.setText(R.string.add_student_tips);

        ArrayList<StudentEnroll> students = CreateClassActivity.enrollStudents;
        if (students !=null && students.size()>0) {

            showAddStatus();
            StudentsAdapter adapter = new StudentsAdapter(this,studentsListView,students);
            studentsListView.setAdapter(adapter);

        }else {
            showEditStatus();
        }

    }

    private void showEditStatus() {

        addButton.setVisibility(View.GONE);

        numEdit.setVisibility(View.VISIBLE);
        nameEdit.setVisibility(View.VISIBLE);
        lineView.setVisibility(View.VISIBLE);

        studentsListView.setVisibility(View.GONE);
    }

    private void showAddStatus() {

        addButton.setVisibility(View.VISIBLE);

        numEdit.setVisibility(View.GONE);
        nameEdit.setVisibility(View.GONE);
        lineView.setVisibility(View.GONE);

        studentsListView.setVisibility(View.VISIBLE);
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
        if(TextUtils.isEmpty(name)) {
            Toast.makeText(this, "学生姓名必须输入", Toast.LENGTH_SHORT).show();
            return;
        }

        StudentEnroll studentEnroll = new StudentEnroll();
        studentEnroll.mobile = Long.parseLong(phone);
        studentEnroll.name = name;

        CreateClassActivity.addStudent(studentEnroll);

        finish();

    }


    //TODO adapter item view : R.layout.layout_student_name_num_item


    public class StudentsAdapter extends AbsSwipeAdapter<StudentEnroll,StudentsAdapter.Holder> {

        public StudentsAdapter(Context context, PullToRefreshSwipeListView listView, List<StudentEnroll> data) {
            super(context, listView, data);
        }

        @Override
        protected void setViewContent(Holder holder, StudentEnroll bean, int position) {
            holder.nameView.setText(bean.name);
            holder.phoneView.setText(String.valueOf(bean.mobile));
        }

        @Override
        protected View createContentView(int position) {
            return LayoutInflater.from(mContext).inflate(R.layout.layout_student_name_num_item,null);
        }

        @Override
        protected Holder initHolder(View view) {
            return new Holder(view);
        }

        @Override
        protected void doRequest() {

        }

        @Override
        protected boolean leftSwipe() {
            return true;
        }

        @Override
        protected void onSwipeDelete(int position) {
           super.onSwipeDelete(position);
        }

        class Holder extends BaseHolder {
            @BindView(R.id.name)
            TextView nameView;
            @BindView(R.id.phone)
            TextView phoneView;

            public Holder(View view) {
                super(view);
            }
        }
    }
}
