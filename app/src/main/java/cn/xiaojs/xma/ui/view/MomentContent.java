package cn.xiaojs.xma.ui.view;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/12/11
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.util.DeviceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MomentContent extends RelativeLayout {

    @BindView(R.id.moment_content_normal)
    LinearLayout mNormal;
    @BindView(R.id.moment_content_question)
    LinearLayout mSpecial;

    @BindView(R.id.moment_content_normal_txt)
    TextView mNormalContent;
    @BindView(R.id.moment_content_normal_image)
    ImageView mNormalImage;

    @BindView(R.id.moment_content_question_title)
    TextView mSpecialContent;
    @BindView(R.id.moment_content_question_answer)
    TextView mAnswer;
    @BindView(R.id.moment_content_question_image)
    ImageView mSpecialImage;

    @BindView(R.id.moment_content_lesson_wrapper)
    RelativeLayout mLessonWrapper;
    @BindView(R.id.moment_content_lesson_type)
    TextView mLessonType;
    @BindView(R.id.moment_content_lesson_name)
    TextView mLessonName;
    @BindView(R.id.moment_content_lesson_time)
    TextView mLessonTime;
    @BindView(R.id.moment_content_lesson_charge)
    TextView mLessonCharge;
    @BindView(R.id.moment_content_lesson_duration)
    TextView mLessonDuration;
    @BindView(R.id.moment_content_lesson_image)
    ImageView mLessonImage;

    @BindView(R.id.moment_content_lesson_student_num)
    TextView mLessonStuNum;
    @BindView(R.id.images)
    OverlayImages mOverlayImage;
    @BindView(R.id.moment_content_lesson_student_enter)
    TextView mLessonEnter;


    public static final int TYPE_NORMAL = 1;//普通动态

    public static final int TYPE_QUESTION = 2;//只有问题的动态

    public static final int TYPE_QUESTION_ANSWER = 3;//有问题也有答案的动态

    public static final int TYPE_LESSON_WARE = 4;//课件动态

    public static final int TYPE_LESSON = 5;//课程动态

    private static final float LESSON_IMAGE_SCALE = 9.0f / 16;

    public MomentContent(Context context) {
        super(context);
        init();
    }

    public MomentContent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MomentContent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_moment_content, this);
        ButterKnife.bind(this);
        setLessonHeight();
    }

    public void setShowType(int type) {
        switch (type) {
            case TYPE_NORMAL:
                showNormal();
                break;
            case TYPE_QUESTION:
                showQuestion();
                break;
            case TYPE_QUESTION_ANSWER:
                showQuestionAnswer();
                break;
            case TYPE_LESSON_WARE:
                showLessonWare();
                break;
            case TYPE_LESSON:
                showLesson();
                break;
        }

    }

    public void show(){
        mOverlayImage.show();
    }

    private void showNormal() {
        mSpecial.setVisibility(GONE);
        mNormal.setVisibility(VISIBLE);
        mNormalContent.setVisibility(VISIBLE);
        mNormalImage.setVisibility(VISIBLE);
    }

    private void showQuestion() {
        mSpecial.setVisibility(VISIBLE);
        mNormal.setVisibility(GONE);
        mSpecialContent.setVisibility(VISIBLE);
        mAnswer.setVisibility(GONE);
        mSpecialImage.setVisibility(GONE);
        //显示回答的人的列表
        //隐藏课的图片
        mLessonWrapper.setVisibility(GONE);
    }

    private void showQuestionAnswer() {
        mSpecial.setVisibility(VISIBLE);
        mNormal.setVisibility(GONE);
        mSpecialContent.setVisibility(VISIBLE);
        mAnswer.setVisibility(VISIBLE);
        mSpecialImage.setVisibility(GONE);
        //显示回答的人的列表
        //隐藏课的图片
        mLessonWrapper.setVisibility(GONE);
    }

    private void showLessonWare() {
        mSpecial.setVisibility(VISIBLE);
        mNormal.setVisibility(GONE);
        mSpecialContent.setVisibility(VISIBLE);
        mAnswer.setVisibility(GONE);
        mSpecialImage.setVisibility(VISIBLE);
        //隐藏回答的人的列表
        //隐藏课的图片
        mLessonWrapper.setVisibility(GONE);
    }

    private void showLesson() {
        mSpecial.setVisibility(VISIBLE);
        mNormal.setVisibility(GONE);
        mSpecialContent.setVisibility(VISIBLE);
        mAnswer.setVisibility(GONE);
        mSpecialImage.setVisibility(GONE);
        //显示回答的人的列表
        //显示课的图片
        mLessonWrapper.setVisibility(VISIBLE);
        setLessonHeight();
    }

    private void setLessonHeight(){
        int width = DeviceUtil.getScreenWidth(getContext()) - getContext().getResources().getDimensionPixelSize(R.dimen.px30) * 2;
        int height = (int) (width * LESSON_IMAGE_SCALE);
        ViewGroup.LayoutParams lp = mLessonWrapper.getLayoutParams();
        lp.height = height;
        mLessonWrapper.setLayoutParams(lp);
    }
}
