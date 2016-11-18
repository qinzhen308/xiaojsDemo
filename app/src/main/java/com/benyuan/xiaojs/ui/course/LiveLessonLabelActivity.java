package com.benyuan.xiaojs.ui.course;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.model.LiveLesson;
import com.benyuan.xiaojs.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

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
 * Author:huangyong
 * Date:2016/11/18
 * Desc:
 *
 * ======================================================================================== */

public class LiveLessonLabelActivity extends BaseActivity implements View.OnClickListener{
    private final static int MAX_LABEL_COUNT = 5;

    @BindView(R.id.add_label_layout)
    LinearLayout mAddLabelLayout;
    @BindView(R.id.add_label_controller)
    LinearLayout mAddLabelController;

    private LiveLesson mLesson;
    private List<TagItem> mTags;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_lesson_label);
        setMiddleTitle(R.string.live_lesson_label);

        initData();
    }

    @OnClick({R.id.left_image, R.id.sub_btn, R.id.add_label_controller})
    public void onButterknifeClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;

            case R.id.add_label_controller:
                addLabel();
                break;
            case R.id.sub_btn:

                break;
            default:
                break;
        }
    }

    private void addLabel() {
        int size = mTags == null ? 0 : mTags.size();
        TagItem item = new TagItem();
        item.view = buildLabelItem(item);
        mTags.add(item);
        if (item.view != null) {
            mAddLabelLayout.addView(item.view, size);
        }

        if (mTags.size() >= MAX_LABEL_COUNT) {
            mAddLabelController.setVisibility(View.GONE);
        }
    }

    private void initData() {
        mTags = new ArrayList<TagItem>();
        Object object = getIntent().getSerializableExtra(CourseConstant.KEY_LESSON_OPTIONAL_INFO);
        if (object instanceof LiveLesson) {
            mLesson = (LiveLesson) object;
            String[] tags = mLesson.getTags();
            if (tags == null || tags.length == 0) {
                //do nothing
            } else {
                for (int i = tags.length - 1; i >= 0; i--) {
                    TagItem item = new TagItem();
                    item.label = tags[i];
                    item.view = buildLabelItem(item);
                    mTags.add(item);
                    if (item.view != null) {
                        mAddLabelLayout.addView(item.view, 0);
                    }
                }

                if (mTags.size() >= MAX_LABEL_COUNT) {
                    mAddLabelController.setVisibility(View.GONE);
                }
            }
        }
    }

    private View buildLabelItem(TagItem tagItem) {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_lesson_label_add, null);
        EditText et = (EditText)view.findViewById(R.id.label_name);
        et.setText(tagItem.label);

        View del = view.findViewById(R.id.del_label);
        del.setTag(tagItem);
        del.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof TagItem) {
            mAddLabelController.removeView(((TagItem) tag).view);
            mTags.remove((TagItem) tag);
            if (mTags.size() < MAX_LABEL_COUNT) {
                mAddLabelController.setVisibility(View.VISIBLE);
            }
        }
    }

    private class TagItem {
        public View view;
        public String label;
    }
}
