package cn.xiaojs.xma.ui.classroom2.member;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.ui.base2.OnItemClickObservable;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.widget.ColorIconTextView;
import cn.xiaojs.xma.ui.widget.CircleTransform;

/**
 * Created by maxiaobao on 2017/10/11.
 */

public class ShareToAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Attendee> attendees;
    private int avatorSize;
    private ClassroomEngine classroomEngine;

    public ShareToAdapter(Context context, ArrayList<Attendee> attendees) {
        this.context = context;
        this.attendees = attendees;
        avatorSize = context.getResources().getDimensionPixelSize(R.dimen.px90);
        classroomEngine = ClassroomEngine.getEngine();
    }

    @Override
    public int getCount() {
        return attendees == null ? 0 : attendees.size();
    }

    @Override
    public Attendee getItem(int position) {
        return attendees == null ? null : attendees.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShareToViewholder viewholder = null;


        if (convertView != null) {
            viewholder = (ShareToViewholder) convertView.getTag();

        } else {
            convertView = ShareToViewholder.createView(context, parent);
            viewholder = new ShareToViewholder(convertView);
            convertView.setTag(viewholder);
        }

        Attendee attendee = getItem(position);
        String titleStr = attendee.name;

        if (position == 0) {
            String title = TextUtils.isEmpty(titleStr) ?
                    "#" : String.valueOf(titleStr.trim().charAt(0));
            viewholder.avatorTextView.setText(title);
            viewholder.avatorTextView.setVisibility(View.VISIBLE);
            viewholder.avatorIcView.setVisibility(View.INVISIBLE);

            viewholder.descView.setText("");

        }else {

            viewholder.avatorTextView.setVisibility(View.INVISIBLE);
            viewholder.avatorIcView.setVisibility(View.VISIBLE);
            viewholder.descView.setText(attendee.title);

            String avatorUrl = Account.getAvatar(attendee.accountId, avatorSize);
            Glide.with(context)
                    .load(avatorUrl)
                    .transform(new CircleTransform(context))
                    .placeholder(R.drawable.ic_defaultavatar)
                    .error(R.drawable.ic_defaultavatar)
                    .into(viewholder.avatorIcView);

        }

        viewholder.titleView.setText(titleStr);
        return convertView;
    }


    static class ShareToViewholder {
        @BindView(R.id.check_view)
        public CheckedTextView checkedTextView;
        @BindView(R.id.root_lay)
        public RelativeLayout rootLayout;
        @BindView(R.id.avator_ic)
        public ImageView avatorIcView;
        @BindView(R.id.avator)
        public ColorIconTextView avatorTextView;
        @BindView(R.id.title)
        public TextView titleView;
        @BindView(R.id.desc)
        public TextView descView;

        public ShareToViewholder(View view) {
            ButterKnife.bind(this, view);
        }

        public static View createView(Context context, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);
            return inflater.inflate(R.layout.layout_classroom2_share_to_item, parent, false);

        }

    }


}
