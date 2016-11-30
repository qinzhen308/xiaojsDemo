package com.benyuan.xiaojs;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by maxiaobao on 2016/11/30.
 */

public class DebugApplication extends XiaojsApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);

//        Stetho.initialize(Stetho.newInitializerBuilder(this)
//                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
//                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
//                .build());

        LeakCanary.install(this);


    }
}
