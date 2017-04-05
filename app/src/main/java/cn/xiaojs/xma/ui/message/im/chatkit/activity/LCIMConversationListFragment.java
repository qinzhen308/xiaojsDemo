package cn.xiaojs.xma.ui.message.im.chatkit.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMConversation;

import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.message.im.chatkit.LCChatKit;
import cn.xiaojs.xma.ui.message.im.chatkit.LCIMCommonListAdapter;
import cn.xiaojs.xma.ui.message.im.chatkit.cache.LCIMConversationItemCache;
import cn.xiaojs.xma.ui.message.im.chatkit.event.LCIMConversationItemLongClickEvent;
import cn.xiaojs.xma.ui.message.im.chatkit.event.LCIMIMTypeMessageEvent;
import cn.xiaojs.xma.ui.message.im.chatkit.event.LCIMOfflineMessageCountChangeEvent;
import cn.xiaojs.xma.ui.message.im.chatkit.view.LCIMDividerItemDecoration;
import cn.xiaojs.xma.ui.message.im.chatkit.viewholder.LCIMConversationItemHolder;

/**
 * Created by wli on 16/2/29.
 * 会话列表页
 */
public class LCIMConversationListFragment extends Fragment {
  protected SwipeRefreshLayout refreshLayout;
  protected RecyclerView recyclerView;

  protected LCIMCommonListAdapter<AVIMConversation> itemAdapter;
  protected LinearLayoutManager layoutManager;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.lcim_conversation_list_fragment, container, false);

    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_conversation_srl_pullrefresh);
    recyclerView = (RecyclerView) view.findViewById(R.id.fragment_conversation_srl_view);

    refreshLayout.setEnabled(false);
    layoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.addItemDecoration(new LCIMDividerItemDecoration(getActivity()));
    itemAdapter = new LCIMCommonListAdapter<AVIMConversation>(LCIMConversationItemHolder.class);
    recyclerView.setAdapter(itemAdapter);
    EventBus.getDefault().register(this);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    updateConversationList();
  }

  @Override
  public void onResume() {
    super.onResume();
    updateConversationList();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    EventBus.getDefault().unregister(this);
  }

  /**
   * 收到对方消息时响应此事件
   *
   * @param event
   */
  public void onEvent(LCIMIMTypeMessageEvent event) {
    updateConversationList();
  }

  /**
   * 删除会话列表中的某个 item
   * @param event
   */
  public void onEvent(LCIMConversationItemLongClickEvent event) {
    if (null != event.conversation) {
      String conversationId = event.conversation.getConversationId();
      LCIMConversationItemCache.getInstance().deleteConversation(conversationId);
      updateConversationList();
    }
  }

  /**
   * 刷新页面
   */
  private void updateConversationList() {
    List<String> convIdList = LCIMConversationItemCache.getInstance().getSortedConversationList();
    List<AVIMConversation> conversationList = new ArrayList<>();
    for (String convId : convIdList) {
      conversationList.add(LCChatKit.getInstance().getClient().getConversation(convId));
    }

    itemAdapter.setDataList(conversationList);
    itemAdapter.notifyDataSetChanged();
  }

  /**
   * 离线消息数量发生变化是响应此事件
   * 避免登陆后先进入此页面，然后才收到离线消息数量的通知导致的页面不刷新的问题
   * @param updateEvent
   */
  public void onEvent(LCIMOfflineMessageCountChangeEvent updateEvent) {
    updateConversationList();
  }
}
