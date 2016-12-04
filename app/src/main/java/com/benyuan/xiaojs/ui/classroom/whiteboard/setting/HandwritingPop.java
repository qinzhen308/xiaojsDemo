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
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.classroom.whiteboard.widget.PaintPathPreview;


public class HandwritingPop extends SettingsPopupWindow {

    public final int MIN_PAINT_SIZE = 6;

    public final int DEFAULT_PAINT_ALPHA = 152;
    public final int MIN_PAINT_ALPHA = 50;

    private final int mOffsetX;
    private final int mOffsetY;
    private final int mWindowOffset;

    private PaintChangeListener mListener;

    private View mAnchorView;
    private View mWindowContentView;

    private SeekBar mPaintSizeBar;
    private PaintPathPreview mPaintPathPreView;

    private int mPaintColor;
    private int mPaintAlpha = DEFAULT_PAINT_ALPHA;
    private Paint mPaint = new Paint();

    public interface PaintChangeListener {
        void onPaintSize(int size);

        void onPaintAlpha(int alpha);
    }

    public HandwritingPop(Context context) {
        super(context);
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mWindowContentView = inflate.inflate(R.layout.layout_wb_handwriting_pop, null);
        mPopupWindow.setContentView(mWindowContentView);

        mWindowOffset = getDimensionPixelSize(context, R.dimen.px1);
        mWindowContentView.measure(0, 0);
        mOffsetX = -mPadding.left + mWindowOffset;
        mOffsetY = mWindowContentView.getMeasuredHeight() + mPadding.top;
        initViews(context);
    }

    private void initViews(Context context) {
        mPaintSizeBar = (SeekBar) mWindowContentView.findViewById(R.id.paint_thickness);
        mPaintSizeBar.setOnSeekBarChangeListener(mSeekBarListener);
        mPaintPathPreView = (PaintPathPreview) mWindowContentView.findViewById(R.id.doodle_paint_preview);
        mPaintPathPreView.setPreviewMode(PaintPathPreview.PAINT_PREVIEW_MODE);
    }


    public void show(View anchor, int anchorHeight) {
        if (mAnchorView == null) {
            mAnchorView = anchor;
        }
        showAsAnchorLocation(anchor, 0, -(mOffsetY + anchorHeight));
    }

    public void setOnDoodlePaintParamsListener(PaintChangeListener listener) {
        mListener = listener;
    }

    public int getDimensionPixelSize(Context context, int resId) {
        return context.getResources().getDimensionPixelSize(resId);
    }

    public void refreshPaintColor(int color) {
        if (mPaintColor != color) {
            mPaintColor = color;
        }
        mPaintPathPreView.setPaintColor(color, mPaintAlpha);
    }

    public void setPathPaintParams(int paintColor, int paintAlpha, int paintSize) {
        mPaintColor = paintColor;
        mPaintAlpha = paintAlpha;
        mPaintPathPreView.setPaintColor(paintColor, paintAlpha);
        mPaintPathPreView.setPaintSize(paintSize);
        mPaintSizeBar.setProgress(paintSize - MIN_PAINT_SIZE);
    }

    private OnSeekBarChangeListener mSeekBarListener = new OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mPaintPathPreView.setPaintSize(seekBar.getProgress() + MIN_PAINT_SIZE);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mListener != null) {
                mListener.onPaintSize(seekBar.getProgress() + MIN_PAINT_SIZE);
            }
        }

    };
}
