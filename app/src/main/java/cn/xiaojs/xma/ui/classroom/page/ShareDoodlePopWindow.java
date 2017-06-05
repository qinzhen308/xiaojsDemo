package cn.xiaojs.xma.ui.classroom.page;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.ui.classroom.page.OnPhotoDoodleShareListener;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom.talk.ContactManager;
import cn.xiaojs.xma.ui.classroom.talk.InviteFriendAdapter;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardController;
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
    private ImageView mCheckToDiscussionBtn;
    private int mCheckMode = MODE_UN_CHECK_ALL;
    private ContactAdapter mContactAdapter;
    private WhiteboardController mBoardController;
    private OnPhotoDoodleShareListener mEditedVideoShareListener;
    private ArrayList<Attendee> mAttendees;

    public ShareDoodlePopWindow(Context context, WhiteboardController controller, OnPhotoDoodleShareListener listener) {
        mContext = context;
        mEditedVideoShareListener = listener;
        mBoardController = controller;
        mAttendees = new ArrayList<Attendee>();
        init();
    }

    private void init() {
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setClippingEnabled(false);
        setBackgroundDrawable(new ColorDrawable());
        setWindowLayoutMode(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_share_contact_book, null);
        setContentView(v);

        mCheckToDiscussionBtn = (ImageView) v.findViewById(R.id.check_to_discussion);
        mCheckToDiscussionBtn.setSelected(true);
        mCheckToDiscussionBtn.setImageResource(R.drawable.single_check_selector);
        mEmptyView = (TextView) v.findViewById(R.id.empty_view);
        mListView = (ListView) v.findViewById(R.id.contact_list);

        mCheckToDiscussionBtn.setOnClickListener(this);
        v.findViewById(R.id.confirm_share).setOnClickListener(this);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);

        if (mContactAdapter != null) {
            mContactAdapter.setData(null);
        }
        initData();
    }

    public void initData() {
        ContactManager.getInstance().getAttendees(mContext, new ContactManager.OnGetAttendsCallback() {
            @Override
            public void onGetAttendeesSuccess(LiveCollection<Attendee> liveCollection) {
                ArrayList<Attendee> attendees = liveCollection != null ? liveCollection.attendees : null;
                //exclude myself
                mAttendees.clear();
                if (attendees != null) {
                    for (Attendee attendee : attendees) {
                        if (!ClassroomBusiness.isMyself(mContext, attendee.accountId)) {
                            mAttendees.add(attendee);
                        }
                    }
                }

                //set data
                if (mContactAdapter == null) {
                    mContactAdapter = new ContactAdapter(mAttendees);
                } else {
                    mContactAdapter.setData(mAttendees);
                }
                mListView.setAdapter(mContactAdapter);
                mEmptyView.setVisibility(mAttendees == null || mAttendees.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onGetAttendeesFailure(String errorCode, String errorMessage) {
                mEmptyView.setVisibility(View.VISIBLE);
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
                    mCheckToDiscussionBtn.setSelected(true);
                    mContactAdapter.unCheckAll();
                }
                break;
            case R.id.confirm_share:
                if (mCheckToDiscussionBtn.isSelected()) {
                    if (mEditedVideoShareListener != null) {
                        mEditedVideoShareListener.onPhotoShared(null, mBoardController.getWhiteboardBitmap());
                    }
                    dismiss();
                } else if (mContactAdapter != null && mContactAdapter.getCheckedAttendee() != null){
                    if (mEditedVideoShareListener != null) {
                        mEditedVideoShareListener.onPhotoShared(mContactAdapter.getCheckedAttendee(), mBoardController.getWhiteboardBitmap());
                    }
                    dismiss();
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

        public void setData(ArrayList<Attendee> attendees) {
            mAttendeeList = attendees;
            notifyDataSetChanged();
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
            holder.portrait = (RoundedImageView) v.findViewById(R.id.portrait);
            holder.name = (TextView) v.findViewById(R.id.name);
            holder.name.setTextColor(mContext.getResources().getColor(R.color.font_white));
            v.setOnClickListener(mClickListener);
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
                    mCheckToDiscussionBtn.setSelected(false);
                    unCheckAll();
                    Holder holder = (Holder)v.getTag();
                    mChoiceList.add(String.valueOf(holder.position));
                    notifyDataSetChanged();
                }
            };
        }

        public Attendee getCheckedAttendee() {
            if (mChoiceList == null || mChoiceList.isEmpty()) {
                return null;
            }

            try {
                return mAttendeeList.get(Integer.parseInt(mChoiceList.get(0)));
            } catch (Exception e) {

            }

            return null;
        }
    }

}
