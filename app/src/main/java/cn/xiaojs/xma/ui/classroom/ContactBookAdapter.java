package cn.xiaojs.xma.ui.classroom;
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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.RoundedImageView;

public class ContactBookAdapter extends BaseAdapter implements View.OnClickListener{
    private Context mContext;
    private boolean mContactManagementMode = false;
    private List<String> mChoiceList;
    private OnContactBookListener mListener;

    public ContactBookAdapter(Context context) {
        mContext = context;
        mChoiceList = new ArrayList<String>();
    }

    public void setOnContactBookListener(OnContactBookListener listener) {
        mListener = listener;
    }

    @Override
    public int getCount() {
        //TODO test count
        return 10;
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

        bindData(holder, position);
        return convertView;
    }

    private View createContentView() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_classroom_contact_item, null);
        Holder holder = new Holder();
        holder.checkbox = (ImageView) v.findViewById(R.id.checkbox);
        holder.portrait = (RoundedImageView) v.findViewById(R.id.portrait);
        holder.name = (TextView) v.findViewById(R.id.name);
        holder.label = (TextView) v.findViewById(R.id.label);
        holder.video = (ImageView) v.findViewById(R.id.video);
        holder.microphone = (ImageView) v.findViewById(R.id.microphone);

        holder.video.setOnClickListener(this);
        holder.microphone.setOnClickListener(this);
        holder.portrait.setOnClickListener(this);
        v.setOnClickListener(this);

        holder.video.setTag(holder);
        holder.microphone.setTag(holder);
        holder.portrait.setTag(holder);

        v.setTag(holder);
        return v;
    }

    private void bindData(Holder holder, int position) {
        holder.portrait.setImageResource(R.drawable.default_portrait);
        holder.position = position;
        if (mContactManagementMode) {
            holder.video.setVisibility(View.GONE);
            holder.microphone.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setSelected(mChoiceList.contains(String.valueOf(position)));
        } else {
            holder.checkbox.setVisibility(View.GONE);
            holder.video.setVisibility(View.VISIBLE);
            holder.microphone.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        Object obj = v.getTag();
        if (obj instanceof Holder) {
            Holder holder = (Holder) v.getTag();
            int pos = holder.position;

            if (mContactManagementMode) {
                String choice = String.valueOf(pos);
                if (mChoiceList.contains(choice)) {
                    holder.checkbox.setSelected(false);
                    mChoiceList.remove(choice);
                } else {
                    holder.checkbox.setSelected(true);
                    mChoiceList.add(choice);
                }
            } else {
                String s = "";
                switch (v.getId()) {
                    case R.id.portrait:
                        s = "portrait";
                        //enter chat
                        if (mListener != null) {
                            mListener.onPortraitClick();
                        }
                        break;
                    case R.id.video:
                        s = "video";
                        break;
                    case R.id.microphone:
                        s = "microphone";
                        break;
                }

                //Toast.makeText(mContext, s + pos, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Holder {
        ImageView checkbox;
        RoundedImageView portrait;
        TextView name;
        TextView label;
        ImageView video;
        ImageView microphone;
        int position = -1;
    }

    public void enterManagementMode() {
        mContactManagementMode = true;
        if (mChoiceList != null) {
            mChoiceList.clear();
        }
        notifyDataSetChanged();
    }

    public void exitManagementMode() {
        mContactManagementMode = false;
        notifyDataSetChanged();
    }

    public interface OnContactBookListener {
        public void onPortraitClick();
    }
}
