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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.im.ChatActivity;
import cn.xiaojs.xma.common.im.CircleImageView;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshExpandableListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.CommonDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import okhttp3.ResponseBody;

import static cn.xiaojs.xma.common.xf_foundation.schemas.Social.ContactGroup.CLASSES;

public class ContactActivity extends BaseActivity {

    @BindView(R.id.list_contact)
    PullToRefreshExpandableListView listView;

    @BindView(R.id.search_view)
    EditText editText;

    private ContactAdapter contactAdapter;


    private static int class_pos = -1;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_contact);
        setMiddleTitle(R.string.contact);
        setRightImage(R.drawable.ic_add);

//        //listView.setIndicatorBounds(8,8);
//        listView.setGroupIndicator(getResources().getDrawable(R.drawable.ic_expand_selector));

        listView.setVerticalScrollBarEnabled(false);
        //listView.setDivider(getResources().getDrawable(R.color.common_list_line));

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                //FIXME 跳转到聊天界面
//                final Intent intent = new Intent(ContactActivity.this, ChatActivity.class);
//                intent.putExtra(ChatActivity.TARGET_ID, "1234567");
//                intent.putExtra(ChatActivity.TARGET_APP_KEY, "e87cffb332432eec3c0807ba");
//                startActivity(intent);

                return false;
            }
        });

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

    @OnClick({R.id.left_image, R.id.right_image})
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
        for (int i = 0; i < groupCount; i++) {

            listView.expandGroup(i, false);

        }
    }

    private void toSearch(String query) {

        if (contactAdapter == null)
            return;

        contactAdapter.filterDta(query);

        expandALL();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // move contact
    public void showMoveContactDlg(int groupPosition, final Contact contact) {

        View view = LayoutInflater.from(this).inflate(R.layout.layout_dlg_list,null);
        final ListView groupListView = (ListView) view;

        List<ContactGroup> groupList= new ArrayList<>(contactAdapter.getGroupData());

        if (class_pos != -1) {
            groupList.remove(class_pos);

            if (groupPosition > class_pos){
                groupPosition--;
            }

        }

        MoveAdapter payAdapter = new MoveAdapter(this,
                R.layout.layout_single_select_item,
                R.id.title,
                groupList);

        groupListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        groupListView.setAdapter(payAdapter);
        groupListView.setItemChecked(groupPosition,true);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int p = position;
            }
        });

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
                dialog.dismiss();
                //TODO 移动联系人
