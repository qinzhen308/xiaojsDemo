package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Context;
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
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.TimeUtil;

/**
 * Created by maxiaobao on 2017/5/18.
 */

public class VerificationActivity extends BaseActivity {


    @BindView(R.id.ver_list)
    PullToRefreshSwipeListView listView;

    private String classId;
    private VerifyAdapter verifyAdapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_verification_add_class_msg);
        setMiddleTitle(getString(R.string.add_class_verification_msg));

        classId = getIntent().getStringExtra(StudentsListActivity.EXTRA_CLASS);

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
        verifyAdapter = new VerifyAdapter(this,listView);
        listView.setAdapter(verifyAdapter);
    }


    public class VerifyAdapter extends AbsSwipeAdapter<StudentEnroll,VerifyAdapter.Holder> {

        public VerifyAdapter(Context context, PullToRefreshSwipeListView listView) {
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

            if (Platform.JoinClassState.PENDING_FOR_ACCEPTANCE.equals(bean.state)) {
                holder.agreeBtn.setVisibility(View.VISIBLE);
                holder.refuseBtn.setVisibility(View.VISIBLE);

                holder.statusView.setVisibility(View.GONE);
                holder.operaNameView.setVisibility(View.GONE);

                holder.statusView.setText("");

            }else if (Platform.JoinClassState.ACCEPTED.equals(bean.state)) {
                holder.agreeBtn.setVisibility(View.GONE);
                holder.refuseBtn.setVisibility(View.GONE);

                holder.statusView.setVisibility(View.VISIBLE);
                //FIXME 处理人姓名没返回,先隐藏
                holder.operaNameView.setVisibility(View.GONE);

                holder.statusView.setText(R.string.had_agreed);

            }else if (Platform.JoinClassState.REJECTTED.equals(bean.state)) {
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
                    //同意
                    ackDecision(position,bean, Ctl.ACKDecision.ACKNOWLEDGE);
                }
            });

            holder.refuseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //拒绝
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
            return new VerifyAdapter.Holder(view);
        }

        @Override
        protected void doRequest() {

            LessonDataManager.getClassStudents(mContext, classId, false, mPagination,
                    new APIServiceCallback<CollectionPage<StudentEnroll>>() {
                        @Override
                        public void onSuccess(CollectionPage<StudentEnroll> object) {

                            if (object != null) {
                                VerifyAdapter.this.onSuccess(object.objectsOfPage);
                            }
                        }

                        @Override
                        public void onFailure(String errorCode, String errorMessage) {
                            VerifyAdapter.this.onFailure(errorCode, errorMessage);
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


        private void ackDecision(int position, final StudentEnroll student, final int decision) {

            DecisionReason reason = new DecisionReason();
            reason.action = decision;

            showProgress(true);
            LessonDataManager.reviewJoinClass(mContext, student.doc.id, reason, new APIServiceCallback() {
                @Override
                public void onSuccess(Object object) {

                    cancelProgress();

                    if (decision == Ctl.ACKDecision.ACKNOWLEDGE) {
                        student.state = Platform.JoinClassState.ACCEPTED;
                    }else if (decision == Ctl.ACKDecision.REFUSED) {
                        student.state = Platform.JoinClassState.REJECTTED;
                    }

                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {

                    cancelProgress();
                    Toast.makeText(mContext,errorMessage,Toast.LENGTH_SHORT).show();

                }
            });

        }

    }

}
