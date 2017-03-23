package cn.xiaojs.xma.ui.message;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.platform.NotificationTemplate;
import cn.xiaojs.xma.data.NotificationDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.GNOResponse;
import cn.xiaojs.xma.model.Notification;
import cn.xiaojs.xma.model.NotificationCategory;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;
import cn.xiaojs.xma.ui.widget.MessageImageView;
import cn.xiaojs.xma.util.TimeUtil;


public class MessageFragment extends BaseFragment {

    ListView listView;

    private MessageAdapter messageAdapter;

    @Override
    protected View getContentView() {
        listView = (ListView) mContext.getLayoutInflater().inflate(R.layout.fragment_message_list, null);
        return listView;
    }

    @Override
    protected void init() {
        messageAdapter = new MessageAdapter();
        listView.setAdapter(messageAdapter);

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
                    messageAdapter.setData(object.categories);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                showFailView();
            }
        });
    }


    private class MessageAdapter extends BaseAdapter {

        private List<NotificationCategory> categories;

        public void setData(List<NotificationCategory> datas) {
            categories = datas;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {

            if (categories== null) {
                return 0;
            }
            return categories.size();
        }

        @Override
        public Object getItem(int position) {

            if (categories == null){
                return null;
            }
            return categories.get(position);
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
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            NotificationCategory category = categories.get(position);


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

            ArrayList<Notification> notifications = category.notifications;
            if (notifications == null || notifications.size() <= 0) {
                holder.timeView.setText("");
                holder.contentView.setText("");
            }else {
                Notification notify = notifications.get(0);
                holder.timeView.setText(TimeUtil.format(notify.createdOn,TimeUtil.TIME_YYYY_MM_DD_HH_MM));
                holder.contentView.setText(notify.body);
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
