package cn.xiaojs.xma.ui.message;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.widget.MessageImageView;


public class MessageFragment extends BaseFragment {

    @BindView(R.id.home_message_list)
    ListView mMessageList;

    private HeaderHolder headerHolder;


    @Override
    protected View getContentView() {
        View v = mContext.getLayoutInflater().inflate(R.layout.fragment_message, null);
        return v;
    }

    @Override
    protected void init() {


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.activity_list_item);
        mMessageList.setAdapter(arrayAdapter);

        headerHolder = new HeaderHolder();
        View headerView = headerHolder.getHeaderView();
        mMessageList.addHeaderView(headerView);
    }

    private class HeaderHolder{
        public TextView inventTimeTv;
        public TextView inventContentTv;
        public MessageImageView inventImg;

        public TextView ctlTimeTv;
        public TextView ctlContentTv;
        public MessageImageView ctlImg;

        public TextView socialTimeTv;
        public TextView socialContentTv;
        public MessageImageView socialImg;

        public TextView issueTimeTv;
        public TextView issueContentTv;
        public MessageImageView issueImg;

        public TextView businessTimeTv;
        public TextView businessContentTv;
        public MessageImageView businessImg;

        public TextView recommTimeTv;
        public TextView recommContentTv;
        public MessageImageView recommImg;

        public TextView xjsTimeTv;
        public TextView xjsContentTv;
        public MessageImageView xjsImg;

        public View getHeaderView() {

            LinearLayout headerLayout = new LinearLayout(getContext());
            headerLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT));
            headerLayout.setOrientation(LinearLayout.VERTICAL);

            View inventView = createHeaderItem("邀请消息");
            inventTimeTv = (TextView) inventView.findViewById(R.id.message_time);
            inventContentTv = (TextView) inventView.findViewById(R.id.message_content);
            inventImg = (MessageImageView) inventView.findViewById(R.id.message_image);
            inventImg.setImageResource(R.drawable.ic_message_invite);
            headerLayout.addView(inventView);

            View ctlView = createHeaderItem("课程消息");
            ctlTimeTv = (TextView) ctlView.findViewById(R.id.message_time);
            ctlContentTv = (TextView) ctlView.findViewById(R.id.message_content);
            ctlImg = (MessageImageView) ctlView.findViewById(R.id.message_image);
            ctlImg.setImageResource(R.drawable.ic_message_course_information);
            headerLayout.addView(ctlView);

            View socialView = createHeaderItem("社交消息");
            socialTimeTv = (TextView) socialView.findViewById(R.id.message_time);
            socialContentTv = (TextView) socialView.findViewById(R.id.message_content);
            socialImg = (MessageImageView) socialView.findViewById(R.id.message_image);
            socialImg.setImageResource(R.drawable.ic_message_socialnews);
            headerLayout.addView(socialView);

            View issueView = createHeaderItem("问答消息");
            issueTimeTv = (TextView) issueView.findViewById(R.id.message_time);
            issueContentTv = (TextView) issueView.findViewById(R.id.message_content);
            issueImg = (MessageImageView) issueView.findViewById(R.id.message_image);
            issueImg.setImageResource(R.drawable.ic_message_qanswerme);
            headerLayout.addView(issueView);

            View businessView = createHeaderItem("交易消息");
            businessTimeTv = (TextView) businessView.findViewById(R.id.message_time);
            businessContentTv = (TextView) businessView.findViewById(R.id.message_content);
            businessImg = (MessageImageView) businessView.findViewById(R.id.message_image);
            businessImg.setImageResource(R.drawable.ic_message_transactionmessage);
            headerLayout.addView(businessView);

            View recommView = createHeaderItem("精选推荐");
            recommTimeTv = (TextView) recommView.findViewById(R.id.message_time);
            recommContentTv = (TextView) recommView.findViewById(R.id.message_content);
            recommImg = (MessageImageView) recommView.findViewById(R.id.message_image);
            recommImg.setImageResource(R.drawable.ic_message_recommendedselection);
            headerLayout.addView(recommView);

            View xjsView = createHeaderItem("平台提醒");
            xjsTimeTv = (TextView) xjsView.findViewById(R.id.message_time);
            xjsContentTv = (TextView) xjsView.findViewById(R.id.message_content);
            xjsImg = (MessageImageView) xjsView.findViewById(R.id.message_image);
            xjsImg.setImageResource(R.drawable.ic_xjs_msg);
            headerLayout.addView(xjsView);

            return headerLayout;

        }

        private View createHeaderItem(String title) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_message_header_item, null);
            TextView titleTv = (TextView) view.findViewById(R.id.message_title);
            titleTv.setText(title);
            return view;
        }
    }


}
