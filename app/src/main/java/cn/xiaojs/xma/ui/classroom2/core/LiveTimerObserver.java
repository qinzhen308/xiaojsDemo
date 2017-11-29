package cn.xiaojs.xma.ui.classroom2.core;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maxiaobao on 2017/10/24.
 */

public class LiveTimerObserver {

    public interface OnTimeChangedListener {
        void onTimeChanged(long time, long reverseTime);
    }


    private ClassroomStateMachine stateMachine;
    private Observable<Long> observable;
    private Disposable disposable;

    private ArrayList<OnTimeChangedListener> timeChangedListeners;

    public void addOnTimeChangedListener(OnTimeChangedListener timeChangedListener) {
        if (timeChangedListeners == null) {
            timeChangedListeners = new ArrayList<>();
        }
        timeChangedListeners.add(timeChangedListener);
    }

    public void removeOnTimeChangedListener(OnTimeChangedListener timeChangedListener) {
        if (timeChangedListeners == null) {
            return;
        }
        timeChangedListeners.remove(timeChangedListener);
    }


    public LiveTimerObserver(ClassroomStateMachine stateMachine) {
        this.stateMachine = stateMachine;
        observable = Observable.interval(1 * 1000, TimeUnit.MILLISECONDS, Schedulers.io());
    }

    public void startCounter() {

        stopObserverNow();

        long taketime = stateMachine.getSession().ctlSession.finishOn;

        if (stateMachine.getLiveState().equals(Live.LiveSessionState.LIVE)
                && stateMachine.getIdentityInLesson() == CTLConstant.UserIdentity.LEAD
                && stateMachine.getSession().ctlSession.ctl != null) {
            long dur = stateMachine.getSession().ctlSession.ctl.duration;
            if (dur > 0) {
                taketime = dur * 60;
            }

        }

        if (XiaojsConfig.DEBUG) {
            Logger.d("the LiveTimer startCounter taketime:%d", taketime);
        }

        disposable = observable.take(taketime, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                        if (XiaojsConfig.DEBUG) {
                            Logger.d("the LiveTimer accept %d", aLong.intValue());
                        }

                        long finishOn = --stateMachine.getSession().ctlSession.finishOn;
                        if (finishOn < 0) {
                            finishOn = 0L;
                        }

                        stateMachine.getSession().ctlSession.finishOn = finishOn;
                        if (timeChangedListeners != null) {
                            for (OnTimeChangedListener timeChanged : timeChangedListeners) {
                                timeChanged.onTimeChanged(aLong, finishOn);
                            }

                        }


                    }
                });

    }

    public void stopObserverNow() {
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }

    public void destoryObserver() {
        stopObserverNow();
        if (timeChangedListeners !=null) {
            timeChangedListeners.clear();
            timeChangedListeners = null;
        }
        stateMachine = null;
    }
}
