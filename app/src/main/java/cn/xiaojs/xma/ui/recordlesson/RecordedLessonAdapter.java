package cn.xiaojs.xma.ui.recordlesson;

import android.app.Activity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mobeta.android.dslv.DragSortListView;


import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
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
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.ToastUtil;

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
                if(what==EVENT_1){
                    notifyDataSetChanged();
                }else if(what==EVENT_2){
                    showEditDirDialog((int)object[1],(RLDirectory) object[0]);
                }else if(what==EVENT_3){
                    int[] targetDirPosition=findInGroup((int)object[1]);
                    AddLessonDirActivity.invoke(mContext,(ArrayList<RLDirectory>) (Object)getList(),(RLLesson) object[0],targetDirPosition[0],targetDirPosition[1]);
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
        return classes.length+2;
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


    public void addDir(RLDirectory dir){
        List list=getList();
        if(list==null){
            list=new ArrayList();
        }
        list.add(dir);
        notifyDataSetChanged();
    }

    public void addLesson(int dirPosition,RLLesson lesson){
        if(lesson==null)return;
        RLDirectory dir=(RLDirectory)super.getItem(dirPosition);
        if(dir==null)return;
        dir.addChild(lesson);
        notifyDataSetChanged();
    }


    public void editLessonItem(int dirPosition,int lessonPosition,RLLesson lesson){
        if(ArrayUtil.isEmpty(getList()))return;
        RLDirectory dir=(RLDirectory) getList().get(dirPosition);
        dir.replace(lessonPosition,lesson);
        notifyDataSetChanged();
    }

    private void showEditDirDialog(int position, final RLDirectory dir){
        final CommonDialog dialog=new CommonDialog(mContext);
        dialog.setTitle(R.string.edit_directory_name);
        final EditTextDel editText=new EditTextDel(mContext);
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin=mContext.getResources().getDimensionPixelSize(R.dimen.px20);
        lp.topMargin=mContext.getResources().getDimensionPixelSize(R.dimen.px20);
        editText.setLayoutParams(lp);
        editText.setHint(R.string.add_new_dir_tip);
        editText.setLines(1);
        editText.setTextColor(mContext.getResources().getColor(R.color.font_black));
        editText.setBackgroundResource(R.drawable.common_search_bg);
        editText.setGravity(Gravity.LEFT|Gravity.TOP);
        int padding=mContext.getResources().getDimensionPixelSize(R.dimen.px10);
        editText.setPadding(padding,padding,padding,padding);
        editText.setHintTextColor(mContext.getResources().getColor(R.color.font_gray));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX,mContext.getResources().getDimensionPixelSize(R.dimen.font_28px));
        editText.setText(dir.name);
        dialog.setCustomView(editText);
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                String dirName=editText.getText().toString().trim();
                if(dirName.length()==0){
                    ToastUtil.showToast(mContext, R.string.add_new_dir_not_input_tip);
                    return;
                }
                dir.name=dirName;
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
