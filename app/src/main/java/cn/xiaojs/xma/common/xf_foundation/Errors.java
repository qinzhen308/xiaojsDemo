package cn.xiaojs.xma.common.xf_foundation;

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

    // Bad session.
    public static final String BAD_SESSION = "0x01000005";

    // The specific API is not served by a gateway.
    public static final String BAD_GATEWAY = "0x01000006";

    //endregion



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

    // Unauthorized access.
    public static final String ACCESS_VIOLATION = "0x11000003";


    //region Settlements & Transactions
    //
    // Low balance or frozen.
    public static final String COFFER_NOT_READY = "0x20000001";

    // A special error that pauses transaction processing.
    public static final String BREAK_POINT = "0x20000002";

    // Payment not completed.
    public static final String PAYMENT_FAILED = "0x20000003";

    // Alipay binding not found.
    public static final String BINDING_NOT_FOUND = "0x20000004";

    // Bad subtotal or specific promotion rule expired.
    public static final String BAD_SUBTOTAL = "0x20000005";

    // Quota limit.
    public static final String QUOTA_LIMIT = "0x20000006";

    // Enrollment forbidden.
    public static final String NO_ENROLL = "0x20000007";

    // Quota is incorrect.
    public static final String QUOTA_INCONSISTENCY = "0x20000008";

    //endregion


    ///////////////////////////////////////////////////////////////////////////////////////////////

    //region XPE (0xA0000000 ~ )

    //
    // Process already in execution.
    public static final String PROCESS_RUNNING = "0xA0000001";

    // Process not found.
    public static final String PROCESS_NOT_FOUND = "0xA0000002";

    // Process is not runnable.
    public static final String PROCESS_NOT_RUNNABLE = "0xA0000003";

    // Interrupted process was not loaded to continue execution.
    public static final String INTERRUPTED_NOT_CONTINUE = "0xA0000004";

    // The specific model is not process-driven.
    public static final String MODEL_NOT_READY = "0xA0000005";

    // Event already registered.
    public static final String EVENT_ALREADY_REGISTERED = "0xA0000006";

    // Process context not available.
    public static final String CONTEXT_NOT_AVAILABLE = "0xA0000007";

    // Process instance not available.
    public static final String INSTANCE_NOT_AVAILABLLE = "0xA0000008";

    // Process instance not available.
    public static final String EVENT_NOT_REGISTERED = "0xA0000009";


    //
    // MSP not ready.
    public static final String MSP_NOT_READY = "0xA000000A";

    // Assignee not found.
    public static final String ASSIGNEE_NOT_FOUND = "0xA1000001";

    // Illegal response due to it is no longer required.
    public static final String RESPONSE_NOT_REQUIRED = "0xA1000002";

    // Human process not found.
    public static final String HUMAN_PROCESS_NOT_FOUND = "0xA1000003";

    // Unknown human action.
    public static final String UNKNOWN_HUMAN_ACTION = "0xA1000004";

    // Action not found.
    public static final String ACTION_NOT_FOUND = "0xA1000005";

    //endregion

}
