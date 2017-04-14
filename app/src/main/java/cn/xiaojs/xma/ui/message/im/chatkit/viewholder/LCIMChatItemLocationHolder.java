package cn.xiaojs.xma.ui.message.im.chatkit.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;

import de.greenrobot.event.EventBus;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.message.im.chatkit.event.LCIMLocationItemClickEvent;

/**
 * Created by wli on 15/9/17.
 * 聊天页面中的地理位置 item 对应的 holder
 */
public class LCIMChatItemLocationHolder extends LCIMChatItemHolder {

  protected TextView contentView;

  public LCIMChatItemLocationHolder(Context context, ViewGroup root, boolean isLeft, AVIMConversation conversation) {
    super(context, root, isLeft, conversation);
  }

  @Override
  public void initView() {
    super.initView();
    conventLayout.addView(View.inflate(getContext(), R.layout.lcim_chat_item_location, null));
    contentView = (TextView) itemView.findViewById(R.id.locationView);
    conventLayout.setBackgroundResource(isLeft ? R.drawable.ic_chat_other : R.drawable.ic_chat_me);
    contentView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        LCIMLocationItemClickEvent event = new LCIMLocationItemClickEvent();
        event.message = message;
        EventBus.getDefault().post(event);
      }
    });
  }

  @Override
  public void bindData(Object o) {
    super.bindData(o);
    AVIMMessage message = (AVIMMessage) o;
    if (message instanceof AVIMLocationMessage) {
      final AVIMLocationMessage locMsg = (AVIMLocationMessage) message;
      contentView.setText(locMsg.getText());
    }
  }
}
