package cn.xiaojs.xma.data.loader;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.PlatformManager;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.UpgradeManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.db.ContactDao;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.ClaimCompetency;
import cn.xiaojs.xma.model.Competency;
import cn.xiaojs.xma.model.Privilege;
import cn.xiaojs.xma.model.Upgrade;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.util.NetUtil;

/**
 * Created by maxiaobao on 2017/1/12.
 */

public class SyncService extends IntentService {


    public SyncService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (XiaojsConfig.DEBUG) {
            Logger.d("begin to sync data...");
        }

        final Context context = getApplicationContext();
        int syncType = intent.getIntExtra(DataManager.SYNC_TYPE, -1);
        DataProvider dataProvider = DataProvider.getProvider(context);
        try {
            switch (syncType) {
                case DataManager.TYPE_INIT:

                    dataProvider.setCompleted(false);
                    ArrayList<ContactGroup> contactGroups = SocialManager.getContacts2(context);
                    if (contactGroups != null && contactGroups.size() > 0) {
                        for (ContactGroup cg : contactGroups) {
                            if (cg.set.equals("dialogs")) {
                                dataProvider.setConversations(cg.collection);
                            } else if (cg.set.equals("contacts")) {
                                dataProvider.setPersons(cg.collection);
                            } else if (cg.set.equals("classes")) {
                                dataProvider.setClasses(cg.collection);
                            }
                        }
                    }

                    ArrayList<Contact> conversations = dataProvider.getConversations();
                    ArrayList<Contact> persons = dataProvider.getPersons();
                    ArrayList<Contact> classes = dataProvider.getClasses();


                    Map<String, Integer> conversationsMapping
                            = dataProvider.getConversationsMapping();
                    conversationsMapping.clear();
                    int count = conversations.size();
                    for (int i = 0; i < count; i++) {
                        Contact contact = conversations.get(i);
                        conversationsMapping.put(contact.id, i);
                    }

                    Map<String, Contact> personsMapping = dataProvider.getPersonsMapping();
                    personsMapping.clear();
                    for (Contact contact : persons) {
                        personsMapping.put(contact.id, contact);
                    }

                    Map<String, Contact> classesMapping = dataProvider.getClassesMapping();
                    classesMapping.clear();
                    for (Contact contact : classes) {
                        classesMapping.put(contact.id, contact);
                    }

                    break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (XiaojsConfig.DEBUG) {
                Logger.d("sync data end ...");
            }

            dataProvider.dispatchLoadComplete();
        }


    }
}
