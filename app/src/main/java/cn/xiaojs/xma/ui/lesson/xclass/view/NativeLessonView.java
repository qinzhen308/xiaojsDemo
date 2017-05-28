package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.LabelImageView;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class NativeLessonView extends RelativeLayout implements IViewModel<ClassLesson> {


    ClassLesson mData;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_total_time)
    TextView tvTotalTime;
    @BindView(R.id.tv_lesson)
    TextView tvLesson;
    @BindView(R.id.iv_avatar)
    LabelImageView ivAvatar;
    @BindView(R.id.tv_speaker)
    TextView tvSpeaker;
    @BindView(R.id.btn_more)
    ImageView btnMore;
    @BindView(R.id.bottom_line)
    View bottomLine;
    @BindView(R.id.v_line)
    View vLine;
    @BindView(R.id.state_point)
    View statePoint;


    private CircleTransform circleTransform;

    public NativeLessonView(Context context) {
        super(context);
        init();
    }

    public NativeLessonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init() {
        inflate(getContext(), R.layout.item_native_lesson, this);
        ButterKnife.bind(this);
        circleTransform = new CircleTransform(getContext());
    }

    //待上课
    public void pendingLiveStatus() {

    }

    //上课中
    public void livingStatus() {

    }

    //草稿
    public void draftStatus() {

    }


    @Override
    public void bindData(ClassLesson data) {
        mData = data;
        tvDate.setText(ScheduleUtil.getHMDate(data.schedule.getStart()));
        tvTotalTime.setText(data.schedule.getDuration() + "分钟");
        tvLesson.setText(data.title);


//        tvSpeaker.setText(data.teaching.assistants);
//        Glide.with(getContext())
//                .load(Account.getAvatar(data.teacher.getId(), 300))
//                .bitmapTransform(circleTransform)
//                .placeholder(R.drawable.default_avatar_grey)
//                .error(R.drawable.default_avatar_grey)
//                .into(ivAvatar);

    }

    @OnClick(R.id.btn_more)
    public void onViewClicked() {
        new LessonOperateBoard(getContext()).show();
    }


}
