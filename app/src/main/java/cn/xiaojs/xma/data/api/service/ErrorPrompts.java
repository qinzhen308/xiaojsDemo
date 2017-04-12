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

        if (TextUtils.isEmpty(errorCode)){
            return "请求失败";
        }

        if (errorCode.equals(Errors.UNAUTHORIZED)
                || errorCode.equals(Errors.BAD_SESSION)
                || errorCode.equals(Errors.BAD_CSRF)) {
            return "登录过期，请重新登录";
        }
//         else if (errorCode.equals(Errors.BAD_PARAMETER)) {
//            return "参数错误";
//        }

        String errorMessage = "请求失败";

        switch (apiType) {
            case APIType.SHARE_DOCUMENT:
                errorMessage = "分享失败";
                break;
            case APIType.DELETE_DOCUMENT:
                errorMessage = "删除失败";
                break;
            case APIType.REQUEST_VERIFICATION:
                errorMessage = "提交认证失败";
                break;
            case APIType.BEGIN_CLASS:
                if (errorCode.equals(Errors.ACCESS_VIOLATION)) {
                    errorMessage = "你没有权限";
                } else if (errorCode.equals(Errors.OPERATION_TIMEOUT)) {
                    errorMessage = "操作超时";
                } else {
                    errorMessage = "开始课程失败";
                }
                break;
            case APIType.BOOT_SESSION:
                if (errorCode.equals(Errors.CLASS_NOT_READY)) {
                    errorMessage = "教室没准备好或着无法访问";
                } else if (errorCode.equals(Errors.CLASS_NOT_INVOLVED)) {
                    errorMessage = "Class not involved";
                } else if (errorCode.equals(Errors.BAD_GATEWAY)) {
                    errorMessage = "Bad gateway";
                } else {
                    errorMessage = "进入教室失败";
                }
                break;
            case APIType.GENERATE_TICKET:
                break;
            case APIType.UNFOLLOW_CONTACT:
                if (errorCode.equals(Errors.INVALID_OPERATION)) {
                    errorMessage = "没有关注";
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
                errorMessage = "发布失败";
                break;
            case APIType.LIKE_ACTIVITY:
                if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
                    errorMessage = "已赞过";
                } else {
                    errorMessage = "操作失败";
                }
                break;

            case APIType.FOLLOW_CONTACT:
                if (errorCode.equals(Errors.INVALID_OPERATION)) {
                    errorMessage = "已关注";
                } else {
                    errorMessage = "关注失败";
                }
                break;
            case APIType.COMMENT_ACTIVITY:
                errorMessage = "评论失败";
                break;
            case APIType.ADD_CONTACT_GROUP:
                if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
                    errorMessage = "组名已存在";
                } else {
                    errorMessage = "操作失败";
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
                    errorMessage = "取消失败";
                }

                break;
            case APIType.CONFIRM_LESSON_ENROLLMENT:

                if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
                    errorMessage = "你已报名";
                } else if (errorCode.equals(Errors.DOC_NOT_FOUND)) {
                    errorMessage = "该课已下架";
                } else if (errorCode.equals(Errors.QUOTA_LIMIT)) {
                    errorMessage = "报名已满";
                } else if (errorCode.equals(Errors.NO_ENROLL)) {
                    errorMessage = "停止招生";
                } else {
                    errorMessage = "报名失败";
                }

                break;
            case APIType.CREATE_LESSON:

                if (errorCode.equals(Errors.ACCESS_VIOLATION)) {
                    errorMessage = "请先声明教学能力";
                } else if (errorCode.equals(Errors.TEACHER_NOT_INVITED)) {
                    errorMessage = "主讲老师没有被邀请";
                } else if (errorCode.equals(Errors.SUBJECT_NOT_RESOLVED)) {
                    errorMessage = "科目无效";
                } else {
                    errorMessage = "创建失败";
                }

                break;
            case APIType.EDIT_LESSON:
                errorMessage = "保存失败";
                break;
            case APIType.ENROLL_LESSON:

                if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
                    errorMessage = "你已报名";
                } else if (errorCode.equals(Errors.DOC_NOT_FOUND)) {
                    errorMessage = "该课已下架";
                } else if (errorCode.equals(Errors.QUOTA_LIMIT)) {
                    errorMessage = "报名已满";
                } else if (errorCode.equals(Errors.NO_ENROLL)) {
                    errorMessage = "停止报名";
                } else if (errorCode.equals(Errors.BAD_SUBTOTAL)) {
                    errorMessage = "折扣或者促销已过期";
                } else {
                    errorMessage = "报名失败";
                }

                break;
            case APIType.PUT_LESSON_ON_SHELVES:

                if (errorCode.equals(Errors.ACCESS_VIOLATION)) {
                    errorMessage = "您不能上架该课程";
                } else {
                    errorMessage = "操作失败";
                }

                break;
            case APIType.TOGGLE_ACCESS_TO_LESSON:
                errorMessage = "公开／取消公开失败";
                break;
            case APIType.GET_SUBJECT:
                if (errorCode.equals(Errors.DOC_ALREADY_EXISTS)) {
                    errorMessage = "已声明教学能力";
                } else {
                    errorMessage = "声明教学能力失败";
                }

                break;

            case APIType.LOGIN:

                if (errorCode.equals(Errors.NOT_IMPLEMENTED)) {
                    errorMessage = "目前不支持机构用户登陆";
                } else if (errorCode.equals(Errors.DOC_NOT_FOUND)) {
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
            case APIType.CREATE_ORDER:
                errorMessage = "创建订单失败";
                break;
            case APIType.CREATE_PAYMENT_CHARGE:
                errorMessage = "创建第三方支付失败";
                break;
            case APIType.ADD_TO_LIBRARY:
                if (errorCode.equals(Errors.NO_ENOUGH_QUOTA)) {
                    errorMessage = "存储已满";
                } else {
                    errorMessage = "上传文档失败";
                }
                break;
            case APIType.DELETE_NOTIFICATION:
            case APIType.IGNORE_NOTIFICATIONS:
                errorMessage = "操作失败";
                break;
            case APIType.GET_DOCUMENTS:
            case APIType.GET_COMPETENCIES:
            case APIType.GET_SUBJECTS:
            case APIType.GET_UPDATES:
            case APIType.GET_LINKED_RECORDS:
            case APIType.GET_CONTACTS:
            case APIType.GET_COMMENTS:
            case APIType.GET_ACTIVITY_DETAILS:
            case APIType.GET_ACTIVITIES:
            case APIType.GET_CENTER_DATA:
            case APIType.GET_PROFILE:
            case APIType.GET_ENROLLED_LESSONS:
            case APIType.GET_LESSON_DATA:
            case APIType.GET_LESSON_DETAILS:
            case APIType.GET_LESSONS:
            case APIType.GET_NOTIFICATIONS_OVERVIEW:
            case APIType.GET_NOTIFICATIONS:
            case APIType.GET_PUBLIC_HOME:
            case APIType.GET_PRIVATE_HOME:
            case APIType.GET_LIBRARY_OVERVIEW:
                errorMessage = "加载失败";
                break;
            default:
                break;
        }


        return errorMessage;

    }


}
