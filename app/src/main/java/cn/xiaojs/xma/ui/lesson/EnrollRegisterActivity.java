package cn.xiaojs.xma.ui.lesson;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SearchManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionResult;
import cn.xiaojs.xma.model.ELResponse;
import cn.xiaojs.xma.model.OfflineRegistrant;
import cn.xiaojs.xma.model.Registrant;
import cn.xiaojs.xma.model.search.AccountSearch;
import cn.xiaojs.xma.model.search.SearchResultV2;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.VerifyUtils;

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
 * Date:2017/3/22
 * Desc:
 *
 * ======================================================================================== */

public class EnrollRegisterActivity extends BaseActivity {
    public final static String KEY_LESSON = "key_lesson";
    public final static String KEY_COVER = "key_cover";
    public final static String KEY_TITLE = "key_title";
    public final static String KEY_START_TIME = "key_start_time";
    public final static String KEY_DURATION = "key_duration";

    @BindView(R.id.lesson_cover)
    ImageView mLessonCoverImg;
    @BindView(R.id.title)
    TextView mLessonTitleTv;
    @BindView(R.id.lesson_time_duration)
    TextView mLessonTimeDurationTv;

    @BindView(R.id.fee)
    TextView mFee;
    @BindView(R.id.phone_num)
    EditTextDel mPhoneNumEdt;
    @BindView(R.id.name)
    EditTextDel mNameEdt;
    @BindView(R.id.remark)
    EditTextDel mRemarkEdt;

