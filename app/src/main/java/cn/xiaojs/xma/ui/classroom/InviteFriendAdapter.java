package cn.xiaojs.xma.ui.classroom;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.Contact;
import cn.xiaojs.xma.ui.message.ChoiceContactActivity;

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

public class InviteFriendAdapter extends BaseExpandableListAdapter {
    private List<GroupData> mGroupData;
    private Context mContext;
    private LayoutInflater mInflater;
    private SparseArray<SparseBooleanArray> mCheckedPositions;
    private String mSelectedFriends;

    private int mChoiceMode = AbsListView.CHOICE_MODE_MULTIPLE;

    public InviteFriendAdapter(Context context) {
        init(context);
    }

    public InviteFriendAdapter(Context context, List<GroupData> data) {
        this(context);
        mGroupData = data;
    }

    public void setData(List<GroupData> data) {
        mGroupData = data;
    }

    private void init (Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mSelectedFriends = context.getString(R.string.invite_selected_friends);
        mCheckedPositions = new SparseArray<SparseBooleanArray>();
    }


    @Override
    public int getGroupCount() {
        return mGroupData != null ? mGroupData.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<Contact> cts = mGroupData.get(groupPosition).contacts;
        return cts != null ? cts.size() : 0;
    }

    @Override
    public GroupData getGroup(int groupPosition) {
        return mGroupData.get(groupPosition);
    }

    @Override
    public Contact getChild(int groupPosition, int childPosition) {
        List<Contact> cts = mGroupData.get(groupPosition).contacts;
        return cts != null ? cts.get(childPosition) : null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_contact_group, parent, false);

            holder = new ViewHolder();
            holder.nameView = (TextView) convertView.findViewById(R.id.group_name);
            holder.countView = (TextView) convertView.findViewById(R.id.group_count);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nameView.setText(getGroup(groupPosition).name);
        //holder.countView.setText(String.valueOf(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_contact_choice_child, parent, false);

            holder = new ViewHolder();
            holder.checkedView = (CheckedTextView) convertView.findViewById(R.id.check_view);
            holder.avatarView = (ImageView) convertView.findViewById(R.id.contact_avatar);
            holder.nameView = (TextView) convertView.findViewById(R.id.contact_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mCheckedPositions.get(groupPosition) != null) {
            boolean isChecked = mCheckedPositions.get(groupPosition).get(childPosition);
            holder.checkedView.setChecked(isChecked);
        }else{
            holder.checkedView.setChecked(false);
        }

        holder.nameView.setText(getChild(groupPosition, childPosition).name);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setChoiceMode(int choiceMode) {
        mChoiceMode = choiceMode;
        mCheckedPositions.clear();
    }

    public int getChildrenChoiceCount(int groupPosition) {
        if (mCheckedPositions !=null){
            SparseBooleanArray booleanArray = mCheckedPositions.get(groupPosition);
            if(booleanArray!=null) {
                return booleanArray.size();
            }
        }

        return 0;
    }

    public void setClicked(int groupPosition, int childPosition) {
        switch (mChoiceMode) {
            case AbsListView.CHOICE_MODE_MULTIPLE:
                SparseBooleanArray checkedChildPositionsMultiple = mCheckedPositions.get(groupPosition);
                // if in the group there was not any child checked
                if (checkedChildPositionsMultiple == null) {
                    checkedChildPositionsMultiple = new SparseBooleanArray();
                    // By default, the status of a child is not checked
                    // So a click will enable it
                    checkedChildPositionsMultiple.put(childPosition, true);
                    mCheckedPositions.put(groupPosition, checkedChildPositionsMultiple);
                } else {
                    boolean oldState = checkedChildPositionsMultiple.get(childPosition);
                    if(oldState){
                        checkedChildPositionsMultiple.delete(childPosition);
                    }else{
                        checkedChildPositionsMultiple.put(childPosition, !oldState);
                    }
                }

                // Notify that some data has been changed
                notifyDataSetChanged();

                break;
            case AbsListView.CHOICE_MODE_SINGLE:

                SparseBooleanArray checkedChildPositionsSingle = mCheckedPositions.get(groupPosition);
                // If in the group there was not any child checked
                if (checkedChildPositionsSingle == null) {
                    checkedChildPositionsSingle = new SparseBooleanArray();
                    // By default, the status of a child is not checked
                    checkedChildPositionsSingle.put(childPosition, true);
                    mCheckedPositions.put(groupPosition, checkedChildPositionsSingle);
                } else {
                    boolean oldState = checkedChildPositionsSingle.get(childPosition);
                    // If the old state was false, set it as the unique one which is true
                    if (!oldState) {
                        checkedChildPositionsSingle.clear();
                        checkedChildPositionsSingle.put(childPosition, !oldState);
                    } // Else does not allow the user to uncheck it
                }

                // Notify that some data has been changed
                notifyDataSetChanged();
                break;
        }
    }

    public static class GroupData {
        public String name;
        public List<Contact> contacts;
    }

    static class ViewHolder {
        TextView nameView;
        TextView countView;
        ImageView avatarView;
        CheckedTextView checkedView;
    }
}
