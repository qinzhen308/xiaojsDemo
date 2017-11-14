package cn.xiaojs.xma.data.provider;

import cn.xiaojs.xma.model.social.Contact;

/**
 * Created by maxiaobao on 2017/11/3.
 */

public abstract class DataObserver {

    // 缓存加载数据完成
    public abstract void onLoadComplete();


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Conversation
    //

    // 会话列表中的某条属性数据有更新
    public void onConversationUpdate(Contact contact, int index){}
    // 会话列表中有新的会话加入
    public void onConversationInsert(Contact contact, int insertIndex) {}
    // 会话列表中的某条会话位置有移动
    public void onConversationMove(Contact contact, int fromIndex, int toIndex){}
    // 会话列表中的某条会话被删除
    public void onConversationRemoved(String conversationId){}


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Classes
    //
    public void onClassesUpdate(int action, Contact contact){}


    //......continue
}
