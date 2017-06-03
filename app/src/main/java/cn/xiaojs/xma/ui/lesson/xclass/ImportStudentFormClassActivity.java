package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.loader.DataLoder;
import cn.xiaojs.xma.model.ctl.EnrollImport;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.message.ChoiceContactActivity;

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

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_share_scope);

        setMiddleTitle(R.string.student_add_from_exist_class);
        tipsContentView.setText(R.string.add_student_tips);
        tipsRootView.setVisibility(View.VISIBLE);


        setRightText(R.string.finish);
        setRightTextColor(getResources().getColor(R.color.font_orange));
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        getData();

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

        }


    }

    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
    }


    private void getData() {
        DataManager.getPrivateClasses(this,
                new DataLoder.DataLoaderCallback<ArrayList<ContactGroup>>() {

                    @Override
                    public void loadCompleted(ArrayList<ContactGroup> contacts) {
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
            long[] ids = listView.getCheckItemIds();
            ArrayList<EnrollImport> contacts = new ArrayList<>(ids.length);
            for(long id: ids) {

                Contact contact = adapter.getItem((int)id);

                EnrollImport enrollImport = new EnrollImport();
                enrollImport.id = contact.account;
                enrollImport.subtype = contact.subtype;

                contacts.add(enrollImport);
            }

            i.putExtra(EXTRA_IMPORTS, contacts);
            setResult(RESULT_OK, i);

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
}
