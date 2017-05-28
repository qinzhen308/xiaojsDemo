package cn.xiaojs.xma.ui.lesson.xclass.util;

/**
 * Created by Paul Z on 2017/5/28.
 *
 * 通过这个接口衔接某些类和activity之间调用弹框的方法
 *
 * 比如，BaseActivity实现这个接口，那么LOpModel里想调用Activity中实现的cancelProgress方法时，
 * 不用与BaseActivity耦合到一起
 */

public interface IDialogMethod {

    public void showProgress(boolean cancellable);

    public void cancelProgress() ;
}
