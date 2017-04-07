package cn.xiaojs.xma.ui.message.im.chatkit.viewholder;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMReservedMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.platform.NotificationTemplate;
import cn.xiaojs.xma.model.Notification;
import cn.xiaojs.xma.model.NotificationCategory;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.message.NotificationCategoryListActivity;
import cn.xiaojs.xma.ui.message.NotificationConstant;
import cn.xiaojs.xma.ui.message.im.chatkit.LCChatMessageInterface;
import cn.xiaojs.xma.ui.message.im.chatkit.cache.LCIMConversationItemCache;
import cn.xiaojs.xma.ui.message.im.chatkit.event.LCIMConversationItemLongClickEvent;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMConstants;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMConversationUtils;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMLogUtils;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.MessageImageView;
import cn.xiaojs.xma.util.TimeUtil;

/**
 * Created by wli on 15/10/8.
 * 会话 item 对应的 holder
 */
public class LCIMConversationItemHolder extends LCIMCommonViewHolder {

  public static final String ACTION_UPDATE = "lc_notify_update";

  ImageView avatarView;
  TextView unreadView;
  TextView noUnReadView;
  TextView messageView;
  TextView timeView;
  TextView nameView;
  RelativeLayout avatarLayout;
  LinearLayout contentLayout;

  private final Object LOCK = new Object();
  private List<Target<GlideDrawable>> mTargets = new ArrayList<Target<GlideDrawable>>();


  public LCIMConversationItemHolder(ViewGroup root) {
    super(root.getContext(), root, R.layout.lcim_conversation_item);
    initView();
  }

  public void initView() {
    avatarView = (ImageView) itemView.findViewById(R.id.conversation_item_iv_avatar);
    nameView = (TextView) itemView.findViewById(R.id.conversation_item_tv_name);

    timeView = (TextView) itemView.findViewById(R.id.conversation_item_tv_time);
    unreadView = (TextView) itemView.findViewById(R.id.conversation_item_tv_unread);
    noUnReadView = (TextView) itemView.findViewById(R.id.notify_item_tv_unread);
    messageView = (TextView) itemView.findViewById(R.id.conversation_item_tv_message);
    avatarLayout = (RelativeLayout) itemView.findViewById(R.id.conversation_item_layout_avatar);
    contentLayout = (LinearLayout) itemView.findViewById(R.id.conversation_item_layout_content);
  }

