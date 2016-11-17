package com.benyuan.xiaojs.ui.message;
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

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.BaseHolder;
import com.benyuan.xiaojs.ui.widget.CanInScrollviewListView;
import com.benyuan.xiaojs.ui.widget.MessageImageView;
import com.benyuan.xiaojs.util.TimeUtil;

import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class PlatformMessageAdapter extends CanInScrollviewListView.Adapter {

    private int[] mIcons = new int[]{
            R.drawable.ic_message_invitation,
    R.drawable.ic_message_course_information,
    R.drawable.ic_message_socialnews,
            R.drawable.ic_message_qanswerme,
            R.drawable.ic_message_transactionmessage,
    R.drawable.ic_message_recommendedselection};

    private String titles[];
    private Context mContext;
    private List<MessageBean> beans ;

    public PlatformMessageAdapter(Context context,List<MessageBean> beans){
        mContext = context;
        this.beans = beans;
        titles = mContext.getResources().getStringArray(R.array.platform_message_title);
    }
    @Override
    public int getCount() {
        return beans == null ? 0 : beans.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
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
        holder.time.setText(TimeUtil.format(new Date(System.currentTimeMillis()),TimeUtil.TIME_YYYY_MM_DD_HH_MM));
        holder.content.setText("这是一条"+ titles[position] +"的内容");
        holder.image.setCount(36);
        return convertView;
    }

    class Holder extends BaseHolder{
        @BindView(R.id.message_image)
        MessageImageView image;
        @BindView(R.id.message_title)
        TextView title;
        @BindView(R.id.message_time)
        TextView time;
        @BindView(R.id.message_content)
        TextView content;

        public Holder(View view) {
            super(view);
        }
    }

}
