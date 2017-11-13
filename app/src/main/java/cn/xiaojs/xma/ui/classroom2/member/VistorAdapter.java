package cn.xiaojs.xma.ui.classroom2.member;

import android.content.Context;
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

public class VistorAdapter extends RecyclerView.Adapter<MemberViewHolder> {
    private Context context;
    private ArrayList<Attendee> attendees;
    private int avatorSize;
    private VistorListFragment fragment;
    private ClassroomEngine classroomEngine;


    public VistorAdapter(VistorListFragment fragment, Context context, ArrayList<Attendee> attendees) {
        this.context = context;
        this.fragment = fragment;
        this.attendees = attendees;
        avatorSize = context.getResources().getDimensionPixelSize(R.dimen.px90);
        classroomEngine = ClassroomEngine.getEngine();
    }

    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_classroom2_member_item, parent, false);

        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {

        final Attendee attendee = attendees.get(position);

            MemberViewHolder memberHolder = holder;

            String avatorUrl = Account.getAvatar(attendee.accountId, avatorSize);
            Glide.with(context)
                    .load(avatorUrl)
                    .transform(new CircleTransform(context))
                    .placeholder(R.drawable.ic_defaultavatar)
                    .error(R.drawable.ic_defaultavatar)
                    .into(memberHolder.avatorView);

            memberHolder.nameView.setText(attendee.name);

            boolean online = attendee.xa == 0 ? false : true;

            if (online) {
                memberHolder.nameView.setTextColor(context.getResources().getColor(R.color.font_orange));
            } else {
                memberHolder.nameView.setTextColor(context.getResources().getColor(R.color.font_black));
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

            memberHolder.nameView.setCompoundDrawablesWithIntrinsicBounds(0,0,identityRes,0);



            if (!TextUtils.isEmpty(attendee.title)) {
                memberHolder.descView.setText(attendee.title);
            }else {
                memberHolder.descView.setText(R.string.contact_empty_tips);
            }

            memberHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
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


}
