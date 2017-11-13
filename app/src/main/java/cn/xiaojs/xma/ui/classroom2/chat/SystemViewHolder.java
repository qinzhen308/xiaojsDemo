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

public class SystemViewHolder extends ChatViewHolder {


    @BindView(R.id.time_line)
    TextView timeLineView;
    @BindView(R.id.content_tips)
    TextView tipsView;


    public SystemViewHolder(Context context, View itemView) {
        super(context, itemView);
        this.context = context;
    }

    @Override
    protected void bindData(TalkItem item) {


        if (item.showTime) {
            String timeStr = TimeUtil.getTimeShowString(item.time, false);
            timeLineView.setText(timeStr);
            timeLineView.setVisibility(View.VISIBLE);
        } else {
            timeLineView.setVisibility(View.GONE);
        }

        tipsView.setText(item.body.text);
    }
}
