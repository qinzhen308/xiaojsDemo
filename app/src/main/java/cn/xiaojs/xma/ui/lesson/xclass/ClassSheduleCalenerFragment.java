package cn.xiaojs.xma.ui.lesson.xclass;

import android.view.View;

import java.util.Date;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.ui.lesson.xclass.util.IUpdateMethod;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class ClassSheduleCalenerFragment extends AbsClassScheduleFragment implements IUpdateMethod {

    ClassScheduleBuz buz=new ClassScheduleBuz();

    @Override
    protected View getContentView() {
        return View.inflate(getActivity(), R.layout.layout_home_schedule,null);
    }

    @Override
    protected void init() {
        buz.init(getActivity(),mContent);
    }


    public void refresh(ClassLesson cl){
        if(mContent==null)return;
        if(cl!=null){
            Date date=cl.schedule.getStart();
            buz.updateAndMoveToDate(ScheduleUtil.getDateYMD(date));
        }
    }

    public long getSelectedDate(){
        return buz.getSelectedDate();
    }

    @Override
    public void updateData(boolean justNative,Object... others) {
        if(mContent==null)return;

        buz.updateData(justNative);
    }

    @Override
    public void updateItem(int position, Object obj, Object... others) {
        if(mContent==null)return;

        buz.updateItem(position,obj,others);
    }
}
