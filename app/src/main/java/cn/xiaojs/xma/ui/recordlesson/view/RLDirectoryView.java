package cn.xiaojs.xma.ui.recordlesson.view;

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
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;

/**
 * Created by Paul Z on 2017/7/18.
 */

public class RLDirectoryView extends LinearLayout implements IViewModel<RLDirectory> {


    @BindView(R.id.check_view)
    TextView checkView;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.drag_handle)
    ImageView dragHandle;
    @BindView(R.id.tv_lesson_count)
    TextView tvLessonCount;

    RLDirectory mData;

    public RLDirectoryView(Context context) {
        super(context);
        init();
    }

    public RLDirectoryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RLDirectoryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater.from(getContext()).inflate(R.layout.item_recorded_lesson_directory, this);
        ButterKnife.bind(this);
    }

    @Override
    public void bindData(int position, RLDirectory data) {
        mData=data;
        tvName.setText(data.name);
        tvLessonCount.setText("共" + mData.getChildrenCount() + "节课");
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
