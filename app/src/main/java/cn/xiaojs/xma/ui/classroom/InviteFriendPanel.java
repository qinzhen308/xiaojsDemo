package cn.xiaojs.xma.ui.classroom;
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
 * Date:2016/12/27
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.social.Contact;

public class InviteFriendPanel extends Panel {
    private ExpandableListView mFriendListView;
    private InviteFriendAdapter mAdapter;

    public InviteFriendPanel(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_classroom_invite_friend, null);
    }

    @Override
    public void initChildView(View root) {
        mFriendListView = (ExpandableListView)root.findViewById(R.id.friends);
    }

    @Override
    public void initData() {
        List<InviteFriendAdapter.GroupData> groupDataList = new ArrayList<InviteFriendAdapter.GroupData>();
        InviteFriendAdapter.GroupData groupData;
        for (int i = 0; i < 8; i++) {
            groupData = new InviteFriendAdapter.GroupData();
            groupData.name = "班级" + i;
            groupData.contacts = new ArrayList<Contact>();

            for (int j = 0; j < 3; j++) {
                Contact contact = new Contact();
                contact.name = "小明" + i + "" + j;
                groupData.contacts.add(contact);
            }

            //add to group
            groupDataList.add(groupData);
        }

        if (mAdapter == null) {
            mAdapter = new InviteFriendAdapter(mContext);
            mAdapter.setData(groupDataList);
            mAdapter.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            mFriendListView.setAdapter(mAdapter);

            // Handle the click when the user clicks an any child
            mFriendListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    mAdapter.setClicked(groupPosition, childPosition);
                    return false;
                }
            });

            mFriendListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {
                    /*for (int i = 0, count = mAdapter.getGroupCount(); i < count; i++) {
                        if (groupPosition != i) {
                            //close other group
                            mFriendListView.collapseGroup(i);
                        }
                    }*/
                }
            });
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    private void expandALL() {
        int groupCount = mFriendListView.getCount();
        for (int i=0; i< groupCount; i++) {
            mFriendListView.expandGroup(i, false);
        }
    }
}
