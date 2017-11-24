package cn.xiaojs.xma.ui.classroom2.member;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom2.widget.LoadmoreRecyclerView;
import cn.xiaojs.xma.ui.widget.MessageImageView;

/**
 * Created by maxiaobao on 2017/11/13.
 */

public class VerifyListViewHolder extends LoadmoreRecyclerView.LMViewHolder{

    @BindView(R.id.time)
    TextView timeView;
    @BindView(R.id.name_num)
    TextView nameView;
    @BindView(R.id.refuse_btn)
    Button refuseBtn;
    @BindView(R.id.agree_btn)
    Button agreeBtn;
    @BindView(R.id.status_view)
    TextView statusView;
    @BindView(R.id.opera_name)
    TextView operaNameView;
    @BindView(R.id.tv_verify_msg)
    TextView tvVerifyMsg;

    public VerifyListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static View createView(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.layout_classroom2_verification_msg_item, parent, false);

    }
}
