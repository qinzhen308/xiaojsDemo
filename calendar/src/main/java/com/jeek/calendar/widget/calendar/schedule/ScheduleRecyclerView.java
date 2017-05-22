package com.jeek.calendar.widget.calendar.schedule;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Jimmy on 2016/10/8 0008.
 */
public class ScheduleRecyclerView extends RecyclerView implements ScrollConflictResolver{


    public ScheduleRecyclerView(Context context) {
        super(context);
    }

    public ScheduleRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isScrollTop() {
        return computeVerticalScrollOffset() == 0;
    }

    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (getOnFocusChangeListener() != null) {
            getOnFocusChangeListener().onFocusChange(child, false);
            getOnFocusChangeListener().onFocusChange(focused, true);
        }
    }

    @Override
    public boolean resolve() {
        return getChildCount() == 0 || isScrollTop();
    }
}