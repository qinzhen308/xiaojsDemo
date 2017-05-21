package cn.xiaojs.xma.ui.lesson.xclass;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;

/**
 * Created by maxiaobao on 2017/5/19.
 */

public class ShareQrcodeActivity extends BaseActivity {

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_share_qrcode);
        setMiddleTitle(getString(R.string.qr_code));
    }
}
