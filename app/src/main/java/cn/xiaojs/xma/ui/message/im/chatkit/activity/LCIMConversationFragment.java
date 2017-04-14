package cn.xiaojs.xma.ui.message.im.chatkit.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import de.greenrobot.event.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;
import cn.xiaojs.xma.data.LoginDataManager;
import cn.xiaojs.xma.ui.message.im.chatkit.LCIMChatAdapter;
import cn.xiaojs.xma.ui.message.im.chatkit.event.LCIMIMTypeMessageEvent;
import cn.xiaojs.xma.ui.message.im.chatkit.event.LCIMInputBottomBarEvent;
import cn.xiaojs.xma.ui.message.im.chatkit.event.LCIMInputBottomBarRecordEvent;
import cn.xiaojs.xma.ui.message.im.chatkit.event.LCIMInputBottomBarTextEvent;
import cn.xiaojs.xma.ui.message.im.chatkit.event.LCIMMessageResendEvent;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMAudioHelper;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMLogUtils;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMNotificationUtils;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMPathUtils;
import cn.xiaojs.xma.ui.message.im.chatkit.view.LCIMInputBottomBar;

/**
 * Created by wli on 15/8/27.
 * 将聊天相关的封装到此 Fragment 里边，只需要通过 setConversation 传入 Conversation 即可
 */
public class LCIMConversationFragment extends Fragment {

  private static final int REQUEST_IMAGE_CAPTURE = 1;
  private static final int REQUEST_IMAGE_PICK = 2;

  private final int PERMISSION_CODE = 0x10;

  protected AVIMConversation imConversation;

  /**
   * recyclerView 对应的 Adapter
   */
  protected LCIMChatAdapter itemAdapter;

  protected RecyclerView recyclerView;
  protected LinearLayoutManager layoutManager;

  /**
   * 负责下拉刷新的 SwipeRefreshLayout
   */
  protected SwipeRefreshLayout refreshLayout;

  /**
   * 底部的输入栏
   */
  protected LCIMInputBottomBar inputBottomBar;

  // 记录拍照等的文件路径
  protected String localCameraPath;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.lcim_conversation_fragment, container, false);

    recyclerView = (RecyclerView) view.findViewById(R.id.fragment_chat_rv_chat);
    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_chat_srl_pullrefresh);
    refreshLayout.setEnabled(false);
    inputBottomBar = (LCIMInputBottomBar) view.findViewById(R.id.fragment_chat_inputbottombar);
    layoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(layoutManager);

    itemAdapter = new LCIMChatAdapter();
    itemAdapter.resetRecycledViewPoolSize(recyclerView);
    recyclerView.setAdapter(itemAdapter);

    EventBus.getDefault().register(this);
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {

    refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        AVIMMessage message = itemAdapter.getFirstMessage();
        if (null == message) {
          refreshLayout.setRefreshing(false);
        } else {
          imConversation.queryMessages(message.getMessageId(), message.getTimestamp(), 20, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {
              refreshLayout.setRefreshing(false);
              if (filterException(e)) {
                if (null != list && list.size() > 0) {
                  itemAdapter.addMessageList(list);
                  itemAdapter.notifyDataSetChanged();
                  layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                }
              }
            }
          });
        }
      }
    });
  }

