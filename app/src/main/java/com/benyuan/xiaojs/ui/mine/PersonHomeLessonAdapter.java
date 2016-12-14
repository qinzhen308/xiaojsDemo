package com.benyuan.xiaojs.ui.mine;
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
import android.view.LayoutInflater;
import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.BaseHolder;
import com.benyuan.xiaojs.ui.base.BaseScrollTabListAdapter;
import com.benyuan.xiaojs.ui.course.LessonBusiness;

import java.util.ArrayList;
import java.util.List;

public class PersonHomeLessonAdapter extends BaseScrollTabListAdapter<LessonBusiness> {
    public PersonHomeLessonAdapter(Context context, ArrayList<LessonBusiness> dataList, boolean isNeedPreLoading) {
        super(context, dataList, isNeedPreLoading);
    }

    public PersonHomeLessonAdapter(Context context, ArrayList<LessonBusiness> dataList) {
        super(context, dataList);
    }

    public PersonHomeLessonAdapter(Context context) {
        super(context);
    }

    @Override
    protected void requestData() {
        List<LessonBusiness> list = new ArrayList<>();
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());
        list.add(new LessonBusiness());

        onSuccess(list);
    }

    @Override
    public View getView(int position, View convertView) {
        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_person_home_lesson_item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        return convertView;
    }

    class Holder extends BaseHolder {
        public Holder(View view) {
            super(view);
        }
    }
}
