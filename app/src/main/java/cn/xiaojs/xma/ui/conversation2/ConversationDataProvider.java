package cn.xiaojs.xma.ui.conversation2;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maxiaobao on 2017/11/2.
 */

public class ConversationDataProvider {

    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_LOADING = 2;

    private static ConversationDataProvider dataProvider;

    private Context context;

    private ArrayList<Contact> conversations;
    private Map<String, Integer> conversationsMapping;
    private ArrayList<Contact> persons;
    private Map<String, Contact> personsMapping;
    private ArrayList<Contact> classes;
    private Map<String, Contact> classesMapping;

    private ArrayList<OnLoadstatusListener> loadstatusListeners;
    private ArrayList<OnDataChangedListener> dataChangedListeners;

    private int status = STATUS_NORMAL;

    public interface OnLoadstatusListener {
        void onLoadComplete();
    }

    public interface OnDataChangedListener {
        void onDataUpdate(Contact contact, int index);

        void onDataMoveOrInsert(ArrayList<Contact> conversations,
                                boolean snew, Contact contact, int index, int oldIndex);
    }


    public static ConversationDataProvider getProvider(Context context) {
        if (dataProvider == null) {

            synchronized (ConversationDataProvider.class) {
                if (dataProvider == null) {
                    dataProvider = new ConversationDataProvider(context);
                }
            }
        }
        return dataProvider;
    }

    public ConversationDataProvider(Context context) {
        this.context = context.getApplicationContext();
        conversations = new ArrayList<>();
        conversations.add(createTimetable());
        conversationsMapping = new HashMap<>();

        persons = new ArrayList<>();
        classes = new ArrayList<>();

        personsMapping = new HashMap<>();
        classesMapping = new HashMap<>();

    }

    public ArrayList<Contact> getClasses() {
        return classes;
    }

    public ArrayList<Contact> getPersons() {
        return persons;
    }

    public ArrayList<Contact> getConversations() {
        return conversations;
    }

    public void addLoadstatusListener(OnLoadstatusListener listener) {
        if (loadstatusListeners == null) {
            loadstatusListeners = new ArrayList<>();
        }

        loadstatusListeners.add(listener);

    }

    public void removeLoadstatusListener(OnLoadstatusListener listener) {
        if (loadstatusListeners == null) {
            return;
        }
        loadstatusListeners.remove(listener);

    }

    public void addDataChangedListener(OnDataChangedListener listener) {
        if (dataChangedListeners == null) {
            dataChangedListeners = new ArrayList<>();
        }

        dataChangedListeners.add(listener);

    }

    public void removeDataChangedListener(OnDataChangedListener listener) {
        if (dataChangedListeners == null) {
            return;
        }
        dataChangedListeners.remove(listener);

    }


    public void updateUnread(String conversationId, int unreadCount) {

        if (TextUtils.isEmpty(conversationId))
            return;

        if (conversationsMapping.containsKey(conversationId)) {
            int index = conversationsMapping.get(conversationId);
            Contact oriContact = conversations.get(index);
            oriContact.unread = unreadCount;

            dispatchDataUpdate(oriContact, index);

        }
    }

    public void updateConversations(Contact contact) {
        if (contact == null)
            return;

        boolean isnew = conversationsMapping.containsKey(contact.id);

        if (isnew) {
            int index = conversationsMapping.get(contact.id);
            Contact oriContact = conversations.get(index);
            oriContact.lastTalked = contact.lastTalked;
            oriContact.lastMessage = contact.lastMessage;
            oriContact.unread = oriContact.unread + contact.unread;

            if (index != 1) {
                conversations.remove(index);
                conversations.add(1, oriContact);
            }

            conversationsMapping.put(contact.id, 1);

            dispatchDataMoveOrInsert(conversations, isnew, oriContact, 1, index);

        } else {

            conversations.add(1, contact);
            conversationsMapping.put(contact.id, 1);

            dispatchDataMoveOrInsert(conversations, isnew, contact, 1, 1);

        }

    }

    private void dispatchDataMoveOrInsert(ArrayList<Contact> conversations,
                                          boolean snew, Contact contact, int index, int oldIndex) {
        if (dataChangedListeners == null)
            return;

        for (OnDataChangedListener listener : dataChangedListeners) {
            listener.onDataMoveOrInsert(conversations, snew, contact, index, oldIndex);
        }
    }

    private void dispatchDataUpdate(Contact contact, int index) {
        if (dataChangedListeners == null)
            return;

        for (OnDataChangedListener listener : dataChangedListeners) {
            listener.onDataUpdate(contact, index);
        }
    }

    private void dispatchLoadComplete() {
        if (loadstatusListeners == null)
            return;

        for (OnLoadstatusListener listener : loadstatusListeners) {
            listener.onLoadComplete();
        }
    }

    private Contact createTimetable() {
        Contact timeTable = new Contact();
        timeTable.id = "-1";
        timeTable.subtype = ConversationType.TypeName.TIME_TABLE;
        return timeTable;
    }


    public void startLoad() {
        status = STATUS_LOADING;
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {


                ArrayList<ContactGroup> contactGroups = SocialManager.getContacts2(context);
                if (contactGroups != null && contactGroups.size() > 0) {
                    for (ContactGroup cg : contactGroups) {
                        if (cg.set.equals("dialogs")) {
                            conversations.addAll(cg.collection);
                        }else if (cg.set.equals("contacts")) {
                            persons = cg.collection;
                        }else if (cg.set.equals("classes")) {
                            classes = cg.collection;
                        }
                    }
                }
                e.onNext(conversations);

                int count = conversations.size();
                if (count > 1) {
                    for (int i = 0; i < count; i++) {
                        Contact contact = conversations.get(i);
                        conversationsMapping.put(contact.id, i);
                    }
                }


                for (Contact contact : persons) {
                    personsMapping.put(contact.id, contact);
                }

                for (Contact contact : classes) {
                    classesMapping.put(contact.id, contact);
                }

                e.onComplete();

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) throws Exception {

                        dispatchLoadComplete();

                        status = STATUS_NORMAL;

                    }
                });
    }


}
