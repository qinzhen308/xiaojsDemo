package cn.xiaojs.xma.common.xf_foundation.schemas;

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

}
