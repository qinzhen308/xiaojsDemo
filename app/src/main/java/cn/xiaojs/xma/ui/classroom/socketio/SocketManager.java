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

import java.net.URISyntaxException;

import cn.xiaojs.xma.ui.classroom.Constants;
import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {
    private final static String SCHEME = "http";
    private final static String SCHEME_SSL = "https";

    private static Socket mSocket;

    public synchronized static Socket getSocket() {
        if (mSocket != null) {
            return mSocket;
        }

        try {
            mSocket = IO.socket(getClassroomSocketUrl());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return mSocket;
    }

    public static String getClassroomSocketUrl() {
        return SCHEME + "://" + Constants.CLASSROOM_BASE_URL + ":" + Constants.CLASSROOM_PORT + "/" + Constants.CLASSROOM_PATH;
    }

    public static void close() {
        if (mSocket != null) {
            mSocket.close();
            mSocket = null;
        }
    }

}
