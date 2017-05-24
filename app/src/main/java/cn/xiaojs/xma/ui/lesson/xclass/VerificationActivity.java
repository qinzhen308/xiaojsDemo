package cn.xiaojs.xma.ui.lesson.xclass;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;

/**
 * Created by maxiaobao on 2017/5/18.
 */

public class VerificationActivity extends BaseActivity {

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_verification_add_class_msg);
        setMiddleTitle(getString(R.string.add_class_verification_msg));

    }

    //TODO adapter item  layout res: R.layout.layout_verification_class_msg_item
}
