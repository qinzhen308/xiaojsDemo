package cn.xiaojs.xma.ui.message;

import android.content.Context;

import android.content.Intent;
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
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshExpandableListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;

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

        requestContactData();
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



    private void expandALL() {

        int groupCount = contactAdapter.getGroupCount();
        for (int i=0; i<groupCount; i++) {

            listView.expandGroup(i,false);

        }
    }

    private void toSearch(String query) {

        if (contactAdapter ==null)
            return;

        contactAdapter.filterDta(query);

        expandALL();

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


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //add new group

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

                    requestAddGroup(inputText);


                }

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void requestAddGroup(String groupName) {

        showProgress(true);
        SocialManager.addContactGroup(this, groupName, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {

                try {

                    String j = object.string();
                    ContactGroup newGroup= SocialManager.parseAddGroup(j);

                    if (contactAdapter !=null) {
                        contactAdapter.appendData(newGroup);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

                cancelProgress();

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                cancelProgress();
                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();

            }
        });
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Request contact data and bind to View

    private void requestContactData() {

        listView.setRefreshing();

        SocialManager.getContacts(this, new APIServiceCallback<ArrayList<ContactGroup>>() {
            @Override
            public void onSuccess(ArrayList<ContactGroup> object) {

                bindDataView(object);
                listView.onRefreshComplete();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                bindDataView(null);
                listView.onRefreshComplete();

                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void bindDataView(ArrayList<ContactGroup> contactData) {

        if (contactData == null) {
            contactData = new ArrayList<>();
        }

        addDefaultGroup(contactData);

        if(contactAdapter ==null) {
            contactAdapter = new ContactAdapter(this, contactData);
            listView.setAdapter(contactAdapter);
        }else{

            contactAdapter.changeData(contactData);
        }

        expandALL();
    }


    private void addDefaultGroup(ArrayList<ContactGroup> contactData) {

        Map<Long, ContactGroup> map = getDefaultGroup();

        for (ContactGroup contactGroup : contactData) {

            long id = contactGroup.group;

            if (map.get(id) != null){
                map.remove(id);

                contactGroup.name = Social.getContactName((int)id);
            }

            if (map.isEmpty()){
                break;
            }

        }

        contactData.addAll(map.values());

    }


    public Map<Long, ContactGroup> getDefaultGroup() {

        Map<Long, ContactGroup> map = new HashMap<>();

        map.put((long)Social.ContactGroup.TEACHERS,createGroup(Social.ContactGroup.TEACHERS));
        map.put((long)Social.ContactGroup.STUDENTS,createGroup(Social.ContactGroup.STUDENTS));
        map.put((long)Social.ContactGroup.CLASSMATES,createGroup(Social.ContactGroup.CLASSMATES));
        map.put((long)Social.ContactGroup.FRIENDS,createGroup(Social.ContactGroup.FRIENDS));
        map.put((long)Social.ContactGroup.ORGANIZATIONS,createGroup(Social.ContactGroup.ORGANIZATIONS));
        map.put((long)Social.ContactGroup.COLLEAGUES,createGroup(Social.ContactGroup.COLLEAGUES));
        map.put((long)Social.ContactGroup.STRANGERS,createGroup(Social.ContactGroup.STRANGERS));

        return map;
    }

    private ContactGroup createGroup(int groupId) {

        ContactGroup group = new ContactGroup();
        group.group = groupId;
        group.name = Social.getContactName(groupId);
        group.collection = new ArrayList<>(0);
        return group;

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

        public void changeData(ArrayList<ContactGroup> contactData) {
            this.originData = contactData;
            this.groupData.clear();
            this.groupData.addAll(originData);

            notifyDataSetChanged();
        }

        public void appendData(ContactGroup newGroup) {
            this.originData.add(newGroup);
            this.groupData.add(newGroup);

            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return groupData.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return groupData.get(groupPosition).collection.size();
        }

        @Override
        public ContactGroup getGroup(int groupPosition) {
            return groupData.get(groupPosition);
        }

        @Override
        public Contact getChild(int groupPosition, int childPosition) {
            return groupData.get(groupPosition).collection.get(childPosition);
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

            holder.nameView.setText(getChild(groupPosition, childPosition).alias);

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
                    for(Contact contact :group.collection) {
                        String name = contact.alias.toLowerCase();
                        if (name.contains(query)){
                            newList.add(contact);
                        }
                    }

                    if (newList.size()>0){
                        ContactGroup contactGroup = new ContactGroup();
                        contactGroup.name = group.name;
                        contactGroup.collection = newList;

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


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //test data

    //    private void addTestData() {
//        List<ContactGroup> groupData = new ArrayList<>();
//
//
//        for (int i = 0; i < 6; i++) {
//
//            ContactGroup cg = new ContactGroup();
//            cg.name = "测试组" + i;
//
//
//
//            cg.contacts = new ArrayList<>();
//
//            for (int j = 0; j < 3; j++) {
//
//                Contact c = new Contact();
//                c.name = "小明" + j;
//                cg.contacts.add(c);
//
//            }
//
//
//            groupData.add(cg);
//
//        }
//
//        contactAdapter = new ContactAdapter(this, groupData);
//
//        listView.setAdapter(contactAdapter);
//
//        expandALL();
//
//    }


}
