package cn.xiaojs.xma.data;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ErrorPrompts;
import cn.xiaojs.xma.data.db.ContactDao;
import cn.xiaojs.xma.data.loader.DataLoder;
import cn.xiaojs.xma.data.loader.SyncService;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.data.preference.DataPref;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.util.APPUtils;
import cn.xiaojs.xma.util.DataCacheManager;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.JpushUtil;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by maxiaobao on 2016/10/25.
 */

public class DataManager {

    public static final String SYNC_TYPE = "stype";
    public static final int TYPE_CONTACT = 1;
    public static final int TYPE_INIT = 2;
    public static final int TYPE_FETCH_CONTACT_FROM_NET = 3;

    public static final String EXTRA_CONTACT = "econtact";
    public static final String EXTRA_GROUP = "egroup";

    public static final String ACTION_UPDATE_CONTACT_FROM_DB = "cn.xiaojs.xma.update_contact_from_db";


    public static MemCache getCache(Context context) {
        return MemCache.getDataCache(context);
    }


    /**
     * init memory data cache when the user already logined
     */
    public static void init(final Context context) {

        saveVersionCode(context, APPUtils.getAPPVersionCode(context));

        if (AccountDataManager.isLogin(context)) {

            XiaojsConfig.mLoginUser = AccountDataManager.getUserInfo(context);
            XiaojsConfig.AVATOR_TIME = AccountPref.getAvatorTime(context);

            lanuchInitDataService(context, null);


            //jpush alias/tags
            AccountDataManager.setAliaTagsWithCheck(context);

        }
    }

    public static void lanuchInitDataService(Context context, HashMap<Long, String> groupMap) {
        Intent i = new Intent(context, SyncService.class);
        i.putExtra(SYNC_TYPE, TYPE_INIT);

        if (groupMap != null) {
            i.putExtra(EXTRA_GROUP,groupMap);
        }

        context.startService(i);
    }

    public static void lanuchLoadContactService(Context context) {
        Intent i = new Intent(context, SyncService.class);
        i.putExtra(DataManager.SYNC_TYPE,DataManager.TYPE_FETCH_CONTACT_FROM_NET);
        context.startService(i);
    }

    public static void lanuchContactService(Context context, ArrayList<ContactGroup> contactGroups) {
        Intent i = new Intent(context, SyncService.class);
        i.putExtra(DataManager.SYNC_TYPE,DataManager.TYPE_CONTACT);
        i.putExtra(DataManager.EXTRA_CONTACT,contactGroups);
        context.startService(i);
    }

    /**
     * 初始化APP内存缓存数据，比较耗时
     * @param context
     */
    public static void initMemCache(Context context) {
        getCache(context).init();
    }

    /**
     * 持久化客户端version code
     */
    private static void saveVersionCode(Context context, int code) {
        DataPref.setVersionCode(context, code);
    }

    /**
     * 获取缓存的联系人分组
     */
    public static Map<Long, ContactGroup> getGroupData(Context context) {
        return getCache(context).getGroupData();
    }

    public static void getFriendsByType(Context context,
                                        int followType,
                                        DataLoder.DataLoaderCallback<ArrayList<ContactGroup>> callback) {
        DataLoder dataLoder = new DataLoder(context, new ContactDao());
        dataLoder.load(callback, -1, followType);
    }

    public static void getFriendsOnly(Context context,
                                      DataLoder.DataLoaderCallback<ArrayList<ContactGroup>> callback) {
        DataLoder dataLoder = new DataLoder(context, new ContactDao());
        dataLoder.load(callback, -2, DataManager.getGroupData(context));
    }


    public static void getClasses(Context context,
                                  DataLoder.DataLoaderCallback<ArrayList<ContactGroup>> callback) {
        DataLoder dataLoder = new DataLoder(context, new ContactDao());
        dataLoder.load(callback, -1, -1);
    }

//    public static ArrayList<ContactGroup> getContactGroupData(Context context) {
//        return getCache(context).getContactGroupData();
//    }

    /**
     * 清除内存、缓存文件、数据库数据
     */
    public static void clearAllData(Context context) {
        clearMemoryData(context);
        clearLocalData(context, true);
    }

    /**
     * 用于用户清理缓存使用
     */
    public static void clearbyUser(Context context) {
        clearLocalData(context, false);
    }

    /**
     * 清除内存缓存数据
     */
    public static void clearMemoryData(Context context) {
        //memory data
        MemCache.getDataCache(context).clear();
        Glide.get(context).clearMemory();
    }


