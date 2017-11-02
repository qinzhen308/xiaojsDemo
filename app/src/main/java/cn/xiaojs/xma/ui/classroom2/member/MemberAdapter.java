package cn.xiaojs.xma.ui.classroom2.member;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.ui.classroom2.chat.ChatSessionFragment;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.MessageImageView;

/**
 * Created by maxiaobao on 2017/9/27.
 */

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private Context context;
    private ArrayList<Attendee> attendees;
    private int avatorSize;
    private MemberListFragment fragment;


    public MemberAdapter(MemberListFragment fragment, Context context, ArrayList<Attendee> attendees) {
        this.context = context;
        this.fragment = fragment;
        this.attendees = attendees;
        avatorSize = context.getResources().getDimensionPixelSize(R.dimen.px90);
    }

    @Override
    public MemberAdapter.MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_classroom2_member_item, parent, false);

        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberAdapter.MemberViewHolder holder, int position) {

        final Attendee attendee = attendees.get(position);
        String avatorUrl = Account.getAvatar(attendee.accountId, avatorSize);
        Glide.with(context)
                .load(avatorUrl)
                .transform(new CircleTransform(context))
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(holder.avatorView);

        holder.nameView.setText(attendee.name);

        boolean online = attendee.xa == 0 ? false : true;

        if (online) {
            holder.nameView.setTextColor(context.getResources().getColor(R.color.font_orange));
        } else {
            holder.nameView.setTextColor(context.getResources().getColor(R.color.font_black));
        }

        holder.descView.setText("stay hungry, stay foolish");
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.enterChatSession(attendee);
            }
        });

    }

    @Override
    public int getItemCount() {
        return attendees == null ? 0 : attendees.size();
    }



    static class MemberViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.root_lay)
        RelativeLayout rootLayout;
        @BindView(R.id.avator)
        MessageImageView avatorView;
        @BindView(R.id.name)
        TextView nameView;
        @BindView(R.id.desc)
        TextView descView;

        public MemberViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    //    private Context context;
//    private ArrayList<Attendee> attendees;
//    private int avatorSize;
//
//    public MemberAdapter(Context context, ArrayList<Attendee> attendees) {
//        this.context = context;
//        this.attendees = attendees;
//        avatorSize = context.getResources().getDimensionPixelSize(R.dimen.px90);
//    }
//
//
//    public void setAttendees(ArrayList<Attendee> attendees) {
//        this.attendees = attendees;
//        notifyDataSetChanged();
//    }
//
//
//    @Override
//    public int getCount() {
//        return attendees == null? 0 : attendees.size();
//    }
//
//    @Override
//    public Attendee getItem(int position) {
//        return attendees == null? null : attendees.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        ViewHolder holder;
//        if (convertView != null) {
//            holder = (ViewHolder) convertView.getTag();
//        } else {
//            convertView = LayoutInflater.from(context)
//                    .inflate(R.layout.layout_classroom2_member_item, parent, false);
//            holder = new ViewHolder(convertView);
//            convertView.setTag(holder);
//        }
//
//        Attendee attendee = getItem(position);
//        String avatorUrl = Account.getAvatar(attendee.accountId, avatorSize);
//        Glide.with(context)
//                .load(avatorUrl)
//                .transform(new CircleTransform(context))
//                .placeholder(R.drawable.default_avatar_grey)
//                .error(R.drawable.default_avatar_grey)
//                .into(holder.avatorView);
//
//        holder.nameView.setText(attendee.name);
//
//        boolean online = attendee.xa == 0 ? false : true;
//
//        if (online) {
//            holder.nameView.setTextColor(context.getResources().getColor(R.color.font_orange));
//        }else {
//            holder.nameView.setTextColor(context.getResources().getColor(R.color.font_black));
//        }
//
//        holder.descView.setText("stay hungry, stay foolish");
//
//        return convertView;
//    }
//
//    static class ViewHolder {
//        @BindView(R.id.avator)
//        MessageImageView avatorView;
//        @BindView(R.id.name)
//        TextView nameView;
//        @BindView(R.id.desc)
//        TextView descView;
//
//        public ViewHolder(View view) {
//            ButterKnife.bind(this, view);
//        }
//    }

}
