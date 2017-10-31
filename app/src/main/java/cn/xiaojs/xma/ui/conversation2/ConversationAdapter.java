package cn.xiaojs.xma.ui.conversation2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.classroom2.util.TimeUtil;

/**
 * Created by maxiaobao on 2017/10/30.
 */

public class ConversationAdapter extends RecyclerView.Adapter<AbsConversationViewHolder>{

    private Context context;
    private ArrayList<Contact> contacts;

    public ConversationAdapter(Context context) {
        this.context = context;
        this.contacts = new ArrayList<>();
        contacts.add(createTimetable());
    }

    public void addContact(ArrayList<Contact> datas) {
        if (contacts != null) {
            contacts.addAll(datas);
            notifyDataSetChanged();
        }
    }

    @Override
    public AbsConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ConversationType.TIME_TABLE) {

            View view = TimetableConViewHolder.createView(context, parent);
            return new TimetableConViewHolder(view);

        }else {
            View view = ClassConViewHolder.createView(context, parent);
            return new ClassConViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        String subType = contacts.get(position).subtype;
        return ConversationType.getConversationType(subType);
    }

    @Override
    public void onBindViewHolder(AbsConversationViewHolder holder, int position) {

        Contact contact = contacts.get(position);

        if (holder instanceof  TimetableConViewHolder) {
            TimetableConViewHolder conViewHolder = (TimetableConViewHolder) holder;
            conViewHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 进入我的课表
                }
            });

        }else {
            ClassConViewHolder conViewHolder = (ClassConViewHolder) holder;

            conViewHolder.avatorTextView.setText(String.valueOf(contact.title.trim().charAt(0)));
            conViewHolder.titleView.setText(contact.title);
            conViewHolder.descView.setText(contact.lastMessage);
            conViewHolder.timeView.setText(TimeUtil.getTimeShowString(contact.lastTalked, false));
        }

    }

    @Override
    public int getItemCount() {
        return contacts== null? 0 : contacts.size();
    }

    private Contact createTimetable() {
        Contact timeTable = new Contact();
        timeTable.subtype = ConversationType.TypeName.TIME_TABLE;
        return timeTable;
    }
}
