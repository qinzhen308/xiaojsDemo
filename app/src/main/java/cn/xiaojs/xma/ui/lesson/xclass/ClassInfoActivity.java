package cn.xiaojs.xma.ui.lesson.xclass;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;

/**
 * Created by maxiaobao on 2017/5/21.
 */

public class ClassInfoActivity extends BaseActivity {



    @Override
    protected void addViewContent() {
        addView(R.layout.activity_class_info);
        setMiddleTitle(getString(R.string.class_info));
    }

    @OnClick({R.id.left_image, R.id.enter_btn,
            R.id.lay_time_table, R.id.lay_material,
            R.id.lay_student, R.id.lay_qrcode})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.enter_btn:
                //TODO 进入教室
                break;
            case R.id.lay_time_table:
                //TODO 课表
                break;
            case R.id.lay_material:
                //TODO 资料库
                break;
            case R.id.lay_student:
                //TODO 学生
                break;
            case R.id.lay_qrcode:
                //TODO 二维码
                break;
        }


    }
}
