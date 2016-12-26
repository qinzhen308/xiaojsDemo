package cn.xiaojs.xma.ui.personal;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/12/13
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.course.LessonBusiness;

public class PersonHomeLessonAdapter extends AbsSwipeAdapter<LessonBusiness,PersonHomeLessonAdapter.Holder> {
    public PersonHomeLessonAdapter(Context context, PullToRefreshSwipeListView list, boolean isNeedPreLoading) {
        super(context, list, isNeedPreLoading);
    }

    public PersonHomeLessonAdapter(Context context, PullToRefreshSwipeListView list) {
        super(context, list);
    }

    @Override
    protected void setViewContent(Holder holder, LessonBusiness bean, int position) {
        holder.title.setText("重要的化妆防晒手册 " + position);
    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_person_home_lesson_item, null);
        return view;
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest() {
        List<LessonBusiness> list = new ArrayList<>();
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());

        onSuccess(list);
    }

    class Holder extends BaseHolder {

        @BindView(R.id.lesson_adapter_name)
        TextView title;
        public Holder(View view) {
            super(view);
        }
    }
}
