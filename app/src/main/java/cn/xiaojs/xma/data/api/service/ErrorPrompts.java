package cn.xiaojs.xma.data.api.service;

import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.model.Error;

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

        if (errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.BAD_SESSION)
                || errorCode.equals(Errors.BAD_CSRF)) {
            return "登录过期，请重新登录";
        } else if (errorCode.equals(Errors.BAD_PARAMETER)) {
            return "参数错误";
        }

        String errorMessage = "请求失败";

        switch (apiType) {
            case APIType.GET_PUBLIC_HOME:
            case APIType.GET_PRIVATE_HOME:
                errorMessage = "加载失败";
                break;
            case APIType.BEGIN_CLASS:
                if (errorCode.equals(Errors.ACCESS_VIOLATION)){
                    errorMessage = "你没有权限";
                } else if (errorCode.equals(Errors.OPERATION_TIMEOUT)){
                    errorMessage = "操作超时";
                } else {
                    errorMessage = "开始课程失败";
                }
                break;
            case APIType.BOOT_SESSION:
                if (errorCode.equals(Errors.CLASS_NOT_READY)){
                    errorMessage = "教室没准备好或着无法访问";
                } else if (errorCode.equals(Errors.CLASS_NOT_INVOLVED)){
                    errorMessage = "Class not involved";
                } else if (errorCode.equals(Errors.BAD_GATEWAY)){
                    errorMessage = "Bad gateway";
                } else {
                    errorMessage = "进入教室失败";
                }
                break;
            case APIType.GENERATE_TICKET:
                break;
            case APIType.UNFOLLOW_CONTACT:
                if (errorCode.equals(Errors.INVALID_OPERATION)) {
                    errorMessage = "您未关注此人";
                } else {
                    errorMessage = "取消关注失败";
                }
                break;
            case APIType.REPLAY_2_REPLY:
                errorMessage = "回复失败";
                break;
            case APIType.REPLAY_COMMENT:
                errorMessage = "回复失败";
                break;
            case APIType.POST_ACTIVITY:
                errorMessage = "发布动态失败";
                break;
            case APIType.LIKE_ACTIVITY:
                if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
                    errorMessage = "您已赞过";
                } else {
                    errorMessage = "点赞失败";
                }
                break;
            case APIType.GET_UPDATES:
                errorMessage = "获取动态更新失败";
                break;
            case APIType.GET_LINKED_RECORDS:
                errorMessage = "获取赞失败";
                break;
            case APIType.GET_CONTACTS:
                errorMessage = "获取联系人失败";
                break;
            case APIType.GET_COMMENTS:
                errorMessage = "获取评论失败";
                break;
            case APIType.GET_ACTIVITY_DETAILS:
                errorMessage = "获取详情失败";
                break;
            case APIType.GET_ACTIVITIES:
                errorMessage = "获取动态失败";
                break;
            case APIType.FOLLOW_CONTACT:
                if (errorCode.equals(Errors.INVALID_OPERATION)) {
                    errorMessage = "您已关注";
                } else {
                    errorMessage = "关注失败";
                }
                break;
            case APIType.COMMENT_ACTIVITY:
                errorMessage = "评论失败";
                break;
            case APIType.ADD_CONTACT_GROUP:
                if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
                    errorMessage = "组已存在";
                } else {
                    errorMessage = "新建组失败";
                }
                break;
            case APIType.HAVE_PROVILEGES:
                errorMessage = "查询特权失败";
                break;
            case APIType.CLAIM_COMPETENCY:

                if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
                    errorMessage = "您已声明该能力";
                } else {
                    errorMessage = "声明教学能力失败";
                }

                break;
            case APIType.EDIT_PROFILE:
                errorMessage = "修改失败";
                break;
            case APIType.GET_CENTER_DATA:
                errorMessage = "加载失败";
                break;
            case APIType.GET_PROFILE:
                errorMessage = "加载失败";
                break;
            case APIType.GET_UPTOKEN:
                errorMessage = "获取上传token失败";
                break;
            case APIType.REGISTER:

                if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
                    errorMessage = "该手机号已注册，请直接登录";
                } else if (errorCode.equals(Errors.BAD_CODE)) {
                    errorMessage = "验证码已过期";
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
                errorMessage = "加载失败";
                break;
//            case APIType.GET_LESSON_COVER_UPTOKEN:
//                errorMessage = "获取上传封面token失败";
//                break;
            case APIType.GET_LESSON_DATA:
                errorMessage = "加载失败";
                break;
            case APIType.GET_LESSON_DETAILS:
                errorMessage = "获取直播课主页失败";
                break;
            case APIType.GET_LESSONS:
                errorMessage = "加载失败";
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
                } else if(errorCode.equals(Errors.DOC_NOT_FOUND)) {
                    errorMessage = "账号不存在";
                } else if (errorCode.equals(Errors.ACCESS_VIOLATION)) {
                    errorMessage = "此账户禁止登陆";
                } else {
                    errorMessage = "登录失败，请检查账号或密码";
                }

                break;
            case APIType.LOGOUT:
                errorMessage = "退出失败";
                break;
            case APIType.VERIFY_MOBILE:

                if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
                    errorMessage = "该手机号已注册，请直接登录";
                } else if (errorCode.equals(Errors.BAD_REQUEST)) {
                    errorMessage = "请求过于频繁，请稍后再试";
                } else {
                    errorMessage = "验证码发送失败";
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
