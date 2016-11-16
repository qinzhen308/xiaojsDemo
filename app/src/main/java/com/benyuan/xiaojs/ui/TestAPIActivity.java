package com.benyuan.xiaojs.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.crop.CropImageMainActivity;
import com.benyuan.xiaojs.common.crop.CropImagePath;
import com.benyuan.xiaojs.common.xf_foundation.schemas.Ctl;
import com.benyuan.xiaojs.common.xf_foundation.schemas.Finance;
import com.benyuan.xiaojs.common.xf_foundation.schemas.Security;
import com.benyuan.xiaojs.data.AccountDataManager;
import com.benyuan.xiaojs.data.CategoriesDataManager;
import com.benyuan.xiaojs.data.LessonDataManager;
import com.benyuan.xiaojs.data.LoginDataManager;
import com.benyuan.xiaojs.data.RegisterDataManager;
import com.benyuan.xiaojs.data.api.AccountRequest;
import com.benyuan.xiaojs.data.api.ApiManager;
import com.benyuan.xiaojs.data.api.QiniuRequest;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.QiniuService;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.model.Account;
import com.benyuan.xiaojs.model.ClaimCompetency;
import com.benyuan.xiaojs.model.CompetencyParams;
import com.benyuan.xiaojs.model.CreateLesson;
import com.benyuan.xiaojs.model.Criteria;
import com.benyuan.xiaojs.model.Duration;
import com.benyuan.xiaojs.model.Enroll;
import com.benyuan.xiaojs.model.Fee;
import com.benyuan.xiaojs.model.GetLessonsResponse;
import com.benyuan.xiaojs.model.GetSubjectResponse;
import com.benyuan.xiaojs.model.LiveLesson;
import com.benyuan.xiaojs.model.LoginInfo;
import com.benyuan.xiaojs.model.LoginParams;
import com.benyuan.xiaojs.model.Pagination;
import com.benyuan.xiaojs.model.RegisterInfo;
import com.benyuan.xiaojs.model.Schedule;
import com.benyuan.xiaojs.model.VerifyCode;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;

public class TestAPIActivity extends Activity {

    private int vcode;
    private long mob = 13812345687l;
    private String subject = "5820a10e101db0af4bcf2fd9";
    private String sessionid = "0519JlXgx3Hl45DPFrKJpqdKdip4fJ4K";
    private String id = "5827e51fa20843720290e2f5";
    private String token = "";///"7ILUcJUmJGSmyeaB3QNWpUlPdT3-E2MXkC1oZhqJ:HBHjxd_oTlAiwaht4niHQFDmh6Q=:eyJyZXR1cm5Cb2R5Ijoie1xuICAgICAgICAgICAgXCJuYW1lXCI6ICQoZm5hbWUpLFxuICAgICAgICAgICAgXCJzaXplXCI6ICQoZnNpemUpLFxuICAgICAgICAgICAgXCJ3XCI6ICQoaW1hZ2VJbmZvLndpZHRoKSxcbiAgICAgICAgICAgIFwiaFwiOiAkKGltYWdlSW5mby5oZWlnaHQpLFxuICAgICAgICAgICAgXCJoYXNoXCI6ICQoZXRhZylcbiAgICAgICAgfSIsInNjb3BlIjoidW5kZWZpbmVkOjU4MjkyMGFmYzcwMDkzM2M0ZDZjMjVkYyIsImRlYWRsaW5lIjoxNDc5MjQ4NDA2fQ==";


