package cn.xiaojs.xma.ui.message;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.common.xf_foundation.platform.NotificationTemplate;
import cn.xiaojs.xma.data.NotificationDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.GNOResponse;
import cn.xiaojs.xma.model.Notification;
import cn.xiaojs.xma.model.NotificationCategory;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.ui.base.BaseFragment;

import cn.xiaojs.xma.ui.widget.MessageImageView;
import cn.xiaojs.xma.util.TimeUtil;


public class MessageFragment extends BaseFragment {

    @BindView(R.id.home_message_list)
    ListView listView;

    private MessageAdapter messageAdapter;

    @Override
    public void onResume() {
        super.onResume();

        getMessageOverview();
    }

    @OnClick({R.id.people_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.people_image:
                startActivity(new Intent(mContext, ContactActivity.class));
                break;
        }

    }



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

                if (messageAdapter == null) {
                    return;
                }

                NotificationCategory category = messageAdapter.getItem(position);
                enterCategoryList(category);

                //消除红点
                if (category.count > 0) {
                    category.count = 0;
                    messageAdapter.notifyDataSetChanged();
                }

            }
        });

        getMessageOverview();
    }

    public void getMessageOverview() {
        Pagination pagination = new Pagination();
        pagination.setPage(1);
        pagination.setMaxNumOfObjectsPerPage(100);

        NotificationDataManager.requestNotificationsOverview(mContext, pagination,
                new APIServiceCallback<GNOResponse>() {
                    @Override
                    public void onSuccess(GNOResponse object) {
                        if (object != null && object.categories != null && messageAdapter !=null) {
                            List<NotificationCategory> categoryList = object.categories;
                            messageAdapter.setData(categoryList);
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {

                    }
                });
    }

    private void analyticClick(NotificationCategory category){
        if (category.name.equalsIgnoreCase(NotificationTemplate.INVITATION_NOTIFICATION)) {
            AnalyticEvents.onEvent(getActivity(),27);
        } else if (category.name.equalsIgnoreCase(NotificationTemplate.FOLLOW_NOTIFICATION)) {
            AnalyticEvents.onEvent(getActivity(),25);
        }else if (category.name.equalsIgnoreCase(NotificationTemplate.CTL_NOTIFICATION)) {
            AnalyticEvents.onEvent(getActivity(),26);
        }else if (category.name.equalsIgnoreCase(NotificationTemplate.PLATFORM_NOTIFICATION)) {
            AnalyticEvents.onEvent(getActivity(),24);
        }
    }


    private void enterCategoryList(NotificationCategory category) {
        analyticClick(category);
        Intent intent = new Intent(mContext, NotificationCategoryListActivity.class);
        intent.putExtra(NotificationConstant.KEY_NOTIFICATION_CATEGORY_ID, category.id);
        intent.putExtra(NotificationConstant.KEY_NOTIFICATION_TITLE, category.remarks);
        startActivity(intent);
        //((BaseActivity) mContext).startActivityForResult(intent, NotificationConstant.REQUEST_NOTIFICATION_CATEGORY_LIST);
    }

    private class MessageAdapter extends BaseAdapter {

        private List<NotificationCategory> conversations;


        public List<NotificationCategory> getConversations() {
            return conversations;
        }

        public void setData(List<NotificationCategory> datas) {
            conversations = datas;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {

            if (conversations == null) {
                return 0;
            }
            return conversations.size();
        }

        @Override
        public NotificationCategory getItem(int position) {

            if (conversations == null) {
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

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            NotificationCategory conversation = conversations.get(position);


            final NotificationCategory category = (NotificationCategory) conversation;

            if (category.name.equalsIgnoreCase(NotificationTemplate.INVITATION_NOTIFICATION)) {
                holder.iconView.setImageResource(R.drawable.ic_message_invite);
            } else if (category.name.equalsIgnoreCase(NotificationTemplate.FOLLOW_NOTIFICATION)) {
                holder.iconView.setImageResource(R.drawable.ic_message_socialnews);
            } else if (category.name.equalsIgnoreCase(NotificationTemplate.ANSWERS_NOTIFICATION)) {
                holder.iconView.setImageResource(R.drawable.ic_message_qanswerme);
            } else if (category.name.equalsIgnoreCase(NotificationTemplate.ARTICLE_NOTIFICATION)) {
                holder.iconView.setImageResource(R.drawable.ic_message_transactionmessage);
            } else if (category.name.equalsIgnoreCase(NotificationTemplate.CTL_NOTIFICATION)) {
                holder.iconView.setImageResource(R.drawable.ic_message_course_information);
            } else if (category.name.equalsIgnoreCase(NotificationTemplate.FINANCE_NOTIFICATION)) {
                holder.iconView.setImageResource(R.drawable.ic_message_recommendedselection);
            } else if (category.name.equalsIgnoreCase(NotificationTemplate.PLATFORM_NOTIFICATION)) {
                holder.iconView.setImageResource(R.drawable.ic_xjs_msg);
            } else {
                holder.iconView.setImageResource(R.drawable.default_avatar_grey);
            }

            holder.titleView.setText(category.remarks);

            holder.iconView.setType(MessageImageView.TYPE_MARK);

            ArrayList<Notification> notifications = category.notifications;
            if (category.count <= 0) {
                holder.iconView.setCount(0);
                if (notifications != null && notifications.size() > 0) {
                    Notification notify = notifications.get(0);
                    holder.timeView.setText(TimeUtil.getTimeByNow(notify.createdOn));
                    holder.contentView.setText(notify.body);

                    holder.timeView.setVisibility(View.VISIBLE);
                    holder.contentView.setVisibility(View.VISIBLE);
                } else {
                    holder.timeView.setText("");
                    holder.contentView.setText("");

                    holder.timeView.setVisibility(View.GONE);
                    holder.contentView.setVisibility(View.GONE);
                }
            } else {
                if (notifications != null && notifications.size() > 0) {
                    Notification notify = notifications.get(0);
                    holder.timeView.setText(TimeUtil.getTimeByNow(notify.createdOn));
                    holder.contentView.setText(notify.body);
                    holder.iconView.setCount(category.count);

                    holder.timeView.setVisibility(View.VISIBLE);
                    holder.contentView.setVisibility(View.VISIBLE);
                }else {
                    holder.timeView.setText("");
                    holder.contentView.setText("");
                    holder.iconView.setCount(0);

                    holder.timeView.setVisibility(View.GONE);
                    holder.contentView.setVisibility(View.GONE);
                }

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


//    private class UpdateReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            String action = intent.getAction();
//
//            if (action.equals(LCIMConversationItemHolder.ACTION_UPDATE) && messageAdapter != null) {
//
//                if (XiaojsConfig.DEBUG) {
//                    Logger.d("conversation item update... ");
//                }
//
//                messageAdapter.notifyDataSetChanged();
//            }
//        }
//    }

}
