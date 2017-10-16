package cn.xiaojs.xma.ui.classroom2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;

/**
 * Created by maxiaobao on 2017/9/25.
 */

public class IdleFragment extends MovieFragment {


    @BindView(R.id.p_bottom_class_name)
    TextView classNameView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_classroom2_idle, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        controlLand.setVisibility(View.GONE);
    }


    @Override
    public void closeMovie() {
        //do nothing
    }

    @Override
    public void onRotate(int orientation) {
        controlHandleOnRotate(orientation);
    }

    @Override
    public void onTopbackClick(View view, boolean land) {
        back();
    }

    @Override
    public void onStartLiveClick(View view) {
        requestLive();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////




}
