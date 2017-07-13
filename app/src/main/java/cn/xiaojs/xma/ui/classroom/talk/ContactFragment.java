package cn.xiaojs.xma.ui.classroom.talk;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.main.ClassroomController;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.widget.ClosableAdapterSlidingLayout;
import cn.xiaojs.xma.ui.widget.SheetFragment;

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
 * Date:2017/5/3
 * Desc:
 *
 * ======================================================================================== */

public class ContactFragment extends SheetFragment implements
        OnAttendItemClick,
        ContactManager.OnAttendsChangeListener,
        TalkManager.OnTalkMsgReceived {
    //contact
    @BindView(R.id.contact_view)
    View mContactView;
    @BindView(R.id.contact_title)
    TextView mContactTitleTv;
    @BindView(R.id.contact_list_view)
    ListView mContactListView;

    private LiveCollection<Attendee> mLiveCollection;
    private ContactBookAdapter mContactBookAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initParams();

        ContactManager.getInstance().registerAttendsChangeListener(this);
        TalkManager.getInstance().registerMsgReceiveListener(this);
    }

    private void initParams() {
        //do nothing
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_classroom_contact, null);
    }

    @Override
    protected View getTargetView(View root) {
        return root.findViewById(R.id.contact_title);
    }

    @Override
    protected void setSlideConflictView(ClosableAdapterSlidingLayout horizontalSlidingLayout) {
        horizontalSlidingLayout.setSlideConflictView(horizontalSlidingLayout.findViewById(R.id.contact_list_view));
    }

    @Override
    protected void onFragmentShow(DialogInterface dialogInterface) {
        getContactBookData();
    }

    /**
     * 获取联系人信息
     */
    private void getContactBookData() {
        if (mContactBookAdapter == null) {
            mContactBookAdapter = new ContactBookAdapter(mContext);
            mContactBookAdapter.setOnPanelItemClick(this);
            mContactListView.setAdapter(mContactBookAdapter);
        }

        if (mLiveCollection != null) {
            mContactBookAdapter.setData(mLiveCollection);
            setEmptyContactView();
        } else {
            ContactManager.getInstance().getAttendees(mContext, new ContactManager.OnGetAttendsCallback() {
                @Override
                public void onGetAttendeesSuccess(LiveCollection<Attendee> liveCollection) {
                    if (!isDetached()) {
                        mLiveCollection = liveCollection;
                        addMyself2Attendees(mLiveCollection);
                        mContactBookAdapter.setData(mLiveCollection);
                        setEmptyContactView();
                    }
                }

                @Override
                public void onGetAttendeesFailure(String errorCode, String errorMessage) {
                    if (!isDetached()) {
                        setEmptyContactView();
                    }
                }
            });
        }
    }

    private void setEmptyContactView() {

    }

    private void addMyself2Attendees(LiveCollection<Attendee> liveCollection) {
        if (mLiveCollection == null || isDetached()) {
            return;
        }

        if (liveCollection.attendees == null) {
            liveCollection.attendees = new ArrayList<Attendee>();
        }

        Attendee mySelf = new Attendee();
        mySelf.accountId = AccountDataManager.getAccountID(mContext);
        mySelf.name = XiaojsConfig.mLoginUser != null ? XiaojsConfig.mLoginUser.getName() : null;
        if (!liveCollection.attendees.contains(mySelf)) {
            liveCollection.attendees.add(0, mySelf);
        }
        int total = liveCollection.attendees != null ? liveCollection.attendees.size() : 0;

        if (liveCollection.current > total) {
            total = liveCollection.current;
        }

        mContactTitleTv.setText(Html.fromHtml(mContext.getString(R.string.cr_room_numbers, liveCollection.current, total)));
    }

    @Override
    public void onItemClick(int action, Attendee attendee) {
        Fragment target = getTargetFragment();
        switch (action) {
            case OnAttendItemClick.ACTION_OPEN_TALK:
                if (target != null) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.KEY_TALK_ATTEND, attendee);
                    intent.putExtra(Constants.KEY_TALK_ACTION, OnAttendItemClick.ACTION_OPEN_TALK);
                    intent.putExtra(Constants.KEY_SHEET_GRAVITY, mSheetGravity);
                    target.onActivityResult(ClassroomController.REQUEST_CONTACT, Activity.RESULT_OK, intent);
                }
                break;
            case OnAttendItemClick.ACTION_OPEN_CAMERA:

                AnalyticEvents.onEvent(mContext,59);

                if (target != null) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.KEY_TALK_ATTEND, attendee);
                    intent.putExtra(Constants.KEY_TALK_ACTION, OnAttendItemClick.ACTION_OPEN_CAMERA);
                    intent.putExtra(Constants.KEY_SHEET_GRAVITY, mSheetGravity);
                    target.onActivityResult(ClassroomController.REQUEST_CONTACT, Activity.RESULT_OK, intent);
                }
                break;
        }

        ContactFragment.this.dismiss();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ContactManager.getInstance().unregisterAttendsChangeListener(this);
        TalkManager.getInstance().unregisterMsgReceiveListener(this);
    }

    @Override
    public void onAttendsChanged(ArrayList<Attendee> attendees, int action) {
        //update list
        if (action == ContactManager.ACTION_JOIN || action == ContactManager.ACTION_LEAVE) {
            int total = attendees != null ? attendees.size() : 0;
            int current = 0;
            if (attendees != null) {
                for (Attendee attendee : attendees) {
                    if (attendee.xa > 0) {
                        current++;
                    }
                }
            }

            if (current > total) {
                total = current;
            }

            mContactTitleTv.setText(Html.fromHtml(mContext.getString(R.string.cr_room_numbers, current, total)));
        }

        if (mContactBookAdapter != null) {
            mContactBookAdapter.notifyDataSetChanged();
        }
    }

    //update unread msg
    @Override
    public void onMsgChanged(boolean receive, int criteria, TalkItem talkItem) {
        if (mContactBookAdapter != null) {
            mContactBookAdapter.notifyDataSetChanged();
        }
    }
}
