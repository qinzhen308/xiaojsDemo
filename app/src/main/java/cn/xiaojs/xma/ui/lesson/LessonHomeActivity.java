package cn.xiaojs.xma.ui.lesson;


import com.google.android.flexbox.FlexboxLayout;

import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Role;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ELResponse;
import cn.xiaojs.xma.model.LessonDetail;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.model.OfflineRegistrant;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.model.Teacher;
import cn.xiaojs.xma.model.ctl.Enroll;
import cn.xiaojs.xma.model.ctl.Price;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.model.social.Relation;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.BaseBusiness;
import cn.xiaojs.xma.ui.classroom.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.Constants;
import cn.xiaojs.xma.ui.personal.PersonHomeActivity;
import cn.xiaojs.xma.ui.personal.PersonalBusiness;
import cn.xiaojs.xma.ui.widget.BlockTabView;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.EvaluationStar;
import cn.xiaojs.xma.ui.widget.flow.ColorTextFlexboxLayout;
import cn.xiaojs.xma.util.ShareUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.ToastUtil;

public class  LessonHomeActivity extends BaseActivity{
    public final static int ENTRANCE_FROM_TEACH_LESSON = 0;
    public final static int ENTRANCE_FROM_ENROLL_LESSON = 1;

    @BindView(R.id.lesson_img)
    ImageView mLessonCoverImg;
    @BindView(R.id.lesson_title)
    TextView mLessonTitleTv;
    @BindView(R.id.lesson_begin_time)
    TextView mLessonBeginTimeTv;
    @BindView(R.id.lesson_duration)
    TextView mLessonDurationTv;
    @BindView(R.id.label_container)
    ColorTextFlexboxLayout mLabelContainer;
    @BindView(R.id.lesson_money)
    TextView mLessonMoneyTv;
    @BindView(R.id.lesson_origin_money)
    TextView mLessonOriMoneyTv;
    @BindView(R.id.enrollment_count)
    TextView mEnrollmentCountTv;
    @BindView(R.id.promotion_info)
    TextView mPromotionInfoTv;
    @BindView(R.id.tea_avatar)
    ImageView mTeaAvatarImg;
    @BindView(R.id.tea_name)
    TextView mTeaNameTv;
    @BindView(R.id.tea_title)
    TextView mTeaTitleTv;
    @BindView(R.id.eval_star)
    EvaluationStar mTeaEvalStar;
    @BindView(R.id.report)
    TextView mReportTv;


    @BindView(R.id.report_layout)
    View mReportLayout;

    @BindView(R.id.layout_scro)
    ScrollView layScrollView;

    //@BindView(R.id.report_divide_live)
    //View mReportDivideLine;


    @BindView(R.id.lesson_opera_bar_lay)
    View mLessonEnrollLayout;
    @BindView(R.id.apply_btn)
    Button applyBtn;
//    @BindView(R.id.consulting)
//    Button mConsultingBtn;


    @BindView(R.id.block_detail_bar)
    BlockTabView mBlockTabView;

    @BindView(R.id.intro_view)
    TextView introView;



    private ArrayList<TextView> textViews;
    private LessonDetail mLessonDetail;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_lession_home);
        //setMiddleTitle("详情");
        //setLeftImage(R.drawable.back_arrow)

        needHeader(false);
        initTabBar();

        mLessonOriMoneyTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        loadData();
    }

    @OnClick({R.id.back_btn, R.id.share_wb_btn, R.id.report, R.id.apply_btn, R.id.lay_teacher})//R.id.consulting
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lay_teacher:

                if (mLessonDetail == null)
                    return;

                Intent intent = new Intent(this, PersonHomeActivity.class);
                intent.putExtra(PersonalBusiness.KEY_PERSONAL_ACCOUNT, mLessonDetail.getTeacher()._id);
                startActivity(intent);
                break;
            case R.id.back_btn:
                finish();
                break;
            case R.id.favourite_btn:
                break;
            case R.id.share_wb_btn:

                if (mLessonDetail == null) {
                    return;
                }

                String shareUrl = ApiManager.getShareLessonUrl(mLessonDetail.getId());

                String startTime = TimeUtil.format(mLessonDetail.getSchedule().getStart().getTime(),
                        TimeUtil.TIME_YYYY_MM_DD_HH_MM);

                ShareUtil.show(LessonHomeActivity.this,
                        mLessonDetail.getTitle(),
                        new StringBuilder(startTime).append("\r\n").append(mLessonDetail.getTeacher().getBasic().getName()).toString(),
                        shareUrl);
                break;
            case R.id.report:
                break;
            case R.id.apply_btn:
                appDeal();
                break;
