package cn.xiaojs.xma.ui.contact2.query;

import android.content.Context;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.ui.contact2.model.AbsContactItem;
import cn.xiaojs.xma.ui.contact2.model.ContactCompare;
import cn.xiaojs.xma.ui.contact2.model.ContactsWhitIndex;
import cn.xiaojs.xma.ui.contact2.model.FriendItem;
import cn.xiaojs.xma.ui.contact2.model.ItemTypes;
import cn.xiaojs.xma.ui.contact2.model.LabelItem;
import cn.xiaojs.xma.ui.conversation2.ConversationDataProvider;
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
    private ConversationDataProvider dataProvider;

    public FriendsDataProvider(Context context, ConversationDataProvider dataProvider) {
        this.context = context;
        this.dataProvider = dataProvider;
    }

    public void loadFriends(Consumer<ContactsWhitIndex> dataReceiver) {
        groupForIndex(dataProvider.getPersons(),dataReceiver);
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


                for (int i=0; i<contacts.size(); i++) {
                    AbsContactItem contactItem = contacts.get(i);
                    if (contactItem.getItemType() == ItemTypes.LABEL){
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
