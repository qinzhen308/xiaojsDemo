package cn.xiaojs.xma.ui.classroom2.member;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.widget.CircleTransform;

/**
 * Created by maxiaobao on 2017/9/27.
 */

public class ContactMulAdapter extends RecyclerView.Adapter<ContactViewHolder>{

    private Context context;
    private ArrayList<Contact> contacts;
    private int avatorSize;
    private Set<Integer> checkedIndex;


    public ContactMulAdapter(Context context, ArrayList<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
        avatorSize = context.getResources().getDimensionPixelSize(R.dimen.px90);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return ContactViewHolder.createViewHolder(context, parent);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, final int position) {

        Contact contact = contacts.get(position);
        String avatorUrl = Account.getAvatar(contact.account, avatorSize);
        Glide.with(context)
                .load(avatorUrl)
                .transform(new CircleTransform(context))
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(holder.avatorView);

        holder.nameView.setText(contact.alias);
        holder.descView.setText("stay hungry, stay foolish");

        holder.checkBoxView.setChecked(check(position));
        holder.checkBoxView.setEnabled(false);

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSelection(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts == null ? 0 : contacts.size();
    }

    private boolean check(int position) {

        return checkedIndex == null ? false : checkedIndex.contains(position);
    }
    public Set<Integer> getCheckedIndexs(){
        return checkedIndex;
    }

    private void toSelection(int position) {

        if (checkedIndex == null) {
            checkedIndex = new HashSet<>();
        }

        if (checkedIndex.contains(position)) {
            checkedIndex.remove(position);
        }else {
            checkedIndex.add(position);
        }

        notifyItemChanged(position);
       // notifyDataSetChanged();
    }
}
