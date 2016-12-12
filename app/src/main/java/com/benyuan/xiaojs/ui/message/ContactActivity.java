package com.benyuan.xiaojs.ui.message;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
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

public class ContactActivity extends BaseActivity {

    @BindView(R.id.list_contact)
    PullToRefreshExpandableListView listView;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_contact);
        setMiddleTitle(R.string.contact);
        setRightImage(R.drawable.ic_add);
        listView.setGroupIndicator(getResources().getDrawable(R.drawable.ic_expand_selector));
        //listView.setDivider(getResources().getDrawable(R.color.common_list_line));

        addTestData();
    }

    @OnClick({R.id.left_image,R.id.right_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image:
                finish();
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

            for (int j = 0; j < 8; j++) {

                Contact c = new Contact();
                c.name = "小明" + j;
                contacts.add(c);

            }

            contactData.put(i, contacts);


        }

        listView.setAdapter(new ContactAdapter(this, groupData, contactData));

    }


    private class ContactAdapter extends BaseExpandableListAdapter {

        private LayoutInflater inflater;

        private List<ContactGroup> groupData;
        private Map<Integer, List<Contact>> contactData;

        public ContactAdapter(Context context, List<ContactGroup> groupData, Map<Integer, List<Contact>> contactData) {

            inflater = LayoutInflater.from(context);

            this.groupData = groupData;
            this.contactData = contactData;

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

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_contact_group, parent, false);
            }

            TextView tv = (TextView) convertView;
            tv.setText(getGroup(groupPosition).name);

            return convertView;
        }


        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_contact_group_child, parent, false);

                holder = new ViewHolder();

                holder.avatarView = (ImageView) convertView.findViewById(R.id.contact_avatar);
                holder.nameView = (TextView) convertView.findViewById(R.id.contact_name);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.nameView.setText(getChild(groupPosition, childPosition).name);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    static class ViewHolder {
        TextView nameView;
        ImageView avatarView;
    }
}
