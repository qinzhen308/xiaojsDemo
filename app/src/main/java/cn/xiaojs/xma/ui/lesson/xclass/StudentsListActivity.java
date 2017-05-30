package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.message.ChooseClassActivity;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;

/**
 * Created by maxiaobao on 2017/5/21.
 */

public class StudentsListActivity extends BaseActivity {

    public static final String EXTRA_CLASS = "class";
    public static final int REQUEST_ADD_STUDENT_CODE = 0x1;

    @BindView(R.id.student_list)
    PullToRefreshSwipeListView listView;

    private String classId;

    private StudentsListAdapter adapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_students_list);
        setMiddleTitle(getString(R.string.student));
        setRightText(R.string.add);

        classId = getIntent().getStringExtra(EXTRA_CLASS);

        initView();

    }

    @OnClick({R.id.left_image, R.id.right_image2, R.id.lay_veri})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image2:
                // 添加
                showAddDlg();
                break;
            case R.id.lay_veri:
                //TODO 进入验证消息页面
                break;

        }
    }

    private void initView() {
        adapter  = new StudentsListAdapter(this,
                listView);
        listView.setAdapter(adapter);
    }

    private void showAddDlg() {

        ListBottomDialog dialog = new ListBottomDialog(this);

        String[] items = getResources().getStringArray(R.array.add_student);
        dialog.setItems(items);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0://手动添加
                        Intent addIntent = new Intent(StudentsListActivity.this,
                                ManualAddStudentActivity.class);
                        addIntent.putExtra(ManualAddStudentActivity.MANUAL_STUDENT_TYPE,
                                ManualAddStudentActivity.TYPE_ADD_ONE_ONLY);
                        addIntent.putExtra(ManualAddStudentActivity.EXTRA_CLASS_ID, classId);
                        startActivityForResult(addIntent,REQUEST_ADD_STUDENT_CODE);
                        break;
                    case 1://从已有班级中添加

                        Intent i = new Intent(StudentsListActivity.this,
                                ChooseClassActivity.class);
                        i.putExtra(ChooseClassActivity.EXTRA_ENTER_TYPE,
                                ChooseClassActivity.ENTER_TYPE_ADD_STUDENT);

                        startActivity(i);
                        break;
                }

            }

        });
        dialog.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADD_STUDENT_CODE:
                    //通过EXTRA_STUDENTS，可以获取到新添加的学生
                    //data.getParcelableArrayListExtra(ManualAddStudentActivity.EXTRA_STUDENTS);
                    //直接通过API刷新界面
                    if(adapter != null) {
                        adapter.resetAndrequest();
                    }
                    break;
            }
        }
    }

    public class StudentsListAdapter extends AbsSwipeAdapter<StudentEnroll,StudentsListAdapter.Holder> {

        public StudentsListAdapter(Context context, PullToRefreshSwipeListView listView) {
            super(context,listView);
        }

        @Override
        protected void setViewContent(StudentsListAdapter.Holder holder, StudentEnroll bean, int position) {
            holder.nameView.setText(bean.name);
            holder.phoneView.setText(String.valueOf(bean.mobile));
        }

        @Override
        protected View createContentView(int position) {
            return LayoutInflater.from(mContext).inflate(R.layout.layout_student_name_num_item,null);
        }

        @Override
        protected StudentsListAdapter.Holder initHolder(View view) {
            return new StudentsListAdapter.Holder(view);
        }

        @Override
        protected void doRequest() {

            LessonDataManager.getClassStudents(mContext, classId, mPagination, new APIServiceCallback<CollectionPage<StudentEnroll>>() {
                @Override
                public void onSuccess(CollectionPage<StudentEnroll> object) {

                    if (object !=null) {
                        StudentsListAdapter.this.onSuccess(object.objectsOfPage);
                    }
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    StudentsListAdapter.this.onFailure(errorCode,errorMessage);
                }
            });

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
        protected void onSwipeDelete(final int position) {
            StudentEnroll studentEnroll = getItem(position);
            if (studentEnroll !=null) {

                showProgress(true);
                LessonDataManager.removeClassStudent(mContext, classId, new String[]{studentEnroll.id}, new APIServiceCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        cancelProgress();
                        removeItem(position);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {

                        cancelProgress();
                        Toast.makeText(mContext,errorMessage,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        public void resetAndrequest() {
            reset();
            request();
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
