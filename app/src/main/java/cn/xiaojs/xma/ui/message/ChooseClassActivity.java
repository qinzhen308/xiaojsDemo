package cn.xiaojs.xma.ui.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.loader.DataLoder;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.ui.base.BaseActivity;

import static cn.xiaojs.xma.ui.message.PostDynamicActivity.EXTRA_CLASS_POS;

public class ChooseClassActivity extends BaseActivity {

    public static final int ENTER_TYPE_CHOOSE_CLASS = 0;
    public static final int ENTER_TYPE_ADD_STUDENT = 1;
    public static final String EXTRA_ENTER_TYPE = "enter_type";
    public static final String EXTRA_SUBTYPE = "subtype";

    @BindView(R.id.tips_content)
    TextView tipsContentView;
    @BindView(R.id.lay_tips)
    LinearLayout tipsRootView;

    @BindView(R.id.lv)
    ListView listView;

    @BindView(R.id.v_empty)
    View emptyView;

    private ChoiceAdapter adapter;
    private UpdateReceiver updateReceiver;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_share_scope);

        int enterType = getIntent().getIntExtra(EXTRA_ENTER_TYPE, ENTER_TYPE_CHOOSE_CLASS);
        if (enterType == ENTER_TYPE_ADD_STUDENT) {
            setMiddleTitle(R.string.student_add_from_exist_class);
            tipsContentView.setText(R.string.add_student_tips);
            tipsRootView.setVisibility(View.VISIBLE);
        }else {
            setMiddleTitle(R.string.choose_class);
        }


        updateReceiver = new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DataManager.ACTION_UPDATE_CLASS_FROM_DB);
        registerReceiver(updateReceiver,intentFilter);


        setRightText(R.string.finish);
        setRightTextColor(getResources().getColor(R.color.font_orange));
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        getData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(updateReceiver);
    }

    @OnClick({R.id.left_image, R.id.right_image2, R.id.lesson_creation_tips_close})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image2:
                //TODO finifsh
                choiceComplete();
                break;
            case R.id.lesson_creation_tips_close://关闭提醒
                closeCourCreateTips();
                break;
        }

    }

    private void closeCourCreateTips() {
        tipsRootView.setVisibility(View.GONE);
    }

    private void updateAdapter(ArrayList<Contact> contacts) {

        if (contacts == null || contacts.size() == 0) {
            showEmptyView();
            return;
        }

        if (adapter == null) {

            adapter = new ChoiceAdapter(this,
                    R.layout.layout_single_select_item,
                    R.id.title,
                    contacts);
            listView.setAdapter(adapter);

            int pos = getIntent().getIntExtra(EXTRA_CLASS_POS, -1);
            if (pos >=0 && listView.getCount()>pos){
                listView.setItemChecked(pos,true);
            }


//            long[] pos = getIntent().getLongArrayExtra(CHECKED_POS);
//            if (pos !=null && pos.length >0) {
//                for (long p :pos) {
//                    listView.setItemChecked((int)p,true);
//                }
//            }

        }


    }

    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
    }


    private void getData() {
        DataManager.getClasses(this,
                new DataLoder.DataLoaderCallback<ArrayList<ContactGroup>>() {

                    @Override
                    public void loadCompleted(ArrayList<ContactGroup> contacts) {

                        DataManager.lanuchLoadContactService(getApplicationContext(),
                                DataManager.TYPE_FETCH_CLASS_FROM_NET);

                        if (contacts == null || contacts.size() == 0) {
                            showEmptyView();
                            return;
                        }
                        updateAdapter(contacts.get(0).collection);
                    }
                });
    }

    private void choiceComplete() {

        Intent i = new Intent();

        if (adapter != null) {
            int pos = listView.getCheckedItemPosition();
            if (pos >= 0) {
                ArrayList<Contact> contacts = new ArrayList<>(1);
                Contact contact = adapter.getItem(pos);
                contacts.add(contact);
                i.putExtra(ChoiceContactActivity.CHOOSE_CONTACT_EXTRA, contacts);
                i.putExtra(EXTRA_SUBTYPE, contact.subtype);
                i.putExtra(EXTRA_CLASS_POS,pos);
                setResult(RESULT_OK, i);
            }

        }
        setResult(RESULT_OK, i);
        finish();
    }


    private class ChoiceAdapter extends ArrayAdapter<Contact> {
        public ChoiceAdapter(Context context, int resource, int tid, List<Contact> objects) {
            super(context, resource, tid, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            TextView textView = (TextView) v.findViewById(R.id.title);

            textView.setText(getItem(position).alias);
            return v;
        }
    }

    private class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DataManager.ACTION_UPDATE_CLASS_FROM_DB)) {

                if(XiaojsConfig.DEBUG) {
                    Logger.d("UpdateReceiver: to update class");
                }

                ArrayList<ContactGroup> newCGroups = (ArrayList<ContactGroup>) intent.
                        getSerializableExtra(DataManager.EXTRA_CONTACT);

                if(newCGroups != null) {
                    updateAdapter(newCGroups.get(0).collection);
                }

            }
        }
    }
}
