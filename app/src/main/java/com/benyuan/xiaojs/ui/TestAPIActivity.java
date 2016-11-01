package com.benyuan.xiaojs.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.data.RegisterDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.APIEntity;
import com.benyuan.xiaojs.model.VerifyCode;
import com.orhanobut.logger.Logger;

public class TestAPIActivity extends Activity {

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

        }
    }


    private void testValidateCode(Context context){
        RegisterDataManager.requestValidateCode(context, 18701686972l, 4325, new APIServiceCallback<APIEntity>() {
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

    private void testSendCode(final Context context){

        RegisterDataManager.requestSendVerifyCode(context, 18701686972l, new APIServiceCallback<VerifyCode>() {
            @Override
            public void onSuccess(VerifyCode object) {
                Logger.d("onSuccess-----------");

                int code = object.getCode();
                Toast.makeText(context,"code:"+code,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(String errorCode) {
                Logger.d("onFailure-----------errorcode:%s",errorCode);
            }
        });

    }

}
