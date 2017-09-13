package cn.xiaojs.xma.ui.classroom2;

import android.content.Context;

import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.ShareboardReceive;
import cn.xiaojs.xma.model.socket.room.SyncBoardReceive;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
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



    public EventObservable getEventObservable(Context context) {

        this.observeOn(Schedulers.io())
                .doOnNext(new Consumer<EventReceived>() {
                    @Override
                    public void accept(EventReceived eventReceived) throws Exception {

                        switch (eventReceived.eventType) {
                            case Su.EventType.SYNC_BOARD:
                                SyncboardHelper.filterBoardData((SyncBoardReceive) eventReceived.t);
                                break;
                            case Su.EventType.SHARE_BOARD:
                                SyncboardHelper.handleShareBoardData((ShareboardReceive) eventReceived.t);
                                break;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        return this;
    }

}
