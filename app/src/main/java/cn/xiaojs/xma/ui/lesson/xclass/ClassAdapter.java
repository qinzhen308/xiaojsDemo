package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.List;

import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.model.ctl.PrivateClass;
import cn.xiaojs.xma.ui.lesson.TeachLessonAdapter;
import cn.xiaojs.xma.ui.lesson.xclass.util.IDialogMethod;
import cn.xiaojs.xma.ui.lesson.xclass.view.ClassView;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.util.JudgementUtil;

/**
 * Created by maxiaobao on 2017/5/23.
 */

public class ClassAdapter extends AbsSwipeAdapter<PrivateClass,ClassAdapter.Holder> {

    public ClassAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
    }

    @Override
    protected void setViewContent(Holder holder, PrivateClass bean, int position) {
        if(holder.root instanceof IViewModel){
            ((IViewModel) holder.root).bindData(bean);
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

    @Override
    protected void doRequest() {
//        LessonDataManager.getClassesSchedule4Class();
        onSuccess(null);
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
