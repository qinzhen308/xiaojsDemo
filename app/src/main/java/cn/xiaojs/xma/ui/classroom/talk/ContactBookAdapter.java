package cn.xiaojs.xma.ui.classroom.talk;
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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.ui.classroom.TalkPanel;
import cn.xiaojs.xma.ui.widget.MessageImageView;

public class ContactBookAdapter extends BaseAdapter implements View.OnClickListener{
    private Context mContext;
    private boolean mContactManagementMode = false;
    private List<String> mChoiceList;
    private OnPortraitClickListener mListener;
    private LiveCollection<Attendee> mLiveCollection;
    private ArrayList<Attendee> mAttendeeList;
    private int mOffset;

    public ContactBookAdapter(Context context) {
        mContext = context;
        mChoiceList = new ArrayList<String>();
        mOffset = context.getResources().getDimensionPixelOffset(R.dimen.px5);
    }

    public void setOnPortraitClickListener(OnPortraitClickListener listener) {
        mListener = listener;
    }

    public void setData(LiveCollection<Attendee> liveCollection) {
        mLiveCollection = liveCollection;
        mAttendeeList = liveCollection != null ? liveCollection.attendees : null;
        notifyDataSetChanged();
    }

    public LiveCollection<Attendee> getLiveCollection() {
        return mLiveCollection;
    }

    @Override
    public int getCount() {
        return mAttendeeList != null ? mAttendeeList.size() : 0;
    }

    @Override
    public Attendee getItem(int position) {
        return mAttendeeList != null ? mAttendeeList.get(position) : null;
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
        holder.portrait = (MessageImageView) v.findViewById(R.id.portrait);
        holder.name = (TextView) v.findViewById(R.id.name);
        holder.label = (TextView) v.findViewById(R.id.label);
        holder.video = (ImageView) v.findViewById(R.id.video);
        holder.microphone = (ImageView) v.findViewById(R.id.microphone);

        holder.video.setOnClickListener(this);
        holder.microphone.setOnClickListener(this);
        holder.portrait.setOnClickListener(this);
        v.setOnClickListener(this);

        //set type
        holder.portrait.setType(MessageImageView.TYPE_NUM);
        holder.portrait.setExtraOffsetX(mOffset);
        holder.portrait.setExtraOffsetY(-mOffset);

        v.setTag(holder);
        return v;
    }

    private void bindData(Holder holder, int position) {
        holder.position = position;
        Attendee attendee = mAttendeeList.get(position);
        Glide.with(mContext).load(attendee.avatar).error(R.drawable.default_avatar).into(holder.portrait);
        holder.name.setText(attendee.name);

        if (mContactManagementMode) {
            //holder.portrait.setCount(0);
            holder.video.setVisibility(View.GONE);
            holder.microphone.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setSelected(mChoiceList.contains(String.valueOf(position)));
        } else {
            holder.checkbox.setVisibility(View.GONE);
            holder.video.setVisibility(View.VISIBLE);
            holder.microphone.setVisibility(View.VISIBLE);
            //holder.portrait.setCount(9);
        }
    }

    @Override
    public void onClick(View v) {
        Object obj = null;
        if (v.getId() == R.id.portrait) {
            obj = ((View)v.getParent()).getTag();
        } else {
            obj = v.getTag();
            if (obj == null && v.getParent() instanceof View) {
                obj = ((View)v.getParent()).getTag();
            }
        }

        if (obj instanceof Holder) {
            Holder holder = (Holder) obj;
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
                            mListener.onPortraitClick(mAttendeeList.get(pos));
                        }
                        break;
                    case R.id.video:
                        s = "video";
                        break;
                    case R.id.microphone:
                        s = "microphone";
                        break;
                }

                Toast.makeText(mContext, s + pos, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Holder {
        ImageView checkbox;
        MessageImageView portrait;
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

}