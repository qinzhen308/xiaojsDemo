package cn.xiaojs.xma.ui.lesson;
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
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Duration;
import cn.xiaojs.xma.model.EnrolledLesson;
import cn.xiaojs.xma.model.GELessonsResponse;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.classroom.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.Constants;
import cn.xiaojs.xma.ui.grade.MaterialActivity;
import cn.xiaojs.xma.ui.view.LessonOperationView;
import cn.xiaojs.xma.ui.view.LessonPersonView;
import cn.xiaojs.xma.ui.view.LessonStatusView;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.ui.widget.LiveProgress;
import cn.xiaojs.xma.util.TimeUtil;

public class
EnrollLessonAdapter extends AbsSwipeAdapter<EnrolledLesson, EnrollLessonAdapter.Holder> {
    private Criteria mCriteria;
    private LessonFragment mFragment;
    private Bitmap bitmap;
    private int radius;

    public EnrollLessonAdapter(Context context, PullToRefreshSwipeListView listView, LessonFragment fragment) {
        super(context, listView);
        mFragment = fragment;
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_avatar_grey);
        radius = mContext.getResources().getDimensionPixelSize(R.dimen.px40);
    }

    public EnrollLessonAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_avatar_grey);
        radius = mContext.getResources().getDimensionPixelSize(R.dimen.px40);
    }

    public EnrollLessonAdapter(Context context, PullToRefreshSwipeListView listView, boolean autoLoad) {
        super(context, listView, autoLoad);
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_avatar_grey);
        radius = mContext.getResources().getDimensionPixelSize(R.dimen.px40);
    }

    @Override
    protected void initParam() {
        Duration duration = new Duration();
        duration.setStart(TimeUtil.original());
        duration.setEnd(TimeUtil.yearAfter(10));

        mCriteria = new Criteria();
        mCriteria.setSource(Ctl.LessonSource.ALL);
        mCriteria.setDuration(duration);
    }

    @Override
    protected void setViewContent(Holder holder, final EnrolledLesson bean, int position) {
        holder.reset();
        holder.name.setText(bean.getTitle());
        holder.lessonCount.setText("共1节");
        holder.persons.show(bean);
        //Glide.with(mContext).load(bean.getCover()).error(R.drawable.default_lesson_cover).into(holder.image);
        if (bean.getState().equalsIgnoreCase(LessonState.CANCELLED)) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.show(bean);
            holder.operation.setVisibility(View.GONE);
            holder.operation.setEnterColor(R.color.common_text);
//            String[] items = new String[]{mContext.getString(R.string.data_bank)};
//            holder.operation.setItems(items);
//            holder.operation.enableMore(false);
//            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
//                @Override
//                public void onClick(int position) {
//                    databank(bean);
//                }
//            });
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case ENTER:
                            enterClass(bean);
                            break;
                    }
                }
            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.setEnterColor(R.color.common_text);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.show(bean);
            //String[] items = new String[]{/*mContext.getString(R.string.schedule), */mContext.getString(R.string.data_bank)};
            String[] items = new String[]{" "};
            holder.operation.setItems(items);
            holder.operation.enableMore(false);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1:
                            ////schedule(bean);
                            //databank(bean);
                            break;
                        case 2:
                            //databank(bean);
                            break;
                        case ENTER:
                            enterClass(bean);
                            break;
                    }
                }
            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.LIVE)) {
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.setEnterColor(R.color.font_orange);
            holder.status.setVisibility(View.GONE);
            holder.progressWrapper.setVisibility(View.VISIBLE);
            holder.progress.showTimeBar(bean.getSchedule().getDuration(), bean.getClassroom().finishOn);
            //String[] items = new String[]{mContext.getString(R.string.data_bank)};
            String[] items = new String[]{" "};
            holder.operation.setItems(items);
            holder.operation.enableMore(false);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1:
                            //databank(bean);
                            break;
                        case ENTER:
                            enterClass(bean);
                            break;
                    }
                }
            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)) {
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.setEnterColor(R.color.common_text);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.show(bean);
            //String[] items = new String[]{/*mContext.getString(R.string.schedule), */mContext.getString(R.string.data_bank)};
            String[] items = new String[]{" "};
            holder.operation.setItems(items);
            holder.operation.enableMore(false);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1:
                            ////schedule(bean);

                            //databank(bean);
                            break;
                        case 2:
                            //databank(bean);
                            break;
                        case ENTER:
                            enterClass(bean);
                            break;
                    }
                }
            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.STOPPED)) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.show(bean);
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.setEnterColor(R.color.common_text);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case ENTER:
                            enterClass(bean);
                            break;
                    }
                }
            });
        }
    }

    private void schedule(EnrolledLesson bean) {

    }

    //班级圈
    private void circle(EnrolledLesson bean) {

    }

    //进入教室
    private void enterClass(EnrolledLesson bean) {
        Intent i = new Intent();
        i.putExtra(Constants.KEY_TICKET, bean.getTicket());
        i.setClass(mContext, ClassroomActivity.class);
        mContext.startActivity(i);
    }

    //资料库
    private void databank(EnrolledLesson bean) {
        Intent intent = new Intent(mContext, MaterialActivity.class);
        mContext.startActivity(intent);
    }

    //退课
    private void dropClass(EnrolledLesson bean) {

    }

    //评价
    private void evaluate(EnrolledLesson bean) {

    }

    //更多
    private void more(final EnrolledLesson bean) {

        if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)) {
            String[] items = new String[]{
                    mContext.getString(R.string.data_bank),
                    mContext.getString(R.string.leave_lesson)};
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {
                        case 0:
                            databank(bean);
                            break;
                        case 1:
                            dropClass(bean);
                            break;
                    }
                }
            });
            dialog.show();
        } else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
            String[] items = new String[]{mContext.getString(R.string.evaluate),
                    mContext.getString(R.string.data_bank)};
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {
                        case 0:
                            evaluate(bean);
                            break;
                        case 1:
                            databank(bean);
                            break;
                    }
                }
            });
            dialog.show();
        }
    }

    @Override
    protected View createContentView(int position) {
        View v = mInflater.inflate(R.layout.layout_enroll_lesson_item, null);
        return v;
    }

    @Override
    protected Holder initHolder(View view) {
        return new Holder(view);
    }

    @Override
    protected void onDataItemClick(int position, EnrolledLesson bean) {

        if (bean.getState().equalsIgnoreCase(LessonState.ACKNOWLEDGED)
                || bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_ACK)
                || bean.getState().equalsIgnoreCase(LessonState.DRAFT)
                || bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_APPROVAL)
                || bean.getState().equalsIgnoreCase(LessonState.CANCELLED)) {
            return;
        }

        Intent i = new Intent(mContext, LessonHomeActivity.class);
        i.putExtra(CourseConstant.KEY_ENTRANCE_TYPE, LessonHomeActivity.ENTRANCE_FROM_ENROLL_LESSON);
        i.putExtra(CourseConstant.KEY_LESSON_ID, bean.getId());
        mContext.startActivity(i);

    }

    @Override
    protected void doRequest() {
        LessonDataManager.getEnrolledClasses(mContext, mCriteria, mPagination, new APIServiceCallback<GELessonsResponse>() {
            @Override
            public void onSuccess(GELessonsResponse object) {
                if (object.getObjectsOfPage() != null && object.getObjectsOfPage().size() > 0) {
                    if (mFragment != null) {
                        mFragment.hideTop();
                    }
                }
                EnrollLessonAdapter.this.onSuccess(object.getObjectsOfPage());
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                EnrollLessonAdapter.this.onFailure(errorCode, errorMessage);
            }
        });

    }

    @Override
    protected void onDataEmpty() {
        if (mFragment != null) {
            mFragment.showTop();
        }
    }

    @Override
    protected void onDataFailed() {
        if (mFragment != null) {
            mFragment.showTop();
        }
    }

    @Override
    protected void onEmptyButtonClick() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(MainActivity.KEY_POSITION, 1);
        mContext.startActivity(intent);
    }

    public void request(Criteria criteria) {
        mCriteria = criteria;
        reset();
        notifyDataSetChanged();
        request();
    }

    static class Holder extends BaseHolder {
        @BindView(R.id.enroll_lesson_item_name)
        TextView name;
        @BindView(R.id.enroll_lesson_item_lessons)
        TextView lessonCount;

        @BindView(R.id.enroll_lesson_item_persons)
        LessonPersonView persons;

        @BindView(R.id.progress_wrapper)
        View progressWrapper;
        @BindView(R.id.enroll_lesson_item_cur_name)
        TextView progressName;
        @BindView(R.id.enroll_lesson_item_progress)
        LiveProgress progress;

        @BindView(R.id.enroll_lesson_item_status)
        LessonStatusView status;
        @BindView(R.id.enroll_lesson_item_opera)
        LessonOperationView operation;

        public void reset() {
            status.setVisibility(View.GONE);
            progressWrapper.setVisibility(View.GONE);
        }

        public Holder(View view) {
            super(view);
        }
    }
}
