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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.benyuan.xiaojs.R;

public class EraserPop extends SettingsPopupWindow implements View.OnClickListener{
    private EraserChangeListener mParamsChangeListener;

    private TextView mClearDoodleButton;

    public interface EraserChangeListener {
        void onEraserPaintSize(int size);

        void onClearDoodles();
    }

    public EraserPop(Context context) {
        super(context);

        initViews(context);
    }

    @Override
    public View createView(LayoutInflater inflate) {
        return inflate.inflate(R.layout.layout_wb_eraser_pop, null);
    }

    private void initViews(Context context) {
        mClearDoodleButton = (TextView) mPopWindowLayout.findViewById(R.id.clear_all);
        mClearDoodleButton.setOnClickListener(this);
    }

    public void setOnEraserParamsListener(EraserChangeListener listener) {
        mParamsChangeListener = listener;
    }

    public void show(View anchor, int panelWidth) {
        super.showAsAnchorTop(anchor);
    }

    @Override
    public void onClick(View v) {
        if (mParamsChangeListener != null) {
            mParamsChangeListener.onClearDoodles();
            EraserPop.this.dismiss();
        }
    }
}
