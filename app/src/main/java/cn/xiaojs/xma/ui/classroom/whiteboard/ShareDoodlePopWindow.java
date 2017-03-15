package cn.xiaojs.xma.ui.classroom.whiteboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.ui.classroom.talk.InviteFriendAdapter;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.RoundedImageView;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/2/22
 * Desc:
 *
 * ======================================================================================== */

public class ShareDoodlePopWindow extends PopupWindow implements InviteFriendAdapter.SelectionListener,
        View.OnClickListener {
    private final static int MODE_UN_CHECK_ALL = 0;
    private final static int MODE_CHECK_ALL = 1;

    private Context mContext;
    private TextView mEmptyView;
    private ListView mListView;
    private ImageView mCheckAllBtn;
    private int mCheckMode = MODE_UN_CHECK_ALL;
    private String mTicket;
    private ContactAdapter mContactAdapter;

    public ShareDoodlePopWindow(Context context) {
        this(context, null);
    }

    public ShareDoodlePopWindow(Context context, String ticket) {
        mContext = context;
        mTicket = ticket;

        init();
    }

    private void init() {
        setFocusable(true);
        setOutsideTouchable(true);
        setWindowLayoutMode(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        setClippingEnabled(false);

        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_share_contact_book, null);
        setContentView(v);

        mCheckAllBtn = (ImageView) v.findViewById(R.id.check_to_discussion);
        mEmptyView = (TextView) v.findViewById(R.id.empty_view);
        mListView = (ListView) v.findViewById(R.id.contact_list);

        mCheckAllBtn.setOnClickListener(this);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);

        initData();
    }

    public void initData() {
        LiveManager.getAttendees(mContext, mTicket, new APIServiceCallback<LiveCollection<Attendee>>() {
            @Override
            public void onSuccess(LiveCollection<Attendee> liveCollection) {
                ArrayList<Attendee> attendees = liveCollection != null ? liveCollection.attendees : null;
                if (mContactAdapter == null) {
                    mContactAdapter = new ContactAdapter(attendees);
                }
                mListView.setAdapter(mContactAdapter);
                mEmptyView.setVisibility(attendees == null || attendees.isEmpty() ? View.VISIBLE : View.GONE);
                if (XiaojsConfig.DEBUG) {
                    Toast.makeText(mContext, "获取好友列表成功", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                mEmptyView.setVisibility(View.VISIBLE);
                if (XiaojsConfig.DEBUG) {
                    Toast.makeText(mContext, "获取联系:" + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onSelectChanged(int selectionCount) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_to_discussion:
                if (mContactAdapter != null) {
                    /*if (mCheckMode == MODE_UN_CHECK_ALL) {
                        mCheckMode = MODE_CHECK_ALL;
                        mCheckAllBtn.setImageResource(R.drawable.ic_multi_checked);
                        mContactAdapter.checkAll();
                    } else {
                        mCheckMode = MODE_UN_CHECK_ALL;
                        mContactAdapter.unCheckAll();
                        mCheckAllBtn.setImageResource(R.drawable.ic_multi_no_checked);
                        mCheckAllBtn.setSelected(false);
                    }*/

                    mContactAdapter.unCheckAll();
                }
                break;
        }
    }

    /**
     * 分享好友的联系人列表
     */
    private class ContactAdapter extends BaseAdapter {
        private ArrayList<Attendee> mAttendeeList;
        private List<String> mChoiceList;
        private View.OnClickListener mClickListener;

        public ContactAdapter(ArrayList<Attendee> attendees) {
            mAttendeeList = attendees;
            mChoiceList = new ArrayList<String>();
            setOnclickListener();
        }

        @Override
        public int getCount() {
            return mAttendeeList != null ? mAttendeeList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mAttendeeList != null ? mAttendeeList.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = createHolder();
            }

            Holder holder = (Holder)convertView.getTag();
            setContentView(position, holder);

            return convertView;
        }

        private View createHolder () {
            View v = LayoutInflater.from(mContext).inflate(R.layout.layout_classroom_invite_friend_item, null);
            Holder holder = new Holder();
            holder.checkbox = (ImageView) v.findViewById(R.id.checkbox);
            holder.checkbox.setImageResource(R.drawable.single_check_selector);
            holder.checkbox.setOnClickListener(mClickListener);
            holder.portrait = (RoundedImageView) v.findViewById(R.id.portrait);
            holder.name = (TextView) v.findViewById(R.id.name);
            holder.name.setTextColor(mContext.getResources().getColor(R.color.font_white));
            v.setTag(holder);
            return v;
        }

        private void setContentView(int position, Holder holder) {
            Attendee bean = mAttendeeList.get(position);
            holder.checkbox.setSelected(mChoiceList.contains(String.valueOf(position)));
            holder.position = position;
            Glide.with(mContext)
                    .load(bean.avatar)
                    .transform(new CircleTransform(mContext))
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(holder.portrait);
            holder.name.setText(bean.name);
        }

        private class Holder {
            ImageView checkbox;
            RoundedImageView portrait;
            TextView name;
            int position = -1;
        }

        public void checkAll() {
            if (mChoiceList != null) {
                int count = getCount();
                for (int i = 0; i < count; i++) {
                    mChoiceList.add(String.valueOf(i));
                }
                notifyDataSetChanged();
            }
        }

        public void unCheckAll() {
            if (mChoiceList != null) {
                mChoiceList.clear();
                notifyDataSetChanged();
            }
        }

        private void setOnclickListener() {
            mClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unCheckAll();
                }
            };
        }
    }
}
