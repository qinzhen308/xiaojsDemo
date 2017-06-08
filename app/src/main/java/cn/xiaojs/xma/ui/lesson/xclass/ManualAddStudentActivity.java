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
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ctl.ClassEnroll;
import cn.xiaojs.xma.model.ctl.ClassEnrollParams;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.home.MomentDetailActivity;
import cn.xiaojs.xma.util.VerifyUtils;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/5/18.
 */

public class ManualAddStudentActivity extends BaseActivity {

    public static final String EXTRA_STUDENTS = "students";



    public static final int REQUEST_ADD_STUDENT_CODE = 0x1;

//    @BindView(R.id.tips_content)
//    TextView tipsContentView;
//    @BindView(R.id.lay_tips)
//    LinearLayout tipsRootView;

    @BindView(R.id.edit_lay)
    LinearLayout editRootLayout;
    @BindView(R.id.student_num)
    EditText numEdit;
    @BindView(R.id.divider)
    View lineView;
    @BindView(R.id.student_name)
    EditText nameEdit;


    @BindView(R.id.student_list)
    PullToRefreshSwipeListView studentsListView;


    private int currentType;
    private String classId;
    private StudentsAdapter adapter;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_manual_add_student);
        setMiddleTitle(getString(R.string.student_list));
        initView();
    }

    @Override
    public void onBackPressed() {

        backCompleted();

        super.onBackPressed();
    }

    @OnClick({R.id.left_image, R.id.right_image2})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                backCompleted();
                break;
//            case R.id.lesson_creation_tips_close://关闭提醒
//                closeCourCreateTips();
//                break;
            case R.id.right_image2://添加
                Intent i = new Intent(this, AddStudentActivity.class);
                startActivityForResult(i, REQUEST_ADD_STUDENT_CODE);
                break;
        }
    }

//    private void closeCourCreateTips() {
//        tipsRootView.setVisibility(View.GONE);
//    }

    private void initView() {

//        tipsContentView.setText(R.string.add_student_tips);
        showAddStatus();

        adapter = new StudentsAdapter(this, studentsListView);

        ArrayList<StudentEnroll> students = new ArrayList<>();
        ArrayList<StudentEnroll> extraStudents = getIntent().getParcelableArrayListExtra(EXTRA_STUDENTS);
        if (extraStudents !=null && extraStudents.size()>0) {
            students.addAll(extraStudents);
        }

        adapter.setList(students);
        studentsListView.setAdapter(adapter);

        studentsListView.setMode(PullToRefreshBase.Mode.DISABLED);

    }


    private void showAddStatus() {

        editRootLayout.setVisibility(View.GONE);

        studentsListView.setVisibility(View.VISIBLE);
        setRightText(R.string.add);
    }

    private void addNewStudent(StudentEnroll studentEnroll) {
        adapter.getList().add(studentEnroll);
        adapter.notifyDataSetChanged();
    }


    private void backCompleted() {

        if (adapter != null) {
            ArrayList<StudentEnroll> studentEnrolls = (ArrayList<StudentEnroll>) adapter.getList();

            Intent i = new Intent();
            i.putParcelableArrayListExtra(EXTRA_STUDENTS, studentEnrolls);
            setResult(RESULT_OK, i);
        }

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADD_STUDENT_CODE:
                    if (data != null) {
                        StudentEnroll studentEnroll = data.getParcelableExtra(AddStudentActivity.EXTRA_STUDENT);
                        //更新页面
                        if (studentEnroll != null) {
                            addNewStudent(studentEnroll);
                        }
                    }
                    break;
            }
        }
    }

    public class StudentsAdapter extends AbsSwipeAdapter<StudentEnroll, StudentsAdapter.Holder> {

        public StudentsAdapter(Context context, PullToRefreshSwipeListView listView) {
            super(context, listView);
        }

        @Override
        protected void setViewContent(Holder holder, StudentEnroll bean, int position) {
            holder.nameView.setText(bean.name);
            holder.phoneView.setText(String.valueOf(bean.mobile));
        }

        @Override
        protected View createContentView(int position) {
            return LayoutInflater.from(mContext).inflate(R.layout.layout_student_name_num_item, null);
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
        protected void onAttachSwipe(TextView mark, TextView del) {
            mark.setVisibility(View.GONE);
            setLeftOffset(mContext.getResources().getDimension(R.dimen.px140));
        }


        @Override
        protected void onSwipeDelete(int position) {
            removeItem(position);
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
