package com.benyuan.xiaojs.data.api.service;

import android.text.TextUtils;

import com.benyuan.xiaojs.common.xf_foundation.Errors;

/**
 * Created by maxiaobao on 2016/11/7.
 */

public class ErrorPrompts {

    private static boolean commonFailure(String errorCode) {
        return (TextUtils.isEmpty(errorCode)
                || errorCode.equals(Errors.NO_ERROR)
                || errorCode.equals(Errors.SERVER_ERROR)
                || errorCode.equals(Errors.ILLEGAL_CALL));

    }


    public static String getErrorMessage(int apiType, String errorCode) {

        if (errorCode.equals(Errors.UNAUTHORIZED) || errorCode.equals(Errors.BAD_SESSION)) {
            return "请重新登陆";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)) {
            return "参数错误";
        }

        String errorMessage = "请求失败";

        switch (apiType) {
            case APIType.CLAIM_COMPETENCY:

                if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
                    errorMessage = "您已声明该能力";
                } else {
                    errorMessage = "声明教学能力失败";
                }

                break;
            case APIType.EDIT_PROFILE:
                errorMessage = "编辑个人资料失败";
                break;
            case APIType.GET_CENTER_DATA:
                errorMessage = "获取个人中心数据失败";
                break;
            case APIType.GET_PROFILE:
                errorMessage = "获取个人资料失败";
                break;
            case APIType.GET_UPTOKEN:
                errorMessage = "获取上传头像token失败";
                break;
            case APIType.REGISTER:

                if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
                    errorMessage = "手机号已被注册";
                } else if (errorCode.equals(Errors.BAD_CODE)) {
                    errorMessage = "验证码已过期，请重新发送";
                } else if (errorCode.equals(Errors.INVALID_CODE)) {
                    errorMessage = "验证码错误";
                } else {
                    errorMessage = "注册失败";
                }
                break;
            case APIType.CANCEL_LESSON:
                errorMessage = "取消失败";
                break;
            case APIType.CANCEL_LESSON_ON_SHELVES:

                if (errorCode.equals(Errors.ACCESS_VIOLATION)) {
                    errorMessage = "您不能取消上架";
                } else {
                    errorMessage = "取消上架失败";
                }

                break;
            case APIType.CONFIRM_LESSON_ENROLLMENT:

                if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
                    errorMessage = "已报名该课程";
                } else if (errorCode.equals(Errors.DOC_NOT_FOUND)) {
                    errorMessage = "用户不存在";
                } else if (errorCode.equals(Errors.QUOTA_LIMIT)) {
                    errorMessage = "名额已满";
                } else if (errorCode.equals(Errors.NO_ENROLL)) {
                    errorMessage = "报名已截止";
                } else {
                    errorMessage = "确认报名失败";
                }

                break;
            case APIType.CREATE_LESSON:

                if (errorCode.equals(Errors.ACCESS_VIOLATION)) {
                    errorMessage = "请先声明教学能力";
                } else if (errorCode.equals(Errors.BAD_PARAMETER)) {
                    errorMessage = "参数错误";
                } else {
                    errorMessage = "创建课程失败";
                }

                break;
            case APIType.EDIT_LESSON:
                errorMessage = "修改直播课失败";
                break;
            case APIType.ENROLL_LESSON:

                if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
                    errorMessage = "已报名该课程";
                } else if (errorCode.equals(Errors.DOC_NOT_FOUND)) {
                    errorMessage = "用户不存在";
                } else if (errorCode.equals(Errors.QUOTA_LIMIT)) {
                    errorMessage = "名额已满";
                } else if (errorCode.equals(Errors.NO_ENROLL)) {
                    errorMessage = "报名已截止";
                } else if (errorCode.equals(Errors.BAD_SUBTOTAL)) {
                    errorMessage = "折扣或者促销已过期";
                } else {
                    errorMessage = "报名失败";
                }

                break;
            case APIType.GET_ENROLLED_LESSONS:
                errorMessage = "获取已报名课程失败";
                break;
            case APIType.GET_LESSON_COVER_UPTOKEN:
                errorMessage = "获取上传封面token失败";
                break;
            case APIType.GET_LESSON_DATA:
                errorMessage = "获取课程详情失败";
                break;
            case APIType.GET_LESSON_DETAILS:
                errorMessage = "获取直播课主页失败";
                break;
            case APIType.GET_LESSONS:
                errorMessage = "获取课程失败";
                break;
            case APIType.PUT_LESSON_ON_SHELVES:

                if (errorCode.equals(Errors.ACCESS_VIOLATION)) {
                    errorMessage = "您不能上架该课程";
                } else {
                    errorMessage = "上架失败";
                }

                break;
            case APIType.TOGGLE_ACCESS_TO_LESSON:
                errorMessage = "公开／取消公开失败";
                break;
            case APIType.GET_SUBJECT:
                errorMessage = "获取能力失败";
                break;
            case APIType.DELETE_NOTIFICATION:
                errorMessage = "删除消息失败";
                break;
            case APIType.GET_NOTIFICATIONS_OVERVIEW:
                errorMessage = "获取消息类别失败";
                break;
            case APIType.GET_NOTIFICATIONS:
                errorMessage = "获取消息失败";
                break;
            case APIType.IGNORE_NOTIFICATIONS:
                errorMessage = "忽略消息失败";
                break;
            case APIType.LOGIN:

                if (errorCode.equals(Errors.NOT_IMPLEMENTED)) {
                    errorMessage = "目前不支持机构用户登陆";
                } else if (errorCode.equals(Errors.INVALID_CREDENTIAL)) {
                    errorMessage = "手机号或密码错误";
                } else if (errorCode.equals(Errors.ACCESS_VIOLATION)) {
                    errorMessage = "此账户禁止登陆";
                } else {
                    errorMessage = "登陆失败";
                }

                break;
            case APIType.LOGOUT:
                errorMessage = "退出登陆失败";
                break;
            case APIType.VERIFY_MOBILE:

                if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
                    errorMessage = "手机号已被注册";
                } else if (errorCode.equals(Errors.BAD_REQUEST)) {
                    errorMessage = "等60秒后，再发送";
                } else {
                    errorMessage = "发送验证码失败";
                }
                break;
            case APIType.VALIDATE_CODE:
            case APIType.GET_HOME_DATA:
            default:
                break;
        }


        return errorMessage;

    }


}
