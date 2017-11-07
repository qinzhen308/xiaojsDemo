package cn.xiaojs.xma.ui.classroom2.material;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.xiaojs.xma.model.social.Contact;

/**
 * Created by maxiaobao on 2017/10/11.
 */

public class ClassAdapter extends RecyclerView.Adapter<ClassViewHolder> {

    private Context context;

    private ArrayList<Contact> classes;
    private int checkIndex = -1;

    public ClassAdapter(Context context,ArrayList<Contact> classes) {
        this.context = context;
        this.classes = classes;
    }

    @Override
    public ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ClassViewHolder.createView(context, parent);
    }

    @Override
    public void onBindViewHolder(ClassViewHolder holder, final int position) {

        Contact contact = classes.get(position);
        holder.nameView.setText(contact.title);
        holder.descView.setText(contact.owner);


        String tt = TextUtils.isEmpty(contact.title)?
                "#" : String.valueOf(contact.title.trim().charAt(0));

        holder.avatorView.setIconWithText(tt);

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
        return classes == null? 0 : classes.size();
    }

    public Contact getCheckItem() {
        return (checkIndex<0 || classes==null)? null : classes.get(checkIndex);
    }
}
