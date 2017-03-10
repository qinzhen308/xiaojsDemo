package cn.xiaojs.xma.ui.message;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/11/16
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.im.CircleImageView;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.model.Notification;
import cn.xiaojs.xma.model.NotificationCategory;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.MessageImageView;
import cn.xiaojs.xma.util.TimeUtil;

public class PlatformNotificationAdapter extends BaseAdapter {

    private int[] mIcons = new int[]{
            R.drawable.ic_message_invite,
            R.drawable.ic_message_course_information,
            R.drawable.ic_message_socialnews,
            R.drawable.ic_message_qanswerme,
            R.drawable.ic_message_transactionmessage,
            R.drawable.ic_message_recommendedselection,
            R.drawable.ic_message_xiaojs};

    private String titles[];
    private Context mContext;
    private List<NotificationCategory> beans;

    private UIHandler mUIHandler = new UIHandler(this);
    private static final int REFRESH_CONVERSATION_LIST = 0x3001;

    private CircleTransform circleTransform;

    public PlatformNotificationAdapter(Context context, List<NotificationCategory> beans, String[] titles) {
        mContext = context;
        this.titles = titles;
        this.beans = beans;
        circleTransform = new CircleTransform(context);
    }

    @Override
    public int getCount() {
        return beans == null ? titles.length : beans.size();
    }

    @Override
    public NotificationCategory getItem(int position) {
        return beans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_message_list_item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        if (position >= 0 && position <= 6) {
            holder.image.setImageResource(mIcons[position]);
            holder.title.setText(titles[position]);
            if (position == mIcons.length - 1) {
                holder.special();
                holder.image.setType(MessageImageView.TYPE_NUM);
            } else {
                holder.image.setType(MessageImageView.TYPE_MARK);
                holder.normal();
            }

            if (beans != null && beans.size() > 0) {
                NotificationCategory category = NotificationBusiness.getPlatformMessageCategory(beans, position);
                if (category != null) {
                    holder.image.setCount(category.count);
                    if (category.notifications != null && category.notifications.size() > 0) {
                        Notification notification = category.notifications.get(0);
                        holder.time.setText(TimeUtil.format(notification.createdOn, TimeUtil.TIME_YYYY_MM_DD_HH_MM));
                        holder.content.setText(notification.body);
                    }else {
                        holder.time.setText("");
                        holder.content.setText("");
                    }
                }else{
                    holder.image.setCount(0);
                    holder.time.setText("");
                    holder.content.setText("");
                }
            }else{
                holder.image.setCount(0);
                holder.time.setText("");
                holder.content.setText("");
            }
        } else {
            NotificationCategory category = beans.get(position);
            if (category != null && category.conversation != null) {
                holder.normal();
                holder.image.setType(MessageImageView.TYPE_NUM);
                holder.image.setCount(category.conversation.getUnReadMsgCnt());
                Message lastMsg = category.conversation.getLatestMessage();
                if (lastMsg != null) {
                    holder.time.setText(TimeUtil.format(new Date(lastMsg.getCreateTime()), TimeUtil.TIME_YYYY_MM_DD_HH_MM));
                    // 按照最后一条消息的消息类型进行处理
                    switch (lastMsg.getContentType()) {
                        case image:
                            holder.content.setText(mContext.getString(R.string.type_picture));
                            break;
                        case voice:
                            holder.content.setText(mContext.getString(R.string.type_voice));
                            break;
                        case location:
                            holder.content.setText(mContext.getString(R.string.type_location));
                            break;
                        case file:
                            holder.content.setText(mContext.getString(R.string.type_file));
                            break;
                        case video:
                            holder.content.setText(mContext.getString(R.string.type_video));
                            break;
                        case eventNotification:
                            holder.content.setText(mContext.getString(R.string.group_notification));
                            break;
                        case custom:
                            CustomContent customContent = (CustomContent) lastMsg.getContent();
                            Boolean isBlackListHint = customContent.getBooleanValue("blackList");
                            if (isBlackListHint != null && isBlackListHint) {
                                holder.content.setText(mContext.getString(R.string.jmui_server_803008));
                            } else {
                                holder.content.setText(mContext.getString(R.string.type_custom));
                            }
                            break;
                        default:
                            holder.content.setText(((TextContent) lastMsg.getContent()).getText());
                    }
                } else {
                    holder.time.setText("");
                    holder.content.setText("");
                }
                //单聊
                if (category.conversation.getType().equals(ConversationType.single)) {
                    holder.title.setText(category.conversation.getTitle());
                    UserInfo user = (UserInfo) category.conversation.getTargetInfo();

                    String avatar = Account.getAvatar(user.getUserName(),holder.image.getMeasuredWidth());

                    Glide.with(mContext)
                            .load(avatar)
                            .bitmapTransform(circleTransform)
                            .placeholder(R.drawable.default_avatar_grey)
                            .error(R.drawable.default_avatar_grey)
                            .into(holder.image);
//                    if (user != null && !TextUtils.isEmpty(user.getAvatar())) {
//                        final Holder finalHolder = holder;
//                        user.getAvatarBitmap(new GetAvatarBitmapCallback() {
//                            @Override
//                            public void gotResult(int status, String desc, Bitmap bitmap) {
//                                if (status == 0) {
//                                    finalHolder.image.setImageBitmap(bitmap);
//                                } else {
//                                    finalHolder.image.setImageResource(R.drawable.default_avatar_grey);
//                                }
//                            }
//                        });
//                    } else {
//                        holder.image.setImageResource(R.drawable.default_avatar_grey);
//                    }
                }else{
                    holder.image.setImageResource(R.drawable.default_avatar_grey);
                    holder.title.setText("");
                }
            }else {
                holder.image.setImageResource(R.drawable.default_avatar_grey);
                holder.title.setText("");
                holder.time.setText("");
                holder.content.setText("");
            }
        }

        return convertView;
    }