    private final static int CROP_PORTRAIT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_api);
    }


    public void btnClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_svc: {
                //testSendCode(this);

                break;
            }
            case R.id.btn_svm: {
                //testValidateCode(this);
                //testRegister(this);
                //testLogin(this);
                testGetUpToken(this);
                break;
            }
            case R.id.btn_q: {
                //testValidateCode(this);
                //testRegister(this);
                //testLogin(this);
                //testLogout(this);
                //testClaimCompetency(this);
                //testCreateLession(this);
                //testGetLessons(this);
                //testPutLessonOnShelves(this);
                //testJsonFormat();
                //testGetSubject(this);
                //testGetUpToken(this);
                //editProfile(this);
                //getProfile(this);
                testCoverUpLoad(this);

                break;
            }

        }
    }

    private void testCoverUpLoad(Context context ) {

//        AccountRequest accountRequest = new AccountRequest();
//        accountRequest.getCoverUpToken(context, sessionid,new APIServiceCallback<String>() {
//            @Override
//            public void onSuccess(String object) {
//                token = object;
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });



    }


    private void editPortrait() {
        Intent i = new Intent(this, CropImageMainActivity.class);
        startActivityForResult(i, CROP_PORTRAIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                if (data != null) {
                    String cropImgPath = data.getStringExtra(CropImagePath.CROP_IMAGE_PATH_TAG);
                    Bitmap portrait = BitmapFactory.decodeFile(cropImgPath);
                    if (portrait != null) {

                        AccountDataManager.requestUploadAvatar(TestAPIActivity.this, sessionid, id, cropImgPath, new QiniuService() {
                            @Override
                            public void uploadSuccess(String fileName, String fileUrl) {



                            }

                            @Override
                            public void uploadFailure() {



                            }
                        });

                    }
                }
                break;
        }
    }

    private void getProfile(Context context) {

        AccountDataManager.requestProfile(context, sessionid, new APIServiceCallback<Account>() {
            @Override
            public void onSuccess(Account object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }

    private void editProfile(Context context) {
        Account.Basic b = new Account.Basic();
        b.setBirthday(new Date(System.currentTimeMillis()));
        b.setSex(false);
        b.setTitle("天下唯我独尊");

        AccountDataManager.requestEditProfile(context, sessionid, b, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }

    private void testGetUpToken(Context context) {

//        AccountDataManager.requestAvatorUpToken(context, sessionid,new APIServiceCallback<String>() {
//            @Override
//            public void onSuccess(String object) {
//                token = object;
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

    }

    //getSubject demo
    private void testGetSubject(Context context) {

        CategoriesDataManager.requestGetSubject(context, new APIServiceCallback<GetSubjectResponse>() {
            @Override
            public void onSuccess(GetSubjectResponse object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }

    private void testJsonFormat() {
        Duration duration = new Duration();
        duration.setStart(new Date(System.currentTimeMillis()-(3600*1000*24)));
        duration.setEnd(new Date(System.currentTimeMillis()));

        Criteria criteria = new Criteria();
        criteria.setSource(Ctl.LessonSource.ALL);
        criteria.setDuration(duration);

        ServiceRequest serviceRequest = new ServiceRequest();

        String jsonstr = serviceRequest.objectToJsonString(criteria);
        Logger.json(jsonstr);
    }

    private void testPutLessonOnShelves(Context context) {

        String lession = "58211abfc52b32f4568faa58";

        LessonDataManager.requestPutLessonOnShelves(context, sessionid, lession, new APIServiceCallback<GetLessonsResponse>() {
            @Override
            public void onSuccess(GetLessonsResponse object) {
                Logger.d("onSuccess-----------");
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                Logger.d("onFailure-----------");
            }
        });
    }

    //获取已开的课
    private void testGetLessons(Context context) {

        Duration duration = new Duration();
        duration.setStart(new Date(System.currentTimeMillis()-(3600*1000*24)));
        duration.setEnd(new Date(System.currentTimeMillis()));

        Criteria criteria = new Criteria();
        criteria.setSource(Ctl.LessonSource.ALL);
        criteria.setDuration(duration);


        Pagination pagination = new Pagination();
        pagination.setPage(1);
        pagination.setMaxNumOfObjectsPerPage(20);


        LessonDataManager.requestGetLessons(context, sessionid,criteria, pagination, new APIServiceCallback<GetLessonsResponse>() {
            @Override
            public void onSuccess(GetLessonsResponse object) {
                Logger.d("onSuccess-----------");
            }

            @Override
            public void onFailure(String errorCode,String errorMessage) {

                Logger.d("onFailure-----------");
            }
        });
    }



    //创建直播课
    private void testCreateLession(Context context) {

        Enroll enroll = new Enroll();
        enroll.setMax(100);
        enroll.setMandatory(false);

        Fee fee = new Fee();
        fee.setFree(true);
        fee.setType(Finance.PricingType.TOTAL);
        fee.setCharge(BigDecimal.valueOf(100));

        Schedule sch = new Schedule();
        sch.setStart(new Date(System.currentTimeMillis()));
        sch.setDuration(1000);


        LiveLesson ll = new LiveLesson();
        ll.setTitle("无人驾驶课");
        ll.setSubject(subject);
        ll.setEnroll(enroll);
        ll.setMode(Ctl.TeachingMode.ONE_2_ONE);
        ll.setFee(fee);
        ll.setSchedule(sch);

        CreateLesson cl = new CreateLesson();
        cl.setData(ll);


        LessonDataManager.requestCreateLiveLesson(context, sessionid, cl, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                Logger.d("onSuccess-----------");
            }

            @Override
            public void onFailure(String errorCode,String errorMessage) {
                Logger.d("onFailure-----------");
            }
        });


    }

    //声明教学能力
    private void testClaimCompetency(Context context) {


        CompetencyParams cp = new CompetencyParams();
        cp.setSubject(subject);


        AccountDataManager.requestClaimCompetency(context, sessionid, cp, new APIServiceCallback<ClaimCompetency>() {
            @Override
            public void onSuccess(ClaimCompetency object) {
                Logger.d("onSuccess-----------");
            }

            @Override
            public void onFailure(String errorCode,String errorMessage) {

                Logger.d("onFailure-----------");
            }
        });

    }


    //退出
    private void testLogout(Context context) {


        LoginDataManager.requestLogoutByAPI(context, sessionid, new APIServiceCallback() {

            @Override
            public void onSuccess(Object object) {
                Logger.d("onSuccess-----------");
            }

            @Override
            public void onFailure(String errorCode,String errorMessage) {
                Logger.d("onFailure-----------");
            }
        });

    }

    //登陆
    private void testLogin(final Context context) {

        LoginParams params = new LoginParams();
        params.setPassword("123456");
        params.setCt(Security.CredentialType.PERSION);
        params.setMobile(mob);

        LoginDataManager.requestLoginByAPI(context, params, new APIServiceCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo object) {

                Logger.d("onSuccess-----------");

                sessionid = object.getUser().getSessionID();

                Toast.makeText(context, "login Ok:" + sessionid, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(String errorCode,String errorMessage) {

                Logger.d("onFailure-----------");
            }
        });
    }


    //注册
    private void testRegister(final Context context) {

        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setCode(vcode);
        registerInfo.setMobile(mob);
        registerInfo.setPassword("123456");
        registerInfo.setUsername("m_test1");

        RegisterDataManager.requestRegisterByAPI(context, registerInfo, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

                Logger.d("onSuccess-----------");
                Toast.makeText(context, "register Ok", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(String errorCode,String errorMessage) {
                Logger.d("onFailure-----------register");
                Toast.makeText(context, "register error code:" + errorCode, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void testValidateCode(final Context context) {
        RegisterDataManager.requestValidateCode(context, mob, vcode, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

                Toast.makeText(context, "validate Ok", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(String errorCode,String errorMessage) {
                Toast.makeText(context, "validate error code:" + errorCode, Toast.LENGTH_LONG).show();
            }
        });

    }

    //发送验证码
    private void testSendCode(final Context context) {

        RegisterDataManager.requestSendVerifyCode(context, mob, new APIServiceCallback<VerifyCode>() {
            @Override
            public void onSuccess(VerifyCode object) {
                Logger.d("onSuccess-----------");

                vcode = object.getCode();
                Toast.makeText(context, "code:" + vcode, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(String errorCode,String errorMessage) {
                Logger.d("onFailure-----------errorcode:%s", errorCode);
            }
        });

    }

}
