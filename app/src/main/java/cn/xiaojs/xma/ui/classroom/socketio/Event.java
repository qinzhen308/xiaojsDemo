package cn.xiaojs.xma.ui.classroom.socketio;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/1/8
 * Desc:
 *
 * ======================================================================================== */

public class Event {
    /**
     * Constant for welcome
     */
    public final static String WELCOME = "welcome";
    /**
     * Constant for join
     */
    public final static String JOIN = "join";
    /**
     * Constant for board
     */
    public final static String BOARD = "board1";
    /**
     * Constant for begin
     */
    public final static String BEGIN = "begin";
    /**
     * Constant for sync
     */
    public final static String SYNC = "board:sync";

    /**
     * Returns the signature for an app event with the specified event category and type.
     */
    public static String getEventSignature(int eventCategory, int eventType) {
        return eventCategory + ":" + eventType;
    }
}
