package cn.xiaojs.xma.common.pulltorefresh;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;


import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;

/**
 * Created by Paul Z on 2017/5/14.
 */

public abstract class AbsMultipleListAdapter<T ,H extends BaseHolder> extends AbsSwipeAdapter<T,H> {


    public AbsMultipleListAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
    }

    public AbsMultipleListAdapter(Context context, PullToRefreshSwipeListView listView, boolean autoLoad) {
        super(context, listView, autoLoad);
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (mBeanList.size() > 0) {
            H holder = null;
            int type=getItemViewType(position);
            if(view==null){
                holder=onCreateViewHolder(parent,type);
                view=holder.root;
                view.setTag(R.id.tag_list_adapter_viewholder,holder);
                view.setTag(R.id.tag_list_adapter_viewtype,type);
            }else {
                if(type==((int)view.getTag(R.id.tag_list_adapter_viewtype))){
                    holder=(H)view.getTag(R.id.tag_list_adapter_viewholder);
                }else {
                    holder=onCreateViewHolder(parent,type);
                    view=holder.root;
                    view.setTag(R.id.tag_list_adapter_viewholder,holder);
                    view.setTag(R.id.tag_list_adapter_viewtype,type);
                }
            }
            onBindViewHolder(position,holder);
            return view;
        } else {//解决加了header后，header高度超过1屏无法下拉
            View v = new View(mContext);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            v.setBackgroundResource(android.R.color.transparent);
            v.setLayoutParams(lp);
            return v;
        }
    }

    public abstract void onBindViewHolder(int position , H holder);

    public abstract H onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    protected final void setViewContent(BaseHolder holder, Object bean, int position) {

    }

    @Override
    protected final View createContentView(int position) {
        return null;
    }

    @Override
    protected final H initHolder(View view) {
        return null;
    }

}
