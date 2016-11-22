package com.benyuan.xiaojs.ui.course;

import android.content.Intent;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.xf_foundation.schemas.Finance;
import com.benyuan.xiaojs.model.LiveLesson;
import com.benyuan.xiaojs.model.Promotion;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.base.BaseBusiness;
import com.benyuan.xiaojs.util.TimeUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

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
 * Date:2016/11/8
 * Desc:
 *
 * ======================================================================================== */

public class SalePromotionActivity extends BaseActivity {
    @BindView(R.id.original_price)
    TextView mOriginalPriceTv;
    @BindView(R.id.enroll_before)
    EditText mEnrollBeforeEdt;
    @BindView(R.id.enroll_discount)
    EditText mEnrollDiscountEdt;
    @BindView(R.id.enroll_promotion_price)
    TextView mEnrollPromotionPriceTv;
    @BindView(R.id.attend_lesson_before)
    EditText mLessonBeforeEdt;
    @BindView(R.id.attend_lesson_discount)
    EditText mLessonDiscountEdt;
    @BindView(R.id.attend_lesson_promotion_price)
    TextView mLessonPromotionPriceTv;
    @BindView(R.id.sale_promotion_one_status)
    TextView mPromotionOneStatus;
    @BindView(R.id.sale_promotion_two_status)
    TextView mPromotionTwoStatus;
    @BindView(R.id.sale_promotion_one_dl)
    View mPromotionOneDl;
    @BindView(R.id.sale_promotion_two_dl)
    View mPromotionTwoDl;



    private final static int PROMOTION_ENROLL_BEFORE = 1;
    private final static int PROMOTION_LESSON_BEFORE = 2;

    private int mDiscountColor;
    private int mDiscountBlackColor;
    private LiveLesson mLesson;

