package cn.xiaojs.xma.ui.live;
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
 * Date:2016/12/9
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.classroom.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.Constants;
import cn.xiaojs.xma.ui.grade.GradeHomeActivity;
import cn.xiaojs.xma.ui.lesson.CancelLessonActivity;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonBusiness;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.LiveLessonDetailActivity;
import cn.xiaojs.xma.ui.lesson.ModifyLessonActivity;
import cn.xiaojs.xma.ui.view.LessonOperationView;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.ui.widget.LiveProgress;
import cn.xiaojs.xma.ui.widget.flow.ImageFlowLayout;
import cn.xiaojs.xma.util.NumberUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.ToastUtil;

public class LiveTeachLessonAdapter extends CanInScrollviewListView.Adapter {
//    private final int MAX = 2;
    private Context mContext;
    private List<TeachLesson> lessons;

    public LiveTeachLessonAdapter(Context context,List<TeachLesson> lessons){
        mContext = context;
        this.lessons = lessons;
    }
    @Override
    public int getCount() {
        if (lessons == null)
            return 0;
//        if (lessons.size() > MAX){
//            return MAX;
//        }
        return lessons.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_teach_lesson_item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.reset();
        final TeachLesson bean = lessons.get(position);
        holder.name.setText(bean.getTitle());
        holder.price.setVisibility(View.VISIBLE);
        if (bean.getFee().free) {
            holder.price.setText(R.string.free);
        } else {
            holder.price.setText(NumberUtil.getPrice(bean.getFee().charge));
        }
        holder.enrollDesc.setText(mContext.getString(R.string.course_stu, bean.getEnroll().current, bean.getEnroll().max));

        holder.time.setText(TimeUtil.getTimeByNow(bean.getSchedule().getStart()) + " " + TimeUtil.getTimeFormat(bean.getSchedule().getStart(), bean.getSchedule().getDuration()));
        holder.name.setText(bean.getTitle());
        //Glide.with(mContext).load(bean.getCover()).error(R.drawable.default_lesson_cover).into(holder.image);
        if (TextUtils.isEmpty(bean.getState()))
            return convertView;
        if (bean.getState().equalsIgnoreCase(LessonState.DRAFT)) {
            String[] items = new String[]{mContext.getString(R.string.shelves),
                    mContext.getString(R.string.edit),
                    mContext.getString(R.string.look_detail)};
            holder.state.setText(R.string.pending_shelves);
            holder.state.setBackgroundResource(R.drawable.course_state_draft_bg);
            holder.operation.enableMore(false);
            holder.operation.enableEnter(false);
            holder.operation.setItems(items);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1://上架
                            shelves(bean);
                            break;
                        case 2://编辑
                            edit(bean);
                            break;
                        case 3://查看详情
                            detail(bean);
                            break;
                    }
                }
            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.REJECTED)) {
            holder.assistants.setVisibility(View.VISIBLE);
            String[] items = new String[]{mContext.getString(R.string.edit),
                    mContext.getString(R.string.look_detail),
                    mContext.getString(R.string.delete)};
            holder.state.setText(R.string.examine_failure);
            holder.state.setBackgroundResource(R.drawable.course_state_failure_bg);
            holder.operation.enableMore(false);
            holder.operation.enableEnter(false);
            holder.operation.setItems(items);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1://编辑
                            edit(bean);
                            break;
                        case 2://查看详情
                            detail(bean);
                            break;
                        case 3://删除
                            delete(bean);
                            break;
                    }
                }
            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.CANCELLED)) {
            String[] items = new String[]{mContext.getString(R.string.look_detail),
                    mContext.getString(R.string.delete)};
            holder.state.setText(R.string.course_state_cancel);
            holder.state.setBackgroundResource(R.drawable.course_state_cancel_bg);
            holder.operation.enableMore(false);
            holder.operation.enableEnter(false);
            holder.operation.setItems(items);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1://查看详情
                            detail(bean);
                            break;
                        case 2://删除
                            delete(bean);
                            break;
                    }
                }
            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.STOPPED)) {
            String[] items = new String[]{mContext.getString(R.string.edit),
                    mContext.getString(R.string.look_detail),
                    mContext.getString(R.string.delete)};
            holder.state.setText(R.string.force_stop);
            holder.state.setBackgroundResource(R.drawable.course_state_stop_bg);
            holder.operation.enableMore(false);
            holder.operation.enableEnter(false);
            holder.operation.setItems(items);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1://编辑
                            edit(bean);
                            break;
                        case 2://查看详情
                            detail(bean);
                            break;
                        case 3://删除
                            delete(bean);
                            break;
                    }
                }
            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_APPROVAL)) {
            holder.assistants.setVisibility(View.VISIBLE);
            String[] items = new String[]{mContext.getString(R.string.cancel_examine),
                    mContext.getString(R.string.look_detail)};
            holder.state.setText(R.string.examining);
            holder.operation.enableEnter(false);
            holder.state.setBackgroundResource(R.drawable.course_state_examine_bg);
            holder.operation.enableMore(false);
            holder.operation.setItems(items);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1://撤销审核
                            offShelves(bean);
                            break;
                        case 2://查看详情
                            detail(bean);
                            break;
                    }
                }
            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)
                || bean.getState().equalsIgnoreCase(LessonState.LIVE)
                || bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
            holder.assistants.setVisibility(View.VISIBLE);
