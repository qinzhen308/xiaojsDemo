package cn.xiaojs.xma.ui.lesson.xclass;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import cn.xiaojs.xma.ui.base.BaseFragment;

/**
 * Created by Paul Z on 2017/5/23.
 */

public abstract class AbsClassScheduleFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rejectButterKnife();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mContent==null){
            super.onCreateView(inflater, container, savedInstanceState);
        }else {
            ViewParent parent=mContent.getParent();
            if(parent!=null){
                ((ViewGroup)parent).removeView(mContent);
            }
        }
        return mContent;
    }


}
