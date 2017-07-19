package cn.xiaojs.xma.ui.recordlesson;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;

import cn.xiaojs.xma.ui.base.AbsListAdapter;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;
import cn.xiaojs.xma.ui.recordlesson.model.RLLesson;
import cn.xiaojs.xma.ui.recordlesson.view.RLDirectoryView;
import cn.xiaojs.xma.ui.recordlesson.view.RLLessonView;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/7/18.
 */

public class RecordedLessonAdapter2 extends AbsListAdapter<Object,AbsListAdapter.ViewHolder> {


    public Class classes[]={RLDirectory.class,RLLesson.class};

    public RecordedLessonAdapter2(Activity activity) {
        super(activity);
    }

    @Override
    public void onBindViewHolder(int position, ViewHolder holder) {
        if(holder.root instanceof IViewModel){
            ((IViewModel) holder.root).bindData(position,getItem(position));
        }
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

    public static class CommonHolder extends ViewHolder{

        public CommonHolder(View v) {
            super(v);
        }
    }



    public void insert(Object item,int to){
        if(ArrayUtil.isEmpty(getList())){
            return;
        }
        getList().add(to,item);
    }

    public boolean remove(Object item){
        if(ArrayUtil.isEmpty(getList())){
            return false;
        }
        return getList().remove(item);
    }

    public Object remove(int position){
        if(ArrayUtil.isEmpty(getList())){
            return null;
        }
        return getList().remove(position);
    }


    public boolean isDir(Object item){
        return item instanceof RLDirectory;
    }
    public boolean isLesson(Object item){
        return item instanceof RLLesson;
    }


    public void onDrop(DragSortListView listView, int from, int to,ArrayList group){
        Object fromO =getItem(from);
        if(isDir(fromO)){
            int nextDirIndex=0;
            //取得该组课,并判断下一组的起始position
            int finalTo=to;
            for(int i=from+1;i<getCount();i++){
                Object item=getItem(i);
                if(isDir(item)){
                    nextDirIndex=i;
                    break;
                }
            }

            if(to>from){
                if (to<nextDirIndex){
                    finalTo=from;
                    return;
                }
                finalTo=nextDirIndex;
            }else {
                int preDirIndex=0;
                for(int i=from-1;i>=0;i--){
                    Object item=getItem(i);
                    if(isDir(item)){
                        preDirIndex=i;
                        break;
                    }
                }
                if (to>preDirIndex){
                    finalTo=from;
                    return;
                }
                finalTo=preDirIndex;
            }
            insert(remove(from),finalTo);
            getList().addAll(finalTo+1,group);
            notifyDataSetChanged();
            listView.moveCheckState(from,to);
            return;
        }else if(isLesson(fromO)){
            if(to==0)return;
        }else {
            return;
        }
        insert(remove(from),to);
        notifyDataSetChanged();
        listView.moveCheckState(from,to);
    }


    public ArrayList onStartDrag(int which){
        Object o=getItem(which);
        if(!isDir(o)){
            return null;
        }
        ArrayList group=new ArrayList();
        for(int i=which+1 ,size=getCount();i<size;i++){
            Object item=getItem(i);
            if(isLesson(item)){
                group.add(item);
            }else {
                break;
            }
        }
        for(int i=0 ,size=group.size();i<size;i++){
            remove(which+1);
        }
        notifyDataSetChanged();
        return group;
    }

}
