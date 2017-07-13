package cn.xiaojs.xma.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.EditText;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataChangeHelper;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ctl.AbsStudent;
import cn.xiaojs.xma.model.ctl.CriteriaStudents;
import cn.xiaojs.xma.model.ctl.CriteriaStudentsDoc;
import cn.xiaojs.xma.model.ctl.JoinClassParams;
import cn.xiaojs.xma.model.ctl.JoinResponse;
import cn.xiaojs.xma.model.ctl.Students;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.lesson.xclass.util.IDialogMethod;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import okhttp3.ResponseBody;

/**
 * Created by Paul Z on 2017/6/9.
 */

public class JsInvokeNativeInterface {
    public static final String TYPE_JION_CLASS="classhome";


    Activity context;
    WebView tagView;


    public JsInvokeNativeInterface(Activity context,WebView tagView){
        this.tagView=tagView;
        this.context=context;
    }


    public void xjsclasshome(String classid,boolean needVerify){
        if(TextUtils.isEmpty(classid)){
            showToastOnUiThread("classid 无效");
            return;
        }
        if(needVerify){
            showVerifyMsgDialog(classid);
        }else {
            doJoinRequest(classid,null);
        }

    }

    /**
     * js调java的统一接口
     * @param params  json格式，e.g. {"type":"for do something", "data":"for do something with params"}
     */
    @JavascriptInterface
    public void jsInvokeNative(String params){
        if(TextUtils.isEmpty(params)){
            showToastOnUiThread("参数错误");
            return;
        }
        try {
            JSONObject object=new JSONObject(params);
            if(TYPE_JION_CLASS.equals(object.optString("type"))){
                JSONObject data=object.optJSONObject("data");
                String id=data.optString("id");
                int needJoin=data.optInt("join");
                String ownerID=object.optString("ownerID");
                if(AccountDataManager.getAccountID(context).equals(ownerID)){
                    showToastOnUiThread(R.string.cant_join_yourself_class);
                }else {
                    checkJoinState(id,needJoin==1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showToastOnUiThread("参数解析失败");
        }
    }


    private void doJoinRequest(String classid,String msg){
        showDialogOnUiThread();
        JoinClassParams params=new JoinClassParams();
        params.remarks=msg;
        LessonDataManager.joinClass(context, classid,params, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
                cancelDialogOnUiThread();
                JoinResponse joinResponse = getJoinResponse(object);
                DataChangeHelper.getInstance().notifyDataChanged(SimpleDataChangeListener.CREATE_CLASS_CHANGED);
                if (joinResponse == null) {
                    showToastOnUiThread("加入成功");
                    context.startActivity(new Intent(context,MainActivity.class));
                }else if(!TextUtils.isEmpty(joinResponse.id)){
                    //此班是需要申请验证才能加入的班
                    showToastOnUiThread("您已提交申请，请等待班主任确认");
                    context.startActivity(new Intent(context,MainActivity.class));
                }else if(!TextUtils.isEmpty(joinResponse.ticket)){
                    showTipDialog(joinResponse.ticket);
                }else {
                    showToastOnUiThread("加入成功");
                    context.startActivity(new Intent(context,MainActivity.class));
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelDialogOnUiThread();
                showToastOnUiThread(errorMessage);
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

    private void showTipDialog( final String ticket){
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


    private void showVerifyMsgDialog(final String classid){
        final CommonDialog dialog=new CommonDialog(context);
        dialog.setTitle(R.string.add_class_verification_msg2);
        final EditText editText=new EditText(context);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setHint(R.string.add_class_verify_msg_tip);
        editText.setLines(4);
        editText.setTextColor(context.getResources().getColor(R.color.font_black));
        editText.setBackgroundResource(R.drawable.common_search_bg);
        editText.setGravity(Gravity.LEFT|Gravity.TOP);
        int padding=context.getResources().getDimensionPixelSize(R.dimen.px10);
        editText.setPadding(padding,padding,padding,padding);
        editText.setHintTextColor(context.getResources().getColor(R.color.font_gray));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimensionPixelSize(R.dimen.font_28px));
        dialog.setCustomView(editText);
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
                doJoinRequest(classid,editText.getText().toString().trim());
            }
        });
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void checkJoinState(final String classid,final boolean needVerify){
        showDialogOnUiThread();
        CriteriaStudents criteria=new CriteriaStudents();
        criteria.roles=new String[]{"ClassStudent"};
        CriteriaStudentsDoc doc=new CriteriaStudentsDoc();
        doc.id=classid;
        doc.subtype="PrivateClass";
        criteria.docs=new CriteriaStudentsDoc[]{doc};
        LessonDataManager.getClasses(context,criteria , new APIServiceCallback<Students>() {
            @Override
            public void onSuccess(Students object) {
                cancelDialogOnUiThread();
                if(object!=null){
                    if(!ArrayUtil.isEmpty(object.classes)){
                        showToastOnUiThread("您已是该班成员");
                    }else if(!ArrayUtil.isEmpty(object.enrollments)){
                        showToastOnUiThread("您已提交申请，请等待班主任确认");
                    }else {
                        xjsclasshome(classid,needVerify);
                    }
                }else {
                    showToastOnUiThread("解析失败");
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelDialogOnUiThread();
                showToastOnUiThread(errorMessage);
            }
        });
    }

    private void showToastOnUiThread(final String msg){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToast(context,msg);
            }
        });
    }

    private void showToastOnUiThread(final int msg){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToast(context,msg);
            }
        });
    }

    private void showDialogOnUiThread(){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((IDialogMethod)context).showProgress(true);
            }
        });
    }

    private void cancelDialogOnUiThread(){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((IDialogMethod)context).cancelProgress();
            }
        });
    }
}
