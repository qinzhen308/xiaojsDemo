package cn.xiaojs.xma.data.provider;

import android.content.Context;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.XMSManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.socket.xms.XMSEventObservable;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.socket.SyncClassesReceived;
import cn.xiaojs.xma.model.socket.room.ChangeNotifyReceived;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.RetainDlg;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.ui.classroom.whiteboard.setting.TextPop;
import cn.xiaojs.xma.ui.classroom2.util.VibratorUtil;
import cn.xiaojs.xma.ui.conversation2.ConversationType;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maxiaobao on 2017/11/3.
 */

public class DataProvider {

    public static final int ACTION_ADD = 0;
    public static final int ACTION_REMOVE = 1;
    public static final int ACTION_UPDATE = 2;


    private static DataProvider cache;

    private Context context;
    private ArrayList<Contact> conversations;
    private ArrayList<Contact> persons;
    private Map<String, Contact> personsMapping;
    private ArrayList<Contact> classes;
    private Map<String, Contact> classesMapping;
    private ArrayList<DataObserver> dataObservers;

    private boolean completed;

    private Disposable eventDisposable;


    private DataProvider(Context context) {
        this.context = context.getApplicationContext();
        conversations = new ArrayList<>();
        conversations.add(createTimetable());
        persons = new ArrayList<>();
        classes = new ArrayList<>();
        personsMapping = new HashMap<>();
        classesMapping = new HashMap<>();

        completed = false;

        eventDisposable = XMSEventObservable.observeGlobalSession(context, receivedConsumer);
    }

