package cn.xiaojs.xma.ui.classroom.document;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.pulltorefresh.stickylistheaders.AdapterWrapper;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.classroom.main.ClassroomController;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomType;

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
 * Date:2017/4/11
 * Desc:
 *
 * ======================================================================================== */

public class DocumentFragment extends BaseFragment implements AdapterView.OnItemClickListener {
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
    @BindView(R.id.video)
    TextView mVideoTv;
    @BindView(R.id.search)
    EditText mSearchEdt;
    @BindView(R.id.doc_list)
    PullToRefreshSwipeListView mDocListView;


    private DocumentAdapter mAdapter;

    private String mLessonId;
    private String mMyAccountId;

    private ClassroomType mClassroomType;

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.activity_classroom_document, null);
    }

    @Override
    protected void init() {
        initData();
    }

    @OnClick({R.id.back, R.id.my_document, R.id.class_document, R.id.upload, R.id.new_folder,
            R.id.all, R.id.ppt, R.id.word, R.id.pdf, R.id.image, R.id.hand_writing, R.id.video})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                exit();
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
            case R.id.video:
                switchDocumentType(v);
                break;
            default:
                break;
        }
    }

    private void exit() {
        getFragmentManager().popBackStackImmediate();
    }

    private void initData() {

        mClassroomType = ClassroomEngine.getEngine().getClassroomType();

        Bundle data = null;
        if ((data = getArguments()) != null) {
            //mLessonId = getArguments().getString(Constants.KEY_LESSON_ID);
            CtlSession session = (CtlSession) data.getSerializable(Constants.KEY_CTL_SESSION);

            if (mClassroomType == ClassroomType.ClassLesson) {
                mLessonId = session != null && session.cls != null ? session.cls.id : "";
            } else {
                mLessonId = session != null && session.ctl != null ? session.ctl.id : "";
            }

        }
        mMyAccountId = AccountDataManager.getAccountID(mContext);
        mAdapter = new DocumentAdapter(mContext, mDocListView, mLessonId, mMyAccountId);
        mDocListView.setAdapter(mAdapter);
        mDocListView.setOnItemClickListener(this);
        mAllTv.setSelected(true);
    }

    private void switchMyDocument() {
        mMyDocumentTv.setBackgroundResource(R.drawable.cr_my_doc_bg);
        mClassDocumentTv.setBackgroundResource(R.drawable.cr_class_doc_stroke_bg);
        mMyDocumentTv.setTextColor(getResources().getColor(R.color.font_white));
        mClassDocumentTv.setTextColor(getResources().getColor(R.color.font_blue));
        mAdapter.setType(Collaboration.SubType.PERSON);
        switchDocumentType(mAllTv);
    }

    private void switchClassDocument() {
        mMyDocumentTv.setBackgroundResource(R.drawable.cr_my_doc_stroke_bg);
        mClassDocumentTv.setBackgroundResource(R.drawable.cr_class_doc_bg);
        mMyDocumentTv.setTextColor(getResources().getColor(R.color.font_blue));
        mClassDocumentTv.setTextColor(getResources().getColor(R.color.font_white));
        if (mClassroomType == ClassroomType.ClassLesson) {
            mAdapter.setType(Collaboration.SubType.PRIVATE_CLASS);
        } else {
            mAdapter.setType(Collaboration.SubType.STANDA_LONE_LESSON);
        }
        switchDocumentType(mAllTv);
    }

    /**
     * 切换不同类型的文档
     */
    private void switchDocumentType(View v) {
        mAllTv.setSelected(false);
        mPptTv.setSelected(false);
        mWordTv.setSelected(false);
        mPdfTv.setSelected(false);
        mImageTv.setSelected(false);
        mHandWritingTv.setSelected(false);
        mVideoTv.setSelected(false);

        v.setSelected(true);

        String category = "";

        switch (v.getId()) {
            case R.id.all:
                category = Collaboration.TypeName.ALL;
                break;
            case R.id.ppt:
                category = Collaboration.TypeName.PPT_IN_LIBRARY;
                break;
            case R.id.word:
                category = Collaboration.TypeName.WORD_IN_LIBRARY;
                break;
            case R.id.pdf:
                category = Collaboration.TypeName.PDF_IN_LIBRARY;
                break;
            case R.id.image:
                category = Collaboration.TypeName.PICTURE_IN_LIBRARY;
                break;
            case R.id.hand_writing:
                break;
            case R.id.video:
                category = Collaboration.TypeName.MEDIA_IN_LIBRARY+","+Collaboration.TypeName.RECORDING_IN_LIBRARY;
                break;
            default:
                break;
        }
        mAdapter.setCategory(category);
        mAdapter.refresh();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //如果是图片，打开图片，进入图片进行涂鸦
        //如果是视频，在白板打开视频。
        Object adapter = parent.getAdapter();
        Fragment target = getTargetFragment();
        if (adapter instanceof AdapterWrapper) {
            Object data = ((AdapterWrapper) adapter).getItem(position);
            if (data instanceof LibDoc && target != null) {
                Intent intent = new Intent();
                getFragmentManager().popBackStackImmediate();
                intent.putExtra(Constants.KEY_OPEN_DOC_BEAN, (LibDoc) data);
                target.onActivityResult(ClassroomController.REQUEST_DOC, Activity.RESULT_OK, intent);
            }
        }
    }
}
