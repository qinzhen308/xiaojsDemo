package cn.xiaojs.xma.ui.conversation2;

import android.content.Context;

import java.util.ArrayList;

import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
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
    private OnDataListener dataListener;

    private int status = STATUS_NORMAL;


    public interface OnDataListener {
        void onDataLoadComplete(ArrayList<Contact> conversations);
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
        this.context = context;
        conversations = new ArrayList<>();
        conversations.add(createTimetable());
    }


    public void setDataListener(OnDataListener dataListener) {
        this.dataListener = dataListener;
    }

    private Contact createTimetable() {
        Contact timeTable = new Contact();
        timeTable.subtype = ConversationType.TypeName.TIME_TABLE;
        return timeTable;
    }


    public void startLoad() {
        Observable.create(new ObservableOnSubscribe<ArrayList<Contact>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ArrayList<Contact>> e) throws Exception {

                loadConversations();
                e.onNext(conversations);
                e.onComplete();

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Contact>>() {
                    @Override
                    public void accept(ArrayList<Contact> contacts) throws Exception {

                        status = STATUS_NORMAL;

                        if (dataListener != null) {
                            dataListener.onDataLoadComplete(contacts);
                        }
                    }
                });
    }


    private void loadConversations() {
        status = STATUS_LOADING;
        SocialManager.getContacts2(context, new APIServiceCallback<ArrayList<ContactGroup>>() {
            @Override
            public void onSuccess(ArrayList<ContactGroup> contactGroups) {

                if (contactGroups != null && contactGroups.size() > 0) {
                    for (ContactGroup cg : contactGroups) {
                        if (cg.set.equals("dialogs")) {
                            conversations.addAll(cg.collection);
                            break;
                        }
                    }
                }

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }

}
