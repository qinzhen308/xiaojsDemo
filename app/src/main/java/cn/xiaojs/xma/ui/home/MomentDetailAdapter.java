package cn.xiaojs.xma.ui.home;
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
 * Date:2016/12/13
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.ui.base.BaseScrollTabListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MomentDetailAdapter extends BaseScrollTabListAdapter<RecommendCourseBean> {

    public MomentDetailAdapter(Context context, ArrayList<RecommendCourseBean> dataList, boolean isNeedPreLoading) {
        super(context, dataList, isNeedPreLoading);
    }

    public MomentDetailAdapter(Context context, ArrayList<RecommendCourseBean> dataList) {
        super(context, dataList);
    }

    public MomentDetailAdapter(Context context) {
        super(context);
    }

    @Override
    protected void requestData() {
        List<RecommendCourseBean> list = new ArrayList<>();
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onSuccess(list);
    }

    @Override
    public View getView(int position, View convertView) {
        Holder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_moment_comment_item,null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        return convertView;
    }

    @Override
    protected void onItemClick(int position) {
        Intent intent = new Intent(mContext,MomentCommentActivity.class);
        intent.putExtra(HomeConstant.KEY_COMMENT_TYPE,HomeConstant.COMMENT_TYPE_REPLY);
        intent.putExtra(HomeConstant.KEY_COMMENT_REPLY_NAME,"菜菜菜");
        mContext.startActivity(intent);
    }

    class Holder extends BaseHolder {
        public Holder(View view) {
            super(view);
        }
    }

}
