package cn.xiaojs.xma.ui.recordlesson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.model.recordedlesson.RLChapter;
import cn.xiaojs.xma.model.recordedlesson.Section;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;
import cn.xiaojs.xma.ui.recordlesson.model.RLLesson;
import cn.xiaojs.xma.util.MaterialUtil;

/**
 * Created by Paul Z on 2017/7/21.
 * 课程目录
 */

public class RLDirListActivity extends BaseActivity {

    public static final String EXTRA_LESSON_ID="extra_lesson_id";

    @BindView(R.id.listview)
    ListView listview;
    RecordedLessonListAdapter adapter;
    String lessonId;


    @Override
    protected void addViewContent() {
        lessonId=getIntent().getStringExtra(EXTRA_LESSON_ID);
        addView(R.layout.activity_recorded_lesson_dir_list);
        needHeader(true);
        setMiddleTitle(R.string.record_lesson_directory);
        initView();
        loadChapters();
    }


    private void initView() {
        adapter = new RecordedLessonListAdapter(this);
        adapter.setEventCallback(new EventCallback() {
            @Override
            public void onEvent(int what, Object... object) {
                if(what==EVENT_1){
                    MaterialUtil.openMaterial(RLDirListActivity.this,(LibDoc) object[0]);
                }
            }
        });
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

    private void loadChapters(){
        LessonDataManager.getRecordedCourseChapters(this, lessonId, "all", new APIServiceCallback<ArrayList<Section>>() {
            @Override
            public void onSuccess(ArrayList<Section> object) {
                adapter.setList(object);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }


    public static void invoke(Context context,String lessonId) {
        Intent intent = new Intent(context, RLDirListActivity.class);
        intent.putExtra(EXTRA_LESSON_ID,lessonId);
        context.startActivity(intent);
    }


}
