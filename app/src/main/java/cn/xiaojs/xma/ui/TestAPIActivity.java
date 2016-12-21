package cn.xiaojs.xma.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.crop.CropImageMainActivity;
import cn.xiaojs.xma.common.crop.CropImagePath;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Finance;
import cn.xiaojs.xma.common.xf_foundation.schemas.Security;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.CategoriesDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.LoginDataManager;
import cn.xiaojs.xma.data.RegisterDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.model.Account;
import cn.xiaojs.xma.model.CLEResponse;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.CenterData;
import cn.xiaojs.xma.model.ClaimCompetency;
import cn.xiaojs.xma.model.CompetencyParams;
import cn.xiaojs.xma.model.CreateLesson;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Duration;
import cn.xiaojs.xma.model.Enroll;
import cn.xiaojs.xma.model.Fee;
import cn.xiaojs.xma.model.GetLessonsResponse;
import cn.xiaojs.xma.model.LessonDetail;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.model.LoginInfo;
import cn.xiaojs.xma.model.LoginParams;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.RegisterInfo;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.VerifyCode;
import cn.xiaojs.xma.ui.message.ContactActivity;
import cn.xiaojs.xma.ui.message.PostDynamicActivity;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.Date;

public class TestAPIActivity extends Activity {

    private int vcode;
    private long mob = 13812345687l;
    private String subject = "5820a10e101db0af4bcf2fd9";
    private String sessionid = "oyPm--oJmTko27XHnaxoyyr0aNgGs424";
    private String id = "582bc19580144c1b773611e9";
    private String token = "";///"7ILUcJUmJGSmyeaB3QNWpUlPdT3-E2MXkC1oZhqJ:HBHjxd_oTlAiwaht4niHQFDmh6Q=:eyJyZXR1cm5Cb2R5Ijoie1xuICAgICAgICAgICAgXCJuYW1lXCI6ICQoZm5hbWUpLFxuICAgICAgICAgICAgXCJzaXplXCI6ICQoZnNpemUpLFxuICAgICAgICAgICAgXCJ3XCI6ICQoaW1hZ2VJbmZvLndpZHRoKSxcbiAgICAgICAgICAgIFwiaFwiOiAkKGltYWdlSW5mby5oZWlnaHQpLFxuICAgICAgICAgICAgXCJoYXNoXCI6ICQoZXRhZylcbiAgICAgICAgfSIsInNjb3BlIjoidW5kZWZpbmVkOjU4MjkyMGFmYzcwMDkzM2M0ZDZjMjVkYyIsImRlYWRsaW5lIjoxNDc5MjQ4NDA2fQ==";


    private final static int CROP_PORTRAIT = 100;


    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_api);

        imageView = (ImageView) findViewById(R.id.loading);


        Glide.with(this).load(R.drawable.login_logo).into(imageView);
        //InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR;
        //ExternalCacheDiskCacheFactory

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
                //testGetUpToken(this);
                //testCenterData(this);

                Intent i = new Intent(this,ContactActivity.class);
                startActivity(i);

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
                //testCoverUpLoad(this);
                //testLessonHomePage(this);
                //editPortrait();
                //testEditLesson(this);
                //confirmEnllor(this);
