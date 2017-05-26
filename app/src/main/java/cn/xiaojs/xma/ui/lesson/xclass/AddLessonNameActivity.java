package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;

/**
 * Created by maxiaobao on 2017/5/18.
 */

public class AddLessonNameActivity extends BaseActivity {

    public static final String EXTRA_NAME = "name";


    @BindView(R.id.search_input)
    EditTextDel editTextDel;
    @BindView(R.id.complete_list)
    ListView completeListView;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_add_lesson_name);
        setMiddleTitle(R.string.live_lesson_name);
        setRightText(R.string.ok);
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

        Intent i = new Intent();
        i.putExtra(EXTRA_NAME, name);

        setResult(RESULT_OK,i);

        finish();

    }


    //TODO item view :R.layout.layout_complete_single_text_item
}
