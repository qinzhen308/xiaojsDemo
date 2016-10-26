package com.benyuan.xiaojs;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.benyuan.xiaojs.api.InterceptorTest;
import com.benyuan.xiaojs.util.APPUtils;

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

        testVersion(appContext);

    }

    private void testVersion(Context context ){
        String fv = APPUtils.getAPPFullVersion(context);
        String name = APPUtils.getAPPVersionName(context);
        int code = APPUtils.getAPPVersionCode(context);
        System.out.println("----------fversion:"+fv);

        System.out.println("----------versionName:"+name);
        System.out.println("----------versionCode:"+code);
    }
}
