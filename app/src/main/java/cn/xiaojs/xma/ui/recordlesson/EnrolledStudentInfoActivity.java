package cn.xiaojs.xma.ui.recordlesson;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;

/**
 * Created by maxiaobao on 2017/7/24.
 */

public class EnrolledStudentInfoActivity extends BaseActivity {

    @BindView(R.id.phone_num)
    TextView phoneView;
    @BindView(R.id.name)
    TextView nameView;
    @BindView(R.id.remark)
    TextView remarkView;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_enrolled_student_info);
        setMiddleTitle(R.string.enroll_register_stu);
        init();
    }

    @OnClick({R.id.left_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
        }
    }

    private void init() {
        // TODO
    }
}
