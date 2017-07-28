package cn.xiaojs.xma.ui.recordlesson.view;

import android.app.Activity;
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
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.pageload.IEventer;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.model.recordedlesson.Section;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.recordlesson.model.RLLesson;
import cn.xiaojs.xma.util.MaterialUtil;

/**
 * Created by Paul Z on 2017/7/26.
 */

public class RLSectionDocView extends LinearLayout implements IViewModel<Section> ,IEventer {


    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_time)
    TextView tvTime;
    Section mData;

    EventCallback eventCallback;

    public RLSectionDocView(Context context) {
        super(context);
        init();
    }

    public RLSectionDocView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RLSectionDocView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater.from(getContext()).inflate(R.layout.item_recorded_lesson_lesson, this);
        ButterKnife.bind(this);
    }

    @Override
    public void bindData(int position, Section data) {
        mData=data;
        tvName.setText(data.name);
        tvTime.setText(data.document.getTime());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eventCallback!=null){
                    eventCallback.onEvent(EventCallback.EVENT_1, mData.document.buildLibDoc());
                }
            }
        });
    }

    @Override
    public void setEventCallback(EventCallback callback) {
        eventCallback=callback;
    }
}
