package cn.xiaojs.xma.ui.classroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.MessageImageView;
import cn.xiaojs.xma.ui.widget.RoundedImageView;

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
 * Date:2016/11/30
 * Desc:
 *
 * ======================================================================================== */

public class ChatSimpleContactAdapter extends BaseAdapter {
    private Context mContext;
    private int mOffset;

    public ChatSimpleContactAdapter(Context context) {
        mContext = context;
        mOffset = context.getResources().getDimensionPixelOffset(R.dimen.px4);
    }

    @Override
    public int getCount() {
        //TODO test count
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
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = createContentView();
        }
        holder = (Holder)convertView.getTag();

        bindData(holder);
        return convertView;
    }

    private View createContentView() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_classroom_chat_contact_item, null);
        Holder holder = new Holder();
        holder.portrait = (MessageImageView) v.findViewById(R.id.portrait);
        //set type
        holder.portrait.setType(MessageImageView.TYPE_NUM);
        holder.portrait.setExtraOffsetX(mOffset);
        holder.portrait.setExtraOffsetY(mOffset);

        v.setTag(holder);
        return v;
    }

    private void bindData(Holder holder) {
        holder.portrait.setImageResource(R.drawable.default_portrait);
        holder.portrait.setCount(5);
    }


    private class Holder {
        MessageImageView portrait;
    }
}
