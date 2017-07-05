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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.EnrolledLesson;
import cn.xiaojs.xma.model.ctl.LiveItem;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.grade.ClassMaterialActivity;
import cn.xiaojs.xma.ui.view.LessonOperationView;
import cn.xiaojs.xma.ui.view.LessonPersonView;
import cn.xiaojs.xma.ui.view.LessonStatusView;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.ui.widget.LiveProgress;
import cn.xiaojs.xma.util.ShareUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.ToastUtil;

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
//            String[] items = new String[]{mContext.getString(R.string.delete)};
//            holder.operation.setItems(items);
//            holder.operation.enableMore(false);
//            holder.operation.enableEnter(false);
//            holder.operation.setOnItemClickListener(new LOpModel.OnItemClick() {
//                @Override
//                public void onClick(int position) {
//                    switch (position) {
//                        case 1:
//                            delete(position, bean);
//                            break;
//                    }
//                }
//            });
        } else if (bean.state.equalsIgnoreCase(LessonState.FINISHED)) {
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.setEnterColor(R.color.common_text);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.show(bean.state, bean.schedule);
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
        } else if (bean.state.equalsIgnoreCase(LessonState.LIVE)) {
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.setEnterColor(R.color.font_orange);
            holder.status.setVisibility(View.GONE);
            holder.progressWrapper.setVisibility(View.VISIBLE);
            holder.progress.showTimeBar(bean.classroom, bean.schedule.getDuration());
            String[] items = new String[]{mContext.getString(R.string.data_bank)};
            //String[] items = new String[]{" "};
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
        } else if (bean.state.equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)) {
            holder.operation.setVisibility(View.VISIBLE);
            holder.operation.setEnterColor(R.color.common_text);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.show(bean.state, bean.schedule);
            String[] items = new String[]{/*mContext.getString(R.string.schedule), */mContext.getString(R.string.data_bank)};
            //String[] items = new String[]{" "};
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
        } else if (bean.state.equalsIgnoreCase(LessonState.STOPPED)) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.show(bean.state, bean.schedule);
            holder.operation.setVisibility(View.GONE);
            holder.operation.setEnterColor(R.color.common_text);
//            String[] items = new String[]{mContext.getString(R.string.delete)};
//            holder.operation.setItems(items);
//            holder.operation.enableMore(false);
//            holder.operation.enableEnter(false);
//            holder.operation.setOnItemClickListener(new LOpModel.OnItemClick() {
//                @Override
//                public void onClick(int position) {
//                    switch (position) {
//                        case 1:
//                            delete(position, bean);
//                            break;
//                    }
//                }
//            });
        }
        return convertView;
    }

    //更多
    private void more(final LiveItem bean) {

        if (bean.state.equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)
                || bean.state.equalsIgnoreCase(LessonState.FINISHED)
                || bean.state.equalsIgnoreCase(LessonState.LIVE)) {
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
    }


    //分享
    private void share(LiveItem bean) {

        if (bean == null) return;

        String startTime = TimeUtil.format(bean.schedule.getStart().getTime(),
                TimeUtil.TIME_YYYY_MM_DD_HH_MM);

        String name = "";
        if (bean.teacher != null && bean.teacher.getBasic() != null) {
            name = bean.teacher.getBasic().getName();
        }

        String shareUrl = ApiManager.getShareLessonUrl(bean.id, Account.TypeName.STAND_ALONE_LESSON);

        ShareUtil.shareUrlByUmeng((Activity) mContext, bean.title, new StringBuilder(startTime).append("\r\n").append(name).toString(), shareUrl);
    }

    //资料库
    private void databank(LiveItem bean) {
        Intent intent = new Intent(mContext, ClassMaterialActivity.class);
        intent.putExtra(ClassMaterialActivity.EXTRA_ID, bean.id);
        intent.putExtra(ClassMaterialActivity.EXTRA_TITLE, bean.title);
        intent.putExtra(ClassMaterialActivity.EXTRA_SUBTYPE, Collaboration.SubType.STANDA_LONE_LESSON);
        mContext.startActivity(intent);
    }

    //删除
    private void delete(final int pos,final LiveItem bean) {
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

    private void hideLesson(final int pos, final LiveItem bean) {
        LessonDataManager.hideLesson(mContext, bean.id, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                removeItem(pos);
                ToastUtil.showToast(mContext, R.string.delete_success);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeItem(int position) {
        if (position > mLessons.size()) {
            return;
        }
        mLessons.remove(position);
        notifyDataSetChanged();
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
