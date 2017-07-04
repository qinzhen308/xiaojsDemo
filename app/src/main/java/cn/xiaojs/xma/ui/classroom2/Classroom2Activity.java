package cn.xiaojs.xma.ui.classroom2;

import android.support.v4.app.FragmentActivity;

/**
 * Created by maxiaobao on 2017/7/3.
 */

public class Classroom2Activity extends FragmentActivity implements ClassroomSession.SessionListener{





    @Override
    public void connectSuccess() {
        //TODO 提示连接成功
        //TODO 根据ClassroomSession,初始化界面
    }

    @Override
    public void connectFailed(String errorCode, String errorMessage) {
        //TODO 提示失败信息
        //TODO 弹出重新连接提示
    }
}
