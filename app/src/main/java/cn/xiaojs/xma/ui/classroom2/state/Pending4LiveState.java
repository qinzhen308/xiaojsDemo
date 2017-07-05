package cn.xiaojs.xma.ui.classroom2.state;

import android.os.Message;

import cn.xiaojs.xma.common.statemachine.State;
import cn.xiaojs.xma.ui.classroom2.CTLConstant;

/**
 * Pending for live的班级
 */

public class Pending4LiveState extends State{

    @Override
    public boolean processMessage(Message msg) {
        switch (msg.what) {
            case CTLConstant.StateChannel.START_LESSON:   //开始上课
                return true;

        }
        return false;
    }
}
