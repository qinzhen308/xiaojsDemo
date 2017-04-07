package cn.xiaojs.xma.ui.message.im.chatkit.handler;

import android.content.Context;
import android.content.Intent;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMFileMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.avos.avoscloud.im.v2.messages.AVIMVideoMessage;

import de.greenrobot.event.EventBus;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.message.im.chatkit.LCChatKit;
import cn.xiaojs.xma.ui.message.im.chatkit.LCChatKitUser;
import cn.xiaojs.xma.ui.message.im.chatkit.cache.LCIMConversationItemCache;
import cn.xiaojs.xma.ui.message.im.chatkit.cache.LCIMProfileCache;
import cn.xiaojs.xma.ui.message.im.chatkit.event.LCIMIMTypeMessageEvent;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMConstants;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMLogUtils;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMNotificationUtils;

/**
 * Created by zhangxiaobo on 15/4/20.
 * AVIMTypedMessage 的 handler，socket 过来的 AVIMTypedMessage 都会通过此 handler 与应用交互
 * 需要应用主动调用 AVIMMessageManager.registerMessageHandler 来注册
 * 当然，自定义的消息也可以通过这种方式来处理
 */
public class LCIMMessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {

  private Context context;

  public LCIMMessageHandler(Context context) {
    this.context = context.getApplicationContext();
  }

  @Override
  public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
    if (message == null || message.getMessageId() == null) {
      LCIMLogUtils.d("may be SDK Bug, message or message id is null");
      return;
    }

    if (LCChatKit.getInstance().getCurrentUserId() == null) {
      LCIMLogUtils.d("selfId is null, please call LCChatKit.open!");
      client.close(null);
    } else {
      if (!client.getClientId().equals(LCChatKit.getInstance().getCurrentUserId())) {
        client.close(null);
      } else {
        if (!message.getFrom().equals(client.getClientId())) {
          if (LCIMNotificationUtils.isShowNotification(conversation.getConversationId())) {
            sendNotification(message, conversation);
          }
          LCIMConversationItemCache.getInstance().increaseUnreadCount(message.getConversationId());
          sendEvent(message, conversation);
        } else {
          LCIMConversationItemCache.getInstance().insertConversation(message.getConversationId());
        }
      }
    }
  }

  @Override
  public void onMessageReceipt(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
    super.onMessageReceipt(message, conversation, client);
  }

  /**
   * 发送消息到来的通知事件
   *
   * @param message
   * @param conversation
   */
  private void sendEvent(AVIMTypedMessage message, AVIMConversation conversation) {
    LCIMIMTypeMessageEvent event = new LCIMIMTypeMessageEvent();
    event.message = message;
    event.conversation = conversation;
    EventBus.getDefault().post(event);
  }

  private void sendNotification(final AVIMTypedMessage message, final AVIMConversation conversation) {
    if (null != conversation && null != message) {
//      String notificationContent = message instanceof AVIMTextMessage ?
//        ((AVIMTextMessage) message).getText() : context.getString(R.string.lcim_unspport_message_type);

      final String notificationContent = getNotificationContent(message);

      LCIMProfileCache.getInstance().getCachedUser(message.getFrom(), new AVCallback<LCChatKitUser>() {
        @Override
        protected void internalDone0(LCChatKitUser userProfile, AVException e) {
          if (e != null) {
            LCIMLogUtils.logException(e);
          } else if (null != userProfile) {
            String title = userProfile.getUserName();
            Intent intent = getIMNotificationIntent(conversation.getConversationId(), message.getFrom());
            LCIMNotificationUtils.showNotification(context, title, notificationContent, null, intent);
          }
        }
      });
    }
  }

  /**
   * 点击 notification 触发的 Intent
   * 注意要设置 package 已经 Category，避免多 app 同时引用 lib 造成消息干扰
   * @param conversationId
   * @param peerId
   * @return
   */
  private Intent getIMNotificationIntent(String conversationId, String peerId) {
    Intent intent = new Intent();
    intent.setAction(LCIMConstants.CHAT_NOTIFICATION_ACTION);
    intent.putExtra(LCIMConstants.CONVERSATION_ID, conversationId);
    intent.putExtra(LCIMConstants.PEER_ID, peerId);
    intent.setPackage(context.getPackageName());
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    return intent;
  }

  private String getNotificationContent(final AVIMTypedMessage message) {
    if (message instanceof AVIMTextMessage) {
      return ((AVIMTextMessage) message).getText();
    } else if (message instanceof AVIMImageMessage) {
      return context.getString(R.string.lcim_message_shorthand_image);
    } else if (message instanceof AVIMAudioMessage) {
      return context.getString(R.string.lcim_message_shorthand_audio);
    } else if (message instanceof AVIMFileMessage) {
      return context.getString(R.string.lcim_message_shorthand_file);
    } else if (message instanceof AVIMLocationMessage) {
      return context.getString(R.string.lcim_message_shorthand_location);
    } else if (message instanceof AVIMVideoMessage) {
      return context.getString(R.string.lcim_message_shorthand_video);
    } else {
      return context.getString(R.string.lcim_message_shorthand_unknown);
    }
  }
}
