package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Context;
import android.view.View;

import java.util.List;

import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.ui.lesson.TeachLessonAdapter;

/**
 * Created by maxiaobao on 2017/5/23.
 */

public class ClassAdapter extends AbsSwipeAdapter<TeachLesson,ClassAdapter.Holder> {

    public ClassAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
    }

    @Override
    protected void setViewContent(Holder holder, TeachLesson bean, int position) {

    }

    @Override
    protected View createContentView(int position) {
        return null;
    }

    @Override
    protected Holder initHolder(View view) {
        return null;
    }

    @Override
    protected void doRequest() {
        onSuccess(null);
    }

    static class Holder extends BaseHolder {

        public Holder(View view) {
            super(view);
        }
    }

}