    public void setData(List<NotificationCategory> beans) {
        if (beans == null || beans.size() == 0)
            return;
        this.beans = beans;
        notifyDataSetChanged();
    }

    public List<NotificationCategory> getData() {
        return beans;
    }

    public synchronized void setToTop(Conversation conv) {
        if (beans == null) {
            beans = new ArrayList<>();
        }

        for (NotificationCategory category : beans) {
            //平台消息中的conversation可能为空
            if (category.conversation == null)
                continue;
            if (conv.getId().equals(category.conversation.getId())) {
                beans.remove(category);
                NotificationCategory cate = new NotificationCategory();
                cate.conversation = conv;
                beans.add(7, cate);
                mUIHandler.removeMessages(REFRESH_CONVERSATION_LIST);
                mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
                return;
            }
        }


        //如果是新会话
        NotificationCategory cate = new NotificationCategory();
        cate.conversation = conv;
        beans.add(7, cate);
        mUIHandler.removeMessages(REFRESH_CONVERSATION_LIST);
        mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
    }

    class Holder extends BaseHolder {
        @BindView(R.id.message_image)
        MessageImageView image;
        @BindView(R.id.message_title)
        TextView title;
        @BindView(R.id.message_time)
        TextView time;
        @BindView(R.id.message_content)
        TextView content;
        @BindView(R.id.message_head)
        View head;
        @BindView(R.id.message_top_divider)
        View topDivider;
        @BindView(R.id.message_bottom_divider)
        View bottomDivider;

        public void normal() {
            head.setVisibility(View.GONE);
            topDivider.setVisibility(View.GONE);
            bottomDivider.setVisibility(View.GONE);
        }

        public void special() {
            head.setVisibility(View.VISIBLE);
            topDivider.setVisibility(View.VISIBLE);
            bottomDivider.setVisibility(View.VISIBLE);
        }

        public Holder(View view) {
            super(view);
        }
    }

    static class UIHandler extends Handler {

        private final WeakReference<PlatformNotificationAdapter> mAdapter;

        public UIHandler(PlatformNotificationAdapter adapter) {
            mAdapter = new WeakReference<PlatformNotificationAdapter>(adapter);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            PlatformNotificationAdapter adapter = mAdapter.get();
            if (adapter != null) {
                switch (msg.what) {
                    case REFRESH_CONVERSATION_LIST:
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }

    public List<NotificationCategory> getBeans() {
        return beans;
    }

    public void notifyConversation(List<Conversation> conversations) {
        if (conversations != null && conversations.size() > 0) {
            for (Conversation conv : conversations) {
                for (NotificationCategory category : beans) {
                    if (category.conversation != null) {//替换原来的conversation
                        if (category.conversation.getId().equalsIgnoreCase(conv.getId())) {
                            category.conversation = conv;
                        } else {//新增conversation
                            NotificationCategory cate = new NotificationCategory();
                            cate.conversation = conv;
                            beans.add(7, cate);
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }
    }
}