//                testHUB(this);
                //testCenterData(this);
                //crash();
               // testGetData(this);
                //testLoading();
                testT(this);

                break;
            }

        }
    }


    private void testT(Context context){


         Intent i = new Intent(this,PostDynamicActivity.class);
        startActivity(i);





//            SecurityManager.requestHavePrivilege(context, new APIServiceCallback<Privilege[]>() {
//                @Override
//                public void onSuccess(Privilege[] object) {
//
//                }
//
//                @Override
//                public void onFailure(String errorCode, String errorMessage) {
//
//                }
//            }, Su.Permission.COURSE_OPEN_CREATE);


        //loginP.login(context,loginParams,null);

//        AccountRequest<HomeData> accountRequest = new AccountRequest<>(context, new APIServiceCallback<HomeData>() {
//            @Override
//            public void onSuccess(HomeData object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });
//
//        accountRequest.getHomeData("TG_yZafXu5EvI9BeOGCfeDcVK-4b65wP");


    }


    private void testLoading() {
        ProgressHUD.create(this).show();
    }

    private void testGetData(Context context) {

        String lesson = "5834f31eaf662a8111362161";

        LessonDataManager.requestLessonData(context, lesson, new APIServiceCallback<LessonDetail>() {
            @Override
            public void onSuccess(LessonDetail object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });

    }

    private void testCenterData(Context context) {

        AccountDataManager.requestCenterData(context, new APIServiceCallback<CenterData>() {
            @Override
            public void onSuccess(CenterData object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });

    }

    private void crash(){


        CrashReport.testJavaCrash();
    }

    private void anr(){


        CrashReport.testANRCrash();
    }

    private void testHUB(Context context) {

        ProgressHUD.create(context).show();

    }

    private void confirmEnllor(Context context) {


        String lesson = "5832adb511efdb0a38faffe1";
        String re = "5832aeaa11efdb0a38fafff1";

        LessonDataManager.requestLessonEnrollment(context, lesson, re, new APIServiceCallback<CLEResponse>() {
            @Override
            public void onSuccess(CLEResponse object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }


    private void testEnrollLesson(Context context){

        String lesson= "582d58353d73a5046fc24490";
//        LessonDataManager.requestEnrollLesson(context, sessionid, lesson, new APIServiceCallback<ELResponse>() {
//            @Override
//            public void onSuccess(ELResponse object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });
    }


    private void testEditLesson(Context context) {

        String lesson= "582d58353d73a5046fc24490";
        Enroll enroll = new Enroll();
        enroll.setMax(100);
        enroll.setMandatory(false);

        Fee fee = new Fee();
        fee.setFree(false);
        fee.setType(Finance.PricingType.TOTAL);
        fee.setCharge(100);

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

        LessonDataManager.requestEditLesson(context, lesson, ll, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });


    }

    private void testLessonHomePage(Context context) {

//        String lesson = "5829530cd82a272d115ae6bc";
//        LessonDataManager.requestLessonHomepage(context, lesson, new APIServiceCallback<LessonDetail>() {
//            @Override
//            public void onSuccess(LessonDetail object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

    }

    private void testCoverUpLoad(Context context ) {





    }


    private void editCover() {
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



                        LessonDataManager.requestUploadCover(TestAPIActivity.this,"",cropImgPath,new QiniuService(){

                            @Override
                            public void uploadSuccess(String fileName, String fileUrl) {

                            }

                            @Override
                            public void uploadFailure() {

                            }
                        });

//                        AccountDataManager.requestUploadAvatar(TestAPIActivity.this, sessionid, id, cropImgPath, new QiniuService() {
//                            @Override
//                            public void uploadSucess(String fileName,String fileUrl) {
//
//
//
//                            }
//
//                            @Override
//                            public void uploadFailure() {
//
//
//
//                            }
//                        });

                    }
                }
                break;
        }
    }



    private void getProfile(Context context) {

        AccountDataManager.requestProfile(context, new APIServiceCallback<Account.Basic>() {
            @Override
            public void onSuccess(Account.Basic object) {

            }


            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }

    private void editProfile(Context context) {
        Account.Basic b = new Account.Basic();
        b.setBirthday(new Date(System.currentTimeMillis()));
        b.setSex("false");
        b.setTitle("天下唯我独尊");

        AccountDataManager.requestEditProfile(context, b, new APIServiceCallback() {
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

        CategoriesDataManager.requestGetSubject(context, new APIServiceCallback<CSubject>() {
            @Override
            public void onSuccess(CSubject object) {

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

//        ServiceRequest serviceRequest = new ServiceRequest();
//
//        String jsonstr = serviceRequest.objectToJsonString(criteria);
//        Logger.json(jsonstr);
    }

    private void testPutLessonOnShelves(Context context) {

        String lession = "58211abfc52b32f4568faa58";

        LessonDataManager.requestPutLessonOnShelves(context, lession, new APIServiceCallback<GetLessonsResponse>() {
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


        LessonDataManager.requestGetLessons(context, criteria, pagination, new APIServiceCallback<GetLessonsResponse>() {
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
        fee.setCharge(100);

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


        LessonDataManager.requestCreateLiveLesson(context, cl, new APIServiceCallback() {
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


        AccountDataManager.requestClaimCompetency(context, cp, new APIServiceCallback<ClaimCompetency>() {
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


        LoginDataManager.requestLogoutByAPI(context, new APIServiceCallback() {

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