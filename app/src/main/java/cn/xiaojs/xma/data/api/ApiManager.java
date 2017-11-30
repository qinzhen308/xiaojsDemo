package cn.xiaojs.xma.data.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.TimeUnit;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.api.interceptor.CommonHeaderInterceptor;
import cn.xiaojs.xma.data.api.service.Live2Service;
import cn.xiaojs.xma.data.api.service.LiveService;
import cn.xiaojs.xma.data.api.service.OtherService;
import cn.xiaojs.xma.data.api.service.XiaojsService;
import cn.xiaojs.xma.data.preference.DataPref;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.util.APPUtils;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
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

    public LiveService getLiveService() {
        return createLiveService();
    }

    public Live2Service getLive2Service() {
        return createLive2Service();
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
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS);
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

    public static OkHttpClient getPayClient() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        if (XiaojsConfig.DEBUG) {
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {

            logInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .addNetworkInterceptor(logInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

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
        File realDir = new File(cacheDirectory, XiaojsConfig.HTTP_CACHE_DIR);

        return new Cache(realDir, XiaojsConfig.HTTP_CACHE_SIZE);
    }

    private XiaojsService createXiaojsService() {

        String url = getXASUrl(getAppContext());

        if (XiaojsConfig.SHOW_DEMO) {
            Log.d("HTTP_LOG", "XAS url:" + url);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(XiaojsService.class);
    }

    private LiveService createLiveService() {


        String url = getXLSUrl(getAppContext());
        if (XiaojsConfig.SHOW_DEMO) {
            Log.d("HTTP_LOG", "XLS url:" + url);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(LiveService.class);
    }

    private Live2Service createLive2Service() {


        String url = getXLSUrl(getAppContext());
        if (XiaojsConfig.SHOW_DEMO) {
            Log.d("HTTP_LOG", "XLS url:" + url);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(Live2Service.class);
    }


    public static ClassResponse getClassResponse(ResponseBody body) {
        if (body == null) return null;

        try {
            String j = body.string();
            if (TextUtils.isEmpty(j)) return null;

            return ClassroomBusiness.parseSocketBean(new JSONObject(j), ClassResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public OtherService createOtherService(String url) {


        if (XiaojsConfig.SHOW_DEMO) {
            Log.d("HTTP_LOG", "XLS url:" + url);
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(OtherService.class);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //url

    /**
     * XAS URL
     * @param context
     * @return
     */
    public static String getXASUrl(Context context) {

        String channel = XiaojsConfig.CHANNEL;

        if (channel.equals(XiaojsConfig.CHANNEL_ENV_DEVTEST)) {
            String port = DataPref.getXASPort(context);
            return createUrl(context,port);
        }else if (channel.equals(XiaojsConfig.CHANNEL_ENV_PRE)){
            return XiaojsConfig.XAS_PRE_BASE_URL;
        }else {
            return XiaojsConfig.XAS_BASE_URL;
        }

    }

    /**
     * XLS URL
     * @param context
     * @return
     */
    public static String getXLSUrl(Context context) {

        String channel = XiaojsConfig.CHANNEL;

        if (channel.equals(XiaojsConfig.CHANNEL_ENV_DEVTEST)) {
            String port = DataPref.getXLSPort(context);
            return createUrl(context,port);
        }else if (channel.equals(XiaojsConfig.CHANNEL_ENV_PRE)){
            return XiaojsConfig.XLS_PRE_BASE_URL;
        }else {
            return XiaojsConfig.XLS_BASE_URL;
        }

    }

    public static String getQrcodeScanUrl() {

        String channel = XiaojsConfig.CHANNEL;

        if (channel.equals(XiaojsConfig.CHANNEL_ENV_DEVTEST)) {
            return new StringBuilder(XiaojsConfig.TEST_BASE_URL).append("/live/").toString();
        }else {
            return new StringBuilder(XiaojsConfig.SHARE_BASE_URL).append("/live/").toString();
        }

    }

    /**
     * 获取分享课的链接地址，内网测试地址不包含端口号
     * @param lessonId
     * @return
     */
    public static String getShareLessonUrl(String lessonId,String type) {

        String baseUrl = null;
        if(APPUtils.isProEvn()){
            baseUrl = XiaojsConfig.SHARE_LESSON_BASE_URL;
        }else {
            baseUrl=XiaojsConfig.SHARE_LESSON_TEST_BASE_URL;
        }
        String typeParam="";
        if(Account.TypeName.STAND_ALONE_LESSON.equals(type)){//公开课
            typeParam="coursedetails";
        }else if(Account.TypeName.CLASS_LESSON.equals(type)){
            typeParam="classhome";
        }else if(Social.SearchType.COURSE.equals(type)){
            typeParam="recorded";
        }else {//班课
            typeParam="coursedetails";
        }
        return new StringBuilder(baseUrl)
//                .append("/web/mobile/")
                .append("/web/share/")
                .append(typeParam)
                .append("/")
                .append(lessonId)
                .append(".html")
                .toString();
    }

    /**
     * 获取“帮助”页面url
     */
    public static String getHelpUrl() {

        String baseUrl = null;
        if(APPUtils.isProEvn()){
            baseUrl = XiaojsConfig.SHARE_LESSON_BASE_URL;
        }else {
            baseUrl=XiaojsConfig.SHARE_LESSON_TEST_BASE_URL;
        }

        return new StringBuilder(baseUrl)
                .append("/web/help/help.html")
                .append("?app=android")
                .toString();
    }
    /**
     * 获取“发现推荐页”页面url
     */
    public static String getDiscoverHomeUrl() {

        String baseUrl = null;
        if(APPUtils.isProEvn()){
            baseUrl = XiaojsConfig.SHARE_LESSON_BASE_URL;
        }else {
            baseUrl=XiaojsConfig.SHARE_LESSON_TEST_BASE_URL;
        }

        return new StringBuilder(baseUrl)
                .append("/web/app/homepage.html")
                .append("?app=android")
                .toString();
    }

    private static String createUrl(Context context,String port) {
        String baseUrl = XiaojsConfig.SHOW_DEMO ? DataPref.getServerIP(context) : XiaojsConfig.TEST_BASE_URL;
        StringBuilder urlBulder = new StringBuilder(baseUrl);
        if (!TextUtils.isEmpty(port)) {
            urlBulder.append(":").append(port);
        }
        return urlBulder.toString();
    }

    /**
     * qiniu file bucket
     * @return
     */
    public static String getFileBucket() {

        String channel = XiaojsConfig.CHANNEL;
        if (channel.equals(XiaojsConfig.CHANNEL_ENV_DEVTEST)) {
            return XiaojsConfig.TEST_FILES_BUCKET_URL;
        }else if(channel.equals(XiaojsConfig.CHANNEL_ENV_PRE)){
            return XiaojsConfig.PRE_FILES_BUCKET_URL;
        }

        return XiaojsConfig.FILES_BUCKET_URL;
    }


    /**
     * qiniu live bucket
     * @return
     */
    public static String getLiveBucket() {
        String channel = XiaojsConfig.CHANNEL;
        if (channel.equals(XiaojsConfig.CHANNEL_ENV_DEVTEST)) {
            return XiaojsConfig.TEST_LIVE_BUCKET_URL;
        } else if(channel.equals(XiaojsConfig.CHANNEL_ENV_PRE)){
            return XiaojsConfig.PRE_LIVE_BUCKET_URL;
        }

        return XiaojsConfig.LIVE_BUCKET_URL;
    }


    public static String getClassroomUrl(Context context, String ticket) {
        return new StringBuilder(getXLSUrl(context))
                .append("/")
                .append(ticket)
                .toString();
    }

    public static String getXMSUrl(Context context) {

        String channel = XiaojsConfig.CHANNEL;
        String baseUrl;

        if (channel.equals(XiaojsConfig.CHANNEL_ENV_DEVTEST)) {
            String port = XiaojsConfig.XMS_PORT;
            baseUrl = createUrl(context,port);
        }else if(channel.equals(XiaojsConfig.CHANNEL_ENV_PRE)){
            baseUrl = XiaojsConfig.XMS_PRE_BASE_URL;
        }else {
            baseUrl = XiaojsConfig.XMS_BASE_URL;
        }


        return new StringBuilder(baseUrl)
                .append("/")
                .append(XiaojsConfig.XMS_IOCM_COMPARTMENT)
                .toString();

    }



}
