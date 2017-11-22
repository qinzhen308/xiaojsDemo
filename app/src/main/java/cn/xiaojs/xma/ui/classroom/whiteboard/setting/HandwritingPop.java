package cn.xiaojs.xma.ui.classroom.whiteboard.setting;
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.WhiteboardConfigs;
import cn.xiaojs.xma.ui.classroom.whiteboard.widget.PaintPathPreview;


public class HandwritingPop extends SettingsPopupWindow {

    public final int MIN_PAINT_SIZE = WhiteboardConfigs.MIN_PAINT_STROKE_WIDTH;
    public final int MAX_PAINT_SIZE = WhiteboardConfigs.MAX_PAINT_STROKE_WIDTH;

    public final int DEFAULT_PAINT_ALPHA = 152;

    private PaintChangeListener mListener;

    private SeekBar mPaintSizeBar;
    private PaintPathPreview mPaintPathPreView;

    private int mPaintColor;
    private int mPaintAlpha = DEFAULT_PAINT_ALPHA;

    public interface PaintChangeListener {
        void onPaintSize(float size);

        void onPaintAlpha(int alpha);
    }

    public HandwritingPop(Context context) {
        super(context);

        initViews(context);
    }

    @Override
    public View createView(LayoutInflater inflate) {
        return inflate.inflate(R.layout.layout_wb_handwriting_pop, null);
    }

    private void initViews(Context context) {
        mPaintSizeBar = (SeekBar) mPopWindowLayout.findViewById(R.id.paint_thickness);
        mPaintSizeBar.setProgress(WhiteboardConfigs.DEFAULT_PAINT_STROKE_WIDTH-MIN_PAINT_SIZE);
        mPaintSizeBar.setMax(MAX_PAINT_SIZE-MIN_PAINT_SIZE);
        mPaintSizeBar.setOnSeekBarChangeListener(mSeekBarListener);
        mPaintPathPreView = (PaintPathPreview) mPopWindowLayout.findViewById(R.id.doodle_paint_preview);
        mPaintPathPreView.setPreviewMode(PaintPathPreview.PAINT_PREVIEW_MODE);
    }


    public void show(View anchor, int panelWidth) {
        super.showAsAnchorTop(anchor);
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

    public void setPathPaintParams(int paintColor, int paintAlpha, float paintSize) {
        mPaintColor = paintColor;
        mPaintAlpha = paintAlpha;
        mPaintPathPreView.setPaintColor(paintColor, paintAlpha);
        mPaintPathPreView.setPaintSize(paintSize);
        int max = mPaintSizeBar.getMax();
        int progress = Utils.clamp((int)(paintSize - MIN_PAINT_SIZE), 0, max);
        mPaintSizeBar.setProgress(progress);
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
            dismiss();
        }

    };
}
