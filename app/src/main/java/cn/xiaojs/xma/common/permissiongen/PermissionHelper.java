package cn.xiaojs.xma.common.permissiongen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.CommonDialog;

/**
 * Created by Administrator on 2017/7/6.
 */

public class PermissionHelper {

    public static void onRequestPermissionsResult(Fragment fragment, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        onRequestPermissionsResult(fragment.getActivity(),requestCode,permissions,grantResults,null);
    }
    public static void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        onRequestPermissionsResult(activity,requestCode,permissions,grantResults,null);
    }

    public static void onRequestPermissionsResult(Fragment fragment, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults,Response response) {
        onRequestPermissionsResult(fragment.getActivity(),requestCode,permissions,grantResults,response);
    }


    public static void onRequestPermissionsResult(Activity activity,int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults,Response response) {
        boolean hasAllGranted = true;
        for (int i = 0; i < grantResults.length; ++i) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false;
                //在用户已经拒绝授权的情况下，如果shouldShowRequestPermissionRationale返回false则
                // 可以推断出用户选择了“不在提示”选项，在这种情况下需要引导用户至设置页手动授权
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                    //解释原因，并且引导用户至设置页手动授权
                    showRationaleDialog(activity);
                    break;
                } else {
                    //权限请求失败，但未选中“不再提示”选项
                }
                break;
            }
        }

        if(hasAllGranted&&response!=null){//本次申请成功
            response.next();
        }

    }

    public interface Response{
        void next();
    }

    public static void showRationaleDialog(Fragment fragment){
        showRationaleDialog(fragment,null);
    }

    public static void showRationaleDialog(final Activity activity){
        showRationaleDialog(activity,null);
    }

    public static void showRationaleDialog(Fragment fragment,String tips){
        showRationaleDialog(fragment.getActivity(),tips);
    }

    public static void showRationaleDialog(final Activity activity,String tips){
        final CommonDialog dialog=new CommonDialog(activity);
        dialog.setDesc(TextUtils.isEmpty(tips)?activity.getString(R.string.permission_rationale_tip):tips);
        dialog.setRightBtnText(R.string.go_to_authorization);
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                //引导用户至设置页手动授权
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
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
}
