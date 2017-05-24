package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.view.AnimationView;

/**
 * Created by maxiaobao on 2017/5/23.
 */

public class ClassLessonView extends RelativeLayout {

    @BindView(R.id.flag_view)
    ImageView flagView;

    @BindView(R.id.title_view)
    TextView titleView;

    @BindView(R.id.pay_tips_view)
    TextView payTipsView;

    @BindView(R.id.member_view)
    TextView memberView;

    @BindView(R.id.status_view)
    TextView statusView;

    @BindView(R.id.living_view)
    AnimationView livingView;

    @BindView(R.id.time_view)
    TextView timeView;

    @BindView(R.id.duration_view)
    TextView durationView;

    @BindView(R.id.teacher_name_view)
    TextView teacherNameView;

    @BindView(R.id.op_data_view)
    ImageButton opDataView;

    @BindView(R.id.op_more_view)
    TextView opMoreView;

    @BindView(R.id.op_room_view)
    TextView opRoomView;



    public ClassLessonView(Context context) {
        super(context);
        init();
    }

    public ClassLessonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setBackgroundColor(Color.WHITE);
        inflate(getContext(), R.layout.layout_classlesson_item,this);
        ButterKnife.bind(this);
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


}
