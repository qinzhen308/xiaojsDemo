package cn.xiaojs.xma.ui.classroom2.member;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.widget.LoadmoreRecyclerView;
import cn.xiaojs.xma.util.TimeUtil;

/**
 * Created by maxiaobao on 2017/9/27.
 */

public class VerifyListAdapter extends LoadmoreRecyclerView.LMAdapter {

    private final int NORMAL_TYPE = 2;

    private Context context;
    private ArrayList<StudentEnroll> studentEnrolls;
    private int avatorSize;
    private VerifyListFragment fragment;
    private ClassroomEngine classroomEngine;


    public VerifyListAdapter(VerifyListFragment fragment, Context context, ArrayList<StudentEnroll> enrolls) {
        super(fragment.getContext());
        this.context = context;
        this.fragment = fragment;
        this.studentEnrolls = enrolls;
        avatorSize = context.getResources().getDimensionPixelSize(R.dimen.px90);
        classroomEngine = ClassroomEngine.getEngine();
    }

    @Override
    public int getDataCount() {
        return studentEnrolls == null ? 0 : studentEnrolls.size();
    }

    @Override
    public int getItemType(int position) {
        return NORMAL_TYPE;
    }

    @Override
    public LoadmoreRecyclerView.LMViewHolder createViewholder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_classroom2_verification_msg_item, parent, false);

        return new VerifyListViewHolder(view);
    }

    @Override
    public void bindViewholder(LoadmoreRecyclerView.LMViewHolder holderx, final int position) {

        final VerifyListViewHolder holder = (VerifyListViewHolder) holderx;
        final StudentEnroll bean = studentEnrolls.get(position);

        holder.nameView.setText(new StringBuilder(bean.name).append(" ")
                .append(bean.mobile)
                .toString());

        holder.timeView.setText(TimeUtil.format(bean.createdOn, TimeUtil.TIME_YYYY_MM_DD_HH_MM));
        // TODO: 2017/6/30 差字段
        if (TextUtils.isEmpty(bean.remarks)) {
            holder.tvVerifyMsg.setText(R.string.request_join_class);
        } else {
            holder.tvVerifyMsg.setText(bean.remarks);
        }

        if (Platform.JoinClassState.PENDING_FOR_ACCEPTANCE.equals(bean.state)) {
            holder.agreeBtn.setVisibility(View.VISIBLE);
            holder.refuseBtn.setVisibility(View.VISIBLE);

            holder.statusView.setVisibility(View.GONE);
            holder.operaNameView.setVisibility(View.GONE);

            holder.statusView.setText("");

        } else if (Platform.JoinClassState.ACCEPTED.equals(bean.state)) {
            holder.agreeBtn.setVisibility(View.GONE);
            holder.refuseBtn.setVisibility(View.GONE);

            holder.statusView.setVisibility(View.VISIBLE);
            //FIXME 处理人姓名没返回,先隐藏
            holder.operaNameView.setVisibility(View.GONE);

            holder.statusView.setText(R.string.had_agreed);

        } else if (Platform.JoinClassState.REJECTTED.equals(bean.state)) {
            holder.agreeBtn.setVisibility(View.GONE);
            holder.refuseBtn.setVisibility(View.GONE);

            holder.statusView.setVisibility(View.VISIBLE);
            //FIXME 处理人姓名没返回,先隐藏
            holder.operaNameView.setVisibility(View.GONE);

            holder.statusView.setText(R.string.had_refused);

        } else {
            holder.agreeBtn.setVisibility(View.GONE);
            holder.refuseBtn.setVisibility(View.GONE);

            holder.statusView.setVisibility(View.GONE);
            holder.operaNameView.setVisibility(View.GONE);

            holder.statusView.setText("");
        }


        holder.agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //同意
                fragment.ackDecision(position, bean, Ctl.ACKDecision.ACKNOWLEDGE);
            }
        });

        holder.refuseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拒绝
                fragment.ackDecision(position, bean, Ctl.ACKDecision.REFUSED);
            }
        });
    }
}
