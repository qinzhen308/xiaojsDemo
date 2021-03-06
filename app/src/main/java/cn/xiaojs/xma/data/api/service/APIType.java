package cn.xiaojs.xma.data.api.service;

/**
 * Created by maxiaobao on 2016/12/2.
 */

public class APIType {

    public static final int CLAIM_COMPETENCY = 1;
    public static final int EDIT_PROFILE = 2;
    public static final int GET_CENTER_DATA = 3;
    public static final int GET_HOME_DATA = 4;
    public static final int GET_PROFILE = 5;
    public static final int GET_UPTOKEN = 6;
    public static final int REGISTER = 7;
    public static final int GET_PRIVATE_HOME = 8;
    public static final int GET_PUBLIC_HOME = 9;

    public static final int CANCEL_LESSON = 10;
    public static final int CANCEL_LESSON_ON_SHELVES = 11;
    public static final int CONFIRM_LESSON_ENROLLMENT = 12;
    public static final int CREATE_LESSON = 13;
    public static final int EDIT_LESSON = 14;
    public static final int ENROLL_LESSON = 15;
    public static final int GET_ENROLLED_LESSONS = 16;
    //public static final int GET_LESSON_COVER_UPTOKEN = 26;
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


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Security
    //

    public static final int LOGIN = 27;
    public static final int LOGOUT = 28;
    public static final int VALIDATE_CODE = 29;
    public static final int VERIFY_MOBILE = 30;
    public static final int HAVE_PROVILEGES = 31;
    public static final int CHECK_SESSION = 32;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Social
    //
    public static final int ADD_CONTACT_GROUP = 33;
    public static final int POST_ACTIVITY = 34;
    public static final int GET_ACTIVITIES = 35;
    public static final int COMMENT_ACTIVITY = 36;
    public static final int GET_COMMENTS = 37;
    public static final int LIKE_ACTIVITY = 38;
    public static final int REPLAY_COMMENT = 39;
    public static final int REPLAY_2_REPLY = 40;
    public static final int GET_UPDATES = 41;
    public static final int FOLLOW_CONTACT = 42;
    public static final int UNFOLLOW_CONTACT = 43;
    public static final int GET_ACTIVITY_DETAILS = 44;
    public static final int GET_LINKED_RECORDS = 45;
    public static final int GET_CONTACTS = 46;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Search
    //

    public static final int SEARCH_ACCOUNT = 47;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Live Sessions
    //
    public static final int GENERATE_TICKET = 48;
    public static final int BOOT_SESSION = 49;
    public static final int GET_TALKS = 50;
    public static final int GET_ATTENDEES = 51;
    public static final int BEGIN_CLASS = 52;
    public static final int CLOSE_BOARD = 53;
    public static final int GET_BOARDS = 54;
    public static final int OPEN_BOARD = 55;
    public static final int PAUSE_CLASS = 56;
    public static final int REGISTER_BOARD = 57;
    public static final int FINISH_CLASS = 58;
    public static final int RESUME_CLASS = 59;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Collaborations
    //
    public static final int GET_UPLOAD_TOKENS = 60;
    public static final int ADD_TO_LIBRARY = 61;
    public static final int GET_LIBRARY_OVERVIEW = 62;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    public static final int CREATE_ORDER = 63;
    public static final int CREATE_PAYMENT_CHARGE = 64;
    public static final int GET_ACCOUNT_ACTIVITIES = 65;
    public static final int GET_ORDERS = 66;

    public static final int GET_SUBJECTS = 67;

    public static final int GET_DOCUMENTS = 68;

    public static final int GET_COMPETENCIES = 69;

    public static final int GET_LESSONS_BY_USER= 70;

    public static final int GET_CLASSES= 71;
    public static final int GET_ENROLLED_CLASSESS= 72;
    public static final int GET_LIVE_CLASSES= 73;

    public static final int RESET_PASSWORD= 74;
    public static final int CHANGE_PASSWORD= 75;
    public static final int REQUEST_VERIFICATION= 76;

