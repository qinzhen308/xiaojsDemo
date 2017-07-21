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
        testData();
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


    private void testData() {
        ArrayList<Object> list = new ArrayList<>();
        RLDirectory dir = null;
        dir = new RLDirectory("01第一个目录", "100");
        dir.addChild(new RLLesson("01第一节课--1", "101"));
        dir.addChild(new RLLesson("02第二节课--1", "102"));
        dir.addChild(new RLLesson("03第三节课--1", "103"));
        list.add(dir);
        dir = new RLDirectory("02第二个目录", "200");
        dir.addChild(new RLLesson("01第一节课--2", "201"));
        list.add(dir);
        dir = new RLDirectory("03第三个目录", "300");
        dir.addChild(new RLLesson("01第一节课---3", "301"));
        dir.addChild(new RLLesson("02第二节课---3", "302"));
        dir.addChild(new RLLesson("03第三节课---3", "303"));
        dir.addChild(new RLLesson("04第四节课---3", "304"));
        dir.addChild(new RLLesson("05第五节课---3", "305"));
        dir.addChild(new RLLesson("06第六节课---3", "306"));
        dir.addChild(new RLLesson("07第七节课---3", "307"));
        list.add(dir);
        dir=new RLDirectory("04第四个目录","400");
        dir.addChild(new RLLesson("01第一节课--4","401"));
        list.add(dir);
        dir=new RLDirectory("05第五个目录","500");
        dir.addChild(new RLLesson("01第一节课--5","501"));
        list.add(dir);
        dir=new RLDirectory("06第六个目录","600");
        dir.addChild(new RLLesson("01第一节课--6","601"));
        list.add(dir);
        dir=new RLDirectory("07第七个目录","700");
        dir.addChild(new RLLesson("01第一节课--7","701"));
        list.add(dir);
        dir=new RLDirectory("08第八个目录","800");
        dir.addChild(new RLLesson("01第一节课--8","801"));
        list.add(dir);
        dir=new RLDirectory("09第九个目录","900");
        dir.addChild(new RLLesson("01第一节课--9","901"));
        list.add(dir);
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    public static void invoke(Context context) {
        Intent intent = new Intent(context, RLDirListActivity.class);
        context.startActivity(intent);
    }


}
