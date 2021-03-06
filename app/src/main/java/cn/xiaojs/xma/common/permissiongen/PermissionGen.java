package cn.xiaojs.xma.common.permissiongen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class PermissionGen {
    private String[] mPermissions;
    private int mRequestCode;
    private Object object;
    Object[] methodParams;

    private PermissionGen(Object object) {
        this.object = object;
    }

    public static PermissionGen with(Activity activity) {
        return new PermissionGen(activity);
    }

    public static PermissionGen with(Fragment fragment) {
        return new PermissionGen(fragment);
    }
    public PermissionGen params(Object... methodArr) {
        methodParams=methodArr;
        return this;
    }

    public PermissionGen permissions(String[] permissions) {
        this.mPermissions = permissions;
        return this;
    }

    public PermissionGen addRequestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    @TargetApi(value = PermissionUtil.Build_VERSION_CODES_M)
    public void request() {
        requestPermissions(object, mRequestCode, mPermissions,methodParams);
    }

    public static void needPermission(Activity activity, int requestCode, String[] permissions) {
        requestPermissions(activity, requestCode, permissions);
    }

    public static void needPermission(Fragment fragment, int requestCode, String[] permissions) {
        requestPermissions(fragment, requestCode, permissions);
    }

    public static void needPermission(Activity activity, int requestCode, String permission) {
        needPermission(activity, requestCode, new String[]{permission});
    }

    public static void needPermission(Fragment fragment, int requestCode, String permission) {
        needPermission(fragment, requestCode, new String[]{permission});
    }

    @TargetApi(value = PermissionUtil.Build_VERSION_CODES_M)
    private static void requestPermissions(Object object, int requestCode, String[] permissions,Object... arr) {
        if (!PermissionUtil.isOverMarshmallow()) {
            doExecuteSuccess(object, requestCode,arr);
            return;
        }
        List<String> deniedPermissions = PermissionUtil.findDeniedPermissions(PermissionUtil.getActivity(object), permissions);

        if (deniedPermissions.size() > 0) {
            if (object instanceof Activity) {
                ((Activity) object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            } else if (object instanceof Fragment) {
                ((Fragment) object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            } else {
                throw new IllegalArgumentException(object.getClass().getName() + " is not supported");
            }
        } else {
            doExecuteSuccess(object, requestCode,arr);
        }
    }


    private static void doExecuteSuccess(Object activity, int requestCode,Object... arr) {
        Method executeMethod = PermissionUtil.findMethodWithRequestCode(activity.getClass(),
                PermissionSuccess.class, requestCode);

        executeMethod(activity, executeMethod,arr);
    }

    private static void doExecuteFail(Object activity, int requestCode) {
        Method executeMethod = PermissionUtil.findMethodWithRequestCode(activity.getClass(),
                PermissionFail.class, requestCode);

        executeMethod(activity, executeMethod);
    }

    private static void doExecuteRationale(Object activity, int requestCode) {
        Method executeMethod = PermissionUtil.findMethodWithRequestCode(activity.getClass(),
                PermissionRationale.class, requestCode);

        executeMethod(activity, executeMethod);
    }

    private static void executeMethod(Object activity, Method executeMethod,Object... arr) {
        if (executeMethod != null) {
            try {
                if (!executeMethod.isAccessible()) {
                    executeMethod.setAccessible(true);
                }
//                Object[] arr = null;
                executeMethod.invoke(activity, arr);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions,
                                                  int[] grantResults) {
        requestResult(activity, requestCode, permissions, grantResults);
    }

    public static void onRequestPermissionsResult(Fragment fragment, int requestCode, String[] permissions,
                                                  int[] grantResults) {
        requestResult(fragment, requestCode, permissions, grantResults);
    }

    private static void requestResult(Object obj, int requestCode, String[] permissions,
                                      int[] grantResults,String... arr) {
        boolean hasNaverAskAgain=false;
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
                if (!ActivityCompat.shouldShowRequestPermissionRationale(obj instanceof Activity?(Activity) obj:((Fragment)obj).getActivity(), permissions[i])) {
                    hasNaverAskAgain=true;
                }
            }
        }

        if (deniedPermissions.size() > 0) {
            if(hasNaverAskAgain){
                doExecuteRationale(obj, requestCode);
            }else {
                doExecuteFail(obj, requestCode);
            }
        } else {
            doExecuteSuccess(obj, requestCode);
        }
    }



}
