package cn.xiaojs.xma.ui.message;

import android.content.Context;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshExpandableListView;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ContactActivity extends BaseActivity {

    @BindView(R.id.list_contact)
    PullToRefreshExpandableListView listView;

    @BindView(R.id.search_view)
    EditText editText;

    private ContactAdapter contactAdapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_contact);
        setMiddleTitle(R.string.contact);
        setRightImage(R.drawable.ic_add);

//        //listView.setIndicatorBounds(8,8);
//        listView.setGroupIndicator(getResources().getDrawable(R.drawable.ic_expand_selector));

        listView.setVerticalScrollBarEnabled(false);
        //listView.setDivider(getResources().getDrawable(R.color.common_list_line));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                toSearch(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addTestData();
    }

    @OnClick({R.id.left_image,R.id.right_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image:

                showCreateGroupDlg();
                break;
        }

    }


    private void addTestData() {
        List<ContactGroup> groupData = new ArrayList<>();


        for (int i = 0; i < 6; i++) {

            ContactGroup cg = new ContactGroup();
            cg.name = "测试组" + i;



            cg.contacts = new ArrayList<>();

            for (int j = 0; j < 3; j++) {

                Contact c = new Contact();
                c.name = "小明" + j;
                cg.contacts.add(c);

            }


            groupData.add(cg);

        }

        contactAdapter = new ContactAdapter(this, groupData);

        listView.setAdapter(contactAdapter);

        expandALL();

    }

    private void expandALL() {

        int groupCount = contactAdapter.getGroupCount();
        for (int i=0; i<groupCount; i++) {

            listView.expandGroup(i,false);

        }
    }

    private void toSearch(String query) {

        contactAdapter.filterDta(query);

        expandALL();

    }

    public void showCreateGroupDlg() {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.setTitle(R.string.create_new_group);



        final EditText editText = new EditText(this);
        editText.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.px100)));
        editText.setHint(R.string.group_name);
        editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setPadding(10,0,10,0);
        editText.setTextColor(getResources().getColor(R.color.common_text));
        editText.setBackgroundResource(R.drawable.common_edittext_bg);
        //editText.requestFocus();


        dialog.setCustomView(editText);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {


                String inputText = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(inputText)){

                    // TODO 检测重复 and 添加


                }

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void showMoveContactDlg() {


        View view = LayoutInflater.from(this).inflate(R.layout.layout_dlg_list, null);
        ListView groupListView = (ListView) view;

        String[] payArray = getResources().getStringArray(R.array.lesson_pay_methods);
        ArrayAdapter<String> payAdapter = new ArrayAdapter<>(this,
                R.layout.layout_single_select_item,
                R.id.title,
                payArray);

        groupListView.setAdapter(payAdapter);
        groupListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        final CommonDialog dialog = new CommonDialog(this);
        dialog.setTitle(R.string.move_contact_to);
        dialog.setCustomView(groupListView);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {


                // TODO 移动操作

                dialog.dismiss();
            }
        });

        dialog.show();


    }




    private class ContactAdapter extends BaseExpandableListAdapter {

        private LayoutInflater inflater;


        private List<ContactGroup> groupData;
        private List<ContactGroup> originData;

        private String defaultCountFromat = getResources().getString(R.string.group_count);

        public ContactAdapter(Context context, List<ContactGroup> groupData) {

            inflater = LayoutInflater.from(context);

            this.originData = groupData;

            this.groupData = new ArrayList<>();
            this.groupData.addAll(originData);

        }

        @Override
        public int getGroupCount() {
            return groupData.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return groupData.get(groupPosition).contacts.size();
        }

        @Override
        public ContactGroup getGroup(int groupPosition) {
            return groupData.get(groupPosition);
        }

        @Override
        public Contact getChild(int groupPosition, int childPosition) {
            return groupData.get(groupPosition).contacts.get(childPosition);
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


            String gcount = String.format(defaultCountFromat,getChildrenCount(groupPosition));
            holder.countView.setText(gcount);

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
                holder.moveBtn = (Button) convertView.findViewById(R.id.move_contact);
                holder.delBtn = (Button) convertView.findViewById(R.id.del_contact);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.nameView.setText(getChild(groupPosition, childPosition).name);

            holder.moveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoveContactDlg();
                }
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


        public void filterDta(String query) {

            groupData.clear();

            if (TextUtils.isEmpty(query)){
                groupData.addAll(originData);

            }else{
                query = query.trim().toLowerCase();
                for (ContactGroup group :originData) {

                    ArrayList<Contact> newList = new ArrayList<Contact>();
                    for(Contact contact :group.contacts) {
                        String name = contact.name.toLowerCase();
                        if (name.contains(query)){
                            newList.add(contact);
                        }
                    }

                    if (newList.size()>0){
                        ContactGroup contactGroup = new ContactGroup();
                        contactGroup.name = group.name;
                        contactGroup.contacts = newList;

                        groupData.add(contactGroup);
                    }

                }
            }

            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        TextView nameView;
        TextView countView;
        ImageView avatarView;
        Button moveBtn;
        Button delBtn;
    }


}
