package cn.xiaojs.xma.data.loader;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Map;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.model.social.ContactGroup;

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

        try {
            Context context = getApplicationContext();

            int syncType = intent.getIntExtra(DataManager.SYNC_TYPE,-1);
            switch (syncType) {
                case DataManager.TYPE_CONTACT:
                    ArrayList<ContactGroup> entry = intent.
                            getParcelableArrayListExtra(DataManager.EXTRA_CONTACT);
                    if (entry != null){
                        DataManager.syncContactData(context,entry);
                    }
                    break;
                default:
                    ArrayList<ContactGroup> contactGroups = SocialManager.getContacts(context);
                    DataManager.syncContactData(context,contactGroups);

                    Map<Long, ContactGroup> map = AccountDataManager.getHomeData(context);
                    if (contactGroups!=null) {

                        for (ContactGroup group : contactGroups){
                            ContactGroup cg = map.get(group.group);
                            if (cg !=null) {
                                cg.collection = group.collection;
                            }
                        }
                    }
                    DataManager.syncGroupData(context,map);

                    break;


            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        if (XiaojsConfig.DEBUG) {
            Logger.d("sync data end ...");
        }

    }
}