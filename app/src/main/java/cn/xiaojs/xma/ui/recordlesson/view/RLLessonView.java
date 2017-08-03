package cn.xiaojs.xma.ui.recordlesson.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.recordlesson.model.RLLesson;
import cn.xiaojs.xma.util.MaterialUtil;

/**
 * Created by Administrator on 2017/7/18.
 */

public class RLLessonView extends LinearLayout implements IViewModel<RLLesson> {


    @BindView(R.id.check_view)
    TextView checkView;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.drag_handle)
    ImageView dragHandle;
    RLLesson mData;

    public RLLessonView(Context context) {
        super(context);
        init();
    }

    public RLLessonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RLLessonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater.from(getContext()).inflate(R.layout.item_recorded_lesson_lesson, this);
        ButterKnife.bind(this);
        setId(R.id.drag_handle);
    }

    @Override
    public void bindData(int position, RLLesson data) {
        mData=data;
        tvName.setText(data.name);
        tvTime.setVisibility(GONE);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialUtil.openMaterial((Activity) getContext(),mData.buildLibDoc());
            }
        });
    }
}
