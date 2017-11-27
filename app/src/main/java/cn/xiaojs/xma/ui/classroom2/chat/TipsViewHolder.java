package cn.xiaojs.xma.ui.classroom2.chat;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom2.util.TimeUtil;

/**
 * Created by maxiaobao on 2017/9/28.
 */

public class TipsViewHolder extends ChatViewHolder {


    @BindView(R.id.time_line)
    TextView timeLineView;
    @BindView(R.id.content_tips)
    TextView tipsView;


    public TipsViewHolder(Context context, View itemView, ChatAdapter adapter, boolean group) {
        super(context, itemView, adapter,group);
        this.context = context;
    }

    @Override
    protected void bindData(int position, TalkItem item) {


        if (item.showTime) {
            String timeStr = TimeUtil.getTimeShowString(item.time, false);
            timeLineView.setText(timeStr);
            timeLineView.setVisibility(View.VISIBLE);
        } else {
            timeLineView.setVisibility(View.GONE);
        }


        tipsView.setText(item.tips);
    }
}
