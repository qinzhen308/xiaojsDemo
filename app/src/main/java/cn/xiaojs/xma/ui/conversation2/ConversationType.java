package cn.xiaojs.xma.ui.conversation2;

import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;

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


    public static String getConversationType(int typeIndex) {
        if (typeIndex == TIME_TABLE) {
            return TypeName.TIME_TABLE;
        } else if (typeIndex == PRIVATE_CLASS) {
            return TypeName.PRIVATE_CLASS;
        } else if (typeIndex == PERSON) {
            return TypeName.PERSON;
        } else if (typeIndex == ORGANIZATION) {
            return TypeName.ORGANIZATION;
        } else {
            return "";
        }
    }


    public static int getTalkType(String typeName) {
        if (TypeName.TIME_TABLE.equals(typeName)) {
            return UNDEFINED;
        } else if (TypeName.PRIVATE_CLASS.equals(typeName)) {
            return Communications.TalkType.OPEN;
        } else if (TypeName.PERSON.equals(typeName)) {
            return Communications.TalkType.PEER;
        } else if (TypeName.ORGANIZATION.equals(typeName)) {
            return UNDEFINED;
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
