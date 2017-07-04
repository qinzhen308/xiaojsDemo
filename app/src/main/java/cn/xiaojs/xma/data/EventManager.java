package cn.xiaojs.xma.data;

import android.content.Context;

import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.api.socket.SocketListen;
import cn.xiaojs.xma.data.api.socket.SocketManager;
import cn.xiaojs.xma.model.socket.room.CloseMediaReceive;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;


/**
 * Created by maxiaobao on 2017/7/3.
 */

public class EventManager {

    public static class EventReceiver{

        private SocketListen socketListen;

        public EventReceiver(SocketManager socketManager) {
            this.socketListen = new SocketListen(socketManager);
            onBaseEvent();
        }

        private void onBaseEvent() {
            socketListen.on(
                    Su.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.CLOSE_MEDIA),
                    closeMediaObserver);
        }

        public <T> void addOnEvent(int eventCategory, int eventType, Observer<T> observer) {
            socketListen.on(Su.getEventSignature(eventCategory,eventType), observer);
        }

        public void removeOnEvent(int eventCategory, int eventType) {
            socketListen.off(Su.getEventSignature(eventCategory,eventType));
        }

        public void removeAllOnEvent() {
            socketListen.off();
        }

        private Observer<CloseMediaReceive> closeMediaObserver = new Observer<CloseMediaReceive>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull CloseMediaReceive closeMediaReceive) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };


    }
}
