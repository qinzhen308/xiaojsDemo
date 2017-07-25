package cn.xiaojs.xma.ui.recordlesson.view;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Date;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.model.ctl.Adviser;
import cn.xiaojs.xma.model.ctl.PrivateClass;
import cn.xiaojs.xma.model.recordedlesson.RLesson;
import cn.xiaojs.xma.model.social.Dimension;
import cn.xiaojs.xma.ui.lesson.xclass.ClassInfoActivity;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.recordlesson.RLDirListActivity;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class HomeRecordedLessonView extends RelativeLayout implements IViewModel<RLesson> {


    RLesson mData;
    @BindView(R.id.flag_view)
    ImageView flagView;
    @BindView(R.id.title_view)
    TextView titleView;
    @BindView(R.id.status_view)
    TextView statusView;
    @BindView(R.id.teachers_view)
    TextView teachersView;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.member_view)
    TextView memberView;
    @BindView(R.id.op_dir_view)
    ImageButton opDirView;
    @BindView(R.id.op_more_view)
    ImageButton opMoreView;
    @BindColor(R.color.chocolate_light)
    int teacherColor;
    Dimension dimension;

    public HomeRecordedLessonView(Context context) {
        super(context);
        init();
    }

    public HomeRecordedLessonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_home_recorded_lesson, this);
        setBackgroundResource(R.drawable.bg_classlist_item);
        ButterKnife.bind(this);
        dimension=new Dimension();
        dimension.width=getResources().getDimensionPixelSize(R.dimen.px250);
        dimension.height=getResources().getDimensionPixelSize(R.dimen.px132);
    }


    @Override
    public void bindData(int position, RLesson data) {
        mData = data;
        titleView.setText(mData.title);
//        if (Ctl.LiveLessonState.LIVE.equals(mData.state)) {
//            statusView.setVisibility(INVISIBLE);
//        } else {
//            statusView.setVisibility(INVISIBLE);
//        }

        if (mData.enroll != null) {
            memberView.setText(mData.enroll.current + "人报名");
        } else {
            memberView.setText(0 + "人报名");
        }
//        String teachers = "班主任：";
//        for (int i = 0; i < mData.advisers.length; i++) {
//            teachers += mData.advisers[i].name + "、";
//        }
//        if ("、".equals(teachers.charAt(teachers.length() - 1) + "")) {
//            teachers = teachers.substring(0, teachers.length() - 1);
//        }
        if(!ArrayUtil.isEmpty(mData.teacher)&&mData.teacher[0].getBasic()!=null){
            SpannableString ss=new SpannableString("主讲："+mData.teacher[0].getBasic().getName());
            ss.setSpan(new ForegroundColorSpan(teacherColor),3,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            teachersView.setText(ss);
        }else {
            teachersView.setText("主讲：");
        }
        if(mData.expire!=null){
//            tvDate.setText("有效期："+mData.expire.effective+"天");
            tvDate.setText("有效期至"+ ScheduleUtil.getDateYMD(new Date(mData.createdOn.getTime()+mData.expire.effective*ScheduleUtil.DAY)));
        }else {
            tvDate.setText("永久");
        }

        Glide.with(getContext()).load(Ctl.getCover(mData.cover,dimension)).fitCenter().placeholder(R.drawable.default_lesson_cover).into(flagView);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }



    @OnClick({R.id.op_dir_view, R.id.op_more_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.op_dir_view:
                RLDirListActivity.invoke(getContext());
                break;
            case R.id.op_more_view:
                break;
        }
    }
}
