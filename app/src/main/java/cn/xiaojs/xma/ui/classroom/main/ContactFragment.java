package cn.xiaojs.xma.ui.classroom.main;

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
import java.util.Collections;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.ui.classroom.OnPanelItemClick;
import cn.xiaojs.xma.ui.classroom.talk.AttendsComparator;
import cn.xiaojs.xma.ui.classroom.talk.ContactBookAdapter;
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

public class ContactFragment extends SheetFragment implements OnPanelItemClick, ContactManager.OnAttendsChangeListener {
    //contact
    @BindView(R.id.contact_view)
    View mContactView;
    @BindView(R.id.contact_title)
    TextView mContactTitleTv;
    @BindView(R.id.contact_list_view)
    ListView mContactListView;

    private LiveCollection<Attendee> mLiveCollection;
    private ContactBookAdapter mContactBookAdapter;
    private Constants.User mUser;
    private String mTicket;
    private AttendsComparator mAttendsComparator;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initParams();

        ContactManager.getInstance().registerAttendsChangeListener(this);
    }

    private void initParams() {
        mUser = LiveCtlSessionManager.getInstance().getUser();
        mTicket = LiveCtlSessionManager.getInstance().getTicket();
        mAttendsComparator = new AttendsComparator();
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
            mContactBookAdapter = new ContactBookAdapter(mContext, mUser);
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
        //TODO 怎么判断是自己是否老师
        if (!liveCollection.attendees.contains(mySelf)) {
            liveCollection.attendees.add(0, mySelf);
        }
        int total = liveCollection.attendees != null ? liveCollection.attendees.size() : 0;
        mContactTitleTv.setText(Html.fromHtml(mContext.getString(R.string.cr_room_numbers, liveCollection.current, total)));
    }

    @Override
    public void onItemClick(int action, Attendee attendee) {
        switch (action) {
            case OnPanelItemClick.ACTION_OPEN_TALK:
                Fragment target = getTargetFragment();
                if (target != null) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.KEY_OPEN_TALK_ATTEND, attendee);
                    intent.putExtra(Constants.KEY_SHEET_GRAVITY, mSheetGravity);
                    target.onActivityResult(ClassroomController.REQUEST_CONTACT, Activity.RESULT_OK, intent);
                }
                ContactFragment.this.dismiss();
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ContactManager.getInstance().unregisterAttendsChangeListener(this);
    }

    @Override
    public void onAttendsChanged(boolean join) {
        //update list

    }
}
