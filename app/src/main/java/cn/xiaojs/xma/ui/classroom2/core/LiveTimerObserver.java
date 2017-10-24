package cn.xiaojs.xma.ui.classroom2.core;

import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import cn.xiaojs.xma.XiaojsConfig;
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

    private OnTimeChangedListener timeChangedListener;

    public void setOnTimeChangedListener(OnTimeChangedListener timeChangedListener) {
        this.timeChangedListener = timeChangedListener;
    }


    public LiveTimerObserver(ClassroomStateMachine stateMachine) {
        this.stateMachine = stateMachine;
        observable = Observable.interval(1 * 1000, TimeUnit.MILLISECONDS, Schedulers.io());
    }

    public void startCounter() {

        stopObserver();

        long taketime = stateMachine.getSession().ctlSession.finishOn;
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

                        if (timeChangedListener != null) {
                            timeChangedListener.onTimeChanged(aLong,finishOn);
                        }


                    }
                });

    }

    public void stopObserver() {
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }

    public void destoryObserver() {
        stopObserver();
        stateMachine = null;
    }
}
