package cn.xiaojs.xma.ui.lesson.xclass;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import java.util.List;

import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.ui.lesson.xclass.Model.ClassLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LastEmptyModel;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.RecyclerViewScrollHelper;
import cn.xiaojs.xma.ui.lesson.xclass.view.ClassView;
import cn.xiaojs.xma.ui.lesson.xclass.view.HomeClassLabelView;
import cn.xiaojs.xma.ui.lesson.xclass.view.HomeLessonLabelView;
import cn.xiaojs.xma.ui.lesson.xclass.view.HomeLessonView;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.lesson.xclass.view.NativeLessonView;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/5/22.
 */

public class HomeClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final int VIEW_TYPE_HOME_LESSON_LABEL =1;
    public static final int VIEW_TYPE_HOME_LESSON=2;
    public static final int VIEW_TYPE_HOME_CLASS_LABEL=3;
    public static final int VIEW_TYPE_HOME_CLASS =4;
    public static final int VIEW_TYPE_NATIVE_LESSON=5;
//    public static final int VIEW_TYPE_HOME_NO_CLASS=6;
    public static final int VIEW_TYPE_LAST_EMPTY=100;

    private List<?> mList;
    private RecyclerView mRecyclerView;
    RecyclerViewScrollHelper scrollHelper;

    public HomeClassAdapter(RecyclerView recyclerView){
        mRecyclerView=recyclerView;
        scrollHelper=new RecyclerViewScrollHelper();
        scrollHelper.bind(recyclerView);
    }


    public void setList(List<?> list){
        mList=list;
    }

    public List<Object> getList(){
        return (List<Object>) mList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder=null;
        if(viewType== VIEW_TYPE_HOME_LESSON_LABEL){
            holder=new CommonHolder(new HomeLessonLabelView(parent.getContext()));
        }else if(viewType==VIEW_TYPE_HOME_LESSON){
            holder=new CommonHolder(new HomeLessonView(parent.getContext()));

        }else if(viewType==VIEW_TYPE_HOME_CLASS_LABEL){
            holder=new CommonHolder(new HomeClassLabelView(parent.getContext()));
        }else if(viewType== VIEW_TYPE_HOME_CLASS){
            holder=new CommonHolder(new ClassView(parent.getContext()));
        }else if(viewType== VIEW_TYPE_NATIVE_LESSON){
            holder=new CommonHolder(new NativeLessonView(parent.getContext()));
        }else {
            View v=new View(parent.getContext());
            v.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,200));
            holder=new LastEmpterHolder(v);
        }
        Logger.d("----qz---- w="+parent.getWidth()+",h="+parent.getHeight()+"--------type="+viewType);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Logger.d("----qz---- "+position);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(),ClassScheduleActivity.class));
//            }
//        });
        if(holder.itemView instanceof IViewModel){
            ((IViewModel) holder.itemView).bindData(position,getItem(position));
        }
        if(holder.itemView instanceof HomeLessonView&&mEventCallback!=null){
            ((HomeLessonView) holder.itemView).setCallback(mEventCallback);
        }
    }

    @Override
    public int getItemViewType(int position) {
        /*if(position==0){
            return VIEW_TYPE_HOME_LESSON_LABEL;
        }else if(position<5){
            return VIEW_TYPE_HOME_LESSON;
        }else if(position==5){
            return VIEW_TYPE_HOME_CLASS_LABEL;
        }else if(position==(getItemCount()-1)){
            return VIEW_TYPE_LAST_EMPTY;
        }*/
        Object o=getItem(position);
        Logger.d("-----qz-----o="+o);
        if(o instanceof ClassLesson){
            return VIEW_TYPE_NATIVE_LESSON;
        }else if(o instanceof LastEmptyModel){
            return VIEW_TYPE_LAST_EMPTY;
        }else if(o instanceof ClassLabelModel){
            return VIEW_TYPE_HOME_CLASS_LABEL;
        }else if(o instanceof LessonLabelModel){
            return VIEW_TYPE_HOME_LESSON_LABEL;
        }else if(o instanceof CLesson){
            return VIEW_TYPE_HOME_LESSON;
        }
        return VIEW_TYPE_HOME_CLASS;
    }

    private Object getItem(int position){
        return ArrayUtil.isEmpty(mList)?null:mList.get(position);
    }


    /**
     * 跳转到 指定日期所在位置
     * @param date eg. 2017-04-01
     */
    public void scrollToLabel(String date){
        int size=getItemCount();
        int index=-1;
        for(int i=0;i<size;i++){
            Object o=getItem(i);
            if(o instanceof LessonLabelModel){
                if(((LessonLabelModel) o).date.contains(date)){
                    index=i;
                }
            }
        }
        if(index>=0){
//            mRecyclerView.smoothScrollToPosition(index);
            scrollHelper.smoothMoveToPosition(mRecyclerView,index);
        }
    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    public static class LastEmpterHolder extends RecyclerView.ViewHolder{

        public LastEmpterHolder(View itemView) {
            super(itemView);
        }
    }

    public static class CommonHolder extends RecyclerView.ViewHolder{

        public CommonHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }


    //通用callback，调用者和回传参数自己定义
    private EventCallback mEventCallback;
    public void setCallback(EventCallback eventCallback){
        mEventCallback=eventCallback;
    }

}
