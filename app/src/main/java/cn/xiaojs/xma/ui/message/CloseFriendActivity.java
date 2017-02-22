package cn.xiaojs.xma.ui.message;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.loader.DataLoder;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.ui.base.BaseActivity;

public class CloseFriendActivity extends BaseActivity {

    public static final String CHECKED_POS = "pos";
    public static final String EXTRA_CONTACT = "extra_c";


    @BindView(R.id.lv)
    ListView listView;

    @BindView(R.id.v_empty)
    View emptyView;

    private ChoiceAdapter adapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_share_scope);
        setMiddleTitle(R.string.remind_somebody);
        setRightText(R.string.finish);
        setRightTextColor(getResources().getColor(R.color.font_orange));
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        ArrayList<Contact> expContacts = (ArrayList<Contact>) getIntent().getSerializableExtra(EXTRA_CONTACT);
        if (expContacts != null) {
            getCloseFriend(expContacts);
        }else{
            getData();
        }
    }

    @OnClick({R.id.left_image, R.id.right_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image:
                //TODO finifsh
                choiceComplete();
                break;
        }

    }

    private void choiceComplete() {

        Intent i = new Intent();

        if (adapter != null) {
            long[] pos = listView.getCheckItemIds();
            if(pos != null && pos.length >0) {
                ArrayList<Contact> contacts = new ArrayList<>(pos.length);
                for (long id: pos) {
                    Contact contact = adapter.getItem((int)id);
                    contacts.add(contact);
                }

                i.putExtra(ChoiceContactActivity.CHOOSE_CONTACT_EXTRA,contacts);
                i.putExtra(CHECKED_POS,pos);
                setResult(RESULT_OK,i);
            }

        }
        setResult(RESULT_OK,i);

        finish();
    }


    private void updateAdapter(ArrayList<Contact> contacts) {

        if (contacts == null || contacts.size() == 0) {
            showEmptyView();
            return;
        }

        if (adapter == null) {
            adapter = new ChoiceAdapter(this,
                    R.layout.layout_contact_choice_child,
                    R.id.contact_name,
                    contacts);
            listView.setAdapter(adapter);


            long[] pos = getIntent().getLongArrayExtra(CHECKED_POS);
            if (pos !=null && pos.length >0) {
                for (long p :pos) {
                    listView.setItemChecked((int)p,true);
                }
            }

        }


    }

    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
    }

    private void getCloseFriend(ArrayList<Contact> econtacts) {

        ArrayList<Contact> contacts = new ArrayList<>();

        for (Contact contact : econtacts) {
            if (contact.followType == Social.FllowType.MUTUAL) {
                contacts.add(contact);
            }
        }

        updateAdapter(contacts);

    }

    private void getData() {
        DataManager.getFriendsByType(this,
                Social.FllowType.NA,//FIXME 测试，正常需要用 MUTUAL
                new DataLoder.DataLoaderCallback<ArrayList<ContactGroup>>() {

            @Override
            public void loadCompleted(ArrayList<ContactGroup> contacts) {
                if (contacts == null || contacts.size()==0){
                    showEmptyView();
                    return;
                }
                updateAdapter(contacts.get(0).collection);
            }
        });
    }





    private class ChoiceAdapter extends ArrayAdapter<Contact> {
        public ChoiceAdapter(Context context, int resource, int tid, List<Contact> objects) {
            super(context, resource,tid,objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            TextView textView = (TextView) v.findViewById(R.id.contact_name);

            textView.setText(getItem(position).alias);
            return v;
        }
    }


}
