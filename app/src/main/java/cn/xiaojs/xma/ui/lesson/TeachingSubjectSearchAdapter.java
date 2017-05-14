package cn.xiaojs.xma.ui.lesson;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.AbsListAdapter;

/**
 * Created by Paul Z on 2017/5/11.
 */

public class TeachingSubjectSearchAdapter extends AbsListAdapter<Object, AbsListAdapter.ViewHolder> {

    private int selectedPosition=-1;
    private OnSelectedListener mOnSelectedListener;

    public TeachingSubjectSearchAdapter(Activity activity) {
        super(activity);
    }

    @Override
    public int getCount() {
        return 10;
    }

    public Object getSelectedItem(){
        if(selectedPosition<0)return null;
        return getItem(selectedPosition);
    }

    @Override
    public void onBindViewHolder(int position, ViewHolder holder) {
        int type = getItemViewType(position);
        if (type == 1) {
            onBindViewHolder1(position, (ViewHolder1) holder);
        } else {
            onBindViewHolder2(position, (ViewHolder2) holder);
        }
    }

    public void onBindViewHolder1(final int position, ViewHolder1 holder) {
        holder.subjectName.setText((String)getItem(position));
        if(position==selectedPosition){
            holder.selectedStatus.setVisibility(View.VISIBLE);
            holder.subjectName.setTextColor(mContext.getResources().getColor(R.color.main_orange));
        }else {
            holder.selectedStatus.setVisibility(View.GONE);
            holder.subjectName.setTextColor(mContext.getResources().getColor(R.color.font_black));
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition=position;
                notifyDataSetChanged();
                if(mOnSelectedListener!=null){
                    mOnSelectedListener.onSelected(position);
                }
            }
        });
    }

    public void onBindViewHolder2(final int position, ViewHolder2 holder) {

        if(position==selectedPosition){
            holder.selectedStatus.setVisibility(View.VISIBLE);
            holder.subjectName.setTextColor(mContext.getResources().getColor(R.color.main_orange));
            holder.subjectParentTree.setTextColor(mContext.getResources().getColor(R.color.main_orange));
        }else {
            holder.selectedStatus.setVisibility(View.GONE);
            holder.subjectName.setTextColor(mContext.getResources().getColor(R.color.font_black));
            holder.subjectParentTree.setTextColor(mContext.getResources().getColor(R.color.common_text));
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition=position;
                notifyDataSetChanged();
                if(mOnSelectedListener!=null){
                    mOnSelectedListener.onSelected(position);
                }
            }
        });
    }

    @Override
    public void setList(List<Object> list) {
        selectedPosition=-1;
        if(mOnSelectedListener!=null){
            mOnSelectedListener.onSelected(-1);
        }
        super.setList(list);
    }

    @Override
    public void setList(Object[] list) {
        selectedPosition=-1;
        if(mOnSelectedListener!=null){
            mOnSelectedListener.onSelected(-1);
        }
        super.setList(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;
        if (viewType == 1) {
            holder = new ViewHolder1(View.inflate(mContext, R.layout.teaching_subject_list_item, null));
        } else if (viewType == 2) {
            holder = new ViewHolder2(View.inflate(mContext, R.layout.item_teaching_subject_search, null));
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && getItem(position) instanceof String) {
            return 1;
        } else {
            return 2;
        }
    }

    public static class ViewHolder1 extends ViewHolder {
        @BindView(R.id.subject_name)
        TextView subjectName;
        @BindView(R.id.selected_status)
        ImageView selectedStatus;

        public ViewHolder1(View v) {
            super(v);
        }
    }

    public static class ViewHolder2 extends ViewHolder {
        @BindView(R.id.subject_name)
        TextView subjectName;
        @BindView(R.id.subject_parent_tree)
        TextView subjectParentTree;
        @BindView(R.id.selected_status)
        ImageView selectedStatus;

        public ViewHolder2(View v) {
            super(v);
        }
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.mOnSelectedListener = onSelectedListener;
    }

    public interface OnSelectedListener{
        void onSelected(int position);
    }

}
