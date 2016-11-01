package com.benyuan.xiaojs.common.xf_foundation;

import android.text.TextUtils;

/**
 * Created by maxiaobao on 2016/10/28.
 */

public class Errors {

    // Error not specified.
    public static final String NO_ERROR = "OK";

    //
    // Illegal request or invocation.
    //
    public static final String ILLEGAL_CALL = "0x00000001";

    //
    // Feature not implemented.
    //
    public static final String NOT_IMPLEMENTED = "0x00000002";

    //
    // Server internal error.
    //
    public static final String SERVER_ERROR = "0x00000003";

    //
    // Invalid CSRF token.
    //
    public static final String INVALID_CSRF = "0x00000004";

    //
    // Invalid operation.
    //
    public static final String INVALID_OPERATION = "0x00000005";

    //
    // Unrecognized doc type.
    //
    public static final String TYPE_NOT_FOUND = "0x00000006";

    //
    // Document not found.
    //
    public static final String DOC_NOT_FOUND = "0x00000007";

    //
    // Document already exists.
    //
    public static final String DOC_ALREADY_EXISTS = "0x00000008";

    //
    // Operation timeout.
    //
    public static final String OPERATION_TIMEOUT = "0x00000009";

    //
    // The specific client application is not supported anymore.
    //
    public static final String NOT_SUPPORTED = "0x00000010";


    //----------------------------------------------------------------------------
    // Bad parameter.
    public static final String BAD_PARAMETER = "0x01000001";

    // The verification code is not match.
    public static final String INVALID_CODE = "0x01000002";

    // The verification code has expired.
    public static final String BAD_CODE = "0x01000003";

    // Bad request.
    public static final String BAD_REQUEST = "0x01000004";


    //endregion

    //region Authentication & Authorization (GSS)

    // Login frozen.
    public static final String LOGIN_FROZEN = "0x10000001";

    // Invalid credentials.
    public static final String INVALID_CREDENTIAL = "0x10000002";

    // Unrecognized benefit role.
    public static final String ROLE_NOT_FOUND = "0x10000003";

    // Benefit or benefit subscription not found.
    public static final String BENEFIT_NOT_FOUND = "0x10000004";

    // Unauthorized access.
    public static final String UNAUTHORIZED = "0x11000001";

    // Unauthorized access (UGC).
    public static final String DENIED_UGC_OWNER = "0x11000002";

    //endregion




    public static String getInternalErrorMessage(String errorCode){
        String errorMessage = "未知错误";

        if (TextUtils.isEmpty(errorCode)){
            return errorMessage;
        }

        switch (errorCode){
            case Errors.NO_ERROR:
                errorMessage = "未指定错误";
                break;
            case Errors.ILLEGAL_CALL:
                errorMessage = "非法请求或调用";
                break;
            case Errors.NOT_IMPLEMENTED:
                errorMessage = "功能没有实现";
                break;
            case Errors.SERVER_ERROR:
                errorMessage = "服务器内部错误";
                break;
            case Errors.INVALID_CSRF:
                errorMessage = "无效的CSRF令牌";
                break;
            case Errors.INVALID_OPERATION:
                errorMessage = "无效的操作";
                break;
            case Errors.TYPE_NOT_FOUND:
                errorMessage = "类型没被找到";
                break;
            case Errors.DOC_NOT_FOUND:
                errorMessage = "文档没被找到";
                break;
            case Errors.DOC_ALREADY_EXISTS:
                errorMessage = "文档已存在";
                break;
            case Errors.OPERATION_TIMEOUT:
                errorMessage = "操作超时";
                break;
            case Errors.NOT_SUPPORTED:
                errorMessage = "不支持该客户端";
                break;
            case Errors.BAD_PARAMETER:
                errorMessage = "参数不正确";
                break;
            case Errors.INVALID_CODE:
                errorMessage = "验证码不匹配";
                break;
            case Errors.BAD_CODE:
                errorMessage = "验证码已过期";
                break;
            case Errors.BAD_REQUEST:
                errorMessage = "错误请求";
                break;
            case Errors.LOGIN_FROZEN:
                errorMessage = "登录被冻结";
                break;
            case Errors.INVALID_CREDENTIAL:
                errorMessage = "无效的凭证";
                break;
            case Errors.ROLE_NOT_FOUND:
                errorMessage = "未被认证的角色";
                break;
            case Errors.BENEFIT_NOT_FOUND:
                errorMessage = "没有找到利益详情";
                break;
            case Errors.UNAUTHORIZED:
                errorMessage = "未经授权的访问";
                break;
            case Errors.DENIED_UGC_OWNER:
                errorMessage = "未经授权的访问(UGC)";
                break;
            default:
                break;
        }
        return errorCode;
    }


}
