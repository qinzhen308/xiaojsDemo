package cn.xiaojs.xma.ui.recordlesson.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.pageload.IEventer;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.recordlesson.AddLessonDirActivity;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;

/**
 * Created by Paul Z on 2017/7/18.
 */

public class RLEditableDirectoryView extends LinearLayout implements IViewModel<RLDirectory>, IEventer {


    @BindView(R.id.check_view)
    TextView checkView;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.drag_handle)
    ImageView dragHandle;

    EventCallback eventCallback;

    RLDirectory mData;
    @BindView(R.id.tv_lesson_count)
    TextView tvLessonCount;


    public RLEditableDirectoryView(Context context) {
        super(context);
        init();
    }

    public RLEditableDirectoryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RLEditableDirectoryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater.from(getContext()).inflate(R.layout.item_recorded_lesson_directory, this);
        ButterKnife.bind(this);
        setId(R.id.drag_handle);
        dragHandle.setVisibility(VISIBLE);
        checkView.setVisibility(VISIBLE);
        tvLessonCount.setVisibility(GONE);
    }

    @Override
    public void bindData(final int position, RLDirectory data) {
        mData = data;
        tvName.setText(mData.name);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eventCallback!=null){
                    eventCallback.onEvent(EventCallback.EVENT_2,mData,position);
                }
            }
        });
        checkView.setSelected(mData.isChecked());
    }

    @OnClick(R.id.check_view)
    public void onCheckClick() {
        boolean isSelected = checkView.isSelected();
        mData.setChecked(!isSelected);
        if (eventCallback != null) {
            eventCallback.onEvent(EventCallback.EVENT_1);
        }
    }

    @Override
    public void setEventCallback(EventCallback callback) {
        eventCallback=callback;
    }
}
