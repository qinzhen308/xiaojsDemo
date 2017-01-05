package cn.xiaojs.xma.ui.classroom.document;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.base.BaseActivity;

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
 * Date:2016/12/28
 * Desc:
 *
 * ======================================================================================== */

public class DocumentActivity extends BaseActivity {
    @BindView(R.id.my_document)
    TextView mMyDocumentTv;
    @BindView(R.id.class_document)
    TextView mClassDocumentTv;
    @BindView(R.id.all)
    TextView mAllTv;
    @BindView(R.id.ppt)
    TextView mPptTv;
    @BindView(R.id.word)
    TextView mWordTv;
    @BindView(R.id.pdf)
    TextView mPdfTv;
    @BindView(R.id.image)
    TextView mImageTv;
    @BindView(R.id.hand_writing)
    TextView mHandWritingTv;
    @BindView(R.id.search)
    EditText mSearchEdt;
    @BindView(R.id.doc_list)
    PullToRefreshSwipeListView mDocListView;

    private DocumentAdapter mDocumentAdapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_classroom_document);
        needHeader(false);

        initData();
    }

    @OnClick({R.id.back, R.id.my_document, R.id.class_document, R.id.upload, R.id.new_folder,
            R.id.all, R.id.ppt, R.id.word, R.id.pdf, R.id.image, R.id.hand_writing})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.my_document:
                switchMyDocument();
                break;

            case R.id.class_document:
                switchClassDocument();
                break;

            case R.id.upload:
                break;

            case R.id.new_folder:
                break;

            case R.id.all:
            case R.id.ppt:
            case R.id.word:
            case R.id.pdf:
            case R.id.image:
            case R.id.hand_writing:
                switchDocumentType(v);
                break;
            default:
                break;
        }
    }

    private void initData() {
        mDocumentAdapter = new DocumentAdapter(this, mDocListView, true);
        mDocListView.setAdapter(mDocumentAdapter);

        mAllTv.setSelected(true);
    }

    private void switchMyDocument() {
        mMyDocumentTv.setBackgroundResource(R.drawable.cr_my_doc_bg);
        mClassDocumentTv.setBackgroundResource(R.drawable.cr_class_doc_stroke_bg);
        mMyDocumentTv.setTextColor(getResources().getColor(R.color.font_white));
        mClassDocumentTv.setTextColor(getResources().getColor(R.color.font_blue));
    }

    private void switchClassDocument() {
        mMyDocumentTv.setBackgroundResource(R.drawable.cr_my_doc_stroke_bg);
        mClassDocumentTv.setBackgroundResource(R.drawable.cr_class_doc_bg);
        mMyDocumentTv.setTextColor(getResources().getColor(R.color.font_blue));
        mClassDocumentTv.setTextColor(getResources().getColor(R.color.font_white));
    }

    private void switchDocumentType(View v) {
        mAllTv.setSelected(false);
        mPptTv.setSelected(false);
        mWordTv.setSelected(false);
        mPdfTv.setSelected(false);
        mImageTv.setSelected(false);
        mHandWritingTv.setSelected(false);

        v.setSelected(true);
        switch (v.getId()) {
            case R.id.all:
                break;
            case R.id.ppt:
                break;
            case R.id.word:
                break;
            case R.id.pdf:
                break;
            case R.id.image:
                break;
            case R.id.hand_writing:
                break;
            default:
                break;
        }
    }
}
