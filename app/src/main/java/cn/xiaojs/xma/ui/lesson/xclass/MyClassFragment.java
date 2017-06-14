package cn.xiaojs.xma.ui.lesson.xclass;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.lesson.xclass.util.ClassFilterHelper;
import cn.xiaojs.xma.ui.lesson.xclass.view.MyClassFilterDialog;

/**
 * Created by maxiaobao on 2017/5/22.
 */

public class MyClassFragment extends Fragment {

    @BindView(R.id.class_lesson_list)
    PullToRefreshSwipeListView listView;

    @BindView(R.id.my_course_search)
    TextView searchView;
    @BindView(R.id.filter_line)
    View mFilterLine;
    @BindView(R.id.course_filter)
    TextView mFilter;


    private Context context;

    protected int timePosition;
    protected int statePosition;
    protected int sourcePosition;

    private ClassAdapter classAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        context = getActivity();
        View v = LayoutInflater.from(context).inflate(R.layout.fragment_class, null);
        ButterKnife.bind(this, v);
        searchView.setHint(R.string.hint_input_class_name);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        classAdapter = new ClassAdapter(context, listView);
        classAdapter.setDesc(getString(R.string.empty_class_tips));
        classAdapter.setIcon(R.drawable.ic_teach_empty);
        classAdapter.setButtonDesc(getString(R.string.want_to_create_class));

        listView.setAdapter(classAdapter);
    }

    @OnClick({R.id.course_filter,R.id.my_course_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.course_filter:
                filter();
                break;
            case R.id.my_course_search:
                startActivity(new Intent(getActivity(),SearchClassActivity.class));
                break;

        }
    }


    private void filter() {
        MyClassFilterDialog dialog = new MyClassFilterDialog(context, false);
        dialog.setTimeSelection(timePosition);
        dialog.setStateSelection(statePosition);
        dialog.setGroup1(getResources().getStringArray(R.array.class_filter_type)).setGroup2(getResources().getStringArray(R.array.class_filter_time));
        dialog.showAsDropDown(mFilterLine);
        mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_up, 0);
        dialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_down, 0);
            }
        });
        dialog.setOnOkListener(new MyClassFilterDialog.OnOkListener() {
            @Override
            public void onOk(int group1Position, int group2Position) {
                MyClassFragment.this.timePosition = group1Position;
                MyClassFragment.this.statePosition = group2Position;
                //TODO 重新查询
                if (classAdapter != null) {
                    classAdapter.setRole(ClassFilterHelper.getType(group1Position));
                    if(group2Position==0){
                        classAdapter.setTime(null,null);
                    }else {
                        classAdapter.setTime(ClassFilterHelper.getStartTime(group2Position),ClassFilterHelper.getEndTime(group2Position));
                    }
                    classAdapter.refresh();
                }
            }
        });
    }


    public void refresh(){
        classAdapter.refresh();
    }

}
