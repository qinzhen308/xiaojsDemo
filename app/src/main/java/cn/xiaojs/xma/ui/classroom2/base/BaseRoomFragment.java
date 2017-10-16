package cn.xiaojs.xma.ui.classroom2.base;

import android.support.v4.app.Fragment;

import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;

/**
 * Created by maxiaobao on 2017/10/12.
 */

public class BaseRoomFragment extends Fragment {

    /**
     * 显示loading状态
     */
    public void showProgress(boolean cancelable) {
        ((Classroom2Activity) getActivity()).showProgress(cancelable);
    }

    /**
     * 退出loading状态
     */
    public void cancelProgress() {
        ((Classroom2Activity) getActivity()).cancelProgress();
    }

}
