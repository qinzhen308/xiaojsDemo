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
 * Date:2016/12/21
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.model.social.DynUpdate;
import cn.xiaojs.xma.util.StringUtil;

public class MomentUpdateTargetView extends FrameLayout {

    @BindView(R.id.moment_update_target_word_wrapper)
    LinearLayout mWordWrapper;
    @BindView(R.id.moment_update_target_image)
    ImageView mImage;
    @BindView(R.id.moment_update_target_lesson_wrapper)
    LinearLayout mLessonWrapper;
    @BindView(R.id.moment_update_target_word)
    TextView mTargetWord;
    @BindView(R.id.moment_update_target_lesson_image)
    ImageView mLessonImage;
    @BindView(R.id.moment_update_target_lesson_name)
    TextView mLessonWord;

    public static final int TYPE_WORD = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_LESSON = 3;

    public MomentUpdateTargetView(Context context) {
        super(context);
        init();
    }

    public MomentUpdateTargetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MomentUpdateTargetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public MomentUpdateTargetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), cn.xiaojs.xma.R.layout.layout_moment_update_target, this);
        ButterKnife.bind(this);
    }

    public void show(int type) {
        if (type == TYPE_WORD) {
            mWordWrapper.setVisibility(VISIBLE);
            mImage.setVisibility(GONE);
            mLessonWrapper.setVisibility(GONE);
        } else if (type == TYPE_IMAGE) {
            mWordWrapper.setVisibility(GONE);
            mImage.setVisibility(VISIBLE);
            mLessonWrapper.setVisibility(GONE);
        } else if (type == TYPE_LESSON){
            mWordWrapper.setVisibility(GONE);
            mImage.setVisibility(GONE);
            mLessonWrapper.setVisibility(VISIBLE);
        }else {
            mWordWrapper.setVisibility(GONE);
            mImage.setVisibility(GONE);
            mLessonWrapper.setVisibility(GONE);
        }
    }

    public void show(DynUpdate bean) {
        if (bean == null || bean.body == null){
            show(-1);
            return;
        }
        if (bean.body.ref != null) {

            //引用其他人的动态
            if (bean.body.ref.account != null && !TextUtils.isEmpty(bean.body.ref.account.name)){
                show(TYPE_WORD);
                StringBuilder sb = new StringBuilder();
                sb.append(bean.body.ref.account.name);
                sb.append(':');
                sb.append(bean.body.ref.text);

                Spannable span = StringUtil.getSpecialString(sb.toString(),bean.body.ref.account.name,getResources().getColor(R.color.font_blue));
                mTargetWord.setText(span);
            }else {
                if (TextUtils.isEmpty(bean.body.ref.snap)) {
                    show(TYPE_WORD);
                    mTargetWord.setText(bean.body.ref.text);
                } else {
                    if (TextUtils.isEmpty(bean.body.ref.text)){//只有图
                        show(TYPE_IMAGE);
                        Glide.with(getContext())
                                .load(Social.getDrawing(bean.body.ref.snap,true))
                                .placeholder(R.drawable.default_lesson_cover)
                                .error(R.drawable.default_lesson_cover)
                                .into(mImage);
                    }else {//有图有文字
                        show(TYPE_LESSON);
                        Glide.with(getContext())
                                .load(Social.getDrawing(bean.body.ref.snap,true))
                                .placeholder(R.drawable.default_lesson_cover)
                                .error(R.drawable.default_lesson_cover)
                                .into(mLessonImage);
                        mLessonWord.setText(bean.body.ref.text);
                    }

                }
            }

        }
    }
}
