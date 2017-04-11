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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.model.ctl.LiveItem;
import cn.xiaojs.xma.ui.classroom.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.Constants;
import cn.xiaojs.xma.ui.grade.MaterialActivity;
import cn.xiaojs.xma.ui.view.LessonOperationView;
import cn.xiaojs.xma.ui.view.LessonPersonView;
import cn.xiaojs.xma.ui.view.LessonStatusView;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;
import cn.xiaojs.xma.ui.widget.LiveProgress;

public class LiveEnrollLessonAdapter extends CanInScrollviewListView.Adapter {
    private Context mContext;
    private List<LiveItem> mLessons;

    public LiveEnrollLessonAdapter(Context context, List<LiveItem> lessons) {
        mContext = context;
        mLessons = lessons;
    }

    @Override
    public int getCount() {
        return mLessons == null ? 0 : mLessons.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_enroll_lesson_item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final LiveItem bean = mLessons.get(position);
        holder.reset();

        holder.name.setText(bean.title);
        holder.lessonCount.setText("共1节");
        holder.persons.show(bean.teacher, null, bean.enroll);
        //Glide.with(mContext).load(bean.getCover()).error(R.drawable.default_lesson_cover).into(holder.image);
        if (bean.state.equalsIgnoreCase(LessonState.CANCELLED)) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.show(bean.state, bean.schedule);
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
        } else if (bean.state.equalsIgnoreCase(LessonState.FINISHED)) {
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.setEnterColor(R.color.common_text);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.show(bean.state, bean.schedule);
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
        } else if (bean.state.equalsIgnoreCase(LessonState.LIVE)) {
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.setEnterColor(R.color.font_orange);
            holder.status.setVisibility(View.GONE);
            holder.progressWrapper.setVisibility(View.VISIBLE);
            holder.progress.showTimeBar(bean.classroom, bean.schedule.getDuration());
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
        } else if (bean.state.equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)) {
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.setEnterColor(R.color.common_text);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.show(bean.state, bean.schedule);
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
        } else if (bean.state.equalsIgnoreCase(LessonState.STOPPED)) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.show(bean.state, bean.schedule);
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
        return convertView;
    }

    //资料库
    private void databank(LiveItem bean) {
        Intent intent = new Intent(mContext, MaterialActivity.class);
        mContext.startActivity(intent);
    }

    //退课
    private void dropClass(LiveItem bean) {

    }

    //评价
    private void evaluate(LiveItem bean) {

    }

    //进入教室
    private void enterClass(LiveItem bean) {
        Intent i = new Intent();
        i.putExtra(Constants.KEY_TICKET, bean.ticket);
        i.setClass(mContext, ClassroomActivity.class);
        mContext.startActivity(i);
    }

    class Holder extends BaseHolder {

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
