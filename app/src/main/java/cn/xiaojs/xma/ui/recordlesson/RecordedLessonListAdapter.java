package cn.xiaojs.xma.ui.recordlesson;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;


import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.model.recordedlesson.Section;
import cn.xiaojs.xma.ui.base.AbsListAdapter;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;
import cn.xiaojs.xma.ui.recordlesson.model.RLLesson;
import cn.xiaojs.xma.ui.recordlesson.view.RLDirectoryView;
import cn.xiaojs.xma.ui.recordlesson.view.RLLessonView;
import cn.xiaojs.xma.ui.recordlesson.view.RLSectionDocView;
import cn.xiaojs.xma.ui.recordlesson.view.RLSectionView;

/**
 * Created by Paul Z on 2017/7/18.
 */

public class RecordedLessonListAdapter extends AbsListAdapter<Section,AbsListAdapter.ViewHolder> {


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
            holder=new CommonHolder(new RLSectionView(mContext));

        }else if(viewType==1){
            holder=new CommonHolder(new RLSectionDocView(mContext));
        }
        return holder;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Section section=getItem(position);
        if(section.isLib()){
            return 0;
        }else if(section.isLesson()){
            return 1;
        }
        return 0;
    }


    @Override
    public int getCount() {
        int count=0;
        for(int i=0,size=super.getCount();i<size;i++){
            Section o=getList().get(i);
            if(o.isLib()){
                count+=1+o.getChildrenCount();
            }else {
                count++;
            }
        }
        return count;
    }

    @Override
    public Section getItem(int position) {
        int cursor=0;
        for(int i=0,size=super.getCount();i<size;i++){
            Section dir= getList().get(i);
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




}
