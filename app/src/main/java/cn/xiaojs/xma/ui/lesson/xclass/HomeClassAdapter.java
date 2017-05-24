package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;
import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/5/22.
 */

public class HomeClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder=null;
        if(viewType==1){
            holder=new LessonLabelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson_schedule_date,parent,false));
        }else if(viewType==2){
            holder=new LessonHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_lesson,parent,false));

        }else if(viewType==3){
            holder=new ClassLabelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_classes_label,parent,false));

        }else if(viewType==4){
            holder=new ClassHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson_schedule_lesson,parent,false));
        }else if(viewType==10){
            holder=new NoLessonHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_no_lesson,parent,false));
        }else if(viewType==11){
            holder=new NoClassHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_no_class,parent,false));
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(),ClassScheduleActivity.class));
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return 1;
        }else if(position==1){
            return 10;
        }else if(position<5){
            return 2;
        }else if(position==5){
            return 3;
        }else if(position==6){
            return 11;
        }else if(position==(getItemCount()-1)){
            return 100;
        }
        return 4;
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    public static class LessonHolder extends RecyclerView.ViewHolder{

        public LessonHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public static class NoLessonHolder extends RecyclerView.ViewHolder{

        public NoLessonHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public static class ClassHolder extends RecyclerView.ViewHolder{

        public ClassHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
 public static class NoClassHolder extends RecyclerView.ViewHolder{

        public NoClassHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public static class ClassLabelHolder extends RecyclerView.ViewHolder{

        public ClassLabelHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    public static class LessonLabelHolder extends RecyclerView.ViewHolder{

        public LessonLabelHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public static class LastEmpterHolder extends RecyclerView.ViewHolder{

        public LastEmpterHolder(View itemView) {
            super(itemView);
        }
    }


}
