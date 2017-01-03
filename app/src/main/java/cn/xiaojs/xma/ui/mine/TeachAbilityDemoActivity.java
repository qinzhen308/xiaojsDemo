package cn.xiaojs.xma.ui.mine;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.CategoriesManager;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.ClaimCompetency;
import cn.xiaojs.xma.model.CompetencyParams;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.UIUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class TeachAbilityDemoActivity extends BaseActivity {

    @BindView(R.id.btn_to_claim)
    Button claimBtn;

    @BindView(R.id.btn_get_subject)
    Button subBtn;


    private String subId;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_teach_ability_demo);
        setMiddleTitle(R.string.teach_ability);
        setLeftImage(R.drawable.back_arrow);

        claimBtn.setVisibility(View.GONE);
        subBtn.setText("正在获取教学能力。。。");
        subBtn.setEnabled(false);

        getSubjects();

    }

    @OnClick({R.id.left_image,R.id.btn_to_claim,R.id.btn_get_subject})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_image:
                finish();
                break;
            case R.id.btn_to_claim:
                toClaim();
                break;
            case R.id.btn_get_subject:
                getSubjects();
                break;

        }
    }

    private void getSubjects() {

        CategoriesManager.requestGetSubject(this, new APIServiceCallback<CSubject>() {
            @Override
            public void onSuccess(CSubject object) {

                if (UIUtils.activityDestoryed(TeachAbilityDemoActivity.this)) {
                    return;
                }


                if(object ==null){

                    Toast.makeText(TeachAbilityDemoActivity.this,"没有获取到教学能力",Toast.LENGTH_SHORT).show();

                }else{
                    String subName = object.getName();
                    claimBtn.setText(subName);

                    subId = object.getId();

                    claimBtn.setVisibility(View.VISIBLE);

                    AccountDataManager.saveSubject(TeachAbilityDemoActivity.this,object.getId());


                }

                subBtn.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                if (UIUtils.activityDestoryed(TeachAbilityDemoActivity.this)) {
                    return;
                }

                Toast.makeText(TeachAbilityDemoActivity.this,errorMessage,Toast.LENGTH_SHORT).show();

                subBtn.setText("重新获取");

                subBtn.setEnabled(true);


            }
        });

    }


    private void toClaim() {


        CompetencyParams cp = new CompetencyParams();
        cp.setSubject(subId);
        AccountDataManager.requestClaimCompetency(this, cp, new APIServiceCallback<ClaimCompetency>() {
            @Override
            public void onSuccess(ClaimCompetency object) {
                Toast.makeText(TeachAbilityDemoActivity.this,"声明成功",Toast.LENGTH_SHORT).show();
                SecurityManager.updatePermission(TeachAbilityDemoActivity.this, Su.Permission.COURSE_OPEN_CREATE,true);

                AccountDataManager.saveSubject(TeachAbilityDemoActivity.this,object.getCompetency().getSubject());

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Toast.makeText(TeachAbilityDemoActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
            }
        });

    }



}
