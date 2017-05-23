package cn.xiaojs.xma.ui.lesson.xclass;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2017/5/22.
 */

public class ClassFragment extends Fragment {

    @BindView(R.id.my_course_search)
    TextView searchView;


    private Context context;

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
    }
}
