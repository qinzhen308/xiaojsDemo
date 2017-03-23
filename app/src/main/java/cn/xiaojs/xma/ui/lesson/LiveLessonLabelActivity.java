package cn.xiaojs.xma.ui.lesson;

import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.TextWriting;

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
    private final static int MAX_LABEL_CHAR = 5;

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
                setLabel();

                Intent i = new Intent();
                i.putExtra(CourseConstant.KEY_LESSON_OPTIONAL_INFO, mLesson);
                setResult(RESULT_OK, i);
                finish();
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
                    mTags.add(0, item);
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
        final EditText et = (EditText)view.findViewById(R.id.label_name);
        et.setText(tagItem.label);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LABEL_CHAR)});
        et.setHint(getString(R.string.add_label_hint, MAX_LABEL_CHAR));
        tagItem.labelEdt = et;

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!TextUtils.isEmpty(text)) {
                    String content = text.trim();
                    if (TextUtils.isEmpty(content)) {
                        et.setText("");
                    }
                }
            }
        });

        View del = view.findViewById(R.id.del_label);
        del.setTag(tagItem);
        del.setOnClickListener(this);

        return view;
    }

    private void setLabel() {
        List<String> tags = new ArrayList<String>();
        int count = mAddLabelLayout.getChildCount();
        if (count > 1) {
            int size = mAddLabelController.isShown() ? count - 1 : MAX_LABEL_COUNT;
            for (int i = 0; i < size; i++) {
                View v = mAddLabelLayout.getChildAt(i);
                TextView lbTv = (TextView)v.findViewById(R.id.label_name);
                String lb = lbTv.getText().toString();
                if (!TextUtils.isEmpty(lb)) {
                    tags.add(lb);
                }
            }
        }

        if (tags == null || tags.size() == 0) {
            mLesson.setTags(null);
        } else {
            String[] tagArr = new String[tags.size()];
            tags.toArray(tagArr);
            mLesson.setTags(tagArr);
        }
    }


    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof TagItem) {
            mAddLabelLayout.removeView(((TagItem) tag).view);
            mTags.remove((TagItem) tag);
            if (mTags.size() < MAX_LABEL_COUNT) {
                mAddLabelController.setVisibility(View.VISIBLE);
            }
        }
    }

    private class TagItem {
        public View view;
        public EditText labelEdt;
        public String label;
    }
}
