package cn.xiaojs.xma.common.xf_foundation.schemas;

/**
 * Created by maxiaobao on 2016/10/29.
 */

public class Security {

    /**
     * Defines the credential type, effective on XA login request and ignored by XBS.
     */
    public static class CredentialType {
        public static final int PERSION = 1;
        public static final int ORGANIZATION = 2;
    }


    /**
     * Defines the verification methods.
     */
    public static class VerifyMethod {
        public static final int NOT_SPECIFIED = 0;
        public static final int SMS_4_REGISTRATION = 1;
        public static final int SMS_4_PASSWORD_RESET = 2;
        public static final int SMS_4_MOBILE_RESET = 3;

        public static final int SMS4OrganizationRegistration = 4;
        public static final int SMS4OrganizationPasswordReset = 5;

        public static final int SMS4WeChatAssociation = 6;
        public static final int SMS4QQAssociation = 7;
    }


}
