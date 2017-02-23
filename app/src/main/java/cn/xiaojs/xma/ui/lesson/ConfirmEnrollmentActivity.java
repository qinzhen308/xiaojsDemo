package cn.xiaojs.xma.ui.lesson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.pingplusplus.android.Pingpp;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Finance;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.OrderManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CLEResponse;
import cn.xiaojs.xma.model.LessonDetail;
import cn.xiaojs.xma.model.Promotion;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.ctl.Price;
import cn.xiaojs.xma.model.order.Orderp;
import cn.xiaojs.xma.model.order.PaymentOrder;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.BaseBusiness;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.util.TimeUtil;
import okhttp3.ResponseBody;

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

    private String orderNo;
    private Orderp orderp;
    private boolean confirmed = false;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_confirm_enrollment);
        mLessonOriMoneyTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        needHeader(false);

        mLessonDetail = (LessonDetail)getIntent().getSerializableExtra(CourseConstant.KEY_LESSON_BEAN);
        String lesson = mLessonDetail != null ? mLessonDetail.getId() : null;
        if (TextUtils.isEmpty(lesson)) {
            finish();
            return;
        }

        createOrder();

        setData();
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

        String registrant = AccountDataManager.getAccountID(this);
        String lesson = mLessonDetail != null ? mLessonDetail.getId() : null;
        LessonDataManager.requestLessonEnrollment(this, lesson, registrant, new APIServiceCallback<CLEResponse>() {
            @Override
            public void onSuccess(CLEResponse cleResponse) {
                confirmed = true;
                if (cleResponse == null)
                    return;
                updateLessonDetail(cleResponse);
                setData();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                confirmed = true;
                cancelProgress();
            }
        });
    }

    private void updateLessonDetail(CLEResponse cleResponse) {
        String title = cleResponse.getTitle();
        if (!TextUtils.isEmpty(title)) {
            mLessonDetail.setTitle(title);
        }

        Schedule schedule = cleResponse.getSchedule();
        if (schedule != null){
            mLessonDetail.setSchedule(schedule);
        }

        Price fee = cleResponse.getFee();
        if (fee != null) {
            mLessonDetail.setFee(fee);
        }

    }

    private void setData() {

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

    }

    private void setSalePromotion(Price fee) {
        Price.Applied[] appliedArr = fee.discounted != null ? fee.discounted.applied : null;
        if (appliedArr == null || appliedArr.length == 0) {
            if (!confirmed){
                mPromotionInfoTv.setVisibility(View.GONE);
            }

            return;
        }

        Price.Applied applied = appliedArr[0];
        if (applied == null) {
            if (!confirmed) {
                mPromotionInfoTv.setVisibility(View.GONE);
            }
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
                            toPay();
                            break;
                    }
                }
            });
        }
        mPayDialog.show();
    }

    private void toPay() {

        showProgress(false);

        OrderManager.toPay(ConfirmEnrollmentActivity.this,
                orderNo, Finance.PayChannel.ALIPAY, orderp, new APIServiceCallback<String>() {

            @Override
            public void onSuccess(String data) {

                cancelProgress();

                if (data == null) {
                    Toast.makeText(ConfirmEnrollmentActivity.this, R.string.pay_failed,Toast.LENGTH_SHORT).show();
                    return;
                }

                Pingpp.createPayment(ConfirmEnrollmentActivity.this, data,XiaojsConfig.PING_WALLET);

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {


                cancelProgress();

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the pay has failed, code:%s, msg:%s", errorCode, errorMessage);
                }

                Toast.makeText(ConfirmEnrollmentActivity.this, R.string.pay_failed,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createOrder() {

        float pri = mLessonDetail.getFee().discounted.subtotal;

        Orderp.BuyItem buyItem = new Orderp.BuyItem();
        buyItem.id = mLessonDetail.getId();
        buyItem.type = Finance.ProductType.LESSON;
        buyItem.total = pri;

        Promotion[] promotions = mLessonDetail.getPromotion();
        if (promotions != null && promotions.length>0) {
            buyItem.promotion = promotions[0].getId();
        }

        orderp = new Orderp();
        orderp.items = new Orderp.BuyItem[1];
        orderp.items[0] = buyItem;
        orderp.amount = pri;

        OrderManager.createOrder(this, orderp, new APIServiceCallback<PaymentOrder>() {
            @Override
            public void onSuccess(PaymentOrder paymentOrder) {

                if (paymentOrder == null) return;

                orderNo = paymentOrder.id;
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("createOrder failed");
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                if (result.equals("success")){
                    result = "支付成功";
                } else if (result.equals("cancel")){
                    result = "支付取消";
                }else{
                    result = "支付失败";
                }
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                showMsg(result, errorMsg, extraMsg);
            }
        }
    }

    public void showMsg(String title, String msg1, String msg2) {
        String str = title;
        if (null !=msg1 && msg1.length() != 0) {
            str += "\n" + msg1;
        }
        if (null !=msg2 && msg2.length() != 0) {
            str += "\n" + msg2;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(str);
        builder.setTitle("提示");
        builder.setPositiveButton("确定", null);
        builder.create().show();
    }
}