    private static void clearLocalData(Context context, final boolean db) {
        final Context appcontext = context.getApplicationContext();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //cache files
                clearCacheFiles(appcontext);
                if (db) {
                    //db data
                    ContactDao.clear(appcontext);
                }

            }
        }).start();

    }

    private static void clearCacheFiles(Context context) {
        //api cache
        clearAPICache(context);
        //清除本地图片缓存
        //Glide.get(context).clearMemory();
        Glide.get(context).clearDiskCache();
    }

    /**
     * clear cache data which create by api service
     */
    private static void clearAPICache(Context context) {
        File cacheDir = new File(context.getCacheDir(), XiaojsConfig.HTTP_CACHE_DIR);
        FileUtil.clearDirFiles(cacheDir);
    }

    public static String getLocalDataCache(Context context) {
        long size = 0 ;
        File cacheDir = new File(context.getCacheDir(), XiaojsConfig.HTTP_CACHE_DIR);
        try {
            size  = DataCacheManager.getFolderSize(cacheDir);
            size = size + DataCacheManager.getFolderSize(Glide.getPhotoCacheDir(context));
        } catch (Exception e) {

        }

        return DataCacheManager.getFormatSize(size);
    }


    /**
     * Add new group to cache and DB
     */
    public static void addGroupData(Context context, ContactGroup group) {

        if (group == null)
            return;

        //add to cache
        Map<Long, ContactGroup> groupMap = MemCache.getDataCache(context).getGroupData();
        if (groupMap != null) {
            groupMap.put(group.group, group);
        }

        //add to db
        ContactDao contactDao = new ContactDao();
        contactDao.addGroup(context, group);

    }

    /**
     * 通过用户ID，判断用户是否存在联系人中
     * @param context
     * @param accountId
     * @return
     */
    public static boolean existInContacts(Context context, String accountId) {
        if (!TextUtils.isEmpty(accountId)) {
            Set<String> ids = getCache(context).getContactIds();
            if (ids !=null && ids.contains(accountId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 同步分组数据到DB和Memory
     * @param context
     * @param map
     */
    public static void syncGroupData(Context context, Map<Long, ContactGroup> map) {

        if (map == null) {
            return;
        }

        DataManager.MemCache cache = getCache(context);
        cache.syncGroupData(map);

        ContactGroup[] groups = new ContactGroup[map.size()];
        map.values().toArray(groups);
        ContactDao contactDao = new ContactDao();
        contactDao.clearGroups(context);
        contactDao.addGroup(context, groups);

    }

    /**
     * 同步联系人数据到DB和Memory
     * @param context
     * @param contactGroups
     */
    public static void syncContactData(Context context, ArrayList<ContactGroup> contactGroups) {

        if (contactGroups == null) {
            return;
        }
        ContactDao contactDao = new ContactDao();
        contactDao.clearContacts(context);

        DataManager.MemCache cache = getCache(context);
        Set<String> ids = cache.getContactIds();
        ids.clear();

        contactDao.addContact(context, contactGroups,ids);
    }


//    public static void removeContact(Context context, long gid, String cid) {
//
//        getCache(context).removeContact(gid,cid);
//
//        ContactDao contactDao = new ContactDao();
//        contactDao.deleteContact(context,cid);
//
//    }

    public static void removeContact(Context context, String cid) {

        ContactDao contactDao = new ContactDao();
        contactDao.deleteContact(context, cid);

    }

    public static void addContact(Context context, long gid, Contact contact) {

        ContactDao contactDao = new ContactDao();
        contactDao.addGroup(context, gid, contact);
    }

    /**
     * Refresh group data by remote api service, then to update cache and DB
     */
//    public static void refreshGroupData(final Context context) {
//
//        AccountDataManager.getHomeData(context, new APIServiceCallback<ResponseBody>() {
//            @Override
//            public void onSuccess(ResponseBody object) {
//
//                if (object == null)
//                    return;
//
//                try {
//                    Map<Long, ContactGroup> map = parseGroupIntoMap(object.string());
//                    //update group data cache
//                    MemCache cache = MemCache.getDataCache(context);
//                    cache.refreshGroupData(map);
//
//                    if (map != null) {
//                        //update group data to DB
//                        ContactGroup[] groups = new ContactGroup[map.size()];
//                        map.values().toArray(groups);
//                        ContactDao contactDao = new ContactDao();
//                        contactDao.clearGroups(context);
//                        contactDao.addGroup(context, groups);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });
//    }
    public static void refreshContact(Context context) {

        SocialManager.getContacts(context, new APIServiceCallback<ArrayList<ContactGroup>>() {
            @Override
            public void onSuccess(ArrayList<ContactGroup> object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }


    public static ContactGroup createGroup(int groupId) {

        ContactGroup group = new ContactGroup();
        group.group = groupId;
        group.name = Social.getContactName(groupId);
        group.collection = new ArrayList<>(0);
        return group;

    }

    public static Map<Long, ContactGroup> parseGroupIntoMap(String body) {

        Map<Long, ContactGroup> groupMap = null;

        try {
            JSONObject jo = new JSONObject(body);
            String node = "contactGroups";
            if (jo.has(node)) {
                JSONObject jobject = jo.getJSONObject(node);

                Iterator<String> iterator = jobject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String name = jobject.getString(key);

                    ContactGroup contactGroup = new ContactGroup();
                    contactGroup.name = name;
                    contactGroup.group = Long.valueOf(key);
                    contactGroup.collection = new ArrayList<>(0);

                    if (groupMap == null) {
                        groupMap = new HashMap<>();
                    }

                    groupMap.put(contactGroup.group, contactGroup);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return groupMap;
    }


    /**
     * check session
     */
    public static boolean checkSession(String session, APIServiceCallback callback) {
        if (TextUtils.isEmpty(session)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the session is empty,so the request return failure");
            }

            if (callback != null) {
                String errorMessage = ErrorPrompts.getErrorMessage(-1, Errors.BAD_SESSION);
                callback.onFailure(Errors.BAD_SESSION, errorMessage);
            }

            return true;
        }

        return false;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Cache class

    public static class MemCache {

        private static MemCache cache;

        private Context context;
        private Map<Long, ContactGroup> groupMap;
        private Set<String> contactIds;
        //private ArrayList<ContactGroup> contactGroups;

        private MemCache(Context context) {
            this.context = context.getApplicationContext();
            contactIds = new HashSet<>();
        }

        private static MemCache getDataCache(Context context) {

            if (cache == null) {
                synchronized (ApiManager.class) {
                    if (cache == null) {
                        cache = new MemCache(context);
                    }
                }
            }
            return cache;
        }

        public void init() {
            //init cache data
            initContactData();

            if (XiaojsConfig.DEBUG) {
                Logger.d("MemCache init completed...");
            }
        }


        protected void clear() {

            if (groupMap != null) {
                groupMap.clear();
            }

            if (contactIds !=null) {
                contactIds.clear();
            }
        }

        protected Map<Long, ContactGroup> getGroupData() {
            return groupMap;
        }

        protected Set<String> getContactIds() {
            if (contactIds ==null) {
                contactIds = new HashSet<>();
            }
            return contactIds;
        }

        ///////////////////////////////////////////////////////////////
        //Contact

        private void initContactData() {

            addDefaultGroup();

            //load from db
            ContactDao contactDao = new ContactDao();
            Map<Long, ContactGroup> map = contactDao.getGroups(context);
            if (map != null) {
                groupMap.putAll(map);
            }

            //contacts
            contactIds = contactDao.getContactIds(context);

        }

        public void addContactId(String accountId) {
            if (contactIds == null) {
                contactIds = new HashSet<>();
            }
            contactIds.add(accountId);
        }

        public void removeContactId(String accountId) {
            if (contactIds != null) {
                contactIds.remove(accountId);
            }
        }

        public void syncGroupData(Map<Long, ContactGroup> map) {

            if (groupMap != null) {
                groupMap.clear();
            }

            addDefaultGroup();

            if (map != null) {
                groupMap.putAll(map);
            }

        }

        private void addDefaultGroup() {

            if (groupMap == null) {
                groupMap = new HashMap<>();
            }

            groupMap.put((long) Social.ContactGroup.TEACHERS, createGroup(Social.ContactGroup.TEACHERS));
            groupMap.put((long) Social.ContactGroup.STUDENTS, createGroup(Social.ContactGroup.STUDENTS));
            groupMap.put((long) Social.ContactGroup.CLASSMATES, createGroup(Social.ContactGroup.CLASSMATES));
            groupMap.put((long) Social.ContactGroup.FRIENDS, createGroup(Social.ContactGroup.FRIENDS));
            groupMap.put((long) Social.ContactGroup.ORGANIZATIONS, createGroup(Social.ContactGroup.ORGANIZATIONS));
            groupMap.put((long) Social.ContactGroup.COLLEAGUES, createGroup(Social.ContactGroup.COLLEAGUES));
            groupMap.put((long) Social.ContactGroup.STRANGERS, createGroup(Social.ContactGroup.STRANGERS));
        }


    }


}
