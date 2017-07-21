package cn.xiaojs.xma.ui.recordlesson;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;


import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.ui.base.AbsListAdapter;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;
import cn.xiaojs.xma.ui.recordlesson.model.RLLesson;
import cn.xiaojs.xma.ui.recordlesson.view.RLDirectoryView;
import cn.xiaojs.xma.ui.recordlesson.view.RLLessonView;

/**
 * Created by Paul Z on 2017/7/18.
 */

public class RecordedLessonListAdapter extends AbsListAdapter<Object,AbsListAdapter.ViewHolder> {

    public Class classes[]={RLDirectory.class,RLLesson.class};

    EventCallback eventCallback;


    public RecordedLessonListAdapter(Activity activity) {
        super(activity);
    }

    @Override
    public void onBindViewHolder(int position, ViewHolder holder) {
        if(holder.root instanceof IViewModel){
            ((IViewModel) holder.root).bindData(position,getItem(position));
        }
//        if(holder.root instanceof IEventer){
//            ((IEventer) holder.root).setEventCallback(eventCallback);
//        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CommonHolder holder=null;
        if(viewType==0){
            holder=new CommonHolder(new RLDirectoryView(mContext));

        }else if(viewType==1){
            holder=new CommonHolder(new RLLessonView(mContext));
        }
        return holder;
    }

    @Override
    public int getViewTypeCount() {
        return classes.length;
    }

    @Override
    public int getItemViewType(int position) {
        for(int i=0,size=classes.length;i<size;i++){
            if(classes[i].equals(getItem(position).getClass()) ){
                return i;
            }
        }
        return 0;
    }


    @Override
    public int getCount() {
        int count=0;
        for(int i=0,size=super.getCount();i<size;i++){
            Object o=getList().get(i);
            if(isDir(o)){
                count+=1+((RLDirectory)o).getChildrenCount();
            }else {
                count++;
            }
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        int cursor=0;
        for(int i=0,size=super.getCount();i<size;i++){
            RLDirectory dir=(RLDirectory) getList().get(i);
            if(cursor==position){
                return dir;
            }
            cursor+=1+dir.getChildrenCount();
            if(cursor>position){
                return dir.getChild(dir.getChildrenCount()-(cursor-position));
            }
        }
        return null;
    }

    public static class CommonHolder extends ViewHolder{

        public CommonHolder(View v) {
            super(v);
        }
    }




    public boolean isDir(Object item){
        return item instanceof RLDirectory;
    }
    public boolean isLesson(Object item){
        return item instanceof RLLesson;
    }




}
