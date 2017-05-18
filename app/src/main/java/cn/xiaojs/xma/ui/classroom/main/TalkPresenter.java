package cn.xiaojs.xma.ui.classroom.main;
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
 * Date:2017/5/9
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.OnPhotoDoodleShareListener;
import cn.xiaojs.xma.ui.classroom.bean.TalkBean;
import cn.xiaojs.xma.ui.classroom.bean.TalkResponse;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.talk.FullScreenTalkMsgAdapter;
import cn.xiaojs.xma.ui.classroom.talk.OnImageClickListener;
import cn.xiaojs.xma.ui.classroom.talk.OnTalkMsgListener;
import cn.xiaojs.xma.ui.classroom.talk.TalkMsgAdapter;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;
import cn.xiaojs.xma.util.BitmapUtils;

public class TalkPresenter implements OnImageClickListener, OnPhotoDoodleShareListener {
    public final static int MULTI_TALK = 1;
    public final static int TEACHING_TALK = 2;
    public final static int PEER_TALK = 3;
    public final static int FULL_SCREEN_MULTI_TALK = 4;

    private Context mContext;

    private PullToRefreshListView mTalkMsgLv;
    private TextView mTalkNameTv;

    private FullScreenTalkMsgAdapter mFullScreenTalkMsgAdapter;
    private TalkMsgAdapter mMultiTalkAdapter;
    private TalkMsgAdapter mTeachingTalkAdapter;
    private Map<String, TalkMsgAdapter> mPeerTalkMsgAdapterMap;
    private LiveCriteria mMultiLiveCriteria;
    private LiveCriteria mTeachingCriteria;
    private LiveCriteria mPeerCriteria;
    private String mTicket;

    private OnTalkMsgListener mOnTalkMsgListener;

    private String mPeerTalkAccountId = "";
    private String mMyAccountId = "";

    private int mTalkCriteria = MULTI_TALK;
    private ProgressHUD mProgress;