//  protected LCIMChatAdapter getAdpter() {
//    return new LCIMChatAdapter(imConversation);
//  }

  @Override
  public void onResume() {
    super.onResume();
    if (null != imConversation) {
      LCIMNotificationUtils.addTag(imConversation.getConversationId());
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    LCIMAudioHelper.getInstance().stopPlayer();
    if (null != imConversation) {
      LCIMNotificationUtils.removeTag(imConversation.getConversationId());
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    EventBus.getDefault().unregister(this);
  }

  public void setConversation(final AVIMConversation conversation) {
    imConversation = conversation;
    itemAdapter.setAVIMConversation(imConversation);
    refreshLayout.setEnabled(true);
    inputBottomBar.setTag(imConversation.getConversationId());
    fetchMessages();
    LCIMNotificationUtils.addTag(conversation.getConversationId());
    if (!conversation.isTransient()) {
      if (conversation.getMembers().size() == 0) {
        conversation.fetchInfoInBackground(new AVIMConversationCallback() {
          @Override
          public void done(AVIMException e) {
            if (null != e) {
              LCIMLogUtils.logException(e);
            }
            itemAdapter.showUserName(conversation.getMembers().size() > 2);
          }
        });
      } else {
        itemAdapter.showUserName(conversation.getMembers().size() > 2);
      }
    } else {
      itemAdapter.showUserName(true);
    }

  }

  /**
   * 拉取消息，必须加入 conversation 后才能拉取消息
   */
  private void fetchMessages() {
    imConversation.queryMessages(new AVIMMessagesQueryCallback() {
      @Override
      public void done(List<AVIMMessage> messageList, AVIMException e) {
        if (filterException(e)) {
          itemAdapter.setMessageList(messageList);
          recyclerView.setAdapter(itemAdapter);
          itemAdapter.notifyDataSetChanged();
          scrollToBottom();
        }
      }
    });
  }

  /**
   * 输入事件处理，接收后构造成 AVIMTextMessage 然后发送
   * 因为不排除某些特殊情况会受到其他页面过来的无效消息，所以此处加了 tag 判断
   */
  public void onEvent(LCIMInputBottomBarTextEvent textEvent) {
    if (null != imConversation && null != textEvent) {
      if (!TextUtils.isEmpty(textEvent.sendContent) && imConversation.getConversationId().equals(textEvent.tag)) {
        sendText(textEvent.sendContent);
      }
    }
  }

  /**
   * 处理推送过来的消息
   * 同理，避免无效消息，此处加了 conversation id 判断
   */
  public void onEvent(LCIMIMTypeMessageEvent messageEvent) {
    if (null != imConversation && null != messageEvent &&
      imConversation.getConversationId().equals(messageEvent.conversation.getConversationId())) {
      itemAdapter.addMessage(messageEvent.message);
      itemAdapter.notifyDataSetChanged();
      scrollToBottom();
    }
  }

  /**
   * 重新发送已经发送失败的消息
   */
  public void onEvent(LCIMMessageResendEvent resendEvent) {
    if (null != imConversation && null != resendEvent &&
      null != resendEvent.message && imConversation.getConversationId().equals(resendEvent.message.getConversationId())) {
      if (AVIMMessage.AVIMMessageStatus.AVIMMessageStatusFailed == resendEvent.message.getMessageStatus()
        && imConversation.getConversationId().equals(resendEvent.message.getConversationId())) {
        sendMessage(resendEvent.message, false);
      }
    }
  }

  /**
   * 处理输入栏发送过来的事件
   *
   * @param event
   */
  public void onEvent(LCIMInputBottomBarEvent event) {
    if (null != imConversation && null != event && imConversation.getConversationId().equals(event.tag)) {
      switch (event.eventAction) {
        case LCIMInputBottomBarEvent.INPUTBOTTOMBAR_IMAGE_ACTION:
          dispatchPickPictureIntent();
          break;
        case LCIMInputBottomBarEvent.INPUTBOTTOMBAR_CAMERA_ACTION:
          //dispatchTakePictureIntent();
          checkCameraPermisson();
          break;
        default:
          break;
      }
    }
  }

  /**
   * 处理录音事件
   *
   * @param recordEvent
   */
  public void onEvent(LCIMInputBottomBarRecordEvent recordEvent) {
    if (null != imConversation && null != recordEvent &&
      !TextUtils.isEmpty(recordEvent.audioPath) &&
      imConversation.getConversationId().equals(recordEvent.tag)) {
      sendAudio(recordEvent.audioPath);
    }
  }

  /**
   * 发送 Intent 跳转到系统拍照页面
   */
  private void dispatchTakePictureIntent() {
    localCameraPath = LCIMPathUtils.getPicturePathByCurrentTime(getContext());
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    Uri imageUri = Uri.fromFile(new File(localCameraPath));
    takePictureIntent.putExtra("return-data", false);
    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }
  }

  /**
   * 发送 Intent 跳转到系统图库页面
   */
  private void dispatchPickPictureIntent() {
    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, null);
    photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
    startActivityForResult(photoPickerIntent, REQUEST_IMAGE_PICK);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (Activity.RESULT_OK == resultCode) {
      switch (requestCode) {
        case REQUEST_IMAGE_CAPTURE:
          sendImage(localCameraPath);
          break;
        case REQUEST_IMAGE_PICK:
          sendImage(getRealPathFromURI(getActivity(), data.getData()));
          break;
        default:
          break;
      }
    }
  }

  /**
   * 滚动 recyclerView 到底部
   */
  private void scrollToBottom() {
    layoutManager.scrollToPositionWithOffset(itemAdapter.getItemCount() - 1, 0);
  }

  /**
   * 根据 Uri 获取文件所在的位置
   *
   * @param context
   * @param contentUri
   * @return
   */
  private String getRealPathFromURI(Context context, Uri contentUri) {
    if (contentUri.getScheme().equals("file")) {
      return contentUri.getEncodedPath();
    } else {
      Cursor cursor = null;
      try {
        String[] proj = {MediaStore.Images.Media.DATA};
        cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor) {
          int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
          cursor.moveToFirst();
          return cursor.getString(column_index);
        } else {
          return "";
        }
      } finally {
        if (cursor != null) {
          cursor.close();
        }
      }
    }
  }

  /**
   * 发送文本消息
   *
   * @param content
   */
  protected void sendText(String content) {
    AVIMTextMessage message = new AVIMTextMessage();
    message.setText(content);
    sendMessage(message);
  }

  /**
   * 发送图片消息
   * TODO 上传的图片最好要压缩一下
   *
   * @param imagePath
   */
  protected void sendImage(String imagePath) {
    try {
      sendMessage(new AVIMImageMessage(imagePath));
    } catch (IOException e) {
      LCIMLogUtils.logException(e);
    }
  }

  /**
   * 发送语音消息
   *
   * @param audioPath
   */
  protected void sendAudio(String audioPath) {
    try {
      AVIMAudioMessage audioMessage = new AVIMAudioMessage(audioPath);
      sendMessage(audioMessage);
    } catch (IOException e) {
      LCIMLogUtils.logException(e);
    }
  }

  public void sendMessage(AVIMMessage message) {
    sendMessage(message, true);
  }

  /**
   * 发送消息
   *
   * @param message
   */
  public void sendMessage(AVIMMessage message, boolean addToList) {
    if (addToList) {
      itemAdapter.addMessage(message);
    }
    itemAdapter.notifyDataSetChanged();
    scrollToBottom();
    imConversation.sendMessage(message, new AVIMConversationCallback() {
      @Override
      public void done(AVIMException e) {
        itemAdapter.notifyDataSetChanged();
        if (null != e) {
          LCIMLogUtils.logException(e);
        }
      }
    });
  }

  private boolean filterException(Exception e) {
    if (null != e) {
      LCIMLogUtils.logException(e);
      Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    return (null == e);
  }


  private void checkCameraPermisson() {

    if (PermissionUtil.isOverMarshmallow()
            && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

        requestPermissions(new String[]{Manifest.permission.CAMERA},
                PERMISSION_CODE);

    }else {
      dispatchTakePictureIntent();
    }
  }


  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case PERMISSION_CODE: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          dispatchTakePictureIntent();

        } else {

          // permission denied, boo! Disable the
          // functionality that depends on this permission.
        }
        return;
      }
    }
  }
}
