package cn.xiaojs.xma.data.loader;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import cn.xiaojs.xma.data.db.BaseDao;

/**
 * Created by maxiaobao on 2017/1/12.
 */

public class DataLoder {

    private Context context;
    private DataLoaderCallback callback;
    private BaseDao dao;

    public DataLoder(Context context, BaseDao dao) {
        this.context = context.getApplicationContext();
        this.dao = dao;

    }

    public void load(DataLoaderCallback callback, int ptype, Object... params) {
        this.callback = callback;
        new Thread(new DataRunnable(ptype, params)).start();
    }


    public interface DataLoaderCallback<T> {
        void loadCompleted(T t);
    }

    private class DataRunnable implements Runnable {

        private Object[] loadParams;
        private int ptype;


        protected DataRunnable(int type, Object[] params) {
            ptype = type;
            loadParams = params;
        }

        @Override
        public void run() {

            Message message = new Message();
            message.obj = dao.loadData(context,ptype,loadParams);
            message.what = 0;

            loadHandler.sendMessage(message);
        }
    }

    private Handler loadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (callback !=null) {
                callback.loadCompleted(msg.obj);
            }
        }
    };
}
