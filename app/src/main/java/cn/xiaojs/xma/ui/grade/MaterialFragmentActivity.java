package cn.xiaojs.xma.ui.grade;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2017/8/1.
 */

public class MaterialFragmentActivity extends FragmentActivity{

    public static final String EXTRA_FROM = "efrom";
    public static final int FROM_CHOICE_EMPTY = 1;


    @BindView(R.id.left_image)
    ImageView backBtn;
    @BindView(R.id.upload_btn)
    ImageView uploadBtn;
    @BindView(R.id.mode_btn)
    ImageView modeBtn;
    @BindView(R.id.cancel_btn)
    Button cancelBtn;
    @BindView(R.id.choice_btn)
    Button choiceBtn;

    MaterialFragment  materialFragment;
    private int from;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_material);
        ButterKnife.bind(this);

        from = getIntent().getIntExtra(EXTRA_FROM, -1);

        materialFragment = (MaterialFragment) getSupportFragmentManager().findFragmentById(R.id.material_fragment);
        uploadBtn.setImageResource(R.drawable.upload_selector);
        modeBtn.setImageResource(R.drawable.ic_datasection_selector);
    }

    @OnClick({R.id.left_image,R.id.cancel_btn,
            R.id.upload_btn, R.id.mode_btn, R.id.choice_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:             //返回
                finish();
                break;
            case R.id.upload_btn:             //上传文件
                materialFragment.upload();
                break;
            case R.id.mode_btn:               //进入选择模式
                changeChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                break;
            case R.id.cancel_btn:             //取消
                changeChoiceMode(ListView.CHOICE_MODE_NONE);
                break;
            case R.id.choice_btn:             //全选OR取消全选
                changeChoiceStatus();
                break;
        }
    }

    @Override
    public void finish() {

        if (from == FROM_CHOICE_EMPTY) {
            resultRefresh();
        }

        super.finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void switchOperaBtn(@IdRes int tabId) {
        if (tabId == R.id.tab_material) {
            modeBtn.setVisibility(View.VISIBLE);
            uploadBtn.setVisibility(View.VISIBLE);

        }else {

            changeChoiceMode(ListView.CHOICE_MODE_NONE);

            modeBtn.setVisibility(View.GONE);
            uploadBtn.setVisibility(View.GONE);
        }

        cancelBtn.setVisibility(View.GONE);
        choiceBtn.setVisibility(View.GONE);
    }

    private void changeChoiceStatus() {

        if (materialFragment.cancelChoiceAll()) {
            choiceBtn.setText(R.string.choice_all);
        } else {
            choiceBtn.setText(R.string.cancel_choice_all);
        }

        materialFragment.changeChoiceStatus();

    }

    private void changeChoiceMode(int choiceMode) {
        choiceBtn.setText(R.string.choice_all);
        if (choiceMode == ListView.CHOICE_MODE_MULTIPLE) {

            backBtn.setVisibility(View.GONE);
            uploadBtn.setVisibility(View.GONE);
            modeBtn.setVisibility(View.GONE);

            choiceBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
        } else {

            backBtn.setVisibility(View.VISIBLE);
            uploadBtn.setVisibility(View.VISIBLE);
            modeBtn.setVisibility(View.VISIBLE);

            choiceBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
        }

        materialFragment.changeChoiceMode(choiceMode);
    }

    private void resultRefresh() {
        MaterialAdapter materialAdapter = materialFragment.mAdapter;
        if(materialAdapter !=null
                && materialAdapter.getList() != null
                && materialAdapter.getList().size() > 0) {
            setResult(RESULT_OK);
        }
    }
}
