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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.MessageImageView;

public class ContactBookAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private boolean mContactManagementMode = false;
    private List<String> mChoiceList;
    private OnAttendItemClick mOnAttendItemClick;
    private LiveCollection<Attendee> mLiveCollection;
    private ArrayList<Attendee> mAttendeeList;
    private int mOffset;
    private int mUser;
    private ColorMatrixColorFilter mGrayFilter;
    private ColorMatrixColorFilter mNormalFilter;
    private int mSize = 90;

    public ContactBookAdapter(Context context) {
        mContext = context;
        mChoiceList = new ArrayList<String>();
        mOffset = context.getResources().getDimensionPixelOffset(R.dimen.px5);
        mUser = ClassroomEngine.getEngine().getLiveMode();
        mSize = mContext.getResources().getDimensionPixelSize(R.dimen.px90);

        initColorFilter();
    }

    public void setOnPanelItemClick(OnAttendItemClick panelItemClick) {
        mOnAttendItemClick = panelItemClick;
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
        holder.labelSecond = (TextView) v.findViewById(R.id.label_second);
        holder.video = (ImageView) v.findViewById(R.id.video);
        holder.talk = (MessageImageView) v.findViewById(R.id.talk);

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
        String portraitUrl = cn.xiaojs.xma.common.xf_foundation.schemas.Account.getAvatar(attendee.accountId, mSize);
        ColorMatrixColorFilter filter = attendee.xa == 0 ? mGrayFilter : mNormalFilter;
        holder.portrait.setColorFilter(filter);
        Glide.with(mContext)
                .load(portraitUrl)
                .transform(new CircleTransform(mContext))
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(holder.portrait);
        //holder.portrait.setCount(attendee.unReadMsgCount);

        holder.name.setText(attendee.name);

        //set checkbox
        if (mContactManagementMode) {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setSelected(mChoiceList.contains(String.valueOf(position)));
        } else {
            holder.checkbox.setVisibility(View.GONE);
        }

        String liveState = ClassroomEngine.getEngine().getLiveState();
        boolean isMyself = ClassroomBusiness.isMyself(mContext, attendee.accountId);
        boolean isSupport = (attendee.avc != null && attendee.avc.video != null && attendee.avc.audio != null)
                ? (attendee.avc.video.supported && attendee.avc.audio.supported) : true;
        boolean online = attendee.xa == 0 ? false : true;


        ClassroomEngine classroomEngine = ClassroomEngine.getEngine();

        CTLConstant.UserIdentity user = classroomEngine.getUserIdentity(attendee.psType);
        CTLConstant.UserIdentity userInLesson = classroomEngine.getUserIdentity(attendee.psTypeInLesson);


        if (Live.LiveSessionState.LIVE.equals(liveState) || Live.LiveSessionState.DELAY.equals(liveState)) {//上课中或者拖堂

            if (!mContactManagementMode
                    && mUser == Live.ClassroomMode.TEACHING
                    && (classroomEngine.getIdentity() == CTLConstant.UserIdentity.LEAD)//上课中只有主讲才能主动发1对1
                    && (userInLesson == CTLConstant.UserIdentity.STUDENT || user == CTLConstant.UserIdentity.STUDENT)
                    && !isMyself
                    && isSupport
                    && online) {
                holder.video.setVisibility(View.VISIBLE);
            } else {
                holder.video.setVisibility(View.INVISIBLE);
            }


        } else if(ClassroomEngine.getEngine().liveShow()) {//正在直播秀状态

            if (!mContactManagementMode
                    && !isMyself
                    && isSupport
                    && online) {

                holder.video.setVisibility(View.VISIBLE);

            } else {
                holder.video.setVisibility(View.INVISIBLE);
            }

        } else {
            holder.video.setVisibility(View.INVISIBLE);
        }





        holder.talk.setVisibility(isMyself ? View.INVISIBLE : View.VISIBLE);
        holder.talk.setCount(attendee.unReadMsgCount);


        if (userInLesson == CTLConstant.UserIdentity.NONE) {

            bindUserView(holder.label, user);
            holder.labelSecond.setText("");
            holder.labelSecond.setVisibility(View.GONE);

            if (user == CTLConstant.UserIdentity.STUDENT) {
                holder.label.setVisibility(View.GONE);
            } else {
                holder.label.setVisibility(View.VISIBLE);
            }

        } else {

            bindUserView(holder.label, userInLesson);
            bindUserView(holder.labelSecond, user);
            holder.labelSecond.setVisibility(View.VISIBLE);

            if (userInLesson == CTLConstant.UserIdentity.STUDENT
                    || userInLesson == CTLConstant.UserIdentity.NONE) {

                holder.label.setVisibility(View.GONE);
            } else {
                holder.label.setVisibility(View.VISIBLE);
            }
        }


    }

    private void bindUserView(TextView v, CTLConstant.UserIdentity user) {
        switch (user) {
            case LEAD:
                v.setText(R.string.lead_teacher);
                break;
            case TEACHER2:
                v.setText(R.string.teacher);
                break;
            case ASSISTANT:
                v.setText(R.string.assistant_teacher);
                break;
            case REMOTE_ASSISTANT://远程助教
                v.setText(R.string.remote_assistant_session);
                break;
            case ADVISER:
                v.setText(R.string.head_teacher);
                break;
            case MANAGER:
                v.setText(R.string.manager_session);
                break;
            case ADMINISTRATOR://管理员
                v.setText(R.string.admin_session);
                break;
            case AUDITOR://旁听
                v.setText(R.string.auditor_session);
                break;
            default:
                v.setText("");
                break;

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
                String liveState = ClassroomEngine.getEngine().getLiveState();
                Attendee attendee = mAttendeeList.get(pos);
                if (mOnAttendItemClick != null) {
                    CTLConstant.UserIdentity user = ClassroomEngine.getEngine().getUserIdentity(attendee.psType);
                        switch (v.getId()) {
                            case R.id.talk:
                                //enter chat
                                if ((Live.LiveSessionState.LIVE.equals(liveState) || Live.LiveSessionState.DELAY.equals(liveState))
                                        && user == CTLConstant.UserIdentity.STUDENT
                                        && ClassroomEngine.getEngine().getIdentity() == CTLConstant.UserIdentity.STUDENT) {
                                    //Toast
                                    Toast.makeText(mContext, R.string.cr_live_forbid_talk, Toast.LENGTH_SHORT).show();
                                }else {
                                    attendee.unReadMsgCount = 0;
                                    mOnAttendItemClick.onItemClick(OnAttendItemClick.ACTION_OPEN_TALK, attendee);
                                }

                                break;
                            case R.id.video:
                                if (mOnAttendItemClick != null) {
                                    if (attendee.xa == 0) {
                                        Toast.makeText(mContext, R.string.offline_peer_to_peer_tips, Toast.LENGTH_SHORT).show();
                                    } else if (ClassroomEngine.getEngine().one2one()){
                                        Toast.makeText(mContext, "您正在1对1视频～", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mOnAttendItemClick.onItemClick(OnAttendItemClick.ACTION_OPEN_CAMERA, attendee);
                                    }
                                }
                                break;
                            default:
                                //enter chat
//                                if (!ClassroomBusiness.isMyself(mContext, attendee.accountId)) {
//                                    attendee.unReadMsgCount = 0;
//                                    mOnAttendItemClick.onItemClick(OnAttendItemClick.ACTION_OPEN_TALK, attendee);
//                                }
                                break;
                        }


//                    Constants.User user = ClassroomBusiness.getUser(attendee.psType, Constants.User.STUDENT);
//                    if ((Live.LiveSessionState.LIVE.equals(liveState) || Live.LiveSessionState.DELAY.equals(liveState))
//                            && user == Constants.User.STUDENT
//                            && LiveCtlSessionManager.getInstance().getUser() == Constants.User.STUDENT) {
//                        //Toast
//                        Toast.makeText(mContext, R.string.cr_live_forbid_talk, Toast.LENGTH_SHORT).show();
//                    } else {
//                        switch (v.getId()) {
//                            case R.id.talk:
//                                //enter chat
//                                attendee.unReadMsgCount = 0;
//                                mOnAttendItemClick.onItemClick(OnAttendItemClick.ACTION_OPEN_TALK, attendee);
//                                break;
//                            case R.id.video:
//                                if (mOnAttendItemClick != null) {
//
//                                    if (attendee.xa == 0) {
//                                        Toast.makeText(mContext, R.string.offline_peer_to_peer_tips, Toast.LENGTH_SHORT).show();
//                                    }else{
//                                        mOnAttendItemClick.onItemClick(OnAttendItemClick.ACTION_OPEN_CAMERA, attendee);
//                                    }
//                                }
//                                break;
//                            default:
//                                //enter chat
//                                if (!ClassroomBusiness.isMyself(mContext, attendee.accountId)) {
//                                    attendee.unReadMsgCount = 0;
//                                    mOnAttendItemClick.onItemClick(OnAttendItemClick.ACTION_OPEN_TALK, attendee);
//                                }
//                                break;
//                        }
//
//                    }
                    }
                }
            }
        }

        private class Holder {
            ImageView checkbox;
            MessageImageView portrait;
            TextView name;
            TextView label;
            TextView labelSecond;
            ImageView video;
            MessageImageView talk;
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
