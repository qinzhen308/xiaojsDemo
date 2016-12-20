package com.benyuan.xiaojs.ui.classroom;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2016/12/20
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.widget.RoundedImageView;

public class ChatMsgAdapter extends BaseAdapter{
    public final static int TYPE_MY_SPEAKER = 0;
    public final static int TYPE_OTHER_SPEAKER = 1;

    private Context mContext;

    public ChatMsgAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? TYPE_MY_SPEAKER : TYPE_OTHER_SPEAKER;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = createContentView(position);
        }
        holder = (Holder)convertView.getTag();

        bindData(holder);

        return convertView;
    }

    private View createContentView(int position) {
        int type = getItemViewType(position);
        View v = null;
        switch (type) {
            case TYPE_MY_SPEAKER:
                v = LayoutInflater.from(mContext).inflate(R.layout.layout_chat_my_speaker_item, null);
                break;
            case TYPE_OTHER_SPEAKER:
                v = LayoutInflater.from(mContext).inflate(R.layout.layout_chat_other_speaker_item, null);
                break;
        }

        Holder holder = new Holder();
        holder.portrait = (RoundedImageView) v.findViewById(R.id.portrait);
        holder.name = (TextView) v.findViewById(R.id.name);
        holder.time = (TextView) v.findViewById(R.id.time);
        holder.msg = (TextView) v.findViewById(R.id.msg);
        v.setTag(holder);
        return v;
    }


    private void bindData(Holder holder) {
        holder.portrait.setImageResource(R.drawable.default_portrait);
    }

    private class Holder {
        RoundedImageView portrait;
        TextView name;
        TextView time;
        TextView msg;
    }
}
