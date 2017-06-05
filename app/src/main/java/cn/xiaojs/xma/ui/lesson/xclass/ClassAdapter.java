package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.orhanobut.logger.Logger;

import java.util.Date;
import java.util.Map;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionResult;
import cn.xiaojs.xma.model.ctl.PrivateClass;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.lesson.xclass.view.ClassView;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.util.JudgementUtil;

/**
 * Created by maxiaobao on 2017/5/23.
 */

public class ClassAdapter extends AbsSwipeAdapter<PrivateClass,ClassAdapter.Holder> {
    String state="All";
    String startTime;
    String endTime;
    String key="";
    boolean firstNotLoad=true;

    public ClassAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
        startTime=ScheduleUtil.getUTCDate(new Date(0).getTime());
        endTime=ScheduleUtil.getUTCDate((new Date().getTime()+365*24*60*60*1000));
    }

    @Override
    protected void setViewContent(Holder holder, PrivateClass bean, int position) {
        if(holder.root instanceof IViewModel){
            ((IViewModel) holder.root).bindData(position,bean);
        }
    }

    @Override
    protected View createContentView(int position) {
        return new ClassView(mContext);
    }

    @Override
    protected Holder initHolder(View view) {
        return new Holder(view);
    }


    public void setTime(String start,String end){
        this.startTime=start;
        this.endTime=end;
        if(XiaojsConfig.DEBUG){
            Logger.d("------qz-----class--start Time="+startTime+"----end Time="+endTime);
        }
    }

    public void setKeyword(String keyword){
        key=keyword;
    }

    public void setState(String state){
        this.state=state;
    }

    @Override
    protected void doRequest() {
        Map map=LessonDataManager.createScheduleOptions(null,null,null,startTime, endTime,null,Account.TypeName.CLASS, state,null,key);
        LessonDataManager.getClassesSchedule4Class(mContext, map, mPagination, new APIServiceCallback<CollectionResult<PrivateClass>>() {
            @Override
            public void onSuccess(CollectionResult<PrivateClass> object) {
                if(object!=null){
                    ClassAdapter.this.onSuccess(object.results);
                }else {
                    ClassAdapter.this.onSuccess(null);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ClassAdapter.this.onFailure(errorCode,errorMessage);
            }
        });
    }

    static class Holder extends BaseHolder {

        public Holder(View view) {
            super(view);
        }
    }

    @Override
    protected void onEmptyButtonClick() {
        if(JudgementUtil.checkTeachingAbility(mContext)){
            mContext.startActivity(new Intent(mContext, CreateClassActivity.class));
        }
    }

}
