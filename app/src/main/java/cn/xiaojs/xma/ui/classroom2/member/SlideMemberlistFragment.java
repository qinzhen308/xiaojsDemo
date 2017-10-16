package cn.xiaojs.xma.ui.classroom2.member;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom2.base.RightSheetFragment;
import cn.xiaojs.xma.ui.widget.ClosableAdapterSlidingLayout;

/**
 * Created by maxiaobao on 2017/10/15.
 */

public class SlideMemberlistFragment extends RightSheetFragment {

    @BindView(R.id.root_lay)
    ClosableAdapterSlidingLayout root;



    @Override
    protected View onCreateView() {
        return LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_classroom2_memberlist_slide, null);
    }


    @Override
    protected View getTargetView(View root) {
        return root.findViewById(R.id.title);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    protected void hidden() {

    }

}
