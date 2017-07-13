package cn.xiaojs.xma.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.model.search.SearchResultV2;
import cn.xiaojs.xma.ui.lesson.xclass.model.LastEmptyModel;
import cn.xiaojs.xma.ui.lesson.xclass.model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.RecyclerViewScrollHelper;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.search.view.SRClassView;
import cn.xiaojs.xma.ui.search.view.SRLiveLessonView;
import cn.xiaojs.xma.ui.search.view.SROrganizationView;
import cn.xiaojs.xma.ui.search.view.SRPersonView;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/5/22.
 * 设计思路：
 * 可以使所有列表都用这个adapter，作为一个只做 "视图模型--数据模型或指定类型" 的适配器。
 * 具体bind逻辑在视图模型中通过实现IViewMode来处理。
 * 还可以考虑实现插入其他adapter，从而对本适配器进行扩展 (未实现，后续思路)
 *
 */
public class CommonRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final int VIEW_TYPE_SEARCH_RESULT_PERSON =1;
    public static final int VIEW_TYPE_SEARCH_RESULT_CLASS =2;
    public static final int VIEW_TYPE_SEARCH_RESULT_ORGANIZATION =3;
    public static final int VIEW_TYPE_SEARCH_RESULT_LIVE_LESSON=4;

    public static final int VIEW_TYPE_LAST_EMPTY=100;

    private List<?> mList;
    private RecyclerView mRecyclerView;
    RecyclerViewScrollHelper scrollHelper;

    public CommonRVAdapter(RecyclerView recyclerView){
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
        if(viewType== VIEW_TYPE_SEARCH_RESULT_PERSON){
            holder=new CommonHolder(new SRPersonView(parent.getContext()));
        }else if(viewType==VIEW_TYPE_SEARCH_RESULT_CLASS){
            holder=new CommonHolder(new SRClassView(parent.getContext()));
        }else if(viewType== VIEW_TYPE_SEARCH_RESULT_ORGANIZATION){
            holder=new CommonHolder(new SROrganizationView(parent.getContext()));
        }else if(viewType== VIEW_TYPE_SEARCH_RESULT_LIVE_LESSON){
            holder=new CommonHolder(new SRLiveLessonView(parent.getContext()));
        }else {
            View v=new View(parent.getContext());
            v.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,200));
            holder=new LastEmpterHolder(v);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder.itemView instanceof IViewModel){
            ((IViewModel) holder.itemView).bindData(position,getItem(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object o=getItem(position);
        if(o instanceof SearchResultV2){
            int viewtype=-1;
            switch (((SearchResultV2)o).getType()){
                case Collaboration.SubType.PERSON:
                    viewtype=VIEW_TYPE_SEARCH_RESULT_PERSON;
                    break;
                case Collaboration.SubType.ORGANIZATION:
                    viewtype=VIEW_TYPE_SEARCH_RESULT_ORGANIZATION;
                    break;
                case Collaboration.SubType.PRIVATE_CLASS:
                    viewtype=VIEW_TYPE_SEARCH_RESULT_CLASS;
                    break;
                case Collaboration.SubType.STANDA_LONE_LESSON:
                    viewtype=VIEW_TYPE_SEARCH_RESULT_LIVE_LESSON;
                    break;
            }
            return viewtype;
        }else if(o instanceof LastEmptyModel){
            return VIEW_TYPE_LAST_EMPTY;
        }else if(o instanceof LessonLabelModel){
            return VIEW_TYPE_SEARCH_RESULT_PERSON;
        }
        return -1;
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
                    break;
                }
            }
        }
        if(index>=0){
//            mRecyclerView.smoothScrollToPosition(index);
            scrollHelper.smoothMoveToPosition(mRecyclerView,index);
        }
    }

    /**
     * 跳转到 指定位置
     * @param position
     */
    public void scrollToPosition(int position){
        if(position>=0&&getItemCount()>0){
            scrollHelper.smoothMoveToPosition(mRecyclerView,position);
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
