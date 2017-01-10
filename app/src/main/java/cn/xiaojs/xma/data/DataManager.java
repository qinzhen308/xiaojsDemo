package cn.xiaojs.xma.data;


import android.content.Context;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ErrorPrompts;
import cn.xiaojs.xma.data.db.ContactDao;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.SecurityUtil;
import okhttp3.ResponseBody;

import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by maxiaobao on 2016/10/25.
 */

public class DataManager {

    /**
     * init memory data cache when the user already logined
     */
    public static void init(Context context) {

        if (AccountDataManager.isLogin(context)) {

            MemCache cache = MemCache.getDataCache(context);
            //init data cache
            cache.init();

        }


    }

    /**
     * return groups from mem cache
     * @param context
     * @return
     */
    public static Map<Long, ContactGroup> getGroupCache(Context context) {
        MemCache cache = MemCache.getDataCache(context);
        return cache.getGroupData();
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
     * Refresh group data by remote api service, then to update cache and DB
     */
    public static void refreshGroupData(final Context context) {

        AccountDataManager.getHomeData(context, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {

                if (object == null)
                    return;

                try {
                    Map<Long, ContactGroup> map = parseGroupIntoMap(object.string());
                    if (map != null) {

                        //update group data cache
                        MemCache cache = MemCache.getDataCache(context);
                        cache.refreshGroupData(map);

                        //update group data to DB
                        ContactGroup[] groups = new ContactGroup[map.size()];
                        map.values().toArray(groups);
                        ContactDao contactDao = new ContactDao();
                        contactDao.clearGroup(context);
                        contactDao.addGroup(context, groups);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


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

            //init group cache data
            initGroupData();

            if (XiaojsConfig.DEBUG) {
                Logger.d("MemCache has init...");
            }
        }

        public Map<Long, ContactGroup> getGroupData() {
            return groupMap;
        }

        ///////////////////////////////////////////////////////////////
        //Contact group

        private void initGroupData() {

            addDefaultGroup();

            //FIXME if has a lot of groups, need to work background thread to avoid ANR
            //load from db
            ContactDao contactDao = new ContactDao();
            Map<Long, ContactGroup> map = contactDao.getGroups(context);
            if (map != null) {
                groupMap.putAll(map);
            }

        }

        private void refreshGroupData(Map<Long, ContactGroup> map) {

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
