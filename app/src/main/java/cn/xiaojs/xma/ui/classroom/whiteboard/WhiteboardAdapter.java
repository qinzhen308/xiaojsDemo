package cn.xiaojs.xma.ui.classroom.whiteboard;
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
 * Date:2016/12/21
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class WhiteboardAdapter extends PagerAdapter {
    private Context mContext;
    private List<WhiteboardLayer> mLayers;
    private List<Whiteboard> mWhiteboards;
    private OnWhiteboardListener mOnWhiteboardListener;

    public WhiteboardAdapter(Context context) {
        mContext = context;
        mWhiteboards = new ArrayList<>();
    }

    public void setData(List<WhiteboardLayer> layers) {
        mLayers = layers;
        mWhiteboards.clear();
    }

    @Override
    public int getCount() {
        return mLayers != null ? mLayers.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mLayers != null) {
            WhiteboardLayer layer = mLayers.get(position);
            WhiteboardLayout wbLayout = new WhiteboardLayout(mContext);
            container.addView(wbLayout);
            Whiteboard whiteboard = wbLayout.getWhiteboard();
            whiteboard.setLayer(layer);
            return wbLayout;
        }

        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof WhiteboardLayout) {
            WhiteboardLayout wbLayout = (WhiteboardLayout)object;
            Whiteboard wb = wbLayout.getWhiteboard();
            wb.release();
            if (mOnWhiteboardListener != null) {
                mOnWhiteboardListener.onWhiteboardRemove(wb);
            }
            container.removeView(wbLayout);

            wb = null;
            wbLayout = null;
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (object instanceof WhiteboardLayout) {
            WhiteboardLayout wbLayout = (WhiteboardLayout)object;
            Whiteboard wb = wbLayout.getWhiteboard();
            if (mOnWhiteboardListener != null) {
                mOnWhiteboardListener.onWhiteboardSelected(wb);
            }
        }
    }

    public void setOnWhiteboardListener(OnWhiteboardListener listener) {
        mOnWhiteboardListener = listener;
    }

    public interface OnWhiteboardListener{
        public void onWhiteboardSelected(Whiteboard whiteboard);

        public void onWhiteboardRemove(Whiteboard whiteboard);
    }
}
