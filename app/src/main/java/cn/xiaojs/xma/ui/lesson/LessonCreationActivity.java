package cn.xiaojs.xma.ui.lesson;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Finance;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.ClaimCompetency;
import cn.xiaojs.xma.model.Competency;
import cn.xiaojs.xma.model.CreateLesson;
import cn.xiaojs.xma.model.LessonDetail;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.model.Publish;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.ctl.Enroll;
import cn.xiaojs.xma.model.ctl.Fee;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.BaseBusiness;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.DataPicker;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.XjsUtils;

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
 * Date:2016/11/2
 * Desc:
 *
 * ======================================================================================== */

public class LessonCreationActivity extends BaseActivity {
    private final int MAX_STUDENT_COUNT = 9999; //
    private final int MAX_LESSON_DURATION = 600; //600 minutes, 10 hours
    private final int REQUEST_CODE_OPTIONAL_INFO = 2000;
    private final static int REQUEST_SELECT_SUBJECT = 2001;

    private final int DEFAULT_SUT_COUNT = 50;

    private final int MIN_LESSON_CHAR = 3;
    private final int MAX_LESSON_CHAR = 25;

    private final static int HALF_HOUR = 30 * 60 * 1000; //30 minutes

    public final static String KEY_COMPETENCY = "key_competency";

    @BindView(R.id.status_fail_reason)
    TextView mStatusFailReason;
    @BindView(R.id.status_fail_reason_info)
    View mStatusFailReasonInfoView;
    @BindView(R.id.lesson_creation_tips)
    View mLessonCreateTipsView;
    @BindView(R.id.place_hold_area)
    View mPlaceHoldArea;
    @BindView(R.id.live_lesson_name)
    EditTextDel mLessonNameEdt;
    @BindView(R.id.lesson_subject)
    TextView mLessonSubjectTv;
    @BindView(R.id.lesson_stu_count)
    EditTextDel mLessonStuCount;
    @BindView(R.id.enroll_switcher)
    ToggleButton mEnrollSwitcher;
    @BindView(R.id.teach_form)
    TextView mTeachFormTv;
    @BindView(R.id.charge_layout)
    View mChargeLayout;
    @BindView(R.id.stu_count_layout)
    View mStuCountLayout;
    @BindView(R.id.stu_count_divide)
    View mStuCountDivide;
    @BindView(R.id.charge_way_switcher)
    ToggleButton mChargeWaySwitcher;
    @BindView(R.id.charge_way)
    View mChargeWayLayout;
    @BindView(R.id.by_duration_title)
    TextView mByLiveDurationTv;
    @BindView(R.id.by_total_price_title)
    TextView mByLiveTotalPriceTv;
    @BindView(R.id.by_duration)
    EditText mByLiveDurationEdt;
    @BindView(R.id.by_total_price)
    EditText mByLiveTotalPriceEdt;
    @BindView(R.id.lesson_start_time)
    TextView mLessonStartTimeTv;
    @BindView(R.id.lesson_duration)
    EditTextDel mLessonDurationEdt;
    @BindView(R.id.publish_personal_page)
    TextView mPublicTv;
    @BindView(R.id.on_shelves)
    TextView mOnShelvesTv;
    @BindView(R.id.publish_to_circle)
    TextView mPublishToCircleTv;

    private boolean mEnrollWayOpen = false;
    private long mLessonStartTime;
    private Date mLessonsDate;
    private LiveLesson mLessonOptionalInfo;

    private String mSubjectId;

    private int mContentFont;
    private int mGrayFont;
    private boolean mStuCountTipsFlag = true;
    private int mType = CourseConstant.TYPE_LESSON_CREATE;
    private String mLessonId;
    private Context mContext;
    private String mCompetencyId;
    private Competency mCompetency;

    @Override
    protected void addViewContent() {
        addView(R.layout.fragment_live_lesson_creation);
        setMiddleTitle(R.string.lesson_creation_title);

        init();
    }

