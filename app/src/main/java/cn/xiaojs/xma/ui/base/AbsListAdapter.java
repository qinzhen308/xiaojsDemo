package cn.xiaojs.xma.ui.base;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/5/11.
 */

public abstract class AbsListAdapter<T,H extends AbsListAdapter.ViewHolder> extends BaseAdapter{
    private List<T> mList;
    protected Activity mContext;

    public AbsListAdapter(Activity activity){
        mContext=activity;
    }

    @Override
    public int getCount() {
        return mList==null?0:mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList==null?null:mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setList(List<T> list){
        mList=list;
    }

    public void setList(T[] list){
        if(list==null){
            mList=null;
            return;
        }
        mList=new ArrayList<>();
        for(T t:list){
            mList.add(t);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        H holder=null;
        int type=getItemViewType(position);
        if(convertView==null){
            holder=onCreateViewHolder(parent,type);
            convertView=holder.root;
            convertView.setTag(R.id.tag_list_adapter_viewholder,holder);
            convertView.setTag(R.id.tag_list_adapter_viewtype,type);
        }else {
            if(type==((int)convertView.getTag(R.id.tag_list_adapter_viewtype))){
                holder=(H)convertView.getTag();
            }else {
                holder=onCreateViewHolder(parent,type);
                convertView=holder.root;
                convertView.setTag(R.id.tag_list_adapter_viewholder,holder);
                convertView.setTag(R.id.tag_list_adapter_viewtype,type);
            }
        }
        onBindViewHolder(position,holder);
        return convertView;
    }

    public List<T> getList() {
        return mList;
    }

    public abstract void onBindViewHolder(int position , H holder);

    public abstract H onCreateViewHolder(ViewGroup parent, int viewType);

    public static class ViewHolder{
        public View root;
        public ViewHolder(View v){
            root=v;
            ButterKnife.bind(this,v);
        }
    }
}
