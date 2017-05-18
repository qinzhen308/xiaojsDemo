package cn.xiaojs.xma.ui.lesson.xclass;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;

/**
 * Created by maxiaobao on 2017/5/18.
 */

public class ManualAddStudentActivity extends BaseActivity {

    @BindView(R.id.tips_content)
    TextView tipsContentView;
    @BindView(R.id.lay_tips)
    LinearLayout tipsRootView;

    @BindView(R.id.add_btn)
    Button addButton;
    @BindView(R.id.student_num)
    EditText numEdit;
    @BindView(R.id.divider)
    View lineView;
    @BindView(R.id.student_name)
    EditText nameEdit;


    @BindView(R.id.student_list)
    ListView studentsListView;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_manual_add_student);
        setMiddleTitle(getString(R.string.manual_add_student));
        setRightText(R.string.ok);
        initView();
    }

    @OnClick({R.id.left_image, R.id.right_image2,R.id.add_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image2://确定
                break;
            case R.id.add_btn://添加
                break;

        }
    }

    private void initView() {
        tipsContentView.setText(R.string.add_student_tips);
        showEditStatus();
    }

    private void showEditStatus() {

        addButton.setVisibility(View.GONE);

        numEdit.setVisibility(View.VISIBLE);
        nameEdit.setVisibility(View.VISIBLE);
        lineView.setVisibility(View.VISIBLE);

        studentsListView.setVisibility(View.GONE);
    }

    private void showAddStatus() {

        addButton.setVisibility(View.VISIBLE);

        numEdit.setVisibility(View.GONE);
        nameEdit.setVisibility(View.GONE);
        lineView.setVisibility(View.GONE);

        studentsListView.setVisibility(View.VISIBLE);
    }


    //TODO adapter item view : R.layout.layout_student_name_num_item
}
