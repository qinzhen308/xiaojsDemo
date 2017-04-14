package cn.xiaojs.xma.ui.view;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2017/1/15
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.util.DeviceUtil;

public class LessonOperationView extends RelativeLayout {

    @BindView(R.id.lesson_opera1)
    TextView opera1;
    @BindView(R.id.lesson_opera2)
    TextView opera2;
    @BindView(R.id.lesson_opera3)
    TextView opera3;

    @BindView(R.id.lesson_opera_enter)
    TextView enter;

    @BindView(R.id.lesson_more)
    ImageView more;

    @BindView(R.id.divider1)
    View divider1;
    @BindView(R.id.divider2)
    View divider2;

    private OnItemClick listener;
    private OnClickListener click;

    public LessonOperationView(Context context) {
        super(context);
        init();
    }

    public LessonOperationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LessonOperationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public LessonOperationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_lesson_operation, this);
        ButterKnife.bind(this);
        click = new Click();
        opera1.setOnClickListener(click);
        opera2.setOnClickListener(click);
        opera3.setOnClickListener(click);
        more.setOnClickListener(click);
        enter.setOnClickListener(click);
    }

    public void setOnItemClickListener(OnItemClick l) {
        listener = l;
    }

    public void enableMore(boolean b) {

        enable = b;

        if (b) {
            more.setVisibility(VISIBLE);
        }else{
            more.setVisibility(GONE);
        }
    }

    private boolean enable;

    public void enableEnter(boolean enable) {
        if (enable) {
            enter.setVisibility(VISIBLE);
        } else {
            enter.setVisibility(INVISIBLE);
        }
    }

    public void setItems(String[] items) {
        if (items == null || items.length <= 0)
            return;
        if (items.length == 1) {
            if (enable) {
                more.setVisibility(VISIBLE);
                divider1.setVisibility(VISIBLE);
            } else {
                more.setVisibility(GONE);
                divider1.setVisibility(GONE);
            }
            opera1.setVisibility(VISIBLE);
            opera2.setVisibility(GONE);
            opera3.setVisibility(GONE);

            divider2.setVisibility(GONE);
            opera1.setText(items[0]);
        } else if (items.length == 2) {
            if (enable) {
                more.setVisibility(VISIBLE);
                divider2.setVisibility(VISIBLE);
            } else {
                more.setVisibility(GONE);
                divider2.setVisibility(GONE);
            }
            opera1.setVisibility(VISIBLE);
            opera2.setVisibility(VISIBLE);
            opera3.setVisibility(GONE);
            divider1.setVisibility(VISIBLE);

            opera1.setText(items[0]);
            opera2.setText(items[1]);
        } else if (items.length == 3) {
            if (enable) {
                opera3.setVisibility(GONE);
                more.setVisibility(VISIBLE);
            } else {
                opera3.setVisibility(VISIBLE);
                more.setVisibility(GONE);
            }
            opera1.setVisibility(VISIBLE);
            opera2.setVisibility(VISIBLE);
            divider1.setVisibility(VISIBLE);
            divider2.setVisibility(VISIBLE);
            opera1.setText(items[0]);
            opera2.setText(items[1]);
            opera3.setText(items[2]);
        }
        if (more.getVisibility() == VISIBLE) {
            DeviceUtil.expandViewTouch(more, 50);
        }
    }

    public void hiddenDiver() {
        divider1.setVisibility(GONE);
        divider2.setVisibility(GONE);
    }

    public void hiddenOpera123() {
        opera1.setVisibility(GONE);
        opera2.setVisibility(GONE);
        opera3.setVisibility(GONE);
    }


    public void setEnterColor(@ColorRes int color) {
        enter.setTextColor(getResources().getColor(color));
    }

    class Click implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (listener == null)
                return;
            switch (v.getId()) {
                case R.id.lesson_opera1:
                    listener.onClick(1);
                    break;
                case R.id.lesson_opera2:
                    listener.onClick(2);
                    break;
                case R.id.lesson_opera3:
                    listener.onClick(3);
                    break;
                case R.id.lesson_more:
                    listener.onClick(OnItemClick.MORE);
                    break;
                case R.id.lesson_opera_enter:
                    listener.onClick(OnItemClick.ENTER);
                    break;
            }
        }
    }


    public interface OnItemClick {

        int MORE = 10;
        int ENTER = 11;

        void onClick(int position);
    }
}
