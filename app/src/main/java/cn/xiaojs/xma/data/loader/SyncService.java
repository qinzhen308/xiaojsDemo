package cn.xiaojs.xma.data.loader;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

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

        try {
            final Context context = getApplicationContext();

            int syncType = intent.getIntExtra(DataManager.SYNC_TYPE, -1);
            switch (syncType) {
                case DataManager.TYPE_FETCH_CONTACT_FROM_NET:

                    if (NetUtil.getCurrentNetwork(context) == NetUtil.NETWORK_NONE) {
                        return;
                    }
                    ArrayList<ContactGroup> cGroups = SocialManager.getContacts(context);
                    if (cGroups != null) {
                        DataManager.syncContactData(context, cGroups);

                        ContactDao contactDao = new ContactDao();
                        ArrayList<ContactGroup> newCGroups = contactDao.getContacts(context, DataManager.getGroupData(context));

                        Intent i = new Intent(DataManager.ACTION_UPDATE_CONTACT_FROM_DB);
                        i.putExtra(DataManager.EXTRA_CONTACT, newCGroups);
                        sendBroadcast(i);
                    } else {

                        DataManager.reloadContactWhenEmpty(context);
                    }

                    if (XiaojsConfig.DEBUG) {
                        Logger.d("sync service: TYPE_FETCH_CONTACT_FROM_NET");
                        //Logger.d(DataManager.getContacts(context));
                    }

                    break;
                case DataManager.TYPE_CONTACT:

                    ArrayList<ContactGroup> entry = (ArrayList<ContactGroup>) intent.
                            getSerializableExtra(DataManager.EXTRA_CONTACT);
                    if (entry != null) {
                        DataManager.syncContactData(context, entry);
                    }


                    if (XiaojsConfig.DEBUG) {
                        Logger.d("sync service: TYPE_CONTACT");
                        //Logger.d(DataManager.getContacts(context));
                    }

                    break;

                case DataManager.TYPE_INIT:

                    DataManager.initMemCache(context);

                    ArrayList<ContactGroup> contactGroups = null;
                    //同步联系人到DB

                    try {
                        if (NetUtil.getCurrentNetwork(context) != NetUtil.NETWORK_NONE) {
                            contactGroups = SocialManager.getContacts(context);
                            if (contactGroups == null) {

                                DataManager.reloadContactWhenEmpty(context);

                            } else {
                                DataManager.syncContactData(context, contactGroups);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //同步分组到DB和Memory cache
                    try {
                        HashMap<Long, String> cgMap = (HashMap<Long, String>) intent.getSerializableExtra(DataManager.EXTRA_GROUP);
                        Map<Long, ContactGroup> map = new HashMap<>();
                        if (cgMap != null) {
                            Set<Long> keys = cgMap.keySet();
                            for (Long key : keys) {
                                ContactGroup contactGroup = new ContactGroup();
                                contactGroup.name = cgMap.get(key);
                                contactGroup.group = key;
                                contactGroup.collection = new ArrayList<>(0);
                                map.put(key, contactGroup);
                            }

                        } else if (NetUtil.getCurrentNetwork(context) != NetUtil.NETWORK_NONE) {
                            map = AccountDataManager.getHomeData(context);
                        }

                        if (contactGroups != null && cgMap != null) {

                            for (ContactGroup group : contactGroups) {

                                ContactGroup cg = map.get(group.group);
                                if (cg != null) {
                                    cg.collection = group.collection;
                                }
                            }
                        }
                        DataManager.syncGroupData(context, map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //同步教学能力
                    try {
                        ClaimCompetency claimCompetency = AccountDataManager.getCompetencies(context);
                        if (claimCompetency != null) {
                            List<Competency> competencies = claimCompetency.competencies;
                            if (competencies != null) {
                                int count = competencies.size();
                                if (count > 0) {
                                    StringBuilder names = new StringBuilder();
                                    for (int i = 0; i < count; i++) {
                                        Competency competency = competencies.get(i);
                                        String name = competency.getSubject().getName();
                                        names.append(name);
                                        if (i < count - 1) {
                                            names.append("、");
                                        }
                                    }
                                    AccountDataManager.clearAbilities(context);
                                    AccountDataManager.addAbility(context, names.toString());
                                }
                            }


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //是否是老师
//                    if (NetUtil.getCurrentNetwork(context) != NetUtil.NETWORK_NONE) {
//                        Privilege[] privileges = SecurityManager.havePrivilegeSync(context,
//                                Su.Permission.COURSE_OPEN_CREATE);
//                        SecurityManager.savePermission(context, privileges);
//                    }

                    //check update


                    if (XiaojsConfig.DEBUG) {
                        Logger.d("sync service: TYPE_INIT");
                        //Logger.d(DataManager.getContacts(context));
                    }

                    break;


            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (XiaojsConfig.DEBUG) {
                Logger.d("sync data end ...");
            }
        }


    }
}
