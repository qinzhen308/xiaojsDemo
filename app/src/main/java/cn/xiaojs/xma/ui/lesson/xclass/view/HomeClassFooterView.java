package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.ui.lesson.xclass.ClassesListActivity;

/**
 * Created by Paul Z on 2017/7/6.
 */

public class HomeClassFooterView extends LinearLayout implements IViewModel<Object> {


    public HomeClassFooterView(Context context) {
        super(context);
        init();
    }

    public HomeClassFooterView(Context context, AttributeSet attrs) {
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
        AnalyticEvents.onEvent(getContext(),6);
        getContext().startActivity(new Intent(getContext(),ClassesListActivity.class));
    }
}
