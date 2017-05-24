package cn.xiaojs.xma.ui.lesson.xclass;

import android.view.View;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;

/**
 * Created by maxiaobao on 2017/5/18.
 */

public class AddLessonNameActivity extends BaseActivity {


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
                break;
        }
    }

    //TODO item view :R.layout.layout_complete_single_text_item
}
