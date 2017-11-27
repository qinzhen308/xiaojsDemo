package cn.xiaojs.xma.ui.classroom2.chat;

import android.content.Context;

import java.util.ArrayList;

import cn.xiaojs.xma.model.live.TalkItem;

/**
 * Created by maxiaobao on 2017/9/29.
 */

public class ChatLandAdapter extends ChatAdapter {

    public ChatLandAdapter(Context context, ArrayList<TalkItem> messages) {
        super(context, messages, true);
    }

    @Override
    public int getItemViewType(int position) {
        return MessageType.LAND;
    }


}
