package com.benyuan.xiaojs.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.xf_foundation.schemas.Security;
import com.benyuan.xiaojs.data.LoginDataManager;
import com.benyuan.xiaojs.data.RegisterDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.APIEntity;
import com.benyuan.xiaojs.model.LoginInfo;
import com.benyuan.xiaojs.model.LoginParams;
import com.benyuan.xiaojs.model.RegisterInfo;
import com.benyuan.xiaojs.model.VerifyCode;
import com.orhanobut.logger.Logger;

public class TestAPIActivity extends Activity {

    private int vcode;
    private long mob = 18701686973l;
    private String sessionid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_api);
    }


    public void btnClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_svc: {
                testSendCode(this);

                break;
            }
            case R.id.btn_svm:{
                //testValidateCode(this);
                //testRegister(this);
                testLogin(this);
                break;
            }
            case R.id.btn_q:{
                //testValidateCode(this);
                //testRegister(this);
                //testLogin(this);
                testLogout(this);
                break;
            }

        }
    }

    private void testLogout(Context context){


        LoginDataManager.requestLogoutByAPI(context, sessionid, new APIServiceCallback() {

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

    private void testLogin(final Context context){

        LoginParams params = new LoginParams();
        params.setPassword("123456");
        params.setCt(Security.CredentialType.PERSION);
        params.setMobile(mob);

        LoginDataManager.requestLoginByAPI(context, params, new APIServiceCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo object) {

                Logger.d("onSuccess-----------");

                sessionid = object.getUser().getSessionID();

                Toast.makeText(context,"login Ok:"+sessionid,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(String errorCode) {

                Logger.d("onFailure-----------");
            }
        });
    }



    private void testRegister(final Context context){

        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setCode(vcode);
        registerInfo.setMobile(mob);
        registerInfo.setPassword("123456");
        registerInfo.setUsername("m_test1");

        RegisterDataManager.requestRegisterByAPI(context, registerInfo, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

                Logger.d("onSuccess-----------");
                Toast.makeText(context,"register Ok",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(String errorCode) {
                Logger.d("onFailure-----------register");
                Toast.makeText(context,"register error code:"+errorCode,Toast.LENGTH_LONG).show();
            }
        });

    }

    private void testValidateCode(final Context context){
        RegisterDataManager.requestValidateCode(context, mob, vcode, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

                Toast.makeText(context,"validate Ok",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(String errorCode) {
                Toast.makeText(context,"validate error code:"+errorCode,Toast.LENGTH_LONG).show();
            }
        });

    }

    private void testSendCode(final Context context){

        RegisterDataManager.requestSendVerifyCode(context, mob, new APIServiceCallback<VerifyCode>() {
            @Override
            public void onSuccess(VerifyCode object) {
                Logger.d("onSuccess-----------");

                vcode = object.getCode();
                Toast.makeText(context,"code:"+vcode,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(String errorCode) {
                Logger.d("onFailure-----------errorcode:%s",errorCode);
            }
        });

    }

}
