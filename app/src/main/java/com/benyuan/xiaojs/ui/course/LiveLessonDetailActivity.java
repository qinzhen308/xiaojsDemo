package com.benyuan.xiaojs.ui.course;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.xf_foundation.schemas.Finance;
import com.benyuan.xiaojs.data.LessonDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.CreateLesson;
import com.benyuan.xiaojs.model.Enroll;
import com.benyuan.xiaojs.model.Fee;
import com.benyuan.xiaojs.model.LessonDetail;
import com.benyuan.xiaojs.model.LiveLesson;
import com.benyuan.xiaojs.model.Promotion;
import com.benyuan.xiaojs.model.Schedule;
import com.benyuan.xiaojs.model.TeachLesson;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.base.BaseBusiness;
import com.benyuan.xiaojs.ui.widget.RoundedImageView;
import com.benyuan.xiaojs.util.TimeUtil;
import com.bumptech.glide.Glide;

import java.util.Date;

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
        final int padding = getResources().getDimensionPixelOffset(R.dimen.px28);

        ViewTreeObserver observer = mLessonBriefTv.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                int count = mLessonBriefTv.getLineCount();
                if (count > INTRO_DEFAULT_LINES) {
                    if (mUnfoldLessonBriefTv.getVisibility() != View.VISIBLE) {
                        mUnfoldLessonBriefTv.setVisibility(View.VISIBLE);
                    }
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
            mLessonSubjectTv.setText(lesson.getSubject());

            //enroll
            Enroll enroll = lesson.getEnroll();
            if (enroll != null && enroll.isMandatory()) {
                mEnrolledView.setVisibility(View.VISIBLE);
                mEnrolledDivideLine.setVisibility(View.VISIBLE);
            } else {
                mEnrolledView.setVisibility(View.GONE);
                mEnrolledDivideLine.setVisibility(View.GONE);
            }

            String p = getString(R.string.person);
            mLessonStuCountTv.setText(String.valueOf(enroll.getMax()) + p);
            mTeachFormTv.setText(BaseBusiness.getTeachingMode(this, lesson.getMode()));

            //fee
            Fee fee = lesson.getFee();
            if (fee == null || fee.isFree()) {
                mLessonFeeTv.setText(R.string.free);
            } else {
                float charge = fee.getCharge();
                if (fee.getType() == Finance.PricingType.TOTAL) {
                    String byTotalPrice = getString(R.string.by_live_total_price);
                    mLessonFeeTv.setText(byTotalPrice + BaseBusiness.formatPrice(charge, true));
                } else if (fee.getType() == Finance.PricingType.PAY_PER_HOUR) {
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
                Glide.with(this).load(lesson.getCover()).into(mLessonCover);
            } else {
                //set gone
                mLessonCoverLayout.setVisibility(View.GONE);
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
            Fee fee = lesson.getFee();
            if (fee == null || fee.isFree()) {
                mSalePromotionLayout.setVisibility(View.GONE);
                return;
            }
            Promotion[] promotions = lesson.getPromotion();
            if (promotions != null && promotions.length > 0) {
                //set sale promotion title
                int type = Finance.PricingType.TOTAL;
                if (lesson.getFee() != null) {
                    type = lesson.getFee().getType();
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

    private void setOnlyOnePromotion(Fee fee, Promotion promotion, int type) {
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

    private void setSalePromotion(Fee fee, Promotion promotion, int type) {
        if (promotion == null) {
            return;
        }

        if (promotion.getQuota() > 0) {
            //enroll before promotion
            String price = fee == null ? String.valueOf("0") :
                    BaseBusiness.formatPrice(fee.getTotal() * promotion.getDiscount(), true);
            String discount = BaseBusiness.formatDiscount(promotion.getDiscount());
            String s = getString(R.string.enroll_before_promotion, promotion.getQuota(), discount, price);
            if (type == Finance.PricingType.PAY_PER_HOUR) {
                s = s + getString(R.string.per_hour);
            }
            mEnrollBeforeTv.setText(Html.fromHtml(s));
        } else if (promotion.getBefore() > 0) {
            //lesson before promotion
            String price = fee == null ? String.valueOf("0") :
                    BaseBusiness.formatPrice(fee.getTotal() * promotion.getDiscount(), true);
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

    private LiveLesson getTestLesson() {
        //===============base info====================
        //enroll
        Enroll enroll = new Enroll();
        int limitPeople = 55;
        enroll.setMax(limitPeople);
        enroll.setMandatory(true);

        //fee
        Fee fee = new Fee();
        fee.setFree(false);
        fee.setType(Finance.PricingType.PAY_PER_HOUR);
        fee.setCharge(2.5f);

        //schedule
        Schedule sch = new Schedule();
        sch.setStart(new Date(System.currentTimeMillis()));
        sch.setDuration(106);

        //publish
        LiveLesson.Publish publish = new LiveLesson.Publish();
        publish.setOnShelves(true);

        LiveLesson ll = new LiveLesson();
        ll.setTitle("气质形象提升修炼");
        ll.setSubject("职业技能");
        ll.setEnroll(enroll);
        ll.setMode(BaseBusiness.getTeachingMode(this, BaseBusiness.getTeachingMode(this, 1)));
        ll.setFee(fee);
        ll.setSchedule(sch);
        ll.setPublish(publish);

        //=================optional info===================
        //cover
        ll.setCover("http://pic6.nipic.com/20100309/3763676_184422049146_2.jpg");

        //Overview
        LiveLesson.Overview overview = new LiveLesson.Overview();
        overview.setText("《隧道工程》是土木工程、交通工程学科的主要技术基础课，本、专科学生每年听课人数达1000多人。" +
                "本课程自建校之初开课至今已有50多年的历史，伴随新中国隧道及地下工程的大量兴建、国内外隧道工程技术的不断提高" +
                "，本课程也不断更新发展，为培养一批能适应现场需要、能解决实际工程问题的隧道工程师、技术人才做出了重要贡献" +
                "隧道是一种埋置于地层中的工程建筑物，隧道周围的地层(围岩)既对隧道结构产生作用，又是隧道承受荷载的体系之一。" +
                "隧道结构与围岩的相互作用，决定了隧道工程受围岩条件影响显著，由于地层情况千变万化，地层特性错综复杂，这种相互影" +
                "响关系还没有被充分认识，现有的理论体系也还不能完善的解释工程实际问题，因而目前隧道设计常常需要理论分析与工程类" +
                "比相结合，而目前大量出现的隧道新结构、新方法、新技术又需要从理论上加以认识、总结和提高，故本课程需要理论与实践高" +
                "度结合，课程内容也需要不断更新和发展，它也是工程地质学、结构设计理论与岩土力学及各种工程新技术的综合体。");
        ll.setOverview(overview);

        LiveLesson.TeachersIntro teachersIntro = new LiveLesson.TeachersIntro();
        teachersIntro.setText("陈秀娟 ：" +
                "任教学科：未分组 名师名校长后备 高级教师 区学科骨干本学年主要教学工作自我介绍&教学格言" +
                "中学英语高级教师，浦东新区骨干教师，区初中英语学科中心组成员、上海市普教系统英语名师培养基" +
                "地第一批学员。曾多次获得北蔡镇教育系统“先进工作者”称号，被浦东新区社发局授予“青年突击手”称号" +
                "。在北蔡中学连续担任17年班主任，深受学生喜爱。课堂教学能遵循学生的认知发展规律，能吸引学生主" +
                "动学习。教学之余能分析教学中的得与失，撰写的多篇论文在各类杂志上发表，曾获得过国家级论文比赛二等奖。" +
                "教育格言是“爱，是教育的灵魂。");
        //teachersIntro.setText(teachersIntro.getText().substring(0, 50));
        ll.setTeachersIntro(teachersIntro);

        //audit
        ll.setAudit(null);

        //promotion
        Promotion[] promotions = new Promotion[2];
        Promotion pr1 = new Promotion();
        Promotion pr2 = new Promotion();
        pr1.setBefore(22);
        pr1.setDiscount(8.6f);

        pr2.setQuota(10);
        pr2.setDiscount(5.8f);
        promotions[0] = pr1;
        promotions[1] = pr2;

        ll.setPromotion(promotions);

        CreateLesson cl = new CreateLesson();
        cl.setData(ll);


        return ll;
    }
}
