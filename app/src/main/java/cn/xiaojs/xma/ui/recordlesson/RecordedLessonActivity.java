package cn.xiaojs.xma.ui.recordlesson;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mobeta.android.dslv.DragSortListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;
import cn.xiaojs.xma.ui.recordlesson.model.RLLesson;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.ObjectUtil;

/**
 * Created by Paul Z on 2017/7/18.
 */

public class RecordedLessonActivity extends BaseActivity {


    @BindView(R.id.listview)
    DragSortListView listview;
    @BindView(R.id.next_btn)
    View nextBtn;
    RecordedLessonAdapter adapter;

    View footer;

    boolean isEditMode = false;
    @BindView(R.id.layout_edit_bar)
    LinearLayout layoutEditBar;

    ArrayList srcList;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_recorded_lesson);
        needHeader(true);
        setRightText(R.string.manage_it);
        setMiddleTitle(R.string.record_lesson_directory);
        initView();
        testData();
    }


    private void initView() {
        footer = LayoutInflater.from(this).inflate(R.layout.layout_recorded_lesson_footer, null);
        footer.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importVideo();
            }
        });
        listview.setChoiceMode(ListView.CHOICE_MODE_NONE);
        listview.setDragEnabled(false);
        adapter = new RecordedLessonAdapter(this, listview);
        listview.addFooterView(footer);
        listview.setAdapter(adapter);
        listview.setDragEnabled(true);
//        DragSortController manager=new DragSortController(mListview);
//        manager.setDragInitMode(DragSortController.ON_LONG_PRESS);
//        mListview.setFloatViewManager(manager);

//        listview.setDropListener(onDrop);
        listview.setDragSortListener(dragSortListener);
    }

    private DragSortListView.DragSortListener dragSortListener = new DragSortListView.DragSortListener() {
        @Override
        public void drag(int from, int to) {
            Logger.d("----dragsortlistview----from=" + from + ",to=" + to);
            if (from == to) {

            }
        }

        @Override
        public void drop(int from, int to) {
            Logger.d("----dragsortlistview----drop----from=" + from + ",to" + to);
            adapter.onDrop(from, to);
        }

        @Override
        public void remove(int which) {

        }

        @Override
        public void startDrag(int which) {
            Logger.d("----dragsortlistview----startDrag----which=" + which);
            adapter.onStartDrag(which);
        }
    };


    @OnClick({R.id.next_btn, R.id.right_view, R.id.left_image,
            R.id.all_check_view, R.id.btn_delete, R.id.add_dir, R.id.btn_lesson})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next_btn:
                break;
            case R.id.right_view:
                if (isEditMode) {
                    changeMode(false);
                    saveManaged();
                } else {
                    changeMode(true);
                    readyToManage();
                }
                break;
            case R.id.left_image:
                if (isEditMode) {
                    changeMode(false);
                    cancelManaged();
                }
                break;
            case R.id.all_check_view:
                break;
            case R.id.btn_delete:
                break;
            case R.id.add_dir:
                break;
            case R.id.btn_lesson:

                break;
        }
    }

    private void readyToManage() {
        if(adapter.getList()==null){
            return;
        }
        adapter.setList(ObjectUtil.deepClone((ArrayList)adapter.getList()));
        adapter.setEditMode(true);
    }

    private void saveManaged() {
        srcList=(ArrayList) adapter.getList();
        adapter.setEditMode(false);
    }

    private void cancelManaged() {
        adapter.setList(srcList);
        adapter.setEditMode(false);
    }

    @Override
    public void onBackPressed() {
        if (isEditMode) {
            changeMode(false);
            cancelManaged();
            return;
        }
        super.onBackPressed();
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
//        dir=new RLDirectory("04第二个目录","200");
//        dir.addChild(new RLLesson("01第一节课--2","201"));
//        list.add(dir);
//        dir=new RLDirectory("05第二个目录","200");
//        dir.addChild(new RLLesson("01第一节课--2","201"));
//        list.add(dir);
//        dir=new RLDirectory("05第二个目录","200");
//        dir.addChild(new RLLesson("01第一节课--2","201"));
//        list.add(dir);
//        dir=new RLDirectory("06第二个目录","200");
//        dir.addChild(new RLLesson("01第一节课--2","201"));
//        list.add(dir);
//        dir=new RLDirectory("07第二个目录","200");
//        dir.addChild(new RLLesson("01第一节课--2","201"));
//        list.add(dir);
//        dir=new RLDirectory("08第二个目录","200");
//        dir.addChild(new RLLesson("01第一节课--2","201"));
//        list.add(dir);
//        dir=new RLDirectory("09第二个目录","200");
//        dir.addChild(new RLLesson("01第一节课--2","201"));
//        list.add(dir);
        srcList=list;
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }


    private void changeMode(boolean isEditMode) {
        if (this.isEditMode == isEditMode) return;
        this.isEditMode = isEditMode;
        if (isEditMode) {
            setRightText(R.string.finish);
            listview.removeFooterView(footer);
            nextBtn.setVisibility(View.GONE);
            layoutEditBar.setVisibility(View.VISIBLE);
            listview.setDragEnabled(true);
        } else {
            setRightText(R.string.manage_it);
            listview.addFooterView(footer);
            nextBtn.setVisibility(View.VISIBLE);
            layoutEditBar.setVisibility(View.GONE);
            listview.setDragEnabled(false);
        }
    }

    private void importVideo() {

    }

    public static void invoke(Context context) {
        Intent intent = new Intent(context, RecordedLessonActivity.class);
        context.startActivity(intent);
    }



}
