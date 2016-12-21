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
 * Date:2016/12/2
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.TextWriting;

public class TextPop extends SettingsPopupWindow implements View.OnClickListener{
    private TextChangeListener mListener;

    public interface TextChangeListener {
        void onTextPaintSize(int size);

        void onTextOrientation(int orientation);
    }

    public TextPop(Context context) {
        super(context);

        setListener(mPopWindowLayout);
    }

    @Override
    public View createView(LayoutInflater inflate) {
        return inflate.inflate(R.layout.layout_wb_text_pop, null);
    }

    private void setListener(View root) {
        root.findViewById(R.id.horizontal_text).setOnClickListener(this);
        root.findViewById(R.id.vertical_text).setOnClickListener(this);
    }

    public void show(View anchor, int panelWidth) {
        super.showAsAnchorTop(anchor);
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
                mListener.onTextOrientation(TextWriting.TEXT_HORIZONTAL);
                break;
            case R.id.vertical_text:
                mListener.onTextOrientation(TextWriting.TEXT_VERTICAL);
                break;
        }

        TextPop.this.dismiss();
    }

}
