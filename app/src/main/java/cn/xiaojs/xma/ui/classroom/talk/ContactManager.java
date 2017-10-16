package cn.xiaojs.xma.ui.classroom.talk;
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
import java.util.HashSet;
import java.util.List;

import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.SyncClassStateReceive;
import cn.xiaojs.xma.ui.classroom.bean.SyncClassStateResponse;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import cn.xiaojs.xma.util.XjsUtils;
import io.reactivex.functions.Consumer;

public class ContactManager{
    public final static int ACTION_JOIN = 1 << 1;
    public final static int ACTION_LEAVE = 1 << 2;
    public final static int ACTION_PSTYPE_CHANGE = 1 << 3;
    public final static int ACTION_GET_ATTENDS_SUCC = 1 << 4;

    private static ContactManager mInstance;
    private AttendsComparator mAttendsComparator;

    private LiveCollection<Attendee> mLiveCollection;

    private List<OnAttendsChangeListener> mOnAttendsChangeListeners;
    private final Object LOCK = new Object();

    private EventListener.ELContact eventListener;

    public static synchronized ContactManager getInstance() {
        if (mInstance == null) {
            mInstance = new ContactManager();
        }

        return mInstance;
    }

    private ContactManager() {
        mOnAttendsChangeListeners = new ArrayList<OnAttendsChangeListener>();
        mAttendsComparator = new AttendsComparator();
    }

    public void init() {
        eventListener = ClassroomEngine.getEngine().observerContact(receivedConsumer);
    }

    public void release() {

        if (eventListener !=null) {
            eventListener.dispose();
        }

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

        mInstance = null;
    }

    public synchronized void getAttendees(Context context, final OnGetAttendsCallback callback) {
        if (mLiveCollection == null) {
            String ticket = ClassroomEngine.getEngine().getTicket();
            LiveManager.getAttendees(context, ticket, new APIServiceCallback<LiveCollection<Attendee>>() {
                @Override
                public void onSuccess(LiveCollection<Attendee> liveCollection) {
                    if (liveCollection != null && liveCollection.attendees != null) {
                        sort(liveCollection.attendees);
                    }
                    mLiveCollection = liveCollection;
                    if (callback != null) {
                        callback.onGetAttendeesSuccess(mLiveCollection);
                    }

                    //notify attend getting succ
                    notifyAttendChanged(mLiveCollection != null ? mLiveCollection.attendees : null, ACTION_GET_ATTENDS_SUCC);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    if (callback != null) {
                        callback.onGetAttendeesFailure(errorCode, errorMessage);
                    }
                }
            });
        } else {
            if (callback != null) {
                callback.onGetAttendeesSuccess(mLiveCollection);
            }
        }
    }

    public synchronized LiveCollection<Attendee> getAttendees() {
        return mLiveCollection;
    }


    private Consumer<EventReceived> receivedConsumer = new Consumer<EventReceived>() {
        @Override
        public void accept(EventReceived eventReceived) throws Exception {

            switch (eventReceived.eventType) {
                case Su.EventType.JOIN:
                    updateContactList(true, (Attendee)eventReceived.t);
                    break;
                case Su.EventType.LEAVE:
                    updateContactList(false, (Attendee)eventReceived.t);
                    break;
                case Su.EventType.SYNC_CLASS_STATE:
                    SyncClassStateReceive receive = (SyncClassStateReceive) eventReceived.t;
                    handleSyncClassState(receive);
                    break;
            }
        }
    };

    private void handleSyncClassState(SyncClassStateReceive syncState) {
        if (syncState != null && syncState.volatiles != null && syncState.volatiles.length > 0) {

            if (mLiveCollection != null && mLiveCollection.attendees != null) {

                for (Attendee attendee : mLiveCollection.attendees) {

                    for (SyncClassStateResponse.Volatiles volatiles : syncState.volatiles) {

                        if (attendee.accountId.equals(volatiles.accountId)) {
//                                    if (TextUtils.isEmpty(volatiles.psType)) {
//                                        attendee.psType = LiveCtlSessionManager.getInstance().getCtlSession().psType;
//                                    }else {
                            attendee.psTypeInLesson = volatiles.psType;
                            //}


                            break;
                        }

                    }


                }

            }

            notifyAttendChanged(mLiveCollection.attendees, ACTION_PSTYPE_CHANGE);
        }
    }


    /**
     * 更新联系人列表
     */
    private void updateContactList(boolean join, Attendee attendee) {

        if (attendee != null && mLiveCollection != null) {

            if (join) {
                mLiveCollection.current ++;
            }else {

                mLiveCollection.current --;
                if (mLiveCollection.current <0) {
                    mLiveCollection.current = 0;
                }
            }


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
                    if (index >0) {
                        Attendee oldAttend = mLiveCollection.attendees.get(index);
                        oldAttend.xa = join ? attendee.xa : 0;
                    }

                }

                if (mLiveCollection.attendees != null) {
                    sort(mLiveCollection.attendees);
                }

                notifyAttendChanged(mLiveCollection.attendees, join ? ACTION_JOIN : ACTION_LEAVE);
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

    public void notifyAttendChanged(ArrayList<Attendee> attendees, int action) {
        if (mOnAttendsChangeListeners != null) {
            for (OnAttendsChangeListener listener : mOnAttendsChangeListeners) {
                listener.onAttendsChanged(attendees, action);
            }
        }
    }

    public static interface OnGetAttendsCallback {
        public void onGetAttendeesSuccess(LiveCollection<Attendee> liveCollection);

        public void onGetAttendeesFailure(String errorCode, String errorMessage);
    }

    public static interface OnAttendsChangeListener {
        public void onAttendsChanged(ArrayList<Attendee> attendees, int action);
    }

    private void sort(ArrayList<Attendee> attendees) {
        if (attendees != null) {

            //FIXME 去除重复的联系人，这是为了解决接口的BUG。
            HashSet<Attendee> tempSet = new HashSet<>(attendees);
            attendees.clear();
            attendees.addAll(tempSet);

            Collections.sort(attendees, mAttendsComparator);
            int rightOffset = attendees.size() - 1;
            for (int i = attendees.size() - 1; i >= 0; i--) {
                if (attendees.get(i).xa == 0) {
                    XjsUtils.circularShiftRight(attendees, rightOffset--, i);
                }
            }
        }
    }

}
