package cn.xiaojs.xma.ui.recordlesson;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ImportStudentFormClassActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;

/**
 * Created by maxiaobao on 2017/7/24.
 */

public class ManualRegistrationActivity extends BaseActivity {

    @BindView(R.id.tips_content)
    TextView tipsContentView;
    @BindView(R.id.lay_tips)
    LinearLayout tipsRootView;

    @BindView(R.id.phone_num)
    EditTextDel phoneView;
    @BindView(R.id.name)
    EditTextDel nameView;
    @BindView(R.id.remark)
    EditTextDel remarkView;
    @BindView(R.id.limit_count)
    TextView limltCountView;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_manual_registration);
        setMiddleTitle(R.string.man_register);
        setRightText(R.string.import_from_class_or_lesson);
        tipsContentView.setText(R.string.phone_usage_tips);
    }

    @OnClick({R.id.left_image,R.id.right_image2,R.id.complete_btn, R.id.lesson_creation_tips_close})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:                  //返回
                finish();
                break;
            case R.id.right_image2:                //从班／课中导入
                Intent i = new Intent(this, ImportStudentFormClassActivity.class);
                startActivity(i);
                break;
            case R.id.lesson_creation_tips_close:  //关闭提醒
                closeCourCreateTips();
                break;
            case R.id.complete_btn:                //完成
                break;
        }
    }

    @OnTextChanged(value = R.id.remark, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(CharSequence text) {
        limltCountView.setText(text.length()+ "/100");
    }



    private void closeCourCreateTips() {
        tipsRootView.setVisibility(View.GONE);
    }


}
