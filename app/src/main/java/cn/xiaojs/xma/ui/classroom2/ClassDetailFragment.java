package cn.xiaojs.xma.ui.classroom2;

import android.os.Bundle;

import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom2.base.BaseDialogFragment;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class ClassDetailFragment extends BaseDialogFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_classroom2_class_detail, container, false);
        ButterKnife.bind(this,view);

        //setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NOTI);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @OnClick({R.id.back_btn})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                dismiss();
                break;
        }
    }
}
