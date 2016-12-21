package cn.xiaojs.xma.ui.classroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import cn.xiaojs.xma.R;

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
 * Date:2016/11/29
 * Desc:
 *
 * ======================================================================================== */

public class CourseWareAdapter extends BaseAdapter {
    private Context mContext;

    public CourseWareAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        //TODO test count
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       Holder holder;
        if (convertView == null) {
            convertView = createContentView();
        }
        holder = (Holder)convertView.getTag();

        bindData(holder);
        return convertView;
    }

    private View createContentView() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_classroom_course_ware_item, null);
        Holder holder = new Holder();
        holder.title = (TextView) v.findViewById(R.id.title);
        holder.duration = (TextView) v.findViewById(R.id.duration);
        holder.content = (TextView) v.findViewById(R.id.content);
        holder.img1 = (ImageView) v.findViewById(R.id.img1);
        holder.img2 = (ImageView) v.findViewById(R.id.img2);
        holder.progress = (SeekBar) v.findViewById(R.id.progress);
        v.setTag(holder);
        return v;
    }

    private void bindData(Holder holder) {

    }


    private class Holder {
        TextView title;
        TextView duration;
        TextView content;
        ImageView img1;
        ImageView img2;
        SeekBar progress;
    }
}
