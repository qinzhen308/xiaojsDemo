package cn.xiaojs.xma.ui.message;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.platform.NotificationTemplate;
import cn.xiaojs.xma.data.NotificationDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.GNOResponse;
import cn.xiaojs.xma.model.Notification;
import cn.xiaojs.xma.model.NotificationCategory;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMConversationUtils;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMLogUtils;
import cn.xiaojs.xma.ui.widget.MessageImageView;
import cn.xiaojs.xma.util.TimeUtil;


public class MessageFragment extends BaseFragment {

    @BindView(R.id.home_message_list)
    ListView listView;

    private MessageAdapter messageAdapter;

    @Override
    protected View getContentView() {
        View view = mContext.getLayoutInflater().inflate(R.layout.fragment_message, null);
        return view;
    }

    @Override
    protected void init() {
        messageAdapter = new MessageAdapter();
        listView.setAdapter(messageAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(messageAdapter == null) {
                    return;
                }

//                NotificationCategory category = messageAdapter.getItem(position);
//                enterCategoryList(category);
//
//                //消除红点
//                if (category.count > 0) {
//                    category.count = 0;
//                    messageAdapter.notifyDataSetChanged();
//                }

            }
        });

        getMessageOverview();
    }

    private void getMessageOverview() {
        Pagination pagination = new Pagination();
        pagination.setPage(1);
        pagination.setMaxNumOfObjectsPerPage(10);

        showProgress(true);

        NotificationDataManager.requestNotificationsOverview(mContext, pagination,
                new APIServiceCallback<GNOResponse>() {
            @Override
            public void onSuccess(GNOResponse object) {
                cancelProgress();
                if (object != null && object.categories != null) {
                    List<NotificationCategory> categoryList = object.categories;

                    getConversation(categoryList);

                    //messageAdapter.setData(categoryList);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                //getConversation(null);
            }
        });
    }

    private void getConversation(List<NotificationCategory> categoryList) {

        List<Conversation> conversations = JMessageClient.getConversationList();
        if (conversations != null) {

            if (categoryList == null) {
                categoryList = new ArrayList<>();
            }

            for (Conversation conversation : conversations) {
                NotificationCategory category = new NotificationCategory();
                category.from = NotificationCategory.MsgFrom.FROM_JMESSAGE;
                //category.conversation = conversation;
                categoryList.add(category);
            }
        }
    }

    private void enterCategoryList(NotificationCategory category) {

        Intent intent = new Intent(mContext, NotificationCategoryListActivity.class);
        intent.putExtra(NotificationConstant.KEY_NOTIFICATION_CATEGORY_ID, category.id);
        intent.putExtra(NotificationConstant.KEY_NOTIFICATION_TITLE, category.remarks);
        ((BaseActivity) mContext).startActivityForResult(intent, NotificationConstant.REQUEST_NOTIFICATION_CATEGORY_LIST);
    }

    private class MessageAdapter extends BaseAdapter {

        private List<AVIMConversation> conversations;

        public void setData(List<AVIMConversation> datas) {
            conversations = datas;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {

            if (conversations== null) {
                return 0;
            }
            return conversations.size();
        }

        @Override
        public AVIMConversation getItem(int position) {

            if (conversations == null){
                return null;
            }
            return conversations.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_msg_item, null);
                holder = new ViewHolder();
                holder.iconView = (MessageImageView) convertView.findViewById(R.id.message_image);
                holder.titleView = (TextView) convertView.findViewById(R.id.message_title);
                holder.timeView = (TextView) convertView.findViewById(R.id.message_time);
                holder.contentView = (TextView) convertView.findViewById(R.id.message_content);

                convertView.setTag(holder);

            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            AVIMConversation conversation = conversations.get(position);


            if (conversation instanceof NotificationCategory) {

                final NotificationCategory category = (NotificationCategory) conversation;

                if(category.name.equalsIgnoreCase(NotificationTemplate.INVITATION_NOTIFICATION)) {
                    holder.iconView.setImageResource(R.drawable.ic_message_invite);
                }else if(category.name.equalsIgnoreCase(NotificationTemplate.FOLLOW_NOTIFICATION)) {
                    holder.iconView.setImageResource(R.drawable.ic_message_socialnews);
                }else if(category.name.equalsIgnoreCase(NotificationTemplate.ANSWERS_NOTIFICATION)) {
                    holder.iconView.setImageResource(R.drawable.ic_message_qanswerme);
                }else if(category.name.equalsIgnoreCase(NotificationTemplate.ARTICLE_NOTIFICATION)) {
                    holder.iconView.setImageResource(R.drawable.ic_message_transactionmessage);
                }else if(category.name.equalsIgnoreCase(NotificationTemplate.CTL_NOTIFICATION)) {
                    holder.iconView.setImageResource(R.drawable.ic_message_course_information);
                }else if(category.name.equalsIgnoreCase(NotificationTemplate.FINANCE_NOTIFICATION)) {
                    holder.iconView.setImageResource(R.drawable.ic_message_recommendedselection);
                }else if(category.name.equalsIgnoreCase(NotificationTemplate.PLATFORM_NOTIFICATION)) {
                    holder.iconView.setImageResource(R.drawable.ic_xjs_msg);
                }else {
                    holder.iconView.setImageResource(R.drawable.default_avatar_grey);
                }

                holder.titleView.setText(category.remarks);

                holder.iconView.setType(MessageImageView.TYPE_MARK);

                ArrayList<Notification> notifications = category.notifications;
                if (category.count <= 0) {
                    holder.iconView.setCount(0);
                    if (notifications!=null && notifications.size()>0) {
                        Notification notify = notifications.get(0);
                        holder.timeView.setText(TimeUtil.format(notify.createdOn,TimeUtil.TIME_YYYY_MM_DD_HH_MM));
                        holder.contentView.setText(notify.body);
                    }else{
                        holder.timeView.setText("");
                        holder.contentView.setText("");
                    }
                }else {
                    Notification notify = notifications.get(0);
                    holder.timeView.setText(TimeUtil.format(notify.createdOn,TimeUtil.TIME_YYYY_MM_DD_HH_MM));
                    holder.contentView.setText(notify.body);
                    holder.iconView.setCount(category.count);
                }

            }else {

                LCIMConversationUtils.getConversationName(conversation, new AVCallback<String>() {
                    @Override
                    protected void internalDone0(String s, AVException e) {

                    }
                });


//                holder.iconView.setType(MessageImageView.TYPE_NUM);
                //holder.iconView.setCount(category.conversation.getUnReadMsgCnt());

//                Message lastMsg = category.conversation.getLatestMessage();
//                if (lastMsg != null) {
//                    //
//                    holder.timeView.setText(TimeUtil.format(new Date(lastMsg.getCreateTime()), TimeUtil.TIME_YYYY_MM_DD_HH_MM));
//                }else {
//                    holder.iconView.setImageResource(R.drawable.default_avatar_grey);
//                    holder.timeView.setText("");
//                    holder.titleView.setText("");
//                    holder.contentView.setText("");
//                }
            }

            return convertView;
        }
    }

    public static class ViewHolder {
        MessageImageView iconView;
        TextView titleView;
        TextView timeView;
        TextView contentView;

    }

}
