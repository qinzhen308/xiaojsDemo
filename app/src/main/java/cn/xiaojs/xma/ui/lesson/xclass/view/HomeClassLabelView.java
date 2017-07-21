package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.pageload.IEventer;
import cn.xiaojs.xma.ui.lesson.xclass.ClassesListActivity;
import cn.xiaojs.xma.ui.lesson.xclass.model.ClassLabelModel;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class HomeClassLabelView extends LinearLayout implements IViewModel<ClassLabelModel> ,IEventer{

    View noData;
    @BindView(R.id.tv_all)
    TextView tvAll;
    @BindView(R.id.no_class_tip)
    TextView noClassTip;
    @BindView(R.id.tab_class)
    TextView tabClass;
    @BindView(R.id.tab_record_lesson)
    TextView tabRecordLesson;

    public static final int TAB_CLASS=0;
    public static final int TAB_RECORDED_LESSON=1;

    int curTab=TAB_CLASS;

    EventCallback eventCallback;


    public HomeClassLabelView(Context context) {
        super(context);
        init();
    }

    public HomeClassLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.item_home_classes_label, this);
        noData = findViewById(R.id.no_data);
        ButterKnife.bind(this);
        tabClass.setSelected(true);
    }


    @Override
    public void bindData(int position, ClassLabelModel data) {
        showNoClass(!data.hasData);
    }

    public void showNoClass(boolean isShow) {
        if (isShow) {
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.tab_class, R.id.tab_record_lesson,R.id.tv_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_all:
                if(curTab==TAB_CLASS){
                    AnalyticEvents.onEvent(getContext(), 6);
                    ClassesListActivity.invoke((Activity) getContext(),0);
                }else if(curTab==TAB_RECORDED_LESSON){
                    ClassesListActivity.invoke((Activity) getContext(),2);
                }
                break;
            case R.id.tab_class:
                checkTab(TAB_CLASS);
                break;
            case R.id.tab_record_lesson:
                checkTab(TAB_RECORDED_LESSON);
                break;
        }
    }

    private void checkTab(int tab){
        if(tab==curTab)return;
        curTab=tab;
        if(tab==TAB_CLASS){
            tabClass.setSelected(true);
            tabRecordLesson.setSelected(false);
        }else if(tab==TAB_RECORDED_LESSON){
            tabClass.setSelected(false);
            tabRecordLesson.setSelected(true);
        }
        if(eventCallback!=null){
            eventCallback.onEvent(EventCallback.EVENT_1,tab);
        }
    }

    @Override
    public void setEventCallback(EventCallback callback) {
        this.eventCallback=callback;
    }
}
