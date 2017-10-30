package cn.xiaojs.xma.ui.conversation2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.xiaojs.xma.model.social.Contact;

/**
 * Created by maxiaobao on 2017/10/30.
 */

public class ConversationAdapter extends RecyclerView.Adapter<AbsConversationViewHolder>{

    private Context context;
    private ArrayList<Contact> contacts;

    public ConversationAdapter(Context context, ArrayList<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    @Override
    public AbsConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = ClassConViewHolder.createView(context, parent);
        return new ClassConViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AbsConversationViewHolder holder, int position) {

        Contact contact = contacts.get(position);

        ClassConViewHolder conViewHolder = (ClassConViewHolder) holder;

        conViewHolder.avatorTextView.setText(String.valueOf(contact.title.trim().charAt(0)));
        conViewHolder.titleView.setText(contact.title);
        conViewHolder.descView.setText(contact.lastMessage);
    }

    @Override
    public int getItemCount() {
        return contacts== null? 0 : contacts.size();
    }
}
