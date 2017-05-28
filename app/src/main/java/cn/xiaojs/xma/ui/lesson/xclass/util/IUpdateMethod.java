package cn.xiaojs.xma.ui.lesson.xclass.util;

/**
 * Created by Paul Z on 2017/5/28.
 * 同IDialogMethod
 */

public interface IUpdateMethod {

    /**
     *
     * @param justNative true：只刷新列表，不请求服务器
     */
    public void updateData(boolean justNative);
}
