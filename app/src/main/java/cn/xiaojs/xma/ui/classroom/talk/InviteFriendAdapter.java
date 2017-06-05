package cn.xiaojs.xma.ui.classroom.talk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
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
    private int mSize = 90;


    public InviteFriendAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);

        mContext = context;
        mChoiceList = new ArrayList<String>();
        mSize = mContext.getResources().getDimensionPixelSize(R.dimen.px90);
    }

    public void resetSelection() {
        mChoiceList.clear();
        notifyDataSetChanged();
    }

    public void setSelectionListener(SelectionListener listener) {
        mSelectionListener = listener;
    }

    @Override
    protected void setViewContent(ViewHolder holder, Attendee bean, int position) {
        holder.checkbox.setSelected(mChoiceList.contains(String.valueOf(position)));
        holder.position = position;
        String portraitUrl = cn.xiaojs.xma.common.xf_foundation.schemas.Account.getAvatar(bean.accountId, mSize);
        Glide.with(mContext).load(portraitUrl).error(R.drawable.default_avatar).into(holder.portrait);
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
            attendees.add(attendee);
        }

        return attendees;
    }

    public interface SelectionListener {
        public void onSelectChanged(int selectionCount);
    }

    public void checkAll() {
        if (mChoiceList != null) {
            int count = getCount();
            for (int i = 0; i < count; i++) {
                mChoiceList.add(String.valueOf(i));
            }
            notifyDataSetChanged();
        }
    }

    public void unCheckAll() {
        if (mChoiceList != null) {
            mChoiceList.clear();
            notifyDataSetChanged();
        }
    }
}
