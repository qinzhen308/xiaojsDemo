package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Context;
import android.content.Intent;
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

    public static final String EXTRA_STUDENTS = "students";

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

    private boolean isEdit;
    private StudentsAdapter adapter;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_manual_add_student);
        setMiddleTitle(getString(R.string.manual_add_student));
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
            case R.id.right_image2://确定 or 完成
                if (isEdit) {
                    addNewStudent();
                }else{

                    if (adapter !=null) {
                        ArrayList<StudentEnroll> studentEnrolls = (ArrayList<StudentEnroll>) adapter.getList();

                        Intent i = new Intent();
                        i.putParcelableArrayListExtra(EXTRA_STUDENTS,studentEnrolls);

                        setResult(RESULT_OK, i);
                    }


                    finish();
                }

                break;
            case R.id.add_btn://添加
                showEditStatus();
                break;

        }
    }

    private void closeCourCreateTips() {
        tipsRootView.setVisibility(View.GONE);
    }

    private void initView() {

        tipsContentView.setText(R.string.add_student_tips);

        ArrayList<StudentEnroll> students = getIntent().getParcelableArrayListExtra(EXTRA_STUDENTS);

        if (students !=null && students.size() > 0) {
            initAdapter(students);
            showAddStatus();
        }else {
            showEditStatus();
        }

    }

    private void initAdapter(ArrayList<StudentEnroll> students) {
        if (adapter == null) {
            adapter = new StudentsAdapter(this,studentsListView);
            adapter.setList(students);
            studentsListView.setAdapter(adapter);
            studentsListView.setMode(PullToRefreshBase.Mode.DISABLED);
        }
    }

    private void showEditStatus() {

        addButton.setVisibility(View.GONE);

        numEdit.setText("");
        nameEdit.setText("");
        numEdit.setVisibility(View.VISIBLE);
        nameEdit.setVisibility(View.VISIBLE);
        lineView.setVisibility(View.VISIBLE);

        studentsListView.setVisibility(View.GONE);
        setRightText(R.string.ok);

        isEdit = true;
    }

    private void showAddStatus() {

        addButton.setVisibility(View.VISIBLE);

        numEdit.setVisibility(View.GONE);
        nameEdit.setVisibility(View.GONE);
        lineView.setVisibility(View.GONE);

        studentsListView.setVisibility(View.VISIBLE);
        setRightText(R.string.finish);

        isEdit = false;
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

        if (adapter == null) {
            ArrayList<StudentEnroll> studentEnrolls = new ArrayList<>();
            studentEnrolls.add(studentEnroll);
            initAdapter(studentEnrolls);
        }else{
            adapter.getList().add(studentEnroll);
            adapter.notifyDataSetChanged();
        }

        showAddStatus();
    }


    public class StudentsAdapter extends AbsSwipeAdapter<StudentEnroll,StudentsAdapter.Holder> {

        public StudentsAdapter(Context context, PullToRefreshSwipeListView listView) {
            super(context,listView);
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
            return false;
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
