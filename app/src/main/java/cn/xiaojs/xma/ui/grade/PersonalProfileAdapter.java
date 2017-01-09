package cn.xiaojs.xma.ui.grade;
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
 * Date:2017/1/8
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Date;
import java.util.List;

import cn.xiaojs.xma.ui.view.PersonalProfile;

public class PersonalProfileAdapter extends BaseAdapter {

    private List<Date> dates;
    private Context context;

    public PersonalProfileAdapter(List<Date> dates,Context context) {
        this.dates = dates;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dates == null ? 0 : dates.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null){
            convertView = new PersonalProfile(context);
            holder = new Holder();
            holder.profile = (PersonalProfile) convertView;
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        return convertView;
    }

    class Holder{
        PersonalProfile profile;
    }
}
