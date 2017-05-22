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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsChatAdapter;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.OnPhotoDoodleShareListener;
import cn.xiaojs.xma.ui.classroom.PhotoDoodleFragment;
import cn.xiaojs.xma.ui.classroom.talk.FullScreenTalkMsgAdapter;
import cn.xiaojs.xma.ui.classroom.talk.OnImageClickListener;
import cn.xiaojs.xma.ui.classroom.talk.TalkMsgAdapter;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;
import cn.xiaojs.xma.util.BitmapUtils;

public class TalkPresenter implements OnImageClickListener, OnPhotoDoodleShareListener, TalkManager.OnTalkMsgReceived {
    private Context mContext;

    private PullToRefreshListView mTalkMsgLv;
    private TextView mTalkNameTv;
    private String mMyAccountId = "";

    private int mTalkCriteria = TalkManager.TYPE_MSG_MUlTI_TAlk;
    private ProgressHUD mProgress;

    public TalkPresenter(Context context, PullToRefreshListView talkMsgLv, TextView talkTargetNameTv) {
        mContext = context;
        mTalkMsgLv = talkMsgLv;
        mTalkNameTv = talkTargetNameTv;

        mMyAccountId = AccountDataManager.getAccountID(mContext);
        ContactManager.getInstance().getAttendees(mContext, null);
        TalkManager.getInstance().registerMsgReceiveListener(this);
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
        switchTalkTab(TalkManager.TYPE_PEER_TALK, attendee.accountId);
    }

    public void switchMsgMultiTalk() {
        switchTalkTab(TalkManager.TYPE_MSG_MUlTI_TAlk, null);
    }

    public void switchFullMultiTalk() {
        switchTalkTab(TalkManager.TYPE_FULL_SCREEN_MUlTI_TAlk, null);
    }

    /**
     * 切换不同的talk tab
     */
    public void switchTalkTab(int criteria, String accountId) {
        switchTalkTab(criteria, accountId, null);
    }

    /**
     * 切换不同的talk tab
     */
    public void switchTalkTab(int criteria, String accountId, TalkItem talkItem) {
        AbsChatAdapter adapter = null;
        mTalkCriteria = criteria;
        switch (criteria) {
            case TalkManager.TYPE_MSG_MUlTI_TAlk:
            case TalkManager.TYPE_FULL_SCREEN_MUlTI_TAlk:
            case TalkManager.TYPE_TEACHING_TALK:
                adapter = TalkManager.getInstance().getChatAdapter(mContext, criteria, mTalkMsgLv);
                if (adapter != null) {
                    mTalkMsgLv.setAdapter(adapter);
                }
                break;
            case TalkManager.TYPE_PEER_TALK:
                adapter = TalkManager.getInstance().getChatAdapter(mContext, criteria, accountId, mTalkMsgLv);
                if (adapter != null) {
                    mTalkMsgLv.setAdapter(adapter);
                }
                break;
        }

        if (adapter instanceof TalkMsgAdapter) {
            ((TalkMsgAdapter)adapter).setOnImageClickListener(this);
        } else if (adapter instanceof FullScreenTalkMsgAdapter) {
            ((FullScreenTalkMsgAdapter)adapter).setOnImageClickListener(this);
        }

        if (adapter != null && talkItem != null) {
            adapter.add(talkItem);
        }

        scrollMsgLvToBottom();
    }

    public int getTalkCriteria() {
        return mTalkCriteria;
    }

    private void scrollMsgLvToBottom() {
        ListAdapter adapter = mTalkMsgLv.getRefreshableView().getAdapter();
        int count = adapter != null ? adapter.getCount() : 0;
        if (count > 0) {
            mTalkMsgLv.getRefreshableView().setSelection(count - 1);
        }
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
                    ClassroomController.getInstance().enterPhotoDoodleByBitmap(bmp, TalkPresenter.this);
                }
            }.execute(key);
        }
    }

    @Override
    public void onPhotoShared(final Attendee attendee, final Bitmap bitmap) {
        //exit
        Fragment fragment = ((FragmentActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.photo_doodle_layout);
        if (fragment instanceof PhotoDoodleFragment) {
            ((ClassroomActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commit();
        }

        //send msg
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
                        TalkManager.getInstance().sendImg(attendee.accountId, mTalkCriteria, result);
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

    @Override
    public void onMsgChanged(int criteria, TalkItem talkItem) {
        switchTalkTab(mTalkCriteria, talkItem.to);
    }
}
