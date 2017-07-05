package cn.xiaojs.xma.ui.classroom2.state;

import android.os.Message;

import cn.xiaojs.xma.common.statemachine.State;
import cn.xiaojs.xma.ui.classroom2.CTLConstant;

/**
 * idle状态的班级
 */

public class IdleState extends State {

    @Override
    public boolean processMessage(Message msg) {

        switch(msg.what) {
            case CTLConstant.StateChannel.NEW_LESSON: //班级中排入了新课
                return true;
            case CTLConstant.StateChannel.NO_LESSON:  //班级中的课都已上完
                return true;
        }

        return false;
    }
}
