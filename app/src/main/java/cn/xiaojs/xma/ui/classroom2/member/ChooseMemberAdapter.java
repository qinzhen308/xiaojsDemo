package cn.xiaojs.xma.ui.classroom2.member;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.base2.OnItemClickObservable;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.material.ClassViewHolder;
import cn.xiaojs.xma.ui.widget.CircleTransform;

/**
 * Created by maxiaobao on 2017/10/11.
 */

public class ChooseMemberAdapter extends RecyclerView.Adapter<ChooseMemberViewHolder> {

    private Context context;

    private ArrayList<Attendee> attendees;
    //private int checkIndex = -1;
    private int avatorSize;
    private ClassroomEngine classroomEngine;
    private OnItemClickObservable<Attendee> itemClickObservable;
    private String currentO2oId;

    public ChooseMemberAdapter(Context context, String currentO2oId, ArrayList<Attendee> attendees,
                               OnItemClickObservable<Attendee> itemClickObservable) {
        this.context = context;
        this.attendees = attendees;
        this.currentO2oId = currentO2oId;
        this.itemClickObservable = itemClickObservable;
        avatorSize = context.getResources().getDimensionPixelSize(R.dimen.px90);
        classroomEngine = ClassroomEngine.getEngine();
    }

    @Override
    public ChooseMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ChooseMemberViewHolder.createView(context, parent);
    }

    @Override
    public void onBindViewHolder(ChooseMemberViewHolder holder, final int position) {
        final Attendee attendee = attendees.get(position);

        String avatorUrl = Account.getAvatar(attendee.accountId, avatorSize);
        Glide.with(context)
                .load(avatorUrl)
                .transform(new CircleTransform(context))
                .placeholder(R.drawable.ic_defaultavatar)
                .error(R.drawable.ic_defaultavatar)
                .into(holder.avatorView);


        holder.nameView.setText(attendee.name);

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

        if (!TextUtils.isEmpty(currentO2oId) && currentO2oId.equals(attendee.accountId)) {
            holder.o2oStatusView.setImageResource(R.drawable.ic_oneononeing);
        }else {
            holder.o2oStatusView.setImageResource(R.drawable.ic_oneonone);
        }


//        if (position == checkIndex) {
//            holder.checkedView.setChecked(true);
//        }else {
//            holder.checkedView.setChecked(false);
//        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemClickObservable != null) {
                    itemClickObservable.onItemClicked(position, attendee);
                }
                //checkIndex = position;
                //notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return attendees == null? 0 : attendees.size();
    }

//    public Attendee getCheckItem() {
//        return (attendees==null || checkIndex<0)? null : attendees.get(checkIndex);
//    }
}
