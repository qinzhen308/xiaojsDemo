package cn.xiaojs.xma.ui.lesson;

import android.graphics.Paint;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CLEResponse;
import cn.xiaojs.xma.model.LessonDetail;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.ctl.Fee;
import cn.xiaojs.xma.model.ctl.Price;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.BaseBusiness;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.util.TimeUtil;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/2/20
 * Desc:确认报名
 *
 * ======================================================================================== */

public class ConfirmEnrollmentActivity extends BaseActivity {
    @BindView(R.id.lesson_cover)
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
    @BindView(R.id.promotion_info)
    TextView mPromotionInfoTv;
    @BindView(R.id.pay_price)
    TextView mPayPriceTv;
    @BindView(R.id.discount_price)
    TextView mDiscountPriceTv;

    private LessonDetail mLessonDetail;
    private ListBottomDialog mPayDialog;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_confirm_enrollment);
        mLessonOriMoneyTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        needHeader(false);
        loadData();
    }

    @OnClick({R.id.back_btn, R.id.favourite_btn, R.id.share_wb_btn, R.id.pay_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.favourite_btn:
                break;
            case R.id.share_wb_btn:
                break;
            case R.id.pay_btn:
                pay();
                break;
        }
    }

    private void loadData() {
        mLessonDetail = (LessonDetail)getIntent().getSerializableExtra(CourseConstant.KEY_LESSON_BEAN);
        String lesson = mLessonDetail != null ? mLessonDetail.getId() : null;
        if (TextUtils.isEmpty(lesson)) {
            finish();
            return;
        }
        String registrant = AccountDataManager.getAccountID(this);

        Dimension dimension = new Dimension();
        dimension.width = mLessonCoverImg.getMeasuredWidth();
        dimension.height = mLessonCoverImg.getMeasuredHeight();
        String url = Ctl.getCover(mLessonDetail.getCover(), dimension);
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.default_lesson_cover)
                .error(R.drawable.default_lesson_cover)
                .into(mLessonCoverImg);

        mLessonTitleTv.setText(mLessonDetail.getTitle());

        Price fee = mLessonDetail.getFee();
        if (fee != null) {
            float originCharge = fee.total;
            Price.Discounted discounted = fee.discounted;
            if (discounted != null) {

                String fY = getResources().getString(R.string.has_youhui);

                if (discounted.ratio == 10.0f) {
                    mLessonOriMoneyTv.setVisibility(View.GONE);
                    mDiscountPriceTv.setText(String.format(fY,0));
                } else {
                    String dprice = BaseBusiness.formatPrice(originCharge, true);
                    mLessonOriMoneyTv.setText(dprice);
                    mDiscountPriceTv.setText(String.format(fY,originCharge - discounted.subtotal));
                }
                String p = BaseBusiness.formatPrice(discounted.subtotal, true);
                mLessonMoneyTv.setText(p);
                mPayPriceTv.setText(p);

            } else {
                mLessonOriMoneyTv.setVisibility(View.GONE);
                mLessonMoneyTv.setText(BaseBusiness.formatPrice(originCharge, true));
            }
            setSalePromotion(fee);
        }

        //schedule
        Schedule schedule = mLessonDetail.getSchedule();
        if (schedule != null) {
            mLessonBeginTimeTv.setText(TimeUtil.format(schedule.getStart().getTime(),
                    TimeUtil.TIME_YYYY_MM_DD_HH_MM));
            String m = getString(R.string.minute);
            mLessonDurationTv.setText(String.valueOf(schedule.getDuration()) + m);
        }




        LessonDataManager.requestLessonEnrollment(this, lesson, registrant, new APIServiceCallback<CLEResponse>() {
            @Override
            public void onSuccess(CLEResponse cleResponse) {
                setData(cleResponse);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
            }
        });
    }

    private void setData (CLEResponse cleResponse) {
        if (cleResponse != null) {
            //set cover
            if (!TextUtils.isEmpty(mLessonDetail.getCover())) {
                //mLessonCoverImg.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mLessonCoverImg.getLayoutParams();
                int w = getResources().getDisplayMetrics().widthPixels;
                int h = (int) ((CourseConstant.COURSE_COVER_HEIGHT / (float) CourseConstant.COURSE_COVER_WIDTH) * w);
                params.height = h;
                params.width = w;
                Dimension dimension = new Dimension();
                dimension.width = w;
                dimension.height = h;
                String url = Ctl.getCover(mLessonDetail.getCover(), dimension);
                Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.default_lesson_cover)
                        .error(R.drawable.default_lesson_cover)
                        .into(mLessonCoverImg);
            } else {
                //set gone
                //mLessonCoverImg.setVisibility(View.GONE);
            }

            //set title
            mLessonTitleTv.setText(cleResponse.getTitle());

            //fee
            Price fee = cleResponse.getFee();
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
                        mDiscountPriceTv.setText(BaseBusiness.formatPrice(0, true));
                    } else {
                        String dprice = BaseBusiness.formatPrice(originCharge, true);
                        mLessonOriMoneyTv.setText(dprice);
                        mDiscountPriceTv.setText(dprice);
                    }

                    String p = BaseBusiness.formatPrice(discounted.subtotal, true);
                    mLessonMoneyTv.setText(p);
                    mPayPriceTv.setText(p);
                } else {
                    mLessonOriMoneyTv.setVisibility(View.GONE);
                    mLessonMoneyTv.setText(BaseBusiness.formatPrice(originCharge, true));
                }
                setSalePromotion(fee);
                setPriceInfo(fee);
            }

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

    private void setPriceInfo(Price fee) {
        mPayPriceTv.setText(BaseBusiness.formatPrice(fee.discounted.subtotal, true));
        if (fee.discounted.saved > 0) {
            mDiscountPriceTv.setVisibility(View.VISIBLE);
            mDiscountPriceTv.setText(BaseBusiness.formatPrice(fee.discounted.saved, true));
        } else {
            mDiscountPriceTv.setVisibility(View.GONE);
        }
    }

    private void pay() {
        if (mPayDialog == null) {
            mPayDialog = new ListBottomDialog(this);
            mPayDialog.setItems(new String[] {getString(R.string.pay_ali)});
            mPayDialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {
                        case 0:
                            //ali pay
                            break;
                    }
                }
            });
        }
        mPayDialog.show();
    }

}
