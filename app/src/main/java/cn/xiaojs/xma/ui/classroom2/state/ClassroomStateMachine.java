package cn.xiaojs.xma.ui.classroom2.state;

import android.os.Handler;
import android.os.Looper;

import cn.xiaojs.xma.common.statemachine.StateMachine;

/**
 * Created by maxiaobao on 2017/7/4.
 */

public class ClassroomStateMachine extends StateMachine {

    public ClassroomStateMachine(String name) {
        super(name);
    }

    public ClassroomStateMachine(String name, Looper looper) {
        super(name, looper);
    }

    public ClassroomStateMachine(String name, Handler handler) {
        super(name, handler);
    }
}
