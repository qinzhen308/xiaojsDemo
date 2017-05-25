package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.view.AnimationView;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class ClassView extends RelativeLayout implements IViewModel<Object>{


    @BindView(R.id.flag_view)
    ImageView flagView;
    @BindView(R.id.title_view)
    TextView titleView;
    @BindView(R.id.member_view)
    TextView memberView;
    @BindView(R.id.status_view)
    TextView statusView;
    @BindView(R.id.living_view)
    AnimationView livingView;
    @BindView(R.id.duration_view)
    TextView durationView;
    @BindView(R.id.op_schedule_view)
    ImageButton opScheduleView;
    @BindView(R.id.op_data_view)
    ImageButton opDataView;
    @BindView(R.id.op_more_view)
    ImageButton opMoreView;
    @BindView(R.id.op_room_view)
    Button opRoomView;
    @BindView(R.id.entrance_count)
    TextView entranceCount;

    public ClassView(Context context) {
        super(context);
        init();
    }

    public ClassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_class_item, this);
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


    @OnClick({R.id.op_schedule_view, R.id.op_data_view, R.id.op_more_view, R.id.op_room_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.op_schedule_view:
                break;
            case R.id.op_data_view:
                break;
            case R.id.op_more_view:
                break;
            case R.id.op_room_view:
                break;
        }
    }

    @Override
    public void bindData(Object data) {

    }
}
