package cn.xiaojs.xma.ui.widget.function;
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
 * Date:2016/10/31
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.xiaojs.xma.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FunctionItemAdapter extends BaseAdapter {
    private List<FunctionItemBean> beans;
    private Context mContext;

    public FunctionItemAdapter(Context context,List<FunctionItemBean> beans){
        this.beans = beans;
        mContext = context;
    }
    @Override
    public int getCount() {
        return beans == null ? 0 : beans.size();
    }

    @Override
    public Object getItem(int i) {
        return beans == null ? null : beans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_function_area_item,null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        if (!TextUtils.isEmpty(beans.get(i).text)){
            holder.text.setText(beans.get(i).text);
        }
        view.setOnClickListener(new OnItemClick(beans.get(i)));
        return view;
    }

    static class ViewHolder{
        @BindView(R.id.function_area_item_image)
        ImageView image;
        @BindView(R.id.function_area_item_text)
        TextView text;

       public ViewHolder(View v){
           ButterKnife.bind(this,v);
       }
    }

    class OnItemClick implements View.OnClickListener{

        private FunctionItemBean bean;
        public OnItemClick(FunctionItemBean bean){
            this.bean = bean;
        }

        @Override
        public void onClick(View view) {
            Class cls = getParamClass();
            if (cls != null){
                Intent intent = new Intent(mContext,cls);
                mContext.startActivity(intent);
            }
        }

        private Class getParamClass(){
            try {
                return Class.forName(bean.param);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
