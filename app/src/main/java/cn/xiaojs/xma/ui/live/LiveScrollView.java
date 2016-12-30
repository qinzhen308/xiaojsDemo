package cn.xiaojs.xma.ui.live;
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
 * Date:2016/12/9
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import cn.xiaojs.xma.ui.widget.HorizontalAdaptScrollerView;

public class LiveScrollView extends HorizontalAdaptScrollerView {
    public LiveScrollView(Context context) {
        super(context);
    }

    public LiveScrollView(Context context, ItemVisibleTypeCount type) {
        super(context, type);
    }

    public LiveScrollView(Context context, float visibleCount) {
        super(context, visibleCount);
    }

    public LiveScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveScrollView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

//    @Override
//    protected void changeChildrenParams(View parent, LayoutParams parentParams) {
//        View view = null;
//        if ((view = parent.findViewById(R.id.live_block_image)) != null){
//            ViewGroup.LayoutParams lp = view.getLayoutParams();
//            lp.width = parentParams.width;
//            lp.height = (int) (3.0 / 4 * lp.width);
//        }
//    }
}
