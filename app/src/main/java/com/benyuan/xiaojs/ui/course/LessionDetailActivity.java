package com.benyuan.xiaojs.ui.course;


import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.widget.BottomSheet;

import butterknife.OnClick;

public class LessionDetailActivity extends BaseActivity {


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_lession_detail);
        setMiddleTitle(R.string.lession_detail);
        setLeftImage(R.drawable.back_arrow);


    }

    @OnClick({R.id.left_image, R.id.apply_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.apply_btn:
                showApplyDlg();
                break;

        }
    }


    private void showApplyDlg() {
        BottomSheet bottomSheet = new BottomSheet(this);
        bottomSheet.setContent(R.layout.layout_apply_lession_dlg);
        bottomSheet.show();

    }




}
