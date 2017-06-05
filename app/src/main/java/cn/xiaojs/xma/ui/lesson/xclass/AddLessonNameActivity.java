package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CLResponse;
import cn.xiaojs.xma.model.ctl.ModifyClassParams;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;

/**
 * Created by maxiaobao on 2017/5/18.
 */

public class AddLessonNameActivity extends BaseActivity {

    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_ROLE = "role";
    public static final String EXTRA_CLASSID = "classid";

    public static final int ROLE_LESSON = 0x1;
    public static final int ROLE_CLASS = 0x2;


    @BindView(R.id.search_input)
    EditTextDel editTextDel;
    @BindView(R.id.complete_list)
    ListView completeListView;

    private int currentRole;
    private String orginName;
    private String classId;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_add_lesson_name);

        currentRole = getIntent().getIntExtra(EXTRA_ROLE, ROLE_LESSON);
        orginName = getIntent().getStringExtra(EXTRA_NAME);

        classId = getIntent().getStringExtra(EXTRA_CLASSID);

        if (currentRole == ROLE_CLASS) {
            setMiddleTitle(R.string.live_class_name);
            completeListView.setVisibility(View.GONE);
        }else {
            setMiddleTitle(R.string.live_lesson_name);
        }

        setRightText(R.string.ok);

        if (!TextUtils.isEmpty(orginName)) {
            editTextDel.setText(orginName);
        }
    }

    @OnClick({R.id.left_image, R.id.right_image2})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image2://确定
                complete();
                break;
        }
    }

    private void complete() {

        String name = editTextDel.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.live_lesson_name_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.length() > CreateClassActivity.MAX_CLASS_CHAR) {
            String nameEr = this.getString(R.string.live_lesson_name_error, CreateClassActivity.MAX_CLASS_CHAR);
            Toast.makeText(this, nameEr, Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentRole == ROLE_CLASS && !name.equals(orginName)) {
            modifyClassName(name);
        }else {
           setResultAndFinsih(name);
        }



    }

    private void modifyClassName(final String newName) {

        ModifyClassParams classParams = new ModifyClassParams();

        classParams.className = newName;

        showProgress(true);
        LessonDataManager.modifyClass(this, classId, classParams, new APIServiceCallback<CLResponse>() {
            @Override
            public void onSuccess(CLResponse object) {
                cancelProgress();
                Toast.makeText(AddLessonNameActivity.this,
                        R.string.lesson_edit_success,
                        Toast.LENGTH_SHORT)
                        .show();

                setResultAndFinsih(newName);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(AddLessonNameActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setResultAndFinsih(String name) {

        if (!name.equals(orginName)) {
            Intent i = new Intent();
            i.putExtra(EXTRA_NAME, name);

            setResult(RESULT_OK,i);
        }
        finish();
    }


    //TODO item view :R.layout.layout_complete_single_text_item
}
