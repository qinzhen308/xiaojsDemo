package com.benyuan.xiaojs.ui.course;
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

import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
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
import com.benyuan.xiaojs.model.LiveLesson;
import com.benyuan.xiaojs.model.Promotion;
import com.benyuan.xiaojs.model.Schedule;
import com.benyuan.xiaojs.ui.base.BaseBusiness;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.ui.widget.EditTextDel;
import com.benyuan.xiaojs.util.DataPicker;
import com.benyuan.xiaojs.util.TimeUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class LiveLessonCreationFragment extends BaseFragment {
    private final int MAX_ENROLL_NUM = 9999; //
    private final int MAX_LESSON_DURATION = 600; //600 minutes, 10 hours
    private final int REQUEST_CODE_OPTIONAL_INFO = 2000;

    private final int MIN_LESSON_CHAR = 3;
    private final int MAX_LESSON_CHAR = 25;

    @BindView(R.id.live_lesson_name)
    EditTextDel mLiveLessonNameEdt;
    @BindView(R.id.subject_category)
    TextView mSubjectTv;
    @BindView(R.id.enroll_people_limit)
    EditTextDel mEnrollLimitEdt;
    @BindView(R.id.teach_form)
    TextView mTeachFormTv;
    @BindView(R.id.enroll_way_switcher)
    ToggleButton mEnrollWaySwitcher;
    @BindView(R.id.charge_way_switcher)
    ToggleButton mChargeWaySwitcher;
    @BindView(R.id.by_live_duration_title)
    TextView mByLiveDurationTv;
    @BindView(R.id.by_live_total_price_title)
    TextView mByLiveTotalPriceTv;
    @BindView(R.id.charge_way)
    View mChargeWayLayout;
    @BindView(R.id.by_live_duration)
    EditTextDel mByLiveDurationEdt;
    @BindView(R.id.by_live_total_price)
    EditTextDel mByLiveTotalPriceEdt;
    @BindView(R.id.lesson_start_time)
    TextView mLessonStartTimeTv;
    @BindView(R.id.lesson_duration)
    EditTextDel mLessonDurationEdt;
    @BindView(R.id.publish_personal_page)
    TextView mPublicTv;
    @BindView(R.id.on_shelves)
    TextView mOnShelvesTv;

    private boolean mEnrollWayOpen = false;
    private long mLessonStartTime;
    private LiveLesson mLessonOptionalInfo;

    private String mSubjectId;

    private final String TEST_SUBJECT = "计算机"; //test subject
    private final String TEST_SUBJECT_ID = "5820a10e101db0af4bcf2fd9";

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_live_lesson_creation, null);
    }

    @Override
    protected void init() {
        mEnrollWaySwitcher.setChecked(false);

        mChargeWaySwitcher.setSelected(false);
        mChargeWayLayout.setVisibility(View.GONE);

        mLiveLessonNameEdt.setHint(getString(R.string.live_lesson_name_hint, MIN_LESSON_CHAR, MAX_LESSON_CHAR));
        mLiveLessonNameEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LESSON_CHAR)});

        mOnShelvesTv.setSelected(true);
        mPublicTv.setSelected(true);

        //TODO
        mSubjectTv.setText(TEST_SUBJECT); //test


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

    @OnClick({R.id.subject_category, R.id.teach_form, R.id.enroll_way_switcher, R.id.charge_way_switcher,
            R.id.by_live_total_price_title, R.id.by_live_duration_title, R.id.lesson_start_time,
            R.id.optional_info, R.id.sub_btn, R.id.on_shelves, R.id.publish_personal_page})
    public void onClick(View v) {
        boolean isSelected = false;
        switch (v.getId()) {
            case R.id.subject_category:
                break;
            case R.id.teach_form:
                selectTeachForm();
                break;
            case R.id.enroll_way_switcher:
                break;
            case R.id.charge_way_switcher:
                openOrCloseChargeWay(v);
                break;
            case R.id.by_live_total_price_title:
            case R.id.by_live_duration_title:
                selectChargeMode(v);
                break;
            case R.id.lesson_start_time:
                selectLessonStartTime();
                break;
            case R.id.optional_info:
                setOptionalInfo();
                break;
            case R.id.on_shelves:
            case R.id.publish_personal_page:
                v.setSelected(!v.isSelected() ? true : false);
                break;
            case R.id.sub_btn:
                createLiveLesson();
                break;
            default:
                break;
        }
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
        DataPicker.pickFutureDate(mContext, new DataPicker.OnDatePickListener() {
            @Override
            public void onDatePicked(int year, int month, int day, int hour, int minute, int second) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hour, minute, second);
                mLessonStartTime = calendar.getTimeInMillis();
                String dateStr = TimeUtil.formatDate(mLessonStartTime, TimeUtil.TIME_YYYY_MM_DD_HH_MM);
                mLessonStartTimeTv.setText(dateStr);
            }
        });
    }

    private void selectChargeMode(View v) {
        boolean isSelected = v.isSelected();
        switch (v.getId()) {
            case R.id.by_live_total_price_title:
                v.setSelected(!isSelected ? true : false);
                mByLiveDurationTv.setSelected(isSelected);
                mByLiveDurationEdt.setEnabled(isSelected);
                mByLiveTotalPriceEdt.setEnabled(!isSelected);
                break;
            case R.id.by_live_duration_title:
                v.setSelected(!isSelected ? true : false);
                mByLiveTotalPriceTv.setSelected(isSelected);
                mByLiveDurationEdt.setEnabled(!isSelected);
                mByLiveTotalPriceEdt.setEnabled(isSelected);
                break;
        }
    }

    private void setOptionalInfo() {
        LiveLesson lesson = new LiveLesson();
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
        String limitStr = mEnrollLimitEdt.getText().toString();
        int limit = 0;
        if (!TextUtils.isEmpty(limitStr)) {
            limit = Integer.parseInt(limitStr);
        }

        Enroll enroll = new Enroll();
        enroll.setMax(limit);
        Schedule sch = new Schedule();
        sch.setStart(new Date(mLessonStartTime));
        Fee fee = new Fee();
        fee.setFree(mChargeWaySwitcher.isChecked());
        fee.setCharge(BigDecimal.valueOf(price));
        fee.setType(pricingType);
        lesson.setEnroll(enroll);
        lesson.setSchedule(sch);
        lesson.setFee(fee);

        i.putExtra(CourseConstant.KEY_LESSON_OPTIONAL_INFO, lesson);
        startActivityForResult(i, REQUEST_CODE_OPTIONAL_INFO);
    }

    /**
     * @return the info checked is legal
     */
    private boolean checkSubmitInfo() {
        try {
            String selectTip = mContext.getString(R.string.please_select);

            String name = mLiveLessonNameEdt.getText().toString();
            if (TextUtils.isEmpty(name) || name.length() < MIN_LESSON_CHAR || name.length() > MAX_LESSON_CHAR) {
                String nameEr = mContext.getString(R.string.live_lesson_name_error, MIN_LESSON_CHAR, MAX_LESSON_CHAR);
                Toast.makeText(mContext, nameEr, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (TextUtils.isEmpty(mSubjectTv.getText().toString().trim())) {
                Toast.makeText(mContext, R.string.subject_empty, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (selectTip.equals(mTeachFormTv.getText().toString())) {
                Toast.makeText(mContext, R.string.teach_form_empty, Toast.LENGTH_SHORT).show();
                return false;
            }

            String limitPeople = mEnrollLimitEdt.getText().toString().trim();
            if (TextUtils.isEmpty(limitPeople)) {
                Toast.makeText(mContext, R.string.enroll_people_empty, Toast.LENGTH_SHORT).show();
                return false;
            }
            int limit = Integer.parseInt(limitPeople);
            /*if (limit > MAX_ENROLL_NUM) {
                String tips = String.format(getString(R.string.enroll_people_must_be_less_than), MAX_ENROLL_NUM);
                Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
                return false;
            }*/
            if (limit <= 0) {
                Toast.makeText(mContext, R.string.enroll_people_must_be_positive, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (mChargeWaySwitcher.isChecked()) {
                if (!mByLiveTotalPriceTv.isSelected() && !mByLiveDurationTv.isSelected()) {
                    //提示请选择收费模式
                    Toast.makeText(mContext, R.string.charge_must_be_checked, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            String startTime = mLessonStartTimeTv.getText().toString().trim();
            if (TextUtils.isEmpty(startTime) || selectTip.equals(startTime)) {
                Toast.makeText(mContext, R.string.lesson_start_time_empty, Toast.LENGTH_SHORT).show();
                return false;
            }

            String durationStr = mLessonDurationEdt.getText().toString().trim();
            if (TextUtils.isEmpty(durationStr)) {
                Toast.makeText(mContext, R.string.lesson_duration_empty, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (Integer.parseInt(durationStr) > MAX_LESSON_DURATION) {
                //上课时长不能大于
                String tips = String.format(getString(R.string.lesson_duration_must_be_less_than), MAX_LESSON_DURATION);
                Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private void createLiveLesson() {
        if (!checkSubmitInfo()) {
            return;
        }

        Enroll enroll = new Enroll();
        int limitPeople = Integer.parseInt(mEnrollLimitEdt.getText().toString());
        enroll.setMax(limitPeople);
        enroll.setMandatory(mEnrollWaySwitcher.isChecked());

        Fee fee = new Fee();
        fee.setFree(mChargeWaySwitcher.isChecked());
        int feeType = mByLiveTotalPriceTv.isSelected() ? Finance.PricingType.TOTAL : Finance.PricingType.PAY_PER_HOUR;
        try {
            float feePrice = mByLiveTotalPriceTv.isSelected() ? Float.parseFloat(mByLiveTotalPriceEdt.getText().toString()) :
                    Float.parseFloat(mByLiveDurationEdt.getText().toString());
            fee.setType(feeType);
            fee.setCharge(BigDecimal.valueOf(feePrice));
        } catch (Exception e) {
            e.printStackTrace();
            //do nothing
        }

        Schedule sch = new Schedule();
        sch.setStart(new Date(mLessonStartTime));
        sch.setDuration(Integer.parseInt(mLessonDurationEdt.getText().toString()));

        LiveLesson.Publish publish = new LiveLesson.Publish();
        publish.setOnShelves(mPublicTv.isSelected());

        LiveLesson ll = new LiveLesson();
        String subject = mSubjectTv.getText().toString();
        ll.setTitle(mLiveLessonNameEdt.getText().toString());
        ll.setSubject(TEST_SUBJECT_ID);
        ll.setEnroll(enroll);
        ll.setMode(BaseBusiness.getTeachingMode(mContext, mTeachFormTv.getText().toString()));
        ll.setFee(fee);
        ll.setSchedule(sch);
        ll.setPublish(publish);

        //add optional info
        if (mLessonOptionalInfo != null) {
            ll.setCover(mLessonOptionalInfo.getCover());
            ll.setOverview(mLessonOptionalInfo.getOverview());
            ll.setTeachersIntro(mLessonOptionalInfo.getTeachersIntro());
            ll.setAudit(mLessonOptionalInfo.getAudit());
            ll.setPromotion(mLessonOptionalInfo.getPromotion());
        }

        CreateLesson cl = new CreateLesson();
        cl.setData(ll);

        LessonDataManager.requestCreateLiveLesson(mContext, BaseBusiness.getSession(), cl, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(mContext, object.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Toast.makeText(mContext, "error:" + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
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
        }
    }
}
