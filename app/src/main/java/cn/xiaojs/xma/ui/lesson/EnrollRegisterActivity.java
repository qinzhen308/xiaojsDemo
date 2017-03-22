package cn.xiaojs.xma.ui.lesson;

import android.content.Intent;
import android.text.TextUtils;
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
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SearchManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ELResponse;
import cn.xiaojs.xma.model.OfflineRegistrant;
import cn.xiaojs.xma.model.Registrant;
import cn.xiaojs.xma.model.search.AccountSearch;
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
            int duration = intent.getIntExtra(KEY_DURATION, 0);
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
            final long phoneNum = Long.parseLong(phone);
            showProgress(false);
            SearchManager.searchAccounts(this, phone, new APIServiceCallback<ArrayList<AccountSearch>>() {
                @Override
                public void onSuccess(ArrayList<AccountSearch> object) {
                    boolean hasExist = false;
                    AccountSearch currSearch = null;
                    if (object != null && !object.isEmpty()) {
                        for (AccountSearch search : object) {
                            //FIXME 此处的目的是判断个人账号，排除掉机构账号。但是目前个人账号的type返回的是account
                            if (Account.TypeName.PERSION.equals(search._type) || "account".equals(search._type)) {
                                hasExist = true;
                                currSearch = search;
                                break;
                            }
                        }
                    }

                    if (currSearch == null || TextUtils.isEmpty(currSearch._id)) {
                        hasExist = false;
                    }

                    OfflineRegistrant offlineRegistrant = new OfflineRegistrant();
                    Registrant registrant = new Registrant();
                    offlineRegistrant.setRegistrant(registrant);
                    if (hasExist) {
                        registrant.setAccount(currSearch._id);
                    } else {
                        registrant.setName(name);
                        registrant.setMobile(phoneNum);
                    }

                    offlineRegister(offlineRegistrant);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    Toast.makeText(EnrollRegisterActivity.this, R.string.enroll_register_fail, Toast.LENGTH_SHORT).show();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
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
}
