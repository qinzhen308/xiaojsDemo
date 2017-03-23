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
import cn.xiaojs.xma.model.EnrolledLesson;
import cn.xiaojs.xma.ui.classroom.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.Constants;
import cn.xiaojs.xma.ui.grade.MaterialActivity;
import cn.xiaojs.xma.ui.view.LessonOperationView;
import cn.xiaojs.xma.ui.view.LessonPersonView;
import cn.xiaojs.xma.ui.view.LessonStatusView;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;
import cn.xiaojs.xma.ui.widget.LiveProgress;

public class LiveEnrollLessonAdapter extends CanInScrollviewListView.Adapter {
//    private final int MAX = 2;

    private Context mContext;
    private List<EnrolledLesson> lessons;

    public LiveEnrollLessonAdapter(Context context,List<EnrolledLesson> lessons){
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_enroll_lesson_item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
//        holder.enter.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//        holder.enter.getPaint().setAntiAlias(true);
////        holder.time.setText("12:00");
////        holder.image.setImageResource(R.drawable.default_portrait);
////        holder.title.setText("titletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitle");
////        holder.stuNum.setText("111人学过");
//        Bitmap b1 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
//        Bitmap b2 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
//        Bitmap b3 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
//        Bitmap b4 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
//        Bitmap b5 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
//        Bitmap b6 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
//        Bitmap b7 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
//        Bitmap b8 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
//        Bitmap b9= BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
//        Bitmap b10 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
//
//        List<Bitmap> list = new ArrayList<>();
//        list.add(b1);
//        list.add(b2);
//        list.add(b3);
//        list.add(b4);
//        list.add(b5);
//        list.add(b6);
//        list.add(b7);
//        list.add(b8);
//        list.add(b9);
//        list.add(b10);
//        //holder.imageFlow.showWithNum(list,mContext.getResources().getDimensionPixelSize(R.dimen.px20),mContext.getResources().getDimensionPixelSize(R.dimen.px2));
//        holder.imageFlow.show(list,mContext.getResources().getDimensionPixelSize(R.dimen.px20),mContext.getResources().getDimensionPixelSize(R.dimen.px5));

        final EnrolledLesson bean = lessons.get(position);
        holder.reset();
        holder.name.setText(bean.getTitle());
        holder.lessonCount.setText("共1节");
        holder.persons.show(bean);
        //Glide.with(mContext).load(bean.getCover()).error(R.drawable.default_lesson_cover).into(holder.image);
        if (bean.getState().equalsIgnoreCase(LessonState.CANCELLED)) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.show(bean);
            holder.operation.setVisibility(View.GONE);
//            String[] items = new String[]{mContext.getString(R.string.data_bank)};
//            holder.operation.setItems(items);
//            holder.operation.enableMore(false);
//            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
//                @Override
//                public void onClick(int position) {
//                    databank(bean);
//                }
//            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
            holder.operation.setVisibility(View.GONE);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.show(bean);
//            String[] items = new String[]{/*mContext.getString(R.string.schedule), */mContext.getString(R.string.data_bank)};
            String[] items = new String[]{" "};
            holder.operation.setItems(items);
            holder.operation.enableMore(false);
            holder.operation.enableEnter(true);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1:
                            //schedule(bean);
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
            holder.status.setVisibility(View.GONE);
            holder.progressWrapper.setVisibility(View.VISIBLE);
            //String[] items = new String[]{mContext.getString(R.string.data_bank)};
            String[] items = new String[]{" "};
            holder.operation.setItems(items);
            holder.operation.enableMore(false);
            holder.operation.enableEnter(true);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    if (position == ENTER){
                        enterClass(bean);
                    }else {
                        databank(bean);
                    }
                }
            });
        } else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)) {
            holder.operation.setVisibility(View.VISIBLE);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.show(bean);
            //String[] items = new String[]{/*mContext.getString(R.string.schedule), */mContext.getString(R.string.data_bank)};
            String[] items = new String[]{" "};
            holder.operation.setItems(items);
            holder.operation.enableMore(false);
            holder.operation.enableEnter(true);
            holder.operation.setOnItemClickListener(new LessonOperationView.OnItemClick() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 1:
                            //schedule(bean);
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
            holder.operation.setVisibility(View.GONE);
        }
        return convertView;
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

    //进入教室
    private void enterClass(EnrolledLesson bean) {
        Intent i = new Intent();
        i.putExtra(Constants.KEY_TICKET, bean.getTicket());
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
