package com.benyuan.xiaojs.ui.course;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.crop.CropImageMainActivity;
import com.benyuan.xiaojs.common.crop.CropImagePath;
import com.benyuan.xiaojs.model.LiveLesson;
import com.benyuan.xiaojs.ui.base.BaseActivity;

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
 * Date:2016/11/7
 * Desc:
 *
 * ======================================================================================== */

public class LessonCreationOptionalInfoActivity extends BaseActivity implements CourseConstant {
    private final static int LESSON_BRIEF = 0;
    private final static int TEACHER_INTRODUCTION = 1;
    private final static int SIT_IN_ON = 2;
    private final static int SALE_PROMOTION = 3;
    private final static int ADD_COVER = 4;

    private final static int DEFAULT_SHOW_CHAR_LEN = 8;

    private final float ZERO = 0.00001f;

    @BindView(R.id.cover_add_layout)
    LinearLayout mCoverAddLayout;
    @BindView(R.id.cover_view)
    ImageView mCoverImgView;
    @BindView(R.id.live_lesson_brief)
    TextView mLessonBriefTv;
    @BindView(R.id.teacher_introduction)
    TextView mTeachIntroTv;
    @BindView(R.id.sit_in_on)
    TextView mSitInOnv;
    @BindView(R.id.sale_promotion)
    TextView mSalePromotionTv;

