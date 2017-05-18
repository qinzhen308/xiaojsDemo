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
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom.OnPanelItemClick;
import cn.xiaojs.xma.ui.classroom.main.LiveCtlSessionManager;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.MessageImageView;

public class ContactBookAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private boolean mContactManagementMode = false;
    private List<String> mChoiceList;
    private OnPortraitClickListener mListener;
    private OnPanelItemClick mOnPanelItemClick;
    private LiveCollection<Attendee> mLiveCollection;
    private ArrayList<Attendee> mAttendeeList;
    private int mOffset;
    private Constants.UserMode mUser;
    private ColorMatrixColorFilter mGrayFilter;
    private ColorMatrixColorFilter mNormalFilter;

    public ContactBookAdapter(Context context) {
        mContext = context;
        mChoiceList = new ArrayList<String>();
        mOffset = context.getResources().getDimensionPixelOffset(R.dimen.px5);
        mUser = LiveCtlSessionManager.getInstance().getUserMode();

        initColorFilter();
    }

    public void setOnPortraitClickListener(OnPortraitClickListener listener) {
        mListener = listener;
    }

    public void setOnPanelItemClick(OnPanelItemClick panelItemClick) {
        mOnPanelItemClick = panelItemClick;
    }

    public void setData(LiveCollection<Attendee> liveCollection) {
        mLiveCollection = liveCollection;
        mAttendeeList = liveCollection != null ? liveCollection.attendees : null;
        notifyDataSetChanged();
    }

    private void initColorFilter() {
        ColorMatrix grayMatrix = new ColorMatrix();
        grayMatrix.setSaturation(0);//0~1
        mGrayFilter = new ColorMatrixColorFilter(grayMatrix);

        ColorMatrix normalMatrix = new ColorMatrix();
        normalMatrix.setSaturation(1);
        mNormalFilter = new ColorMatrixColorFilter(normalMatrix);
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
        holder = (Holder) convertView.getTag();

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
        holder.talk = (ImageView) v.findViewById(R.id.talk);

        v.setOnClickListener(this);
        holder.talk.setOnClickListener(this);
        holder.video.setOnClickListener(this);

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
        int size = mContext.getResources().getDimensionPixelSize(R.dimen.px90);
        String portraitUrl = cn.xiaojs.xma.common.xf_foundation.schemas.Account.getAvatar(attendee.accountId, size);
        ColorMatrixColorFilter filter = attendee.xa == 0 ? mGrayFilter : mNormalFilter;
        holder.portrait.setColorFilter(filter);
        Glide.with(mContext)
                .load(portraitUrl)
                .transform(new CircleTransform(mContext))
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(holder.portrait);

        holder.name.setText(attendee.name);

        if (mContactManagementMode) {
            holder.video.setVisibility(View.INVISIBLE);
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setSelected(mChoiceList.contains(String.valueOf(position)));
        } else {
            holder.checkbox.setVisibility(View.GONE);
            if (mUser == Constants.UserMode.TEACHING) {
                holder.video.setVisibility(View.VISIBLE);
            }
        }

        Constants.User user = ClassroomBusiness.getUser(attendee.psType);
        switch (user) {
            case TEACHER:
                holder.label.setText(R.string.lead_teacher);
                break;
            case ASSISTANT:
                holder.label.setText(R.string.assistant_teacher);
                break;
            default:
                holder.label.setText("");
                break;
        }

        if (ClassroomBusiness.isMyself(mContext, attendee.accountId)) {
            holder.video.setVisibility(View.INVISIBLE);
        } else if (mUser == Constants.UserMode.TEACHING){
            holder.video.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        Object obj = null;
        if (v.getId() == R.id.portrait) {
            obj = ((View) v.getParent()).getTag();
        } else {
            obj = v.getTag();
            if (obj == null && v.getParent() instanceof View) {
                obj = ((View) v.getParent()).getTag();
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
                switch (v.getId()) {
                    case R.id.talk:
                        //enter chat
                        if (mOnPanelItemClick != null) {
                            mOnPanelItemClick.onItemClick(OnPanelItemClick.ACTION_OPEN_TALK, mAttendeeList.get(pos));
                        }
                        break;
                    case R.id.video:
                        if (mOnPanelItemClick != null) {
                            mOnPanelItemClick.onItemClick(OnPanelItemClick.ACTION_OPEN_CAMERA, mAttendeeList.get(pos));
                        }
                        break;
                }
            }
        }
    }

    private class Holder {
        ImageView checkbox;
        MessageImageView portrait;
        TextView name;
        TextView label;
        ImageView video;
        ImageView talk;
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