//            case R.id.consulting:
//
//                if (mLessonDetail == null) return;
//
//                final Teacher tea = mLessonDetail.getTeacher();
//                if (tea == null || tea.getBasic() == null) return;
//
//                String sex = "true";
//
//                boolean isFollowed = DataManager.existInContacts(this, tea._id);
//
//                BaseBusiness.advisory(this, isFollowed, tea._id, tea.getBasic().getName(), sex, new BaseBusiness.OnFollowListener() {
//                    @Override
//                    public void onFollow(long group) {
//                        if (group > 0) {
//                            SocialManager.followContact(LessonHomeActivity.this, tea._id,tea.getBasic().getName(), group, new APIServiceCallback<Relation>() {
//                                @Override
//                                public void onSuccess(Relation object) {
//                                    ToastUtil.showToast(getApplicationContext(), R.string.followed);
//
//                                }
//
//                                @Override
//                                public void onFailure(String errorCode, String errorMessage) {
//                                    ToastUtil.showToast(getApplicationContext(), errorMessage);
//                                }
//                            });
//                        }
//                    }
//                });
//                break;

        }
    }

    private void loadData() {
        int type = getIntent().getIntExtra(CourseConstant.KEY_ENTRANCE_TYPE, -1);
        if (type == ENTRANCE_FROM_ENROLL_LESSON) {
            //mReportLayout.setVisibility(View.GONE);
            //mReportDivideLine.setVisibility(View.GONE);
            mLessonEnrollLayout.setVisibility(View.GONE);
        }

        String lessonId = getIntent().getStringExtra(CourseConstant.KEY_LESSON_ID);
        if (TextUtils.isEmpty(lessonId)) {
            finish();
            return;
        }

        showProgress(true);
        LessonDataManager.requestLessonDetails(this, lessonId, new APIServiceCallback<LessonDetail>() {
            @Override
            public void onSuccess(LessonDetail lessonDetail) {
                cancelProgress();
                layScrollView.setVisibility(View.VISIBLE);
                setData(lessonDetail);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                layScrollView.setVisibility(View.GONE);
                mLessonEnrollLayout.setVisibility(View.GONE);
                Toast.makeText(LessonHomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData (LessonDetail lesson) {
        if (lesson != null) {
            mLessonDetail = lesson;
            //set cover
            if (!TextUtils.isEmpty(lesson.getCover())) {
                mLessonCoverImg.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mLessonCoverImg.getLayoutParams();
                int w = getResources().getDisplayMetrics().widthPixels;
                int h = (int) ((CourseConstant.COURSE_COVER_HEIGHT / (float) CourseConstant.COURSE_COVER_WIDTH) * w);
                params.height = h;
                params.width = w;
                Dimension dimension = new Dimension();
                dimension.width = w;
                dimension.height = h;
                String url = Ctl.getCover(lesson.getCover(), dimension);
                Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.default_lesson_cover)
                        .error(R.drawable.default_lesson_cover)
                        .into(mLessonCoverImg);
            } else {
                //set gone
                mLessonCoverImg.setVisibility(View.GONE);
            }

            //set title
            mLessonTitleTv.setText(lesson.getTitle());

            //set tag
            String[] tags = lesson.getTags();
            if (tags != null && tags.length > 0) {
                mLabelContainer.setVisibility(View.VISIBLE);
                for (String tag : tags) {
                    mLabelContainer.addText(tag);
                }
            }

            //enroll
            Enroll enroll = lesson.getEnroll();
            if (enroll != null && enroll.mandatory) {
                mEnrollmentCountTv.setVisibility(View.VISIBLE);
                mEnrollmentCountTv.setText(getString(R.string.enrolled_count,
                        enroll.current, enroll.max));
            } else {
                mEnrollmentCountTv.setVisibility(View.GONE);
            }

            //fee
            Price fee = lesson.getFee();
            if (fee == null || fee.free) {
                mLessonMoneyTv.setText(R.string.free);
                mLessonOriMoneyTv.setVisibility(View.GONE);
                mPromotionInfoTv.setVisibility(View.GONE);
            } else {
                //mLessonOriMoneyTv.setVisibility(View.VISIBLE);
                float originCharge = fee.total;
                Price.Discounted discounted = fee.discounted;
                if (discounted != null) {
                    if (discounted.ratio == 10.0f) {
                        mLessonOriMoneyTv.setVisibility(View.GONE);
                    } else {
                        mLessonOriMoneyTv.setText(BaseBusiness.formatPrice(originCharge, true));
                    }
                    mLessonMoneyTv.setText(BaseBusiness.formatPrice(discounted.subtotal, true));
                } else {
                    mLessonOriMoneyTv.setVisibility(View.GONE);
                    mLessonMoneyTv.setText(BaseBusiness.formatPrice(originCharge, true));
                }
                setSalePromotion(fee);
            }

            //schedule
            Schedule schedule = lesson.getSchedule();
            if (schedule != null) {
                mLessonBeginTimeTv.setText(TimeUtil.format(schedule.getStart().getTime(),
                        TimeUtil.TIME_YYYY_MM_DD_HH_MM));
                String m = getString(R.string.minute);
                mLessonDurationTv.setText(String.valueOf(schedule.getDuration()) + m);
            }

            setTeacherInfo(lesson);

            if (lesson.getOverview() != null && !TextUtils.isEmpty(lesson.getOverview().getText())) {
                introView.setText(lesson.getOverview().getText());
//                textViews.get(0).setText(lesson.getOverview().getText());
                //evaluate: not implemented
            } else {
                introView.setText(R.string.lesson_no_introduction);
//                textViews.get(0).setText(R.string.lesson_no_introduction);
            }

        } else {
            mReportLayout.setVisibility(View.GONE);
            //mReportDivideLine.setVisibility(View.GONE);
            mLessonEnrollLayout.setVisibility(View.GONE);
        }

        setLayBottom();
    }

    private void setLayBottom() {

        mLessonEnrollLayout.setVisibility(View.VISIBLE);

        String lessonState = mLessonDetail.getState();
        if (lessonState.equals(Ctl.LiveLessonState.CANCELLED)) {
            applyBtn.setText("课已取消");
            applyBtn.setEnabled(false);
        }else {

            if (mLessonDetail.isEnrolled) {

                String state = mLessonDetail.enrollState;
                if (TextUtils.isEmpty(state) || state.equals(Ctl.EnrollmentState.ENROLLED)) {
                    applyBtn.setText("进入教室");
                    applyBtn.setEnabled(true);
                } else if (state.equals(Ctl.EnrollmentState.PENDING_FOR_PAYMENT)) {
                    applyBtn.setText("立即支付");
                    applyBtn.setEnabled(true);
                } else if (state.equals(Ctl.EnrollmentState.CANCELLED)) {
                    applyBtn.setText("立即报名");
                    applyBtn.setEnabled(true);
                } else if (state.equals(Ctl.EnrollmentState.PENDING_FOR_ACK)) {
                    //nothing to do
                }

            }else if (lessonState.equals(Ctl.LiveLessonState.FINISHED)) {
                applyBtn.setText("停止报名");
                applyBtn.setEnabled(false);
            }else {

                Enroll enroll = mLessonDetail.getEnroll();
                if (enroll != null && enroll.max == enroll.current) {
                    applyBtn.setText("名额已满");
                    applyBtn.setEnabled(false);
                }else {
                    applyBtn.setText("立即报名");
                    applyBtn.setEnabled(true);
                }
            }
        }

        String accId = AccountDataManager.getAccountID(this);
        String createBy = mLessonDetail.getCreatedBy();
        if ((!TextUtils.isEmpty(createBy) && createBy.equals(accId))
                || isLessonRole(mLessonDetail.currentRoles)
                || (mLessonDetail.getTeacher() !=null && mLessonDetail.getTeacher()._id.equals(accId)) ){

            if (lessonState.equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)
                    || lessonState.equalsIgnoreCase(LessonState.LIVE)
                    || lessonState.equalsIgnoreCase(LessonState.FINISHED)) {
                mLessonEnrollLayout.setVisibility(View.VISIBLE);
                //mConsultingBtn.setVisibility(View.GONE);
                applyBtn.setText("进入教室");
                applyBtn.setEnabled(true);
            }else{
                mLessonEnrollLayout.setVisibility(View.GONE);
                //mConsultingBtn.setVisibility(View.VISIBLE);
            }

        }else{
            mLessonEnrollLayout.setVisibility(View.VISIBLE);
            //mConsultingBtn.setVisibility(View.VISIBLE);
        }

    }

    private void setSalePromotion(Price fee) {
        Price.Applied[] appliedArr = fee.discounted.applied;
        if (appliedArr == null || appliedArr.length == 0) {
            mPromotionInfoTv.setVisibility(View.GONE);
            return;
        }

        Price.Applied applied = appliedArr[0];
        if (applied == null) {
            mPromotionInfoTv.setVisibility(View.GONE);
            return;
        }

        String s = null;
        double discPrice = fee.discounted != null ? fee.discounted.subtotal : 0;
        if (applied.quota > 0) {
            //enroll before promotion
            String discount = BaseBusiness.formatDiscount(applied.discount);
            s = getString(R.string.enroll_before_promotion, applied.quota, discount, discPrice);
        } else if (applied.before > 0) {
            //lesson before promotion
            String discount = BaseBusiness.formatDiscount(applied.discount);
            s = getString(R.string.lesson_before_promotion, applied.before, discount, discPrice);
        }

        if (!TextUtils.isEmpty(s)) {
            mPromotionInfoTv.setText(Html.fromHtml(s).toString());
        } else {
            mPromotionInfoTv.setVisibility(View.GONE);
        }
    }

    /**
     * set teacher info
     */
    private void setTeacherInfo(LessonDetail lesson) {
        if (lesson != null) {
            Teacher tea = lesson.getTeacher();
            if (tea != null && tea.getBasic() != null) {

                String avator = Account.getAvatar(tea._id, mTeaAvatarImg.getMeasuredWidth());

                Glide.with(this).load(avator)
                        .bitmapTransform(new CircleTransform(this))
                        .placeholder(R.drawable.default_avatar_grey)
                        .error(R.drawable.default_avatar_grey)
                        .into(mTeaAvatarImg);
                mTeaNameTv.setText(tea.getBasic().getName());

                String title = tea.getBasic().getTitle();

                LiveLesson.TeachersIntro intro = lesson.getTeachersIntro();
                if (intro != null && !TextUtils.isEmpty(intro.getText())) {
                    title = intro.getText();
                }

                if (!TextUtils.isEmpty(title)) {
                    mTeaTitleTv.setText(title);
                } else {
                    mTeaTitleTv.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mTeaNameTv.getLayoutParams();
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    params = (RelativeLayout.LayoutParams)mTeaEvalStar.getLayoutParams();
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                }

                //TODO test
                //mTeaEvalStar.setGrading(EvaluationStar.Grading.FOUR);
            }
        }
    }

    private boolean isLessonRole(String role) {

        if (TextUtils.isEmpty(role)) return false;

        if (role.equals(Role.LessonRole.LESSON_ASSISTANT)
                || role.equals(Role.LessonRole.LESSON_AUDITOR)
                || role.equals(Role.LessonRole.LESSON_LEAD)
                || role.equals(Role.LessonRole.LESSON_REPRESENTATIVE)
                || role.equals(Role.LessonRole.LESSON_STUDENT)) {
            return true;
        }

        return false;
    }


    private void initTabBar() {
        String[] titles =getResources().getStringArray(R.array.lesson_home_tab_titles);
        initShowTextView();
        mBlockTabView.setViews("",titles,textViews,"");
    }

    private void initShowTextView() {
        textViews = new ArrayList<>(1);
        TextView textView1 = createTextView();
        //TextView textView2 = createTextView();

        textViews.add(textView1);
        //textViews.add(textView2);
    }

    private TextView createTextView() {

        TextView textView = new TextView(this);

        FlexboxLayout.LayoutParams p = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.MATCH_PARENT,
                FlexboxLayout.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(p);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.font_32px));

        int pad = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);

        textView.setPadding(pad,pad,pad,pad);
        textView.setTextColor(getResources().getColor(R.color.font_black));

        textView.setLineSpacing(2f, 1.5f);

        textView.setMinHeight(getResources().getDimensionPixelSize(R.dimen.px400));

        return textView;
    }


    private void appDeal() {

        if (mLessonDetail == null) return;

        String lessonState = mLessonDetail.getState();

        String accId = AccountDataManager.getAccountID(this);
        String createBy = mLessonDetail.getCreatedBy();
        if ((createBy != null && createBy.equals(accId))
                ||isLessonRole(mLessonDetail.currentRoles)
                || (mLessonDetail.getTeacher()!= null && mLessonDetail.getTeacher()._id.equals(accId)) ){

            if (lessonState.equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)
                    || lessonState.equalsIgnoreCase(LessonState.LIVE)
                    || lessonState.equalsIgnoreCase(LessonState.FINISHED)) {
                enterClass(mLessonDetail.ticket);
            }


        } else {

            if (!lessonState.equals(Ctl.LiveLessonState.CANCELLED)) {

                if(mLessonDetail.isEnrolled) {
                    dealEnrolled();
                }else{
                    enterConfirmPayPage();
                }
            }

        }
    }

    private void dealEnrolled() {
        String state = mLessonDetail.enrollState;
        if (TextUtils.isEmpty(state) || state.equals(Ctl.EnrollmentState.ENROLLED)) {
            enterClass(mLessonDetail.ticket);
        } else if (state.equals(Ctl.EnrollmentState.PENDING_FOR_PAYMENT)) {
            enterPay();
        } else if (state.equals(Ctl.EnrollmentState.CANCELLED)) {
            //nothing to do
        } else if (state.equals(Ctl.EnrollmentState.PENDING_FOR_ACK)) {
            //nothing to do
        }

    }

    private void enterConfirmPayPage() {
        if (mLessonDetail.getFee().free){
            //免费课不用付款
            showProgress(true);
            LessonDataManager.requestEnrollLesson(this, mLessonDetail.getId(),new OfflineRegistrant(), new APIServiceCallback<ELResponse>() {
                @Override
                public void onSuccess(ELResponse object) {
                    cancelProgress();
                    mLessonDetail.isEnrolled = true;
                    mLessonDetail.enrollState = Ctl.EnrollmentState.ENROLLED;
                    setLayBottom();
                    ToastUtil.showToast(getApplicationContext(),"报名成功!");
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    ToastUtil.showToast(getApplicationContext(),errorMessage);
                }
            });
        }else {
            enterPay();
        }
        //ConfirmEnrollmentActivity

    }

    //进入教室
    private void enterClass(String ticket) {

        if (TextUtils.isEmpty(ticket)) {
            Toast.makeText(this,"进入教室失败",Toast.LENGTH_SHORT).show();
        }

        Intent i = new Intent();
        i.putExtra(Constants.KEY_TICKET, ticket);
        i.setClass(this, ClassroomActivity.class);
        this.startActivity(i);
    }

    private void enterPay() {
        Intent i = new Intent();
        i.setClass(this, ConfirmEnrollmentActivity.class);
        i.putExtra(CourseConstant.KEY_LESSON_BEAN, mLessonDetail);
        startActivity(i);
    }

    private void enjoyFreeLesson(String lesson) {
        showProgress(true);
        LessonDataManager.requestEnrollLesson(this,
                lesson, null, new APIServiceCallback<ELResponse>() {
            @Override
            public void onSuccess(ELResponse object) {

                applyBtn.setText(R.string.lesson_enrolled);
                applyBtn.setEnabled(false);

                cancelProgress();

                Toast.makeText(LessonHomeActivity.this,
                        R.string.enroll_lesson_success, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                cancelProgress();

                Toast.makeText(LessonHomeActivity.this,
                        R.string.enroll_lesson_failed, Toast.LENGTH_SHORT).show();

            }
        });
    }


//    private class PayAdapter extends ArrayAdapter<String> {
//        public PayAdapter(Context context, int resource, String[] objects) {
//            super(context, resource, objects);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View v = super.getView(position, convertView, parent);
//            CheckedTextView textView = (CheckedTextView) v;
//
//            if (position == 0) {
//                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alipay, 0, 0, 0);
//            } else {
//                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wechat, 0, 0, 0);
//            }
//            return v;
//        }
//    }



}
