package cn.xiaojs.xma.ui.message.im.chatkit.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.orhanobut.logger.Logger;

import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.NotificationDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.GNOResponse;
import cn.xiaojs.xma.model.NotificationCategory;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.ui.message.ContactActivity;
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
    protected ImageView people_imageView;

    protected LCIMCommonListAdapter<AVIMConversation> itemAdapter;
    protected LinearLayoutManager layoutManager;

    private UpdateReceiver updateReceiver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lcim_conversation_list_fragment, container, false);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_conversation_srl_pullrefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_conversation_srl_view);

        people_imageView = (ImageView) view.findViewById(R.id.people_image);

        refreshLayout.setEnabled(false);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new LCIMDividerItemDecoration(getActivity()));
        itemAdapter = new LCIMCommonListAdapter<AVIMConversation>(LCIMConversationItemHolder.class);
        recyclerView.setAdapter(itemAdapter);
        EventBus.getDefault().register(this);


        people_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ContactActivity.class));
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(LCIMConversationItemHolder.ACTION_UPDATE);
        updateReceiver = new UpdateReceiver();
        getContext().registerReceiver(updateReceiver,filter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //updateConversationList();
        getMessageOverview();
    }

    @Override
    public void onResume() {
        super.onResume();
        //updateConversationList();
        getMessageOverview();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

        if (updateReceiver!= null) {
            getContext().unregisterReceiver(updateReceiver);
            updateReceiver = null;
        }
    }


    /**
     * 收到对方消息时响应此事件
     */
    public void onEvent(LCIMIMTypeMessageEvent event) {
        //updateConversationList();
        getMessageOverview();
    }

    /**
     * 删除会话列表中的某个 item
     */
    public void onEvent(LCIMConversationItemLongClickEvent event) {
        if (null != event.conversation) {
            String conversationId = event.conversation.getConversationId();
            LCIMConversationItemCache.getInstance().deleteConversation(conversationId);
            //updateConversationList();
            getMessageOverview();
        }
    }

    /**
     * 刷新页面
     */
    private void updateConversationList(List<NotificationCategory> categoryList) {

        List<String> convIdList = LCIMConversationItemCache.getInstance().getSortedConversationList();

        List<AVIMConversation> conversationList = new ArrayList<>();
        if (categoryList != null && categoryList.size() > 0) {
            conversationList.addAll(categoryList);
        }


        for (String convId : convIdList) {
            conversationList.add(LCChatKit.getInstance().getClient().getConversation(convId));
        }

        itemAdapter.setDataList(conversationList);
        itemAdapter.notifyDataSetChanged();
    }

    /**
     * 离线消息数量发生变化是响应此事件
     * 避免登陆后先进入此页面，然后才收到离线消息数量的通知导致的页面不刷新的问题
     */
    public void onEvent(LCIMOfflineMessageCountChangeEvent updateEvent) {
        //updateConversationList();
        getMessageOverview();
    }


    private void getMessageOverview() {
        Pagination pagination = new Pagination();
        pagination.setPage(1);
        pagination.setMaxNumOfObjectsPerPage(100);

        NotificationDataManager.requestNotificationsOverview(getContext(), pagination,
                new APIServiceCallback<GNOResponse>() {
                    @Override
                    public void onSuccess(GNOResponse object) {

                        if (object != null && object.categories != null) {
                            updateConversationList(object.categories);
                        }else{
                            updateConversationList(null);
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        updateConversationList(null);
                    }
                });
    }


    private class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(LCIMConversationItemHolder.ACTION_UPDATE) && itemAdapter != null) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("conversation item update... ");
                }

                itemAdapter.notifyDataSetChanged();
            }
        }
    }
}
