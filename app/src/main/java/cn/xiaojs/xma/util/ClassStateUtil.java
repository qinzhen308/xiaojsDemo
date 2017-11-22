package cn.xiaojs.xma.util;

import android.content.Context;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;

import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.ctl.ClassInfo;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Paul Z on 2017/11/22.
 */

public class ClassStateUtil {


    /**
     *  判断能不能进教室
     */
    public static void checkClassroomStateForMe(final Context context, final String id, final ClassStateCallback classStateCallback){

        LessonDataManager.getClassInfo(context, id, new APIServiceCallback<ClassInfo>() {
            @Override
            public void onSuccess(final ClassInfo object) {

                if(object!=null){
                    if(object.visitor){
                        classStateCallback.onClassroomOpen(object.ticket);
                    }else if(DataProvider.getProvider(context).isCompleted()){
                        if(DataProvider.getProvider(context).existInClasses(object.id)){
                            classStateCallback.onClassroomOpen(object.ticket);
                        }else {
                            classStateCallback.onClassroomClose(object.id);
                        }
                    }else {
                        //缓存没有，请求接口
                        Observable.just(object.id).observeOn(Schedulers.io()).map(new Function<String, String>() {
                            @Override
                            public String apply(@NonNull String s) throws Exception {
                                try {
                                    ArrayList<ContactGroup> groups=SocialManager.getContacts2(context);
                                    if(ArrayUtil.isEmpty(groups)){
                                        return "close";
                                    }
                                    for(ContactGroup group:groups){
                                        if("classes".equals(group.set)&&!ArrayUtil.isEmpty(group.collection)){
                                            for(Contact contact:group.collection){
                                                if(contact.id.equals(object.id)){
                                                    return "open";
                                                }
                                            }
                                        }
                                        break;
                                    }
                                }catch (IOException e){
                                    Logger.d(e);
                                    return "获取失败";
                                }
                                return "close";
                            }
                        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                if("open".equals(s)){
                                    classStateCallback.onClassroomOpen(object.ticket);
                                }else if("close".equals(s)){
                                    classStateCallback.onClassroomClose(object.id);
                                }else {
                                    classStateCallback.onError(s);
                                }
                            }
                        });
                    }
                }else {
                    classStateCallback.onError("班级不存在");
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                classStateCallback.onError(errorMessage);
            }
        });

    }


    public interface ClassStateCallback{
        void onClassroomOpen(String ticket);
        void onClassroomClose(String id);
        void onError(String msg);
    }
}
