package cn.xiaojs.xma.ui.contact2.query;

import android.content.Context;
import android.text.TextUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.contact2.model.AbsContactItem;
import cn.xiaojs.xma.ui.contact2.model.ClassItem;
import cn.xiaojs.xma.ui.contact2.model.ContactsWhitIndex;
import cn.xiaojs.xma.ui.contact2.model.FriendItem;
import cn.xiaojs.xma.ui.contact2.model.ItemTypes;
import cn.xiaojs.xma.ui.contact2.model.LabelItem;
import cn.xiaojs.xma.ui.conversation2.ConversationType;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class FriendsDataProvider {

    private Context context;
    private boolean filter;
    private String excludeClassId;

    public FriendsDataProvider(Context context) {
        this.context = context;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public void setExcludeClassId(String excludeClassId) {
        this.excludeClassId = excludeClassId;

    }

    public void loadFriends(DataProvider dataProvider, Consumer<ContactsWhitIndex> dataReceiver) {
        groupForIndex(dataProvider.getPersons(), dataReceiver);
    }

    public void loadClasses(DataProvider dataProvider, Consumer<ContactsWhitIndex> dataReceiver) {
        classesForIndex(dataProvider.getClasses(), dataReceiver);
    }

    private void groupForIndex(
            final ArrayList<Contact> originContacts, Consumer<ContactsWhitIndex> dataReceiver) {

        Observable.create(new ObservableOnSubscribe<ContactsWhitIndex>() {

            @Override
            public void subscribe(
                    @NonNull ObservableEmitter<ContactsWhitIndex> e) throws Exception {

                ArrayList<AbsContactItem> contacts = new ArrayList<>();
                Map<String, Integer> belongCollect = new HashMap<>();

                if (originContacts != null && originContacts.size() > 0) {

                    for (Contact contact : originContacts) {

                        if (filter && contact.subtype.equals(ConversationType.TypeName.ORGANIZATION)) {
                            continue;
                        }


                        if (AccountDataManager.isXiaojsAccount(contact.account)) {
                            FriendItem item = new FriendItem(contact);
                            item.setTop(true);
                            contacts.add(item);
                            continue;
                        }

                        FriendItem item = new FriendItem(contact);
                        String belong = item.belongsGroup();

                        if (!belongCollect.containsKey(belong)) {
                            LabelItem label = new LabelItem(belong);
                            contacts.add(label);
                            belongCollect.put(belong, -1);
                        }

                        contacts.add(item);
                    }
                }

                Collections.sort(contacts);

                ArrayList<String> letters = new ArrayList<>();
                for (int i = 0; i < contacts.size(); i++) {
                    AbsContactItem contactItem = contacts.get(i);
                    if (contactItem.getItemType() == ItemTypes.LABEL) {
                        String belong = contactItem.belongsGroup();
                        belongCollect.put(belong, i);
                        letters.add(belong);
                    }
                }


                ContactsWhitIndex contactsWhitIndex = new ContactsWhitIndex();
                contactsWhitIndex.contacts = contacts;
                contactsWhitIndex.indexMap = belongCollect;
                contactsWhitIndex.letters = letters;

                e.onNext(contactsWhitIndex);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataReceiver);

    }


    private void classesForIndex(
            final ArrayList<Contact> originContacts, Consumer<ContactsWhitIndex> dataReceiver) {

        Observable.create(new ObservableOnSubscribe<ContactsWhitIndex>() {

            @Override
            public void subscribe(
                    @NonNull ObservableEmitter<ContactsWhitIndex> e) throws Exception {

                ArrayList<AbsContactItem> contacts = new ArrayList<>();
                Map<String, Integer> belongCollect = new HashMap<>();

                String mid = AccountDataManager.getAccountID(context);
                Account myAccount = AccountDataManager.getAccont(context);
                String myClassName = null;
                if (myAccount != null && myAccount.getBasic() != null) {
                    String myname = myAccount.getBasic().getName();
                    myClassName = TextUtils.isEmpty(myname) ? null : myname + "的教室";
                }


                if (originContacts != null && originContacts.size() > 0) {

                    for (Contact contact : originContacts) {


                        if (filter) {
                            if (contact.id.equals(excludeClassId) || contact.ownerId.equals(mid)) {
                                continue;
                            }
                        }


                        if (!TextUtils.isEmpty(myClassName)
                                && myClassName.equals(contact.title)
                                && contact.ownerId.equals(mid)) {
                            ClassItem item = new ClassItem(contact);
                            item.setTop(true);
                            contacts.add(item);
                            continue;
                        }

                        ClassItem item = new ClassItem(contact);
                        String belong = item.belongsGroup();

                        if (!belongCollect.containsKey(belong)) {
                            LabelItem label = new LabelItem(belong);
                            contacts.add(label);
                            belongCollect.put(belong, -1);
                        }

                        contacts.add(item);
                    }
                }

                Collections.sort(contacts);

                ArrayList<String> letters = new ArrayList<>();
                for (int i = 0; i < contacts.size(); i++) {
                    AbsContactItem contactItem = contacts.get(i);
                    if (contactItem.getItemType() == ItemTypes.LABEL) {
                        String belong = contactItem.belongsGroup();
                        belongCollect.put(belong, i);
                        letters.add(belong);
                    }
                }


                ContactsWhitIndex contactsWhitIndex = new ContactsWhitIndex();
                contactsWhitIndex.contacts = contacts;
                contactsWhitIndex.indexMap = belongCollect;
                contactsWhitIndex.letters = letters;

                e.onNext(contactsWhitIndex);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataReceiver);

    }

}
