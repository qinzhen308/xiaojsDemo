package cn.xiaojs.xma.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.DataChangeHelper;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ctl.JoinResponse;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.lesson.xclass.util.IDialogMethod;
import cn.xiaojs.xma.ui.widget.CommonDialog;
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
                DataChangeHelper.getInstance().notifyDataChanged(SimpleDataChangeListener.CREATE_CLASS_CHANGED);
                if (joinResponse == null) {
                    ToastUtil.showToast(context,"加入成功");
                    context.startActivity(new Intent(context,MainActivity.class));
                }else if(!TextUtils.isEmpty(joinResponse.id)){
                    //此班是需要申请验证才能加入的班
                    ToastUtil.showToast(context,"你已经申请加入，等待确认");
                    context.startActivity(new Intent(context,MainActivity.class));
                }else if(!TextUtils.isEmpty(joinResponse.ticket)){
                    showTipDialog(context,joinResponse.ticket);
                }else {
                    ToastUtil.showToast(context,"加入成功");
                    context.startActivity(new Intent(context,MainActivity.class));
                }
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

    private void showTipDialog(final Activity context, final String ticket){
        CommonDialog dialog=new CommonDialog(context);
        dialog.setDesc(R.string.join_class_suc_tip);
        dialog.setLefBtnText(R.string.into_cls);
        dialog.setRightBtnText(R.string.into_my_classes);
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                MainActivity.invokeWithAction(context,MainActivity.ACTION_TO_MY_CLASSES);
            }
        });
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                MainActivity.invokeWithAction(context,MainActivity.ACTION_TO_CLASSROOM,ticket);
            }
        });
        dialog.show();
    }

}
