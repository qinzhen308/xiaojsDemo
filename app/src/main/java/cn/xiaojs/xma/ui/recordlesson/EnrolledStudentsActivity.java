package cn.xiaojs.xma.ui.recordlesson;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.ctl.Enroll;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.model.recordedlesson.RLStudentsCriteria;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.util.ToastUtil;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/7/24.
 */

public class EnrolledStudentsActivity extends BaseActivity {

    public static final String EXTRA_LESSON_ID="extra_lesson_id";
    public static final String EXTRA_NEED_VERIFICATION="extra_need_verification";
    public static final String EXTRA_IM_OWNER="extra_im_owner";

    @BindView(R.id.student_list)
    PullToRefreshSwipeListView listView;

    @BindView(R.id.lay_veri)
    RelativeLayout verLayout;
    @BindView(R.id.veri_count)
    TextView veriCount;
    private String lessonId;


    private boolean teaching;

    private StudentsAdapter adapter;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_enrolled_students);
        setMiddleTitle(R.string.enroll_register_stu);
        setRightText(R.string.registration);
        lessonId=getIntent().getStringExtra(EXTRA_LESSON_ID);
        teaching=getIntent().getBooleanExtra(EXTRA_IM_OWNER,false);
        init();
    }

    @OnClick({R.id.left_image, R.id.right_image2, R.id.lay_veri})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:      //返回
                finish();
                break;
            case R.id.right_image2:    //报名注册
                startActivity(new Intent(this,ManualRegistrationActivity.class).putExtra(CourseConstant.KEY_LESSON_ID,lessonId));
                break;
            case R.id.lay_veri:        //报名确认
                EnrollConfirmActivity.invoke(this,lessonId);
                break;

        }
    }

    private void init() {
        if(getIntent().getBooleanExtra(EXTRA_NEED_VERIFICATION,false)){
            verLayout.setVisibility(View.VISIBLE);
        }else {
            verLayout.setVisibility(View.GONE);
        }

        adapter = new StudentsAdapter(this, listView);
        adapter.setDesc("暂无报名学生");
        if (teaching) {
            listView.enableLeftSwipe(true);
            setRightText(R.string.lesson_op_signup);
        } else {
            listView.enableLeftSwipe(false);
        }


        listView.setAdapter(adapter);
    }


    class StudentsAdapter extends AbsSwipeAdapter<StudentEnroll, StudentsAdapter.Holder> {


        public StudentsAdapter(Context context, PullToRefreshSwipeListView listView) {
            super(context, listView);
        }

        @Override
        protected void setViewContent(Holder holder, StudentEnroll bean, int position) {
            holder.nameView.setText(bean.name);
            holder.phoneView.setText(""+bean.mobile);
            String time="报名时间"+ (bean.createdOn==null?"暂无":ScheduleUtil.getDateYMDHM(bean.createdOn));
            if("OnlineEnrollment".equals(bean.typeName)){
                time+="    线上";
            }else if("OfflineRegistration".equals(bean.typeName)){
                time+="    线下";
            }
            holder.timeEnvView.setText(time);
        }

        @Override
        protected View createContentView(int position) {
            return LayoutInflater.from(mContext).inflate(R.layout.layout_rl_enrolled_student_item, null);
        }

        @Override
        protected Holder initHolder(View view) {
            return new Holder(view);
        }

        @Override
        protected void doRequest() {
            RLStudentsCriteria criteria=new RLStudentsCriteria();
            criteria.enrolled= "true";
            LessonDataManager.getRecordedCourseStudents(EnrolledStudentsActivity.this, lessonId, criteria, mPagination, new APIServiceCallback<CollectionPage<StudentEnroll>>() {
                @Override
                public void onSuccess(CollectionPage<StudentEnroll> object) {
                    if(object!=null){
                        StudentsAdapter.this.onSuccess(object.objectsOfPage);
                    }else {
                        StudentsAdapter.this.onSuccess(null);
                    }
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    StudentsAdapter.this.onFailure(errorCode,errorMessage);
                }
            });
        }

        @Override
        protected void onSwipeDelete(final int position) {
            StudentEnroll bean=getItem(position);
            showProgress(false);
            LessonDataManager.removeRecordedCourseStudent(mContext, lessonId, bean.id, new APIServiceCallback<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody object) {
                    cancelProgress();
                    removeItem(position);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    ToastUtil.showToast(getApplicationContext(),errorMessage);
                }
            });

        }

        @Override
        protected void onAttachSwipe(TextView mark, TextView del) {
            mark.setVisibility(View.GONE);
        }

        class Holder extends BaseHolder {

            @BindView(R.id.ic_nav)
            ImageView icNav;
            @BindView(R.id.name)
            TextView nameView;
            @BindView(R.id.phone)
            TextView phoneView;
            @BindView(R.id.time_env)
            TextView timeEnvView;


            public Holder(View view) {
                super(view);
            }
        }
    }

    public static void invoke(Context context,String lessonId,boolean needVerification,boolean imOwner){
        Intent intent=new Intent(context,EnrolledStudentsActivity.class);
        intent.putExtra(EXTRA_LESSON_ID,lessonId);
        intent.putExtra(EXTRA_NEED_VERIFICATION,needVerification);
        intent.putExtra(EXTRA_IM_OWNER,imOwner);
        context.startActivity(intent);
    }


}
