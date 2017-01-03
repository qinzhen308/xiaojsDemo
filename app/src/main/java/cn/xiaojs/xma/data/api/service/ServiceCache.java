package cn.xiaojs.xma.data.api.service;

import android.os.Handler;
import android.os.Message;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Method;

import cn.xiaojs.xma.XiaojsConfig;
import okhttp3.Cache;
import okhttp3.Request;

/**
 * Created by maxiaobao on 2016/12/26.
 */

public class ServiceCache<T> {

    private Cache cache;
    private CacheCallback<T> callback;

    public ServiceCache(Cache cache) {
        this.cache = cache;
    }

    public void loadCache(Request request,CacheCallback<T> callback,Class<T> pClass,Class... _class) {

        if (request == null) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the load cache request is null,so return");
            }
            return;
        }

        this.callback = callback;
        new Thread(new LoadCacheTask(request,pClass,_class)).start();
    }

     T loadCache(Request request,Class<T> pClass,Class... _class) {

        T object = null;

        try {
            object = read(request,pClass,_class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    private T read(Request request,Class<T> pClass,Class... _class) throws Exception{

        Method method = cache.getClass().getDeclaredMethod("get", Request.class);
        //for invoke private or protected method
        method.setAccessible(true);

        okhttp3.Response response = (okhttp3.Response) method.invoke(cache,request);
        String resStr = response.body().string();
        ObjectMapper objectMapper = new ObjectMapper();

        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(pClass,_class);

        return objectMapper.readValue(resStr,javaType);

    }

    public interface CacheCallback<T> {
        void loadCacheCompleted(T entity);
    }


    private Handler cacheHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (callback !=null) {
                callback.loadCacheCompleted((T)msg.obj);
            }

            //super.handleMessage(msg);
        }
    };

    private class LoadCacheTask implements Runnable {

        Request request;
        Class<T> tClass;
        Class[] classes;

        public LoadCacheTask(Request req,Class<T> tClass,Class... classes){
            request = req;
            this.tClass = tClass;
            this.classes = classes;
        }

        @Override
        public void run() {

            Message message = new Message();
            message.obj = loadCache(request,tClass,classes);
            message.what = 0;

            cacheHandler.sendMessage(message);


        }
    }
}
