package cn.xiaojs.xma.ui.classroom2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom2.base.PlayerFragment;


/**
 * Created by maxiaobao on 2017/9/18.
 */

public class PlaybackFragment extends PlayerFragment {

    @BindView(R.id.left_btn)
    ImageView controlLeftBtn;
    @BindView(R.id.right_btn)
    ImageView controlRightBtn;
    @BindView(R.id.top_bar)
    ConstraintLayout topBarLay;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);

        controlLeftBtn.setVisibility(View.VISIBLE);
        controlLeftBtn.setImageResource(R.drawable.ic_close);
        controlRightBtn.setVisibility(View.VISIBLE);
        controlRightBtn.setImageResource(R.drawable.ic_class_crossscreen);

        return view;
    }

    @OnClick({R.id.back_btn, R.id.menu_btn, R.id.live_btn, R.id.left_btn, R.id.right_btn})
    void OnViewClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:                       //返回
                back();
                break;
            case R.id.menu_btn:                       //更多
                break;
            case R.id.live_btn:                       //开始直播
                break;
            case R.id.left_btn:                       //关闭
                closeMovie();
                break;
            case R.id.right_btn:                      //横竖屏切换
                changeOrientation();
                break;
        }
    }

    @Override
    public void closeMovie() {

    }

    @Override
    public void onVisibilityChange(int visibility) {
        super.onVisibilityChange(visibility);

        topBarLay.setVisibility(visibility);

    }

}
