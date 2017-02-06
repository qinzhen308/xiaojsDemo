package cn.xiaojs.xma.data.api.service;

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
    //public static final int GET_LESSON_COVER_UPTOKEN = 26;
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


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Security
    //

    public static final int LOGIN = 5;
    public static final int LOGOUT = 50;
    public static final int VALIDATE_CODE = 51;
    public static final int VERIFY_MOBILE = 52;
    public static final int HAVE_PROVILEGES = 53;
    public static final int CHECK_SESSION = 54;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Social
    //
    public static final int ADD_CONTACT_GROUP = 6;
    public static final int POST_ACTIVITY = 60;
    public static final int GET_ACTIVITIES = 61;
    public static final int COMMENT_ACTIVITY = 62;
    public static final int GET_COMMENTS = 63;
    public static final int LIKE_ACTIVITY = 64;
    public static final int REPLAY_COMMENT = 65;
    public static final int REPLAY_2_REPLY = 66;
    public static final int GET_UPDATES = 67;
    public static final int FOLLOW_CONTACT = 68;
    public static final int UNFOLLOW_CONTACT = 69;
    public static final int GET_ACTIVITY_DETAILS = 690;
    public static final int GET_LINKED_RECORDS = 691;
    public static final int GET_CONTACTS = 692;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Search
    //

    public static final int SEARCH_ACCOUNT = 7;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Live Sessions
    //
    public static final int GENERATE_TICKET = 8;
    public static final int BOOT_SESSION = 80;
    public static final int GET_TALKS = 81;
    public static final int GET_ATTENDEES = 82;
    public static final int BEGIN_CLASS = 83;
    public static final int CLOSE_BOARD = 84;
    public static final int GET_BOARDS = 85;
    public static final int OPEN_BOARD = 86;
    public static final int PAUSE_CLASS = 87;
    public static final int REGISTER_BOARD = 88;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Collaborations
    //
    public static final int GET_UPLOAD_TOKENS = 9;
    public static final int ADD_TO_LIBRARY = 90;
    public static final int GET_LIBRARY_OVERVIEW = 91;

}
