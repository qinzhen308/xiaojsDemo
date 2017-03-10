package cn.xiaojs.xma.ui.message;

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
 * Date:2016/10/11
 * Desc:
 *
 * ======================================================================================== */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.eventbus.EventBus;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.im.ChatActivity;
import cn.xiaojs.xma.common.im.utils.HandleResponseCode;
import cn.xiaojs.xma.data.NotificationDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.GNOResponse;
import cn.xiaojs.xma.model.NotificationCategory;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.BaseFragment;

public class NotificationFragment extends BaseFragment {

//    @BindView(R.id.listview)
//    PullToRefreshSwipeListView mPullList;

    @BindView(R.id.home_message_list)
    ListView mMessageList;
    //View mHeader;
    //CanInScrollviewListView mListView;

    PlatformNotificationAdapter platformMessageAdapter;

    NotificationAdapter notificationAdapter;

    private String[] titles;
    private List<NotificationCategory> mMessages;

    private HandlerThread mThread;
    private BackgroundHandler mBackgroundHandler;
    private static final int REFRESH_CONVERSATION_LIST = 0x3000;

    @Override
    protected View getContentView() {
        View v = mContext.getLayoutInflater().inflate(R.layout.fragment_message, null);
        return v;
    }

    @Override
    protected void init() {
        //设置新消息接收监听
        EventBus.getDefault().register(this);
        mThread = new HandlerThread("Work on MainActivity");
        mThread.start();
        mBackgroundHandler = new BackgroundHandler(mThread.getLooper());
        mMessages = new ArrayList<>();
        getMessageOverview();
        //mListView.setAdapter(platformMessageAdapter);
//        mListView.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                String title = titles[position];
//                NotificationCategory category = platformMessageAdapter.getItem(position);
//                String categoryId = "";
//                if (category != null){
//                    categoryId = category.id;
//                }
//
//                Intent intent = new Intent(mContext,NotificationCategoryListActivity.class);
//                intent.putExtra(NotificationConstant.KEY_NOTIFICATION_CATEGORY_ID,categoryId);
//                intent.putExtra(NotificationConstant.KEY_NOTIFICATION_TITLE,title);
//                ((BaseActivity)mContext).startActivityForResult(intent,NotificationConstant.REQUEST_NOTIFICATION_CATEGORY_LIST);
//            }
//        });
        //mPullList.getRefreshableView().addHeaderView(mHeader);
//        View view = new View(mContext);
//        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mContext.getResources().getDimensionPixelSize(R.dimen.px100));
//        view.setLayoutParams(lp);
        //mPullList.getRefreshableView().addFooterView(view);
        //notificationAdapter = new NotificationAdapter(mContext,mPullList,this);
        //mPullList.setAdapter(notificationAdapter);
    }