//            List<Bitmap> l = new ArrayList<>();
//            l.add(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_ad));
//            holder.assistants.show(l);
            String[] items = new String[]{/*mContext.getString(R.string.prepare_lesson),*/
                    mContext.getString(R.string.class_home)};
//            if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
//                items[0] = mContext.getString(R.string.lesson_again);
//            }
            holder.operation.enableMore(true);
            holder.operation.enableEnter(true);
            holder.operation.setItems(items);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1://备课
//                            if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
//                                lessonAgain(bean);
//                            } else {
//                                prepare(bean);
//                            }
                            home(bean);
                            break;
                        case 2://班级主页
                            home(bean);
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
            if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)) {
                holder.state.setText(R.string.pending_for_course);
                holder.state.setBackgroundResource(R.drawable.course_state_wait_bg);
            } else if (bean.getState().equalsIgnoreCase(LessonState.LIVE)) {
                holder.state.setText(R.string.living);
                holder.state.setBackgroundResource(R.drawable.course_state_on_bg);
                holder.progressWrapper.setVisibility(View.VISIBLE);
            } else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
                holder.state.setVisibility(View.GONE);
                holder.end.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }
    //上架
    private void shelves(final TeachLesson bean) {
        showProgress(false);
        LessonDataManager.requestPutLessonOnShelves(mContext, bean.getId(), new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();
                bean.setState(LessonState.PENDING_FOR_APPROVAL);
                //notifyData(bean);
                ToastUtil.showToast(mContext, R.string.shelves_need_examine);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                ToastUtil.showToast(mContext, errorMessage);
            }
        });
    }

    //编辑
    private void edit(TeachLesson bean) {
        Intent intent = new Intent(mContext, LessonCreationActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_ID, bean.getId());
        intent.putExtra(CourseConstant.KEY_TEACH_ACTION_TYPE, CourseConstant.TYPE_LESSON_EDIT);
        ((BaseActivity) mContext).startActivityForResult(intent, CourseConstant.CODE_EDIT_LESSON);
    }

    //撤销审核，取消上架
    private void offShelves(final TeachLesson bean) {
        final CommonDialog dialog = new CommonDialog(mContext);
        dialog.setTitle(R.string.cancel_examine);
        dialog.setDesc(R.string.cancel_examine_tip);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
            }
        });

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
                showProgress(false);
                LessonDataManager.requestCancelLessonOnShelves(mContext, bean.getId(), new APIServiceCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        cancelProgress();
                        bean.setState(LessonState.DRAFT);
                        //notifyData(bean);
                        ToastUtil.showToast(mContext, R.string.off_shelves_success);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        cancelProgress();
                        ToastUtil.showToast(mContext, errorMessage);
                    }
                });
            }
        });

        dialog.show();

    }

    //查看详情
    private void detail(TeachLesson bean) {
        Intent intent = new Intent(mContext, LiveLessonDetailActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN, bean);
        mContext.startActivity(intent);
    }

    //班级圈
    private void circle(TeachLesson bean) {
        //modifyLesson(bean);
    }

    //进入教室
    private void enterClass(TeachLesson bean) {
        Intent i = new Intent();
        i.putExtra(Constants.KEY_TICKET, bean.getTicket());
        i.setClass(mContext, ClassroomActivity.class);
        mContext.startActivity(i);
    }

    private void modifyLesson(TeachLesson bean) {
        Intent intent = new Intent(mContext, ModifyLessonActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN, bean);
        ((BaseActivity) mContext).startActivityForResult(intent, CourseConstant.CODE_EDIT_LESSON);
    }

    //取消上课
    private void cancelLesson(TeachLesson bean) {
        Intent intent = new Intent(mContext, CancelLessonActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN, bean);
        ((BaseActivity) mContext).startActivityForResult(intent, CourseConstant.CODE_CANCEL_LESSON);
    }

    //删除
    private void delete(TeachLesson bean) {
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

            }
        });
        dialog.show();
    }

    //备课
    private void prepare(TeachLesson bean) {

    }

    //班级主页
    private void home(TeachLesson bean) {
        Intent intent = new Intent(mContext, GradeHomeActivity.class);
        mContext.startActivity(intent);
    }

    //分享
    private void share(TeachLesson bean) {

    }

    //报名注册
    private void registration(TeachLesson bean) {
        Schedule schedule = bean.getSchedule();
        long start = (schedule != null && schedule.getStart() != null) ? schedule.getStart().getTime() : 0;
        LessonBusiness.enterEnrollRegisterPage(mContext,
                bean.getId(),
                bean.getCover(),
                bean.getTitle(),
                start,
                schedule != null ? schedule.getDuration() : 0);
    }

    //发布到主页
    private void publish(final TeachLesson bean) {
        if (bean.getPublish().accessible) {
            cancelPublish(bean);
            return;
        }
        showProgress(true);
        LessonDataManager.requestToggleAccessLesson(mContext, bean.getId(), true, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();
                bean.getPublish().accessible = true;
                ToastUtil.showToast(mContext, R.string.lesson_publish_tip);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                ToastUtil.showToast(mContext, errorMessage);
            }
        });
    }

    //取消发布
    private void cancelPublish(final TeachLesson bean) {
        final CommonDialog dialog = new CommonDialog(mContext);
        dialog.setTitle(R.string.cancel_publish);
        dialog.setDesc(R.string.cancel_publish_tip);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
            }
        });
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                showProgress(true);
                LessonDataManager.requestToggleAccessLesson(mContext, bean.getId(), true, new APIServiceCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        cancelProgress();
                        bean.getPublish().accessible = false;
                        ToastUtil.showToast(mContext, R.string.course_state_cancel);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        cancelProgress();
                        ToastUtil.showToast(mContext, errorMessage);
                    }
                });
            }
        });
        dialog.show();
    }

    //再次开课
    private void lessonAgain(TeachLesson bean) {
        Intent intent = new Intent(mContext, LessonCreationActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_ID, bean.getId());
        intent.putExtra(CourseConstant.KEY_TEACH_ACTION_TYPE, CourseConstant.TYPE_LESSON_AGAIN);
        ((BaseActivity) mContext).startActivityForResult(intent, CourseConstant.CODE_LESSON_AGAIN);
    }

    //更多
    private void more(final TeachLesson bean) {

        int publishId = R.string.publish_to_home_page;
        if (bean.getPublish().accessible) {
            publishId = R.string.cancel_publish;
        }
        /*if (bean.getState().equalsIgnoreCase(LessonState.DRAFT)) {
            String[] items = new String[]{
                    mContext.getString(R.string.look_detail),
                    mContext.getString(R.string.edit)};
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {
                        case 0://上架
                            detail(bean);
                            break;
                        case 1://编辑
                            delete(bean);
                            break;
                    }
                }
            });
            dialog.show();
        } else*/ /*if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_APPROVAL)) {
            String[] items = new String[]{mContext.getString(R.string.cancel_examine),
                    mContext.getString(R.string.look_detail)};
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {
                        case 0://撤销审核
                            offShelves(bean);
                            break;
                        case 1://查看详情
                            detail(bean);
                            break;
                    }
                }
            });
            dialog.show();
        } else*/
        if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)) {
            String[] items = new String[]{
                    mContext.getString(R.string.registration),
                    mContext.getString(R.string.share),
                    mContext.getString(R.string.look_detail),
                    mContext.getString(publishId),
                    mContext.getString(R.string.modify_lesson_time),
                    mContext.getString(R.string.cancel_lesson)};
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {
                        case 0://报名注册
                            registration(bean);
                            break;
                        case 1://分享
                            share(bean);
                            break;
                        case 2://查看详情
                            detail(bean);
                            break;
                        case 3://发布到主页
                            publish(bean);
                            break;
                        case 4://修改上课时间
                            modifyLesson(bean);
                            break;
                        case 5://取消上课
                            cancelLesson(bean);
                            break;
                    }
                }
            });
            dialog.show();
        } else if (bean.getState().equalsIgnoreCase(LessonState.LIVE)) {
            String[] items = new String[]{
                    mContext.getString(R.string.share),
                    mContext.getString(R.string.look_detail),
                    mContext.getString(publishId)};
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {
                        case 0://分享
                            share(bean);
                            break;
                        case 1://查看详情
                            detail(bean);
                            break;
                        case 2://发布到主页
                            publish(bean);
                            break;
                    }
                }
            });
            dialog.show();
        } else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
            String[] items = new String[]{
                    //mContext.getString(R.string.prepare_lesson),
                    mContext.getString(R.string.share),
                    mContext.getString(R.string.look_detail),
                    mContext.getString(publishId)};
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {
                        //case 0://备课
                        //    prepare(bean);
                        //    break;
                        case 0://分享
                            share(bean);
                            break;
                        case 1://查看详情
                            detail(bean);
                            break;
                        case 2://发布到主页
                            publish(bean);
                            break;
                    }
                }
            });
            dialog.show();
        } /*else if (bean.getState().equalsIgnoreCase(LessonState.REJECTED)) {
            String[] items = new String[]{
                    mContext.getString(R.string.look_detail),
                    mContext.getString(R.string.delete)};
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {
                        case 0://查看详情
                            detail(bean);
                            break;
                        case 1://删除
                            delete(bean);
                            break;
                    }
                }
            });
            dialog.show();
        } else if (bean.getState().equalsIgnoreCase(LessonState.STOPPED)) {
            String[] items = new String[]{
                    mContext.getString(R.string.look_detail),
                    mContext.getString(R.string.delete)};
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {
                        case 0://查看详情
                            detail(bean);
                            break;
                        case 1://删除
                            delete(bean);
                            break;
                    }
                }
            });
            dialog.show();
        }
*/
    }

    private void showProgress(boolean b){
        ((BaseActivity)mContext).showProgress(b);
    }

    private void cancelProgress(){
        ((BaseActivity)mContext).cancelProgress();
    }

    static class Holder extends BaseHolder {

        @BindView(R.id.teach_lesson_item_name)
        TextView name;
        @BindView(R.id.teach_lesson_state)
        TextView state;
        @BindView(R.id.teach_lesson_item_time)
        TextView time;
        @BindView(R.id.teach_lesson_open_distance)
        TextView distance;
        @BindView(R.id.teach_lesson_item_price)
        TextView price;
        @BindView(R.id.teach_lesson_item_enroll_desc)
        TextView enrollDesc;

        @BindView(R.id.assistants)
        ImageFlowLayout assistants;

        @BindView(R.id.teach_lesson_opera)
        LessonOperationView operation;

        @BindView(R.id.teach_lesson_item_end)
        ImageView end;

        @BindView(R.id.teach_lesson_item_assistants)
        View assistantsWrapper;

        @BindView(R.id.progress_wrapper)
        View progressWrapper;
        @BindView(R.id.progress)
        LiveProgress progress;

        public void reset() {
            assistantsWrapper.setVisibility(View.GONE);
            end.setVisibility(View.GONE);
            distance.setVisibility(View.GONE);
            state.setVisibility(View.VISIBLE);
            progressWrapper.setVisibility(View.GONE);
        }

        public Holder(View view) {
            super(view);
        }
    }
}
