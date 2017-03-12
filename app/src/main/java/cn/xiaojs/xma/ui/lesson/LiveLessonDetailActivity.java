package cn.xiaojs.xma.ui.lesson;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Finance;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.LessonDetail;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.model.Promotion;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.model.ctl.Enroll;
import cn.xiaojs.xma.model.ctl.Price;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.BaseBusiness;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.ui.widget.flow.ColorTextFlexboxLayout;
import cn.xiaojs.xma.util.TimeUtil;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:Administrator
 * Date:2016/11/13
 * Desc:
 *
 * ======================================================================================== */

public class LiveLessonDetailActivity extends BaseActivity {
    //===============base info================
    @BindView(R.id.lesson_name)
    TextView mLessonNameTv;
    @BindView(R.id.lesson_subject)
    TextView mLessonSubjectTv;
    @BindView(R.id.lesson_stu_count)
    TextView mLessonStuCountTv;
    @BindView(R.id.enrolled_layout)
    View mEnrolledView;
    @BindView(R.id.enrolled_divide_line)
    View mEnrolledDivideLine;
    @BindView(R.id.enrolled)
    TextView mEnrolledCountTv;
    @BindView(R.id.lesson_viewed)
    TextView mLessonViewedTv;
    @BindView(R.id.lesson_collected)
    TextView mLessonCollectedTv;
    @BindView(R.id.teach_form)
    TextView mTeachFormTv;
    @BindView(R.id.lesson_fee)
    TextView mLessonFeeTv;
    @BindView(R.id.lesson_start_time)
    TextView mLessonStartTimeTv;
    @BindView(R.id.lesson_duration)
    TextView mLessonDurationTv;
    @BindView(R.id.lesson_status)
    TextView mLessonStatusTv;
    @BindView(R.id.lesson_fail_reason)
    TextView mLessonFailReasonTv;
    @BindView(R.id.lesson_fail_reason_layout)
    LinearLayout mLessonFailReasonLayout;

    //==============optional info==============
    //lesson cover
    @BindView(R.id.lesson_cover_layout)
    View mLessonCoverLayout;
    @BindView(R.id.lesson_cover)
    ImageView mLessonCover;
    @BindView(R.id.lesson_label_layout)
    View mLessonLabelLayout;
    @BindView(R.id.label_container)
    ColorTextFlexboxLayout mLabelContainer;

    //lesson brief
    @BindView(R.id.lesson_brief_layout)
    LinearLayout mLessonBriefLayout;
    @BindView(R.id.live_lesson_brief)
    TextView mLessonBriefTv;
    @BindView(R.id.unfold_lesson_brief)
    TextView mUnfoldLessonBriefTv;

    //teacher introduction
    @BindView(R.id.teacher_intro_layout)
    LinearLayout mTeacherIntroLayout;
    @BindView(R.id.teacher_introduction)
    TextView mTeacherIntroTv;
    @BindView(R.id.unfold_teacher_intro)
    TextView mUnfoldTeacherIntroTv;

    //sale promotion
    @BindView(R.id.sale_promotion_layout)
    LinearLayout mSalePromotionLayout;
    @BindView(R.id.sale_promotion_title)
    TextView mSalePromotionTitleTv;
    @BindView(R.id.enroll_before_promotion)
    LinearLayout mEnrollBeforePromotionLayout;
    @BindView(R.id.enroll_before_title)
    TextView mEnrollBeforeTitle;
    @BindView(R.id.enroll_before)
    TextView mEnrollBeforeTv;
    @BindView(R.id.lesson_before_promotion)
    LinearLayout mLessonBeforePromotionLayout;
    @BindView(R.id.lesson_before_title)
    TextView mLessonBeforeTitle;
    @BindView(R.id.lesson_before)
    TextView mLessonBeforeTv;

    //audit
    @BindView(R.id.audit_layout)
    LinearLayout mAuditLayout;
    @BindView(R.id.audit_portrait)
    LinearLayout mAuditPortraitLayout;
    @BindView(R.id.visible_to_stu)
    ToggleButton mAuditVisibleBtn;