    public static final int GET_ARTICLE= 77;

    public static final int DELETE_ACTIVITY= 78;
    public static final int DELETE_COMMENT_REPLY= 79;
    public static final int CHECK_UPGRADE = 80;

    public static final int GET_VERIFICATION_STATUS = 81;

    public static final int ACKNOWLEDGE_INVITATION = 82;

    public static final int ACKNOWLEDGE_LESSON = 83;

    public static final int GET_ENROLLED_STUDENTS = 84;

    public static final int EDIT_LESSON_SCHEDULE = 85;

    public static final int SEARCH_ACCOUNT_OR_LESSON = 86;
    public static final int SEARCH_ACCOUNT_V2 = 87;

    public static final int SEARCH_LESSONS = 88;

    public static final int DELETE_DOCUMENT = 89;
    public static final int SHARE_DOCUMENT = 90;
    public static final int SHARE_DOCUMENTS = 91;

    public static final int ADD_OPEN_SUBJECT = 92;
    public static final int SEARCH_SUBJECTS = 93;

    public static final int HIDE_LESSON = 94;
    public static final int GET_ORG_TEACHERS = 95;
    public static final int JOIN_LESSON = 96;

    public static final int SOCIAL_LOGIN = 97;
    public static final int SOCIAL_ASSOCIATE = 98;
    public static final int CHECK_ASSOCIATION = 99;

    public static final int CREATE_CLASS = 100;
    public static final int GET_CLASSES_SCHEDULE = 101;
    public static final int SCHEDULE_CLASS_LESSON = 102;
    public static final int CHECK_OVERLAP = 103;
    public static final int GET_CLASS = 104;
    public static final int MODIFY_CLASS = 105;
    public static final int GET_CLASS_STUDENTS = 106;
    public static final int ADD_CLASS_STUDENTS = 107;
    public static final int REMOVE_CLASS_STUDENT = 108;
    public static final int JOIN_CLASS = 109;
    public static final int REVIEW_JOIN_CLASS = 110;
    public static final int REMOVE_CLASS = 111;
    public static final int MODIFY_CLASSES_LESSON = 112;
    public static final int GET_LIVE_SCHEDULE = 113;
    public static final int DELETE_CLASS_LESSON = 114;
    public static final int CANCEL_CLASS_LESSON = 115;
    public static final int GET_CLASS_STUDENTS_JOIN = 116;
    public static final int SEARCH_SEARCH = 117;

    public static final int CREATE_RECORDED_COURSE = 118;
    public static final int PUT_RECORDED_COURSE_ON_SHELVES = 119;

    public static final int CREATE_DIRECTORY = 120;
    public static final int GET_COURSES = 121;
    public static final int GET_RECORDED_COURSES = 122;
    public static final int GET_RECORDED_COURSE = 123;
    public static final int GET_RECORDED_COURSE_CHAPTERS = 124;
    public static final int GET_RECORDED_COURSE_STUDENTS = 125;
    public static final int ADD_RECORDED_COURSE_STUDENT = 126;
    public static final int REVIEW_RECORDED_COURSE_ENROLL = 127;
    public static final int ENROLL_RECORDED_COURSE = 128;
    public static final int MODIFY_RECORDED_COURSE = 129;
    public static final int GET_RECORD_COURSE_BY_USER= 130;
    public static final int GET_CLASS_BY_USER= 131;
    public static final int REMOVE_RECORDED_COURSE_STUDENT= 132;
    public static final int REMOVE_RECORDED_COURSE= 133;
    public static final int CANCEL_RECORDED_COURSE_ON_SHELVES= 134;
    public static final int CONVERT_DOCUMENT= 135;
    public static final int ABORT_RECORDED_COURSE= 136;
    public static final int ABORT_CLASS= 137;
    public static final int EDIT_DOCUMENT = 138;
    public static final int MOVE_DOCUMENT = 139;
    public static final int SAVE_BOARD = 140;
    public static final int DELETE_BOARD = 141;

}
