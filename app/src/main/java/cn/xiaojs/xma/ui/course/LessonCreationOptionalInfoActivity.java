package cn.xiaojs.xma.ui.course;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.crop.CropImageMainActivity;
import cn.xiaojs.xma.common.crop.CropImagePath;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

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
    private final static int LESSON_LABEL = 2;
    private final static int SIT_IN_ON = 3;
    private final static int SALE_PROMOTION = 4;
    private final static int ADD_COVER = 5;

    private final static int DEFAULT_SHOW_CHAR_LEN = 8;

    private final float ZERO = 0.00001f;

    @BindView(R.id.cover_add_layout)
    LinearLayout mCoverAddLayout;
    @BindView(R.id.cover_view)
    ImageView mCoverImgView;
    @BindView(R.id.live_lesson_brief)
    TextView mLessonBriefTv;
    @BindView(R.id.live_lesson_label)
    TextView mLessonLabel;
    @BindView(R.id.teacher_introduction)
    TextView mTeachIntroTv;
    @BindView(R.id.sit_in_on)
    TextView mSitInOnv;
    @BindView(R.id.sale_promotion)
    TextView mSalePromotionTv;

    private LiveLesson mLesson;
    private String mLessonId;
    private float mPrice;
    private int mPricingType;
    private long mLessonStartTime;
    private int mLimit;
    private boolean mIsFree = true;
    private boolean mIsMandatory = true;
    private int mBlackFont;
    private int mLightGrayFont;
    private Drawable mErrorDrawable;

    private String mCoverFileName;
    private String mCoverUrl;

    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.optional_info);

        addView(R.layout.activity_lesson_create_optional_info);

        initCoverLayout();
        initData();
    }

    @OnClick({R.id.left_image, R.id.add_cover, R.id.live_lesson_brief, R.id.teacher_introduction,
            R.id.live_lesson_label, R.id.sit_in_on, R.id.sale_promotion, R.id.cover_view})
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
            case R.id.live_lesson_label:
                i = new Intent(this, LiveLessonLabelActivity.class);
                i.putExtra(KEY_LESSON_OPTIONAL_INFO, mLesson);
                startActivityForResult(i, LESSON_LABEL);
                break;
            case R.id.teacher_introduction:
                i = new Intent(this, TeacherIntroductionActivity.class);
                i.putExtra(KEY_LESSON_OPTIONAL_INFO, mLesson);
                startActivityForResult(i, TEACHER_INTRODUCTION);
                break;
            case R.id.sit_in_on:
                i = new Intent(this, AuditActivity.class);
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

    private void initData() {
        mErrorDrawable = new ColorDrawable(Color.WHITE);

        mBlackFont = getResources().getColor(R.color.font_black);
        mLightGrayFont = getResources().getColor(R.color.font_light_gray);

        mLessonId = getIntent().getStringExtra(KEY_LESSON_ID);
        Object object = getIntent().getSerializableExtra(KEY_LESSON_OPTIONAL_INFO);
        if (object instanceof LiveLesson) {
            mLesson = (LiveLesson) object;
            try {
                mPrice = mLesson.getFee().getCharge();
                mPricingType = mLesson.getFee().getType();
                mIsFree = mLesson.getFee().isFree();
                mLessonStartTime = mLesson.getSchedule().getStart().getTime();
                mLimit = mLesson.getEnroll().getMax();
                mIsMandatory = mLesson.getEnroll().isMandatory();
                if (mIsFree || !mIsMandatory) {
                    findViewById(R.id.sale_promotion_divide_line).setVisibility(View.GONE);
                    findViewById(R.id.sale_promotion_layout).setVisibility(View.GONE);
                }

                //set cover
                if (!TextUtils.isEmpty(mLesson.getCover())) {
                    String cover = mLesson.getCover();
                    if (!cover.startsWith("http")) {
                        cover = QiniuService.COVER_BASE_URL + cover;
                    }
                    Glide.with(this).load(cover).into(new GlideDrawableImageViewTarget(mCoverImgView) {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            mCoverImgView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            mCoverImgView.setVisibility(View.GONE);
                        }
                    });
                }

                initLessonBrief();
                initTeacherIntro();
                initLessonLabel();
                initAudit();
                initPromotion();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkPromotionEnable() {
        if (mLimit <= 0) {
            Toast.makeText(this, R.string.lesson_people_empty, Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_COVER:
                if (resultCode == CropImageMainActivity.RESULT_DELETE) {
                    mCoverImgView.setImageBitmap(null);
                    mCoverImgView.setVisibility(View.GONE);
                } else {
                    //upload cover to server and set lesson cover url
                    //mLesson.setCover(coverUrl);
                    if (data != null) {
                        String cropImgPath = data.getStringExtra(CropImagePath.CROP_IMAGE_PATH_TAG);
                        final Context c = LessonCreationOptionalInfoActivity.this;
                        showProgress(true);
                        LessonDataManager.requestUploadCover(c, mLessonId, cropImgPath, new QiniuService() {
                            @Override
                            public void uploadSuccess(String fileName, String fileUrl) {
                                cancelProgress();
                                mCoverFileName = fileName;
                                mCoverImgView.setVisibility(View.VISIBLE);
                                Glide.with(c)
                                        .load(fileUrl)
                                        .error(mErrorDrawable)
                                        .into(mCoverImgView);

                                if (mLesson != null) {
                                    mLesson.setCover(fileName);
                                }
                            }

                            @Override
                            public void uploadFailure() {
                                cancelProgress();
                                Toast.makeText(c, R.string.upload_cover_fail, Toast.LENGTH_SHORT).show();
                                mCoverImgView.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        mCoverImgView.setVisibility(View.GONE);
                    }
                }
                break;
            case LESSON_BRIEF:
                updateLesson(data);
                initLessonBrief();
                break;
            case LESSON_LABEL:
                updateLesson(data);
                initLessonLabel();
                break;
            case TEACHER_INTRODUCTION:
                updateLesson(data);
                initTeacherIntro();
                break;
            case SIT_IN_ON:
                updateLesson(data);
                initAudit();
                break;
            case SALE_PROMOTION:
                updateLesson(data);
                initPromotion();
                break;
        }
    }

    private void initLessonBrief() {
        if (mLesson != null && mLesson.getOverview() != null) {
            String txt = formatResult(mLesson.getOverview().getText());
            if (!TextUtils.isEmpty(txt) && mLesson.getOverview() != null) {
                mLessonBriefTv.setText(txt);
                mLessonBriefTv.setTextColor(mBlackFont);
            } else {
                mLessonBriefTv.setTextColor(mLightGrayFont);
                mLessonBriefTv.setText(R.string.please_input);
            }
        } else {
            mLessonBriefTv.setTextColor(mLightGrayFont);
            mLessonBriefTv.setText(R.string.please_input);
        }
    }

    private void initLessonLabel() {
        if (mLesson != null) {
            String[] labels = mLesson.getTags();
            if (labels != null) {
                StringBuilder sb = new StringBuilder();

                for (String label : labels) {
                    sb.append(label + ",");
                }
                String txt = sb.toString();
                if (!TextUtils.isEmpty(txt)) {
                    txt = txt.substring(0, txt.length() - 1);
                }
                if (!TextUtils.isEmpty(txt)) {
                    mLessonLabel.setText(formatResult(txt));
                    mLessonLabel.setTextColor(mBlackFont);
                }
            } else {
                mLessonLabel.setText(R.string.live_lesson_label_hint);
                mLessonLabel.setTextColor(mLightGrayFont);
            }
        }
    }

    private void initTeacherIntro() {
        if (mLesson != null && mLesson.getTeachersIntro() != null) {
            String txt = formatResult(mLesson.getTeachersIntro().getText());
            if (!TextUtils.isEmpty(txt) && mLesson.getTeachersIntro() != null) {
                mTeachIntroTv.setText(txt);
                mTeachIntroTv.setTextColor(mBlackFont);
            } else {
                mTeachIntroTv.setTextColor(mLightGrayFont);
                mTeachIntroTv.setText(R.string.please_input);
            }
        } else {
            mTeachIntroTv.setTextColor(mLightGrayFont);
            mTeachIntroTv.setText(R.string.please_input);
        }
    }

    private void initAudit() {
        mSitInOnv.setTextColor(mLightGrayFont);
        if (mLesson != null && mLesson.getAudit() != null) {
            String[] sitInOnPersons = mLesson.getAudit().getGrantedTo();
            if (sitInOnPersons != null) {
                StringBuilder sb = new StringBuilder();

                for (String per : sitInOnPersons) {
                    sb.append(per + ",");
                }
                String txt = sb.toString();
                if (!TextUtils.isEmpty(txt)) {
                    txt = txt.substring(0, txt.length() - 1);
                }
                if (!TextUtils.isEmpty(txt)) {
                    mSitInOnv.setText(formatResult(txt));
                    mSitInOnv.setTextColor(mBlackFont);
                } else {
                    mSitInOnv.setText(R.string.please_set);
                    mSitInOnv.setTextColor(mLightGrayFont);
                }
            }
        } else {
            mSitInOnv.setText(R.string.please_set);
            mSitInOnv.setTextColor(mLightGrayFont);
        }
    }

    private void initPromotion() {
        if (mLesson != null && mLesson.getPromotion() != null) {
            mSalePromotionTv.setText(getString(R.string.edit));
            mSalePromotionTv.setTextColor(mBlackFont);
        } else {
            mSalePromotionTv.setTextColor(mLightGrayFont);
        }
    }

    private void updateLesson(Intent data) {
        if (data != null) {
            Object object = data.getSerializableExtra(KEY_LESSON_OPTIONAL_INFO);
            if (object instanceof LiveLesson) {
                String coverUrl = mLesson.getCover();
                mLesson = (LiveLesson) object;
                mLesson.setCover(coverUrl);
            }
        }
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