    @OnClick({R.id.left_image, R.id.lesson_creation_tips_close, R.id.lesson_subject, R.id.teach_form,
            R.id.enroll_switcher, R.id.charge_way_switcher, R.id.by_total_price_title,
            R.id.by_duration_title, R.id.lesson_start_time, R.id.optional_info, R.id.sub_btn,
            R.id.on_shelves, R.id.publish_personal_page, R.id.publish_to_circle})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.lesson_creation_tips_close:
                closeCourCreateTips();
                break;
            case R.id.lesson_subject:
                selectSubject();
                break;
            case R.id.teach_form:
                selectTeachForm();
                break;
            case R.id.enroll_switcher:
                boolean checked = ((ToggleButton) v).isChecked();
                int visibility = checked ? View.VISIBLE : View.GONE;
                mChargeLayout.setVisibility(visibility);
                mStuCountLayout.setVisibility(visibility);
                mStuCountDivide.setVisibility(visibility);
                if (!checked) {
                    mTeachFormTv.setEnabled(false);
                    mTeachFormTv.setTextColor(mContentFont);
                    mTeachFormTv.setText(getString(R.string.teach_form_lecture));
                    mTeachFormTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else {
                    mTeachFormTv.setEnabled(true);
                    String s = mLessonStuCount.getText().toString();
                    mLessonStuCount.setText(s);
                    mTeachFormTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_entrance, 0);
                }
                break;
            case R.id.charge_way_switcher:
                openOrCloseChargeWay(v);
                break;
            case R.id.by_total_price_title:
            case R.id.by_duration_title:
                selectChargeMode(v);
                break;
            case R.id.lesson_start_time:
                selectLessonStartTime();
                break;
            case R.id.optional_info:
                enterOptionalInfoPage();
                break;
            case R.id.on_shelves:
            case R.id.publish_personal_page:
            case R.id.publish_to_circle:
                v.setSelected(!v.isSelected() ? true : false);
                break;
            case R.id.sub_btn:
                createOrEditLiveLesson();
                break;
            default:
                break;
        }
    }

    protected void init() {
        mContext = this;
        Bundle data = getIntent().getExtras();
        mLessonId = data != null ? data.getString(CourseConstant.KEY_LESSON_ID) : null;
        mType = data != null ? data.getInt(CourseConstant.KEY_TEACH_ACTION_TYPE, CourseConstant.TYPE_LESSON_CREATE)
                : CourseConstant.TYPE_LESSON_CREATE;

        SharedPreferences spf = XjsUtils.getSharedPreferences();
        boolean close = spf.getBoolean(CourseConstant.KEY_CLOSE_COURSE_CREATE_TIP, false);
        if (mType == CourseConstant.TYPE_LESSON_CREATE && !close) {
            mPlaceHoldArea.setVisibility(View.GONE);
            mLessonCreateTipsView.setVisibility(View.VISIBLE);
        }

        loadData();
        initView();
        addViewListener();

        //TODO test
        mChargeWaySwitcher.setEnabled(false);
    }

    private void closeCourCreateTips() {
        mPlaceHoldArea.setVisibility(View.VISIBLE);
        mLessonCreateTipsView.setVisibility(View.GONE);
        SharedPreferences spf = XjsUtils.getSharedPreferences();
        spf.edit().putBoolean(CourseConstant.KEY_CLOSE_COURSE_CREATE_TIP, true).commit();
    }

    private void loadData() {

        if (!TextUtils.isEmpty(mLessonId)) {
            showProgress(false);
            LessonDataManager.requestLessonData(mContext, mLessonId, new APIServiceCallback<LessonDetail>() {
                @Override
                public void onSuccess(LessonDetail lessonDetail) {
                    cancelProgress();
                    setFailedReason(lessonDetail);
                    initBaseInfo(lessonDetail);
                    initOptionalInfo(lessonDetail);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                }
            });
        }

        switch (mType) {
            case CourseConstant.TYPE_LESSON_CREATE:
            case CourseConstant.TYPE_LESSON_AGAIN:
                AccountDataManager.getCompetencies(this, new APIServiceCallback<ClaimCompetency>() {
                    @Override
                    public void onSuccess(ClaimCompetency object) {
                        if (object != null) {
                            try {
                                List<Competency> competencies = object.competencies;
                                if (competencies != null && competencies.size() == 1) {
                                    CSubject subject = competencies.get(0).getSubject();
                                    mCompetencyId = subject.getId();
                                    mLessonSubjectTv.setTextColor(mContentFont);
                                    mLessonSubjectTv.setText(subject.getName());
                                }
                            } catch (Exception e) {

                            }
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                    }
                });
                break;
            case CourseConstant.TYPE_LESSON_EDIT:
                break;
        }
    }

    private void setFailedReason(LessonDetail lessonDetail) {
        if (lessonDetail != null) {
            if (LessonState.REJECTED.equals(lessonDetail.getState())) {
                //TODO
                //mStatusFailReasonInfoView.setVisibility(View.VISIBLE);
                //mStatusFailReason.setText();
                //mPlaceHoldArea.setVisibility(View.GONE);
            }
        }
    }

    private void initBaseInfo(LessonDetail lessonDetail) {
        if (lessonDetail == null) {
            return;
        }

        mLessonNameEdt.setText(lessonDetail.getTitle());

        Enroll enroll = lessonDetail.getEnroll();
        if (enroll != null) {
            mLessonStuCount.setText(String.valueOf(enroll.max));
            mEnrollSwitcher.setChecked(enroll.mandatory);
        }

        mTeachFormTv.setText(BaseBusiness.getTeachingMode(mContext, lessonDetail.getMode()));

        Fee fee = lessonDetail.getFee();
        if (fee != null) {
            mChargeWaySwitcher.setChecked(!fee.free);

            if (fee.type == Finance.PricingType.TOTAL) {
                mByLiveTotalPriceTv.setSelected(true);
                mByLiveTotalPriceEdt.setText(BaseBusiness.formatPrice(fee.charge));
            } else if (fee.type == Finance.PricingType.PAY_PER_HOUR) {
                mByLiveDurationTv.setSelected(true);
                mByLiveDurationEdt.setText(BaseBusiness.formatPrice(fee.charge));
            }

            openOrCloseChargeWay(mChargeWaySwitcher);
        }

        Schedule sch = lessonDetail.getSchedule();
        if (sch != null) {
            //重新创建课程，上课时间无效
            //mLessonStartTime = sch.getStart().getTime();
            //String dateStr = TimeUtil.formatDate(mLessonStartTime, TimeUtil.TIME_YYYY_MM_DD_HH_MM);
            //mLessonStartTimeTv.setText(dateStr);
            //mLessonStartTimeTv.setTextColor(mContentFont);

            mLessonDurationEdt.setText(String.valueOf(sch.getDuration()));
        }

        //TODO publish person page, not implemented
        Publish publish = lessonDetail.getPublish();
        if (publish != null) {
            mPublicTv.setSelected(publish.accessible);
        }
        //mOnShelvesTv
    }

    private void initOptionalInfo(LessonDetail lessonDetail) {
        if (lessonDetail == null) {
            return;
        }

        if (mLessonOptionalInfo == null) {
            mLessonOptionalInfo = new LiveLesson();
        }

        mLessonOptionalInfo.setCover(lessonDetail.getCover());
        mLessonOptionalInfo.setTags(lessonDetail.getTags());
        mLessonOptionalInfo.setTeachersIntro(lessonDetail.getTeachersIntro());
        mLessonOptionalInfo.setOverview(lessonDetail.getOverview());
        mLessonOptionalInfo.setAudit(lessonDetail.getAudit());
        mLessonOptionalInfo.setPromotion(lessonDetail.getPromotion());
    }

    private void initView() {
        mEnrollSwitcher.setChecked(true);

        mChargeWaySwitcher.setChecked(false);
        mChargeWayLayout.setVisibility(View.GONE);

        mLessonNameEdt.setHint(getString(R.string.live_lesson_name_hint, MIN_LESSON_CHAR, MAX_LESSON_CHAR));
        mLessonNameEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LESSON_CHAR)});

        mOnShelvesTv.setSelected(true);
        mPublicTv.setSelected(true);

        //get color
        mContentFont = getResources().getColor(R.color.font_create_lesson_content);
        mGrayFont = getResources().getColor(R.color.font_gray);
    }


    private void addViewListener() {
        mByLiveTotalPriceEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                formatPrice(s);
            }
        });

        mByLiveDurationEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                formatPrice(s);
            }
        });

        mLessonStuCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                /*if (s.length() > 0) {
                    int count = Integer.parseInt(s.toString());

                    if (count > DEFAULT_SUT_COUNT && mStuCountTipsFlag) {
                        mStuCountTipsFlag = false;
                        String tips = getString(R.string.lesson_stu_exceed, DEFAULT_SUT_COUNT);
                        Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
                    }

                    if (count == 1) {
                        mTeachFormTv.setTextColor(mContentFont);
                        mTeachFormTv.setText(R.string.teach_form_one2one);
                    } else if (count > 1 && count <= DEFAULT_SUT_COUNT) {
                        mTeachFormTv.setTextColor(mContentFont);
                        mTeachFormTv.setText(R.string.teach_form_one2many);
                    } else if (count > DEFAULT_SUT_COUNT) {
                        mTeachFormTv.setTextColor(mContentFont);
                        mTeachFormTv.setText(R.string.teach_form_lecture);
                    }
                } else {
                    mTeachFormTv.setText(R.string.please_select);
                    mTeachFormTv.setTextColor(mGrayFont);
                }*/
            }
        });


        mLessonDurationEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    checkLessonConflict(mLessonStartTime, Integer.parseInt(s.toString()));
                }
            }
        });
    }

    private void formatPrice(Editable s) {
        String temp = s.toString();
        int posDot = temp.indexOf(".");
        if (posDot <= 0) {
            return;
        }
        if (temp.length() - posDot - 1 > 2) {
            s.delete(posDot + 3, posDot + 4);
        }
    }

    private void selectSubject() {
        //mLessonSubjectTv.setTextColor(mContentFont);
        Intent intent = new Intent();
        intent.setClass(mContext, SubjectSelectorActivity.class);
        intent.putExtra(CourseConstant.KEY_SUBJECT, mCompetency);
        startActivityForResult(intent, REQUEST_SELECT_SUBJECT);
    }

    private void selectTeachForm() {
        List<String> sexList = new ArrayList<String>();
        sexList.add(getString(R.string.teach_form_lecture));
        sexList.add(getString(R.string.teach_form_one2one));
        sexList.add(getString(R.string.teach_form_one2many));
        DataPicker.pickData(mContext, sexList, new DataPicker.OnDataPickListener() {
            @Override
            public void onDataPicked(Object data) {
                if (data instanceof String) {
                    mTeachFormTv.setText((String) data);
                    mTeachFormTv.setTextColor(mContentFont);
                }
            }
        });
    }

    private void openOrCloseChargeWay(View view) {
        if (view instanceof ToggleButton) {
            boolean isChecked = ((ToggleButton) view).isChecked();
            mChargeWayLayout.setVisibility(!isChecked ? View.GONE : View.VISIBLE);
            //set default selection
            if (isChecked && !mByLiveDurationTv.isSelected() && !mByLiveTotalPriceTv.isSelected()) {
                mByLiveTotalPriceTv.setSelected(true);
                mByLiveDurationEdt.setEnabled(false);
            }
        }
    }

    private void selectLessonStartTime() {
        if (mLessonsDate == null) {
            mLessonsDate = new Date(System.currentTimeMillis() + HALF_HOUR);
        }
        DataPicker.pickFutureDate(mContext, mLessonsDate, new DataPicker.OnDatePickListener() {
            @Override
            public void onDatePicked(int year, int month, int day, int hour, int minute, int second) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hour, minute, second);
                mLessonStartTime = calendar.getTimeInMillis();
                mLessonsDate.setTime(mLessonStartTime);
                String dateStr = TimeUtil.formatDate(mLessonStartTime, TimeUtil.TIME_YYYY_MM_DD_HH_MM);
                mLessonStartTimeTv.setText(dateStr);
                mLessonStartTimeTv.setTextColor(mContentFont);

                if (!TextUtils.isEmpty(mLessonDurationEdt.getText().toString())) {
                    checkLessonConflict(mLessonStartTime, Integer.parseInt(mLessonDurationEdt.getText().toString()));
                }
            }
        });
    }

    private void selectChargeMode(View v) {
        boolean isSelected = v.isSelected();
        switch (v.getId()) {
            case R.id.by_total_price_title:
                v.setSelected(!isSelected ? true : false);
                mByLiveDurationTv.setSelected(isSelected);
                mByLiveDurationEdt.setEnabled(isSelected);
                mByLiveTotalPriceEdt.setEnabled(!isSelected);
                break;
            case R.id.by_duration_title:
                v.setSelected(!isSelected ? true : false);
                mByLiveTotalPriceTv.setSelected(isSelected);
                mByLiveDurationEdt.setEnabled(!isSelected);
                mByLiveTotalPriceEdt.setEnabled(isSelected);
                break;
        }
    }

    private void enterOptionalInfoPage() {
        if (mLessonOptionalInfo == null) {
            mLessonOptionalInfo = new LiveLesson();
        }

        LiveLesson lesson = mLessonOptionalInfo;
        Intent i = new Intent(mContext, LessonCreationOptionalInfoActivity.class);
        int pricingType = Finance.PricingType.TOTAL;
        float price = 0;
        if (mByLiveTotalPriceTv.isSelected()) {
            pricingType = Finance.PricingType.TOTAL;
            String priceStr = mByLiveTotalPriceEdt.getText().toString();
            if (!TextUtils.isEmpty(priceStr)) {
                price = Float.parseFloat(priceStr);
            }
        } else if (mByLiveDurationTv.isSelected()) {
            pricingType = Finance.PricingType.PAY_PER_HOUR;
            String priceStr = mByLiveDurationEdt.getText().toString();
            if (!TextUtils.isEmpty(priceStr)) {
                price = Float.parseFloat(priceStr);
            }
        }
        String limitStr = mLessonStuCount.getText().toString();
        int limit = 0;
        if (!TextUtils.isEmpty(limitStr)) {
            limit = Integer.parseInt(limitStr);
        }

        Enroll enroll = new Enroll();
        enroll.max = limit;
        enroll.mandatory = mEnrollSwitcher.isChecked();
        Schedule sch = new Schedule();
        sch.setStart(new Date(mLessonStartTime));
        Fee fee = new Fee();
        fee.free = !mChargeWaySwitcher.isChecked();
        fee.charge = price;
        fee.type = pricingType;
        lesson.setEnroll(enroll);
        lesson.setSchedule(sch);
        lesson.setFee(fee);

        i.putExtra(CourseConstant.KEY_LESSON_ID, mLessonId);
        i.putExtra(CourseConstant.KEY_LESSON_OPTIONAL_INFO, lesson);
        startActivityForResult(i, REQUEST_CODE_OPTIONAL_INFO);
    }

    //TODO not implemented
    private void checkLessonConflict(long startTime, int duration) {
        //Toast.makeText(mContext, R.string.lesson_duration_conflict, Toast.LENGTH_SHORT).show();
    }

    /**
     * @return the info checked is legal
     */
    private boolean checkSubmitInfo() {
        try {
            String selectTip = mContext.getString(R.string.please_select);

            String name = mLessonNameEdt.getText().toString();
            if (TextUtils.isEmpty(name) || name.length() < MIN_LESSON_CHAR || name.length() > MAX_LESSON_CHAR) {
                String nameEr = mContext.getString(R.string.live_lesson_name_error, MIN_LESSON_CHAR, MAX_LESSON_CHAR);
                Toast.makeText(mContext, nameEr, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (selectTip.equals(mLessonSubjectTv.getText().toString()) ||
                    TextUtils.isEmpty(mLessonSubjectTv.getText().toString().trim())) {
                Toast.makeText(mContext, R.string.subject_empty, Toast.LENGTH_SHORT).show();
                return false;
            }

            /*if (selectTip.equals(mTeachFormTv.getText().toString())) {
                Toast.makeText(mContext, R.string.teach_form_empty, Toast.LENGTH_SHORT).show();
                return false;
            }*/

            String limitPeople = mLessonStuCount.getText().toString().trim();
            if (mEnrollSwitcher.isChecked() && TextUtils.isEmpty(limitPeople)) {
                Toast.makeText(mContext, R.string.lesson_people_empty, Toast.LENGTH_SHORT).show();
                return false;
            }
            int limit = TextUtils.isEmpty(limitPeople) ? 0 : Integer.parseInt(limitPeople);
            /*if (limit > MAX_STUDENT_COUNT) {
                String tips = String.format(getString(R.string.enroll_people_must_be_less_than), MAX_STUDENT_COUNT);
                Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
                return false;
            }*/
            if (mEnrollSwitcher.isChecked() && limit <= 0) {
                Toast.makeText(mContext, R.string.lesson_people_must_be_positive, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (mChargeWaySwitcher.isChecked()) {
                if (!mByLiveTotalPriceTv.isSelected() && !mByLiveDurationTv.isSelected()) {
                    Toast.makeText(mContext, R.string.charge_must_be_checked, Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (mByLiveTotalPriceTv.isSelected()) {
                    if (TextUtils.isEmpty(mByLiveTotalPriceEdt.getText().toString())) {
                        Toast.makeText(mContext, R.string.charge_empty, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }

                if (mByLiveDurationTv.isSelected()) {
                    if (TextUtils.isEmpty(mByLiveDurationEdt.getText().toString())) {
                        Toast.makeText(mContext, R.string.charge_empty, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }

            String startTime = mLessonStartTimeTv.getText().toString().trim();
            if (TextUtils.isEmpty(startTime) || selectTip.equals(startTime)) {
                Toast.makeText(mContext, R.string.lesson_start_time_empty, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (mLessonStartTime <= System.currentTimeMillis()) {
                Toast.makeText(mContext, R.string.lesson_start_time_error, Toast.LENGTH_SHORT).show();
                return false;
            }

            String durationStr = mLessonDurationEdt.getText().toString().trim();
            if (TextUtils.isEmpty(durationStr)) {
                Toast.makeText(mContext, R.string.lesson_duration_empty, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (Integer.parseInt(durationStr) > MAX_LESSON_DURATION) {
                String tips = String.format(getString(R.string.lesson_duration_must_be_less_than), MAX_LESSON_DURATION);
                Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private void createOrEditLiveLesson() {
        if (!checkSubmitInfo()) {
            return;
        }

        Enroll enroll = new Enroll();

        String studentNum = mLessonStuCount.getText().toString();

        int limitPeople = TextUtils.isEmpty(studentNum) ? 0: Integer.parseInt(studentNum);
        enroll.max = limitPeople;
        enroll.mandatory = mEnrollSwitcher.isChecked();

        Fee fee = new Fee();
        boolean isFree = !mChargeWaySwitcher.isChecked();
        fee.free = isFree;
        if (isFree) {
            fee.type = Finance.PricingType.TOTAL;
        } else {
            //charge
            int feeType = mByLiveTotalPriceTv.isSelected() ? Finance.PricingType.TOTAL : Finance.PricingType.PAY_PER_HOUR;
            try {
                float feePrice = mByLiveTotalPriceTv.isSelected() ? Float.parseFloat(mByLiveTotalPriceEdt.getText().toString()) :
                        Float.parseFloat(mByLiveDurationEdt.getText().toString());
                fee.type = feeType;
                fee.charge = feePrice;
            } catch (Exception e) {
                e.printStackTrace();
                fee.type = Finance.PricingType.TOTAL;
                fee.charge = 0;
            }
        }

        Schedule sch = new Schedule();
        sch.setStart(new Date(mLessonStartTime));
        sch.setDuration(Integer.parseInt(mLessonDurationEdt.getText().toString()));



        LiveLesson ll = new LiveLesson();
        //String subject = mLessonSubjectTv.getText().toString();
        ll.setTitle(mLessonNameEdt.getText().toString());

        CSubject subject1 = new CSubject();
        subject1.setId(mCompetencyId);

        ll.setSubject(subject1);
        ll.setEnroll(enroll);
        ll.setMode(BaseBusiness.getTeachingMode(mContext, mTeachFormTv.getText().toString()));
        ll.setFee(fee);
        ll.setSchedule(sch);
        ll.setAccessible(mPublicTv.isSelected());
        ll.setAutoOnShelves(mOnShelvesTv.isSelected());
        //mPublishToCircleTv.isSelected();

        //add optional 6
        if (mLessonOptionalInfo != null) {
            ll.setCover(mLessonOptionalInfo.getCover());
            ll.setOverview(mLessonOptionalInfo.getOverview());
            ll.setTeachersIntro(mLessonOptionalInfo.getTeachersIntro());
            ll.setAudit(mLessonOptionalInfo.getAudit());
            ll.setPromotion(mLessonOptionalInfo.getPromotion());
        }

        CreateLesson cl = new CreateLesson();
        cl.setData(ll);

        switch (mType) {
            case CourseConstant.TYPE_LESSON_CREATE:
            case CourseConstant.TYPE_LESSON_AGAIN:
                requestCreateLesson(cl);
                break;
            case CourseConstant.TYPE_LESSON_EDIT:
                requestEditLesson(ll);
                break;
        }

    }

    private void requestCreateLesson(CreateLesson cl) {
        showProgress(false);
        LessonDataManager.requestCreateLiveLesson(mContext, cl, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();
                Toast.makeText(mContext, R.string.lesson_creation_success, Toast.LENGTH_SHORT).show();
                setResultOnFinish();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestEditLesson(LiveLesson ls) {
        showProgress(false);
        LessonDataManager.requestEditLesson(mContext, mLessonId, ls, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();
                Toast.makeText(mContext, R.string.lesson_edit_success, Toast.LENGTH_SHORT).show();
                setResultOnFinish();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setResultOnFinish() {
        if (mType == CourseConstant.TYPE_LESSON_EDIT || mType == CourseConstant.TYPE_LESSON_AGAIN || mType == CourseConstant.TYPE_LESSON_CREATE) {
            setResult(RESULT_OK);
        }

        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_OPTIONAL_INFO) {
            if (data != null) {
                Object obj = data.getSerializableExtra(CourseConstant.KEY_LESSON_OPTIONAL_INFO);
                if (obj instanceof LiveLesson) {
                    mLessonOptionalInfo = (LiveLesson) obj;
                }
            }
        } else if (requestCode == REQUEST_SELECT_SUBJECT) {
            if (data != null) {
                mCompetency = (Competency)data.getSerializableExtra(KEY_COMPETENCY);
                CSubject subject = null;
                if (mCompetency != null && (subject = mCompetency.getSubject()) != null) {
                    mLessonSubjectTv.setTextColor(mContentFont);
                    mCompetencyId = subject.getId();
                    mLessonSubjectTv.setText(subject.getName());
                }
            }
        }
    }

}
