package cn.xiaojs.xma.ui.lesson;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.AbsListAdapter;

/**
 * Created by Paul Z on 2017/5/11.
 */

public class TeachingSubjectSearchAdapter extends AbsListAdapter<Object,AbsListAdapter.ViewHolder> {

    public TeachingSubjectSearchAdapter(Activity activity) {
        super(activity);
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public void onBindViewHolder(int position, ViewHolder holder) {
        int type=getItemViewType(position);
        if(type==1){
            onBindViewHolder1(position,(ViewHolder1) holder);
        }else {
            onBindViewHolder2(position,(ViewHolder2) holder);
        }
    }

    public void onBindViewHolder1(int position, ViewHolder1 holder){

    }

    public void onBindViewHolder2(int position, ViewHolder2 holder){

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder=null;
        if(viewType==1){
            holder=new ViewHolder1(View.inflate(mContext,R.layout.teaching_subject_list_item,null));
        }else if(viewType==2){
            holder=new ViewHolder1(View.inflate(mContext,R.layout.item_teaching_subject_search,null));
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0&&getItem(position) instanceof String){
            return 1;
        }else {
            return 2;
        }
    }

    public static class ViewHolder1 extends AbsListAdapter.ViewHolder{


        public ViewHolder1(View v) {
            super(v);
        }
    }
    public static class ViewHolder2 extends AbsListAdapter.ViewHolder{


        public ViewHolder2(View v) {
            super(v);
        }
    }

}
