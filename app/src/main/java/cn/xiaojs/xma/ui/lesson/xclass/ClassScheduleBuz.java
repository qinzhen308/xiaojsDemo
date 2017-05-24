package cn.xiaojs.xma.ui.lesson.xclass;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.schedule.ScheduleRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/5/22.
 */

public class ClassScheduleBuz {
    Activity mContext;
    View mRoot;
    @BindView(R.id.over_layout)
    ScheduleRecyclerView overLayout;
    @BindView(R.id.slSchedule)
    ScheduleLayout calendarView;

    HomeClassAdapter mAdapter;


    /**
     * @param context
     * @param root
     * @link cn.xiaojs.xma.R.layout.fragment_home_class_normal.xml
     */
    public void init(Activity context, View root) {
        mContext = context;
        mRoot = root;
//        R.layout.fragment_home_class_normal
        ButterKnife.bind(this, root);
        initListView();
    }

    private void initListView(){
        mAdapter=new HomeClassAdapter();
        overLayout.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        overLayout.setAdapter(mAdapter);
        test();
    }


    private void test(){
        calendarView.setOnCalendarClickListener(new OnCalendarClickListener() {
            @Override
            public void onClickDate(int year, int month, int day) {

            }

            @Override
            public void onPageChange(int year, int month, int day) {

            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                calendarView.addTaskHint(5);
                calendarView.addTaskHint(10);
                calendarView.addTaskHint(15);
            }
        }, 2000);
    }


}
