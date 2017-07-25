package cn.xiaojs.xma.ui.recordlesson;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;

import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.grade.ImportVideoActivity;

/**
 * Created by maxiaobao on 2017/5/17.
 */

public class CreateRecordlessonActivity extends BaseActivity {


    @BindView(R.id.tips_content)
    TextView tipsContentView;
    @BindView(R.id.lay_tips)
    LinearLayout tipsRootView;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_create_recordlesson);
        setMiddleTitle(getString(R.string.create_recordlesson));

        initView();
    }


    @OnClick({R.id.left_image, R.id.btn_add, R.id.lesson_creation_tips_close})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.btn_add: //导入视频
                Intent i = new Intent(this, ImportVideoActivity.class);
                startActivityForResult(i,ImportVideoActivity.REQUEST_CODE);
                break;
            case R.id.lesson_creation_tips_close://关闭提醒
                closeCourCreateTips();
                break;
        }
    }

    private void initView() {
        tipsContentView.setText(R.string.create_record_lesson_tips);
    }


    private void closeCourCreateTips() {
        tipsRootView.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK==resultCode){
            if(requestCode==ImportVideoActivity.REQUEST_CODE){
                RecordedLessonActivity.invoke(this,data);
                finish();
            }
        }
    }
}

