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
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.XMSManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.socket.xms.XMSEventObservable;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.socket.RemoveDlgReceived;
import cn.xiaojs.xma.model.socket.SyncClassesReceived;
import cn.xiaojs.xma.model.socket.room.ChangeNotifyReceived;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.RetainDlg;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.ui.classroom.whiteboard.setting.TextPop;
import cn.xiaojs.xma.ui.classroom2.util.VibratorUtil;
import cn.xiaojs.xma.ui.conversation2.ConversationType;
import cn.xiaojs.xma.util.APPUtils;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.MessageUitl;
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
    private int unreadTotal;

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

    public int getUnreadTotal() {
        return unreadTotal < 0 ? 0 : unreadTotal;
    }

    public void setUnreadTotal(int unreadTotal) {

        if (unreadTotal < 0) {
            this.unreadTotal = 0;
            return;
        }

        this.unreadTotal = unreadTotal;
    }

    private void offsetUnreadTotal(boolean add, int offset) {
        if (add) {
            unreadTotal = unreadTotal + offset;
        } else {
            unreadTotal = unreadTotal - offset;
        }

        if (unreadTotal < 0) {
            unreadTotal = 0;
        }

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

    public String getPersonName(String contactId) {
        Contact contact = getPersonById(contactId);
        if (contact != null) {
            return contact.name;
        }
        return null;
    }

    public int getFollowtypeFromContact(String contactId) {

        Contact contact = getPersonById(contactId);
        if (contact != null) {
            return contact.followType;
        }
        return Social.FllowType.NA;
    }

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

    public boolean existInClasses(String accountId) {
        return classesMapping == null ? false : classesMapping.get(accountId) != null;
    }

    public boolean existInClassesByTicket(String ticket) {
        if (ArrayUtil.isEmpty(classes)) {
            return false;
        }
        for (Contact contact : classes) {
            if (ticket.equals(contact.ticket)) {
                return true;
            }
        }
        return false;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Conversation
    //

    public void updateConversationWhenReceivedTalkremoved(String conversationId, long time) {
        if (TextUtils.isEmpty(conversationId) || ArrayUtil.isEmpty(conversations) || time <= 0)
            return;

        Contact tempContact = new Contact();
        tempContact.id = conversationId;

        int index = conversations.indexOf(tempContact);
        if (index >= 0) {
            Contact oriContact = conversations.get(index);

            if (oriContact.lastTalked != time)
                return;

            //FIXME 并不知道删除或者撤销的那条消息是不是未读的，所以不知道怎么更新unreadToal

            oriContact.lastMessage = "";//FIXME 目前获取不到最近的一条消息，所以先放空；

            if (dataObservers != null) {
                for (DataObserver observer : dataObservers) {
                    observer.onConversationUpdate(oriContact, index);
                }
            }
        }

    }

    public void updateConversationWhenTalkRemoved(String conversationId, String lastMessage) {

        Contact tempContact = new Contact();
        tempContact.id = conversationId;

        int index = conversations.indexOf(tempContact);
        if (index >= 0) {
            Contact oriContact = conversations.get(index);
            oriContact.lastMessage = lastMessage;

            if (dataObservers != null) {
                for (DataObserver observer : dataObservers) {
                    observer.onConversationUpdate(oriContact, index);
                }
            }

        }
    }

    public void removeConversations(String[] conversationIds) {
        if (conversationIds == null)
            return;

        for (String conversationId : conversationIds) {
            Contact tempContact = new Contact();
            tempContact.id = conversationId;

            int index = conversations.indexOf(tempContact);
            if (index >= 0) {
                Contact oriContact = conversations.get(index);
                offsetUnreadTotal(false, oriContact.unread);
                conversations.remove(index);
            }
        }

        if (dataObservers != null) {
            for (DataObserver observer : dataObservers) {
                observer.onConversationsRemoved(conversationIds);
            }
        }
    }

    public void removeConversation(String conversationId) {
        if (TextUtils.isEmpty(conversationId))
            return;

        Contact tempContact = new Contact();
        tempContact.id = conversationId;

        int index = conversations.indexOf(tempContact);
        if (index >= 0) {
            Contact oriContact = conversations.get(index);
            offsetUnreadTotal(false, oriContact.unread);
            conversations.remove(index);

            if (dataObservers != null) {
                for (DataObserver observer : dataObservers) {
                    observer.onConversationRemoved(conversationId);
                }
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

            int offset = unreadCount - oriContact.unread;
            offsetUnreadTotal(true, offset);

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
            if (contact.lastTalked != 0) {
                oriContact.lastTalked = contact.lastTalked;
            }

            if (contact.lastMessage != null) {
                oriContact.lastMessage = contact.lastMessage;
            }
            oriContact.unread = oriContact.unread + contact.unread;

            offsetUnreadTotal(true, contact.unread);


            if (!TextUtils.isEmpty(contact.signature)) {
                oriContact.state = contact.state;
                oriContact.streaming = contact.streaming;
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

            if (contact.subtype == ConversationType.TypeName.PRIVATE_CLASS
                    && !existInClasses(contact.id)) {//此处是为了防止游客的情况；
                return;
            }

            conversations.add(1, contact);

            offsetUnreadTotal(true, contact.unread);

            if (dataObservers != null) {
                for (DataObserver observer : dataObservers) {
                    observer.onConversationInsert(contact, 1);
                }
            }

            vibrate(contact);
        }

    }


    public void conversationRetained(Contact contact) {
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

            if (contact.subtype == ConversationType.TypeName.PRIVATE_CLASS
                    && !existInClasses(contact.id)) {//此处是为了防止游客的情况；
                return;
            }

            conversations.add(1, contact);
            if (dataObservers != null) {
                for (DataObserver observer : dataObservers) {
                    observer.onConversationInsert(contact, 1);
                }
            }

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

            if (contact.subtype == ConversationType.TypeName.PRIVATE_CLASS
                    && !existInClasses(contact.id)) {//此处是为了防止游客的情况；
                return;
            }

            conversations.add(1, contact);
            if (dataObservers != null) {
                for (DataObserver observer : dataObservers) {
                    observer.onConversationInsert(contact, 1);
                }
            }

        }

        RetainDlg retainDlg = new RetainDlg();
        retainDlg.to = contact.id;
        retainDlg.type = ConversationType.getTalkType(contact.subtype);
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
        if (contact.silent || contact.sync) {
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
                    RemoveDlgReceived removeDlgReceived = (RemoveDlgReceived) eventReceived.t;
                    handleRemoveDlg(removeDlgReceived);
                    break;
                case Su.EventType.RETAIN_DIALOG:
                    Talk retainDlg = (Talk) eventReceived.t;
                    handleRetainDlg(retainDlg);
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

    private void handleRemoveDlg(RemoveDlgReceived received) {
        if (received == null)
            return;

        if (received.type == Communications.TalkType.OPEN) {
            removeConversation(received.cls);
        } else if (received.type == Communications.TalkType.PEER) {
            removeConversations(received.peers);
        }
    }

    private void handleRetainDlg(Talk talkItem) {

        if (talkItem == null)
            return;

        Contact contact = new Contact();
        if (talkItem.type == Communications.TalkType.PEER) {
            contact.id = talkItem.to;
            contact.name = talkItem.name;
            contact.title = talkItem.name;
            contact.subtype = ConversationType.TypeName.PERSON;
            contact.followType = talkItem.followType;
        } else if (talkItem.type == Communications.TalkType.OPEN) {
            contact.id = talkItem.to;
            String title = TextUtils.isEmpty(talkItem.name) ? getClassName(talkItem.to) : talkItem.name;
            contact.name = title;
            contact.title = title;
            contact.subtype = ConversationType.TypeName.PRIVATE_CLASS;
        } else {
            return;
        }

        conversationRetained(contact);
    }

    private void updateConveration(Talk talkItem) {


        if (!TextUtils.isEmpty(talkItem.signature)) {
            if (talkItem.signature.equals(Su.getRemoveTalkSignature())
                    || talkItem.signature.equals(Su.getRecallTalkSignature())) {

                //FIXME 此处传的stime应该是被删除那条消息的时间， 但是目前事件并没有返回该时间；
                updateConversationWhenReceivedTalkremoved(talkItem.to, talkItem.stime);
                return;
            }
        }


        if (TextUtils.isEmpty(talkItem.name)) {
            talkItem.name = "nil";
        }
        Contact contact = new Contact();

        if (talkItem.type == Communications.TalkType.PEER) {
            if (talkItem.sync) {
                contact.id = talkItem.to;
            } else {
                contact.id = talkItem.from;
            }
            contact.name = talkItem.name;
            contact.title = talkItem.name;
            contact.subtype = ConversationType.TypeName.PERSON;
            contact.followType = talkItem.followType;
        } else if (talkItem.type == Communications.TalkType.OPEN) {
            contact.id = talkItem.to;
            String title = TextUtils.isEmpty(talkItem.name) ? getClassName(talkItem.to) : talkItem.name;
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
            } else if (talkItem.signature.equals(Su.getStopStreamingSignature())) {
                contact.state = Live.LiveSessionState.IDLE;
                contact.streaming = false;

            } else if (talkItem.signature.equals(Su.getFollowSignature())) {
                //TODO 被关注
            } else if (talkItem.signature.equals(Su.getUnFollowSignature())) {
                //TODO 被取消关注
            } else {//FIXME 此处要判断多种状态，目前接口支持不全；
                contact.streaming = false;
                contact.state = Live.LiveSessionState.IDLE;
            }

        }

        contact.lastMessage = talkItem.body.text;

        long realtime = talkItem.stime >= 0 ? talkItem.stime : talkItem.time;
        contact.lastTalked = realtime;
        contact.sync = talkItem.sync;


        if (talkItem.sync) {
            contact.unread = 0;
        } else {
            contact.unread = 1;
        }

        if (talkItem.depressed) {
            //FIXME
            return;
        }
        pushNotify(contact, talkItem.from, talkItem.to);
        moveOrInsertConversation(contact);
    }


    private void pushNotify(Contact contact, String from, String to) {

        if (contact.unread == 0)
            return;

        if (!TextUtils.isEmpty(contact.signature)
                && contact.signature.equals(Su.getUnFollowSignature())) {
            return;
        }

        if (APPUtils.isAppOnForeground(context)) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the app is foreground, so cancel notify push...");
            }
            return;
        }

        String title = contact.name;
        String summary = contact.lastMessage;
        if (contact.subtype == ConversationType.TypeName.PRIVATE_CLASS) {

            String cname = getClassName(to);
            if (TextUtils.isEmpty(cname)) {
                //说明此班不再我的通讯录里面；不显示
                if (XiaojsConfig.DEBUG) {
                    Logger.d("the class is not exist in your contacts, so cancel notify push...");
                }
                return;
            }

            if (getClassSilent(to)) {
                if (XiaojsConfig.DEBUG) {
                    Logger.d("the class is setted silent by you , so cancel notify push...");
                }
                return;
            }


            title = cname;

            String fromName = getPersonName(from);
            summary = TextUtils.isEmpty(fromName) ? summary : fromName + ":" + summary;
        }

        MessageUitl.createLocalNotify(context, title, summary);

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
