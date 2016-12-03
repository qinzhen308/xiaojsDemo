package com.benyuan.xiaojs.data.api.service;

/**
 * Created by maxiaobao on 2016/12/2.
 */

public class APIType {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Accounts
    //

    public static final int CLAIM_COMPETENCY = 1;
    public static final int EDIT_PROFILE = 2;
    public static final int GET_CENTER_DATA = 3;
    public static final int GET_HOME_DATA = 4;
    public static final int GET_PROFILE = 5;
    public static final int GET_UPTOKEN = 6;
    public static final int REGISTER = 7;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //CTL
    //

    public static final int CANCEL_LESSON = 8;
    public static final int CANCEL_LESSON_ON_SHELVES = 10;
    public static final int CONFIRM_LESSON_ENROLLMENT = 11;
    public static final int CREATE_LESSON = 12;
    public static final int EDIT_LESSON = 13;
    public static final int ENROLL_LESSON = 14;
    public static final int GET_ENROLLED_LESSONS = 15;
    public static final int GET_LESSON_COVER_UPTOKEN = 16;
    public static final int GET_LESSON_DATA = 17;
    public static final int GET_LESSON_DETAILS = 18;
    public static final int GET_LESSONS = 19;
    public static final int PUT_LESSON_ON_SHELVES = 20;
    public static final int TOGGLE_ACCESS_TO_LESSON = 21;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Categories
    //

    public static final int GET_SUBJECT = 22;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Platform
    //

    public static final int DELETE_NOTIFICATION = 23;
    public static final int GET_NOTIFICATIONS_OVERVIEW = 24;
    public static final int GET_NOTIFICATIONS = 25;
    public static final int IGNORE_NOTIFICATIONS = 26;

    //////////////////////////////////////////////////////////////////////////////////////
    //Security
    //

    public static final int LOGIN = 27;
    public static final int LOGOUT = 28;
    public static final int VALIDATE_CODE = 29;
    public static final int VERIFY_MOBILE = 30;
}
