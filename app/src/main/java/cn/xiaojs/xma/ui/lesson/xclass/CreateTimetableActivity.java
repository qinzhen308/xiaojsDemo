package cn.xiaojs.xma.ui.lesson.xclass;

import android.view.View;

import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;

/**
 * Created by maxiaobao on 2017/5/17.
 */

public class CreateTimetableActivity extends BaseActivity {

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_create_timetable);
        setMiddleTitle(R.string.time_table);
        setRightText(R.string.finish);

        initView();
    }

    @OnClick({R.id.left_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
        }
    }


    private void initView(){

    }
}