    private float mOriginalPrice;
    private int mPricingType;
    private String mUnitPriceStr;
    Promotion[] mPromotions;
    private long mLessonStartTime;
    private int mLimit;

    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.sale_promotion_set);

        addView(R.layout.activity_sale_promotion);

        mEnrollBeforeEdt.addTextChangedListener(new PromotionTextWatcher(mEnrollBeforeEdt));
        mLessonBeforeEdt.addTextChangedListener(new PromotionTextWatcher(mLessonBeforeEdt));
        mEnrollDiscountEdt.addTextChangedListener(new PromotionTextWatcher(mEnrollDiscountEdt));
        mLessonDiscountEdt.addTextChangedListener(new PromotionTextWatcher(mLessonDiscountEdt));

        //hide
        //mPromotionOneStatus.setVisibility(View.GONE);
        //mPromotionTwoStatus.setVisibility(View.GONE);
        //mPromotionOneDl.setVisibility(View.GONE);
        //mPromotionTwoDl.setVisibility(View.GONE);

        initData();
    }

    @OnClick({R.id.left_image, R.id.sub_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.sub_btn:
                //set promotion
                boolean flag = checkSubmitInfo();
                if (flag) {
                    if (mPromotions != null && mPromotions.length > 0) {
                        mLesson.setPromotion(mPromotions);
                        Intent i = new Intent();
                        i.putExtra(CourseConstant.KEY_LESSON_OPTIONAL_INFO, mLesson);
                        setResult(RESULT_OK, i);
                        finish();
                    } else {
                        Toast.makeText(SalePromotionActivity.this, getString(R.string.promotion_info_empty),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void initData() {
        mDiscountColor = getResources().getColor(R.color.font_red);
        mDiscountBlackColor = getResources().getColor(R.color.font_black);
        Object object = getIntent().getSerializableExtra(CourseConstant.KEY_LESSON_OPTIONAL_INFO);
        if (object instanceof LiveLesson) {
            mLesson = (LiveLesson) object;
        }

        mUnitPriceStr = getString(R.string.per_hour);
        try {
            mOriginalPrice = mLesson.getFee().getCharge().floatValue();
            mPricingType = mLesson.getFee().getType();
            mLessonStartTime = mLesson.getSchedule().getStart().getTime();
            mLimit = mLesson.getEnroll().getMax();
            mOriginalPriceTv.setText(formatPrice(mOriginalPrice, mPricingType));
        } catch (Exception e) {

        }

        Promotion[] promotions = null;
        if (mLesson != null && (promotions = mLesson.getPromotion()) != null) {
            for (Promotion promotion : promotions) {
                if (promotion != null) {
                    if (promotion.getType() == PROMOTION_ENROLL_BEFORE) {
                        mEnrollBeforeEdt.setText(String.valueOf(promotion.getQuota()));
                        mEnrollDiscountEdt.setText(String.valueOf(promotion.getDiscount() * 10));
                        mEnrollPromotionPriceTv.setText(formatPrice(mOriginalPrice * promotion.getDiscount(),
                                PROMOTION_ENROLL_BEFORE));
                    } else if (promotion.getType() == PROMOTION_LESSON_BEFORE) {
                        mLessonBeforeEdt.setText(String.valueOf(promotion.getBefore()));
                        mLessonDiscountEdt.setText(String.valueOf(promotion.getDiscount() * 10));
                        mLessonPromotionPriceTv.setText(formatPrice(mOriginalPrice * promotion.getDiscount(),
                                PROMOTION_LESSON_BEFORE));
                    }
                }
            }
        }
    }

    private class PromotionTextWatcher implements TextWatcher {
        private View mView;

        public PromotionTextWatcher(View view) {
            mView = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            float op = 1.0f;
            switch (mView.getId()) {
                case R.id.enroll_before:
                    checkLimitPeople(s.toString(), true);
                    mPromotionOneStatus.setSelected(checkPromotionOne());
                    break;
                case R.id.attend_lesson_before:
                    checkLessonDay(s.toString(), true);
                    mPromotionTwoStatus.setSelected(checkPromotionTwo());
                    break;
                case R.id.enroll_discount:
                    if (s.length() > 0) {
                        if (checkDiscountLegal(s.toString(), true)) {
                            mEnrollPromotionPriceTv.setText(formatPrice(mOriginalPrice * getDiscount(s.toString()), mPricingType));
                        } else {
                            mEnrollPromotionPriceTv.setText("");
                        }
                    } else {
                        mEnrollPromotionPriceTv.setText("");
                    }
                    mPromotionOneStatus.setSelected(checkPromotionOne());
                    break;
                case R.id.attend_lesson_discount:
                    if (s.length() > 0) {
                        if (checkDiscountLegal(s.toString(), true)) {
                            mLessonPromotionPriceTv.setText(formatPrice(mOriginalPrice * getDiscount(s.toString()), mPricingType));
                        } else {
                            mLessonPromotionPriceTv.setText("");
                        }
                    } else {
                        mLessonPromotionPriceTv.setText("");
                    }
                    mPromotionTwoStatus.setSelected(checkPromotionTwo());
                    break;
            }
        }
    }

    private long getSpaceDays() {
        if (System.currentTimeMillis() > mLessonStartTime) {
            //默认是当天，特殊处理
            return 1;
        }

        long[] time = TimeUtil.getSpaceTime(System.currentTimeMillis(), mLessonStartTime);
        long day = time[0];
        if (time[1] != 0 || time[2] != 0 || time[3] != 0) {
            return day + 1;
        }

        return day;
    }

    private float getDiscount(String s) {
        if (s.length() > 0) {
            return Float.parseFloat(s.toString()) / 10.f;
        }

        return 1;
    }

    private SpannableString formatPrice(float price, int pricingType) {
        String priceStr = BaseBusiness.formatPrice(price, true);
        SpannableString ss = new SpannableString(priceStr);
        ss.setSpan(new ForegroundColorSpan(mDiscountColor), 0, priceStr.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (pricingType == Finance.PricingType.PAY_PER_HOUR) {
            int start = priceStr.length();
            priceStr += mUnitPriceStr;
            ss = new SpannableString(priceStr);
            ss.setSpan(new ForegroundColorSpan(mDiscountColor), 0, start,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(mDiscountBlackColor), start, priceStr.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return ss;
    }

    private boolean checkLimitPeople(String enrBefore, boolean withTips) {
        if (enrBefore.length() > 0 && (Integer.parseInt(enrBefore) > mLimit || Integer.parseInt(enrBefore) <= 0 )) {
            if ((Integer.parseInt(enrBefore) > mLimit)) {
                if (withTips) {
                    String tips = getString(R.string.promotion_people_exceed, mLimit);
                    Toast.makeText(SalePromotionActivity.this, tips, Toast.LENGTH_SHORT).show();
                }
            } else if (Integer.parseInt(enrBefore) <= 0 ) {
                if (withTips) {
                    String tips = getString(R.string.promotion_people_error, mLimit);
                    Toast.makeText(SalePromotionActivity.this, tips, Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        }

        return true;
    }

    private boolean checkLessonDay(String lessonBefore, boolean withTips) {
        long spaceDays = getSpaceDays();
        if (lessonBefore.length() > 0 && Integer.parseInt(lessonBefore.toString()) > spaceDays) {
            if (withTips) {
                String tips = getString(R.string.promotion_day_exceed, spaceDays);
                Toast.makeText(SalePromotionActivity.this, tips, Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        return true;
    }

    private boolean checkDiscountLegal(String discount, boolean withTips) {
        if (!TextUtils.isEmpty(discount)) {
            if (discount.length() > 1 && discount.charAt(1) != '.') {
                if (withTips) {
                    Toast.makeText(SalePromotionActivity.this, R.string.promotion_discount_format_error,
                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        }

        return true;
    }

    private boolean checkSubmitInfo() {
        String enrBefore = mEnrollBeforeEdt.getText().toString().trim();
        String enrDiscount = mEnrollDiscountEdt.getText().toString().trim();

        String lessBefore = mLessonBeforeEdt.getText().toString().trim();
        String lessDiscount = mLessonDiscountEdt.getText().toString().trim();

        Promotion promotion1 = null;
        Promotion promotion2 = null;
        ArrayList<Promotion> temp = new ArrayList<Promotion>(2);
        if ((TextUtils.isEmpty(enrBefore) && TextUtils.isEmpty(enrDiscount))
                || (!TextUtils.isEmpty(enrBefore) && !TextUtils.isEmpty(enrDiscount))) {
            boolean flag = checkLimitPeople(enrBefore, true) && checkDiscountLegal(enrDiscount, true);
            if (!TextUtils.isEmpty(enrBefore) && !TextUtils.isEmpty(enrDiscount)) {
                if (flag) {
                    promotion1 = new Promotion();
                    promotion1.setQuota(Integer.parseInt(enrBefore));
                    promotion1.setDiscount(getDiscount(enrDiscount));
                    promotion1.setType(PROMOTION_ENROLL_BEFORE);
                    temp.add(promotion1);
                } else {
                    return false;
                }
            }

        } else {
            //promotion 1
            Toast.makeText(this, getString(R.string.promotion_info_error, 1), Toast.LENGTH_SHORT).show();
            return false;
        }

        if ((TextUtils.isEmpty(lessBefore) && TextUtils.isEmpty(lessDiscount))
                || (!TextUtils.isEmpty(lessBefore) && !TextUtils.isEmpty(lessDiscount))) {
            boolean flag = checkLessonDay(lessBefore, true) && checkDiscountLegal(lessDiscount, true);
            if (!TextUtils.isEmpty(lessBefore) && !TextUtils.isEmpty(lessDiscount)) {
                if (flag) {
                    promotion2 = new Promotion();
                    promotion2.setBefore(Integer.parseInt(lessBefore));
                    promotion2.setDiscount(getDiscount(lessDiscount));
                    promotion2.setType(PROMOTION_LESSON_BEFORE);
                    temp.add(promotion2);
                } else {
                    return false;
                }
            }
        } else {
            //promotion 2
            Toast.makeText(this, getString(R.string.promotion_info_error, 2), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!temp.isEmpty()) {
            mPromotions = new Promotion[temp.size()];
            for (int i = 0; i < mPromotions.length; i++) {
                mPromotions[i] = temp.get(i);
            }
        } else {
            mPromotions = null;
        }

        return true;
    }


    private boolean checkPromotionOne() {
        String enrBefore = mEnrollBeforeEdt.getText().toString().trim();
        String enrDiscount = mEnrollDiscountEdt.getText().toString().trim();
        if ((TextUtils.isEmpty(enrBefore) && TextUtils.isEmpty(enrDiscount))
                || (!TextUtils.isEmpty(enrBefore) && !TextUtils.isEmpty(enrDiscount))) {
            if (!TextUtils.isEmpty(enrBefore) && !TextUtils.isEmpty(enrDiscount)) {
                return checkLimitPeople(enrBefore, false) && checkDiscountLegal(enrDiscount, false);
            }
        } else {
            return false;
        }

        return false;
    }

    private boolean checkPromotionTwo() {
        String lessBefore = mLessonBeforeEdt.getText().toString().trim();
        String lessDiscount = mLessonDiscountEdt.getText().toString().trim();

        if ((TextUtils.isEmpty(lessBefore) && TextUtils.isEmpty(lessDiscount))
                || (!TextUtils.isEmpty(lessBefore) && !TextUtils.isEmpty(lessDiscount))) {
            if (!TextUtils.isEmpty(lessBefore) && !TextUtils.isEmpty(lessDiscount)) {
               return checkLessonDay(lessBefore, false) && checkDiscountLegal(lessDiscount, false);
            }
        } else {
            return false;
        }

        return false;
    }
}
