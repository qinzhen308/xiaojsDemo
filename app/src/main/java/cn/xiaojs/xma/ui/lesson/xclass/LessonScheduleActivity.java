package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;

/**
 * Created by Paul Z on 2017/5/18.
 */

public class LessonScheduleActivity extends BaseActivity{

    public final int REQUEST_NEW_LESSON_CODE = 0x1;


    @BindView(R.id.listview)
    PullToRefreshSwipeListView mListView;
    LessonScheduleAdapter mAdapter;



    @Override
    protected void addViewContent() {
        needHeader(true);
        setRightText(R.string.add);
        setMiddleTitle(R.string.schedule);
        addView(R.layout.activity_lesson_schedule);
        mAdapter=new LessonScheduleAdapter(this,mListView);
        mListView.setAdapter(mAdapter);
    }

    @OnClick({R.id.right_view})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.right_view:
                startActivityForResult(new Intent(this, CreateTimetableActivity.class),
                        REQUEST_NEW_LESSON_CODE);
                break;
            case R.id.left_view:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_NEW_LESSON_CODE:
                    //新添加的课
                    ClassLesson classLesson =
                            (ClassLesson) data.getSerializableExtra(CreateTimetableActivity.EXTRA_CLASS_LESSON);
                    CreateClassActivity.addClassLesson(classLesson);
                    //TODO 更新显示
                    break;
            }
        }
    }
}
