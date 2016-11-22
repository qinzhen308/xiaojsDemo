package com.benyuan.xiaojs.common.xf_foundation;

import android.text.TextUtils;

/**
 * Created by maxiaobao on 2016/11/7.
 */

public class ErrorPrompts {

    public static String cancelLessonPrompt(String errorCode) {

        String errorMessage = "取消失败";


        return errorMessage;
    }


    public static String editLessonPrompt(String errorCode) {

        String errorMessage = "修改直播课失败";


        return errorMessage;
    }

    public static String getLessonHomepagePrompt(String errorCode) {

        String errorMessage = "获取直播课主页失败";


        return errorMessage;
    }

    public static String getCoverUpTokenPrompt(String errorCode) {

        String errorMessage = "获取上传封面token失败";


        return errorMessage;
    }

    public static String uploadAvatorPrompt(String errorCode) {

        String errorMessage = "上传头像失败";


        return errorMessage;
    }

    public static String getProfilePrompt(String errorCode) {

        String errorMessage = "获取个人资料失败";


        return errorMessage;
    }


    public static String editProfilePrompt(String errorCode) {

        String errorMessage = "编辑个人资料失败";


        return errorMessage;
    }

    public static String getAvatarUpTokenPrompt(String errorCode) {

        String errorMessage = "获取上传头像token失败";


        return errorMessage;
    }

