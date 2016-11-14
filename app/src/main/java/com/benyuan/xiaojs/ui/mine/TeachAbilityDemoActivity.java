package com.benyuan.xiaojs.ui.mine;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.data.AccountDataManager;
import com.benyuan.xiaojs.data.CategoriesDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.ClaimCompetency;
import com.benyuan.xiaojs.model.CompetencyParams;
import com.benyuan.xiaojs.model.GetSubjectResponse;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.base.BaseBusiness;

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

        CategoriesDataManager.requestGetSubject(this, new APIServiceCallback<GetSubjectResponse>() {
            @Override
            public void onSuccess(GetSubjectResponse object) {

                if(object ==null){

                    Toast.makeText(TeachAbilityDemoActivity.this,"没有获取到教学能力",Toast.LENGTH_SHORT).show();

                }else{
                    String subName = object.getName();
                    claimBtn.setText(subName);

                    subId = object.getId();

                    claimBtn.setVisibility(View.VISIBLE);


                }

                subBtn.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                Toast.makeText(TeachAbilityDemoActivity.this,errorMessage,Toast.LENGTH_SHORT).show();

                subBtn.setText("重新获取");

                subBtn.setEnabled(true);


            }
        });

    }


    private void toClaim() {


        CompetencyParams cp = new CompetencyParams();
        cp.setSubject(subId);
        AccountDataManager.requestClaimCompetency(this, BaseBusiness.getSession(), cp, new APIServiceCallback<ClaimCompetency>() {
            @Override
            public void onSuccess(ClaimCompetency object) {
                Toast.makeText(TeachAbilityDemoActivity.this,"声明成功",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Toast.makeText(TeachAbilityDemoActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
            }
        });

    }



}
