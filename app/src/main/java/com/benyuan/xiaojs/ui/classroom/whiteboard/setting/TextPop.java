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
 * Date:2016/12/2
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import com.benyuan.xiaojs.R;

public class TextPop extends SettingsPopupWindow implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    public final static int TEXT_HORIZONTAL = 1;
    public final static int TEXT_VERTICAL = 2;
    public final static int MIN_TEXT_SIZE = 10;

    private final int mOffsetX;
    private final int mOffsetY;
    private final int mWindowOffset;

    private View mAnchorView;
    private View mWindowContentView;

    private TextChangeListener mListener;

    public interface TextChangeListener {
        void onTextPaintSize(int size);

        void onTextOrientation(int orientation);

        void onInsertLine();
    }

    public TextPop(Context context) {
        super(context);
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mWindowContentView = inflate.inflate(R.layout.layout_wb_text_pop, null);
        mPopupWindow.setContentView(mWindowContentView);

        mWindowOffset = getDimensionPixelSize(context, R.dimen.px1);
        mWindowContentView.measure(0, 0);
        mOffsetX = mWindowContentView.getMeasuredWidth() + mPadding.left + mWindowOffset;
        mOffsetY = mWindowContentView.getMeasuredHeight() + mPadding.top;

        setListener(mWindowContentView);
    }

    private void setListener(View root) {
        root.findViewById(R.id.horizontal_text).setOnClickListener(this);
        root.findViewById(R.id.vertical_text).setOnClickListener(this);
        root.findViewById(R.id.insert_line).setOnClickListener(this);
        ((SeekBar)root.findViewById(R.id.text_size)).setOnSeekBarChangeListener(this);
    }

    public void show(View anchor, int anchorHeight) {
        if (mAnchorView == null) {
            mAnchorView = anchor;
        }
        showAsAnchorLocation(anchor, 0, -(mOffsetY + anchorHeight));
    }

    public void setTextChangeListener(TextChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.horizontal_text:
                mListener.onTextOrientation(TEXT_HORIZONTAL);
                break;
            case R.id.vertical_text:
                mListener.onTextOrientation(TEXT_VERTICAL);
                break;
            case R.id.insert_line:
                mListener.onInsertLine();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mListener != null) {
            mListener.onTextPaintSize(seekBar.getProgress() + MIN_TEXT_SIZE);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mListener != null) {
            mListener.onTextPaintSize(seekBar.getProgress() + MIN_TEXT_SIZE);
        }
    }

}
