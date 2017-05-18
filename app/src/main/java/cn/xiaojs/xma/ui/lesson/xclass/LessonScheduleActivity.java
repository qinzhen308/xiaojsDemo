package cn.xiaojs.xma.ui.lesson.xclass;

import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.base.BaseActivity;

/**
 * Created by Paul Z on 2017/5/18.
 */

public class LessonScheduleActivity extends BaseActivity{

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

                break;
            case R.id.left_view:
                finish();
                break;
            default:
                break;
        }
    }


}