    private LiveLesson mLesson;
    private float mPrice;
    private int mPricingType;
    private long mLessonStartTime;
    private int mLimit;
    private boolean isFree = true;

    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.optional_info);

        addView(R.layout.activity_lesson_create_optional_info);

        initCoverLayout();
        initData();
    }

    private void initData() {
        Object object = getIntent().getSerializableExtra(KEY_LESSON_OPTIONAL_INFO);
        if (object instanceof LiveLesson) {
            mLesson = (LiveLesson) object;
            try {
                mPrice = mLesson.getFee().getCharge().floatValue();
                mPricingType = mLesson.getFee().getType();
                isFree = mLesson.getFee().isFree();
                mLessonStartTime = mLesson.getSchedule().getStart().getTime();
                mLimit = mLesson.getEnroll().getMax();
                if (!isFree) {
                    findViewById(R.id.sale_promotion_divide_line).setVisibility(View.GONE);
                    findViewById(R.id.sale_promotion_layout).setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkPromotionEnable() {
        if (mLimit <= 0) {
            Toast.makeText(this, R.string.enroll_people_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPrice <= ZERO) {
            Toast.makeText(this, R.string.charge_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (mLessonStartTime <= 0) {
            Toast.makeText(this, R.string.lesson_start_time_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void initCoverLayout() {
        mCoverImgView.setVisibility(View.GONE);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mCoverAddLayout.getLayoutParams();
        FrameLayout.LayoutParams imgParams = (FrameLayout.LayoutParams) mCoverImgView.getLayoutParams();
        int w = getResources().getDisplayMetrics().widthPixels;
        int h = (int) ((COURSE_COVER_HEIGHT / (float) COURSE_COVER_WIDTH) * w);
        params.height = h;
        params.width = w;
        imgParams.width = w;
        imgParams.height = h;
    }

    @OnClick({R.id.left_image, R.id.add_cover, R.id.live_lesson_brief, R.id.teacher_introduction,
            R.id.sit_in_on, R.id.sale_promotion, R.id.cover_view})
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {
            case R.id.left_image:
                handleBackPressed();
                finish();
                break;
            case R.id.add_cover:
                i = new Intent(this, CropImageMainActivity.class);
                i.putExtra(CropImagePath.CROP_IMAGE_WIDTH, COURSE_COVER_WIDTH);
                i.putExtra(CropImagePath.CROP_IMAGE_HEIGHT, COURSE_COVER_HEIGHT);
                startActivityForResult(i, ADD_COVER);
                break;
            case R.id.cover_view:
                i = new Intent(this, CropImageMainActivity.class);
                i.putExtra(CropImageMainActivity.NEED_DELETE, true);
                i.putExtra(CropImagePath.CROP_IMAGE_WIDTH, COURSE_COVER_WIDTH);
                i.putExtra(CropImagePath.CROP_IMAGE_HEIGHT, COURSE_COVER_HEIGHT);
                startActivityForResult(i, ADD_COVER);
                break;
            case R.id.live_lesson_brief:
                i = new Intent(this, LiveLessonBriefActivity.class);
                i.putExtra(KEY_LESSON_OPTIONAL_INFO, mLesson);
                startActivityForResult(i, LESSON_BRIEF);
                break;
            case R.id.teacher_introduction:
                i = new Intent(this, TeacherIntroductionActivity.class);
                i.putExtra(KEY_LESSON_OPTIONAL_INFO, mLesson);
                startActivityForResult(i, TEACHER_INTRODUCTION);
                break;
            case R.id.sit_in_on:
                i = new Intent(this, SitInOnActivity.class);
                i.putExtra(KEY_LESSON_OPTIONAL_INFO, mLesson);
                startActivityForResult(i, SIT_IN_ON);
                break;
            case R.id.sale_promotion:
                if (checkPromotionEnable()) {
                    i = new Intent(this, SalePromotionActivity.class);
                    i.putExtra(KEY_LESSON_OPTIONAL_INFO, mLesson);
                    startActivityForResult(i, SALE_PROMOTION);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_COVER:
                //TODO
                if (resultCode == CropImageMainActivity.RESULT_DELETE) {
                    mCoverImgView.setImageBitmap(null);
                    mCoverImgView.setVisibility(View.GONE);
                } else {
                    //upload cover to server and set lesson cover url
                    //mLesson.setCover(coverUrl);
                    if (data != null) {
                        String cropImgPath = data.getStringExtra(CropImagePath.CROP_IMAGE_PATH_TAG);
                        Bitmap portrait = BitmapFactory.decodeFile(cropImgPath);
                        if (portrait != null) {
                            mCoverImgView.setVisibility(View.VISIBLE);
                            mCoverImgView.setImageBitmap(portrait);
                        }
                    } else {
                        mCoverImgView.setVisibility(View.GONE);
                    }
                }
                break;
            case LESSON_BRIEF:
                mLesson = getLesson(data);
                if (mLesson != null) {
                    String txt = formatResult(mLesson.getOverview().getText());
                    if (!TextUtils.isEmpty(txt) && mLesson.getOverview() != null) {
                        mLessonBriefTv.setText(txt);
                    }
                }
                break;
            case TEACHER_INTRODUCTION:
                mLesson = getLesson(data);
                if (mLesson != null) {
                    String txt = formatResult(mLesson.getTeachersIntro().getText());
                    if (!TextUtils.isEmpty(txt) && mLesson.getTeachersIntro() != null) {
                        mTeachIntroTv.setText(txt);
                    }
                }
                break;
            case SIT_IN_ON:
                mLesson = getLesson(data);
                if (mLesson != null && mLesson.getAudit() != null) {
                    String[] sitInOnPersons = mLesson.getAudit().getGrantedTo();
                    if (sitInOnPersons != null) {
                        int i = 0;
                        StringBuilder sb = new StringBuilder();

                        for (String per : sitInOnPersons) {
                            if (++i > 2) {
                                break;
                            }
                            sb.append(per + ",");
                        }
                        String txt = sb.toString();
                        if (!TextUtils.isEmpty(txt)) {
                            txt = txt.substring(0, txt.length() - 1);
                        }
                        if (!TextUtils.isEmpty(txt)) {
                            mSitInOnv.setText(txt);
                        }
                    }
                }
                break;
            case SALE_PROMOTION:
                mLesson = getLesson(data);
                if (mLesson != null && mLesson.getPromotion() != null) {
                    mSalePromotionTv.setText(getString(R.string.edit));
                }
                break;
        }
    }

    private LiveLesson getLesson(Intent data) {
        if (data != null) {
            Object object = data.getSerializableExtra(KEY_LESSON_OPTIONAL_INFO);
            if (object instanceof LiveLesson) {
                String coverUrl = mLesson.getCover();
                mLesson = (LiveLesson) object;
                mLesson.setCover(coverUrl);
                return mLesson;
            }
        }

        return null;
    }

    private String formatResult(String s) {
        if (!TextUtils.isEmpty(s)) {
            if (s.length() > DEFAULT_SHOW_CHAR_LEN) {
                return s.substring(0, DEFAULT_SHOW_CHAR_LEN) + "...";
            }
        }

        return s;
    }

    private void handleBackPressed() {
        Intent i = new Intent();
        i.putExtra(KEY_LESSON_OPTIONAL_INFO, mLesson);
        setResult(RESULT_OK, i);
    }

    @Override
    public void onBackPressed() {
        handleBackPressed();
        super.onBackPressed();
    }
}
