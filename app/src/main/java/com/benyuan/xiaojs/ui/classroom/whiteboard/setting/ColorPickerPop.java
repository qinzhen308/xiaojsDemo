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

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.classroom.whiteboard.widget.CircleView;

public class ColorPickerPop extends SettingsPopupWindow implements View.OnClickListener {
    private final int DEFAULT_COLOR = 0xff000000;

    private static int[] COLORS;

    public ColorChangeListener mListener;

    private CircleView mColorButtons[];
    //private ImageView mColorPickerBtn;

    public interface ColorChangeListener {
        public void onColorChange(int color);

        public void onColorPick();
    }

    public ColorPickerPop(Context context) {
        super(context);

        mAnchorPaddingTop = getDimensionPixelSize(context, R.dimen.px25);
        initViews();
    }

    @Override
    public View createView(LayoutInflater inflate) {
        return inflate.inflate(R.layout.layout_wb_color_picker_pop, null);
    }

    private void initViews() {
        mColorButtons = new CircleView[]{
                (CircleView) mPopWindowLayout.findViewById(R.id.btn_color_1),
                (CircleView) mPopWindowLayout.findViewById(R.id.btn_color_2),
                (CircleView) mPopWindowLayout.findViewById(R.id.btn_color_3),
                (CircleView) mPopWindowLayout.findViewById(R.id.btn_color_4),
                (CircleView) mPopWindowLayout.findViewById(R.id.btn_color_5),
                (CircleView) mPopWindowLayout.findViewById(R.id.btn_color_6),
                (CircleView) mPopWindowLayout.findViewById(R.id.btn_color_7),
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

    public void show(View anchor, int panelWidth) {
        super.showAsAnchorTop(anchor);
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
