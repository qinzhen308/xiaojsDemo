package com.benyuan.xiaojs.common.xf_foundation;

import android.text.TextUtils;

/**
 * Created by maxiaobao on 2016/11/7.
 */

public class ErrorPrompts {


    /**
     * 获取课程失败提示
     * @param errorCode
     * @return
     */
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


    /**
     * 创建课程失败提示
     * @param errorCode
     * @return
     */
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


    /**
     * 声明教学能力失败提示
     * @param errorCode
     * @return
     */
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


    /**
     * 注册失败提示
     * @param errorCode
     * @return
     */
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


    /**
     * 发送验证码失败提示
     * @param errorCode
     * @return
     */
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



    /**
     * 推出登陆失败提示
     * @param errorCode
     * @return
     */
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

    /**
     * 登陆失败提示
     * @param errorCode
     * @return
     */
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
        } else if (errorCode.equals(Errors.INVALID_CREDENTIAL) || errorCode.equals(Errors.BAD_PARAMETER)) {
            errorMessage = "用户名或密码错误";
        } else if (errorCode.equals(Errors.ACCESS_VIOLATION)) {
            errorMessage = "此账户禁止登陆";
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

}
