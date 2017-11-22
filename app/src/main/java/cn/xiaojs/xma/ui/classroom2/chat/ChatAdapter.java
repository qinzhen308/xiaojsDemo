package cn.xiaojs.xma.ui.classroom2.chat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.XMSManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.social.Relation;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.RemoveTalk;
import cn.xiaojs.xma.ui.view.ChatPopupMenu;

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

        if (!TextUtils.isEmpty(talkItem.tips)) {
            return MessageType.TIPS;
        }

        if (talkItem.from != null && MessageType.TypeName.SYSTEM.equals(talkItem.from.accountId)) {
            return MessageType.SYSTEM;
        }

        if (!TextUtils.isEmpty(talkItem.signature) && talkItem.signature.equals(Su.getFollowSignature())) {
            return MessageType.FOLLOWED;
        }


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
                        inflater.inflate(R.layout.layout_classroom2_chat_sendout, parent, false), this);
                break;
            case MessageType.RECEIVED:
                holder = new ReceivedViewHolder(context,
                        inflater.inflate(R.layout.layout_classroom2_chat_received, parent, false), this);
                break;
            case MessageType.FOLLOWED:
                holder = new FollowedViewHolder(context,
                        inflater.inflate(R.layout.layout_classroom2_chat_followed, parent, false), this);
                break;
            case MessageType.SYSTEM:
                holder = new SystemViewHolder(context,
                        inflater.inflate(R.layout.layout_classroom2_chat_tips, parent, false), this);
                break;
            case MessageType.TIPS:
                holder = new TipsViewHolder(context,
                        inflater.inflate(R.layout.layout_classroom2_chat_tips, parent, false), this);
                break;
            case MessageType.LAND:
                holder = new LandViewHolder(context,
                        inflater.inflate(R.layout.layout_classroom2_chat_land, parent, false), this);
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
        return messages == null ? 0 : messages.size();
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

    public void toFollow(final TalkItem talkItem) {

        SocialManager.followContact(context,
                talkItem.extra.followedBy,
                talkItem.from.name,
                Social.ContactGroup.FRIENDS, null, new APIServiceCallback<Relation>() {
                    @Override
                    public void onSuccess(Relation object) {

                        if (object != null) {
                            talkItem.extra.followType = object.followType;
                            notifyDataSetChanged();
                        }

                        Toast.makeText(context, "您已关注", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {

                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showMenu(View view, final TalkItem item) {
        ChatPopupMenu chatPopupMenu = new ChatPopupMenu(context);
        chatPopupMenu.setMenuClickListener(new ChatPopupMenu.MenuClickListener() {
            @Override
            public void onMenuClick(View view) {
                switch (view.getId()) {
                    case R.id.delete:

                        //TODO

//                        RemoveTalk removeTalk = new RemoveTalk();
//                        removeTalk.
//
//                        XMSManager.sendRemoveTalk(context, , new EventCallback<EventResponse>() {
//                            @Override
//                            public void onSuccess(EventResponse response) {
//
//                            }
//
//                            @Override
//                            public void onFailed(String errorCode, String errorMessage) {
//
//                            }
//                        });
                        break;
                    case R.id.copy:
                        String text = item.body.text;
                        ClipboardManager cm = (ClipboardManager)
                                context.getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setPrimaryClip(ClipData.newPlainText(null, text));
                        Toast.makeText(context, "复制成功", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
        chatPopupMenu.show(view);
    }

}
