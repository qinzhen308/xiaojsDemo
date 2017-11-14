package cn.xiaojs.xma.data.api.socket.xms;

import android.content.Context;

import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maxiaobao on 2017/9/12.
 */

public class XMSEventObservable extends Observable<EventReceived> {

    private XMSEventObserver eventObserver;


    public XMSEventObservable (XMSEventObserver eventObserver) {
        this.eventObserver = eventObserver;
    }

    public XMSEventObserver getEventObserver() {
        return eventObserver;
    }

    @Override
    protected void subscribeActual(Observer<? super EventReceived> observer) {
        eventObserver.setObserver(observer);
        observer.onSubscribe(eventObserver);
        eventObserver.onEvent();
    }

    private static XMSEventObservable createEventObservable(XMSEventObserver observer) {
        XMSEventObservable observable = new XMSEventObservable(observer);
        observable.subscribeOn(Schedulers.io());
        return observable;
    }

    public static Disposable observeChatSession(Context context, Consumer<EventReceived> consumer) {
        XMSEventObserver.ChatSession observer = new XMSEventObserver.ChatSession(context);
        return createEventObservable(observer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    public static Disposable observeGlobalSession(Context context, Consumer<EventReceived> consumer) {
        XMSEventObserver.GlobalSession observer = new XMSEventObserver.GlobalSession(context);
        return createEventObservable(observer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }


    public static Disposable observeMainSession(Context context, Consumer<EventReceived> consumer) {
        XMSEventObserver.MainSession observer = new XMSEventObserver.MainSession(context);
        return createEventObservable(observer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

}