    public static String enrollLessonPrompt(String errorCode) {

        String errorMessage = "报名失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)
                || errorCode.equals(Errors.INVALID_OPERATION)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.BAD_SESSION)) {
            errorMessage = "bad session";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)){
            errorMessage = "参数错误";
        } else if(errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
            errorMessage = "已报名该课程";
        } else if(errorCode.equals(Errors.DOC_NOT_FOUND)) {
            errorMessage = "用户不存在";
        } else if(errorCode.equals(Errors.QUOTA_LIMIT)) {
            errorMessage = "名额已满";
        } else if(errorCode.equals(Errors.NO_ENROLL)) {
            errorMessage = "报名已截止";
        } else if(errorCode.equals(Errors.BAD_SUBTOTAL)) {
            errorMessage = "折扣或者促销已过期";
        }

        return errorMessage;
    }


    public static String confirmLessonEnrollmentPrompt(String errorCode) {

        String errorMessage = "确认报名失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)
                || errorCode.equals(Errors.INVALID_OPERATION)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.BAD_SESSION)) {
            errorMessage = "bad session";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)){
            errorMessage = "参数错误";
        } else if(errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
            errorMessage = "已报名该课程";
        } else if(errorCode.equals(Errors.DOC_NOT_FOUND)) {
            errorMessage = "用户不存在";
        } else if(errorCode.equals(Errors.QUOTA_LIMIT)) {
            errorMessage = "名额已满";
        } else if(errorCode.equals(Errors.NO_ENROLL)) {
            errorMessage = "报名已截止";
        }

        return errorMessage;
    }

    public static String ignoreNotificationsPrompt(String errorCode) {

        String errorMessage = "忽略消息失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.BAD_SESSION)) {
            errorMessage = "bad session";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)){
            errorMessage = "参数错误";
        }
        return errorMessage;
    }


    public static String deleteNotificationPrompt(String errorCode) {

        String errorMessage = "删除消息失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.BAD_SESSION)) {
            errorMessage = "bad session";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)){
            errorMessage = "参数错误";
        }
        return errorMessage;
    }

    public static String getNotificationsPrompt(String errorCode) {

        String errorMessage = "获取消息失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.BAD_SESSION)) {
            errorMessage = "bad session";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)){
            errorMessage = "参数错误";
        }
        return errorMessage;
    }

    public static String getNotificationsOverviewPrompt(String errorCode) {

        String errorMessage = "获取消息类别失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.BAD_SESSION)) {
            errorMessage = "bad session";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)){
            errorMessage = "参数错误";
        }
        return errorMessage;
    }

    public static String getLessonDetailsPrompt(String errorCode) {

        String errorMessage = "获取课程详情失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.BAD_SESSION)) {
            errorMessage = "bad session";
        }
        return errorMessage;
    }

    public static String getEnrolledLessonsPrompt(String errorCode) {

        String errorMessage = "获取已报名课程失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.BAD_SESSION)) {
            errorMessage = "bad session";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)) {
            errorMessage = "参数错误";
        }
        return errorMessage;
    }


    public static String cancelLessonOnShelvesPrompt(String errorCode) {

        String errorMessage = "取消上架失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)
                || errorCode.equals(Errors.DOC_NOT_FOUND)
                || errorCode.equals(Errors.ACTION_NOT_FOUND)
                || errorCode.equals(Errors.INVALID_OPERATION)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.BAD_SESSION)) {
            errorMessage = "bad session";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)) {
            errorMessage = "参数错误";
        } else if (errorCode.equals(Errors.ACCESS_VIOLATION)) {
            errorMessage = "您不能取消上架";
        }
        return errorMessage;
    }


    public static String getSubjectPrompt(String errorCode) {

        String errorMessage = "获取能力失败";

//        if (TextUtils.isEmpty(errorCode)
//                || errorCode.equals(Errors.NO_ERROR)
//                || errorCode.equals(Errors.SERVER_ERROR)
//                || errorCode.equals(Errors.UNAUTHORIZED)
//                || errorCode.equals(Errors.ILLEGAL_CALL)) {
//            return errorMessage;
//        }
        return errorMessage;
    }

    public static String putLessonOnShelvesPrompt(String errorCode) {

        String errorMessage = "上架失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)
                || errorCode.equals(Errors.DOC_NOT_FOUND)
                || errorCode.equals(Errors.ACTION_NOT_FOUND)
                || errorCode.equals(Errors.INVALID_OPERATION)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.BAD_SESSION)) {
            errorMessage = "bad session";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)) {
            errorMessage = "参数错误";
        } else if (errorCode.equals(Errors.ACCESS_VIOLATION)) {
            errorMessage = "您不能上架该课程";
        }
        return errorMessage;
    }


    public static String getLessonPrompt(String errorCode) {

        String errorMessage = "获取课程失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)
                || errorCode.equals(Errors.MSP_NOT_READY)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.BAD_SESSION)) {
            errorMessage = "bad session";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)) {
            errorMessage = "参数错误";
        }

        return errorMessage;
    }


    public static String createLessonPrompt(String errorCode) {

        String errorMessage = "创建课程失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)
                || errorCode.equals(Errors.MSP_NOT_READY)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.BAD_SESSION)) {
            errorMessage = "bad session";
        } else if (errorCode.equals(Errors.ACCESS_VIOLATION)) {
            errorMessage = "请先声明教学能力";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)) {
            errorMessage = "参数错误";
        }

        return errorMessage;
    }


    public static String claimCompetencyPrompt(String errorCode) {

        String errorMessage = "声明教学能力失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
            errorMessage = "您已声明该能力";
        } else if (errorCode.equals(Errors.BAD_SESSION)) {
            errorMessage = "bad session";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)) {
            errorMessage = "参数错误";
        }

        return errorMessage;
    }


    public static String registerPrompt(String errorCode) {

        String errorMessage = "注册失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
            errorMessage = "手机号已被注册";
        } else if (errorCode.equals(Errors.BAD_CODE)) {
            errorMessage = "验证码已过期，请重新发送";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)) {
            errorMessage = "参数错误";
        } else if (errorCode.equals(Errors.INVALID_CODE)) {
            errorMessage = "验证码错误";
        }

        return errorMessage;
    }


    public static String sendCodePrompt(String errorCode) {

        String errorMessage = "发送验证码失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
            errorMessage = "手机号已被注册";
        } else if (errorCode.equals(Errors.BAD_REQUEST)) {
            errorMessage = "等60秒后，再发送";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)) {
            errorMessage = "参数错误";
        }

        return errorMessage;
    }


    public static String logoutPrompt(String errorCode) {

        String errorMessage = "退出登陆失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.ILLEGAL_CALL)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.BAD_SESSION)) {
            errorMessage = "bad session";
        }

        return errorMessage;
    }

    public static String loginPrompt(String errorCode) {

        String errorMessage = "登陆失败";

        if (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.INVALID_OPERATION)
                || errorCode.equals(Errors.ILLEGAL_CALL)) {
            return errorMessage;
        } else if (errorCode.equals(Errors.NOT_IMPLEMENTED)) {
            errorMessage = "目前不支持机构用户登陆";
        } else if (errorCode.equals(Errors.INVALID_CREDENTIAL)) {
            errorMessage = "手机号或密码错误";
        } else if (errorCode.equals(Errors.ACCESS_VIOLATION)) {
            errorMessage = "此账户禁止登陆";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)) {
            errorMessage = "参数错误";
        }

        /**
         *
         * 0x01000004
         * 405 SharedErrs.InvalidOperation The current session is not allowed to invoke this API.
         */


        return errorMessage;
    }


    public static String getHomeDataPrompt(String errorCode) {

        String errorMessage = "请求失败";
        return errorMessage;
    }

    public static String validateCodePrompt(String errorCode) {

        String errorMessage = "请求失败";
        return errorMessage;
    }

