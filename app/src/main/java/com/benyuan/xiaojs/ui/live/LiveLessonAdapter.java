package com.benyuan.xiaojs.ui.live;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.BaseHolder;
import com.benyuan.xiaojs.ui.widget.CanInScrollviewListView;
import com.benyuan.xiaojs.ui.widget.flow.ImageFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LiveLessonAdapter extends CanInScrollviewListView.Adapter {
    private Context mContext;

    public LiveLessonAdapter(Context context){
        mContext = context;
    }
    @Override
    public int getCount() {
        return 3;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_home_live_lesson_item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.enter.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        holder.enter.getPaint().setAntiAlias(true);
//        holder.time.setText("12:00");
//        holder.image.setImageResource(R.drawable.default_portrait);
//        holder.title.setText("titletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitle");
//        holder.stuNum.setText("111人学过");
        Bitmap b1 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
        Bitmap b2 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
        Bitmap b3 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
        Bitmap b4 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
        Bitmap b5 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
        Bitmap b6 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
        Bitmap b7 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
        Bitmap b8 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
        Bitmap b9= BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);
        Bitmap b10 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_center_shader);

        List<Bitmap> list = new ArrayList<>();
        list.add(b1);
        list.add(b2);
        list.add(b3);
        list.add(b4);
        list.add(b5);
        list.add(b6);
        list.add(b7);
        list.add(b8);
        list.add(b9);
        list.add(b10);
        //holder.imageFlow.showWithNum(list,mContext.getResources().getDimensionPixelSize(R.dimen.px20),mContext.getResources().getDimensionPixelSize(R.dimen.px2));
        holder.imageFlow.show(list,mContext.getResources().getDimensionPixelSize(R.dimen.px20),mContext.getResources().getDimensionPixelSize(R.dimen.px5));
        return convertView;
    }

    class Holder extends BaseHolder {

        @BindView(R.id.image_flow)
        ImageFlowLayout imageFlow;
        @BindView(R.id.live_enter)
        TextView enter;

        public Holder(View view) {
            super(view);
        }
    }
}
