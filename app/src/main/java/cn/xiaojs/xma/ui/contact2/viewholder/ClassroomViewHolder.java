package cn.xiaojs.xma.ui.contact2.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom2.widget.ColorIconTextView;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class ClassroomViewHolder {

    @BindView(R.id.root_lay)
    public RelativeLayout rootLayout;
    @BindView(R.id.avator)
    public ColorIconTextView avatorTextView;
    @BindView(R.id.title)
    public TextView titleView;
    @BindView(R.id.desc)
    public TextView descView;


    public ClassroomViewHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public static View createView(Context context, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.layout_contact2_classrooms_item, parent, false);

    }
}
