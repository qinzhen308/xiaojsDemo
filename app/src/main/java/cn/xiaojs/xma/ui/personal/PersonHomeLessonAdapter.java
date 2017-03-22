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
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.model.PersonHomeLesson;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonHomeActivity;
import cn.xiaojs.xma.util.NumberUtil;
import cn.xiaojs.xma.util.TimeUtil;

public class PersonHomeLessonAdapter extends AbsSwipeAdapter<PersonHomeLesson, PersonHomeLessonAdapter.Holder> {
    public PersonHomeLessonAdapter(Context context, PullToRefreshSwipeListView list) {
        super(context, list);
    }

    public PersonHomeLessonAdapter(Context context, PullToRefreshSwipeListView list, List<PersonHomeLesson> data) {
        super(context, list, data);
    }

    @Override
    protected void setViewContent(Holder holder, PersonHomeLesson bean, int position) {
        holder.title.setText(bean.title);
        Dimension dimension = new Dimension();
        dimension.width = CourseConstant.COURSE_COVER_WIDTH;
        dimension.height = CourseConstant.COURSE_COVER_HEIGHT;
        Glide.with(mContext)
                .load(Ctl.getCover(bean.cover, dimension))
                .placeholder(R.drawable.default_lesson_cover)
                .error(R.drawable.default_lesson_cover)
                .into(holder.image);
        String time = TimeUtil.format(bean.schedule.getStart(), TimeUtil.TIME_YYYY_MM_DD_HH_MM) + "  " + bean.schedule.getDuration() + "分钟";
        holder.time.setText(time);
        holder.enroll.setText(bean.enroll.current + "人报名");
        if (bean.fee.free) {
            holder.price.setText(R.string.free);
        } else {
            holder.price.setText(NumberUtil.getPrice(bean.fee.charge));
        }
    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_person_home_lesson_item, null);
        return view;
    }

    @Override
    protected void onDataItemClick(int position, PersonHomeLesson bean) {
        Intent i = new Intent(mContext, LessonHomeActivity.class);
        i.putExtra(CourseConstant.KEY_ENTRANCE_TYPE, LessonHomeActivity.ENTRANCE_FROM_TEACH_LESSON);
        i.putExtra(CourseConstant.KEY_LESSON_ID, bean.id);
        mContext.startActivity(i);
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest() {
//        List<PersonHomeLesson> list = new ArrayList<>();
//        list.add(new PersonHomeLesson());
//        list.add(new PersonHomeLesson());
//        list.add(new PersonHomeLesson());
//        list.add(new PersonHomeLesson());
//        list.add(new PersonHomeLesson());
//        list.add(new PersonHomeLesson());
//        list.add(new PersonHomeLesson());
//        list.add(new PersonHomeLesson());
//        list.add(new PersonHomeLesson());
//        list.add(new PersonHomeLesson());
//
//        onSuccess(list);
    }

    class Holder extends BaseHolder {

        @BindView(R.id.lesson_adapter_name)
        TextView title;
        @BindView(R.id.lesson_adapter_image)
        ImageView image;
        @BindView(R.id.lesson_adapter_time)
        TextView time;
        @BindView(R.id.lesson_adapter_enroll)
        TextView enroll;
        @BindView(R.id.lesson_adapter_charge)
        TextView price;

        public Holder(View view) {
            super(view);
        }
    }
}
