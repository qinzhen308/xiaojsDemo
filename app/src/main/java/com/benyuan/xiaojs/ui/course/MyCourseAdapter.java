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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.benyuan.xiaojs.model.ObjectsOfPage;
import com.benyuan.xiaojs.ui.widget.RedTipTextView;
import com.benyuan.xiaojs.util.BitmapUtils;
import com.benyuan.xiaojs.util.NumberUtil;
import com.benyuan.xiaojs.util.TimeUtil;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;
import com.orhanobut.logger.Logger;

import butterknife.BindView;

public class MyCourseAdapter extends AbsSwipeAdapter<ObjectsOfPage, MyCourseAdapter.Holder> {
    private boolean mIsTeacher;
    private Bitmap mEnterClass1;//人数小于99的图
    private Bitmap mEnterClass2;//人数大于99的图
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
        if (mIsTeacher){
            showTeacher(holder,bean,position);
        }else {
            showStu(holder,bean,position);
        }
    }

    private void showStu(Holder holder, ObjectsOfPage bean, int position) {
        holder.name.setText(bean.getTitle());
        //holder.desc.setText(mContext.getString(R.string.speaker) + bean.speaker);
        holder.time.setText(TimeUtil.getTimeFormat(bean.getSchedule().getStart(),bean.getSchedule().getDuration()));
        holder.operaF4.setVisibility(View.GONE);
        holder.opera1.setTipEnable(true);
        holder.opera2.setTipEnable(true);
        holder.opera3.setTipEnable(true);

        if (bean.getState().equalsIgnoreCase(LessonState.CANCELLED)
                || bean.getState().equalsIgnoreCase(LessonState.LIVE)){
            setShow(holder.opera1,R.drawable.learn_circle_selector,R.string.cls_moment);
            enterClass(holder.opera2,1);
            setShow(holder.opera3,R.drawable.data_bank_selector,R.string.data_bank);
        }else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)
                || bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)){
            setShow(holder.opera1,R.drawable.learn_circle_selector,R.string.cls_moment);
            enterClass(holder.opera2,0);
            setShow(holder.opera3,R.drawable.more_selector,R.string.more);
        }
        showState(holder,bean);
    }

    private void showTeacher(Holder holder, ObjectsOfPage bean, int position) {
        holder.name.setText(bean.getTitle());
        if (bean.getFee().isFree()){
            holder.desc.setText(R.string.free);
        }else {
            holder.desc.setText(NumberUtil.getPrice(bean.getFee().getCharge()));
        }

        holder.time.setText(TimeUtil.getTimeFormat(bean.getSchedule().getStart(),bean.getSchedule().getDuration()));
        holder.opera1.setTipEnable(true);
        holder.opera2.setTipEnable(true);
        holder.opera3.setTipEnable(true);
        holder.opera4.setTipEnable(true);

        if (bean.getState().equalsIgnoreCase(LessonState.DRAFT)
                || bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_APPROVAL)
                || bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)
                || bean.getState().equalsIgnoreCase(LessonState.LIVE)){
            holder.opera1.setVisibility(View.VISIBLE);
            holder.opera2.setVisibility(View.VISIBLE);
            holder.opera3.setVisibility(View.VISIBLE);
            setShow(holder.opera1,R.drawable.learn_circle_selector,R.string.cls_moment);
            enterClass(holder.opera2,1);
            setShow(holder.opera3,R.drawable.prepare_lesson_selector,R.string.prepare_lesson);
            setShow(holder.opera4,R.drawable.more_selector,R.string.more);
        }else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)){
            holder.opera1.setVisibility(View.VISIBLE);
            holder.opera2.setVisibility(View.VISIBLE);
            holder.opera3.setVisibility(View.VISIBLE);
            setShow(holder.opera1,R.drawable.learn_circle_selector,R.string.cls_moment);
            setShow(holder.opera2,R.drawable.prepare_lesson_selector,R.string.prepare_lesson);
            setShow(holder.opera3,R.drawable.ic_add_private_course_pressed,R.string.lesson_again);
            setShow(holder.opera4,R.drawable.more_selector,R.string.more);
        }else if (bean.getState().equalsIgnoreCase(LessonState.REJECTED)){
            holder.opera1.setVisibility(View.INVISIBLE);
            holder.opera2.setVisibility(View.INVISIBLE);
            holder.opera3.setVisibility(View.INVISIBLE);
            setShow(holder.opera4,R.drawable.more_selector,R.string.more);
        }else if (bean.getState().equalsIgnoreCase(LessonState.CANCELLED)){
            holder.opera1.setVisibility(View.INVISIBLE);
            holder.opera2.setVisibility(View.VISIBLE);
            holder.opera3.setVisibility(View.VISIBLE);
            setShow(holder.opera2,R.drawable.learn_circle_selector,R.string.cls_moment);
            enterClass(holder.opera3,1);
            setShow(holder.opera4,R.drawable.show_pwd,R.string.look_detail);
        }else if (bean.getState().equalsIgnoreCase(LessonState.STOPPED)){
            holder.opera1.setVisibility(View.INVISIBLE);
            holder.opera2.setVisibility(View.INVISIBLE);
            holder.opera3.setVisibility(View.INVISIBLE);
            setShow(holder.opera4,R.drawable.show_pwd,R.string.look_detail);
        }
        showState(holder,bean);
    }

    private void showState(Holder holder, ObjectsOfPage bean){

        if (bean.getState().equalsIgnoreCase(LessonState.LIVE)){
            holder.courseState.setVisibility(View.VISIBLE);
            holder.courseState.setText(R.string.course_state_playing);
            holder.courseState.setBackgroundResource(R.drawable.course_state_on_bg);
        }else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)){
            holder.courseState.setVisibility(View.VISIBLE);
            holder.courseState.setText(R.string.course_state_wait);
            holder.courseState.setBackgroundResource(R.drawable.course_state_wait_bg);
        }else if (bean.getState().equalsIgnoreCase(LessonState.CANCELLED)){
            holder.courseState.setVisibility(View.VISIBLE);
            holder.courseState.setText(R.string.course_state_cancel);
            holder.courseState.setBackgroundResource(R.drawable.course_state_cancel_bg);
        }else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)){
            holder.courseState.setVisibility(View.VISIBLE);
            holder.courseState.setText(R.string.course_state_end);
            holder.courseState.setBackgroundResource(R.drawable.course_state_end_bg);
        }else {
            holder.courseState.setVisibility(View.GONE);
        }
    }

    private void setShow(TextView tv,int drawable,int text){
        tv.setText(text);
        tv.setCompoundDrawablesWithIntrinsicBounds(0,drawable,0,0);
    }

    private void enterClass(TextView tv,int num){
        tv.setText(R.string.into_cls);
        if (num > 0){
            tv.setCompoundDrawablesWithIntrinsicBounds(null, BitmapUtils.getDrawableWithText(mContext,getEnterClassBg(num),String.valueOf(num)),null,null);
        }else {
            tv.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_enter_cls_no,0,0);
        }
    }

    private Bitmap getEnterClassBg(int num){
        if (num > 99){
            if (mEnterClass2 == null){
                mEnterClass2 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_enter_cls_have);
            }
            return mEnterClass2.copy(Bitmap.Config.ARGB_8888,true);
        }else {
            if (mEnterClass1 == null){
                mEnterClass1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_enter_cls_have);
            }
            return mEnterClass1.copy(Bitmap.Config.ARGB_8888,true);
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
                MyCourseAdapter.this.onSuccess(object.getObjectsOfPage());
            }

            @Override
            public void onFailure(String errorCode,String errorMessage) {
                MyCourseAdapter.this.onFailure(errorCode,errorMessage);
                Logger.d("onFailure-----------");
            }
        });

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

        @BindView(R.id.course_opera_1)
        RedTipTextView opera1;
        @BindView(R.id.course_opera_2)
        RedTipTextView opera2;
        @BindView(R.id.course_opera_3)
        RedTipTextView opera3;
        @BindView(R.id.course_opera_4)
        RedTipTextView opera4;

        @BindView(R.id.course_opera_f1)
        FrameLayout operaF1;
        @BindView(R.id.course_opera_f2)
        FrameLayout operaF2;
        @BindView(R.id.course_opera_f3)
        FrameLayout operaF3;
        @BindView(R.id.course_opera_f4)
        FrameLayout operaF4;


        public Holder(View view) {
            super(view);
        }
    }
}
