package com.jeek.calendar.widget.calendar.schedule;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * Created by Paul Z on 2017/5/22.
 */

public class SchedulListView extends ListView implements ScrollConflictResolver {

    public SchedulListView(Context context) {
        super(context);
    }

    public SchedulListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SchedulListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