  @Override
  public void bindData(Object o) {
    //reset();
    synchronized (LOCK) {
      for (Target<GlideDrawable> target : mTargets) {
         target.getRequest().pause();
      }

      mTargets.clear();
    }
    final AVIMConversation conversation = (AVIMConversation) o;
    if (conversation instanceof NotificationCategory) {

      unreadView.setVisibility(View.GONE);

      final NotificationCategory category = (NotificationCategory) conversation;
      Log.i("aaa", "name="+category.getName()+"  this="+this);
      if(category.getName().equalsIgnoreCase(NotificationTemplate.INVITATION_NOTIFICATION)) {
        avatarView.setImageResource(R.drawable.ic_message_invite);
      }else if(category.getName().equalsIgnoreCase(NotificationTemplate.FOLLOW_NOTIFICATION)) {
        avatarView.setImageResource(R.drawable.ic_message_socialnews);
      }else if(category.getName().equalsIgnoreCase(NotificationTemplate.ANSWERS_NOTIFICATION)) {
        avatarView.setImageResource(R.drawable.ic_message_qanswerme);
      }else if(category.getName().equalsIgnoreCase(NotificationTemplate.ARTICLE_NOTIFICATION)) {
        avatarView.setImageResource(R.drawable.ic_message_transactionmessage);
      }else if(category.getName().equalsIgnoreCase(NotificationTemplate.CTL_NOTIFICATION)) {
        avatarView.setImageResource(R.drawable.ic_message_course_information);
      }else if(category.getName().equalsIgnoreCase(NotificationTemplate.FINANCE_NOTIFICATION)) {
        avatarView.setImageResource(R.drawable.ic_message_recommendedselection);
      }else if(category.getName().equalsIgnoreCase(NotificationTemplate.PLATFORM_NOTIFICATION)) {
        avatarView.setImageResource(R.drawable.ic_xjs_msg);
      } else {
        avatarView.setImageResource(R.drawable.default_avatar_grey);
      }
      nameView.setText(category.remarks);

      ArrayList<Notification> notifications = category.notifications;
      if (category.count <= 0) {
        noUnReadView.setVisibility(View.GONE);
        if (notifications!=null && notifications.size()>0) {
          Notification notify = notifications.get(0);
          timeView.setText(TimeUtil.getTimeByNow(notify.createdOn));
          messageView.setText(notify.body);
        }else{
          timeView.setText("");
          messageView.setText("");
        }
      }else {

        noUnReadView.setVisibility(View.VISIBLE);

        Notification notify = notifications.get(0);
        timeView.setText(TimeUtil.getTimeByNow(notify.createdOn));
        messageView.setText(notify.body);
      }

      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

          enterCategoryList(category);

          if (category.count> 0) {
             category.count = 0;

            getContext().sendBroadcast(new Intent(ACTION_UPDATE));

          }
        }
      });


    }else {

      noUnReadView.setVisibility(View.GONE);

      if (null != conversation) {
        if (null == conversation.getCreatedAt()) {
          conversation.fetchInfoInBackground(new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
              if (e != null) {
                LCIMLogUtils.logException(e);
                avatarView.setImageResource(R.drawable.default_avatar_grey);
                nameView.setText("");
              } else {
                updateName(conversation);
                updateIcon(conversation);
              }
            }
          });
        } else {
          updateName(conversation);
          updateIcon(conversation);
          Log.i("aaa",  "===========this="+this);
        }

        updateUnreadCount(conversation);
        updateLastMessage(conversation.getLastMessage());
        itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            onConversationItemClick(conversation);
          }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
          @Override
          public boolean onLongClick(View v) {
            //FIXME
//          AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//          builder.setItems(new String[]{"删除该聊天"}, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//              EventBus.getDefault().post(new LCIMConversationItemLongClickEvent(conversation));
//            }
//          });
//          AlertDialog dialog = builder.create();
//          dialog.show();
            return false;
          }
        });
      }else{
        nameView.setText("");
        timeView.setText("");
        messageView.setText("");
        avatarView.setImageResource(R.drawable.default_avatar_grey);
        unreadView.setVisibility(View.GONE);
      }
    }

  }

  /**
   * 一开始的时候全部置为空，避免因为异步请求造成的刷新不及时而导致的展示原有的缓存数据
   */
  private void reset() {
    avatarView.setImageResource(R.drawable.default_avatar_grey);
    nameView.setText("");
    timeView.setText("");
    messageView.setText("");
    unreadView.setVisibility(View.GONE);
    noUnReadView.setVisibility(View.GONE);
  }

  /**
   * 更新 name，单聊的话展示对方姓名，群聊展示所有用户的用户名
   *
   * @param conversation
   */
  private void updateName(AVIMConversation conversation) {
    LCIMConversationUtils.getConversationName(conversation, new AVCallback<String>() {
      @Override
      protected void internalDone0(String s, AVException e) {
        if (null != e) {
          LCIMLogUtils.logException(e);
          nameView.setText("");
        } else {
          nameView.setText(s);
        }
      }
    });
  }

  /**
   * 更新 item icon，目前的逻辑为：
   * 单聊：展示对方的头像
   * 群聊：展示一个静态的 icon
   *
   * @param conversation
   */
  private void updateIcon(AVIMConversation conversation) {
    if (null != conversation) {
      if (conversation.isTransient() || conversation.getMembers().size() > 2) {
        avatarView.setImageResource(R.drawable.lcim_group_icon);
      } else {
        LCIMConversationUtils.getConversationPeerIcon(conversation, new AVCallback<String>() {
          @Override
          protected void internalDone0(String s, AVException e) {
            if (null != e) {
              LCIMLogUtils.logException(e);
            }
            if (!TextUtils.isEmpty(s)) {
              Target<GlideDrawable> target =  Glide.with(getContext()).load(s)
                      .bitmapTransform(circleTransform)
                      .placeholder(R.drawable.default_avatar_grey)
                      .error(R.drawable.default_avatar_grey)
                      .into(avatarView);
              synchronized (LOCK) {
                mTargets.add(target);
              }
//              Picasso.with(getContext()).load(s).e
//                .placeholder(R.drawable.default_avatar_grey).into(avatarView);
            } else {
              avatarView.setImageResource(R.drawable.default_avatar_grey);
            }
          }
        });
      }
    }
  }

  /**
   * 更新未读消息数量
   *
   * @param conversation
   */
  private void updateUnreadCount(AVIMConversation conversation) {
    int num = LCIMConversationItemCache.getInstance().getUnreadCount(conversation.getConversationId());
    unreadView.setText(num + "");
    unreadView.setVisibility(num > 0 ? View.VISIBLE : View.GONE);
  }

  /**
   * 更新 item 的展示内容，及最后一条消息的内容
   *
   * @param message
   */
  private void updateLastMessage(AVIMMessage message) {
    if (null != message) {
      Date date = new Date(message.getTimestamp());
      //SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
      //timeView.setText(format.format(date));
      timeView.setText(TimeUtil.getTimeByNow(date));
      messageView.setText(getMessageeShorthand(getContext(), message));
    }else{
      timeView.setText("");
      messageView.setText("");
    }
  }

  private void onConversationItemClick(AVIMConversation conversation) {
    try {
      Intent intent = new Intent();
      intent.setPackage(getContext().getPackageName());
      intent.setAction(LCIMConstants.CONVERSATION_ITEM_CLICK_ACTION);
      intent.addCategory(Intent.CATEGORY_DEFAULT);
      intent.putExtra(LCIMConstants.CONVERSATION_ID, conversation.getConversationId());
      getContext().startActivity(intent);
    } catch (ActivityNotFoundException exception) {
      Log.i(LCIMConstants.LCIM_LOG_TAG, exception.toString());
    }
  }

  private void enterCategoryList(NotificationCategory category) {

    Intent intent = new Intent(getContext(), NotificationCategoryListActivity.class);
    intent.putExtra(NotificationConstant.KEY_NOTIFICATION_CATEGORY_ID, category.id);
    intent.putExtra(NotificationConstant.KEY_NOTIFICATION_TITLE, category.remarks);
    ((BaseActivity) getContext()).startActivityForResult(intent, NotificationConstant.REQUEST_NOTIFICATION_CATEGORY_LIST);
  }

  public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<LCIMConversationItemHolder>() {
    @Override
    public LCIMConversationItemHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
      return new LCIMConversationItemHolder(parent);
    }
  };

  private static CharSequence getMessageeShorthand(Context context, AVIMMessage message) {
    if (message instanceof AVIMTypedMessage) {
      AVIMReservedMessageType type = AVIMReservedMessageType.getAVIMReservedMessageType(
        ((AVIMTypedMessage) message).getMessageType());
      switch (type) {
        case TextMessageType:
          return ((AVIMTextMessage) message).getText();
        case ImageMessageType:
          return context.getString(R.string.lcim_message_shorthand_image);
        case LocationMessageType:
          return context.getString(R.string.lcim_message_shorthand_location);
        case AudioMessageType:
          return context.getString(R.string.lcim_message_shorthand_audio);
        default:
          CharSequence shortHand = "";
          if (message instanceof LCChatMessageInterface) {
            LCChatMessageInterface messageInterface = (LCChatMessageInterface) message;
            shortHand = messageInterface.getShorthand();
          }
          if (TextUtils.isEmpty(shortHand)) {
            shortHand = context.getString(R.string.lcim_message_shorthand_unknown);
          }
          return shortHand;
      }
    } else {
      return message.getContent();
    }
  }
}
