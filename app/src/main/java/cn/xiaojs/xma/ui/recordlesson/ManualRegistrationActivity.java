package cn.xiaojs.xma.ui.recordlesson;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.SearchManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionResult;
import cn.xiaojs.xma.model.search.SearchResultV2;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ImportStudentFormClassActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.VerifyUtils;

import static cn.xiaojs.xma.R.id.text;

/**
 * Created by maxiaobao on 2017/7/24.
 */

public class ManualRegistrationActivity extends BaseActivity {

    @BindView(R.id.tips_content)
    TextView tipsContentView;
    @BindView(R.id.lay_tips)
    LinearLayout tipsRootView;

    @BindView(R.id.phone_num)
    EditTextDel phoneView;
    @BindView(R.id.name)
    EditTextDel nameView;
    @BindView(R.id.remark)
    EditTextDel remarkView;
    @BindView(R.id.limit_count)
    TextView limltCountView;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_manual_registration);
        setMiddleTitle(R.string.man_register);
        setRightText(R.string.import_from_class_or_lesson);
        tipsContentView.setText(R.string.phone_usage_tips);
    }

    @OnClick({R.id.left_image,R.id.right_image2,R.id.complete_btn, R.id.lesson_creation_tips_close})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:                  //返回
                finish();
                break;
            case R.id.right_image2:                //从班／课中导入
                Intent i = new Intent(this, ImportStudentFormClassActivity.class);
                startActivity(i);
                break;
            case R.id.lesson_creation_tips_close:  //关闭提醒
                closeCourCreateTips();
                break;
            case R.id.complete_btn:                //完成
                break;
        }
    }

    @OnTextChanged(value = R.id.remark, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void remarkTextChanged(Editable editable) {
        limltCountView.setText(editable.length()+ "/100");
    }

    @OnTextChanged(value = R.id.phone_num, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void phoneTextChanged(Editable editable) {

        String phone = editable.toString().trim();

        if (phone.length() ==11 && VerifyUtils.checkPhoneNum(phone)) {
            searchAccountInfo(phone);
        }

    }



    private void closeCourCreateTips() {
        tipsRootView.setVisibility(View.GONE);
    }


    private void searchAccountInfo(String keyWord) {
        showProgress(true);
        SearchManager.search(this,
                Social.SearchType.PERSON,
                keyWord,
                1,
                10,
                new APIServiceCallback<CollectionResult<SearchResultV2>>() {
                    @Override
                    public void onSuccess(CollectionResult<SearchResultV2> result) {
                        if (result != null && result.results != null && result.results.size()>0) {

                            SearchResultV2 searchResultV2 = result.results.get(0);
                            //if (searchResultV2 != null )

                        }
                        cancelProgress();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        cancelProgress();
                    }
                });
    }

}