    public static DataProvider getProvider(Context context) {
        if (cache == null) {
            synchronized (ApiManager.class) {
                if (cache == null) {
                    cache = new DataProvider(context);
                }
            }
        }
        return cache;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void registesObserver(DataObserver dataObserver) {
        if (dataObservers == null) {
            dataObservers = new ArrayList<>();
        }
        this.dataObservers.add(dataObserver);

    }

    public void unregistesObserver(DataObserver dataObserver) {
        if (dataObservers == null)
            return;
        this.dataObservers.remove(dataObserver);

    }


    public ArrayList<Contact> getConversations() {
        return conversations;
    }

    public ArrayList<Contact> getPersons() {
        return persons;
    }

    public ArrayList<Contact> getClasses() {
        return classes;
    }

    public Map<String, Contact> getPersonsMapping() {
        return personsMapping;
    }

    public Map<String, Contact> getClassesMapping() {
        return classesMapping;
    }

    public void setConversations(ArrayList<Contact> conversations) {
        if (conversations == null)
            return;
        this.conversations.clear();
        this.conversations.add(createTimetable());
        this.conversations.addAll(conversations);
    }

    public void setPersons(ArrayList<Contact> persons) {
        if (persons == null)
            return;
        this.persons = persons;
    }

    public void setClasses(ArrayList<Contact> classes) {
        if (classes == null)
            return;
        this.classes = classes;
    }

    public void updateSilent(String conversationId, boolean silent) {
        updateConversationSilent(conversationId, silent);
        updateClassSilent(conversationId, silent);
    }

    public void joinedClass(Contact contact) {
        addOrUpdateClass(contact);
    }

    public void leavedClass(String classid) {
        removeConversation(classid);
        removeClass(classid);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Person
    //
    public boolean existInContact(String aid) {

        if (personsMapping != null && personsMapping.size() > 0) {
            return personsMapping.get(aid) == null ? false : true;
        }

        return false;
    }

    public Contact getPersonById(String cid) {
        return personsMapping.get(cid);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 教室
    //
    public void addOrUpdateClass(Contact contact) {
        int index = classes.indexOf(contact);
        if (index >= 0) {
            Contact oriContact = classes.get(index);
            oriContact.state = contact.state;
            oriContact.owner = contact.owner;

            if (dataObservers != null) {
                for (DataObserver observer : dataObservers) {
                    observer.onClassesUpdate(ACTION_UPDATE, oriContact);
                }
            }

        } else {
            classes.add(contact);
            classesMapping.put(contact.id, contact);

            if (dataObservers != null) {
                for (DataObserver observer : dataObservers) {
                    observer.onClassesUpdate(ACTION_ADD, contact);
                }
            }
        }
    }

    public void removeClass(String classid) {
        if (TextUtils.isEmpty(classid))
            return;

        Contact rcontact = new Contact();
        rcontact.id = classid;

        boolean removed = classes.remove(rcontact);
        classesMapping.remove(classid);

        if (removed && dataObservers != null) {
            for (DataObserver observer : dataObservers) {
                observer.onClassesUpdate(ACTION_REMOVE, rcontact);
            }
        }
    }

    public Contact getClassAdviser(String classId) {
        Contact contact = classesMapping.get(classId);
        Contact adviser = null;
        if (contact != null) {
            adviser = new Contact();
            adviser.owner = contact.owner;
            adviser.ownerId = contact.ownerId;
        }

        if (adviser != null && !TextUtils.isEmpty(adviser.ownerId)) {
            Contact person = getPersonById(adviser.owner);
            if (person != null) {
                adviser.title = person.title;
            }
        }

        return adviser;
    }

    public String getClassTicket(String cid) {
        Contact contact = classesMapping.get(cid);
        if (contact != null) {
            return contact.ticket;
        }
        return null;
    }

    public String getClassName(String cid) {
        Contact contact = classesMapping.get(cid);
        if (contact != null) {
            return contact.title;
        }
        return "";
    }

    public void updateClassSilent(String classId, boolean silent) {
        Contact contact = classesMapping.get(classId);
        if (contact != null) {
            contact.silent = silent;
        }
    }

    public boolean getClassSilent(String classId) {
        Contact contact = classesMapping.get(classId);
        if (contact != null) {
            return contact.silent;
        }
        return false;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Conversation
    //

    public void removeConversation(String conversationId) {
        if (TextUtils.isEmpty(conversationId))
            return;

        Contact tempContact = new Contact();
        tempContact.id = conversationId;

        boolean removed = conversations.remove(tempContact);

        if (removed && dataObservers != null) {
            for (DataObserver observer : dataObservers) {
                observer.onConversationRemoved(conversationId);
            }
        }
    }

    public void updateConversationSilent(String conversationId, boolean silent) {
        if (TextUtils.isEmpty(conversationId))
            return;

        Contact tempContact = new Contact();
        tempContact.id = conversationId;

        int index = conversations.indexOf(tempContact);
        if (index >= 0) {
            Contact oriContact = conversations.get(index);
            oriContact.silent = silent;

            if (dataObservers != null) {
                for (DataObserver observer : dataObservers) {
                    observer.onConversationUpdate(oriContact, index);
                }
            }
        }
    }


    public void updateConversationUnread(String conversationId, int unreadCount) {

        if (TextUtils.isEmpty(conversationId))
            return;

        Contact tempContact = new Contact();
        tempContact.id = conversationId;

        int index = conversations.indexOf(tempContact);

        if (index >= 0) {

            Contact oriContact = conversations.get(index);
            oriContact.unread = unreadCount;

            if (dataObservers != null) {
                for (DataObserver observer : dataObservers) {
                    observer.onConversationUpdate(oriContact, index);
                }
            }

            if (unreadCount > 0) {
                vibrate(oriContact);
            }

        }
    }


    public void moveOrInsertConversation(Contact contact) {
        if (contact == null)
            return;

        int index = conversations.indexOf(contact);
        if (index >= 0) {
            Contact oriContact = conversations.get(index);
            oriContact.lastTalked = contact.lastTalked;
            oriContact.lastMessage = contact.lastMessage;
            oriContact.unread = oriContact.unread + contact.unread;

            if (!TextUtils.isEmpty(contact.signature)) {
                oriContact.state = contact.state;
            }

            if (index != 1) {
                conversations.remove(index);
                conversations.add(1, oriContact);
            }

            if (dataObservers != null) {
                for (DataObserver observer : dataObservers) {
                    observer.onConversationMove(oriContact, index, 1);
                }
            }

            vibrate(contact);

        } else {

            conversations.add(1, contact);

            if (dataObservers != null) {
                for (DataObserver observer : dataObservers) {
                    observer.onConversationInsert(contact, 1);
                }
            }

            vibrate(contact);
        }

    }

    public void conversationOpened(Contact contact) {
        if (contact == null)
            return;

        int index = conversations.indexOf(contact);
        if (index >= 0) {
            Contact oriContact = conversations.get(index);
            if (index != 1) {
                conversations.remove(index);
                conversations.add(1, oriContact);

                if (dataObservers != null) {
                    for (DataObserver observer : dataObservers) {
                        observer.onConversationMove(oriContact, index, 1);
                    }
                }
            }

        } else {

            conversations.add(1, contact);

            if (dataObservers != null) {
                for (DataObserver observer : dataObservers) {
                    observer.onConversationInsert(contact, 1);
                }
            }
        }

        RetainDlg retainDlg = new RetainDlg();
        retainDlg.to = contact.id;
        retainDlg.type = ConversationType.getConversationType(contact.subtype);
        XMSManager.sendRetainDialog(context, retainDlg, null);
    }


    public void dispatchLoadComplete() {
        if (dataObservers == null) {
            setCompleted(true);
            return;
        }

        Observable.fromIterable(dataObservers)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DataObserver>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull DataObserver dataObserver) {
                        dataObserver.onLoadComplete();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        setCompleted(true);
                    }

                    @Override
                    public void onComplete() {
                        setCompleted(true);

                    }
                });
    }

    private Contact createTimetable() {
        Contact timeTable = new Contact();
        timeTable.id = "-1";
        timeTable.subtype = ConversationType.TypeName.TIME_TABLE;
        timeTable.unread = 0;
        return timeTable;
    }

    private boolean vibrate(Contact contact) {
        //消息免打扰的会话，不需要响铃；
        if (contact.silent) {
            return false;
        }

        //如果是已经打开的会话，不需要响铃；
        XMSManager xmsManager = XMSManager.getXmsManager(context);
        if (xmsManager.sessionOpened(contact.id)) {
            return false;
        }

        VibratorUtil.Vibrate(context, 100);
        return true;

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////


    private Consumer<EventReceived> receivedConsumer = new Consumer<EventReceived>() {
        @Override
        public void accept(EventReceived eventReceived) throws Exception {
            if (XiaojsConfig.DEBUG) {
                Logger.d("receivedConsumer .....");
            }

            switch (eventReceived.eventType) {
                case Su.EventType.TALK:
                    updateConveration((Talk) eventReceived.t);
                    break;
                case Su.EventType.DIALOG_READ:
                    Talk talk = (Talk) eventReceived.t;
                    updateDlgRead(talk.type, talk.to);
                    break;
                case Su.EventType.REMOVE_DIALOG:
                    break;
                case Su.EventType.RETAIN_DIALOG:
                    break;
                case Su.EventType.CHANGE_NOTIFY:
                    ChangeNotifyReceived received = (ChangeNotifyReceived) eventReceived.t;
                    handleChangeNotify(received);
                    break;
                case Su.EventType.SYNC_CLASSES:
                    SyncClassesReceived classesReceived = (SyncClassesReceived) eventReceived.t;
                    handleSyncClasses(classesReceived);
                    break;
            }
        }
    };


    private void updateDlgRead(int type, String to) {
        updateConversationUnread(to, 0);
    }


    private void updateConveration(Talk talkItem) {
        if (TextUtils.isEmpty(talkItem.name)) {
            talkItem.name = "nil";
        }
        Contact contact = new Contact();

        //FIXME followType 没返回

        if (talkItem.type == Communications.TalkType.PEER) {
            contact.id = talkItem.from;
            contact.name = talkItem.name;
            contact.title = talkItem.name;
            contact.subtype = ConversationType.TypeName.PERSON;
        } else if (talkItem.type == Communications.TalkType.OPEN) {
            contact.id = talkItem.to;
            String title = getClassName(talkItem.to);
            contact.name = title;
            contact.title = title;
            contact.subtype = ConversationType.TypeName.PRIVATE_CLASS;
        } else {
            return;
        }

        if (!TextUtils.isEmpty(talkItem.signature)) {

            contact.signature = talkItem.signature;

            if (talkItem.signature.equals(Su.getLiveStreamingSignature())) {

                contact.state = Live.LiveSessionState.LIVE;
            } else {//FIXME 此处要判断多种状态，目前接口支持不全；
                contact.state = Live.LiveSessionState.IDLE;
            }

        }

        contact.lastMessage = talkItem.body.text;
        contact.lastTalked = talkItem.time;
        contact.unread = 1;

        moveOrInsertConversation(contact);
    }

    private void handleChangeNotify(ChangeNotifyReceived changeNotify) {
        if (changeNotify == null)
            return;
        updateSilent(changeNotify.to, changeNotify.silent);

    }

    private void handleSyncClasses(final SyncClassesReceived syncClasses) {
        if (syncClasses == null)
            return;

        Observable.create(new ObservableOnSubscribe<ChangeHolder>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<ChangeHolder> e) throws Exception {

                String mid = AccountDataManager.getAccountID(context);

                ChangeHolder changeHolder = null;

                SyncClassesReceived.ChangeTarget[] changes = syncClasses.changes;
                if (changes != null && changes.length > 0) {

                    changeHolder = new ChangeHolder();

                    for (SyncClassesReceived.ChangeTarget target : changes) {
                        if (mid.equals(target.accountId)) {
                            if ("joined".equals(target.change)) {

                                Contact contact = new Contact();
                                contact.id = syncClasses.id;
                                contact.subtype = syncClasses.subtype;
                                contact.title = syncClasses.title;
                                contact.state = syncClasses.state;
                                contact.ticket = syncClasses.ticket;
                                contact.ownerId = syncClasses.ownerId;
                                contact.owner = syncClasses.owner;

                                changeHolder.change = target.change;
                                changeHolder.target = contact;

                            } else if ("left".equals(target.change)) {
                                changeHolder.change = target.change;
                                changeHolder.targetId = syncClasses.id;
                            }
                            break;
                        }
                    }
                }

                e.onNext(changeHolder);
                e.onComplete();

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChangeHolder>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ChangeHolder changeHolder) {

                        if (changeHolder == null || TextUtils.isEmpty(changeHolder.change)) {
                            return;
                        }

                        if (changeHolder.change.equals("joined")) {
                            joinedClass(changeHolder.target);

                        } else if (changeHolder.change.equals("left")) {
                            leavedClass(changeHolder.targetId);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    static class ChangeHolder {
        public String change;
        public Contact target;
        public String targetId;
    }

}
