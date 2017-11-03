package cn.xiaojs.xma.data.provider;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.model.social.Contact;
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
    private Map<String, Integer> conversationsMapping;
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
        conversationsMapping = new HashMap<>();
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

    public Map<String, Integer> getConversationsMapping() {
        return conversationsMapping;
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


    public void updateConversationUnread(String conversationId, int unreadCount) {

        if (TextUtils.isEmpty(conversationId))
            return;

        if (conversationsMapping.containsKey(conversationId)) {
            int index = conversationsMapping.get(conversationId);
            Contact oriContact = conversations.get(index);
            oriContact.unread = unreadCount;

            for (DataObserver observer : dataObservers) {
                observer.onConversationUpdate(oriContact, index);
            }

        }
    }


    public void moveOrInsertConversation(Contact contact) {
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
            for (DataObserver observer : dataObservers) {
                observer.onConversationMove(oriContact, index, 1);
            }


        } else {

            conversations.add(1, contact);
            conversationsMapping.put(contact.id, 1);

            for (DataObserver observer : dataObservers) {
                observer.onConversationInsert(contact, 1);
            }


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
        return timeTable;
    }


}
