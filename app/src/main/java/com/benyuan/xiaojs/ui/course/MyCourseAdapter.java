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
import android.view.View;
import android.widget.AdapterView;
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
import com.benyuan.xiaojs.model.Enroll;
import com.benyuan.xiaojs.model.Fee;
import com.benyuan.xiaojs.model.GetLessonsResponse;
import com.benyuan.xiaojs.model.ObjectsOfPage;
import com.benyuan.xiaojs.model.Schedule;
import com.benyuan.xiaojs.ui.widget.RedTipTextView;
import com.benyuan.xiaojs.util.NumberUtil;
import com.benyuan.xiaojs.util.TimeUtil;
import com.benyuan.xiaojs.util.ToastUtil;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class MyCourseAdapter extends AbsSwipeAdapter<ObjectsOfPage, MyCourseAdapter.Holder> {
    private boolean mIsTeacher;
    private Criteria mCriteria;

    public MyCourseAdapter(Context context, AutoPullToRefreshListView listView, boolean isTeacher) {
        super(context, listView);
        mIsTeacher = isTeacher;
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
    protected void setViewContent(Holder holder, ObjectsOfPage bean, int position) {
        holder.reset();
        if (mIsTeacher){
            showTeacher(holder,bean,position);
        }else {
            showStu(holder,bean,position);
        }
    }

    private void showStu(Holder holder, final ObjectsOfPage bean, int position) {
        holder.name.setText(bean.getTitle());
        holder.time.setText(TimeUtil.getTimeByNow(bean.getSchedule().getStart()) + " " + TimeUtil.getTimeFormat(bean.getSchedule().getStart(),bean.getSchedule().getDuration()));

        holder.clsFunction.setVisibility(View.VISIBLE);
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
        if (bean.getState().equalsIgnoreCase(LessonState.CANCELLED)){
            holder.courseState.setText(R.string.course_state_cancel);
            holder.courseState.setBackgroundResource(R.drawable.course_state_cancel_bg);
        }else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)){
            holder.courseState.setText(R.string.course_state_end);
            holder.courseState.setBackgroundResource(R.drawable.course_state_end_bg);
        }else if (bean.getState().equalsIgnoreCase(LessonState.LIVE)){
            holder.courseState.setText(R.string.living);
            holder.courseState.setBackgroundResource(R.drawable.course_state_on_bg);
        }else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)){
            holder.courseState.setText(R.string.pending_for_course);
            holder.courseState.setBackgroundResource(R.drawable.course_state_wait_bg);
        }
    }

    private void showTeacher(Holder holder, final ObjectsOfPage bean, int position) {
        holder.name.setText(bean.getTitle());
        holder.price.setVisibility(View.VISIBLE);
        holder.price.setText(NumberUtil.getPrice(bean.getFee().getCharge().doubleValue()));
        if (bean.getFee().isFree()){
            holder.desc.setText(R.string.free);
        }else {
            holder.desc.setText(mContext.getString(R.string.course_stu,bean.getEnroll().getCurrent(),bean.getEnroll().getMax()));
        }

        holder.time.setText(TimeUtil.getTimeByNow(bean.getSchedule().getStart()) + " " + TimeUtil.getTimeFormat(bean.getSchedule().getStart(),bean.getSchedule().getDuration()));

        if (bean.getState().equalsIgnoreCase(LessonState.DRAFT)){
            holder.operaFunction.setVisibility(View.VISIBLE);
            holder.shelves.setVisibility(View.VISIBLE);
            holder.shelvesLine.setVisibility(View.VISIBLE);
            holder.edit.setVisibility(View.VISIBLE);
            holder.editLine.setVisibility(View.VISIBLE);
            holder.shelves.setOnClickListener(new View.OnClickListener() {//上架
                @Override
                public void onClick(View view) {
                    shelves(bean);
                }
            });
            holder.edit.setOnClickListener(new View.OnClickListener() {//编辑
                @Override
                public void onClick(View view) {
                    edit(bean);
                }
            });
            holder.detail.setOnClickListener(new View.OnClickListener() {//查看详情
                @Override
                public void onClick(View view) {
                    detail(bean);
                }
            });
            holder.courseState.setText(R.string.pending_shelves);
            holder.courseState.setBackgroundResource(R.drawable.course_state_draft_bg);
        }else if (bean.getState().equalsIgnoreCase(LessonState.REJECTED)){
            holder.more.setVisibility(View.VISIBLE);
            holder.error.setText(R.string.course_examine_error_tip);
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
            holder.operaFunction.setVisibility(View.VISIBLE);
            holder.error.setText(R.string.course_stop_error_tip);
            holder.detail.setOnClickListener(new View.OnClickListener() {//查看详情
                @Override
                public void onClick(View view) {
                    detail(bean);
                }
            });
            holder.courseState.setText(R.string.force_stop);
            holder.courseState.setBackgroundResource(R.drawable.course_state_stop_bg);
        }else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_APPROVAL)){
            holder.operaFunction.setVisibility(View.VISIBLE);
            holder.cancel.setVisibility(View.VISIBLE);
            holder.cancelLine.setVisibility(View.VISIBLE);
            holder.cancel.setOnClickListener(new View.OnClickListener() {//撤销
                @Override
                public void onClick(View view) {
                    cancel(bean);
                }
            });
            holder.detail.setOnClickListener(new View.OnClickListener() {//查看详情
                @Override
                public void onClick(View view) {
                    detail(bean);
                }
            });
            holder.courseState.setText(R.string.examining);
            holder.courseState.setBackgroundResource(R.drawable.course_state_examine_bg);
        }else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)
                || bean.getState().equalsIgnoreCase(LessonState.LIVE)
                || bean.getState().equalsIgnoreCase(LessonState.FINISHED)){
            holder.more.setVisibility(View.VISIBLE);
            holder.clsFunction.setVisibility(View.VISIBLE);
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
    private void shelves(final ObjectsOfPage bean){
        LessonDataManager.requestPutLessonOnShelves(mContext,XiaojsConfig.mLoginUser.getSessionID(),bean.getId(),new APIServiceCallback<GetLessonsResponse>() {
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
    private void edit(ObjectsOfPage bean){

    }

    //撤销
    private void cancel(ObjectsOfPage bean){

    }

    //查看详情
    private void detail(ObjectsOfPage bean){

    }

    //更多
    private void more(ObjectsOfPage bean){

    }

    //班级圈
    private void circle(ObjectsOfPage bean){

    }

    //进入教室
    private void enterClass(ObjectsOfPage bean){

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
    protected void onDataItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Toast.makeText(mContext,"position = " + position,Toast.LENGTH_SHORT).show();
        mContext.startActivity(new Intent(mContext,LessionDetailActivity.class));

    }

    @Override
    protected void doRequest() {
        LessonDataManager.requestGetLessons(mContext, XiaojsConfig.mLoginUser.getSessionID(),mCriteria, mPagination, new APIServiceCallback<GetLessonsResponse>() {
            @Override
            public void onSuccess(GetLessonsResponse object) {
                Logger.d("onSuccess-----------");
                ObjectsOfPage bean = new ObjectsOfPage();
                bean.setTitle("java course1");
                Schedule schedule = new Schedule();
                schedule.setStart(new Date(System.currentTimeMillis() + 1000 * 60 *60 *22));
                schedule.setDuration(100);
                bean.setSchedule(schedule);
                Fee fee = new Fee();
                fee.setFree(false);
                fee.setCharge(BigDecimal.valueOf(555));
                bean.setFee(fee);
                Enroll enroll = new Enroll();
                enroll.setCurrent(12);
                enroll.setMax(50);
                enroll.setMandatory(true);
                bean.setEnroll(enroll);
                bean.setState(LessonState.LIVE);

                List<ObjectsOfPage> beans = new ArrayList<ObjectsOfPage>();
                beans.add(bean);
                MyCourseAdapter.this.onSuccess(beans);
            }

            @Override
            public void onFailure(String errorCode,String errorMessage) {
                MyCourseAdapter.this.onFailure(errorCode,errorMessage);
                Logger.d("onFailure-----------");
            }
        });

    }

    private void notifyData(ObjectsOfPage bean){
        boolean changed = false;
        for (int i = 0;i<mBeanList.size();i++){
            ObjectsOfPage b = mBeanList.get(i);
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
        TextView more;

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
            error.setVisibility(View.GONE);
            more.setVisibility(View.GONE);
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
