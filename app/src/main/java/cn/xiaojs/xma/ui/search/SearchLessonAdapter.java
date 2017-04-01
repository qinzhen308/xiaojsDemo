package cn.xiaojs.xma.ui.search;
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
 * Date:2016/12/21
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.stickylistheaders.StickyListHeadersAdapter;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.search.AccountSearch;
import cn.xiaojs.xma.model.search.LessonInfo;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;
import cn.xiaojs.xma.ui.widget.EvaluationStar;
import cn.xiaojs.xma.util.TimeUtil;

public class SearchLessonAdapter extends CanInScrollviewListView.Adapter {

    private int MAX_COUNT = 0;

    private List<LessonInfo> mBeans;
    private Context mContext;
    private Dimension dimension;

    public SearchLessonAdapter(Context context, List<LessonInfo> beans,int max) {
        mContext = context;
        mBeans = beans;
        MAX_COUNT = max;

        dimension = new Dimension();
        dimension.width = CourseConstant.COURSE_COVER_WIDTH;
        dimension.height = CourseConstant.COURSE_COVER_HEIGHT;

    }

    public SearchLessonAdapter(Context context, List<LessonInfo> beans) {
        mContext = context;
        mBeans = beans;

        dimension = new Dimension();
        dimension.width = CourseConstant.COURSE_COVER_WIDTH;
        dimension.height = CourseConstant.COURSE_COVER_HEIGHT;
    }

    public SearchLessonAdapter(Context context) {
        mContext = context;
        dimension = new Dimension();
        dimension.width = CourseConstant.COURSE_COVER_WIDTH;
        dimension.height = CourseConstant.COURSE_COVER_HEIGHT;
    }

    @Override
    public int getCount() {
        if (MAX_COUNT > 0){
            int count = 0;
            if (mBeans != null) {
                if (mBeans.size() > MAX_COUNT) {//最多显示3个
                    count = MAX_COUNT;
                } else {
                    count = mBeans.size();
                }
            }
            return count;
        }
        return mBeans == null ? 0 : mBeans.size();
    }

    @Override
    public LessonInfo getItem(int position) {
        if (mBeans != null) {
            return mBeans.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_search_lesson_item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        LessonInfo lessonInfo = getItem(position);
        holder.nameView.setText(lessonInfo.title);

        Schedule schedule = lessonInfo.schedule;
        String timestr = TimeUtil.format(schedule.getStart(), TimeUtil.TIME_YYYY_MM_DD_HH_MM);
        holder.timeView.setText(timestr);

//        String cover = lessonInfo.cover;
//        if (TextUtils.isEmpty(cover)) {
//            holder.coverView.setImageResource(R.drawable.default_lesson_cover);
//        }else {
//
//            String coverurl = Ctl.getCover(cover,dimension);
//            Glide.with(mContext)
//                    .load(coverurl)
//                    .placeholder(R.drawable.default_lesson_cover)
//                    .error(R.drawable.default_lesson_cover)
//                    .into(holder.coverView);
//        }

        return convertView;
    }

    public void setData(List<LessonInfo> accounts){
        mBeans = accounts;
    }

    public List<LessonInfo> getData(){
        return mBeans;
    }

    class Holder extends BaseHolder {
//        @BindView(R.id.search_lesson_image)
//        ImageView coverView;
//        @BindView(R.id.search_lesson_state)
//        TextView stateView;
        @BindView(R.id.search_lesson_name)
        TextView nameView;
        @BindView(R.id.search_lesson_time)
        TextView timeView;
//        @BindView(R.id.search_lesson_teacher_head)
//        ImageView teacherIconView;
//        @BindView(R.id.search_lesson_teacher_name)
//        TextView teacherNameView;
        @BindView(R.id.search_lesson_price)
        TextView priceView;

        public Holder(View view) {
            super(view);
        }
    }
}
