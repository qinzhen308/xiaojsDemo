package com.benyuan.xiaojs.data.api.service;

import android.text.TextUtils;

import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.data.preference.AccountPref;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class ServiceRequest {

    protected static final int SUCCESS_CODE = 200;
    //protected static final String EMPTY_EXCEPTION = "No content to map due to end-of-input";

    protected final String getExceptionErrorCode() {
        return getDefaultErrorCode();
    }

    protected final String getDefaultErrorCode() {
        return Errors.NO_ERROR;
    }

    /**
     * Convert object to JSON string
     */
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

    /**
     * 解析error body json
     */
    public String parseErrorBody(String errorBody) {

        String errorCode = getDefaultErrorCode();

        if (TextUtils.isEmpty(errorBody)) {
            return errorCode;
        }

        try {
            JSONObject jobject = new JSONObject(errorBody);

            errorCode = jobject.getString("ec");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return errorCode;

    }

}
