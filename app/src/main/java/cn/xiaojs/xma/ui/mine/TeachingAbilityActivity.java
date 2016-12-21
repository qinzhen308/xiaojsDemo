package cn.xiaojs.xma.ui.mine;


import android.content.Intent;
import android.view.View;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;

import butterknife.OnClick;

public class TeachingAbilityActivity extends BaseActivity {

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_teaching_ability);
        setMiddleTitle(R.string.teach_ability);
        setLeftImage(R.drawable.back_arrow);
    }

    @OnClick({R.id.left_image,R.id.edit_ability_layout})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_image:
                finish();
                break;
            case R.id.edit_ability_layout:
                startActivity(new Intent(TeachingAbilityActivity.this,TeachAbilityDemoActivity.class));
                break;
        }
    }
}
