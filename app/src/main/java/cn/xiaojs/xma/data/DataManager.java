package cn.xiaojs.xma.data;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ErrorPrompts;
import cn.xiaojs.xma.data.db.ContactDao;
import cn.xiaojs.xma.data.loader.DataLoder;
import cn.xiaojs.xma.data.loader.SyncService;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.SecurityUtil;
import okhttp3.ResponseBody;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

    public static final String EXTRA_CONTACT = "econtact";

    private static MemCache getCache(Context context) {
        return MemCache.getDataCache(context);
    }


    /**
     * init memory data cache when the user already logined
     */
    public static void init(Context context) {

        if (AccountDataManager.isLogin(context)) {
            //init data cache
            getCache(context).init();
        }
    }

    public static Map<Long, ContactGroup> getGroupData(Context context) {
        return getCache(context).getGroupData();
    }

    public static void getFriendsByType(Context context,
                                        int followType,
                                        DataLoder.DataLoaderCallback<ArrayList<ContactGroup>> callback) {
        DataLoder dataLoder = new DataLoder(context,new ContactDao());
        dataLoder.load(callback,-1,followType);
    }

    public static void getFriendsOnly(Context context,
                                        DataLoder.DataLoaderCallback<ArrayList<ContactGroup>> callback) {
        DataLoder dataLoder = new DataLoder(context,new ContactDao());
        dataLoder.load(callback,-2,DataManager.getGroupData(context));
    }


    public static void getClasses(Context context,
                                  DataLoder.DataLoaderCallback<ArrayList<ContactGroup>> callback) {
        DataLoder dataLoder = new DataLoder(context,new ContactDao());
        dataLoder.load(callback,-1,-1);
    }

//    public static ArrayList<ContactGroup> getContactGroupData(Context context) {
//        return getCache(context).getContactGroupData();
//    }

    public static void syncData(Context context,Intent intent) {

        //intent.setClass(context,SyncService.class);
        context.startService(intent);
    }

    /**
     * Clear the cache and local data
     * @param context
     */
    public static void clearAllData(Context context) {
        //memory data
        MemCache.getDataCache(context).clear();
        //cache files
        clearCacheFiles(context);
        //local data
        ContactDao.clear(context);
    }

    /**
     * Clear the cache and local data
     * @param context
     */
    public static void clearCacheFiles(Context context) {
        //api cache
        clearAPICache(context);

        //TODO 清除图片缓存
        //img cache
        //Glide.getPhotoCacheDir();
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

    public static void syncGroupData(Context context,Map<Long, ContactGroup> map) {

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

    public static void syncContactData(Context context,ArrayList<ContactGroup> contactGroups) {

        if (contactGroups == null) {
            return;
        }

//        DataManager.MemCache cache = getCache(context);
//        cache.syncContactData(contactGroups);

        ContactDao contactDao = new ContactDao();
        contactDao.clearContacts(context);
        contactDao.addContact(context, contactGroups);
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
        contactDao.deleteContact(context,cid);

    }

    public static void addContact(Context context,long gid,Contact contact) {

        ContactDao contactDao = new ContactDao();
        contactDao.addGroup(context,gid,contact);
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
            if(jo.has(node)) {
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

    /**
     * clear cache data which create by api service
     */
    public static void clearAPICache(Context context) {

        File cacheDir = new File(context.getCacheDir(), XiaojsConfig.HTTP_CACHE_DIR);
        FileUtil.clearDirFiles(cacheDir);

    }





    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Cache class

    public static class MemCache {

        private static MemCache cache;

        private Context context;
        private Map<Long, ContactGroup> groupMap;
        //private ArrayList<ContactGroup> contactGroups;

        private MemCache(Context context) {
            this.context = context.getApplicationContext();
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
            //FIXME if has a lot of groups, need to work background thread to avoid ANR
            //init group cache data
            initGroupData();

            //initContact(groupMap);

            if (XiaojsConfig.DEBUG) {
                Logger.d("MemCache has init...");
            }
        }


        protected void clear() {

            if (groupMap !=null) {
                groupMap.clear();
            }
        }


        protected Map<Long, ContactGroup> getGroupData() {
            return groupMap;
        }

//        protected ArrayList<ContactGroup> getContactGroupData() {
//            return contactGroups;
//        }


        ///////////////////////////////////////////////////////////////
        //Contact

        private void initGroupData() {

            addDefaultGroup();

            //load from db
            ContactDao contactDao = new ContactDao();
            Map<Long, ContactGroup> map = contactDao.getGroups(context);
            if (map != null) {
                groupMap.putAll(map);
            }

        }

//        private void initContact(Map<Long, ContactGroup> map) {
//
//            if (map ==null)
//                return;
//
//            ContactDao contactDao = new ContactDao();
//            ArrayList<ContactGroup> contacts = contactDao.getContacts(context,map);
//            if (contacts!=null) {
//                if (contactGroups == null) {
//                    contactGroups = new ArrayList<>(contacts.size());
//                }
//                contactGroups.addAll(contacts);
//            }
//        }

//        public void syncContactData(ArrayList<ContactGroup> contacts) {
//
//            if (contactGroups == null) {
//                contactGroups = new ArrayList<>(contacts);
//            }else{
//                contacts.clear();
//                contacts.addAll(contacts);
//            }
//
//        }

        public void syncGroupData(Map<Long, ContactGroup> map) {

            if (groupMap != null) {
                groupMap.clear();
            }

            addDefaultGroup();

            if (map != null) {
                groupMap.putAll(map);
            }

        }

//        private void removeContact(long gid,String cid) {
//            if (groupMap == null) {
//                return;
//            }
//
//            ContactGroup contactGroup = groupMap.get(gid);
//
//            if (contactGroup != null) {
//                Contact rc = null;
//                for (Contact c : contactGroup.collection) {
//                    if (c.account.equals(cid)) {
//                        rc = c;
//                        break;
//                    }
//                }
//
//                if (rc != null) {
//                    contactGroup.collection.remove(rc);
//                }
//            }
//        }

//        private void addContact(long gid,Contact contact) {
//
//            if (contact == null)
//                return;
//
//            ContactGroup contactGroup = groupMap.get(gid);
//            contactGroup.collection.add(contact);
//
//            if (contactGroups == null) {
//                contactGroups = new ArrayList<>();
//                contactGroups.add(contactGroup);
//            }
//
//        }

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
