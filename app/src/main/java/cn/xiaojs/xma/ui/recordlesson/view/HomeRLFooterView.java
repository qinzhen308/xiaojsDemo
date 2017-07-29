package cn.xiaojs.xma.ui.recordlesson.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.ui.lesson.xclass.ClassesListActivity;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;

/**
 * Created by Paul Z on 2017/7/6.
 */

public class HomeRLFooterView extends LinearLayout implements IViewModel<Object> {


    public HomeRLFooterView(Context context) {
        super(context);
        init();
    }

    public HomeRLFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.item_home_classes_footer, this);
        ButterKnife.bind(this);
    }


    @Override
    public void bindData(int position,Object data) {

    }

    @OnClick(R.id.btn_more)
    public void onViewClicked() {
        ClassesListActivity.invoke((Activity) getContext(),2);
    }
}
