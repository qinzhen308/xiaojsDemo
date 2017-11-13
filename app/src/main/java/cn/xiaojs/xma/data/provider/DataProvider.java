package cn.xiaojs.xma.data.provider;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.xiaojs.xma.data.XMSManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.classroom2.util.VibratorUtil;
import cn.xiaojs.xma.ui.conversation2.ConversationType;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by maxiaobao on 2017/11/3.
 */

public class DataProvider {

    private static DataProvider cache;

    private Context context;
    private ArrayList<Contact> conversations;
    private ArrayList<Contact> persons;
    private Map<String, Contact> personsMapping;
    private ArrayList<Contact> classes;
    private Map<String, Contact> classesMapping;
    private ArrayList<DataObserver> dataObservers;

    private boolean completed;


    private DataProvider(Context context) {
        this.context = context.getApplicationContext();
        conversations = new ArrayList<>();
        conversations.add(createTimetable());
        persons = new ArrayList<>();
        classes = new ArrayList<>();
        personsMapping = new HashMap<>();
        classesMapping = new HashMap<>();

        completed = false;
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 联系人
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

    public Contact getClassAdviser(String classId) {
        Contact contact = classesMapping.get(classId);
        Contact adviser = null;
        if (contact != null) {
            adviser = new Contact();
            adviser.owner = contact.owner;
            adviser.ownerId = contact.ownerId;
        }

        if (adviser !=null && !TextUtils.isEmpty(adviser.ownerId)) {
            Contact person = getPersonById(adviser.owner);
            if (person !=null) {
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

    public void updateConversationSilent(String conversationId, boolean silent) {
        if (TextUtils.isEmpty(conversationId))
            return;

        Contact tempContact = new Contact();
        tempContact.id = conversationId;

        int index = conversations.indexOf(tempContact);
        if (index >= 0) {
            Contact oriContact = conversations.get(index);
            oriContact.silent = silent;

            for (DataObserver observer : dataObservers) {
                observer.onConversationUpdate(oriContact, index);
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

            for (DataObserver observer : dataObservers) {
                observer.onConversationUpdate(oriContact, index);
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

            if (index != 1) {
                conversations.remove(index);
                conversations.add(1, oriContact);
            }

            for (DataObserver observer : dataObservers) {
                observer.onConversationMove(oriContact, index, 1);
            }

            vibrate(contact);

        } else {

            conversations.add(1, contact);
            for (DataObserver observer : dataObservers) {
                observer.onConversationInsert(contact, 1);
            }

            vibrate(contact);
        }

    }


    public void dispatchLoadComplete() {
        if (dataObservers == null)
            return;

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

}
