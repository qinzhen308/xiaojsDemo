package cn.xiaojs.xma.ui.recordlesson;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.mobeta.android.dslv.DragSortListView;


import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.pageload.IEventer;
import cn.xiaojs.xma.ui.base.AbsListAdapter;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;
import cn.xiaojs.xma.ui.recordlesson.model.RLLesson;
import cn.xiaojs.xma.ui.recordlesson.view.RLDirectoryView;
import cn.xiaojs.xma.ui.recordlesson.view.RLEditableDirectoryView;
import cn.xiaojs.xma.ui.recordlesson.view.RLEditableLessonView;
import cn.xiaojs.xma.ui.recordlesson.view.RLLessonView;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/7/18.
 */

public class RecordedLessonAdapter extends AbsListAdapter<Object,AbsListAdapter.ViewHolder> {
    private DragSortListView listView;

    private boolean onlyShowDir=false;
    private boolean isEditMode=false;

    public Class classes[]={RLDirectory.class,RLLesson.class};

    EventCallback eventCallback;

    public void setOnlyShowDir(boolean onlyShowDir){
        this.onlyShowDir=onlyShowDir;
        notifyDataSetChanged();
    }

    public void setEditMode(boolean isEditMode){
        this.isEditMode=isEditMode;
        notifyDataSetChanged();
    }

    public RecordedLessonAdapter(Activity activity,DragSortListView listView) {
        super(activity);
        this.listView=listView;
        eventCallback=new EventCallback() {
            @Override
            public void onEvent(int what, Object... object) {
                if(what==1){
                    notifyDataSetChanged();
                }
            }
        };
    }

    @Override
    public void onBindViewHolder(int position, ViewHolder holder) {
        if(holder.root instanceof IViewModel){
            ((IViewModel) holder.root).bindData(position,getItem(position));
        }
        if(holder.root instanceof IEventer){
            ((IEventer) holder.root).setEventCallback(eventCallback);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CommonHolder holder=null;
        if(viewType==0){
            holder=new CommonHolder(new RLDirectoryView(mContext));

        }else if(viewType==1){
            holder=new CommonHolder(new RLLessonView(mContext));
        }else if(viewType==2){
            holder=new CommonHolder(new RLEditableDirectoryView(mContext));

        }else if(viewType==3){
            holder=new CommonHolder(new RLEditableLessonView(mContext));
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
                return isEditMode?i+2:i;
            }
        }
        return 0;
    }

    public int[] findInGroup(int position){
        if(onlyShowDir)return new int[]{-1,position};
        int[] result=new int[2];
        int cursor=0;
        for(int i=0,size=super.getCount();i<size;i++){
            Object item=super.getItem(i);
            if(isDir(item)){
                cursor+=((RLDirectory)item).getChildrenCount()+1;
            }else {
                cursor++;
            }
            if (cursor>=position){
                result[0]=i;
                result[1]=((RLDirectory)item).getChildrenCount()-(cursor-position);
                return result;
            }
        }
        return new int[]{-1,-1};
    }


    @Override
    public int getCount() {
        if(onlyShowDir)return super.getCount();
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
        if(onlyShowDir){
            return super.getItem(position);
        }else {
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
    }

    public static class CommonHolder extends ViewHolder{

        public CommonHolder(View v) {
            super(v);
        }
    }



    public void insert(Object item,int to){
        if(ArrayUtil.isEmpty(getList())){
            return;
        }
        if(onlyShowDir){
            getList().add(to,item);
            return;
        }
        int[] indexs=findInGroup(to);
        int group=indexs[0];
        int child=indexs[1];
        if(child==-1&&group>0){
            group--;
            RLDirectory dir=(RLDirectory) super.getItem(group);
            dir.addChild((RLLesson) item);
        }else {
            RLDirectory dir=(RLDirectory) super.getItem(group);
            dir.insert(child,(RLLesson) item);
        }
    }

    public boolean remove(Object item){
        if(ArrayUtil.isEmpty(getList())){
            return false;
        }
        if(onlyShowDir){
            return getList().remove(item);
        }else {
            // TODO: 2017/7/19 暂未实现
            return false;
        }
    }

    public Object remove(int position){
        if(ArrayUtil.isEmpty(getList())){
            return null;
        }
        if(onlyShowDir){
            return getList().remove(position);
        }
        int[] indexs=findInGroup(position);
        int group=indexs[0];
        int child=indexs[1];
        RLDirectory dir=(RLDirectory) super.getItem(group);
        return dir.remove(child);
    }


    public boolean isDir(Object item){
        return item instanceof RLDirectory;
    }
    public boolean isLesson(Object item){
        return item instanceof RLLesson;
    }


    public void onDrop(int from, int to){
        Object fromO =getItem(from);
        if(isDir(fromO)){
            insert(remove(from),to);
            notifyDataSetChanged();
            setOnlyShowDir(false);
            return;
        }else if(isLesson(fromO)){
            if(to==0){
                return;
            }else {
                insert(remove(from),to);
                notifyDataSetChanged();
            }
        }else {
            return;
        }
    }


    public void onStartDrag(int which){
        Object o=getItem(which);
        if(isDir(o)){
            setOnlyShowDir(true);
            int cursor=0;
            int group=-1;
            for(int i=0,size=super.getCount();i<size;i++){
                if(cursor==which){
                    group=i;
                    break;
                }
                Object item=super.getItem(i);
                if(isDir(item)){
                    cursor+=((RLDirectory)item).getChildrenCount()+1;
                }else {
                    cursor++;
                }
            }
            if(group>=0){
                listView.hookChangeStartPosition(group);
            }
        }
    }

}
