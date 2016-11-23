package com.benyuan.xiaojs.ui.course;


import com.google.android.flexbox.FlexboxLayout;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.data.LessonDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.Enroll;
import com.benyuan.xiaojs.model.Fee;
import com.benyuan.xiaojs.model.LessonDetail;
import com.benyuan.xiaojs.model.Promotion;
import com.benyuan.xiaojs.model.Schedule;
import com.benyuan.xiaojs.model.Teacher;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.base.BaseBusiness;
import com.benyuan.xiaojs.ui.widget.BlockTabView;
import com.benyuan.xiaojs.ui.widget.BottomSheet;
import com.benyuan.xiaojs.ui.widget.EvaluationStar;
import com.benyuan.xiaojs.ui.widget.RoundedImageView;
import com.benyuan.xiaojs.util.TimeUtil;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class LessonHomeActivity extends BaseActivity {
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
    @BindView(R.id.lesson_money)
    TextView mLessonMoneyTv;
    @BindView(R.id.lesson_origin_money)
    TextView mLessonOriMoneyTv;
    @BindView(R.id.enrollment_count)
    TextView mEnrollmentCountTv;
    @BindView(R.id.promotion_info)
    TextView mPromotionInfoTv;
    @BindView(R.id.tea_avatar)
    RoundedImageView mTeaAvatarImg;
    @BindView(R.id.tea_name)
    TextView mTeaNameTv;
    @BindView(R.id.tea_title)
    TextView mTeaTitleTv;
    @BindView(R.id.eval_star)
    EvaluationStar mTeaEvalStar;
    @BindView(R.id.report)
    TextView mReportTv;
    @BindView(R.id.consulting)
    Button mConsultingBtn;

    @BindView(R.id.report_layout)
    View mReportLayout;
    @BindView(R.id.report_divide_live)
    View mReportDivideLine;
    @BindView(R.id.lesson_opera_bar_lay)
    View mLessonEnrollLayout;

    @BindView(R.id.block_detail_bar)
    BlockTabView mBlockTabView;

    private ArrayList<TextView> textViews;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_lession_detail);
        //setMiddleTitle(R.string.lession_detail);
        //setLeftImage(R.drawable.back_arrow);

        needHeader(false);
        initTabBar();

        mLessonOriMoneyTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        loadData();
    }

    @OnClick({R.id.back_btn, R.id.favourite_btn, R.id.share_btn, R.id.report, R.id.apply_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.favourite_btn:
                break;
            case R.id.share_btn:
                break;
            case R.id.report:
                break;
            case R.id.apply_btn:
                showApplyDlg();
                break;

        }
    }

    private void loadData() {
        int type = getIntent().getIntExtra(CourseConstant.KEY_ENTRANCE_TYPE, ENTRANCE_FROM_TEACH_LESSON);
        if (type == ENTRANCE_FROM_TEACH_LESSON) {
            mReportLayout.setVisibility(View.GONE);
            mReportDivideLine.setVisibility(View.GONE);
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
                setData(lessonDetail);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(LessonHomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData (LessonDetail lesson) {
        if (lesson != null) {
            //set cover
            if (!TextUtils.isEmpty(lesson.getCover())) {
                mLessonCoverImg.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mLessonCoverImg.getLayoutParams();
                int padding = getResources().getDimensionPixelOffset(R.dimen.px32);
                int w = getResources().getDisplayMetrics().widthPixels - padding * 2;
                int h = (int) ((CourseConstant.COURSE_COVER_HEIGHT / (float) CourseConstant.COURSE_COVER_WIDTH) * w);
                params.height = h;
                params.width = w;
                Glide.with(this).load(lesson.getCover()).into(mLessonCoverImg);
            } else {
                //set gone
                mLessonCoverImg.setVisibility(View.GONE);
            }

            //set title
            mLessonTitleTv.setText(lesson.getTitle());

            //enroll
            Enroll enroll = lesson.getEnroll();
            if (enroll != null && enroll.isMandatory()) {
                mEnrollmentCountTv.setVisibility(View.VISIBLE);
                mEnrollmentCountTv.setText(getString(R.string.enrolled_count,
                        enroll.getCurrent(), enroll.getMax()));
            } else {
                mEnrollmentCountTv.setVisibility(View.GONE);
            }

            //fee
            Fee fee = lesson.getFee();
            if (fee == null || fee.isFree()) {
                mLessonMoneyTv.setText(R.string.free);
                mLessonOriMoneyTv.setVisibility(View.GONE);
                mPromotionInfoTv.setVisibility(View.GONE);
            } else {
                //mLessonOriMoneyTv.setVisibility(View.VISIBLE);
                double originCharge = fee.getTotal() != null ? fee.getTotal().doubleValue() : 0;
                mLessonOriMoneyTv.setText(BaseBusiness.formatPrice(originCharge, true));
                if (fee.getDiscounted() != null) {
                    mLessonMoneyTv.setText(BaseBusiness.formatPrice(fee.getDiscounted().getSubtotal().doubleValue(), true));
                } else {
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
        }
    }

    private void setSalePromotion(Fee fee) {
        Fee.Applied[] appliedArr = fee.getApplied();
        if (appliedArr == null || appliedArr.length == 0) {
            mPromotionInfoTv.setVisibility(View.GONE);
            return;
        }

        Fee.Applied applied = appliedArr[0];
        if (applied == null) {
            mPromotionInfoTv.setVisibility(View.GONE);
            return;
        }

        String s = null;
        double discPrice = fee.getDiscounted() != null ? fee.getDiscounted().getSubtotal().doubleValue() : 0;
        if (applied.getQuota() > 0) {
            //enroll before promotion
            String discount = BaseBusiness.formatDiscount(applied.getDiscount());
            s = getString(R.string.enroll_before_promotion, applied.getQuota(), discount, discPrice);
        } else if (applied.getBefore() > 0) {
            //lesson before promotion
            String discount = BaseBusiness.formatDiscount(applied.getDiscount());
            s = getString(R.string.lesson_before_promotion, applied.getBefore(), discount, discPrice);
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
                Glide.with(this).load(tea.getBasic().getAvatar())
                        .error(new ColorDrawable(getResources().getColor(R.color.round_img_border_)))
                        .into(mTeaAvatarImg);
                mTeaNameTv.setText(tea.getBasic().getName());

                String title = tea.getBasic().getTitle();
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

            if (lesson.getTeachersIntro() != null && textViews != null) {
                textViews.get(0).setText(lesson.getTeachersIntro().getText());
                //evaluate: not implemented
            }
        }
    }


    private void initTabBar() {
        String[] titles =getResources().getStringArray(R.array.lesson_home_tab_titles);
        initShowTextView();
        mBlockTabView.setViews("",titles,textViews,"");
    }

    private void initShowTextView() {
        textViews = new ArrayList<TextView>(2);
        TextView textView1 = createTextView();
        TextView textView2 = createTextView();

        textViews.add(textView1);
        textViews.add(textView2);
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

        return textView;
    }


    private void showApplyDlg() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_apply_lession_dlg, null);
        ListView payListView = (ListView) view.findViewById(R.id.list_pay);

        String[] payArray = getResources().getStringArray(R.array.lesson_pay_methods);
        PayAdapter payAdapter = new PayAdapter(this, R.layout.layout_pay_lesson_item, payArray);
        payListView.setAdapter(payAdapter);
        payListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        BottomSheet bottomSheet = new BottomSheet(this);
        bottomSheet.setTitleVisibility(View.GONE);
        bottomSheet.setContent(view);
        bottomSheet.show();

    }


    private class PayAdapter extends ArrayAdapter<String> {
        public PayAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            CheckedTextView textView = (CheckedTextView) v;

            if (position == 0) {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alipay, 0, 0, 0);
            } else {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wechat, 0, 0, 0);
            }
            return v;
        }
    }




}
