package cn.xiaojs.xma.data;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/3/27
 * Desc:
 *
 * ======================================================================================== */

import java.util.Vector;

public class DataChangeHelper {
    private static DataChangeHelper mInstance;
    private Vector<DataChangeListener> mDataChangeListener;

    private DataChangeHelper () {
        mDataChangeListener = new Vector<DataChangeListener>();
    }

    public static synchronized DataChangeHelper getInstance() {
        if (mInstance == null) {
            mInstance = new DataChangeHelper();
        }

        return mInstance;
    }

    public void registerDataChangeListener(DataChangeListener listener) {
        if (listener != null) {
            mDataChangeListener.add(listener);
        }
    }

    public void unregisterDataChangeListener(DataChangeListener listener) {
        if (listener != null) {
            mDataChangeListener.remove(listener);
        }
    }

    public void notifyDataChanged() {
        if (mDataChangeListener != null) {
            for (DataChangeListener listener : mDataChangeListener) {
                listener.onDataChange();
            }
        }
    }

    public void notifyDataChanged(SimpleDataChangeListener.ListenerType type) {
        if (mDataChangeListener != null) {
            for (DataChangeListener listener : mDataChangeListener) {
                if (listener instanceof SimpleDataChangeListener && ((SimpleDataChangeListener)listener).getType() == type) {
                    listener.onDataChange();
                }
            }
        }
    }

    public void unregisterAllDataChangeListener() {
        mDataChangeListener.clear();
    }

    public void destroy() {
        unregisterAllDataChangeListener();
        mDataChangeListener = null;
    }

}
