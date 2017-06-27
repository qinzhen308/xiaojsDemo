package cn.xiaojs.xma.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.xiaojs.xma.data.DataChangeHelper;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ctl.JoinResponse;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.lesson.xclass.util.IDialogMethod;
import okhttp3.ResponseBody;

/**
 * Created by Paul Z on 2017/6/9.
 */

public class JsInvokeNativeInterface {
    Activity context;
    WebView tagView;


    public JsInvokeNativeInterface(Activity context,WebView tagView){
        this.tagView=tagView;
        this.context=context;
    }


    @JavascriptInterface
    public void xjsclasshome(String classid){
        if(TextUtils.isEmpty(classid)){
            ToastUtil.showToast(context,"classid 无效");
            return;
        }
        ((IDialogMethod)context).showProgress(true);
        LessonDataManager.joinClass(context, classid, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
                ((IDialogMethod)context).cancelProgress();

                JoinResponse joinResponse = getJoinResponse(object);
                if (joinResponse == null) {
                    ToastUtil.showToast(context,"加入成功");
                }else {
                    //此班是需要申请验证才能加入的班
                    ToastUtil.showToast(context,"你已经申请加入，等待确认");
                }

                DataChangeHelper.getInstance().notifyDataChanged(SimpleDataChangeListener.CREATE_CLASS_CHANGED);
                context.startActivity(new Intent(context,MainActivity.class));
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ((IDialogMethod)context).cancelProgress();
                ToastUtil.showToast(context,errorMessage);
            }
        });

    }


    private JoinResponse getJoinResponse(ResponseBody body) {
        JoinResponse response = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = body.string();
            response = mapper.readValue(json, JoinResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


}
