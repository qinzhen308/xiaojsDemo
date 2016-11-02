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
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.AbsSwipeAdapter;
import com.benyuan.xiaojs.common.pulltorefresh.BaseHolder;
import com.benyuan.xiaojs.util.NumberUtil;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MyCourseAdapter extends AbsSwipeAdapter<CourseBean, MyCourseAdapter.Holder> {
    private boolean mIsTeacher;

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
        holder.teachFun.setVisibility(View.GONE);
        holder.name.setText(bean.name);
        holder.desc.setText(mContext.getString(R.string.speaker) + bean.speaker);
        holder.time.setText(bean.time);
        holder.opera4.setVisibility(View.GONE);
        switch (bean.courseState){
            case CourseConstant.STU_ON_COURSING:
            case CourseConstant.STU_CANCEL_COURSE:
            case CourseConstant.STU_PRIVATE_WAIT_COURSE:
                holder.opera1.setText(R.string.cls_moment);
                holder.opera2.setText(R.string.into_cls);
                holder.opera3.setText(R.string.data_bank);
                break;
            case CourseConstant.STU_END_COURSE:
            case CourseConstant.STU_WAIT_COURSE:
                holder.opera1.setText(R.string.cls_moment);
                holder.opera2.setText(R.string.into_cls);
                holder.opera3.setText(R.string.more);
                break;
            default:
                break;
        }
    }

    private void showTeacher(Holder holder, CourseBean bean, int position) {
        holder.name.setText(bean.name);
        holder.desc.setText(NumberUtil.getPrice(bean.price));
        holder.time.setText(bean.time);
        holder.stuNum.setText(bean.stuNum);
        holder.share.setText(bean.share);
        holder.focus.setText(bean.focus);
        switch (bean.courseState){
            case CourseConstant.TEACHER_WAIT_GROUND:
            case CourseConstant.TEACHER_EXAMINING:
            case CourseConstant.TEACHER_GROUND_WAIT_COURSE:
            case CourseConstant.TEACHER_GROUND_COURSING:
                holder.opera1.setText(R.string.cls_moment);
                holder.opera2.setText(R.string.prepare_lesson);
                holder.opera3.setText(R.string.into_cls);
                holder.opera4.setText(R.string.more);
                break;
            case CourseConstant.TEACHER_GROUND_END_COURSE:
                holder.opera1.setText(R.string.cls_moment);
                holder.opera2.setText(R.string.prepare_lesson);
                holder.opera3.setText(R.string.lesson_again);
                holder.opera4.setText(R.string.more);
                break;
            case CourseConstant.TEACHER_FAILURE:
                holder.opera1.setVisibility(View.INVISIBLE);
                holder.opera2.setVisibility(View.INVISIBLE);
                holder.opera3.setVisibility(View.INVISIBLE);
                holder.opera4.setText(R.string.more);
                break;
            case CourseConstant.TEACHER_FORCE_CLOSE:
                holder.opera1.setVisibility(View.INVISIBLE);
                holder.opera2.setVisibility(View.INVISIBLE);
                holder.opera3.setVisibility(View.INVISIBLE);
                holder.opera4.setText(R.string.look_detail);
                break;
            case CourseConstant.TEACHER_GROUND_CANCEL_COURSE:
                holder.opera1.setVisibility(View.INVISIBLE);
                holder.opera2.setText(R.string.cls_moment);
                holder.opera3.setText(R.string.into_cls);
                holder.opera4.setText(R.string.look_detail);
                break;
            case CourseConstant.TEACHER_PRIVATE_WAIT_COURSE:
                holder.opera1.setText(R.string.cls_moment);
                holder.opera2.setText(R.string.prepare_lesson);
                holder.opera3.setText(R.string.into_cls);
                holder.opera4.setText(R.string.invite);
                break;

            default:
                break;
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
    protected void doRequest(int pageNo) {

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
                    bean.courseState = (int) (t % 5);
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
        ImageView xbsState;
        @BindView(R.id.course_name)
        TextView name;
        @BindView(R.id.course_desc)
        TextView desc;
        @BindView(R.id.course_state)
        ImageView courseState;
        @BindView(R.id.time_mark)
        TextView time;

        @BindView(R.id.teach_fun)
        RelativeLayout teachFun;
        @BindView(R.id.course_stu_num)
        TextView stuNum;
        @BindView(R.id.course_share)
        TextView share;
        @BindView(R.id.course_focus)
        TextView focus;

        @BindView(R.id.course_opera_1)
        TextView opera1;
        @BindView(R.id.course_opera_2)
        TextView opera2;
        @BindView(R.id.course_opera_3)
        TextView opera3;
        @BindView(R.id.course_opera_4)
        TextView opera4;

        public Holder(View view) {
            super(view);
        }
    }
}
