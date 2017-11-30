package cn.xiaojs.xma.ui.classroom2.core;

/**
 * Created by maxiaobao on 2017/11/3.
 */

public abstract class SessionDataObserver {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //
    public void onYouRemovedFromCurrentClass(){}

    public void onKickoutByLeft(){}
    public void onKickoutByLogout(){}
    public void onKickoutByConsttraint(){}
    public void onClosePreviewByClassOver() {}



    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Member
    //
    public void onMemberUpdated(){}


}
