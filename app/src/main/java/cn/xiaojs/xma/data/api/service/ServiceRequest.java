package cn.xiaojs.xma.data.api.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.model.Error;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.util.APPUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class ServiceRequest<T> implements ContextLifecycle {

    protected static final int SUCCESS_CODE = 200;

    private ApiManager apiManager;

    private APIServiceCallback<T> serviceCallback;
    private Call<T> serviceCall;

    private Object requestObj;

    public ServiceRequest(Context context, APIServiceCallback<T> callback) {

        this.serviceCallback = callback;
        apiManager = ApiManager.getAPIManager(context);
        configContext(context);

    }

    public ApiManager getApiManager() {
        return apiManager;
    }

    public XiaojsService getService() {
        return apiManager.getXiaojsService();
    }

    public APIServiceCallback<T> getServiceCallback() {
        return serviceCallback;
    }

    public void doTask(int apiType, T responseBody) {
    }

    //Convert object to JSON string
    public String objectToJsonString(Object object) {

        String jsonStr = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat(XiaojsService.DATE_FORMAT));
            mapper.setTimeZone(TimeZone.getTimeZone(XiaojsService.TIME_ZONE_ID));
            jsonStr = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return jsonStr;

    }


    //解析error body json
//    public String parseErrorBody(String errorBody) {
//
//        String errorCode = getDefaultErrorCode();
//
//        if (TextUtils.isEmpty(errorBody)) {
//            return errorCode;
//        }
//
//        try {
//            JSONObject jobject = new JSONObject(errorBody);
//
//            errorCode = jobject.getString("ec");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return errorCode;
//
//    }

    private Error getError(String errorBody) {
        Error error = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            error = mapper.readValue(errorBody, Error.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return error;
    }

    private ContactGroup parseAddGroup(String body) {

        ContactGroup contactGroup = null;

        try {
            JSONObject jobject = new JSONObject(body);

            String key = jobject.keys().next();
            String name = jobject.getString(key);

            contactGroup = new ContactGroup();
            contactGroup.name = name;
            contactGroup.id = key;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return contactGroup;
    }


    private void configContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("You cannot start a load on a null Context");
        } else if (APPUtils.isMainThread() && !(context instanceof Application)) {
            if (context instanceof FragmentActivity) {
                get((FragmentActivity) context);
            } else if (context instanceof Activity) {
                get((Activity) context);
            } //else if (context instanceof ContextWrapper) {
            //  return configContext(((ContextWrapper) context).getBaseContext());
            //}
        }
    }

    private void get(FragmentActivity activity) {
        if (APPUtils.isBackgroundThread()) {
            configContext(activity.getApplicationContext());
        } else {
            assertNotDestroyed(activity);
            android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
            getSupportRequestFragment(fm);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void get(Activity activity) {
        if (APPUtils.isMainThread() || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            configContext(activity.getApplicationContext());
        } else {
            assertNotDestroyed(activity);
            android.app.FragmentManager fm = activity.getFragmentManager();
            getRequestFragment(fm);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void assertNotDestroyed(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
        }
    }


    private RequestFragment getRequestFragment(final FragmentManager fm) {

        RequestFragment requestFragment =
                (RequestFragment) fm.findFragmentByTag(RequestFragment.FRAGMENT_TAG);
        if (requestFragment == null) {

            requestFragment = new RequestFragment();
            fm.beginTransaction()
                    .add(requestFragment, RequestFragment.FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        }

        requestFragment.addLifecycle(this);

        requestObj = requestFragment;

        return requestFragment;
    }

    private SupportRequestFragment getSupportRequestFragment(final android.support.v4.app.FragmentManager fm) {

        SupportRequestFragment supportRequestFragment =
                (SupportRequestFragment) fm.findFragmentByTag(SupportRequestFragment.FRAGMENT_TAG);

        if (supportRequestFragment == null) {
            supportRequestFragment = new SupportRequestFragment();
            fm.beginTransaction().add(supportRequestFragment, SupportRequestFragment.FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        }

        supportRequestFragment.addLifecycle(this);

        requestObj = supportRequestFragment;

        return supportRequestFragment;

    }

    private void delRefrence() {

        if (requestObj == null)
            return;

        if (XiaojsConfig.DEBUG) {
            Logger.d("delete reference from fragment");
        }

        if (requestObj instanceof RequestFragment) {

            RequestFragment requestFragment = (RequestFragment) requestObj;
            requestFragment.removeLifecycle(this);

        } else if (requestObj instanceof SupportRequestFragment) {

            SupportRequestFragment supportRequestFragment = (SupportRequestFragment) requestObj;
            supportRequestFragment.removeLifecycle(this);
        }


    }


    private void cancelAndResetnull() {

        if (serviceCall != null) {
            serviceCall.cancel();
        }

        serviceCallback = null;
        requestObj = null;
    }

    private final String getExceptionErrorCode() {
        return getDefaultErrorCode();
    }

    private final String getDefaultErrorCode() {
        return Errors.NO_ERROR;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Resolve the Xiaojs service callback
    //

    public final void enqueueRequest(final int apiType, Call<T> call) {

        if (call == null) {
            return;
        }

        serviceCall = call;

        serviceCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {

                //okhttp3.Response res = response.raw().cacheResponse();
                //okhttp3.Response res1 = response.raw().networkResponse();

                onRespones(apiType, response);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {

                onFailures(apiType, t);
            }
        });

    }

    private void onRespones(int apiType, Response<T> response) {

        int responseCode = response.code();
        if (XiaojsConfig.DEBUG) {
            Logger.d("the request has onResponse, the code:%d", responseCode);
        }

        if (responseCode == SUCCESS_CODE) {

            T object = response.body();

            if (serviceCallback != null) {
                serviceCallback.onSuccess(object);
            }

            doTask(apiType, object);

        } else {

            String errorBody = null;
            try {
                errorBody = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Error error = getError(errorBody);
            String errorCode = "-1";

            if (error != null){
                errorCode = error.ec;
            }
            String errorMessage = ErrorPrompts.getErrorMessage(apiType, errorCode);

            if (serviceCallback != null) {
                serviceCallback.onFailure(errorCode, errorMessage);
            }

        }

        delRefrence();
    }


    private void onFailures(int apiType, Throwable t) {
        if (XiaojsConfig.DEBUG) {
            String exception = t.getMessage();
            Logger.d("the request has occur exception:\n %s", exception);
        }


        String errorCode = getExceptionErrorCode();
        String errorMessage = ErrorPrompts.getErrorMessage(apiType, errorCode);

        if (serviceCallback != null) {
            serviceCallback.onFailure(errorCode, errorMessage);
        }

        delRefrence();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Response the context cyclelife
    //

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

        if (XiaojsConfig.DEBUG) {
            Logger.d("the current activity is onDestroy,so cancel request and reaset refrence null");
        }

        cancelAndResetnull();

    }

}
