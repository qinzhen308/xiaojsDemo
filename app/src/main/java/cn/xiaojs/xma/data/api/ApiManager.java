package cn.xiaojs.xma.data.api;

import android.content.Context;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.common.xf_foundation.schemas.Security;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.api.interceptor.CommonHeaderInterceptor;
import cn.xiaojs.xma.data.api.service.XiaojsService;

import cn.xiaojs.xma.util.APPUtils;
import cn.xiaojs.xma.util.UIUtils;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.File;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by maxiaobao on 2016/10/25.
 */

public class ApiManager {

    private volatile static ApiManager apiManager;
    //private XiaojsService xiaojsService;
    private OkHttpClient okHttpClient;
    private Context appContext;


    public static ApiManager getAPIManager(Context appContext) {

        if (apiManager == null) {

            synchronized (ApiManager.class) {

                if (apiManager == null) {
                    apiManager = new ApiManager(appContext);
                }
            }
        }

        return apiManager;
    }

    private ApiManager(Context appContext) {
        this.appContext = appContext.getApplicationContext();
        this.okHttpClient = createOkhttp();
    }

    public Context getAppContext() {
        return appContext;
    }


    public XiaojsService getXiaojsService() {

        return createXiaojsService();

    }

    public Cache getCache() {
        return okHttpClient.cache();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    private OkHttpClient createOkhttp() {

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        if (XiaojsConfig.DEBUG) {
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {

            logInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .addNetworkInterceptor(logInterceptor)
                .addInterceptor(new CommonHeaderInterceptor(appContext))
                //.addInterceptor(new CacheInterceptor())
                .cache(createCache(appContext))
                .connectTimeout(20,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20,TimeUnit.SECONDS);
//                .cookieJar(new CookieJar() {
//                    //private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
//
//                    private List<Cookie> cookieStore;
//
//                    @Override
//                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//                        //cookieStore.put(url, cookies);
//                        cookieStore = cookies;
//                    }
//
//                    @Override
//                    public List<Cookie> loadForRequest(HttpUrl url) {
//                        //List<Cookie> cookies = cookieStore.get(url);
//                        return cookieStore != null ? cookieStore : new ArrayList<Cookie>();
//                    }
//                });


        if (XiaojsConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }

        return builder.build();

    }

    private Cache createCache(Context context) {

        File cacheDirectory = context.getCacheDir();
        if (cacheDirectory == null) {
            return null;
        }
        File realDir = new File(cacheDirectory,XiaojsConfig.HTTP_CACHE_DIR);

        return new Cache(realDir,XiaojsConfig.HTTP_CACHE_SIZE);
    }

    private XiaojsService createXiaojsService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(XiaojsService.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(XiaojsService.class);
    }



}
