package cn.xiaojs.xma.ui.lesson.xclass;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
            holder=new LessonHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson_schedule_lesson,parent,false));

        }else if(viewType==3){
            holder=new ClassLabelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson_schedule_date,parent,false));

        }else if(viewType==4){
            holder=new ClassHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_organization_teacher,parent,false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return 1;
        }else if(position<4){
            return 2;
        }else if(position==5){
            return 3;
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

    public static class ClassHolder extends RecyclerView.ViewHolder{

        public ClassHolder(View itemView) {
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


}
