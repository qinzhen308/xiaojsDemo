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

    public ChatAdapter(Context context, ArrayList<TalkItem> messages) {
        this.context = context;
        this.messages = messages;
        this.myAccountId = AccountDataManager.getAccountID(context);
    }

    @Override
    public int getItemViewType(int position) {
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

}
