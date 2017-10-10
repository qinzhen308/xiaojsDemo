package cn.xiaojs.xma.ui.classroom2.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.live.TalkItem;

/**
 * Created by maxiaobao on 2017/9/28.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {


    private Context context;
    private ArrayList<TalkItem> messages;
    private String myAccountId;
    private FetchMoreListener fetchMoreListener;
    private int autoFetchMoreSize = 3;
    private int perpageMaxCount = 0;
    private boolean firstLoad = true;


    public interface FetchMoreListener {
        void onFetchMoreRequested();
    }


    public ChatAdapter(Context context, ArrayList<TalkItem> messages) {
        this.context = context;
        this.messages = messages;
        this.myAccountId = AccountDataManager.getAccountID(context);
    }


    public void setFetchMoreListener(FetchMoreListener listener) {
        this.fetchMoreListener = listener;
    }

    public void setFirstLoad(boolean firstLoad) {
        this.firstLoad = firstLoad;
    }

    public void setAutoFetchMoreSize(int size) {
        this.autoFetchMoreSize = size;
    }

    public void setPerpageMaxCount(int count) {
        this.perpageMaxCount = count;
    }

    @Override
    public int getItemViewType(int position) {

        autoRequestFetchMoreData(position);

        TalkItem talkItem = messages.get(position);
        if (talkItem.from == null || myAccountId.equals(talkItem.from.accountId)) {
            return MessageType.SEND_OUT;
        }

        return MessageType.RECEIVED;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ChatViewHolder holder = null;

        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case MessageType.SEND_OUT:
                holder = new SendoutViewHolder(context,
                        inflater.inflate(R.layout.layout_classroom2_chat_sendout,parent,false));
                break;
            case MessageType.RECEIVED:
                holder = new ReceivedViewHolder(context,
                        inflater.inflate(R.layout.layout_classroom2_chat_received,parent,false));
                break;
            case MessageType.TIPS:
                holder = new TipsViewHolder(context,
                        inflater.inflate(R.layout.layout_classroom2_chat_tips,parent,false));
                break;
            case MessageType.LAND:
                holder = new LandViewHolder(context,
                        inflater.inflate(R.layout.layout_classroom2_chat_land,parent,false));
                break;

        }

        return holder;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        TalkItem item = messages.get(position);
        holder.bindData(item);
    }

    @Override
    public int getItemCount() {
        return messages == null? 0 : messages.size();
    }


    private void autoRequestFetchMoreData(int position) {

        if (firstLoad)
            return;

        if (getItemCount() == 0 // 都还没有数据，不自动触发加载
                || getItemCount() < perpageMaxCount) {
            return;
        }

        if (position > autoFetchMoreSize - 1) {
            return;
        }

        if (fetchMoreListener != null) {
            fetchMoreListener.onFetchMoreRequested();
        }
    }

}
