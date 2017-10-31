package cn.xiaojs.xma.ui.conversation2;

/**
 * Created by maxiaobao on 2017/10/31.
 */

public class ConversationType {

    public static final int UNDEFINED = -1;

    public static final int PRIVATE_CLASS = 1;
    public static final int PERSON = 2;
    public static final int ORGANIZATION = 3;


    public static final int TIME_TABLE = 4;                                  //我的课表


    public static int getConversationType(String typeName) {
        if (TypeName.TIME_TABLE.equals(typeName)) {
            return TIME_TABLE;
        } else if (TypeName.PRIVATE_CLASS.equals(typeName)) {
            return PRIVATE_CLASS;
        } else if (TypeName.PERSON.equals(typeName)) {
            return PERSON;
        } else if (TypeName.ORGANIZATION.equals(typeName)) {
            return ORGANIZATION;
        } else {
            return UNDEFINED;
        }
    }



    public static class TypeName {
        public static final String TIME_TABLE = "TimeTable";
        public static final String PRIVATE_CLASS = "PrivateClass";
        public static final String PERSON = "Person";
        public static final String ORGANIZATION = "Organization";

    }

}
