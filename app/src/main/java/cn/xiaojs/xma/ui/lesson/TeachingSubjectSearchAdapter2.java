package cn.xiaojs.xma.ui.lesson;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pulltorefresh.AbsMultipleListAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.CategoriesManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.CollectionPage;

/**
 * Created by Paul Z on 2017/5/11.
 */

public class TeachingSubjectSearchAdapter2 extends AbsMultipleListAdapter<CSubject, BaseHolder> {
    private String key="";
    private int selectedPosition=-1;
    private OnSelectedListener mOnSelectedListener;

    public TeachingSubjectSearchAdapter2(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
    }

    public TeachingSubjectSearchAdapter2(Context context, PullToRefreshSwipeListView listView, boolean autoLoad) {
        super(context, listView, autoLoad);
    }


    public CSubject getSelectedItem(){
        if(selectedPosition<0)return null;
        return getItem(selectedPosition);
    }
    public int getSelectedPosition(){
        return selectedPosition;
    }

    @Override
    public void onBindViewHolder(int position, BaseHolder holder) {
        int type = getItemViewType(position);
        if (type == 1) {
            onBindViewHolder1(position, (ViewHolder1) holder);
        } else {
            onBindViewHolder2(position, (ViewHolder2) holder);
        }
    }

    public void onBindViewHolder1(final int position, ViewHolder1 holder) {
//        holder.subjectName.setText((String)getItem(position));
        holder.subjectName.setText(getItem(position).getName());
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

        CSubject item = (CSubject)getItem(position);
        holder.subjectName.setText(item.getName());
        holder.subjectParentTree.setText(item.getRemarks());
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

    public void clear(){
        if(mBeanList!=null){
            mBeanList.clear();
            notifyDataSetChanged();
        }
    }

    private CSubject createSubject() {
        CSubject subject = new CSubject();
        subject.setName(key);
        return subject;
    }


    @Override
    protected void doRequest() {
        if(TextUtils.isEmpty(key)||key.trim().length()==0){
            clear();
            return;
        }
        CategoriesManager.searchSubjects(mContext, key, mPagination, new APIServiceCallback<CollectionPage<CSubject>>() {
            @Override
            public void onSuccess(CollectionPage<CSubject> object) {
                if(mPagination.getPage()<=1){
                    getList().clear();
                    //getList().add(createSubject());
                    object.objectsOfPage.add(0,createSubject());
                }


                if(object!=null&&object.objectsOfPage.size()!=0){
//                    getList().addAll(object.objectsOfPage);
                    TeachingSubjectSearchAdapter2.this.onSuccess(object.objectsOfPage);
                }else {
                    TeachingSubjectSearchAdapter2.this.onSuccess(null);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                mPagination.setPage(mPagination.getPage()-1);
                getList().clear();
                getList().add(createSubject());
                notifyDataSetChanged();
            }
        });
    }

    public void doRequest(final String key){
        this.key=key;
        selectedPosition=-1;
        if(mOnSelectedListener!=null){
            mOnSelectedListener.onSelected(-1);
        }
        doRequest();
    }


    private void updateDisplay(String key, List<CSubject> object) {
        List<CSubject> list=new ArrayList<>();
        list.add(createSubject());
        if(object!=null){
            list.addAll(object);
        }
        setList(list);
        notifyDataSetChanged();
    }


//    @Override
//    public void setList(List<Object> list) {
//        selectedPosition=-1;
//        if(mOnSelectedListener!=null){
//            mOnSelectedListener.onSelected(-1);
//        }
//        super.setList(list);
//    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseHolder holder = null;
        if (viewType == 1) {
            holder = new ViewHolder1(View.inflate(mContext, R.layout.teaching_subject_list_item, null));
        } else if (viewType == 2) {
            holder = new ViewHolder2(View.inflate(mContext, R.layout.item_teaching_subject_search, null));
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        Object item=getItem(position);
        if (position == 0 ) {
            return 1;
        } else if(item instanceof CSubject){
            return 2;
        }
        return 0;
    }

    public static class ViewHolder1 extends BaseHolder {
        @BindView(R.id.subject_name)
        TextView subjectName;
        @BindView(R.id.selected_status)
        ImageView selectedStatus;

        public ViewHolder1(View v) {
            super(v);
        }
    }

    public static class ViewHolder2 extends BaseHolder {
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
