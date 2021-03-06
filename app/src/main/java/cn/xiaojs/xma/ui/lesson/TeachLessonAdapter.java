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
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Duration;
import cn.xiaojs.xma.model.GetLessonsResponse;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.model.Teacher;
import cn.xiaojs.xma.model.account.DealAck;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.grade.ClassMaterialActivity;
import cn.xiaojs.xma.ui.grade.GradeHomeActivity;
import cn.xiaojs.xma.ui.view.LessonOperationView;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.ui.widget.LiveProgress;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.NumberUtil;
import cn.xiaojs.xma.util.ShareUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.ToastUtil;

public class TeachLessonAdapter extends AbsSwipeAdapter<TeachLesson, TeachLessonAdapter.Holder> {
    private Criteria mCriteria;
    private LessonFragment mFragment;


    public TeachLessonAdapter(Context context, PullToRefreshSwipeListView listView, LessonFragment fragment) {
        super(context, listView);
        mFragment = fragment;
    }

    public TeachLessonAdapter(Context context, PullToRefreshSwipeListView listView, boolean autoLoad) {
        super(context, listView, autoLoad);
    }

    public TeachLessonAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
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
    protected void setViewContent(Holder holder, final TeachLesson bean, final int pos) {

        holder.reset();
        holder.name.setText(bean.getTitle());
        holder.price.setVisibility(View.VISIBLE);
        if (bean.getFee().free) {
            holder.price.setText(R.string.free);
        } else {
            holder.price.setText(NumberUtil.getPrice(bean.getFee().charge));
        }

        if (bean.getEnroll()!=null && bean.getEnroll().mandatory) {
            holder.enrollDesc.setText(mContext.getString(R.string.course_stu, bean.getEnroll().current, bean.getEnroll().max));
        }else{
            holder.enrollDesc.setText(mContext.getString(R.string.want_to_learn_num,
                    bean.getEnroll() ==null? 0: bean.getEnroll().current));
        }



        holder.time.setText(TimeUtil.getTimeByNow(bean.getSchedule().getStart(), true) + " " + TimeUtil.getTimeFormat(bean.getSchedule().getStart(), bean.getSchedule().getDuration()));
        holder.name.setText(bean.getTitle());
        //Glide.with(mContext).load(bean.getCover()).error(R.drawable.default_lesson_cover).into(holder.image);
        if (TextUtils.isEmpty(bean.getState()))
            return;
        if (bean.getState().equalsIgnoreCase(LessonState.DRAFT)) {
            String[] items = new String[]{mContext.getString(R.string.shelves),
                    mContext.getString(R.string.edit)};
            holder.state.setText(R.string.pending_shelves);
            holder.state.setBackgroundResource(R.drawable.course_state_draft_bg);

            holder.distance.setVisibility(View.VISIBLE);
            holder.distance.setText(TimeUtil.distanceDay(bean.getSchedule().getStart(), true));

            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.enableMore(true);
            holder.operation.enableEnter(false);
            holder.operation.setItems(items);
            holder.operation.setEnterColor(R.color.common_text);
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
                        case MORE:
                            more(pos,bean);
                            break;
                    }
                }
            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.REJECTED)) {
            String[] items = new String[]{mContext.getString(R.string.edit),
                    mContext.getString(R.string.look_detail)};
            holder.state.setText(R.string.examine_failure);
            holder.state.setBackgroundResource(R.drawable.course_state_failure_bg);
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.enableMore(true);
            holder.operation.enableEnter(false);
            holder.operation.setEnterColor(R.color.common_text);
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
                        case MORE://删除
                            more(pos, bean);
                            break;
                    }
                }
            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.CANCELLED)) {
            String[] items = new String[]{mContext.getString(R.string.look_detail)};
            holder.state.setText(R.string.course_state_cancel);
            holder.state.setBackgroundResource(R.drawable.course_state_cancel_bg);
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.enableMore(true);
            holder.operation.enableEnter(false);
            holder.operation.setEnterColor(R.color.common_text);
            holder.operation.setItems(items);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1://查看详情
                            detail(bean);
                            break;
                        case MORE:
                            more(pos, bean);
                            break;
//                        case ENTER:
//                            enterClass(bean);
//                            break;
                    }
                }
            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.STOPPED)) {
            String[] items = new String[]{mContext.getString(R.string.look_detail)};
            holder.state.setText(R.string.force_stop);
            holder.state.setBackgroundResource(R.drawable.course_state_stop_bg);
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.enableMore(true);
            holder.operation.enableEnter(false);
            holder.operation.setEnterColor(R.color.common_text);
            holder.operation.setItems(items);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1://查看详情
                            detail(bean);
                            break;
                        case MORE:
                            more(pos, bean);
                            break;
                    }
                }
            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_ACK)) {
            String[] items = new String[]{"同意", "拒绝"};
            holder.state.setText("待确认");
            holder.state.setBackgroundResource(R.drawable.course_state_examine_bg);
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.enableMore(false);
            holder.operation.enableEnter(false);
            holder.operation.setEnterColor(R.color.common_text);
            holder.operation.setItems(items);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1://同意
                            dealAck(pos, bean, Ctl.ACKDecision.ACKNOWLEDGE);
                            break;
                        case 2://拒绝
                            dealAck(pos, bean, Ctl.ACKDecision.REFUSED);
                            break;
                    }
                }
            });

        } else if (bean.getState().equalsIgnoreCase(LessonState.ACKNOWLEDGED)) {
            holder.state.setText("已确认");
            holder.state.setBackgroundResource(R.drawable.course_state_ackledged_bg);
            holder.operation.setVisibility(View.GONE);
            holder.operation.enableMore(false);
            holder.operation.enableEnter(false);

        } else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_APPROVAL)) {
            String[] items = new String[]{mContext.getString(R.string.cancel_examine),
                    mContext.getString(R.string.look_detail)};
            holder.state.setText(R.string.examining);
            holder.state.setBackgroundResource(R.drawable.course_state_examine_bg);
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.enableMore(false);
            holder.operation.enableEnter(false);
            holder.operation.setEnterColor(R.color.common_text);
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
//            List<Bitmap> l = new ArrayList<>();
//            l.add(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_ad));
//            holder.assistants.show(l);
//            String[] items = new String[]{/*mContext.getString(R.string.prepare_lesson),*/
//                    mContext.getString(R.string.class_home)};
            String[] items = new String[]{mContext.getString(R.string.data_bank)};

//            if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
//                items[0] = mContext.getString(R.string.lesson_again);
//            }
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.enableMore(true);
            holder.operation.enableEnter(true);
//            if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
//                holder.operation.hiddenDiver();
//                holder.operation.hiddenOpera123();
//                holder.operation.setItems(null);
//            } else {
                holder.operation.setItems(items);
            //}

            //holder.operation.hiddenDiver();
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1:
                            //detail(bean);
//                            if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
//                                lessonAgain(bean);
//                            } else {
//                                prepare(bean);
//                            }
                            databank(bean);
                            break;
//                        case 2://班级主页
//                            home(bean);
//                            break;
                        case MORE:
                            more(pos,bean);
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
                holder.operation.setEnterColor(R.color.common_text);
                holder.distance.setVisibility(View.VISIBLE);
                holder.distance.setText(TimeUtil.distanceDay(bean.getSchedule().getStart(), true));

            } else if (bean.getState().equalsIgnoreCase(LessonState.LIVE)) {
                holder.state.setText(R.string.living);
                holder.state.setBackgroundResource(R.drawable.course_state_on_bg);
                holder.operation.setEnterColor(R.color.font_orange);
                holder.progressWrapper.setVisibility(View.VISIBLE);
                holder.progress.showTimeBar(bean.getClassroom(), bean.getSchedule().getDuration());
            } else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
                holder.state.setVisibility(View.GONE);
                holder.operation.setEnterColor(R.color.common_text);
                holder.end.setVisibility(View.VISIBLE);
            }
        }else{
            holder.operation.enableMore(false);
        }

        if (LessonState.PENDING_FOR_LIVE.equalsIgnoreCase(bean.getState())
                || LessonState.DRAFT.equalsIgnoreCase(bean.getState())) {

        }

        //设置助教，目前最多显示3个助教
        Teacher[] assistants = bean.getAssistants();
        if (assistants != null && assistants.length > 0) {
            holder.assistantsWrapper.setVisibility(View.VISIBLE);
            int size = mContext.getResources().getDimensionPixelSize(R.dimen.px40);
            int margin = mContext.getResources().getDimensionPixelSize(R.dimen.px10);
            int i = 0;
            holder.assistants.removeAllViews();
            for (Teacher teacher : assistants) {
                String url = cn.xiaojs.xma.common.xf_foundation.schemas.Account.getAvatar(teacher.getId(), 40);
                RoundedImageView roundImgView = new RoundedImageView(mContext);
                roundImgView.setImageResource(R.drawable.default_avatar_grey);
                roundImgView.setOval(true);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                params.rightMargin = margin;
                holder.assistants.addView(roundImgView, params);
                Glide.with(mContext)
                        .load(url)
                        .error(R.drawable.default_avatar_grey)
                        .into(roundImgView);
                if (++i > 2) {
                    break;
                }
            }
        } else {
            holder.assistantsWrapper.setVisibility(View.GONE);
        }
    }

    //上架
    private void shelves(final TeachLesson bean) {
        showProgress(false);
        LessonDataManager.requestPutLessonOnShelves(mContext, bean.getId(), new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();

                //如果是已经实名认证的用户开的课，上架成功后，自动通过，不需要审核
                if (AccountDataManager.isVerified(mContext)) {

                    if (TextUtils.isEmpty(bean.getTicket())) {
                        //如果没有ticket，需要重新调用接口，刷新课的信息。
                        request(mCriteria);
                    }else {
                        bean.setState(LessonState.PENDING_FOR_LIVE);
                        ToastUtil.showToast(mContext, R.string.shelves_ok);

                        notifyData(bean);
                    }

                }else {
                    bean.setState(LessonState.PENDING_FOR_APPROVAL);
                    ToastUtil.showToast(mContext, R.string.shelves_need_examine);

                    notifyData(bean);
                }



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
                        notifyData(bean);
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

    //同意或者拒绝
    private void dealAck(final int position, final TeachLesson bean, final int descion) {

        DealAck ack = new DealAck();
        ack.decision = descion;

        showProgress(true);
        LessonDataManager.acknowledgeLesson(mContext, bean.getId(), ack, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();
                if (descion == Ctl.ACKDecision.ACKNOWLEDGE) {
                    bean.setState(LessonState.ACKNOWLEDGED);
                    Toast.makeText(mContext, "您已同意", Toast.LENGTH_SHORT).show();
                    notifyData(bean);
                } else {
                    Toast.makeText(mContext, "您已拒绝", Toast.LENGTH_SHORT).show();
                    getList().remove(position);
                    notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
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

    //资料库
    private void databank(TeachLesson bean) {
        Intent intent = new Intent(mContext, ClassMaterialActivity.class);
        intent.putExtra(ClassMaterialActivity.EXTRA_DELETEABLE,true);
        intent.putExtra(ClassMaterialActivity.EXTRA_ID, bean.getId());
        intent.putExtra(ClassMaterialActivity.EXTRA_TITLE, bean.getTitle());
        intent.putExtra(ClassMaterialActivity.EXTRA_SUBTYPE, Collaboration.SubType.STANDA_LONE_LESSON);
        mContext.startActivity(intent);
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
    private void delete(final int pos,final TeachLesson bean) {
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
                dialog.cancel();
                hideLesson(pos, bean);
            }
        });
        dialog.show();
    }

    private void hideLesson(final int pos, final TeachLesson bean) {
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


    //备课
    private void prepare(TeachLesson bean) {

    }

    //班级主页
    private void home(TeachLesson bean) {
        Intent intent = new Intent(mContext, GradeHomeActivity.class);
        //mContext.startActivity(intent);
    }

    //分享
    private void share(TeachLesson bean) {

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

                dialog.dismiss();

                showProgress(true);
                LessonDataManager.requestToggleAccessLesson(mContext, bean.getId(), false, new APIServiceCallback() {
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
    private void more(final int pos, final TeachLesson bean) {

        boolean createMe = false;
        if (bean.getCreatedBy() !=null
                && !TextUtils.isEmpty(bean.getCreatedBy().getId())
                && bean.getCreatedBy().getId().equals(AccountDataManager.getAccountID(mContext))) {
            createMe = true;
        }


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
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            if (createMe) {
                if (!bean.getEnroll().mandatory) {
                    String[] items = new String[]{
                            mContext.getString(R.string.share),
                            mContext.getString(R.string.look_detail),
                            mContext.getString(publishId),
                    /*mContext.getString(R.string.modify_lesson_time),*/
                            mContext.getString(R.string.cancel_lesson)};

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
//                        case 4://修改上课时间
//                            modifyLesson(bean);
//                            break;
                                case 4://取消上课
                                    cancelLesson(bean);
                                    break;
                            }
                        }
                    });
                }else {
                    String[] items = new String[]{
                            mContext.getString(R.string.registration),
                            mContext.getString(R.string.share),
                            mContext.getString(R.string.look_detail),
                            mContext.getString(publishId),
                    /*mContext.getString(R.string.modify_lesson_time),*/
                            mContext.getString(R.string.cancel_lesson)};

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
//                        case 4://修改上课时间
//                            modifyLesson(bean);
//                            break;
                                case 4://取消上课
                                    cancelLesson(bean);
                                    break;
                            }
                        }
                    });
                }

            }else {
                String[] items = new String[]{mContext.getString(R.string.share)};

                dialog.setItems(items);
                dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position) {
                            case 0://报名注册
                                share(bean);
                                break;
                        }
                    }
                });
            }

            dialog.show();
        } else if (bean.getState().equalsIgnoreCase(LessonState.LIVE)) {
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            if (createMe) {

                if (!bean.getEnroll().mandatory) {
                    String[] items = new String[]{
                            mContext.getString(R.string.share),
                            mContext.getString(R.string.look_detail),
                            mContext.getString(publishId)};

                    dialog.setItems(items);
                    dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                        @Override
                        public void onItemClick(int position) {
                            switch (position) {
                                case 0:
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
                }else{
                    String[] items = new String[]{
                            mContext.getString(R.string.registration),
                            mContext.getString(R.string.share),
                            mContext.getString(R.string.look_detail),
                            mContext.getString(publishId)};

                    dialog.setItems(items);
                    dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                        @Override
                        public void onItemClick(int position) {
                            switch (position) {
                                case 0://报名注册
                                    registration(bean);
                                    break;
                                case 1:
                                    share(bean);
                                    break;
                                case 2://查看详情
                                    detail(bean);
                                    break;
                                case 3://发布到主页
                                    publish(bean);
                                    break;
                            }
                        }
                    });
                }


            }else {
                String[] items = new String[]{mContext.getString(R.string.share)};

                dialog.setItems(items);
                dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position) {
                            case 0://分享
                                share(bean);
                                break;
                        }
                    }
                });
            }

            dialog.show();
        } else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            if (createMe) {
                String[] items = new String[]{
                        //mContext.getString(R.string.prepare_lesson),
                        mContext.getString(R.string.share),
                        //mContext.getString(R.string.look_detail),
                        mContext.getString(publishId),
                        mContext.getString(R.string.lesson_again)};

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
//                        case 1://查看详情
//                            detail(bean);
//                            break;
                            case 1://发布到主页
                                publish(bean);
                                break;
                            case 2://再次开课
                                lessonAgain(bean);
                                break;
                        }
                    }
                });
            }else {
                String[] items = new String[]{mContext.getString(R.string.share)};

                dialog.setItems(items);
                dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position) {
                            case 0://分享
                                share(bean);
                                break;
                        }
                    }
                });
            }

            dialog.show();

        } else if (bean.getState().equalsIgnoreCase(LessonState.DRAFT)) {
            ListBottomDialog dialog = new ListBottomDialog(mContext);
                String[] items = new String[]{
                        mContext.getString(R.string.look_detail),
                        mContext.getString(R.string.delete)};

                dialog.setItems(items);
                dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position) {
                            case 0://查看详情
                                detail(bean);
                                break;
                            case 1:
                                delete(pos,bean);
                                break;
                        }
                    }
                });
            dialog.show();
        }else if (bean.getState().equalsIgnoreCase(LessonState.CANCELLED)
                || bean.getState().equalsIgnoreCase(LessonState.STOPPED)
                || bean.getState().equalsIgnoreCase(LessonState.REJECTED)) {
            String[] items = new String[]{mContext.getString(R.string.delete)};
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {
                        case 0://删除
                            delete(pos, bean);
                            break;
                    }
                }
            });
            dialog.show();
        }

    }

    @Override
    protected View createContentView(int position) {
        View v = mInflater.inflate(R.layout.layout_teach_lesson_item, null);
        return v;
    }

    @Override
    protected Holder initHolder(View view) {
        return new Holder(view);
    }

    @Override
    protected void onDataItemClick(int position, TeachLesson bean) {
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
        i.putExtra(CourseConstant.KEY_ENTRANCE_TYPE, LessonHomeActivity.ENTRANCE_FROM_TEACH_LESSON);
        i.putExtra(CourseConstant.KEY_LESSON_ID, bean.getId());
        mContext.startActivity(i);


    }

    @Override
    protected void doRequest() {
        LessonDataManager.getClasses(mContext, mCriteria, mPagination, new APIServiceCallback<GetLessonsResponse>() {
            @Override
            public void onSuccess(GetLessonsResponse object) {
                if (object.getObjectsOfPage() != null && object.getObjectsOfPage().size() > 0) {
                    if (mFragment != null) {
                        mFragment.hideTop();
                    }
                }


                TeachLessonAdapter.this.onSuccess(object.getObjectsOfPage());

                if (mContext instanceof TeachLessonActivity) {
                    TeachLessonActivity activity = ((TeachLessonActivity) mContext);
                    if (activity.timePosition > 0 || activity.statePosition > 0) {
                        setDescNow("没有符合该筛选条件的课～");
                        setIconNow(R.drawable.ic_data_empty);
                        setButtonVisible(View.GONE);
                    } else {
                        setDescNow("你还没开过课哦～");
                        setIconNow(R.drawable.ic_teach_empty);
                        setButtonVisible(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                TeachLessonAdapter.this.onFailure(errorCode, errorMessage);
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

        //if (AccountDataManager.isTeacher(mContext)) {
            //老师可以开课
            Intent intent = new Intent(mContext, LessonCreationActivity.class);
            ((BaseActivity) mContext).startActivityForResult(intent, CourseConstant.CODE_CREATE_LESSON);
//        } else {
//            //提示申明教学能力
//            final CommonDialog dialog = new CommonDialog(mContext);
//            dialog.setTitle(R.string.declare_teaching_ability);
//            dialog.setDesc(R.string.declare_teaching_ability_tip);
//            dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
//                @Override
//                public void onClick() {
//                    dialog.dismiss();
//                    Intent intent = new Intent(mContext, TeachingSubjectActivity.class);
//                    mContext.startActivity(intent);
//                }
//            });
//            dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
//                @Override
//                public void onClick() {
//                    dialog.dismiss();
//                }
//            });
//            dialog.show();
//        }
    }

    private void notifyData(TeachLesson bean) {
        boolean changed = false;
        for (int i = 0; i < mBeanList.size(); i++) {
            TeachLesson b = mBeanList.get(i);
            if (b.getId().equalsIgnoreCase(bean.getId())) {
                mBeanList.set(i, bean);
                changed = true;
                break;
            }
        }
        if (changed) {
            notifyDataSetChanged();
        }
    }

    private void deleteItem(TeachLesson bean) {
        boolean changed = false;
        for (int i = 0; i < mBeanList.size(); i++) {
            TeachLesson b = mBeanList.get(i);
            if (b.getId().equalsIgnoreCase(bean.getId())) {
                removeItem(i);
                changed = true;
                break;
            }
        }
        if (changed) {
            notifyDataSetChanged();
        }
    }

    public void request(Criteria criteria) {
        mCriteria = criteria;
        reset();
        notifyDataSetChanged();
        request();
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
        LinearLayout assistants;

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
