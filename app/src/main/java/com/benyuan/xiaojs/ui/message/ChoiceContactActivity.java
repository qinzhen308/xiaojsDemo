package com.benyuan.xiaojs.ui.message;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.core.PullToRefreshExpandableListView;
import com.benyuan.xiaojs.model.Contact;
import com.benyuan.xiaojs.model.ContactGroup;
import com.benyuan.xiaojs.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class ChoiceContactActivity extends BaseActivity {

    @BindView(R.id.list_contact)
    PullToRefreshExpandableListView listView;

    @BindView(R.id.search_view)
    EditText editText;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_contact);
        setMiddleTitle(R.string.choice_contact);
        setRightText(R.string.finish);
        listView.setGroupIndicator(getResources().getDrawable(R.drawable.ic_expand_selector));
        //listView.setDivider(getResources().getDrawable(R.color.common_list_line));
        //listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        editText.setVisibility(View.GONE);

        addTestData();
    }

    @OnClick({R.id.left_image,R.id.right_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image:
                //finish();
                break;
        }

    }


    private void addTestData() {
        List<ContactGroup> groupData = new ArrayList<>();

        Map<Integer, List<Contact>> contactData = new HashMap<>();

        for (int i = 0; i < 6; i++) {

            ContactGroup cg = new ContactGroup();
            cg.name = "测试组" + i;
            groupData.add(cg);


            List<Contact> contacts = new ArrayList<>();

            for (int j = 0; j < 3; j++) {

                Contact c = new Contact();
                c.name = "小明" + j;
                contacts.add(c);

            }

            contactData.put(i, contacts);


        }


        final ChoiceAdapter choiceAdapter = new ChoiceAdapter(this, groupData, contactData);
        choiceAdapter.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        listView.setAdapter(choiceAdapter);

        expandALL();

        // Handle the click when the user clicks an any child
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                choiceAdapter.setClicked(groupPosition, childPosition);
                return false;
            }
        });

    }

    private void expandALL() {

        int groupCount = listView.getCount();
        for (int i=0; i<groupCount; i++) {

            listView.expandGroup(i,false);

        }
    }



    private class ChoiceAdapter extends BaseExpandableListAdapter {

        private LayoutInflater inflater;
        private SparseArray<SparseBooleanArray> checkedPositions;
        private int choiceMode;


        private List<ContactGroup> groupData;
        private Map<Integer, List<Contact>> contactData;

        private String defaultCountFromat = getResources().getString(R.string.group_select_count);

        public ChoiceAdapter(Context context, List<ContactGroup> groupData, Map<Integer, List<Contact>> contactData) {

            inflater = LayoutInflater.from(context);

            this.groupData = groupData;
            this.contactData = contactData;

            checkedPositions = new SparseArray<>();

        }

        @Override
        public int getGroupCount() {
            return groupData.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return contactData.get(groupPosition).size();
        }

        @Override
        public ContactGroup getGroup(int groupPosition) {
            return groupData.get(groupPosition);
        }

        @Override
        public Contact getChild(int groupPosition, int childPosition) {
            return contactData.get(groupPosition).get(childPosition);
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
                convertView = inflater.inflate(R.layout.layout_contact_group, parent, false);

                holder = new ViewHolder();
                holder.nameView = (TextView) convertView.findViewById(R.id.group_name);
                holder.countView = (TextView) convertView.findViewById(R.id.group_count);
                convertView.setTag(holder);

            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            holder.nameView.setText(getGroup(groupPosition).name);


            String gcount = String.format(defaultCountFromat,getChildrenChoiceCount(groupPosition),
                    getChildrenCount(groupPosition));

            holder.countView.setText(gcount);

            return convertView;
        }


        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_contact_choice_child, parent, false);

                holder = new ViewHolder();

                holder.checkedView = (CheckedTextView) convertView.findViewById(R.id.check_view);
                holder.avatarView = (ImageView) convertView.findViewById(R.id.contact_avatar);
                holder.nameView = (TextView) convertView.findViewById(R.id.contact_name);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (checkedPositions.get(groupPosition) != null) {
                boolean isChecked = checkedPositions.get(groupPosition).get(childPosition);
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


        public void setClicked(int groupPosition, int childPosition) {
            switch (choiceMode) {
                case AbsListView.CHOICE_MODE_MULTIPLE:
                    SparseBooleanArray checkedChildPositionsMultiple = checkedPositions.get(groupPosition);
                    // if in the group there was not any child checked
                    if (checkedChildPositionsMultiple == null) {
                        checkedChildPositionsMultiple = new SparseBooleanArray();
                        // By default, the status of a child is not checked
                        // So a click will enable it
                        checkedChildPositionsMultiple.put(childPosition, true);
                        checkedPositions.put(groupPosition, checkedChildPositionsMultiple);
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

                    SparseBooleanArray checkedChildPositionsSingle = checkedPositions.get(groupPosition);
                    // If in the group there was not any child checked
                    if (checkedChildPositionsSingle == null) {
                        checkedChildPositionsSingle = new SparseBooleanArray();
                        // By default, the status of a child is not checked
                        checkedChildPositionsSingle.put(childPosition, true);
                        checkedPositions.put(groupPosition, checkedChildPositionsSingle);
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


        public void setChoiceMode(int choiceMode) {
            this.choiceMode = choiceMode;

            checkedPositions.clear();

        }

        public SparseArray<SparseBooleanArray> getCheckedPositions() {
            return checkedPositions;
        }

        public int getChildrenChoiceCount(int groupPosition) {

            if (checkedPositions !=null){

                SparseBooleanArray booleanArray = checkedPositions.get(groupPosition);
                if(booleanArray!=null) {
                    return booleanArray.size();
                }

            }

            return 0;
        }

    }

    static class ViewHolder {
        TextView nameView;
        TextView countView;
        ImageView avatarView;
        CheckedTextView checkedView;
    }
}
