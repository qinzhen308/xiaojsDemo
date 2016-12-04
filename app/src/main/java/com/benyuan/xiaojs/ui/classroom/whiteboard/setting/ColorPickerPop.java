package com.benyuan.xiaojs.ui.classroom.whiteboard.setting;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2016/10/24
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.classroom.whiteboard.widget.CircleView;

public class ColorPickerPop extends SettingsPopupWindow implements View.OnClickListener {
    private final int DEFAULT_COLOR = 0xff000000;

    private static int[] COLORS;

    public ColorChangeListener mListener;
    private Context mContext;

    private View mWindowContentView;
    private View mAnchorView;
    private final int mOffsetX;
    private final int mOffsetY;
    private final int mWindowOffset;

    private CircleView mColorButtons[];
    //private ImageView mColorPickerBtn;

    public interface ColorChangeListener {
        public void onColorChange(int color);

        public void onColorPick();
    }

    public ColorPickerPop(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mWindowContentView = inflate.inflate(R.layout.layout_wb_color_picker_pop, null);
        mPopupWindow.setContentView(mWindowContentView);

        mWindowOffset = getDimensionPixelSize(context, R.dimen.px1);
        mWindowContentView.measure(0, 0);
        mOffsetX = mWindowContentView.getMeasuredWidth() + mPadding.left + mWindowOffset;
        mOffsetY = mWindowContentView.getMeasuredHeight();

        initViews();
    }

    private void initViews() {
        mColorButtons = new CircleView[]{
                (CircleView) mWindowContentView.findViewById(R.id.btn_color_1),
                (CircleView) mWindowContentView.findViewById(R.id.btn_color_2),
                (CircleView) mWindowContentView.findViewById(R.id.btn_color_3),
                (CircleView) mWindowContentView.findViewById(R.id.btn_color_4),
                (CircleView) mWindowContentView.findViewById(R.id.btn_color_5),
                (CircleView) mWindowContentView.findViewById(R.id.btn_color_6),
                (CircleView) mWindowContentView.findViewById(R.id.btn_color_7),
        };

        initColor(mColorButtons.length);

        for (int i = 0; i < mColorButtons.length; i++) {
            mColorButtons[i].setOnClickListener(this);
        }

        /*mColorPickerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onColorPick();
                }
                dismiss();
            }
        });*/
    }

    private void initColor(int size) {
        if (COLORS != null && COLORS.length == size) {
            return;
        }

        COLORS = new int[size];
        Resources rs = mContext.getResources();
        COLORS[0] = rs.getColor(R.color.wb_color_1);
        COLORS[1] = rs.getColor(R.color.wb_color_2);
        COLORS[2] = rs.getColor(R.color.wb_color_3);
        COLORS[3] = rs.getColor(R.color.wb_color_4);
        COLORS[4] = rs.getColor(R.color.wb_color_5);
        COLORS[5] = rs.getColor(R.color.wb_color_6);
        COLORS[6] = rs.getColor(R.color.wb_color_7);
    }

    public void show(View anchor, int anchorHeight) {
        if (mAnchorView == null) {
            mAnchorView = anchor;
        }
        showAsAnchorLocation(anchor, 0, -(mOffsetY + anchorHeight));
    }

    public void setOnColorChangeListener(ColorChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        int n = mColorButtons.length;
        for (int i = 0; i <= n; i++) {
            if (v == mColorButtons[i]) {
                if (mListener != null) {
                    mListener.onColorChange(COLORS[i]);
                }
                break;
            }
        }
        ColorPickerPop.this.dismiss();
    }
}
