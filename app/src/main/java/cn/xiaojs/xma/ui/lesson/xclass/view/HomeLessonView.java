package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.content.Context;
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

public class HomeLessonView extends RelativeLayout implements IViewModel<Object>{



    public HomeLessonView(Context context) {
        super(context);
        init();
    }

    public HomeLessonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_home_lesson, this);
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


    @Override
    public void bindData(Object data) {

    }
}
