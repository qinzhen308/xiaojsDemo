package cn.xiaojs.xma.ui.recordlesson;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.ctl.DecisionReason;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.model.recordedlesson.RLStudentsCriteria;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.lesson.xclass.StudentsListActivity;
import cn.xiaojs.xma.util.TimeUtil;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/5/18.
 */

public class EnrollConfirmActivity extends BaseActivity {
    public static final String EXTRA_LESSON_ID="extra_lesson_id";
    private String lessonId;


    @BindView(R.id.ver_list)
    PullToRefreshSwipeListView listView;

    private ConfirmAdapter confirmAdapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_verification_add_class_msg);
        setMiddleTitle(R.string.enroll_confirm);
        lessonId=getIntent().getStringExtra(EXTRA_LESSON_ID);
        initView();

    }

    @OnClick({R.id.left_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
        }
    }

    private void initView() {
        confirmAdapter = new ConfirmAdapter(this,listView);
        confirmAdapter.setDesc("暂无报名学生");
        listView.setAdapter(confirmAdapter);
    }


    public class ConfirmAdapter extends AbsSwipeAdapter<StudentEnroll,ConfirmAdapter.Holder> {

        public ConfirmAdapter(Context context, PullToRefreshSwipeListView listView) {
            super(context, listView);
        }

        @Override
        protected void setViewContent(Holder holder, final StudentEnroll bean, final int position) {

            holder.nameView.setText(new StringBuilder(bean.name).append(" ")
                    .append(bean.mobile)
                    .toString());

            holder.timeView.setText(TimeUtil.format(bean.createdOn, TimeUtil.TIME_YYYY_MM_DD_HH_MM));
            // TODO: 2017/6/30 差字段
            if(TextUtils.isEmpty(bean.remarks)){
                holder.tvVerifyMsg.setText(R.string.request_join_class);
            }else {
                holder.tvVerifyMsg.setText(bean.remarks);
            }

            if (Ctl.CourseEnrollmentState.PENDING_FOR_ACCEPTANCE.equals(bean.state)) {
                holder.agreeBtn.setVisibility(View.VISIBLE);
                holder.refuseBtn.setVisibility(View.VISIBLE);

                holder.statusView.setVisibility(View.GONE);
                holder.operaNameView.setVisibility(View.GONE);

                holder.statusView.setText("");

            }else if (Ctl.CourseEnrollmentState.ENROLLED.equals(bean.state)) {
                holder.agreeBtn.setVisibility(View.GONE);
                holder.refuseBtn.setVisibility(View.GONE);

                holder.statusView.setVisibility(View.VISIBLE);
                //FIXME 处理人姓名没返回,先隐藏
                holder.operaNameView.setVisibility(View.GONE);

                holder.statusView.setText(R.string.had_agreed);

            }else if (Ctl.CourseEnrollmentState.REJECTED.equals(bean.state)) {
                holder.agreeBtn.setVisibility(View.GONE);
                holder.refuseBtn.setVisibility(View.GONE);

                holder.statusView.setVisibility(View.VISIBLE);
                //FIXME 处理人姓名没返回,先隐藏
                holder.operaNameView.setVisibility(View.GONE);

                holder.statusView.setText(R.string.had_refused);

            }else {
                holder.agreeBtn.setVisibility(View.GONE);
                holder.refuseBtn.setVisibility(View.GONE);

                holder.statusView.setVisibility(View.GONE);
                holder.operaNameView.setVisibility(View.GONE);

                holder.statusView.setText("");
            }


            holder.agreeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ackDecision(position,bean, Ctl.ACKDecision.ACKNOWLEDGE);
                }
            });

            holder.refuseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ackDecision(position,bean, Ctl.ACKDecision.REFUSED);
                }
            });
        }

        @Override
        protected View createContentView(int position) {
            return LayoutInflater.from(mContext).inflate(R.layout.layout_verification_class_msg_item, null);
        }

        @Override
        protected Holder initHolder(View view) {
            return new ConfirmAdapter.Holder(view);
        }

        @Override
        protected void doRequest() {
            RLStudentsCriteria criteria=new RLStudentsCriteria();
            criteria.enrolled= "false";
            LessonDataManager.getRecordedCourseStudents(EnrollConfirmActivity.this, lessonId, criteria, mPagination, new APIServiceCallback<CollectionPage<StudentEnroll>>() {
                @Override
                public void onSuccess(CollectionPage<StudentEnroll> object) {
                    if(object!=null){
                        ConfirmAdapter.this.onSuccess(object.objectsOfPage);
                    }else {
                        ConfirmAdapter.this.onSuccess(null);
                    }
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    ConfirmAdapter.this.onFailure(errorCode,errorMessage);
                }
            });
        }

        class Holder extends BaseHolder{
            @BindView(R.id.time)
            TextView timeView;
            @BindView(R.id.name_num)
            TextView nameView;
            @BindView(R.id.refuse_btn)
            Button refuseBtn;
            @BindView(R.id.agree_btn)
            Button agreeBtn;
            @BindView(R.id.status_view)
            TextView statusView;
            @BindView(R.id.opera_name)
            TextView operaNameView;
            @BindView(R.id.tv_verify_msg)
            TextView tvVerifyMsg;

            public Holder(View view) {
                super(view);
            }

        }

    }

    private void ackDecision(int position, final StudentEnroll student, final int decision) {

        DecisionReason reason = new DecisionReason();
        reason.action = decision;

        showProgress(true);
        LessonDataManager.reviewRecordedCourseEnroll(this, student.doc.id, reason, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {

                cancelProgress();

                if (decision == Ctl.ACKDecision.ACKNOWLEDGE) {
                    student.state = Ctl.CourseEnrollmentState.ENROLLED;
                }else if (decision == Ctl.ACKDecision.REFUSED) {
                    student.state = Ctl.CourseEnrollmentState.REJECTED;
                }

                confirmAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                cancelProgress();
                Toast.makeText(EnrollConfirmActivity.this,errorMessage,Toast.LENGTH_SHORT).show();

            }
        });

    }


    public static void invoke(Context context,String lessonId){
        Intent intent=new Intent(context,EnrollConfirmActivity.class);
        intent.putExtra(EXTRA_LESSON_ID,lessonId);
        context.startActivity(intent);
    }

}
