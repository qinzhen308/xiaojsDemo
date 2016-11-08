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
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.AbsSwipeAdapter;
import com.benyuan.xiaojs.common.pulltorefresh.BaseHolder;
import com.benyuan.xiaojs.ui.widget.RedTipTextView;
import com.benyuan.xiaojs.util.BitmapUtils;
import com.benyuan.xiaojs.util.NumberUtil;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MyCourseAdapter extends AbsSwipeAdapter<CourseBean, MyCourseAdapter.Holder> {
    private boolean mIsTeacher;
    private Bitmap mEnterClass1;//人数小于99的图
    private Bitmap mEnterClass2;//人数大于99的图

    public MyCourseAdapter(Context context, AutoPullToRefreshListView listView, boolean isTeacher) {
        super(context, listView);
        mIsTeacher = isTeacher;
    }

    @Override
    protected void setViewContent(Holder holder, CourseBean bean, int position) {
        if (mIsTeacher){
            showTeacher(holder,bean,position);
        }else {
            showStu(holder,bean,position);
        }
    }

    private void showStu(Holder holder, CourseBean bean, int position) {
        holder.name.setText(bean.name);
        holder.desc.setText(mContext.getString(R.string.speaker) + bean.speaker);
        holder.time.setText(bean.time);
        holder.operaF4.setVisibility(View.GONE);
        holder.opera1.setTipEnable(true);
        holder.opera2.setTipEnable(true);
        holder.opera3.setTipEnable(true);
        switch (bean.courseState){
            case CourseConstant.STU_ON_COURSING:
            case CourseConstant.STU_CANCEL_COURSE:
            case CourseConstant.STU_PRIVATE_WAIT_COURSE:
                setShow(holder.opera1,R.drawable.learn_circle_selector,R.string.cls_moment);
                enterClass(holder.opera2,1);
                setShow(holder.opera3,R.drawable.data_bank_selector,R.string.data_bank);
                break;
            case CourseConstant.STU_END_COURSE:
            case CourseConstant.STU_WAIT_COURSE:
                setShow(holder.opera1,R.drawable.learn_circle_selector,R.string.cls_moment);
                enterClass(holder.opera2,0);
                setShow(holder.opera3,R.drawable.more_selector,R.string.more);
                break;
            default:
                break;
        }
        showState(holder,bean);
    }

    private void showTeacher(Holder holder, CourseBean bean, int position) {
        holder.name.setText(bean.name);
        holder.desc.setText(NumberUtil.getPrice(bean.price));
        holder.time.setText(bean.time);
        holder.opera1.setTipEnable(true);
        holder.opera2.setTipEnable(true);
        holder.opera3.setTipEnable(true);
        holder.opera4.setTipEnable(true);

        switch (bean.courseState){
            case CourseConstant.TEACHER_WAIT_GROUND:
            case CourseConstant.TEACHER_EXAMINING:
            case CourseConstant.TEACHER_GROUND_WAIT_COURSE:
            case CourseConstant.TEACHER_GROUND_COURSING:
                holder.opera1.setVisibility(View.VISIBLE);
                holder.opera2.setVisibility(View.VISIBLE);
                holder.opera3.setVisibility(View.VISIBLE);
                setShow(holder.opera1,R.drawable.learn_circle_selector,R.string.cls_moment);
                enterClass(holder.opera2,1);
                setShow(holder.opera3,R.drawable.prepare_lesson_selector,R.string.prepare_lesson);
                setShow(holder.opera4,R.drawable.more_selector,R.string.more);
                break;
            case CourseConstant.TEACHER_GROUND_END_COURSE:
                holder.opera1.setVisibility(View.VISIBLE);
                holder.opera2.setVisibility(View.VISIBLE);
                holder.opera3.setVisibility(View.VISIBLE);
                setShow(holder.opera1,R.drawable.learn_circle_selector,R.string.cls_moment);
                setShow(holder.opera2,R.drawable.prepare_lesson_selector,R.string.prepare_lesson);
                setShow(holder.opera3,R.drawable.ic_add_private_course_pressed,R.string.lesson_again);
                //holder.opera3.setText(R.string.lesson_again);
                setShow(holder.opera4,R.drawable.more_selector,R.string.more);
                break;
            case CourseConstant.TEACHER_FAILURE:
                holder.opera1.setVisibility(View.INVISIBLE);
                holder.opera2.setVisibility(View.INVISIBLE);
                holder.opera3.setVisibility(View.INVISIBLE);
                setShow(holder.opera4,R.drawable.more_selector,R.string.more);
                break;
            case CourseConstant.TEACHER_FORCE_CLOSE:
                holder.opera1.setVisibility(View.INVISIBLE);
                holder.opera2.setVisibility(View.INVISIBLE);
                holder.opera3.setVisibility(View.INVISIBLE);
                setShow(holder.opera4,R.drawable.show_pwd,R.string.look_detail);
                break;
            case CourseConstant.TEACHER_GROUND_CANCEL_COURSE:
                holder.opera1.setVisibility(View.INVISIBLE);
                holder.opera2.setVisibility(View.VISIBLE);
                holder.opera3.setVisibility(View.VISIBLE);
                setShow(holder.opera2,R.drawable.learn_circle_selector,R.string.cls_moment);
                enterClass(holder.opera3,1);
                setShow(holder.opera4,R.drawable.show_pwd,R.string.look_detail);
                break;
            case CourseConstant.TEACHER_PRIVATE_WAIT_COURSE:
                holder.opera1.setVisibility(View.VISIBLE);
                holder.opera2.setVisibility(View.VISIBLE);
                holder.opera3.setVisibility(View.VISIBLE);
                setShow(holder.opera1,R.drawable.learn_circle_selector,R.string.cls_moment);
                enterClass(holder.opera2,0);
                setShow(holder.opera3,R.drawable.prepare_lesson_selector,R.string.prepare_lesson);
                holder.opera4.setText(R.string.invite);
                break;

            default:
                break;
        }
        showState(holder,bean);
    }

    private void showState(Holder holder, CourseBean bean){
        switch (bean.courseState){
            case CourseConstant.STU_ON_COURSING:
            case CourseConstant.TEACHER_GROUND_COURSING:
                holder.courseState.setVisibility(View.VISIBLE);
                holder.courseState.setText(R.string.course_state_playing);
                holder.courseState.setBackgroundResource(R.drawable.course_state_on_bg);
                break;
            case CourseConstant.STU_PRIVATE_WAIT_COURSE:
            case CourseConstant.TEACHER_GROUND_WAIT_COURSE:
                holder.courseState.setVisibility(View.VISIBLE);
                holder.courseState.setText(R.string.course_state_wait);
                holder.courseState.setBackgroundResource(R.drawable.course_state_wait_bg);
                break;
            case CourseConstant.STU_CANCEL_COURSE:
            case CourseConstant.TEACHER_GROUND_CANCEL_COURSE:
                holder.courseState.setVisibility(View.VISIBLE);
                holder.courseState.setText(R.string.course_state_cancel);
                holder.courseState.setBackgroundResource(R.drawable.course_state_cancel_bg);
                break;
            case CourseConstant.STU_END_COURSE:
            case CourseConstant.TEACHER_GROUND_END_COURSE:
                holder.courseState.setVisibility(View.VISIBLE);
                holder.courseState.setText(R.string.course_state_end);
                holder.courseState.setBackgroundResource(R.drawable.course_state_end_bg);
                break;
            default:
                holder.courseState.setVisibility(View.GONE);
                break;
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
    protected void doRequest(int pageNo) {
        mCurrentPage = pageNo;
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CourseBean> beans = new ArrayList<>();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0;i<5;i++){
                    CourseBean bean = new CourseBean();
                    long t = System.currentTimeMillis();
                    bean.name = t / 7 + "课";
                    bean.speaker = t / 8 + "s";
                    bean.time = t/222225564143l + "分";
                    if (!mIsTeacher){
                        bean.courseState = (int) (t % 5);
                    }else {
                        bean.courseState = (int) (System.currentTimeMillis() % 10) + 10;
                    }
                    beans.add(bean);
                }
                Message s = Message.obtain();
                s.obj = beans;
                h.sendMessage(s);
            }
        }).start();

    }

    private Handler h = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            List<CourseBean> beans = (List<CourseBean>) message.obj;
            onSuccess(beans);
            return false;
        }
    });

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
