package cn.xiaojs.xma.ui.lesson.xclass;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsMultipleListAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;

/**
 * Created by Paul Z on 2017/5/18.
 */

public class LessonScheduleAdapter extends AbsMultipleListAdapter<Object,BaseHolder> {


    public LessonScheduleAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
        data();
    }

    public LessonScheduleAdapter(Context context, PullToRefreshSwipeListView listView, boolean autoLoad) {
        super(context, listView, autoLoad);
    }

    private void data(){
        mBeanList=new ArrayList<>();
        mBeanList.add(new DateHolder(new View(mContext)));
        mBeanList.add(new LessonHolder(new View(mContext)));
        mBeanList.add(new LessonHolder(new View(mContext)));
        mBeanList.add(new LessonHolder(new View(mContext)));
        mBeanList.add(new LessonHolder(new View(mContext)));
        mBeanList.add(new LessonHolder(new View(mContext)));
        mBeanList.add(new LessonHolder(new View(mContext)));
        mBeanList.add(new LessonHolder(new View(mContext)));
        mBeanList.add(new DateHolder(new View(mContext)));
        mBeanList.add(new LessonHolder(new View(mContext)));
        mBeanList.add(new LessonHolder(new View(mContext)));
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(int position, BaseHolder holder) {
        int type=getItemViewType(position);
        if(type==1){
            onBindDate(position,(DateHolder) holder);
        }else if(type==2){
            onBindLesson(position,(LessonHolder) holder);
        }
    }

    public void onBindDate(final int position, DateHolder holder) {

    }

    public void onBindLesson(final int position, LessonHolder holder) {

    }



    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseHolder holder=null;
        if(viewType==1){
            holder=new DateHolder(LayoutInflater.from(mContext).inflate(R.layout.item_lesson_schedule_date,null));
        }else if(viewType==2){
            holder=new LessonHolder(LayoutInflater.from(mContext).inflate(R.layout.item_lesson_schedule_lesson,null));
        }
        return holder;
    }

    @Override
    protected void doRequest() {

    }


    @Override
    public int getItemViewType(int position) {
        Object item=getItem(position);
        if(item instanceof DateHolder){
            return 1;
        }else if(item instanceof LessonHolder){
            return 2;
        }
        return 0;
    }

    public static class DateHolder extends BaseHolder{

        public DateHolder(View view) {
            super(view);
        }
    }

    public static class LessonHolder extends BaseHolder{

        public LessonHolder(View view) {
            super(view);
        }
    }


}
