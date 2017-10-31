package cn.xiaojs.xma.ui.conversation2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom2.widget.ColorIconTextView;
import cn.xiaojs.xma.ui.widget.SwipeLayout;

/**
 * Created by maxiaobao on 2017/10/30.
 */

public class ClassConViewHolder extends AbsConversationViewHolder {

    @BindView(R.id.uproot)
    public RelativeLayout uprootLayout;
    @BindView(R.id.avator)
    public ColorIconTextView avatorTextView;
    @BindView(R.id.title)
    public TextView titleView;
    @BindView(R.id.desc)
    public TextView descView;
    @BindView(R.id.time)
    public TextView timeView;
    @BindView(R.id.unread_flag)
    public TextView flagView;

    @BindView(R.id.root)
    public SwipeLayout swipeLayout;
    @BindView(R.id.rdelete)
    public Button deleteView;
    @BindView(R.id.rtop)
    public Button topView;


    public ClassConViewHolder(View itemView) {
        super(itemView);
    }

    public static View createView(Context context, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.layout_conversation2_class_conversation_item,parent,false);
    }
}
