package com.benyuan.xiaojs.ui.course;
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
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.pulltorefresh.AbsSwipeAdapter;
import com.benyuan.xiaojs.common.pulltorefresh.BaseHolder;
import com.benyuan.xiaojs.common.xf_foundation.LessonState;
import com.benyuan.xiaojs.common.xf_foundation.schemas.Ctl;
import com.benyuan.xiaojs.data.LessonDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.Criteria;
import com.benyuan.xiaojs.model.Duration;
import com.benyuan.xiaojs.model.GetLessonsResponse;
import com.benyuan.xiaojs.model.TeachLesson;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.widget.CommonDialog;
import com.benyuan.xiaojs.ui.widget.ListBottomDialog;
import com.benyuan.xiaojs.ui.widget.RedTipImageView;
import com.benyuan.xiaojs.ui.widget.RedTipTextView;
import com.benyuan.xiaojs.util.NumberUtil;
import com.benyuan.xiaojs.util.TimeUtil;
import com.benyuan.xiaojs.util.ToastUtil;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;
import com.orhanobut.logger.Logger;

import butterknife.BindView;

public class TeachLessonAdapter extends AbsSwipeAdapter<TeachLesson, TeachLessonAdapter.Holder> {
    private Criteria mCriteria;
    private LessonFragment mFragment;

    public TeachLessonAdapter(Context context, AutoPullToRefreshListView listView, LessonFragment fragment) {
        super(context, listView);
        mFragment = fragment;
    }

    public TeachLessonAdapter(Context context, AutoPullToRefreshListView listView) {
        super(context, listView);
    }

    @Override
    protected void initParam(){
        Duration duration = new Duration();
        duration.setStart(TimeUtil.original());
        duration.setEnd(TimeUtil.now());

        mCriteria = new Criteria();
        mCriteria.setSource(Ctl.LessonSource.ALL);
        mCriteria.setDuration(duration);
    }

