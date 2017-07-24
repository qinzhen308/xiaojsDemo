package cn.xiaojs.xma.ui.recordlesson;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;
import cn.xiaojs.xma.ui.recordlesson.model.RLLesson;

/**
 * Created by Paul Z on 2017/7/21.
 */

public class RLDirListActivity extends BaseActivity {


    @BindView(R.id.listview)
    ListView listview;
    RecordedLessonListAdapter adapter;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_recorded_lesson_dir_list);
        needHeader(true);
        setMiddleTitle(R.string.record_lesson_directory);
        initView();
    }


    private void initView() {
        adapter = new RecordedLessonListAdapter(this);
        listview.setAdapter(adapter);
    }


    @OnClick({R.id.left_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
        }
    }


    public static void invoke(Context context) {
        Intent intent = new Intent(context, RLDirListActivity.class);
        context.startActivity(intent);
    }


}
