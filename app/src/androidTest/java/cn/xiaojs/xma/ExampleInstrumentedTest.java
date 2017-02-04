package cn.xiaojs.xma;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Security;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.LoginDataManager;
import cn.xiaojs.xma.data.RegisterDataManager;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.api.CollaRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.APIEntity;
import cn.xiaojs.xma.model.ClaimCompetency;
import cn.xiaojs.xma.model.Collaboration.TokenPair;
import cn.xiaojs.xma.model.CompetencyParams;
import cn.xiaojs.xma.model.HomeData;
import cn.xiaojs.xma.model.LessonDetail;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.model.security.LoginInfo;
import cn.xiaojs.xma.model.security.LoginParams;
import cn.xiaojs.xma.model.Privilege;
import cn.xiaojs.xma.model.VerifyCode;
import cn.xiaojs.xma.util.APPUtils;

import com.orhanobut.logger.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    String seesionId = "fqlt0Xmwcp16-m2Qt8LgmhWUMN9O6i5G";

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        //assertEquals("cn.xiaojs.xma", appContext.getPackageName());


        // InterceptorTest.testCommonHeader();

        //testVersion(appContext);

        //testRegister(appContext);
        //testLogin(appContext);

        //testLogout(appContext);
        //testSendCode(appContext);
        //testValidateCode(appContext);
        //testGetHomeData(appContext);

        //testCreateLession(appContext);
        //testClaimCompetency(appContext);
        //testEditLesson(appContext);
        //testCenterData(appContext);
        //testGetData(appContext);
        //testToggleLesson(appContext);
        testT(appContext);


//        String json = "{\"1482891322185\":\"xiao peng you\"}";
//
//
//        ObjectMapper o = new ObjectMapper();
//        JsonNode x = o.readTree(json);
//
//
//
//        JSONObject jobject = new JSONObject(json);
//        String cc = jobject.keys().next();
//        System.out.println("===========:"+cc);


    }


    private void testT(Context context){


        CollaRequest collaRequest = new CollaRequest(context, new APIServiceCallback<TokenPair[]>() {
            @Override
            public void onSuccess(TokenPair[] pairs) {
                TokenPair tokenPair = pairs[0];
                System.out.println("key:"+tokenPair.key+", token:"+tokenPair.token);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });

//        SecurityManager.requestHavePrivilege(context, new APIServiceCallback<Privilege[]>() {
//            @Override
//            public void onSuccess(Privilege[] object) {
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        }, Su.Permission.COURSE_OPEN_CREATE);

    }




    private void testToggleLesson(Context context) {
        String lesson = "5834f31eaf662a8111362161";

        LessonDataManager.requestToggleAccessLesson(context, lesson, true, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });

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


    private void testEditLesson(Context context) {

        String lesson = "582d42b6ac3a72d80b14a2e6";
        LiveLesson liveLesson = new LiveLesson();
        liveLesson.setTitle("我是");

        LessonDataManager.requestEditLesson(context, lesson, liveLesson, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });


    }

    private void testCreateLession(Context context) {

//        Enroll enroll = new Enroll();
//        enroll.setMax(100);
//        //enroll.setType(Ctl.EnrollType.ONLINE);
//
//        Fee fee = new Fee();
//        fee.setFree(true);
//        fee.setType(Finance.PricingType.TOTAL);
//        fee.setCharge(100);
//
//        Schedule sch = new Schedule();
//        sch.setStart(new Date(System.currentTimeMillis()));
//        sch.setDuration(1000);
//
//
//        LiveLesson ll = new LiveLesson();
//        ll.setTitle("无人驾驶课");
//        //ll.setSubject("开启AI之旅");
//        ll.setEnroll(enroll);
//        ll.setMode(Ctl.TeachingMode.ONE_2_ONE);
//        ll.setFee(fee);
//        ll.setSchedule(sch);
//
//        CreateLesson cl = new CreateLesson();
//        cl.setData(ll);
//
//
//        LessonDataManager.requestCreateLiveLesson(context, cl, new APIServiceCallback() {
//            @Override
//            public void onSuccess(Object object) {
//                Logger.d("onSuccess-----------");
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//                Logger.d("onFailure-----------");
//            }
//        });


    }

    private void testClaimCompetency(Context context) {

        String seesionId = "XdHK58sMYug_0lhhoYAVocNNLfjgjYs2";
        String subject = "582087dcb3072a5f0b147807";

        CompetencyParams cp = new CompetencyParams();
        cp.setSubject(subject);


        AccountDataManager.requestClaimCompetency(context, cp, new APIServiceCallback<ClaimCompetency>() {
            @Override
            public void onSuccess(ClaimCompetency object) {
                Logger.d("onSuccess-----------");
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                Logger.d("onFailure-----------");
            }
        });

    }

    private void testGetHomeData(Context context) {

//        String seesionId = "fqlt0Xmwcp16-m2Qt8LgmhWUMN9O6i5G";
//
//        AccountDataManager.requestHomeData(context, seesionId, new APIServiceCallback<HomeData>() {
//            @Override
//            public void onSuccess(HomeData object) {
//                Logger.d("onSuccess-----------");
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//                Logger.d("onFailure-----------");
//            }
//        });

    }

    private void testVersion(Context context) {
        String fv = APPUtils.getAPPFullVersion(context);
        String name = APPUtils.getAPPVersionName(context);
        int code = APPUtils.getAPPVersionCode(context);
        System.out.println("----------fversion:" + fv);

        System.out.println("----------versionName:" + name);
        System.out.println("----------versionCode:" + code);
    }

    private void testRegister(Context context) {

//        RegisterInfo registerInfo = new RegisterInfo();
//        registerInfo.setCode(4444);
//        registerInfo.setMobile(12222222222l);
//        registerInfo.setPassword("123456");
//        registerInfo.setUsername("m_test1");
//
//        RegisterDataManager.requestRegisterByAPI(context, registerInfo, new APIServiceCallback() {
//            @Override
//            public void onSuccess(Object object) {
//
//                Logger.d("onSuccess-----------");
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//                Logger.d("onFailure-----------");
//            }
//        });

    }

    private void testLogin(Context context) {

        LoginParams params = new LoginParams();
        params.setPassword("123456");
        params.setCt(Security.CredentialType.PERSION);
        params.setMobile(12222222222l);

        LoginDataManager.requestLoginByAPI(context, params, new APIServiceCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo object) {

                Logger.d("onSuccess-----------");

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                Logger.d("onFailure-----------");
            }
        });
    }

    private void testLogout(Context context) {

        String sessionID = "123444444rgdgdfgs";

        LoginDataManager.requestLogoutByAPI(context, new APIServiceCallback() {

            @Override
            public void onSuccess(Object object) {
                Logger.d("onSuccess-----------");
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Logger.d("onFailure-----------");
            }
        });

    }

    private void testSendCode(Context context) {

        RegisterDataManager.requestSendVerifyCode(context, 12222222222l, new APIServiceCallback<VerifyCode>() {
            @Override
            public void onSuccess(VerifyCode object) {
                Logger.d("onSuccess-----------");
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Logger.d("onFailure-----------");
            }
        });

    }


    private void testValidateCode(Context context) {
        RegisterDataManager.requestValidateCode(context, 13888888888l, 4325, new APIServiceCallback<APIEntity>() {
            @Override
            public void onSuccess(APIEntity object) {
                Logger.d("onSuccess-----------");
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Logger.d("onFailure-----------");
            }
        });
    }
}
