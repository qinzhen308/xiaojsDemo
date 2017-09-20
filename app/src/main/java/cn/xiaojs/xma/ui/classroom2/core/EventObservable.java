package cn.xiaojs.xma.ui.classroom2.core;

import cn.xiaojs.xma.model.socket.room.EventReceived;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maxiaobao on 2017/9/12.
 */

public class EventObservable extends Observable<EventReceived> {

    private EventListener eventListener;

    public EventObservable eventListener(EventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }


    @Override
    protected void subscribeActual(Observer<? super EventReceived> observer) {
        eventListener.setObserver(observer);
        observer.onSubscribe(eventListener);
        eventListener.onEvent();
    }


    public static EventObservable createEventObservable() {
        EventObservable observable = new EventObservable();
        observable.observeOn(Schedulers.io());
        return observable;
    }

}
