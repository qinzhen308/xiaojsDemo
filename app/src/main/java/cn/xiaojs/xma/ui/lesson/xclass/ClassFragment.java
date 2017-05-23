package cn.xiaojs.xma.ui.lesson.xclass;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.ui.lesson.CourseFilterDialog;
import cn.xiaojs.xma.ui.lesson.LessonBusiness;
import cn.xiaojs.xma.ui.lesson.TeachLessonActivity;

/**
 * Created by maxiaobao on 2017/5/22.
 */

public class ClassFragment extends Fragment {

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
        ButterKnife.bind(this,v);
        searchView.setHint(R.string.hint_input_class_name);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        classAdapter = new ClassAdapter(context,listView);
        classAdapter.setDesc(getString(R.string.empty_class_tips));
        classAdapter.setIcon(R.drawable.ic_teach_empty);
        classAdapter.setButtonDesc(getString(R.string.want_to_create_class));

        listView.setAdapter(classAdapter);
    }

    @OnClick({R.id.course_filter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.course_filter:
                filter();
                break;
        }
    }

    private void filter() {
        CourseFilterDialog dialog = new CourseFilterDialog(context, false);
        dialog.setTimeSelection(timePosition);
        dialog.setStateSelection(statePosition);
        dialog.showAsDropDown(mFilterLine);
        mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_up, 0);
        dialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_down, 0);
            }
        });
        dialog.setOnOkListener(new CourseFilterDialog.OnOkListener() {
            @Override
            public void onOk(int timePosition, int statePosition, int sourcePosition) {
                ClassFragment.this.timePosition = timePosition;
                ClassFragment.this.statePosition = statePosition;
                ClassFragment.this.sourcePosition = sourcePosition;
                Criteria criteria = LessonBusiness.getFilter(timePosition, statePosition, sourcePosition, false);
                //TODO 重新查询
//                if (mAdapter != null) {
//                    mAdapter.request(criteria);
//                }
            }
        });
    }
}
