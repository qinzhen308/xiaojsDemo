package cn.xiaojs.xma.ui.contact2.query;

import android.content.Context;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.contact2.model.AbsContactItem;
import cn.xiaojs.xma.ui.contact2.model.ContactsWhitIndex;
import cn.xiaojs.xma.ui.contact2.model.FriendItem;
import cn.xiaojs.xma.ui.contact2.model.ItemTypes;
import cn.xiaojs.xma.ui.contact2.model.LabelItem;
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

    public FriendsDataProvider(Context context) {
        this.context = context;
    }

    public void loadFriends(DataProvider dataProvider, Consumer<ContactsWhitIndex> dataReceiver) {
        groupForIndex(dataProvider.getPersons(), dataReceiver);
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


                for (int i = 0; i < contacts.size(); i++) {
                    AbsContactItem contactItem = contacts.get(i);
                    if (contactItem.getItemType() == ItemTypes.LABEL) {
                        belongCollect.put(contactItem.belongsGroup(), i);
                    }
                }


                ContactsWhitIndex contactsWhitIndex = new ContactsWhitIndex();
                contactsWhitIndex.contacts = contacts;
                contactsWhitIndex.indexMap = belongCollect;

                e.onNext(contactsWhitIndex);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataReceiver);

    }

}
