package cn.xiaojs.xma.ui.widget.banner;
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
 * Date:2016/10/29
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class BannerAdapter extends PagerAdapter {

    private List<BannerBean> beans;
    private Context mContext;

    public BannerAdapter(Context context,List<BannerBean> beans){
        this.beans = beans;
        mContext = context;
    }

    @Override
    public int getCount() {
        return beans == null?0:beans.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageView image = new ImageView(mContext);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        image.setLayoutParams(lp);
        image.setImageResource(beans.get(position).resId);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        container.addView(image);
        return image;
    }
}