//                int pos = groupListView.getCheckedItemPosition();
//                ContactGroup group = (ContactGroup) groupListView.getItemAtPosition(pos);
//                requestMove(contact,group.group);
            }
        });

        dialog.show();

    }

    private void requestMove(Contact contact, long group) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Unfollow contact

    private void requestUnfollow(final ContactGroup contactGroup,
                                 final Contact contact,
                                 final int groupPosition,
                                 final int childPosition) {

        showProgress(true);
        SocialManager.unfollowContact(this, contact.account, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

                cancelProgress();

                removeContact(contactGroup, contact, groupPosition, childPosition);

                DataManager.removeContact(ContactActivity.this,contact.account);

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
            }
        });
    }

    private void removeContact(ContactGroup contactGroup,
                               Contact contact,
                               int groupPosition,
                               int childPosition) {
        if (contactAdapter == null)
            return;

        contactAdapter.removeData(contactGroup, contact);

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
        editText.setPadding(10, 0, 10, 0);
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
                if (!TextUtils.isEmpty(inputText)) {

                    if (isExistGroup(inputText)) {

                        Toast.makeText(ContactActivity.this,
                                R.string.group_has_exist,
                                Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    requestAddGroup(inputText);
                }

                dialog.dismiss();
            }
        });

        dialog.show();

    }


    private boolean isExistGroup(String groupName) {

        if (contactAdapter == null) {
            return false;
        }

        List<ContactGroup> groups = contactAdapter.getGroupData();
        if (groups != null) {
            for (ContactGroup contactGroup : groups) {
                if (groupName.equals(contactGroup.name)) {
                    return true;
                }
            }
        }

        return false;

    }

    private void requestAddGroup(String groupName) {

        showProgress(true);
        SocialManager.addContactGroup(this, groupName, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {

                try {

                    String j = object.string();
                    ContactGroup newGroup = SocialManager.parseAddGroup(j);

                    DataManager.addGroupData(ContactActivity.this, newGroup);

                    if (contactAdapter != null) {
                        contactAdapter.appendData(newGroup);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                cancelProgress();

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                cancelProgress();
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();

            }
        });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Request contact data and bind to View

    private void requestContactData() {

        //listView.setRefreshing();

        SocialManager.getContacts(this, new APIServiceCallback<ArrayList<ContactGroup>>() {
            @Override
            public void onSuccess(ArrayList<ContactGroup> object) {


                bindDataView(object);
                //listView.onRefreshComplete();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                //bindDataView(null);
                //listView.onRefreshComplete();

                //Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void bindDataView(ArrayList<ContactGroup> contactData) {

        if (contactData == null) {
            contactData = new ArrayList<>();
        }

        addDefaultGroup(this, contactData);

        if (contactAdapter == null) {
            contactAdapter = new ContactAdapter(this, contactData);
            listView.setAdapter(contactAdapter);
        } else {

            contactAdapter.changeData(contactData);
        }

        expandALL();
    }


    public static void addDefaultGroup(Context context, ArrayList<ContactGroup> contactData) {

        Map<Long, ContactGroup> cacheMap = DataManager.getGroupData(context);

        if (cacheMap == null)
            return;

        Map<Long, ContactGroup> tempMap = new HashMap<>(cacheMap);

        for (int i=0;i< contactData.size(); i++) {

            ContactGroup contactGroup = contactData.get(i);

            long id = contactGroup.group;

            //判断subject是为了解决从网络回调回来的问题
            if (id == CLASSES || contactGroup.subject != null){
                contactGroup.name = "班级";
                class_pos = i;
                continue;
            }

            ContactGroup mapCG = tempMap.get(id);
            if (mapCG != null) {
                tempMap.remove(id);

                if (Social.isDefaultGroup(id)){
                    contactGroup.name = Social.getContactName((int) id);
                }else{
                    contactGroup.name = mapCG.name;
                }
            }

//            if (tempMap.isEmpty()) {
//                break;
//            }

        }

        contactData.addAll(tempMap.values());

    }


    private class ContactAdapter extends BaseExpandableListAdapter {

        private LayoutInflater inflater;


        private List<ContactGroup> groupData;
        private List<ContactGroup> originData;

        private String defaultCountFromat = getResources().getString(R.string.group_count);

        private CircleTransform circleTransform;

        public ContactAdapter(Context context, List<ContactGroup> groupData) {

            inflater = LayoutInflater.from(context);

            this.originData = groupData;

            this.groupData = new ArrayList<>();
            this.groupData.addAll(originData);

            circleTransform = new CircleTransform(context);

        }

        public List<ContactGroup> getGroupData() {
            return originData;
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

        public void removeData(ContactGroup contactGroup, Contact ocontact) {
            //this.groupData.get(groupPosition).collection.remove(childPosition);

            if (filterChanges != null) {
                filterChanges.get(contactGroup).collection.remove(ocontact);
            }

            contactGroup.collection.remove(ocontact);


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

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.nameView.setText(getGroup(groupPosition).name);


            String gcount = String.format(defaultCountFromat, getChildrenCount(groupPosition));
            holder.countView.setText(gcount);

            return convertView;
        }


        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_contact_group_child, parent, false);

                holder = new ViewHolder();

                holder.avatarView = (ImageView) convertView.findViewById(R.id.contact_avatar);
                holder.nameView = (TextView) convertView.findViewById(R.id.contact_name);
                holder.moveBtn = (Button) convertView.findViewById(R.id.move_contact);
                holder.delBtn = (Button) convertView.findViewById(R.id.del_contact);
                holder.size = holder.avatarView.getMeasuredWidth();

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Contact c = getChild(groupPosition, childPosition);

            String avatar = Account.getAvatar(c.account, holder.size);
            Glide.with(ContactActivity.this)
                    .load(avatar)
                    .bitmapTransform(circleTransform)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(holder.avatarView);

            final boolean isclass = !TextUtils.isEmpty(c.title);

            String name = isclass? c.title : c.alias;
            holder.nameView.setText(name);

            holder.moveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isclass){
                        Toast.makeText(ContactActivity.this, R.string.cannt_move_class,Toast.LENGTH_SHORT).show();
                    }else{
                        showMoveContactDlg(groupPosition,c);
                    }

                }
            });

            holder.delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ContactGroup cg = getGroup(groupPosition);
                    requestUnfollow(cg, c, groupPosition, childPosition);
                }
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


        private Map<ContactGroup, ContactGroup> filterChanges;

        public void filterDta(String query) {

            groupData.clear();

            if (filterChanges != null) {
                filterChanges.clear();
            }

            if (TextUtils.isEmpty(query)) {
                groupData.addAll(originData);

            } else {
                query = query.trim().toLowerCase();
                for (ContactGroup group : originData) {

                    ArrayList<Contact> newList = new ArrayList<Contact>();
                    for (Contact contact : group.collection) {
                        String name = contact.alias;
                        if (name == null){
                            name  = contact.title;
                        }
                        if (name == null) continue;

                        if (name.toLowerCase().contains(query)) {
                            newList.add(contact);
                        }
                    }

                    if (newList.size() > 0) {
                        ContactGroup contactGroup = new ContactGroup();
                        contactGroup.name = group.name;
                        contactGroup.collection = newList;

                        groupData.add(contactGroup);

                        if (filterChanges == null) {
                            filterChanges = new HashMap<>();
                        }

                        filterChanges.put(contactGroup, group);
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
        int size;
    }


    private class MoveAdapter extends ArrayAdapter<ContactGroup> {
        public MoveAdapter(Context context, int resource,int tid, List<ContactGroup> objects) {
            super(context, resource,tid,objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            TextView textView = (TextView) v.findViewById(R.id.title);

            textView.setText(getItem(position).name);
            return v;
        }
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