//    private static String getInternalErrorMessage(String errorCode){
//        String errorMessage = "请求失败";
//
//        if (TextUtils.isEmpty(errorCode)){
//            return errorMessage;
//        }else if (errorCode.equals(Errors.NO_ERROR)) {
//            errorMessage = "请求失败";
//        }else if (errorCode.equals(Errors.ILLEGAL_CALL)) {
//            errorMessage = "非法请求或调用";
//        }else if (errorCode.equals(Errors.NOT_IMPLEMENTED)) {
//            errorMessage = "功能没有实现";
//        }else if (errorCode.equals(Errors.SERVER_ERROR)) {
//            errorMessage = "服务器内部错误";
//        }else if (errorCode.equals(Errors.INVALID_CSRF)) {
//            errorMessage = "无效的CSRF令牌";
//        }else if (errorCode.equals(Errors.INVALID_OPERATION)) {
//            errorMessage = "无效的操作";
//        }else if (errorCode.equals(Errors.TYPE_NOT_FOUND)) {
//            errorMessage = "类型没被找到";
//        }else if (errorCode.equals(Errors.DOC_NOT_FOUND)) {
//            errorMessage = "文档没被找到";
//        }else if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
//            errorMessage = "文档已存在";
//        }else if (errorCode.equals(Errors.OPERATION_TIMEOUT)) {
//            errorMessage = "操作超时";
//        }else if (errorCode.equals(Errors.NOT_SUPPORTED)) {
//            errorMessage = "不支持该客户端";
//        }else if (errorCode.equals(Errors.BAD_PARAMETER)) {
//            errorMessage = "参数不正确";
//        }else if (errorCode.equals(Errors.INVALID_CODE)) {
//            errorMessage = "验证码不匹配";
//        }else if (errorCode.equals(Errors.BAD_CODE)) {
//            errorMessage = "验证码已过期";
//        }else if (errorCode.equals(Errors.BAD_REQUEST)) {
//            errorMessage = "错误请求";
//        }else if (errorCode.equals(Errors.LOGIN_FROZEN)) {
//            errorMessage = "登录被冻结";
//        }else if (errorCode.equals(Errors.INVALID_CREDENTIAL)) {
//            errorMessage = "无效的凭证";
//        }else if (errorCode.equals(Errors.ROLE_NOT_FOUND)) {
//            errorMessage = "未被认证的角色";
//        }else if (errorCode.equals(Errors.BENEFIT_NOT_FOUND)) {
//            errorMessage = "没有找到利益详情";
//        }else if (errorCode.equals(Errors.UNAUTHORIZED)) {
//            errorMessage = "未经授权的访问";
//        }else if (errorCode.equals(Errors.DENIED_UGC_OWNER)) {
//            errorMessage = "未经授权的访问(UGC)";
//        }else {
//            errorMessage = "请求失败";
//        }
//
//
//        return errorMessage;
//    }

}
