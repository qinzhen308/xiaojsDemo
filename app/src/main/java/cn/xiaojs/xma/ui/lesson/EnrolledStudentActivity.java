package cn.xiaojs.xma.ui.lesson;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ctl.StudentInfo;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.personal.PersonHomeActivity;
import cn.xiaojs.xma.ui.personal.PersonalBusiness;
import cn.xiaojs.xma.ui.widget.CircleTransform;

import static cn.xiaojs.xma.ui.lesson.CourseConstant.KEY_LESSON_ID;

public class EnrolledStudentActivity extends BaseActivity {


    @BindView(R.id.student_grid)
    GridView gridView;

    private String lessonId;

    private int page = 1;
    private int limit = 100000;


    private StudentAdapter studentAdapter;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_enrolled_student);
        setMiddleTitle(getString(R.string.student));

        lessonId = getIntent().getStringExtra(KEY_LESSON_ID);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (studentAdapter != null) {
                     StudentInfo info = studentAdapter.getItem(position);
                     if (info != null) {
                         Intent intent = new Intent(EnrolledStudentActivity.this, PersonHomeActivity.class);
                         intent.putExtra(PersonalBusiness.KEY_PERSONAL_ACCOUNT, info.id);
                         EnrolledStudentActivity.this.startActivity(intent);
                     }
                }
            }
        });

        loadData();
    }

    @OnClick({R.id.left_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
        }

    }

    private void initViews(List<StudentInfo> studentInfos) {

        if (studentInfos == null || studentInfos.size()<=0) {
            //显示空页面
            showEmptyView("还没有同学报名哦～");
            return;
        }

        if (studentAdapter == null) {
            studentAdapter = new StudentAdapter(this);
        }

        studentAdapter.setData(studentInfos);

        gridView.setAdapter(studentAdapter);

    }

    private void loadData() {

        showProgress(true);
        LessonDataManager.getEnrolledStudents(this, lessonId, page, limit,
                new APIServiceCallback<List<StudentInfo>>() {

            @Override
            public void onSuccess(List<StudentInfo> object) {
                cancelProgress();
                initViews(object);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(EnrolledStudentActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
                showFailedView(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadData();
                    }
                });
            }
        });

    }


    private class StudentAdapter extends BaseAdapter {

        private Context context;

        List<StudentInfo> studentInfos;

        private LayoutInflater inflater;
        private CircleTransform circleTransform;

        public StudentAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            circleTransform = new CircleTransform(context);
        }

        public void setData(List<StudentInfo> students) {
            studentInfos = students;
        }


        @Override
        public int getCount() {

            if (studentInfos != null) {
                return studentInfos.size();
            }
            return 0;
        }

        @Override
        public StudentInfo getItem(int position) {

            if (studentInfos != null) {
                return studentInfos.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_student_item, parent, false);
                holder = new ViewHolder();
                holder.photoView = (ImageView)convertView.findViewById(R.id.photo);
                holder.nameview = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            StudentInfo student = getItem(position);

            String name = student.basic.getName();
            holder.nameview.setText(name);

            String avator = Account.getAvatar(student.id,300);

            Glide.with(context)
                    .load(avator)
                    .bitmapTransform(circleTransform)
                    .placeholder(R.drawable.default_avatar_grey)
                    .error(R.drawable.default_avatar_grey)
                    .into(holder.photoView);

            return convertView;
        }
    }

    private static class ViewHolder{
        TextView nameview;
        ImageView photoView;
    }

}
