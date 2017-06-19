package cn.xiaojs.xma.util;

import android.app.Activity;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import cn.xiaojs.xma.data.DataChangeHelper;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.ui.lesson.xclass.util.IDialogMethod;

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
        LessonDataManager.joinClass(context, classid, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                ((IDialogMethod)context).cancelProgress();
                ToastUtil.showToast(context,"加入成功");
                DataChangeHelper.getInstance().notifyDataChanged(SimpleDataChangeListener.CREATE_CLASS_CHANGED);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ((IDialogMethod)context).cancelProgress();
                ToastUtil.showToast(context,errorMessage);
            }
        });

    }


}
