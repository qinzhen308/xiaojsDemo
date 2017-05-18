package cn.xiaojs.xma.ui.classroom.main;
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
 * Date:2017/5/17
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.talk.AttendsComparator;

public class ContactManager {
    private static ContactManager mInstance;
    private AttendsComparator mAttendsComparator;

    public LiveCollection<Attendee> mLiveCollection;
    public List<OnAttendsChangeListener> mOnAttendsChangeListeners;
    private final Object LOCK = new Object();

    public static synchronized ContactManager getInstance() {
        if (mInstance == null) {
            mInstance = new ContactManager();
        }

        return mInstance;
    }

    private ContactManager() {
        mAttendsComparator = new AttendsComparator();
        mOnAttendsChangeListeners = new ArrayList<OnAttendsChangeListener>();

    }

    public void listenerSocket() {
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.JOIN), mOnJoin);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.LEAVE), mOnLeave);
    }

    public void reListenerSocket() {
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.JOIN));
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.LEAVE));

        listenerSocket();
    }

    public void release() {
        if (mLiveCollection != null) {
            if (mLiveCollection.attendees != null) {
                mLiveCollection.attendees.clear();
                mLiveCollection.attendees = null;
            }

            mLiveCollection = null;
        }

        if (mOnAttendsChangeListeners != null) {
            mOnAttendsChangeListeners.clear();
            mOnAttendsChangeListeners = null;
        }

        listenerSocket();
    }

    public synchronized void getAttendees(Context context, final OnGetAttendsCallback callback) {
        if (mLiveCollection == null) {
            String ticket = LiveCtlSessionManager.getInstance().getTicket();
            LiveManager.getAttendees(context, ticket, new APIServiceCallback<LiveCollection<Attendee>>() {
                @Override
                public void onSuccess(LiveCollection<Attendee> liveCollection) {
                    if (liveCollection != null && liveCollection.attendees != null) {
                        Collections.sort(liveCollection.attendees, mAttendsComparator);
                    }
                    mLiveCollection = liveCollection;

                    if (callback != null) {
                        callback.onGetAttendeesSuccess(liveCollection);
                    }
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    if (callback != null) {
                        callback.onGetAttendeesFailure(errorCode, errorMessage);
                    }
                }
            });
        } else {
            callback.onGetAttendeesSuccess(mLiveCollection);
        }
    }

    /**
     * 加入事件
     */
    private SocketManager.EventListener mOnJoin = new SocketManager.EventListener() {
        @Override
        public void call(final Object... args) {
            updateContactList(true, args);
        }
    };

    /**
     * 退出事件
     */
    private SocketManager.EventListener mOnLeave = new SocketManager.EventListener() {
        @Override
        public void call(final Object... args) {
            updateContactList(false, args);
        }
    };

    /**
     * 更新联系人列表
     */
    private void updateContactList(boolean join, Object... args) {
        if (args == null || args.length == 0) {
            return;
        }

        Attendee attendee = ClassroomBusiness.parseSocketBean(args[0], Attendee.class);
        if (attendee != null && mLiveCollection != null) {
            if (mLiveCollection.attendees == null) {
                mLiveCollection.attendees = new ArrayList<Attendee>();
            }

            //refresh
            synchronized (LOCK) {
                int index = mLiveCollection.attendees.indexOf(attendee);
                if (join && index < 0) {
                    //add
                    mLiveCollection.attendees.add(attendee);
                } else {
                    Attendee oldAttend = mLiveCollection.attendees.get(index);
                    oldAttend.xa = join ? attendee.xa : 0;
                }

                notifyAttendChanged(mLiveCollection.attendees, join);
            }
        }
    }

    public void registerAttendsChangeListener(OnAttendsChangeListener listener) {
        if (mOnAttendsChangeListeners != null) {
            mOnAttendsChangeListeners.add(listener);
        }
    }

    public void unregisterAttendsChangeListener(OnAttendsChangeListener listener) {
        if (mOnAttendsChangeListeners != null) {
            mOnAttendsChangeListeners.remove(listener);
        }
    }

    public void notifyAttendChanged(ArrayList<Attendee> attendees, boolean join) {
        if (mOnAttendsChangeListeners != null) {
            for (OnAttendsChangeListener listener : mOnAttendsChangeListeners) {
                listener.onAttendsChanged(attendees, join);
            }
        }
    }

    public static interface OnGetAttendsCallback {
        public void onGetAttendeesSuccess(LiveCollection<Attendee> liveCollection);

        public void onGetAttendeesFailure(String errorCode, String errorMessage);
    }

    public static interface OnAttendsChangeListener {
        public void onAttendsChanged(ArrayList<Attendee> attendees, boolean join);
    }


}
