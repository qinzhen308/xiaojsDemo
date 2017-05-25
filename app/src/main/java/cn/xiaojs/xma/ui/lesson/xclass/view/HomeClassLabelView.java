package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class HomeClassLabelView extends LinearLayout implements IViewModel<Object> {

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
        noData=findViewById(R.id.no_data);
        ButterKnife.bind(this);
    }


    @Override
    public void bindData(Object data) {
        showNoClass(new Random().nextInt(2) == 0);
    }

    public void showNoClass(boolean isShow) {
        if (isShow) {
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
        }
    }
}