    private String mLessonId;

    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.registration);

        addView(R.layout.activity_enroll_register);

        //init data
        loadData();
        mPhoneNumEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = mPhoneNumEdt.getText().toString();
                if (phone.length() != 11) {
                    return;
                }
                if (!VerifyUtils.checkPhoneNum(phone)) {
                    return;
                }

                autoSearchUser(phone);
            }
        });
    }

    @OnClick({R.id.left_view, R.id.submit_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_view:
                finish();
                break;
            case R.id.submit_btn:
                submitRegister();
                break;
            default:
                break;
        }
    }

    private void loadData() {
        Intent intent = getIntent();
        if (intent != null) {
            mLessonId = intent.getStringExtra(KEY_LESSON);
            String coverKey = intent.getStringExtra(KEY_COVER);
            String title = intent.getStringExtra(KEY_TITLE);
            long duration = intent.getLongExtra(KEY_DURATION, 0);
            long startTime = intent.getLongExtra(KEY_START_TIME, 0);

            //set cover
            if (!TextUtils.isEmpty(coverKey)) {
                Dimension dimension = new Dimension();
                dimension.width = CourseConstant.COURSE_COVER_WIDTH;
                dimension.height = CourseConstant.COURSE_COVER_HEIGHT;
                Glide.with(this)
                        .load(Ctl.getCover(coverKey, dimension))
                        .error(R.drawable.default_lesson_cover)
                        .placeholder(R.drawable.default_lesson_cover)
                        .into(mLessonCoverImg);
            }

            //set title
            mLessonTitleTv.setText(title);

            //set time and duration
            String minute = getString(R.string.minute);
            String time = TimeUtil.format(startTime, TimeUtil.TIME_YYYY_MM_DD_HH_MM) + "  " + duration + minute;
            mLessonTimeDurationTv.setText(time);
        }
    }

    private boolean checkSubmitInfo() {
        String name = mNameEdt.getEditableText().toString();
        String phone = mPhoneNumEdt.getEditableText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, R.string.phone_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.enroll_register_name_hint, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!VerifyUtils.checkPhoneNum(phone)) {
            Toast.makeText(this, R.string.phone_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void submitRegister() {
        if (!checkSubmitInfo()) {
            return;
        }

        //TODO 当学生账号已经在注册时，需优化
        final String name = mNameEdt.getEditableText().toString();
        final String phone = mPhoneNumEdt.getEditableText().toString();
        final long phoneNum = Long.parseLong(phone);


//        OfflineRegistrant offlineRegistrant = new OfflineRegistrant();
//        Registrant registrant = new Registrant();
//        offlineRegistrant.setRegistrant(registrant);
//
//        registrant.setName(name);
//        registrant.setMobile(Long.valueOf(phone));
//
//
//        offlineRegister(offlineRegistrant);

        //String remark = mRemarkEdt.getEditableText().toString();
        try {

            showProgress(true);
            //因为已经注册的用户，需要上传用户的ID，所以在这里要检测是否注册，如果注册了就获取ID.

            SearchManager.search(this,
                    Social.SearchType.PERSON,
                    phone,
                    1,
                    10,
                    new APIServiceCallback<CollectionResult<SearchResultV2>>() {
                        @Override
                        public void onSuccess(CollectionResult<SearchResultV2> result) {
                            boolean hasExist = false;
                            SearchResultV2 searchResultV2 = null;

                            if (result != null && result.results != null && !result.results.isEmpty()) {
                                searchResultV2 = result.results.get(0);
                                hasExist = true;
                            }

                            if (searchResultV2 == null || TextUtils.isEmpty(searchResultV2.id)) {
                                hasExist = false;
                            }

                            OfflineRegistrant offlineRegistrant = new OfflineRegistrant();
                            Registrant registrant = new Registrant();
                            offlineRegistrant.setRegistrant(registrant);
                            if (hasExist) {
                                registrant.setAccount(searchResultV2.id);
                            } else {
                                registrant.setName(name);
                                registrant.setMobile(phoneNum);
                            }

                            offlineRegister(offlineRegistrant);

                        }

                        @Override
                        public void onFailure(String errorCode, String errorMessage) {
                            //Toast.makeText(EnrollRegisterActivity.this, R.string.enroll_register_fail, Toast.LENGTH_SHORT).show();

                            OfflineRegistrant offlineRegistrant = new OfflineRegistrant();
                            Registrant registrant = new Registrant();
                            registrant.setName(name);
                            registrant.setMobile(phoneNum);
                            offlineRegistrant.setRegistrant(registrant);

                            offlineRegister(offlineRegistrant);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();

            OfflineRegistrant offlineRegistrant = new OfflineRegistrant();
            Registrant registrant = new Registrant();
            registrant.setName(name);
            registrant.setMobile(phoneNum);
            offlineRegistrant.setRegistrant(registrant);

            offlineRegister(offlineRegistrant);
        }
    }

    private void offlineRegister(OfflineRegistrant offlineRegistrant) {

        LessonDataManager.requestEnrollLesson(this, mLessonId, offlineRegistrant, new APIServiceCallback<ELResponse>() {
            @Override
            public void onSuccess(ELResponse object) {
                cancelProgress();
                Toast.makeText(EnrollRegisterActivity.this, R.string.enroll_register_succ, Toast.LENGTH_SHORT).show();
                mNameEdt.setText("");
                mPhoneNumEdt.setText("");
                mRemarkEdt.setText("");
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(EnrollRegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void autoSearchUser(String phone) {

        showProgress(true);
        //因为已经注册的用户，需要上传用户的ID，所以在这里要检测是否注册，如果注册了就获取ID.

        SearchManager.search(this,
                Social.SearchType.PERSON,
                phone,
                1,
                10,
                new APIServiceCallback<CollectionResult<SearchResultV2>>() {
                    @Override
                    public void onSuccess(CollectionResult<SearchResultV2> result) {
                        cancelProgress();
                        boolean hasExist = false;
                        SearchResultV2 searchResultV2 = null;

                        if (result != null && result.results != null && !result.results.isEmpty()) {
                            searchResultV2 = result.results.get(0);
                            hasExist = true;
                        }

                        if (searchResultV2 == null || TextUtils.isEmpty(searchResultV2.id)) {
                            hasExist = false;
                        }
                        if (hasExist) {
                            mNameEdt.setText(searchResultV2.basic!=null?searchResultV2.basic.getName():"");
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        cancelProgress();
                    }
                });


    }
}
