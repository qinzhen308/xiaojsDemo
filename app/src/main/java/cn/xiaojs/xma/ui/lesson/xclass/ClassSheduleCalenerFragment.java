package cn.xiaojs.xma.ui.lesson.xclass;

import android.view.View;

import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class ClassSheduleCalenerFragment extends AbsClassScheduleFragment {

    ClassScheduleBuz buz=new ClassScheduleBuz();

    @Override
    protected View getContentView() {
        return View.inflate(getActivity(), R.layout.layout_home_schedule,null);
    }

    @Override
    protected void init() {
        buz.init(getActivity(),mContent);
    }

}
