package cn.xiaojs.xma.ui.lesson.xclass;

import android.support.v7.widget.RecyclerView;

import cn.xiaojs.xma.ui.lesson.xclass.view.HomeLessonView;

/**
 * Created by Paul Z on 2017/6/7.
 */

public class ScheduleAdapter extends HomeClassAdapter{
    public ScheduleAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.itemView instanceof HomeLessonView ) {
            ((HomeLessonView) holder.itemView).setIsFromSchedule(true);
        }
        super.onBindViewHolder(holder, position);

    }
}
