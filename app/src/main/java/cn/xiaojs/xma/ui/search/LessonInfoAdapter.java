package cn.xiaojs.xma.ui.search;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.SearchManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPageData;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.search.LessonInfo;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.util.TimeUtil;

/**
 * Created by maxiaobao on 2017/3/31.
 */

public class LessonInfoAdapter extends SearchAdapter<LessonInfo, LessonInfoAdapter.Holder> {

    private Dimension dimension;


    public LessonInfoAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);

        dimension = new Dimension();
        dimension.width = CourseConstant.COURSE_COVER_WIDTH;
        dimension.height = CourseConstant.COURSE_COVER_HEIGHT;
    }


    @Override
    protected void setViewContent(Holder holder, LessonInfo bean, int position) {

        LessonInfo lessonInfo = getItem(position);
        holder.nameView.setText(lessonInfo.title);

        Schedule schedule = lessonInfo.schedule;
        String timestr = TimeUtil.format(schedule.getStart(), TimeUtil.TIME_YYYY_MM_DD_HH_MM);
        holder.timeView.setText(timestr);

        //TODO 课程免费或者付费解析、课所有者解析

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

    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_search_lesson_item, null);
        return view;
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void onDataItemClick(int position, LessonInfo bean) {
        if (bean != null) {
            SearchBusiness.goLessonHome(mContext, bean);
        }
    }

    @Override
    protected void doRequest() {

        SearchManager.searchLessons(mContext, keyWord, mPagination, Account.TypeName.STAND_ALONE_LESSON, new APIServiceCallback<CollectionPageData<LessonInfo>>() {
            @Override
            public void onSuccess(CollectionPageData<LessonInfo> object) {

                if (object !=null && object.lessons != null) {
                    LessonInfoAdapter.this.onSuccess(object.lessons);
                }else{
                    LessonInfoAdapter.this.onSuccess(null);
                }

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                LessonInfoAdapter.this.onFailure(errorCode,errorCode);
            }
        });

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
