package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.model.ctl.Adviser;
import cn.xiaojs.xma.model.ctl.PrivateClass;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.grade.ClassMaterialActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassInfoActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassScheduleActivity;
import cn.xiaojs.xma.ui.lesson.xclass.model.LoadStateMode;
import cn.xiaojs.xma.ui.view.AnimationView;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class LoadStateBarView extends LinearLayout implements IViewModel<LoadStateMode>{

    LoadStateMode mData;

    public LoadStateBarView(Context context) {
        super(context);
        init();
    }

    public LoadStateBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_load_state_bar, this);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setOrientation(VERTICAL);
        ButterKnife.bind(this);
    }


    @Override
    public void bindData(int position,LoadStateMode data) {
        mData=data;

    }


}
