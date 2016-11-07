package com.benyuan.xiaojs.ui.course;

import android.support.design.widget.TabLayout;

import android.view.View;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.widget.BottomSheet;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.OnClick;

public class LessionDetailActivity extends BaseActivity {

    @BindView(R.id.lession_tab_detail)
    TabLayout tabLayout;

    @BindView(R.id.tab_detail_text)
    TextView tabDetailView;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_lession_detail);
        setMiddleTitle(R.string.lession_detail);
        setLeftImage(R.drawable.back_arrow);

        tabDetailView.setText("站在海拔1300多米的云彩山顶峰，向东俯瞰，如绿海似翡翠的万亩松林，把山体覆盖得严严实实，走进林中一不小心就迷路，要是没有老护林员指点迷津，肯定走不出松林。松林间的阡陌小路,是山民穿松枝，采松子踏出来的。");


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();

                if (pos == 0) {
                    tabDetailView.setText("站在海拔1300多米的云彩山顶峰，向东俯瞰，如绿海似翡翠的万亩松林，把山体覆盖得严严实实，走进林中一不小心就迷路，要是没有老护林员指点迷津，肯定走不出松林。松林间的阡陌小路,是山民穿松枝，采松子踏出来的。山脚下阴坡脸儿上的幼松");
                } else {

                    tabDetailView.setText("小村的万亩松林啊，如天地之精灵，傲霜斗雪，搏击风雨，不屈不挠，那是小村父老乡亲坚强意志的象征，更是父老乡亲对美丽生活充满无限信念的不竭动力。");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
