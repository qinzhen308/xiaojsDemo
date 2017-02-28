package cn.xiaojs.xma.ui.mine;


import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.CompetencyParams;
import cn.xiaojs.xma.model.account.CompetencySubject;
import cn.xiaojs.xma.ui.base.BaseActivity;

import butterknife.OnClick;
import cn.xiaojs.xma.ui.lesson.TeachingSubjectActivity;

public class TeachingAbilityActivity extends BaseActivity {
    private final static int REQUEST_TEACHING_ABILITY = 100;
    public final static String KEY_SUBJECT = "key_subject";

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_teaching_ability);
        setMiddleTitle(R.string.teach_ability);
        setLeftImage(R.drawable.back_arrow);
    }

    @OnClick({R.id.left_image, R.id.edit_ability_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.edit_ability_layout:
                //startActivity(new Intent(TeachingAbilityActivity.this,TeachAbilityDemoActivity.class));
                Intent intent = new Intent(this, TeachingSubjectActivity.class);
                startActivityForResult(intent, REQUEST_TEACHING_ABILITY);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TEACHING_ABILITY && data != null) {
            CSubject subject = (CSubject) data.getSerializableExtra(KEY_SUBJECT);
            if (subject != null) {
                CompetencyParams params = new CompetencyParams();
                params.setSubject(subject.getId());
                AccountDataManager.requestClaimCompetency(this, params, new APIServiceCallback<CompetencySubject>() {
                    @Override
                    public void onSuccess(CompetencySubject object) {
                        Toast.makeText(TeachingAbilityActivity.this, R.string.claim_succ, Toast.LENGTH_SHORT).show();
                        SecurityManager.updatePermission(TeachingAbilityActivity.this, Su.Permission.COURSE_OPEN_CREATE, true);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        Toast.makeText(TeachingAbilityActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
