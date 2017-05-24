package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.message.ChooseClassActivity;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;

/**
 * Created by maxiaobao on 2017/5/21.
 */

public class StudentsListActivity extends BaseActivity {

    @BindView(R.id.student_list)
    PullToRefreshSwipeListView listView;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_students_list);
        setMiddleTitle(getString(R.string.student));
        setRightText(R.string.add);
    }

    @OnClick({R.id.left_image, R.id.right_image2, R.id.lay_veri})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image2:
                // 添加
                showAddDlg();
                break;
            case R.id.lay_veri:
                //TODO 进入验证消息页面
                break;

        }
    }

    private void showAddDlg() {

        ListBottomDialog dialog = new ListBottomDialog(this);

        String[] items = getResources().getStringArray(R.array.add_student);
        dialog.setItems(items);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0://手动添加
                        startActivity(new Intent(StudentsListActivity.this,
                                ManualAddStudentActivity.class));
                        break;
                    case 1://从已有班级中添加

                        Intent i = new Intent(StudentsListActivity.this,
                                ChooseClassActivity.class);
                        i.putExtra(ChooseClassActivity.EXTRA_ENTER_TYPE,
                                ChooseClassActivity.ENTER_TYPE_ADD_STUDENT);

                        startActivity(i);
                        break;
                }

            }

        });
        dialog.show();

    }

    //TODO adapter item view : R.layout.layout_student_name_num_item
}
