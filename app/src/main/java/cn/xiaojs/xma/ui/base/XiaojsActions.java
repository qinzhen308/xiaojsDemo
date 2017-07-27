package cn.xiaojs.xma.ui.base;

import android.content.Intent;

/**
 * Created by Paul Z on 2017/6/28.
 */

public interface XiaojsActions {
    String ACTION_TO_MY_CLASSES="xiaojs_action_my_classes";
    String ACTION_TO_MY_LESSONS="xiaojs_action_my_lessons";
    String ACTION_TO_CLASSROOM="xiaojs_action_classroom";
    String ACTION_TO_MY_RECORDED_LESSONS="xiaojs_action_my_recorded_lessons";


    void doAction(String action,Intent intent);
}
