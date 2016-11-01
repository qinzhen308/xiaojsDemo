package com.benyuan.xiaojs;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.benyuan.xiaojs.api.InterceptorTest;
import com.benyuan.xiaojs.data.RegisterDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.RegisterInfo;
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

        testRegister(appContext);

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
}
