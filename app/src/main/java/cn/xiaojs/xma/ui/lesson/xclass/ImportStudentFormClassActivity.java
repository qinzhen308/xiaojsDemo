package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.loader.DataLoder;
import cn.xiaojs.xma.model.ctl.ClassEnroll;
import cn.xiaojs.xma.model.ctl.ClassEnrollParams;
import cn.xiaojs.xma.model.ctl.EnrollImport;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.message.ChoiceContactActivity;
import cn.xiaojs.xma.util.ToastUtil;

import static cn.xiaojs.xma.ui.message.PostDynamicActivity.EXTRA_CLASS_POS;

public class ImportStudentFormClassActivity extends BaseActivity {

    public static final String EXTRA_IMPORTS = "imports";

    @BindView(R.id.tips_content)
    TextView tipsContentView;
    @BindView(R.id.lay_tips)
    LinearLayout tipsRootView;

    @BindView(R.id.lv)
    ListView listView;

    @BindView(R.id.v_empty)
    View emptyView;

    private ChoiceAdapter adapter;
    private String classId;

    private UpdateReceiver updateReceiver;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_share_scope);

        classId = getIntent().getStringExtra(ClassInfoActivity.EXTRA_CLASSID);

        updateReceiver = new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DataManager.ACTION_UPDATE_CLASS_FROM_DB);
        registerReceiver(updateReceiver,intentFilter);

        setMiddleTitle(R.string.student_add_from_exist_class);
        tipsContentView.setText(R.string.import_class_student_tips);
        tipsRootView.setVisibility(View.VISIBLE);


        setRightText(R.string.finish);
        setRightTextColor(getResources().getColor(R.color.font_orange));
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
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
                if (adapter != null) {
                    choiceComplete();
                }
                break;
            case R.id.lesson_creation_tips_close://关闭提醒
                closeCourCreateTips();
                break;
        }

    }

    private void closeCourCreateTips() {
        tipsRootView.setVisibility(View.GONE);
    }

    private synchronized void updateAdapter(ArrayList<Contact> contacts) {

        if (contacts == null || contacts.size() == 0) {
            showEmptyView();
            return;
        }

        if (adapter == null) {

            adapter = new ChoiceAdapter(this,
                    R.layout.layout_multiple_select_item,
                    R.id.title,
                    contacts);
            listView.setAdapter(adapter);

            int pos = getIntent().getIntExtra(EXTRA_CLASS_POS, -1);
            if (pos >= 0 && listView.getCount() > pos) {
                listView.setItemChecked(pos, true);
            }


//            long[] pos = getIntent().getLongArrayExtra(CHECKED_POS);
//            if (pos !=null && pos.length >0) {
//                for (long p :pos) {
//                    listView.setItemChecked((int)p,true);
//                }
//            }

        }else {
            adapter.clear();
            adapter.addAll(contacts);
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

        long[] ids = listView.getCheckItemIds();
        ArrayList<EnrollImport> contacts = new ArrayList<>(ids.length);
        for (long id : ids) {

            Contact contact = adapter.getItem((int) id);

            EnrollImport enrollImport = new EnrollImport();
            enrollImport.id = contact.account;
            enrollImport.subtype = contact.subtype;

            contacts.add(enrollImport);
        }

        if (TextUtils.isEmpty(classId)) {

            finishComplete(contacts);
        } else {
            commitToClass(contacts);
        }

    }

    private void commitToClass(final ArrayList<EnrollImport> imports) {


        ClassEnroll classEnroll = new ClassEnroll();
        classEnroll.importe = imports;

        ClassEnrollParams params = new ClassEnrollParams();
        params.enroll = classEnroll;

        showProgress(true);
        LessonDataManager.addClassStudent(this, classId, params, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

                cancelProgress();
                Toast.makeText(ImportStudentFormClassActivity.this, R.string.add_success, Toast.LENGTH_SHORT).show();
                finishComplete(imports);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                cancelProgress();
                Toast.makeText(ImportStudentFormClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void finishComplete(ArrayList<EnrollImport> imports) {
        Intent i = new Intent();
        i.putExtra(EXTRA_IMPORTS, imports);
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
                    Logger.d("UpdateReceiver: to update private class");
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
