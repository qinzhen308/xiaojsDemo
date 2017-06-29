package cn.xiaojs.xma.ui.lesson.xclass.view;

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
import cn.xiaojs.xma.ui.lesson.xclass.ClassesListActivity;
import cn.xiaojs.xma.ui.lesson.xclass.Model.ClassLabelModel;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class HomeClassLabelView extends LinearLayout implements IViewModel<ClassLabelModel> {

    View noData;
    @BindView(R.id.tv_all)
    TextView tvAll;
    @BindView(R.id.no_class_tip)
    TextView noClassTip;


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
    }


    @Override
    public void bindData(int position,ClassLabelModel data) {
        showNoClass(!data.hasData);
    }

    public void showNoClass(boolean isShow) {
        if (isShow) {
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tv_all)
    public void onViewClicked() {
        AnalyticEvents.onEvent(getContext(),6);
        getContext().startActivity(new Intent(getContext(),ClassesListActivity.class));
    }
}