    private void getMessageOverview() {
        Pagination pagination = new Pagination();
        pagination.setPage(1);
        pagination.setMaxNumOfObjectsPerPage(10);
        NotificationDataManager.requestNotificationsOverview(mContext, pagination, new APIServiceCallback<GNOResponse>() {
            @Override
            public void onSuccess(GNOResponse object) {
                if (object.categories != null) {
                    List<NotificationCategory> platform = NotificationBusiness.getPlatformMessageCategory(object.categories);
                    if (platform != null) {//从返回的数据中提出平台类型消息，放到头部显示区域
                        mMessages.addAll(platform);
                    } else {
                        mMessages.addAll(NotificationBusiness.getPlatformCategory());
                    }
                    getConversation();
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                mMessages.addAll(NotificationBusiness.getPlatformCategory());
                getConversation();
            }
        });
    }

    private void getConversation() {
        List<Conversation> conversations = JMessageClient.getConversationList();
        if (conversations != null) {
            for (Conversation conversation : conversations) {
                NotificationCategory category = new NotificationCategory();
                category.conversation = conversation;
                mMessages.add(category);
            }
        }
        initView();
    }

    private void initView() {
        titles = mContext.getResources().getStringArray(R.array.platform_message_title);
        platformMessageAdapter = new PlatformNotificationAdapter(mContext, mMessages,titles);
        mMessageList.setAdapter(platformMessageAdapter);
        mMessageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 7) {
                    //进入平台消息列表界面
                    String title = titles[position];
                    NotificationCategory category = NotificationBusiness.getPlatformMessageCategory(platformMessageAdapter.getBeans(),position);
                    String categoryId = "";
                    if (category != null) {
                        categoryId = category.id;
                    }

                    Intent intent = new Intent(mContext, NotificationCategoryListActivity.class);
                    intent.putExtra(NotificationConstant.KEY_NOTIFICATION_CATEGORY_ID, categoryId);
                    intent.putExtra(NotificationConstant.KEY_NOTIFICATION_TITLE, title);
                    ((BaseActivity) mContext).startActivityForResult(intent, NotificationConstant.REQUEST_NOTIFICATION_CATEGORY_LIST);
                } else {
                    //进入聊天界面
                    final Intent intent = new Intent();
                    NotificationCategory category = platformMessageAdapter.getItem(position);
                    if (category != null) {
                        Conversation conv = category.conversation;
//                        intent.putExtra(ChatActivity.CONV_TITLE, conv.getTitle());
                        if (null != conv) {
                            // 当前点击的会话是否为群组
                            if (conv.getType() == ConversationType.group) {
                                long groupId = ((GroupInfo) conv.getTargetInfo()).getGroupID();
                                intent.putExtra(ChatActivity.GROUP_ID, groupId);
                                //intent.putExtra(ChatActivity.DRAFT, getAdapter().getDraft(conv.getId()));
                                intent.setClass(mContext, ChatActivity.class);
                                mContext.startActivity(intent);
                                return;
                            } else {
                                String targetId = ((UserInfo) conv.getTargetInfo()).getUserName();
                                intent.putExtra(ChatActivity.TARGET_ID, targetId);
                                intent.putExtra(ChatActivity.TARGET_APP_KEY, conv.getTargetAppKey());
                                Log.d("ConversationList", "Target app key from conversation: " + conv.getTargetAppKey());
                                //intent.putExtra(JChatDemoApplication.DRAFT, getAdapter().getDraft(conv.getId()));
                            }
                            intent.setClass(mContext, ChatActivity.class);
                            mContext.startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    public void notifyConversation(){
        if (platformMessageAdapter != null){
            List<Conversation> conversations = JMessageClient.getConversationList();
            platformMessageAdapter.notifyConversation(conversations);
        }
    }

    /**
     * 在会话列表中接收消息
     *
     * @param event 消息事件
     */
    public void onEvent(MessageEvent event) {
        Message msg = event.getMessage();
        Log.d(this.getClass().getName(), "收到消息：msg = " + msg.toString());
        ConversationType convType = msg.getTargetType();
        if (convType == ConversationType.group) {
            long groupID = ((GroupInfo) msg.getTargetInfo()).getGroupID();
            Conversation conv = JMessageClient.getGroupConversation(groupID);
            if (conv != null) {
                mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST,
                        conv));
            }
        } else {
            final UserInfo userInfo = (UserInfo) msg.getTargetInfo();
            final String targetID = userInfo.getUserName();
            final Conversation conv = JMessageClient.getSingleConversation(targetID, userInfo.getAppKey());
            if (conv != null) {
//                mContext.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //如果设置了头像
//                        if (!TextUtils.isEmpty(userInfo.getAvatar())) {
//                            //如果本地不存在头像
//                            userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
//                                @Override
//                                public void gotResult(int status, String desc, Bitmap bitmap) {
//                                    if (status == 0) {
//                                        platformMessageAdapter.notifyDataSetChanged();
//                                    } else {
//                                        HandleResponseCode.onHandle(mContext, status, false);
//                                    }
//                                }
//                            });
//                        }
//                    }
//                });
                mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST,
                        conv));
            }
        }
    }

    private class BackgroundHandler extends Handler {
        public BackgroundHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_CONVERSATION_LIST:
                    Conversation conv = (Conversation) msg.obj;
                    platformMessageAdapter.setToTop(conv);
                    break;
            }
        }
    }


    public void notifyHeader(List<NotificationCategory> beans) {
        if (platformMessageAdapter != null) {
            platformMessageAdapter.setData(beans);
        }
    }

    @OnClick({R.id.right_image, R.id.people_image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.right_image:
                break;
            case R.id.people_image:
                startActivity(new Intent(getActivity(), ContactActivity.class));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NotificationConstant.REQUEST_NOTIFICATION_CATEGORY_LIST:
                if (resultCode == Activity.RESULT_OK) {
                    if (notificationAdapter != null) {
                        notificationAdapter.refresh();
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mBackgroundHandler.removeCallbacksAndMessages(null);
        mThread.getLooper().quit();
        super.onDestroy();
    }
}
