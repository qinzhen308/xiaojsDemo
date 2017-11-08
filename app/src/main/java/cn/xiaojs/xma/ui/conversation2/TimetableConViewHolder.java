package cn.xiaojs.xma.ui.conversation2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom2.widget.ColorIconTextView;

/**
 * Created by maxiaobao on 2017/10/30.
 */

public class TimetableConViewHolder extends AbsConversationViewHolder {

    @BindView(R.id.root_lay)
    public RelativeLayout rootLayout;
    @BindView(R.id.title)
    public TextView titleView;
    @BindView(R.id.desc)
    public TextView descView;

    public TimetableConViewHolder(View itemView) {
        super(itemView);
    }

    public static View createView(Context context, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(
                R.layout.layout_conversation2_my_schuder_conversation_item, parent, false);
    }
}