    public TalkPresenter(Context context, PullToRefreshListView talkMsgLv, TextView talkTargetNameTv, String ticket) {
        mContext = context;
        mTalkMsgLv = talkMsgLv;
        mTalkNameTv = talkTargetNameTv;
        mTicket = ticket;

        mMyAccountId = AccountDataManager.getAccountID(mContext);
        mPeerTalkMsgAdapterMap = new HashMap<String, TalkMsgAdapter>();

        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.TALK), mOnReceiveTalk);

        ContactManager.getInstance().getAttendees(mContext, null);
    }

    /**
     * 切换到一对一聊天
     */
    public void switchPeerTalk(Attendee attendee, boolean needBack) {
        if (mTalkNameTv != null) {
            mTalkNameTv.setText(attendee.name);
            if (needBack) {
                mTalkNameTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_back_pressed, 0, 0, 0);
            }
        }
        switchTalkTab(PEER_TALK, attendee.accountId);
    }

    public void switchMultiTalk() {
        switchTalkTab(MULTI_TALK, null);
    }

    public void switchFullMultiTalk() {
        switchTalkTab(FULL_SCREEN_MULTI_TALK, null);
    }

    /**
     * 切换不同的talk tab
     */
    public void switchTalkTab(int criteria, String accountId) {
        switch (criteria) {
            case MULTI_TALK:
                break;
            case TEACHING_TALK:
                break;
            case PEER_TALK:
                mPeerTalkAccountId = accountId;
                //String name = getNameByAccountId(mPeerTalkAccountId);
                break;
            case FULL_SCREEN_MULTI_TALK:
                break;
        }

        getTalkMsgData(criteria, accountId);
    }

    /**
     * 获取talk消息数据
     */
    private void getTalkMsgData(int criteria, String accountId) {
        mTalkCriteria = criteria;
        switch (criteria) {
            case MULTI_TALK:
                if (mMultiTalkAdapter == null) {
                    mMultiLiveCriteria = new LiveCriteria();
                    mMultiLiveCriteria.to = String.valueOf(Communications.TalkType.OPEN);
                    mMultiTalkAdapter = new TalkMsgAdapter(mContext, mTicket, mMultiLiveCriteria, mTalkMsgLv);
                    mMultiTalkAdapter.setOnImageClickListener(this);
                }

                mTalkMsgLv.setAdapter(mMultiTalkAdapter);
                break;
            case TEACHING_TALK:
                if (mTeachingTalkAdapter == null) {
                    mTeachingCriteria = new LiveCriteria();
                    mTeachingCriteria.to = String.valueOf(Communications.TalkType.FACULTY);
                    mTeachingTalkAdapter = new TalkMsgAdapter(mContext, mTicket, mTeachingCriteria, mTalkMsgLv);
                    mTeachingTalkAdapter.setOnImageClickListener(this);
                }

                mTalkMsgLv.setAdapter(mTeachingTalkAdapter);
                break;
            case PEER_TALK:
                //重新装载数据
                TalkMsgAdapter adapter = getPeekTalkMsgAdapter(accountId);
                mTalkMsgLv.setAdapter(adapter);
                break;
            case FULL_SCREEN_MULTI_TALK:
                if (mFullScreenTalkMsgAdapter == null) {
                    mMultiLiveCriteria = new LiveCriteria();
                    mMultiLiveCriteria.to = String.valueOf(Communications.TalkType.OPEN);
                    mFullScreenTalkMsgAdapter = new FullScreenTalkMsgAdapter(mContext, mTicket, mTalkMsgLv, null);
                    mFullScreenTalkMsgAdapter.setOnImageClickListener(this);
                }

                mTalkMsgLv.setAdapter(mFullScreenTalkMsgAdapter);
                break;
        }

        scrollMsgLvToBottom();
    }

    private TalkMsgAdapter getPeekTalkMsgAdapter(String accountId) {
        TalkMsgAdapter adapter = null;
        if (mPeerTalkMsgAdapterMap.containsKey(accountId)) {
            adapter = mPeerTalkMsgAdapterMap.get(accountId);
        } else {
            LiveCriteria peerCriteria = new LiveCriteria();
            peerCriteria.to = accountId;
            adapter = new TalkMsgAdapter(mContext, mTicket, peerCriteria, mTalkMsgLv);
            mPeerTalkMsgAdapterMap.put(accountId, adapter);
            adapter.setOnImageClickListener(this);
        }

        return adapter;
    }

    /**
     * 更新talk消息数据
     *
     * @param updateMsgListView 是否更新列表
     */
    private void updateTalkMsgData(int criteria, TalkItem talkItem, boolean updateMsgListView) {
        switch (criteria) {
            case MULTI_TALK:
                if (mMultiTalkAdapter != null) {
                    mMultiTalkAdapter.add(talkItem);
                    if (updateMsgListView) {
                        mTalkMsgLv.setAdapter(mMultiTalkAdapter);
                    }
                }
                break;
            case TEACHING_TALK:
                if (mTeachingTalkAdapter != null) {
                    mTeachingTalkAdapter.add(talkItem);
                    if (updateMsgListView) {
                        mTalkMsgLv.setAdapter(mTeachingTalkAdapter);
                    }
                }
                break;
            case PEER_TALK:
                TalkMsgAdapter adapter = getPeekTalkMsgAdapter(talkItem.to);
                if (adapter != null) {
                    adapter.add(talkItem);
                    if (updateMsgListView) {
                        mTalkMsgLv.setAdapter(adapter);
                    }
                }
                break;
            case FULL_SCREEN_MULTI_TALK:
                if (mFullScreenTalkMsgAdapter != null) {
                    mFullScreenTalkMsgAdapter.add(talkItem);
                    if (updateMsgListView) {
                        mTalkMsgLv.setAdapter(mFullScreenTalkMsgAdapter);
                    }
                }
                break;
        }

        scrollMsgLvToBottom();
    }


    /**
     * 接收到消息
     */
    private SocketManager.EventListener mOnReceiveTalk = new SocketManager.EventListener() {
        @Override
        public void call(final Object... args) {
            if (args != null && args.length > 0) {
                //TODO fix同一条消息多次回调?
                handleReceivedMsg(args);
            }
        }
    };

    /**
     * 更新消息列表
     */
    private void handleReceivedMsg(Object... args) {
        if (args == null || args.length == 0) {
            return;
        }

        try {
            TalkBean receiveBean = ClassroomBusiness.parseSocketBean(args[0], TalkBean.class);
            if (receiveBean == null) {
                return;
            }

            TalkItem talkItem = new TalkItem();
            talkItem.time = receiveBean.time;
            talkItem.body = new cn.xiaojs.xma.model.live.TalkItem.TalkContent();
            talkItem.from = new cn.xiaojs.xma.model.live.TalkItem.TalkPerson();
            talkItem.body.text = receiveBean.body.text;
            talkItem.body.contentType = receiveBean.body.contentType;
            talkItem.from.accountId = receiveBean.from;
            talkItem.from.name = getNameByAccountId(receiveBean.from);

            boolean update = false;
            try {
                int talkType = Integer.parseInt(receiveBean.to);
                int criteria = PEER_TALK;
                switch (talkType) {
                    case Communications.TalkType.OPEN:
                        criteria = MULTI_TALK;
                        break;
                    case Communications.TalkType.PEER:
                        criteria = PEER_TALK;
                        break;
                    case Communications.TalkType.FACULTY:
                        criteria = TEACHING_TALK;
                        break;
                }
                update = mTalkCriteria == criteria || (mTalkCriteria == FULL_SCREEN_MULTI_TALK && criteria == MULTI_TALK);
            } catch (NumberFormatException e) {

            }
            updateTalkMsgData(mTalkCriteria, talkItem, update);
            if (mOnTalkMsgListener != null) {
                mOnTalkMsgListener.onTalkMsgReceived(talkItem);
                /*if (mDrawerOpened) {
                    if ((mPeerTalkAccountId == null && talkItem.from.accountId != null) || (mPeerTalkAccountId != null &&
                            !mPeerTalkAccountId.equals(talkItem.from.accountId))) {
                        updateUnreadMsgCount(criteria, talkItem);
                    }
                } else {
                    updateUnreadMsgCount(criteria, talkItem);
                }*/
            }
        } catch (Exception e) {

        }
    }

    /**
     * 默认是发送文本
     */
    public void sendMsg(String content) {
        sendMsg(Communications.ContentType.TEXT, content);
    }

    /**
     * 发送图片
     */
    public void sendImg(Attendee attendee, int talkCriteria, String content) {
        String text = content;
        TalkItem talkItem = new TalkItem();
        //file myself info
        fillMyselfInfo(talkItem);

        talkItem.body = new TalkItem.TalkContent();
        talkItem.body.text = text;
        talkItem.body.contentType = Communications.ContentType.STYLUS;
        if (attendee == null) {
            talkItem.to = String.valueOf(Communications.TalkType.OPEN);
        } else {
            talkItem.to = attendee.accountId;
        }

        long sendTime = System.currentTimeMillis();
        talkItem.time = sendTime;

        //update task message info
        updateTalkMsgData(talkCriteria, talkItem, true);

        //send socket info
        TalkBean talkBean = new TalkBean();
        talkBean.from = mMyAccountId;
        talkBean.body = new TalkBean.TalkContent();
        talkBean.body.text = text;
        talkBean.body.contentType = Communications.ContentType.STYLUS;
        talkBean.time = sendTime;
        if (attendee == null) {
            talkBean.to = String.valueOf(Communications.TalkType.OPEN);
        } else {
            talkBean.to = attendee.accountId;
        }
        if (talkBean != null) {
            String event = Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.TALK);
            SocketManager.emit(event, talkBean, new SocketManager.AckListener() {
                @Override
                public void call(final Object... args) {
                    handSendResponse(args);
                }
            });
        }
    }

    public void sendMsg(int type, String content) {
        String text = content;
        if (TextUtils.isEmpty(text)) {
            return;
        }

        TalkItem talkItem = new TalkItem();
        //file myself info
        fillMyselfInfo(talkItem);

        talkItem.body = new TalkItem.TalkContent();
        talkItem.body.text = text;
        talkItem.body.contentType = type;
        talkItem.to = mPeerTalkAccountId;

        long sendTime = System.currentTimeMillis();
        talkItem.time = sendTime;

        //update task message info
        updateTalkMsgData(mTalkCriteria, talkItem, true);

        //send socket info
        TalkBean talkBean = new TalkBean();
        talkBean.from = mMyAccountId;
        talkBean.body = new TalkBean.TalkContent();
        talkBean.body.text = text;
        talkBean.body.contentType = type;
        talkBean.time = sendTime;
        switch (mTalkCriteria) {
            case MULTI_TALK:
                talkBean.to = String.valueOf(Communications.TalkType.OPEN);
                break;
            case TEACHING_TALK:
                talkBean.to = String.valueOf(Communications.TalkType.FACULTY);
                break;
            case PEER_TALK:
                try {
                    talkBean.to = mPeerTalkAccountId;
                } catch (Exception e) {

                }
                break;
            case FULL_SCREEN_MULTI_TALK:
                talkBean.to = String.valueOf(Communications.TalkType.OPEN);
                break;
        }
        if (talkBean != null) {
            String event = Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.TALK);
            SocketManager.emit(event, talkBean, new SocketManager.AckListener() {
                @Override
                public void call(final Object... args) {
                    //TalkResponse
                    handSendResponse(args);
                }
            });
        }
    }

    /**
     * 处理消息发送的回调信息
     */
    private void handSendResponse(Object... args) {
        if (args == null || args.length == 0) {
            return;
        }

        try {
            TalkResponse talkResponse = null;
            talkResponse = ClassroomBusiness.parseSocketBean(args[0], TalkResponse.class);

            if (talkResponse == null) {
                return;
            }
        } catch (Exception e) {

        }
    }

    private void scrollMsgLvToBottom() {
        ListAdapter adapter = mTalkMsgLv.getRefreshableView().getAdapter();
        int count = adapter != null ? adapter.getCount() : 0;
        if (count > 0) {
            mTalkMsgLv.getRefreshableView().setSelection(count - 1);
        }
    }

    private void fillMyselfInfo(TalkItem talkItem) {
        talkItem.from = new TalkItem.TalkPerson();
        talkItem.from.accountId = mMyAccountId;
        cn.xiaojs.xma.model.account.User mLoginUser = XiaojsConfig.mLoginUser;
        if (mLoginUser != null) {
            talkItem.from.name = mLoginUser.getName();
            if (mLoginUser.getAccount() != null && mLoginUser.getAccount().getBasic() != null) {
                talkItem.from.avatar = mLoginUser.getAccount().getBasic().getAvatar();
            }
        }
    }

    private String getNameByAccountId(String accountId) {
        LiveCollection<Attendee> liveCollection = ContactManager.getInstance().getAttendees();
        if (accountId != null && liveCollection != null && liveCollection.attendees != null) {
            for (Attendee attendee : liveCollection.attendees) {
                if (accountId != null && accountId.equals(attendee.accountId)) {
                    return attendee.name;
                }
            }
        }

        return accountId;
    }

    @Override
    public void onImageClick(final int type, final String key) {
        //聊天点击大图回调到视频图片编辑页面
        if (!TextUtils.isEmpty(key)) {
            new AsyncTask<String, Integer, Bitmap>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Bitmap doInBackground(String... params) {
                    if (params == null || params.length == 0) {
                        return null;
                    }

                    String content = params[0];
                    if (TextUtils.isEmpty(content)) {
                        return null;
                    }

                    if (type == OnImageClickListener.IMG_FROM_BASE64) {
                        return ClassroomBusiness.base64ToBitmap(content);
                    } else if (type == OnImageClickListener.IMG_FROM_QINIU) {
                        try {
                            return Glide.with(mContext)
                                    .load(ClassroomBusiness.getMediaUrl(key))
                                    .asBitmap()
                                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                    .get();
                        } catch (Exception e) {
                            Logger.i(e != null ? e.getLocalizedMessage() : "null");
                        }
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap bmp) {
                    //enter video edit fragment
                    ClassroomController.getInstance().enterPhotoDoodleByBitmap(bmp);
                }
            }.execute(key);
        }
    }

    @Override
    public void onVideoShared(final Attendee attendee, final Bitmap bitmap) {
        //send msg
        showProgress(true);
        if (bitmap != null) {
            new AsyncTask<Integer, Integer, String>() {

                @Override
                protected String doInBackground(Integer... params) {
                    //resize max length to 800
                    Bitmap resizeBmp = BitmapUtils.resizeDownBySideLength(bitmap, Constants.SHARE_IMG_SIZE, false);
                    return ClassroomBusiness.bitmapToBase64(resizeBmp);
                }

                @Override
                protected void onPostExecute(String result) {
                    if (!TextUtils.isEmpty(result)) {
                        sendImg(attendee, mTalkCriteria, result);
                    } else {
                        cancelProgress();
                    }
                }
            }.execute(0);
        }
    }

    public void showProgress(boolean cancelable) {
        if (mProgress == null) {
            mProgress = ProgressHUD.create(mContext);
        }
        mProgress.setCancellable(cancelable);
        mProgress.show();
    }

    public void cancelProgress() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }
}
