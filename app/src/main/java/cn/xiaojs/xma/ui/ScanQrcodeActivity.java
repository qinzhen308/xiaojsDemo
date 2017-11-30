package cn.xiaojs.xma.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.kaola.qrcodescanner.qrcode.QrCodeActivity;
import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ctl.ClassInfo;
import cn.xiaojs.xma.model.ctl.CriteriaStudents;
import cn.xiaojs.xma.model.ctl.CriteriaStudentsDoc;
import cn.xiaojs.xma.model.ctl.Students;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonHomeActivity;
import cn.xiaojs.xma.ui.recordlesson.RecordedLessonEnrollActivity;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.ClassStateUtil;
import cn.xiaojs.xma.util.ToastUtil;


/**
 * Created by maxiaobao on 2017/5/2.
 */

public class ScanQrcodeActivity extends QrCodeActivity {

    private final int splitLength = 6;
    private final String urlSuffix = "/1";
    private String urlPrefix;

    private final String IDENTIFICATION_CLASS_CODE="/classhome/";
    private final String IDENTIFICATION_STANDALONG_LESSON_CODE="/coursedetails/";
    private final String IDENTIFICATION_RECORDED_LESSON_CODE="/recorded/";


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

        if(!TextUtils.isEmpty(data)&&!URLUtil.isHttpUrl(data)&&!URLUtil.isHttpsUrl(data)){//认为是纯文本
            //直接显示
            CommonWebActivity.invoke(this,"",data);
            finish();
            return;
        }

        //要判断是不是教室的URL http://192.168.100.3/live/590ad80c25558febef0f6957/1
        String key = checkAndParseKey(data);
        if (!TextUtils.isEmpty(key)) {
            Classroom2Activity.invoke(this,key);
            finish();
        } else if(data!=null){
            if(data.contains(IDENTIFICATION_CLASS_CODE)){
                //h5 班级信息的url
                checkJoinClassStateAndEnterClassroom(data);
            }else if(data.contains(IDENTIFICATION_STANDALONG_LESSON_CODE)){
                String id=data.substring(data.lastIndexOf("/")+1,data.contains(".")?data.lastIndexOf("."):data.length());
                startActivity(new Intent(this,LessonHomeActivity.class).putExtra(CourseConstant.KEY_LESSON_ID,id));
            }else if(data.contains(IDENTIFICATION_RECORDED_LESSON_CODE)){
                String id=data.substring(data.lastIndexOf("/")+1,data.contains(".")?data.lastIndexOf("."):data.length());
                RecordedLessonEnrollActivity.invoke(this,id);
            }

        }else if(URLUtil.isHttpUrl(data)||URLUtil.isHttpsUrl(data)){
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(data));
            startActivity(Intent.createChooser(intent,"请选择浏览器打开链接："+data));
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

    /**
     * 进教室或者进教室详情页
     * @param data
     */
    private void checkJoinClassStateAndEnterClassroom(final String data){
       /* if(true){
            String url=null;
            if(data.contains("?")){
                url=data+"&app=android";
            }else {
                url=data+"?app=android";
            }
            CommonWebActivity.invoke(ScanQrcodeActivity.this,"",url);
            finish();
            return;
        }*/
        final String id=data.substring(data.lastIndexOf("/")+1,data.lastIndexOf("."));
        ClassStateUtil.checkClassroomStateForMe(this, id, new ClassStateUtil.ClassStateCallback() {
            @Override
            public void onClassroomOpen(String ticket) {
                Classroom2Activity.invoke(ScanQrcodeActivity.this,ticket);
                finish();
            }

            @Override
            public void onClassroomClose(String id) {
                String url=null;
                if(data.contains("?")){
                    url=data+"&app=android";
                }else {
                    url=data+"?app=android";
                }
                CommonWebActivity.invoke(ScanQrcodeActivity.this,"",url);
                finish();
            }

            @Override
            public void onError(String msg) {
                ToastUtil.showToast(getApplicationContext(),msg);

            }
        });
    }

}