    private final static int INTRO_DEFAULT_LINES = 3;
    private final static int INTRO_MAX_LINES = 100;
    private boolean mLessonBriefSwitcher = false;
    private boolean mTeacherIntroSwitcher = false;

    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.live_lesson_detail);
        addView(R.layout.activity_live_lesson_detail);

        init();
        loadData();
    }

    @OnClick({R.id.left_image, R.id.audit_person_select_enter, R.id.audit_portrait, R.id.visible_to_stu,
            R.id.unfold_lesson_brief, R.id.unfold_teacher_intro})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.audit_person_select_enter:
            case R.id.audit_portrait:
                //setting person
                break;
            case R.id.visible_to_stu:
                setVisibleToStu();
                break;
            case R.id.unfold_lesson_brief:
            case R.id.unfold_teacher_intro:
                foldOrUnfold(v);
            default:
                break;
        }
    }

    private void init() {
        final int padding = getResources().getDimensionPixelOffset(R.dimen.px24);
        final int padding2 = getResources().getDimensionPixelOffset(R.dimen.px6);
        ViewTreeObserver observer = mLessonBriefTv.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                int count = mLessonBriefTv.getLineCount();
                if (count > INTRO_DEFAULT_LINES) {
                    if (mUnfoldLessonBriefTv.getVisibility() != View.VISIBLE) {
                        mUnfoldLessonBriefTv.setVisibility(View.VISIBLE);
                    }
                    mLessonBriefTv.setPadding(0, 0, 0, padding2);
                } else {
                    mLessonBriefTv.setPadding(0, 0, 0, padding);
                }
                return true;
            }
        });


        ViewTreeObserver observer1 = mTeacherIntroTv.getViewTreeObserver();
        observer1.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                int count = mTeacherIntroTv.getLineCount();
                if (count > INTRO_DEFAULT_LINES) {
                    if (mUnfoldTeacherIntroTv.getVisibility() != View.VISIBLE) {
                        mUnfoldTeacherIntroTv.setVisibility(View.VISIBLE);
                    }
                    mTeacherIntroTv.setPadding(0, 0, 0, padding2);
                } else {
                    mTeacherIntroTv.setPadding(0, 0, 0, padding);
                }
                return true;
            }
        });


    }

    private void loadData() {
        Object obj = getIntent().getSerializableExtra(CourseConstant.KEY_LESSON_BEAN);
        if (obj instanceof TeachLesson) {
            String lessonId = ((TeachLesson) obj).getId();
            if (TextUtils.isEmpty(lessonId)) {
                finish();
            }

            showProgress(true);
            LessonDataManager.requestLessonData(this, lessonId, new APIServiceCallback<LessonDetail>() {
                @Override
                public void onSuccess(LessonDetail lessonDetail) {
                    cancelProgress();
                    setData(lessonDetail);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    Toast.makeText(LiveLessonDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            finish();
        }
    }


    private void setData(LessonDetail lesson) {
        setBaseData(lesson);
        setOptionalData(lesson);
    }

    private void foldOrUnfold(View v) {
        switch (v.getId()) {
            case R.id.unfold_lesson_brief:
                if (mLessonBriefSwitcher) {
                    //close
                    mLessonBriefSwitcher = false;
                    mLessonBriefTv.setMaxLines(INTRO_DEFAULT_LINES);
                } else {
                    //open
                    mLessonBriefSwitcher = true;
                    mLessonBriefTv.setMaxLines(INTRO_MAX_LINES);
                }
                break;
            case R.id.unfold_teacher_intro:
                if (mTeacherIntroSwitcher) {
                    mTeacherIntroSwitcher = false;
                    mTeacherIntroTv.setMaxLines(INTRO_DEFAULT_LINES);
                } else {
                    mTeacherIntroSwitcher = true;
                    mTeacherIntroTv.setMaxLines(INTRO_MAX_LINES);
                }
                break;
            default:
                break;
        }
    }

    /**
     * set base info
     */
    private void setBaseData(LessonDetail lesson) {
        if (lesson != null) {
            mLessonNameTv.setText(lesson.getTitle());
            mLessonSubjectTv.setText(lesson.getSubject().getName());

            //enroll
            Enroll enroll = lesson.getEnroll();
            if (enroll != null && enroll.mandatory) {
                mEnrolledView.setVisibility(View.VISIBLE);
                mEnrolledDivideLine.setVisibility(View.VISIBLE);
                mEnrolledCountTv.setText(enroll.current + "/" + enroll.max);
            } else {
                mEnrolledView.setVisibility(View.GONE);
                mEnrolledDivideLine.setVisibility(View.GONE);
            }

            mLessonViewedTv.setText(String.valueOf(0));
            mLessonCollectedTv.setText(String.valueOf(0));

            String p = getString(R.string.person);
            mLessonStuCountTv.setText(String.valueOf(enroll.max) + p);
            mTeachFormTv.setText(BaseBusiness.getTeachingMode(this, lesson.getMode()));

            //fee
            Price fee = lesson.getFee();
            if (fee == null || fee.free) {
                mLessonFeeTv.setText(R.string.free);
            } else {
                float charge = fee.charge;
                if (fee.type == Finance.PricingType.TOTAL) {
                    String byTotalPrice = getString(R.string.by_live_total_price);
                    mLessonFeeTv.setText(byTotalPrice + BaseBusiness.formatPrice(charge, true));
                } else if (fee.type == Finance.PricingType.PAY_PER_HOUR) {
                    String byDuration = getString(R.string.by_live_duration);
                    mLessonFeeTv.setText(byDuration + BaseBusiness.formatPrice(charge, true));
                }
            }

            //schedule
            Schedule schedule = lesson.getSchedule();
            if (schedule != null) {
                mLessonStartTimeTv.setText(TimeUtil.format(schedule.getStart(), TimeUtil.TIME_YYYY_MM_DD_HH_MM));
                String m = getString(R.string.minute);
                mLessonDurationTv.setText(String.valueOf(schedule.getDuration()) + m);
            }

            //status
            mLessonStatusTv.setText(BaseBusiness.getLessonStatusText(this, lesson.getState()));
            mLessonStatusTv.setBackgroundResource(BaseBusiness.getLessonStatusDrawable(lesson.getState()));

            //TODO set failure reason
            //mLessonFailReasonLayout.setVisibility(View.VISIBLE);
            //mLessonFailReasonTv.setText();
        }
    }

    /**
     * set optional info
     */
    private void setOptionalData(LessonDetail lesson) {
        if (lesson != null) {
            //set cover
            if (!TextUtils.isEmpty(lesson.getCover())) {
                mLessonCoverLayout.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mLessonCover.getLayoutParams();
                int padding = getResources().getDimensionPixelOffset(R.dimen.px32);
                int w = getResources().getDisplayMetrics().widthPixels - padding * 2;
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
                        .into(mLessonCover);
            } else {
                //set gone
                mLessonCoverLayout.setVisibility(View.GONE);
            }

            //set tag
            String[] tags = lesson.getTags();
            if (tags != null && tags.length > 0) {
                for (String tag : tags) {
                    mLabelContainer.addText(tag);
                }
            } else {
                mLessonLabelLayout.setVisibility(View.GONE);
            }

            //set lesson brief
            LiveLesson.Overview overview = lesson.getOverview();
            String brief = null;
            if (overview != null && !TextUtils.isEmpty((brief = overview.getText()))) {
                mLessonBriefLayout.setVisibility(View.VISIBLE);
                mLessonBriefTv.setText(brief);
            } else {
                mLessonBriefLayout.setVisibility(View.GONE);
            }

            //set teacher introduction
            LiveLesson.TeachersIntro teaIntro = lesson.getTeachersIntro();
            String intro = null;
            if (teaIntro != null && !TextUtils.isEmpty((intro = teaIntro.getText()))) {
                mTeacherIntroLayout.setVisibility(View.VISIBLE);
                mTeacherIntroTv.setText(intro);
            } else {
                mTeacherIntroLayout.setVisibility(View.GONE);
            }

            //set sale promotion
            Price fee = lesson.getFee();
            if (fee == null || fee.free) {
                mSalePromotionLayout.setVisibility(View.GONE);
                return;
            }
            Promotion[] promotions = lesson.getPromotion();
            if (promotions != null && promotions.length > 0) {
                //set sale promotion title
                int type = Finance.PricingType.TOTAL;
                if (lesson.getFee() != null) {
                    type = lesson.getFee().type;
                    String suffix = "";
                    if (type == Finance.PricingType.PAY_PER_HOUR) {
                        suffix = "(" + getString(R.string.by_live_duration) + ")";
                    } else if (type == Finance.PricingType.TOTAL) {
                        suffix = "(" + getString(R.string.by_live_total_price) + ")";
                    }

                    if (!TextUtils.isEmpty(suffix)) {
                        mSalePromotionTitleTv.setText(mSalePromotionTitleTv.getText().toString() + suffix);
                    }
                }

                mSalePromotionLayout.setVisibility(View.VISIBLE);
                if (promotions.length == 1) {
                    setOnlyOnePromotion(lesson.getFee(), promotions[0], type);
                } else if (promotions.length == 2) {
                    int i = 0;
                    Promotion temp = null;
                    for (Promotion p : promotions) {
                        if (p != null) {
                            temp = p;
                            i++;
                        }
                    }

                    if (i == 1) {
                        //only one valid promotion
                        setOnlyOnePromotion(lesson.getFee(), temp, type);
                    } else if (i == 2) {
                        mEnrollBeforePromotionLayout.setVisibility(View.VISIBLE);
                        mLessonBeforePromotionLayout.setVisibility(View.VISIBLE);

                        setSalePromotion(lesson.getFee(), promotions[0], type);
                        setSalePromotion(lesson.getFee(), promotions[1], type);
                        //enroll before promotion
                        mEnrollBeforeTitle.setText(getString(R.string.promotion_one));
                        //lesson before promotion
                        mLessonBeforeTitle.setText(getString(R.string.promotion_two));
                    } else {
                        mSalePromotionLayout.setVisibility(View.GONE);
                    }
                }
            } else {
                mSalePromotionLayout.setVisibility(View.GONE);
            }


        }
    }

    private void setOnlyOnePromotion(Price fee, Promotion promotion, int type) {
        if (promotion == null) {
            mSalePromotionLayout.setVisibility(View.GONE);
        } else {
            setSalePromotion(fee, promotion, type);

            if (promotion.getQuota() > 0) {
                //enroll before promotion
                mEnrollBeforePromotionLayout.setVisibility(View.VISIBLE);
                mLessonBeforePromotionLayout.setVisibility(View.GONE);
                mEnrollBeforeTitle.setText(getString(R.string.promotion_one));
            } else if (promotion.getBefore() > 0) {
                //lesson before promotion
                mEnrollBeforePromotionLayout.setVisibility(View.GONE);
                mLessonBeforePromotionLayout.setVisibility(View.VISIBLE);
                mLessonBeforeTitle.setText(getString(R.string.promotion_one));
            }
        }
    }

    private void setSalePromotion(Price fee, Promotion promotion, int type) {
        if (promotion == null) {
            return;
        }

        if (promotion.getQuota() > 0) {
            //enroll before promotion
            String price = fee == null ? String.valueOf("0") :
                    BaseBusiness.formatPrice(fee.total * promotion.getDiscount(), true);
            String discount = BaseBusiness.formatDiscount(promotion.getDiscount());
            String s = getString(R.string.enroll_before_promotion, promotion.getQuota(), discount, price);
            if (type == Finance.PricingType.PAY_PER_HOUR) {
                s = s + getString(R.string.per_hour);
            }
            mEnrollBeforeTv.setText(Html.fromHtml(s));
        } else if (promotion.getBefore() > 0) {
            //lesson before promotion
            String price = fee == null ? String.valueOf("0") :
                    BaseBusiness.formatPrice(fee.total * promotion.getDiscount(), true);
            String discount = BaseBusiness.formatDiscount(promotion.getDiscount());
            String s = getString(R.string.lesson_before_promotion, promotion.getBefore(), discount, price);
            if (type == Finance.PricingType.PAY_PER_HOUR) {
                s = s + getString(R.string.per_hour);
            }
            mLessonBeforeTv.setText(Html.fromHtml(s));
        }
    }

    //TODO not implemented
    private void setVisibleToStu() {

    }

    //TODO not data
    private void setAuditPerson() {
        int portraitSize = getResources().getDimensionPixelSize(R.dimen.px42);
        int marginLeft = getResources().getDimensionPixelSize(R.dimen.px12);
        ImageView portrait = buildAuditPortrait(portraitSize, marginLeft);
        mAuditPortraitLayout.addView(portrait);
    }

    private ImageView buildAuditPortrait(int portraitSize, int marginLeft) {
        RoundedImageView imageView = new RoundedImageView(this);
        imageView.setOval(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(portraitSize, portraitSize);
        imageView.setLayoutParams(params);
        params.leftMargin = marginLeft;

        return imageView;
    }

}
