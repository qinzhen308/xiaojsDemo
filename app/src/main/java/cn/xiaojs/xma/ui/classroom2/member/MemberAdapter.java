package cn.xiaojs.xma.ui.classroom2.member;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
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
    private ClassroomEngine classroomEngine;


    public MemberAdapter(MemberListFragment fragment, Context context, ArrayList<Attendee> attendees) {
        this.context = context;
        this.fragment = fragment;
        this.attendees = attendees;
        avatorSize = context.getResources().getDimensionPixelSize(R.dimen.px90);
        classroomEngine = ClassroomEngine.getEngine();
    }

    public void updateData(ArrayList<Attendee> atts) {

        if (atts == null)
            return;

        attendees.clear();
        attendees.addAll(atts);
        Collections.sort(attendees);
        notifyDataSetChanged();

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
                .placeholder(R.drawable.ic_defaultavatar)
                .error(R.drawable.ic_defaultavatar)
                .into(holder.avatorView);

        holder.nameView.setText(attendee.name);

        boolean online = attendee.xa == 0 ? false : true;

        if (online) {
            holder.nameView.setTextColor(context.getResources().getColor(R.color.font_orange));
        } else {
            holder.nameView.setTextColor(context.getResources().getColor(R.color.font_black));
        }


        String realType = TextUtils.isEmpty(attendee.psTypeInLesson)?
                attendee.psType : attendee.psTypeInLesson;

        int identityRes;

        CTLConstant.UserIdentity identity = classroomEngine.getUserIdentity(realType);
        if (identity == CTLConstant.UserIdentity.ADMINISTRATOR) {
            identityRes = R.drawable.mem_administrators;
        }else if(identity == CTLConstant.UserIdentity.ADVISER) {
            identityRes = R.drawable.mem_headmaster;
        }else if(identity == CTLConstant.UserIdentity.LEAD) {
            identityRes = R.drawable.mem_speaker;
        }else if(identity == CTLConstant.UserIdentity.TEACHER2) {
            identityRes = R.drawable.mem_teacher;
        }else if(identity == CTLConstant.UserIdentity.ASSISTANT) {
            identityRes = R.drawable.mem_assistant;
        } else {
            identityRes = 0;
        }

        holder.nameView.setCompoundDrawablesWithIntrinsicBounds(0,0,identityRes,0);



        if (!TextUtils.isEmpty(attendee.title)) {
            holder.descView.setText(attendee.title);
        }else {
            holder.descView.setText(R.string.contact_empty_tips);
        }

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

}
