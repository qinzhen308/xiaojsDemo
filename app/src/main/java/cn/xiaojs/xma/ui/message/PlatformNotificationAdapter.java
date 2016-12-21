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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.model.Notification;
import cn.xiaojs.xma.model.NotificationCategory;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;
import cn.xiaojs.xma.ui.widget.MessageImageView;
import cn.xiaojs.xma.util.TimeUtil;

import java.util.List;

import butterknife.BindView;

public class PlatformNotificationAdapter extends CanInScrollviewListView.Adapter {

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
    private List<NotificationCategory> beans ;

    public PlatformNotificationAdapter(Context context,String[] titles){
        mContext = context;
        this.titles = titles;
    }
    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public NotificationCategory getItem(int position) {
        return NotificationBusiness.getPlatformMessageCategory(beans,position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_message_list_item,null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        holder.image.setImageResource(mIcons[position]);
        holder.title.setText(titles[position]);
        if (position == mIcons.length - 1){
            holder.special();
            holder.image.setType(MessageImageView.TYPE_NUM);
        }else {
            holder.image.setType(MessageImageView.TYPE_MARK);
            holder.normal();
        }

        if (beans != null && beans.size() > 0){
            NotificationCategory category = NotificationBusiness.getPlatformMessageCategory(beans,position);
            if (category != null){
                holder.image.setCount(category.count);
                if (category.notifications != null && category.notifications.size() > 0){
                    Notification notification = category.notifications.get(0);
                    holder.time.setText(TimeUtil.format(notification.createdOn,TimeUtil.TIME_YYYY_MM_DD_HH_MM));
                    holder.content.setText(notification.body);
                }
            }
        }
        return convertView;
    }

    public void setData(List<NotificationCategory> beans){
        if (beans == null || beans.size() == 0)
            return;
        this.beans = beans;
        notifyDataSetChanged();
    }

    public List<NotificationCategory> getData(){
        return beans;
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

        public void normal(){
            head.setVisibility(View.GONE);
            topDivider.setVisibility(View.GONE);
            bottomDivider.setVisibility(View.GONE);
        }

        public void special(){
            head.setVisibility(View.VISIBLE);
            topDivider.setVisibility(View.VISIBLE);
            bottomDivider.setVisibility(View.VISIBLE);
        }

        public Holder(View view) {
            super(view);
        }
    }

}
