package cn.xiaojs.xma.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.kaola.qrcodescanner.qrcode.QrCodeActivity;
import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;


/**
 * Created by maxiaobao on 2017/5/2.
 */

public class ScanQrcodeActivity extends QrCodeActivity {

    private final int splitLength = 6;
    private final String urlSuffix = "/1";
    private String urlPrefix;

    private final String IDENTIFICATION_CLASS_CODE="xjsweixin";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlPrefix = ApiManager.getQrcodeScanUrl();
    }

    @Override
    public void handleSuccessResult(String data) {

        if (XiaojsConfig.DEBUG) {
            Logger.d("decode data:" + data);
        }

        //要判断是不是教室的URL http://192.168.100.3/live/590ad80c25558febef0f6957/1
        String key = checkAndParseKey(data);
        if (!TextUtils.isEmpty(key)) {

            Intent i = new Intent(this, ClassroomActivity.class);
            i.putExtra(Constants.KEY_TICKET,key);
            startActivity(i);
            finish();
        } else if(data!=null&&data.contains(IDENTIFICATION_CLASS_CODE)){
            //h5 班级信息的url
            String url=null;
            if(data.contains("?")){
                url=data+"&app=android";
            }else {
                url=data+"?app=android";
            }
            CommonWebActivity.invoke(this,"",url);
            finish();
        }else {
            Toast.makeText(this, R.string.Invalid_calss_qrcode_tips, Toast.LENGTH_SHORT).show();
            restartPreview();
        }

    }


    private String checkAndParseKey(String data) {

        if (TextUtils.isEmpty(data)) {
            return null;
        }

        if(data.endsWith(urlSuffix) && data.startsWith(urlPrefix)){

            String[] items = data.split("/");
            if (items.length == splitLength && !TextUtils.isEmpty(items[4])) {
                return items[4];
            }
        }

        return null;
    }

}
