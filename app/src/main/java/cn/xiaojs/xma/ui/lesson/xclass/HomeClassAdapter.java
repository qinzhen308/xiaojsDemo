package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.ui.lesson.xclass.view.ClassView;
import cn.xiaojs.xma.ui.lesson.xclass.view.HomeClassLabelView;
import cn.xiaojs.xma.ui.lesson.xclass.view.HomeLessonLabelView;
import cn.xiaojs.xma.ui.lesson.xclass.view.HomeLessonView;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/5/22.
 */

public class HomeClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final int VIEW_TYPE_HOME_LESSON_LABEL =1;
    public static final int VIEW_TYPE_HOME_LESSON=2;
    public static final int VIEW_TYPE_HOME_CLASS_LABEL=3;
    public static final int VIEW_TYPE_HOME_CLASS =4;
//    public static final int VIEW_TYPE_HOME_NO_LESSON=5;
//    public static final int VIEW_TYPE_HOME_NO_CLASS=6;
    public static final int VIEW_TYPE_LAST_EMPTY=100;

    private List<?> mList;


    public void setList(List<?> list){
        mList=list;
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
            ((IViewModel) holder.itemView).bindData(null);
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
        if(position==(getItemCount()-1)){
            return VIEW_TYPE_LAST_EMPTY;
        }else if(o instanceof ClassLesson){
            return VIEW_TYPE_HOME_LESSON;
        }
        return VIEW_TYPE_HOME_CLASS;
    }

    private Object getItem(int position){
        return ArrayUtil.isEmpty(mList)?null:mList.get(position);
    }

    @Override
    public int getItemCount() {
        return mList==null?0:(mList.size()+1);
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


}
