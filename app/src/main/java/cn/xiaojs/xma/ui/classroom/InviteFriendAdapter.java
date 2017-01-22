package cn.xiaojs.xma.ui.classroom;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.social.Contact;
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
 * Date:2016/12/27
 * Desc:
 *
 * ======================================================================================== */

public class InviteFriendAdapter extends AbsSwipeAdapter<Attendee, InviteFriendAdapter.ViewHolder> implements View.OnClickListener{
    private Context mContext;
    private List<String> mChoiceList;
    private SelectionListener mSelectionListener;


    public InviteFriendAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);

        mContext = context;
        mChoiceList = new ArrayList<String>();
    }

    public void setSelectionListener(SelectionListener listener) {
        mSelectionListener = listener;
    }

    @Override
    protected void setViewContent(ViewHolder holder, Attendee bean, int position) {
        holder.position = position;
        Glide.with(mContext).load(bean.avatar).error(R.drawable.default_avatar).into(holder.portrait);
        holder.name.setText(bean.name);
    }

    @Override
    protected View createContentView(int position) {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_classroom_invite_friend_item, null);
    }

    @Override
    protected ViewHolder initHolder(View v) {
        v.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(v);
        holder.checkbox = (ImageView) v.findViewById(R.id.checkbox);
        holder.portrait = (RoundedImageView) v.findViewById(R.id.portrait);
        holder.name = (TextView) v.findViewById(R.id.name);

        return holder;
    }

    @Override
    protected void doRequest() {
        onSuccess(getTestData());
    }

    @Override
    public void onClick(View v) {
        Object object = v.getTag();
        if (object instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder)object;
            String choice = String.valueOf(holder.position);
            if (mChoiceList.contains(choice)) {
                holder.checkbox.setSelected(false);
                mChoiceList.remove(choice);
            } else {
                holder.checkbox.setSelected(true);
                mChoiceList.add(choice);
            }

            if (mSelectionListener != null) {
                mSelectionListener.onSelectChanged(mChoiceList.size());
            }
        }
    }

    static class ViewHolder extends BaseHolder{
        ImageView checkbox;
        RoundedImageView portrait;
        TextView name;
        int position = -1;

        public ViewHolder(View view) {
            super(view);
        }
    }

    private List<Attendee> getTestData() {
        List<Attendee> attendees = new ArrayList<Attendee>();
        for (int i = 0; i < 10; i++) {
            Attendee attendee = new Attendee();
            attendee.name = "张无忌" + i;
            attendee.avatar = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1485083983103&di=c4c4d2b8cafe4b5b94e41" +
                    "171a5d138ff&imgtype=0&src=http%3A%2F%2Fwww.qq1234.org%2Fuploads%2Fallimg%2F140526%2F3_140526213847_1-lp.jpg";
            attendees.add(attendee);
        }

        return attendees;
    }

    public interface SelectionListener {
        public void onSelectChanged(int selectionCount);
    }
}
