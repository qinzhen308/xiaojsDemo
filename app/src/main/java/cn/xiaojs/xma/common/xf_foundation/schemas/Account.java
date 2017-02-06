package cn.xiaojs.xma.common.xf_foundation.schemas;

import cn.xiaojs.xma.common.xf_foundation.Constants;

/**
 * Created by maxiaobao on 2016/12/28.
 */

public class Account {

    /**
     * Define account type name
     */
    public class TypeName {
        public static final String PERSION = "Person";
        public static final String ORGANIZATION = "Organization";
        public static final String STAND_ALONE_LESSON="StandaloneLesson";
    }

    public class Sex{
        public static final String MALE = "true";
        public static final String FEMALE = "false";
    }

    /**
     * Returns the URL to a sized avatar for specific account.
     * @param accountId 用户的ID
     * @param size
     * @return
     */
    public static String getAvatar(String accountId, int size) {

        return new StringBuilder(Constants.XCFSUrl)
                .append("/")
                .append(Collaboration.UploadTokenType.AVATAR)
                .append("-")
                .append(accountId)
                .append("?imageView2/3/w/")
                .append(size)
                .toString();
    }

}
