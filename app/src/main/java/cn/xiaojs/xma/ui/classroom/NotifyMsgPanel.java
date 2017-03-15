package cn.xiaojs.xma.ui.classroom;
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
 * Date:2016/11/28
 * Desc: 通知消息，第一获取消息列表复用的是talk msg的api数据
 *
 * ======================================================================================== */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.talk.NotifyMsgAdapter;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class NotifyMsgPanel extends Panel {
    private PullToRefreshSwipeListView mNotifyMsgListView;
    private NotifyMsgAdapter mNotifyMsgAdapter;
    private String mTicket;

    private Socket mSocket;

    public NotifyMsgPanel(Context context, String ticket) {
        super(context);

        mTicket = ticket;
    }

    @Override
    public View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_classroom_msg, null);
    }

    @Override
    public void initChildView(View root) {
        mNotifyMsgListView = (PullToRefreshSwipeListView)root.findViewById(R.id.msg_list);
    }

    @Override
    public void initData() {
        mSocket = SocketManager.getSocket();
        if (mSocket != null) {
            mSocket.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.JOIN), mOnJoin);
            mSocket.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.LEAVE), mOnLeave);
        }

        mNotifyMsgAdapter = new NotifyMsgAdapter(mContext, mTicket, mNotifyMsgListView);
        mNotifyMsgListView.setAdapter(mNotifyMsgAdapter);
    }


    /**
     * 退出事件
     */
    private Emitter.Listener mOnLeave = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (mContext instanceof Activity && args != null && args.length > 0) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(mContext, "有人退出", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    };

    /**
     * 加入事件
     */
    private Emitter.Listener mOnJoin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (mContext instanceof Activity && args != null && args.length > 0) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(mContext, "有人加入", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };
}