    @Override
    protected void setViewContent(Holder holder,final TeachLesson bean, int position) {
        holder.reset();
        holder.name.setText(bean.getTitle());
        String state = LessonBusiness.getStateByPosition(position,true);
        if (!TextUtils.isEmpty(state)){
            bean.setState(state);
        }
        holder.price.setVisibility(View.VISIBLE);
        if (bean.getFee().isFree()){
            holder.price.setText(R.string.free);
        }else {
            holder.price.setText(NumberUtil.getPrice(bean.getFee().getCharge().doubleValue()));
        }
        holder.desc.setText(mContext.getString(R.string.course_stu,bean.getEnroll().getCurrent(),bean.getEnroll().getMax()));

        holder.time.setText(TimeUtil.getTimeByNow(bean.getSchedule().getStart()) + " " + TimeUtil.getTimeFormat(bean.getSchedule().getStart(),bean.getSchedule().getDuration()));

        if (bean.getState().equalsIgnoreCase(LessonState.DRAFT)){
            holder.clsFunction.setVisibility(View.VISIBLE);
            holder.circle.setText(R.string.shelves);
            holder.enter.setText(R.string.edit);
            holder.circle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shelves(bean);
                }
            });
            holder.enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edit(bean);
                }
            });
            holder.more.setVisibility(View.VISIBLE);
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    more(bean);
                }
            });
            holder.courseState.setText(R.string.pending_shelves);
            holder.courseState.setBackgroundResource(R.drawable.course_state_draft_bg);
        }else if (bean.getState().equalsIgnoreCase(LessonState.REJECTED)){
            holder.error.setVisibility(View.VISIBLE);
            holder.error.setText(R.string.course_examine_error_tip);
            holder.more.setVisibility(View.VISIBLE);
            holder.more.setOnClickListener(new View.OnClickListener() {//更多
                @Override
                public void onClick(View view) {
                    more(bean);
                }
            });
            holder.courseState.setText(R.string.examine_failure);
            holder.courseState.setBackgroundResource(R.drawable.course_state_failure_bg);
        }else if (bean.getState().equalsIgnoreCase(LessonState.CANCELLED)){
            holder.operaFunction.setVisibility(View.VISIBLE);
            holder.detail.setOnClickListener(new View.OnClickListener() {//查看详情
                @Override
                public void onClick(View view) {
                    detail(bean);
                }
            });
            holder.courseState.setText(R.string.course_state_cancel);
            holder.courseState.setBackgroundResource(R.drawable.course_state_cancel_bg);
        }else if (bean.getState().equalsIgnoreCase(LessonState.STOPPED)){
            holder.error.setVisibility(View.VISIBLE);
            holder.error.setText(R.string.course_stop_error_tip);
            holder.more.setVisibility(View.VISIBLE);
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    more(bean);
                }
            });
            holder.courseState.setText(R.string.force_stop);
            holder.courseState.setBackgroundResource(R.drawable.course_state_stop_bg);
        }else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_APPROVAL)){
            holder.more.setVisibility(View.VISIBLE);
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    more(bean);
                }
            });
            holder.courseState.setText(R.string.examining);
            holder.courseState.setBackgroundResource(R.drawable.course_state_examine_bg);
        }else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)
                || bean.getState().equalsIgnoreCase(LessonState.LIVE)
                || bean.getState().equalsIgnoreCase(LessonState.FINISHED)){
            holder.more.setVisibility(View.VISIBLE);
            holder.clsFunction.setVisibility(View.VISIBLE);
            holder.circle.setText(R.string.cls_moment);
            holder.enter.setText(R.string.into_cls);
            holder.circle.setOnClickListener(new View.OnClickListener() {//班级圈
                @Override
                public void onClick(View view) {
                    circle(bean);
                }
            });
            holder.enter.setOnClickListener(new View.OnClickListener() {//进入教室
                @Override
                public void onClick(View view) {
                    enterClass(bean);
                }
            });
            holder.more.setOnClickListener(new View.OnClickListener() {//更多
                @Override
                public void onClick(View view) {
                    more(bean);
                }
            });

            if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)){
                holder.courseState.setText(R.string.pending_for_course);
                holder.courseState.setBackgroundResource(R.drawable.course_state_wait_bg);
            }else if (bean.getState().equalsIgnoreCase(LessonState.LIVE)){
                holder.courseState.setText(R.string.living);
                holder.courseState.setBackgroundResource(R.drawable.course_state_on_bg);
            }else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)){
                holder.courseState.setText(R.string.course_state_end);
                holder.courseState.setBackgroundResource(R.drawable.course_state_end_bg);
            }
        }
    }

    //上架
    private void shelves(final TeachLesson bean){
        LessonDataManager.requestPutLessonOnShelves(mContext, bean.getId(), new APIServiceCallback<GetLessonsResponse>() {
            @Override
            public void onSuccess(GetLessonsResponse object) {
                bean.setState(LessonState.PENDING_FOR_APPROVAL);
                notifyData(bean);
                ToastUtil.showToast(mContext,R.string.shelves_need_examine);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(mContext,errorMessage);
            }
        });
    }

    //编辑
    private void edit(TeachLesson bean){
        Intent intent = new Intent(mContext,LessonCreationActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_ID, bean.getId());
        intent.putExtra(CourseConstant.KEY_TEACH_ACTION_TYPE,CourseConstant.TYPE_LESSON_EDIT);
        ((BaseActivity)mContext).startActivityForResult(intent,CourseConstant.CODE_EDIT_LESSON);
    }

    //撤销审核，取消上架
    private void offShelves(final TeachLesson bean){
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
                LessonDataManager.requestCancelLessonOnShelves(mContext, bean.getId(), new APIServiceCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        bean.setState(LessonState.DRAFT);
                        notifyData(bean);
                        ToastUtil.showToast(mContext,R.string.off_shelves_success);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        ToastUtil.showToast(mContext,errorMessage);
                    }
                });
            }
        });

        dialog.show();

    }

    //查看详情
    private void detail(TeachLesson bean){
        Intent intent = new Intent(mContext,LiveLessonDetailActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN,bean);
        mContext.startActivity(intent);
    }

    //班级圈
    private void circle(TeachLesson bean){
        //modifyLesson(bean);
    }

    //进入教室
    private void enterClass(TeachLesson bean){

    }

    private void modifyLesson(TeachLesson bean){
        Intent intent = new Intent(mContext,ModifyLessonActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN,bean);
        ((BaseActivity)mContext).startActivityForResult(intent,CourseConstant.CODE_EDIT_LESSON);
    }
    //取消上课
    private void cancelLesson(TeachLesson bean){
        Intent intent = new Intent(mContext,CancelLessonActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN,bean);
        ((BaseActivity)mContext).startActivityForResult(intent,CourseConstant.CODE_CANCEL_LESSON);
    }

    //删除
    private void delete(TeachLesson bean){

    }
    //备课
    private void prepare(TeachLesson bean){

    }

    //分享
    private void share(TeachLesson bean){

    }

    //报名注册
    private void registration(TeachLesson bean){

    }

    //发布到主页
    private void publish(TeachLesson bean){

    }

    //取消发布
    private void cancelPublish(TeachLesson bean){

    }

    //再次开课
    private void lessonAgain(TeachLesson bean){
        Intent intent = new Intent(mContext,LessonCreationActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_ID, bean.getId());
        intent.putExtra(CourseConstant.KEY_TEACH_ACTION_TYPE,CourseConstant.TYPE_LESSON_AGAIN);
        ((BaseActivity)mContext).startActivityForResult(intent,CourseConstant.CODE_LESSON_AGAIN);
    }

    //更多
    private void more(final TeachLesson bean){

            if (bean.getState().equalsIgnoreCase(LessonState.DRAFT)){
                String[] items = new String[]{
                        mContext.getString(R.string.look_detail),
                        mContext.getString(R.string.delete)};
                ListBottomDialog dialog = new ListBottomDialog(mContext);
                dialog.setItems(items);
                dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position){
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
            }else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_APPROVAL)){
                String[] items = new String[]{mContext.getString(R.string.cancel_examine),
                        mContext.getString(R.string.look_detail)};
                ListBottomDialog dialog = new ListBottomDialog(mContext);
                dialog.setItems(items);
                dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position){
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
            } else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)){
                String[] items = new String[]{
                        mContext.getString(R.string.prepare_lesson),
                        mContext.getString(R.string.registration),
                mContext.getString(R.string.share),
                mContext.getString(R.string.look_detail),
                        mContext.getString(R.string.publish_to_home_page),
                mContext.getString(R.string.modify_lesson_time),
                mContext.getString(R.string.cancel_lesson),
                mContext.getString(R.string.cancel_publish)};
                ListBottomDialog dialog = new ListBottomDialog(mContext);
                dialog.setItems(items);
                dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position){
                            case 0://备课
                                prepare(bean);
                                break;
                            case 1://报名注册
                                registration(bean);
                                break;
                            case 2://分享
                                share(bean);
                                break;
                            case 3://查看详情
                                detail(bean);
                                break;
                            case 4://发布到主页
                                publish(bean);
                                break;
                            case 5://修改上课时间
                                modifyLesson(bean);
                                break;
                            case 6://取消上课
                                cancelLesson(bean);
                                break;
                            case 7://取消发布
                                cancelPublish(bean);
                                break;

                        }
                    }
                });
                dialog.show();
            }else if (bean.getState().equalsIgnoreCase(LessonState.LIVE)){
                String[] items = new String[]{
                        mContext.getString(R.string.share),
                        mContext.getString(R.string.look_detail),
                        mContext.getString(R.string.publish_to_home_page),
                        mContext.getString(R.string.cancel_publish)};
                ListBottomDialog dialog = new ListBottomDialog(mContext);
                dialog.setItems(items);
                dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position){
                            case 0://分享
                                share(bean);
                                break;
                            case 1://查看详情
                                detail(bean);
                                break;
                            case 2://发布到主页
                                publish(bean);
                                break;
                            case 3://取消发布
                                cancelPublish(bean);
                                break;
                        }
                    }
                });
                dialog.show();
            }else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)){
                String[] items = new String[]{
                        mContext.getString(R.string.lesson_again),
                        mContext.getString(R.string.prepare_lesson),
                        mContext.getString(R.string.share),
                        mContext.getString(R.string.look_detail),
                        mContext.getString(R.string.publish_to_home_page),
                        mContext.getString(R.string.cancel_publish)};
                ListBottomDialog dialog = new ListBottomDialog(mContext);
                dialog.setItems(items);
                dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position){
                            case 0://再次开课
                                lessonAgain(bean);
                                break;
                            case 1://备课
                                prepare(bean);
                                break;
                            case 2://分享
                                share(bean);
                                break;
                            case 3://查看详情
                                detail(bean);
                                break;
                            case 4://发布到主页
                                publish(bean);
                                break;
                            case 5://取消发布
                                cancelPublish(bean);
                                break;
                        }
                    }
                });
                dialog.show();
            }else if (bean.getState().equalsIgnoreCase(LessonState.REJECTED)){
                String[] items = new String[]{
                        mContext.getString(R.string.edit),
                        mContext.getString(R.string.look_detail),
                        mContext.getString(R.string.delete)};
                ListBottomDialog dialog = new ListBottomDialog(mContext);
                dialog.setItems(items);
                dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position){
                            case 0://编辑
                                edit(bean);
                                break;
                            case 1://查看详情
                                detail(bean);
                                break;
                            case 2://删除
                                delete(bean);
                                break;
                        }
                    }
                });
                dialog.show();
            }else if (bean.getState().equalsIgnoreCase(LessonState.STOPPED)){
                String[] items = new String[]{
                        mContext.getString(R.string.look_detail),
                        mContext.getString(R.string.delete)};
                ListBottomDialog dialog = new ListBottomDialog(mContext);
                dialog.setItems(items);
                dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position){
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

     }

    @Override
    protected View createContentView(int position) {
        View v = mInflater.inflate(R.layout.layout_my_course, null);
        return v;
    }

    @Override
    protected Holder initHolder(View view) {
        return new Holder(view);
    }

    @Override
    protected void onDataItemClick(int position,TeachLesson bean) {
        Toast.makeText(mContext,"position = " + position,Toast.LENGTH_SHORT).show();
        Intent i = new Intent(mContext, LessonHomeActivity.class);
        i.putExtra(CourseConstant.KEY_ENTRANCE_TYPE, LessonHomeActivity.ENTRANCE_FROM_TEACH_LESSON);
        i.putExtra(CourseConstant.KEY_LESSON_ID, bean.getId());
        mContext.startActivity(i);

    }

    @Override
    protected void doRequest() {
        LessonDataManager.requestGetLessons(mContext, mCriteria, mPagination, new APIServiceCallback<GetLessonsResponse>() {
            @Override
            public void onSuccess(GetLessonsResponse object) {
                Logger.d("onSuccess-----------");
                if (object.getObjectsOfPage() != null && object.getObjectsOfPage().size() > 0){
                    if (mFragment != null){
                        mFragment.hideTop();
                    }
                }
                TeachLessonAdapter.this.onSuccess(object.getObjectsOfPage());
            }

            @Override
            public void onFailure(String errorCode,String errorMessage) {
                TeachLessonAdapter.this.onFailure(errorCode,errorMessage);
                Logger.d("onFailure-----------");
            }
        });

    }

    @Override
    protected void onDataEmpty() {
        if (mFragment != null){
            mFragment.showTop();
        }
    }

    private void notifyData(TeachLesson bean){
        boolean changed = false;
        for (int i = 0;i<mBeanList.size();i++){
            TeachLesson b = mBeanList.get(i);
            if (b.getId().equalsIgnoreCase(bean.getId())){
                mBeanList.set(i,bean);
                changed = true;
                break;
            }
        }
        if (changed){
            notifyDataSetChanged();
        }
    }

    private void deleteItem(TeachLesson bean){
        boolean changed = false;
        for (int i = 0;i<mBeanList.size();i++){
            TeachLesson b = mBeanList.get(i);
            if (b.getId().equalsIgnoreCase(bean.getId())){
                removeItem(i);
                changed = true;
                break;
            }
        }
        if (changed){
            notifyDataSetChanged();
        }
    }

    public void request(Criteria criteria){
        mCriteria = criteria;
        mPagination.setPage(PAGE_FIRST);
        mClearItems = true;
        doRequest();
    }

    static class Holder extends BaseHolder {
        @BindView(R.id.course_item_image)
        ImageView image;
        @BindView(R.id.course_xbs_state)
        TextView xbsState;
        @BindView(R.id.course_name)
        TextView name;
        @BindView(R.id.course_desc)
        TextView desc;//用户小头像为40x40
        @BindView(R.id.course_state)
        TextView courseState;
        @BindView(R.id.time_mark)
        TextView time;
        @BindView(R.id.course_price)
        TextView price;

        @BindView(R.id.course_item_fun_circle)
        RedTipTextView circle;
        @BindView(R.id.course_item_fun_enter)
        RedTipTextView enter;

        @BindView(R.id.course_item_fun_shelves)
        TextView shelves;
        @BindView(R.id.course_item_fun_edit)
        TextView edit;
        @BindView(R.id.course_item_fun_cancel)
        TextView cancel;
        @BindView(R.id.course_item_fun_detail)
        TextView detail;

        @BindView(R.id.course_item_fun_error)
        TextView error;

        @BindView(R.id.course_item_fun_more)
        RedTipImageView more;
        @BindView(R.id.course_item_fun_data_base)
        RedTipImageView databank;

        @BindView(R.id.course_item_fun_cls)
        LinearLayout clsFunction;
        @BindView(R.id.course_item_fun_opera)
        LinearLayout operaFunction;

        @BindView(R.id.course_item_fun_shelves_line)
        View shelvesLine;
        @BindView(R.id.course_item_fun_edit_line)
        View editLine;
        @BindView(R.id.course_item_fun_cancel_line)
        View cancelLine;


        public void reset(){
            clsFunction.setVisibility(View.GONE);
            operaFunction.setVisibility(View.GONE);
            error.setVisibility(View.INVISIBLE);
            more.setVisibility(View.GONE);
            databank.setVisibility(View.GONE);
            shelves.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            shelvesLine.setVisibility(View.GONE);
            editLine.setVisibility(View.GONE);
            cancelLine.setVisibility(View.GONE);
            price.setVisibility(View.GONE);
        }

        public Holder(View view) {
            super(view);
        }
    }
}
