package cn.xiaojs.xma.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.kaola.qrcodescanner.qrcode.QrCodeActivity;
import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.classroom.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.Constants;


/**
 * Created by maxiaobao on 2017/5/2.
 */

public class ScanQrcodeActivity extends QrCodeActivity {


    @Override
    public void handleSuccessResult(String data) {

        if (XiaojsConfig.DEBUG) {
            Logger.d("decode data:" + data);
        }

        //要判断是不是教室的URL
        if (checkRoomUrl(data)) {

            //TODO 解析URL中的KEY，传递给教室
            String key = "";

            Intent i = new Intent(this, ClassroomActivity.class);
            i.putExtra(Constants.KEY_TICKET,key);
            startActivity(i);
            finish();
        }else {
            Toast.makeText(this, "无效的教室二维码，请扫描正确的教室二维码", Toast.LENGTH_SHORT).show();
            restartPreview();
        }

    }


    private boolean checkRoomUrl(String data) {

        if (TextUtils.isEmpty(data)) {
            return false;
        }

        if(data.startsWith("http://xw.local.com/live/")){
            return true;
        }

        return false;


    }
}
