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
    public static final int EDIT_PROFILE = 10;
    public static final int GET_CENTER_DATA = 11;
    public static final int GET_HOME_DATA = 12;
    public static final int GET_PROFILE = 13;
    public static final int GET_UPTOKEN = 14;
    public static final int REGISTER = 15;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //CTL
    //

    public static final int CANCEL_LESSON = 2;
    public static final int CANCEL_LESSON_ON_SHELVES = 20;
    public static final int CONFIRM_LESSON_ENROLLMENT = 21;
    public static final int CREATE_LESSON = 22;
    public static final int EDIT_LESSON = 23;
    public static final int ENROLL_LESSON = 24;
    public static final int GET_ENROLLED_LESSONS = 25;
    public static final int GET_LESSON_COVER_UPTOKEN = 26;
    public static final int GET_LESSON_DATA = 27;
    public static final int GET_LESSON_DETAILS = 28;
    public static final int GET_LESSONS = 29;
    public static final int PUT_LESSON_ON_SHELVES = 200;
    public static final int TOGGLE_ACCESS_TO_LESSON = 201;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Categories
    //

    public static final int GET_SUBJECT = 3;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Platform
    //

    public static final int DELETE_NOTIFICATION = 4;
    public static final int GET_NOTIFICATIONS_OVERVIEW = 40;
    public static final int GET_NOTIFICATIONS = 41;
    public static final int IGNORE_NOTIFICATIONS = 42;

    //////////////////////////////////////////////////////////////////////////////////////
    //Security
    //

    public static final int LOGIN = 5;
    public static final int LOGOUT = 50;
    public static final int VALIDATE_CODE = 51;
    public static final int VERIFY_MOBILE = 52;
    public static final int HAVE_PROVILEGES = 53;
}