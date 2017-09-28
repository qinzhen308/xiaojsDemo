package cn.xiaojs.xma.ui.classroom2;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;

/**
 * Created by maxiaobao on 2017/9/25.
 */

public class IdleFragment extends MovieFragment {


    @BindView(R.id.bottom_class_name)
    TextView classNameView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_classroom2_idle, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.top_back,R.id.top_more, R.id.top_live, R.id.bottom_orient})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.top_back:
                back();
                break;
            case R.id.top_more:
                break;
            case R.id.top_live:                      //开始直播
                requestLive();
                break;
            case R.id.bottom_more:
                break;
            case R.id.bottom_orient:
                changeOrientation();
                break;
        }
    }


    @Override
    public void closeMovie() {
        //do nothing
    }

    @Override
    public void onRotate(int orientation) {

        //do nothing
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////


}
