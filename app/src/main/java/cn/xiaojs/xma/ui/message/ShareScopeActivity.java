package cn.xiaojs.xma.ui.message;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.model.Doc;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.DynPost;
import cn.xiaojs.xma.ui.base.BaseActivity;

public class ShareScopeActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    public static final int REQUEST_CHOOSE_CONTACT_CODE = 0x1;
    public static final int REQUEST_CHOOSE_CLASS_CODE = 0x2;

    public static final String CHOOSE_INDEX = "cindex";
    public static final String CHOOSE_DATA = "cdata";
    public static final String CHOOSE_C = "ccontact";


    @BindView(R.id.lv)
    ListView listView;

    private ArrayAdapter<String> adapter;
    private ArrayList<Doc> docs;
    private ArrayList<Contact> choiceContacts;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_share_scope);
        setMiddleTitle(R.string.who_can_see_me);
        setRightText(R.string.finish);
        setRightTextColor(getResources().getColor(R.color.font_orange));

        initView();
    }

    @OnClick({R.id.left_image, R.id.right_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image:
                chooseOver();
                break;
        }

    }


    private void initView() {

        String[] shareScopes = getResources().getStringArray(R.array.share_scope);

        adapter = new ArrayAdapter<>(this,
                R.layout.layout_single_select_item,
                R.id.title,
                shareScopes);

        listView.setBackgroundColor(getResources().getColor(R.color.white));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        //set public scope defaut checked
        listView.setItemChecked(getIntent().getIntExtra(CHOOSE_INDEX,0), true);


    }

    private void chooseOver() {

        int checkedPos = listView.getCheckedItemPosition();

        DynPost.Audience audience = new DynPost.Audience();
        audience.type = getScopeId(checkedPos);

        if (checkedPos == 2 || checkedPos == 3) {
            audience.chosen = docs;
        }

        Intent data = new Intent();
        data.putExtra(CHOOSE_DATA, audience);
        data.putExtra(CHOOSE_INDEX, checkedPos);
        data.putExtra(CHOOSE_C,choiceContacts);
        setResult(RESULT_OK, data);

        finish();


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (position == 2) {

            // TODO 班级圈
            startActivity(new Intent(this, ChooseClassActivity.class));
        } else if (position == 3) {

            //FIXME 如果用户再次进入选择联系人，需要将之前以选择的联系人删除清零。

            startActivityForResult(new Intent(this, ChoiceContactActivity.class),
                    REQUEST_CHOOSE_CONTACT_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSE_CONTACT_CODE) {
            if (resultCode == RESULT_OK) {
                choiceContacts = (ArrayList<Contact>) data.getSerializableExtra(
                        ChoiceContactActivity.CHOOSE_CONTACT_EXTRA);

                if (choiceContacts != null && choiceContacts.size()>0) {

                    if (docs == null) {
                        docs = new ArrayList<>(choiceContacts.size());
                    }else{
                        docs.clear();
                    }

                    for (Contact contact : choiceContacts) {
                        Doc doc = new Doc();
                        doc.id = contact.account;
                        doc.subtype = contact.subtype;
                        docs.add(doc);
                    }
                }

            }
        } else if (requestCode == REQUEST_CHOOSE_CLASS_CODE) {
            //TODO class
        }
    }

    private int getScopeId(int checkedPos) {
        switch (checkedPos) {
            case 1:
                return Social.ShareScope.FRIENDS;
            case 2:
                return Social.ShareScope.CLASSES;
            case 3:
                return Social.ShareScope.SPECIFIC;
            case 4:
                return Social.ShareScope.PRIVATE;
            default:
                return Social.ShareScope.PUBLIC;
        }
    }

}
