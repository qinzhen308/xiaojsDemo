package cn.xiaojs.xma.ui.conversation2;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by maxiaobao on 2017/10/30.
 */

public abstract class AbsConversationViewHolder extends RecyclerView.ViewHolder {

    public AbsConversationViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
