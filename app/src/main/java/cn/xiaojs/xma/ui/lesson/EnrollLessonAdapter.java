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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Duration;
import cn.xiaojs.xma.model.EnrolledLesson;
import cn.xiaojs.xma.model.GELessonsResponse;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.grade.ClassMaterialActivity;
import cn.xiaojs.xma.ui.view.LessonOperationView;
import cn.xiaojs.xma.ui.view.LessonPersonView;
import cn.xiaojs.xma.ui.view.LessonStatusView;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.ui.widget.LiveProgress;
import cn.xiaojs.xma.util.ShareUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.ToastUtil;

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
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.setEnterColor(R.color.common_text);
            String[] items = new String[]{mContext.getString(R.string.delete)};
            holder.operation.setItems(items);
            holder.operation.enableMore(false);
            holder.operation.enableEnter(false);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1:
                            delete(position, bean);
                            break;
//                        case ENTER:
//                            enterClass(bean);
//                            break;
                    }
                }
            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.setEnterColor(R.color.common_text);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.show(bean);
            //String[] items = new String[]{/*mContext.getString(R.string.schedule), */mContext.getString(R.string.data_bank)};
            String[] items = new String[]{mContext.getString(R.string.data_bank)};
            holder.operation.enableMore(true);
            holder.operation.setItems(items);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1:
                            ////schedule(bean);
                            databank(bean);
                            break;
                        case 2:
                            //databank(bean);
                            break;
                        case MORE:
                            more(bean);
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
            holder.progress.showTimeBar(bean.getClassroom(), bean.getSchedule().getDuration());
            //String[] items = new String[]{mContext.getString(R.string.data_bank)};
            String[] items = new String[]{mContext.getString(R.string.data_bank)};
            holder.operation.enableMore(true);
            holder.operation.setItems(items);

            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1:
                            databank(bean);
                            break;
                        case MORE:
                            more(bean);
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
            String[] items = new String[]{mContext.getString(R.string.data_bank)};
            holder.operation.enableMore(true);
            holder.operation.setItems(items);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1:
                            ////schedule(bean);

                            databank(bean);
                            break;
                        case MORE:
                            more(bean);
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
            String[] items = new String[]{mContext.getString(R.string.delete)};
            holder.operation.setItems(items);
            holder.operation.enableMore(false);
            holder.operation.enableEnter(false);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1:
                            delete(position, bean);
                            break;
                    }
                }
            });
        }
    }

    //删除
    private void delete(final int pos, final EnrolledLesson bean) {
        final CommonDialog dialog = new CommonDialog(mContext);
        dialog.setTitle(R.string.delete);
        dialog.setDesc(R.string.delete_lesson_tip);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
            }
        });
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                hideLesson(pos, bean);
            }
        });
        dialog.show();
    }

    private void hideLesson(final int pos, final EnrolledLesson bean) {
        showProgress(false);
        LessonDataManager.hideLesson(mContext, bean.getId(), new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();
                removeItem(pos);
                ToastUtil.showToast(mContext, R.string.delete_success);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
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
        Intent intent = new Intent(mContext, ClassMaterialActivity.class);
        intent.putExtra(ClassMaterialActivity.EXTRA_ID, bean.getId());
        intent.putExtra(ClassMaterialActivity.EXTRA_TITLE, bean.getTitle());
        intent.putExtra(ClassMaterialActivity.EXTRA_SUBTYPE, Collaboration.SubType.STANDA_LONE_LESSON);
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

        if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)
                || bean.getState().equalsIgnoreCase(LessonState.FINISHED)
                || bean.getState().equalsIgnoreCase(LessonState.LIVE)) {
            String[] items = new String[]{
                    mContext.getString(R.string.share)};
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {
                        case 0:
                            share(bean);
                            break;
//                        case 1:
//                            dropClass(bean);
//                            break;
                    }
                }
            });
            dialog.show();
        }
//        } else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
//            String[] items = new String[]{mContext.getString(R.string.share)};
//            ListBottomDialog dialog = new ListBottomDialog(mContext);
//            dialog.setItems(items);
//            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
//                @Override
//                public void onItemClick(int position) {
//                    switch (position) {
//                        case 0://share
//                            share(bean);
//                            break;
////                        case 1:
////                            databank(bean);
////                            break;
//                    }
//                }
//            });
//            dialog.show();
//        }
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
        String state = bean.getState();
        if (TextUtils.isEmpty(state)
                || state.equalsIgnoreCase(LessonState.ACKNOWLEDGED)
                || state.equalsIgnoreCase(LessonState.PENDING_FOR_ACK)
                || state.equalsIgnoreCase(LessonState.DRAFT)
                || state.equalsIgnoreCase(LessonState.PENDING_FOR_APPROVAL)
                || state.equalsIgnoreCase(LessonState.CANCELLED)
                || state.equalsIgnoreCase(LessonState.REJECTED)) {
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
        intent.putExtra(MainActivity.KEY_POSITION, 0);
        mContext.startActivity(intent);
    }

    public void request(Criteria criteria) {
        mCriteria = criteria;
        reset();
        notifyDataSetChanged();
        request();
    }

    //分享
    private void share(EnrolledLesson bean) {

        if (bean == null) return;

        String startTime = TimeUtil.format(bean.getSchedule().getStart().getTime(),
                TimeUtil.TIME_YYYY_MM_DD_HH_MM);

        String name = "";
        if (bean.getTeacher() != null && bean.getTeacher().getBasic() != null) {
            name = bean.getTeacher().getBasic().getName();
        }

        String shareUrl = ApiManager.getShareLessonUrl(bean.getId(), Account.TypeName.STAND_ALONE_LESSON);

        ShareUtil.shareUrlByUmeng((Activity) mContext, bean.getTitle(), new StringBuilder(startTime).append("\r\n").append(name).toString(), shareUrl);
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
