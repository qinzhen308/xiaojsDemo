package com.benyuan.xiaojs;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.benyuan.xiaojs.api.InterceptorTest;
import com.benyuan.xiaojs.common.xf_foundation.schemas.Security;
import com.benyuan.xiaojs.data.LoginDataManager;
import com.benyuan.xiaojs.data.RegisterDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.APIEntity;
import com.benyuan.xiaojs.model.LoginInfo;
import com.benyuan.xiaojs.model.LoginParams;
import com.benyuan.xiaojs.model.RegisterInfo;
import com.benyuan.xiaojs.model.VerifyCode;
import com.benyuan.xiaojs.util.APPUtils;
import com.orhanobut.logger.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        //assertEquals("com.benyuan.xiaojs", appContext.getPackageName());


       // InterceptorTest.testCommonHeader();

        //testVersion(appContext);

        //testRegister(appContext);
        //testLogin(appContext);

        //testLogout(appContext);
        testSendCode(appContext);
        //testValidateCode(appContext);
    }

    private void testVersion(Context context ){
        String fv = APPUtils.getAPPFullVersion(context);
        String name = APPUtils.getAPPVersionName(context);
        int code = APPUtils.getAPPVersionCode(context);
        System.out.println("----------fversion:"+fv);

        System.out.println("----------versionName:"+name);
        System.out.println("----------versionCode:"+code);
    }

    private void testRegister(Context context){

        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setCode(4444);
        registerInfo.setMobile(12222222222l);
        registerInfo.setPassword("123456");
        registerInfo.setUsername("m_test1");

        RegisterDataManager.requestRegisterByAPI(context, registerInfo, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

                Logger.d("onSuccess-----------");

            }

            @Override
            public void onFailure(String errorCode) {
                Logger.d("onFailure-----------");
            }
        });

    }

    private void testLogin(Context context){

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
            public void onFailure(String errorCode) {

                Logger.d("onFailure-----------");
            }
        });
    }

    private void testLogout(Context context){

        String sessionID = "123444444rgdgdfgs";

        LoginDataManager.requestLogoutByAPI(context, sessionID, new APIServiceCallback() {

            @Override
            public void onSuccess(Object object) {
                Logger.d("onSuccess-----------");
            }

            @Override
            public void onFailure(String errorCode) {
                Logger.d("onFailure-----------");
            }
        });

    }

    private void testSendCode(Context context){

        RegisterDataManager.requestSendVerifyCode(context, 12222222222l, new APIServiceCallback<VerifyCode>() {
            @Override
            public void onSuccess(VerifyCode object) {
                Logger.d("onSuccess-----------");
            }

            @Override
            public void onFailure(String errorCode) {
                Logger.d("onFailure-----------");
            }
        });

    }


    private void testValidateCode(Context context){
        RegisterDataManager.requestValidateCode(context, 13888888888l, 4325, new APIServiceCallback<APIEntity>() {
            @Override
            public void onSuccess(APIEntity object) {
                Logger.d("onSuccess-----------");
            }

            @Override
            public void onFailure(String errorCode) {
                Logger.d("onFailure-----------");
            }
        });
    }
}
