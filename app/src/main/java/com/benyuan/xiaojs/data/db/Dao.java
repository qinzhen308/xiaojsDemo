package com.benyuan.xiaojs.data.db;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/10/14
 * Desc:数据库访问类
 *
 * ======================================================================================== */

import android.content.Context;

import com.benyuan.xiaojs.data.db.ex.DbException;
import com.benyuan.xiaojs.data.db.sqlite.KeyValue;
import com.benyuan.xiaojs.data.db.sqlite.WhereBuilder;

import java.util.List;

public class Dao {

    private static Dao mInstance;
    private static Context mApp;
    private DbManager.DaoConfig config;
    private DbManager db;

    private static final String DB_NAME = "xiaojs.db";
    private static final int DB_VERSION = 1;

    public static void init(Context context){
        mApp = context;
    }

    public static Context getApp(){
        return mApp;
    }
    private Dao(){
        config = new DbManager.DaoConfig();
        config.setDbName(DB_NAME).setDbVersion(DB_VERSION).setAllowTransaction(true);
        db = DbManagerImpl.getInstance(config);
    }

    public static Dao getInstance(){
        if (mInstance == null){
            synchronized (Dao.class){
                mInstance = new Dao();
            }
        }
        return mInstance;
    }

    public boolean saveOrUpdate(Object object){
        try {
            db.saveOrUpdate(object);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean replace(Object entity){
        try {
            db.replace(entity);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean save(Object entity){
        try {
            db.save(entity);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveBindingId(Object entity){
        try {
            return db.saveBindingId(entity);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteById(Class<?> entityType, Object idValue){
        try {
            db.deleteById(entityType,idValue);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Object entity){
        try {
            db.delete(entity);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Class<?> entityType){
        try {
            db.delete(entityType);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int delete(Class<?> entityType, WhereBuilder whereBuilder){
        try {
            return db.delete(entityType,whereBuilder);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean update(Object entity, String... updateColumnNames){
        try {
            db.update(entity,updateColumnNames);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int update(Class<?> entityType, WhereBuilder whereBuilder, KeyValue... nameValuePairs){
        try {
            return db.update(entityType, whereBuilder, nameValuePairs);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    public <T> T findById(Class<T> entityType, Object idValue){
        try {
           return db.findById(entityType,idValue);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T findFirst(Class<T> entityType){
        try {
            return db.findFirst(entityType);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> List<T> findAll(Class<T> entityType){
        try {
            return db.findAll(entityType);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
}
