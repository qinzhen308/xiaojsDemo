package cn.xiaojs.xma.ui.classroom2.member;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.classroom2.material.ClassViewHolder;
import cn.xiaojs.xma.ui.widget.CircleTransform;

/**
 * Created by maxiaobao on 2017/10/11.
 */

public class ChooseMemberAdapter extends RecyclerView.Adapter<ChooseMemberViewHolder> {

    private Context context;

    private ArrayList<Attendee> attendees;
    private int checkIndex = -1;
    private int avatorSize;

    public ChooseMemberAdapter(Context context, ArrayList<Attendee> attendees) {
        this.context = context;
        this.attendees = attendees;
        avatorSize = context.getResources().getDimensionPixelSize(R.dimen.px90);
    }

    @Override
    public ChooseMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ChooseMemberViewHolder.createView(context, parent);
    }

    @Override
    public void onBindViewHolder(ChooseMemberViewHolder holder, final int position) {

        Attendee attendee = attendees.get(position);
        holder.nameView.setText(attendee.name);
        holder.descView.setText("summary");

        String avatorUrl = Account.getAvatar(attendee.accountId, avatorSize);
        Glide.with(context)
                .load(avatorUrl)
                .transform(new CircleTransform(context))
                .placeholder(R.drawable.ic_defaultavatar)
                .error(R.drawable.ic_defaultavatar)
                .into(holder.avatorView);


        if (position == checkIndex) {
            holder.checkedView.setChecked(true);
        }else {
            holder.checkedView.setChecked(false);
        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIndex = position;
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return attendees == null? 0 : attendees.size();
    }

    public Attendee getCheckItem() {
        return (attendees==null || checkIndex<0)? null : attendees.get(checkIndex);
    }
}
